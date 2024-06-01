package com.example.newsapplication;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.newsapplication.adapters.VideoAdapter;
import com.example.newsapplication.models.VideoData;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayer extends AppCompatActivity { // Renamed to match common naming convention

    private ListView listView;
    private ImageButton button;
    private VideoAdapter videoAdapter;
    private List<VideoData> videoList;
    private DatabaseReference databaseReference;

    //private LinearLayout bannerAdContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_player);

        // Hide the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //bannerAdContainer = findViewById(R.id.bannerAdContainer);

        //AdManager adManager = new AdManager(this);
        //adManager.initAds();
        // Initialize and show Ad
        //adManager.showBannerAd(this, bannerAdContainer);

        final int defaultIconColor = getResources().getColor(R.color.default_icons_color);
        final int selectedIconColor = getResources().getColor(R.color.selected_icon_color);

        button = findViewById(R.id.backBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setColorFilter(selectedIconColor);

                // You can also perform other actions when the button is clicked
                // For example, navigate back to the previous screen or perform some action.

                // Delay for a short time (e.g., 500 milliseconds) to give the appearance of a click
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Reset the icon color to the default color
                        button.setColorFilter(defaultIconColor);

                        // Finish the Accounts activity to go back to the previous screen
                        finish();
                    }
                }, 500); // Adjust the delay as needed
            }
        });


        listView = findViewById(R.id.list_view);
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(this, videoList);
        listView.setAdapter(videoAdapter);


        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("videos");

        // Fetch data from Firebase
        fetchVideosFromFirebase();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        MobileAds.initialize(this);
        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-4932588199263963/8148048096")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        NativeTemplateStyle style = new NativeTemplateStyle.Builder().build();
                        TemplateView templateView = findViewById(R.id.nativeAdTemplate);
                        templateView.setStyles(style);
                        templateView.setNativeAd(nativeAd);
                    }
                }).build();
        adLoader.loadAd(new AdRequest.Builder().build());

        TextView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TemplateView templateView = findViewById(R.id.nativeAdTemplate);
                templateView.setVisibility(View.GONE);
                v.setVisibility(View.GONE);

                // Create a Handler to re-show the button and the ad after 5 minutes
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        templateView.setVisibility(View.VISIBLE);
                        v.setVisibility(View.VISIBLE);
                    }
                }, 30000); // half a minute
            }
        });


    }


    private void fetchVideosFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    VideoData videoData = postSnapshot.getValue(VideoData.class);
                    if (videoData != null) {
                        Log.d("VideoPlayerActivity", "Video URL: " + videoData.getVideoUrl());
                        videoList.add(videoData);
                    }
                }
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("VideoPlayerActivity", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release all players when the activity is destroyed
        for (int i = 0; i < videoAdapter.getCount(); i++) {
            View view = videoAdapter.getView(i, null, null);
            if (view != null) {
                ExoPlayer player = (ExoPlayer) view.getTag();
                if (player != null) {
                    player.release();
                }
            }
        }
    }
}
