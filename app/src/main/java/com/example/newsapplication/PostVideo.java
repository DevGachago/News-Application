package com.example.newsapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapplication.models.VideoData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostVideo extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST_CODE = 1;

    VideoView videoView;
    int REQUEST_CODE_VIDEO_CAPTURE = 2607;
    private DatabaseReference newsRef;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private EditText dateEditText;
    private Button selectVideoButton;
    private Button postButton;

    private Uri selectedVideoUri;
    private String userId; // User ID of the current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);

        // Check if the user is authenticated
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            // User is not authenticated, redirect to sign-in page
            Intent signInIntent = new Intent(this, Login.class); // Replace with your sign-in activity
            startActivity(signInIntent);
            finish(); // Close this activity
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        newsRef = database.getReference("videos");

        videoView = findViewById(R.id.videoView);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        dateEditText = findViewById(R.id.dateEditText);
        selectVideoButton = findViewById(R.id.selectVideoButton);
        postButton = findViewById(R.id.postButton);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);


        userId = getIntent().getStringExtra("user_id");

        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickVideoFromStorage();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postvideo();
            }
        });
    }

    public void recordVideo(View view){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent,REQUEST_CODE_VIDEO_CAPTURE);
        }
    }


    private void pickVideoFromStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST_CODE) {
            // This is the video picker request
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                selectedVideoUri = data.getData();
                videoView.setVideoURI(selectedVideoUri);
                videoView.start();
            } else {
                // Handle the case where video selection was not successful
                Toast.makeText(this, "Video selection failed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_VIDEO_CAPTURE) {
            // This is the video recording request
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                selectedVideoUri = data.getData();
                videoView.setVideoURI(selectedVideoUri);
                videoView.start();
            } else {
                // Handle the case where video recording was not successful
                Toast.makeText(this, "Video recording failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void postvideo() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        // Check if all fields are filled
        if (selectedVideoUri != null && !title.isEmpty() && !description.isEmpty() && !location.isEmpty() && !date.isEmpty()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Check if the user is verified
                        Boolean isVerified = dataSnapshot.child("verified").getValue(Boolean.class);
                        if (isVerified != null && isVerified) {
                            String videoUrl = selectedVideoUri.toString();

                            // Create a new video object
                            VideoData video = new VideoData();
                            video.setTitle(title);
                            video.setDescription(description);
                            video.setLocation(location);
                            video.setDate(date);

                            // Save the live stream to Firebase Realtime Database
                            DatabaseReference videosRef = FirebaseDatabase.getInstance().getReference("videos");
                            String videoId = videosRef.push().getKey();
                            videosRef.child(videoId).setValue(video)
                                    .addOnSuccessListener(aVoid -> {
                                        // Upload the video file to Firebase Storage
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("video_videos").child(videoId + ".mp4");
                                        UploadTask uploadTask = storageRef.putFile(selectedVideoUri);
                                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                                            // Get the download URL of the uploaded video
                                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                // Update the video URL in the live stream object
                                                video.setVideoUrl(uri.toString());
                                                videosRef.child(videoId).setValue(video)
                                                        .addOnSuccessListener(aVoid1 -> {
                                                            Toast.makeText(PostVideo.this, "Video posted successfully", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(PostVideo.this, "Failed to post video", Toast.LENGTH_SHORT).show();
                                                        });
                                            }).addOnFailureListener(e -> {
                                                Toast.makeText(PostVideo.this, "Failed to get video download URL", Toast.LENGTH_SHORT).show();
                                            });
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(PostVideo.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(PostVideo.this, "Failed to post video", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Show a message that the user needs to be verified to post videos
                            Toast.makeText(PostVideo.this, "Please get verified to post videos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Log.e("PostVideo", "Error getting user data", databaseError.toException());
                    }
                });
            } else {
                Toast.makeText(this, "Please login to post videos", Toast.LENGTH_SHORT).show();
                // Redirect the user to the login page
                Intent intent = new Intent(PostVideo.this, Login.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Please fill in all fields and select a video", Toast.LENGTH_SHORT).show();
        }
    }

}
