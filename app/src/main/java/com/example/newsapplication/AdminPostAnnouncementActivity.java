package com.example.newsapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminPostAnnouncementActivity extends AppCompatActivity {

    private static final int PICK_MEDIA_REQUEST = 1;

    private EditText etTitle, etDescription, contact;
    private Button btnSelectMedia, btnPostAnnouncement;
    private ImageView ivPreview;
    private TextView textView;
    private TextView editDate;
    private VideoView vvPreview;
    ImageButton back;
    private Uri mediaUri;
    private boolean isImage = true;

    private DatabaseReference announcementsRef;
    private StorageReference storageRef;
    private RewardedAd rewardedAd;
    private final String TAG = "AdminPostAnnouncementActivity";
    private boolean isAdLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_post_announcement);

        // Hide the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadRewardedAd();
            }
        });

        final int defaultIconColor = getResources().getColor(R.color.default_icons_color);
        final int selectedIconColor = getResources().getColor(R.color.selected_icon_color);

        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        btnSelectMedia = findViewById(R.id.btn_select_media);
        btnPostAnnouncement = findViewById(R.id.btn_post_announcement);
        ivPreview = findViewById(R.id.iv_preview);
        vvPreview = findViewById(R.id.vv_preview);
        contact =findViewById(R.id.et_contact);
        editDate = findViewById(R.id.edit_date);
        back = findViewById(R.id.backBtn);


        announcementsRef = FirebaseDatabase.getInstance().getReference("announcements");
        storageRef = FirebaseStorage.getInstance().getReference("announcement_media");

        btnSelectMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMedia();
            }
        });

        btnPostAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWatchAdDialog();
            }
        });
        setAutomaticDate();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setColorFilter(selectedIconColor);

                // You can also perform other actions when the button is clicked
                // For example, navigate back to the previous screen or perform some action.

                // Delay for a short time (e.g., 500 milliseconds) to give the appearance of a click
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Reset the icon color to the default color
                        back.setColorFilter(defaultIconColor);

                        // Finish the Accounts activity to go back to the previous screen
                        finish();
                    }
                }, 500); // Adjust the delay as needed
            }
        });

    }

    private void setAutomaticDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        editDate.setText(currentDate);
    }

    private void setFullScreenContentCallback(RewardedAd ad) {
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed fullscreen content.");
                rewardedAd = null;
                loadRewardedAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                Log.e(TAG, "Ad failed to show fullscreen content.");
                rewardedAd = null;
                loadRewardedAd();
            }

            @Override
            public void onAdImpression() {
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
    }

    private void showWatchAdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("To post an announcement, you need to watch an ad. Do you want to watch the ad?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    if (rewardedAd != null) {
                        Activity activityContext = AdminPostAnnouncementActivity.this;
                        rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                // Handle the reward.
                                Log.d(TAG, "The user earned the reward.");
                                int rewardAmount = rewardItem.getAmount();
                                String rewardType = rewardItem.getType();
                                postAnnouncement();
                            }
                        });
                    } else {
                        Log.d(TAG, "The rewarded ad wasn't ready yet.");
                    }

                    loadRewardedAd();

                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-4932588199263963/8005212325",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        PointsManager.getInstance().addPoints(1);
                        isAdLoaded = true;
                    }
                });
    }

    private void selectMedia() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_MEDIA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA_REQUEST && resultCode == RESULT_OK && data != null) {
            mediaUri = data.getData();
            if (mediaUri.toString().contains("image")) {
                isImage = true;
                ivPreview.setVisibility(View.VISIBLE);
                vvPreview.setVisibility(View.GONE);
                ivPreview.setImageURI(mediaUri);
            } else if (mediaUri.toString().contains("video")) {
                isImage = false;
                ivPreview.setVisibility(View.GONE);
                vvPreview.setVisibility(View.VISIBLE);
                vvPreview.setVideoURI(mediaUri);
                vvPreview.start();
            }
        }
    }

    private void postAnnouncement() {
        final String title = etTitle.getText().toString().trim();
        final String description = etDescription.getText().toString().trim();
        final String contacts = contact.getText().toString().trim();
        String date = editDate.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || contacts.isEmpty() || mediaUri == null || date.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        final StorageReference mediaRef = storageRef.child(mediaUri.getLastPathSegment());
        mediaRef.putFile(mediaUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mediaRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                postToDatabase(title, description, contacts, date,downloadUri.toString());
                            } else {
                                Toast.makeText(AdminPostAnnouncementActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(AdminPostAnnouncementActivity.this, "Media upload failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void postToDatabase(String title, String description, String contacts, String date, String mediaUrl) {
        String key = announcementsRef.push().getKey();
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("title", title);
        announcement.put("description", description);
        announcement.put("contact", contacts);
        announcement.put("date",date);
        announcement.put("viewsCount", 0);
        announcement.put("mediaUrl", mediaUrl);
        announcement.put("isImage", isImage);
        announcement.put("isApproved", false);

        announcementsRef.child(key).setValue(announcement).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AdminPostAnnouncementActivity.this, "Announcement posted, now lets wait for Admin approval", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminPostAnnouncementActivity.this, "Failed to post announcement", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
