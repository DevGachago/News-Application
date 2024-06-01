package com.example.newsapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapplication.adapters.AnnouncementAdapter;
import com.example.newsapplication.models.Announcement;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Accounts extends AppCompatActivity implements PointsManager.PointsChangeListener {

    ImageButton button, button2;
    TextView textView;
    private TextView editDate;
    private LinearLayout animatedLayout;
    private Animation slideIn, slideOut;

    private ListView listView;

    private List<Announcement> announcementList;

    private AnnouncementAdapter adapter;

    private RewardedAd rewardedAd;

    private SwipeRefreshLayout swipeRefreshLayout;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcements);


        // Hide the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        final int defaultIconColor = getResources().getColor(R.color.default_icons_color);
        final int selectedIconColor = getResources().getColor(R.color.selected_icon_color);

        animatedLayout = findViewById(R.id.animated_layout);
        textView = findViewById(R.id.balance);


        // Handle swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the news items
                refreshNews();
            }
        });


        // Load the animations
        slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_right);
        slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);

        // Set animation listeners
        slideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animatedLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animatedLayout.startAnimation(slideOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                animatedLayout.setVisibility(View.INVISIBLE);
                animatedLayout.postDelayed(() -> {
                    animatedLayout.startAnimation(slideIn);
                }, 1500); // Delay before reappearing from the left
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Start the slide-in animation
        animatedLayout.startAnimation(slideIn);



        button = findViewById(R.id.backButton);
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

        button2 = findViewById(R.id.doMore);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrimaryMenu(v);
            }
        });


        listView = findViewById(R.id.list_view);
        announcementList = new ArrayList<>();
        adapter = new AnnouncementAdapter(this, announcementList);
        listView.setAdapter(adapter);

        fetchAnnouncementsFromFirebase();

        updatePointsDisplay(PointsManager.getInstance().getPoints());

        // Register this activity as a listener
        PointsManager.getInstance().addListener(this);
    }

    private void refreshNews() {
        // Reload news items from Firebase
        fetchAnnouncementsFromFirebase();
    }

    private void showPrimaryMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.videos) {
                    Intent intent = new Intent(Accounts.this, AdminPostAnnouncementActivity.class);
                    startActivity(intent);
                    return true;

                } else if (itemId == R.id.menu_suggestion) {
                    Intent intent = new Intent(Accounts.this, Suggestions.class);
                    startActivity(intent);
                    showWatchAdDialog();
                    return true;
                } else if (itemId == R.id.menu_contact_us) {
                    Intent intent = new Intent(Accounts.this, ContactUs.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_report_abuse) {
                    Intent intent = new Intent(Accounts.this, ReportAbuse.class);
                    startActivity(intent);

                    return true;
                }else {
                    return false;
                }
            }
        });

        popupMenu.show();
    }

    private void showWatchAdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("To post an announcement, you need to watch an ad. Do you want to watch the ad?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    if (rewardedAd != null) {
                        Activity activityContext = Accounts.this;
                        rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                // Handle the reward.
                                Log.d(TAG, "The user earned the reward.");
                                int rewardAmount = rewardItem.getAmount();
                                String rewardType = rewardItem.getType();
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
                    }
                });
    }

    private void fetchAnnouncementsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference announcementsRef = database.getReference("announcements");

        announcementsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                announcementList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String contacts = snapshot.child("contact").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String mediaUrl = snapshot.child("mediaUrl").getValue(String.class);
                    Boolean isImage = snapshot.child("isImage").getValue(Boolean.class);
                    Boolean isApproved = snapshot.child("isApproved").getValue(Boolean.class);
                    Long viewsCount = snapshot.child("viewsCount").getValue(Long.class);

                    if (Boolean.TRUE.equals(isApproved) && title != null && description != null && contacts != null && mediaUrl != null && isImage != null && viewsCount != null && date != null) {
                        Announcement announcement = new Announcement(title, description, contacts, date, mediaUrl, isImage, viewsCount.intValue());
                        announcementList.add(announcement);
                    } else {
                        Log.e("MainActivity", "One of the fields is null or announcement is not approved. Title: " + title + ", Description: " + description + ", MediaUrl: " + mediaUrl + ", IsImage: " + isImage + ", IsApproved: " + isApproved);
                    }
                }

                // Hide the refresh indicator
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to read announcements", databaseError.toException());
                // Hide the refresh indicator in case of error
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister this activity as a listener
        PointsManager.getInstance().removeListener(this);
    }
    @Override
    public void onPointsChanged(int Points) {
        updatePointsDisplay(Points);
    }

    private void updatePointsDisplay(int Points) {
        textView.setText("Points: " + Points);
    }
}