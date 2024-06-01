package com.example.newsapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapplication.models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapplication.models.News;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostNews extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseReference newsRef;

    private Uri selectedImageUri;
    private Spinner spinnerCategory;
    private EditText editTextTitle;
    private EditText editTextContent;
    private TextView editDate;
    private ImageView imageViewSelectedImage, logout;
    private Button buttonChooseImage,openCamera;
    private Button buttonPost;
    private String userId; // User ID of the current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_news);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        newsRef = database.getReference("news");

        spinnerCategory = findViewById(R.id.spinner_category);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        editDate = findViewById(R.id.edit_date);
        imageViewSelectedImage = findViewById(R.id.imageViewSelectedImage);
        buttonChooseImage = findViewById(R.id.buttonChooseImage);
        openCamera = findViewById(R.id.opencam);
        buttonPost = findViewById(R.id.buttonPost);
        logout = findViewById(R.id.logout);

        // Get the user ID from the intent or your authentication mechanism
        userId = getIntent().getStringExtra("user_id");

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(open_camera,100);
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNews();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an instance of LogoutUtils
                LogoutUtils logoutUtils = new LogoutUtils();
                // Call the logout method to clear the local session
                logoutUtils.logout(PostNews.this);
                // Log out the user from Firebase
                FirebaseAuth.getInstance().signOut();
                // Redirect the user to the login screen
                startActivity(new Intent(PostNews.this, Login.class));
                finish(); // Optional: Finish the current activity to prevent going back to it
            }
        });


        setAutomaticDate();
    }


    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void setAutomaticDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        editDate.setText(currentDate);
    }

    private void postNews() {
        String category = spinnerCategory.getSelectedItem().toString();
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();
        String date = editDate.getText().toString().trim();

        // Validate the input fields
        if (category.isEmpty() || title.isEmpty() || content.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if an image is selected
        if (selectedImageUri != null) {
            uploadImageAndCreateNews(category, title, content, date);
        } else {
            // Handle the case where no image is selected
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageAndCreateNews(String category, String title, String content, String date) {
        // Generate a unique ID for the news item in the database
        String newsId = newsRef.push().getKey();

        // Create a reference to the Firebase Storage location
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("news_images/" + newsId);

        // Upload the image to Firebase Storage
        UploadTask uploadTask = storageRef.putFile(selectedImageUri);

        // Listen for the upload success/failure event
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded image
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        String imageUrl = downloadUri.toString();
                        // Save the news to the database with the image URL
                        saveNewsToDatabase(category, title, content, date, imageUrl);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure to retrieve the download URL
                        Toast.makeText(PostNews.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        // Hide the progress dialog or loading indicator if desired
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the image upload failure
                Toast.makeText(PostNews.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                // Hide the progress dialog or loading indicator if desired
            }
        });
    }

    private void saveNewsToDatabase(String category, String title, String content, String date, String imageUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Check if the user is verified
                    Boolean isVerified = dataSnapshot.child("verified").getValue(Boolean.class);
                    if (isVerified != null && isVerified) {
                        String ownerId = currentUser.getUid();
                        String username = currentUser.getDisplayName();

                        // Generate the news ID using the owner ID and current timestamp
                        String newsId = ownerId + "_" + System.currentTimeMillis();

                        // Create a new News object
                        News news = new News(newsId, category, title, content, date, ownerId, username, imageUrl);

                        // Save the news item in the "news" node
                        newsRef.child(newsId).setValue(news)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Show a success message
                                        Toast.makeText(PostNews.this, "News posted successfully", Toast.LENGTH_SHORT).show();

                                        // Update the user's newsIds list with the new news item ID
                                        DatabaseReference userNewsRef = FirebaseDatabase.getInstance().getReference("users")
                                                .child(ownerId)
                                                .child("newsIds");
                                        userNewsRef.push().setValue(newsId); // Add newsId to the list

                                        // Clear the input fields
                                        editTextTitle.setText("");
                                        editTextContent.setText("");
                                        imageViewSelectedImage.setImageResource(R.drawable.placeholder_image);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Show an error message
                                        Toast.makeText(PostNews.this, "Failed to post news" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Show a message that the user needs to be verified to post news
                        Toast.makeText(PostNews.this, "Please get verified to post news", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    Log.e("PostNews", "Error getting user data", databaseError.toException());
                }
            });
        } else {
            Toast.makeText(PostNews.this, "Please login or register to post news", Toast.LENGTH_SHORT).show();
            // Redirect the user to the login page
            Intent intent = new Intent(PostNews.this, Login.class);
            startActivity(intent);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            // This is the camera request
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bitmap capturedImageBitmap = (Bitmap) data.getExtras().get("data");
                // Do something with the captured image (e.g., display it)
                imageViewSelectedImage.setImageBitmap(capturedImageBitmap);

                // Initialize selectedImageUri with the captured image URI
                selectedImageUri = getImageUri(getApplicationContext(), capturedImageBitmap);
            } else {
                // Handle the case where the camera capture was not successful
                Toast.makeText(this, "Camera capture failed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            // This is the image picker request
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    Uri imageUri = data.getData();
                    Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    // Do something with the selected image (e.g., display it)
                    imageViewSelectedImage.setImageBitmap(selectedImageBitmap);
                    selectedImageUri = imageUri;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the case where image selection was not successful
                Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }


    public class LogoutUtils {
        public void logout(Context context) {
            // Clear any user session or preferences here
            // For example, you can use SharedPreferences to clear the session
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }


}
