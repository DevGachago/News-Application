package com.example.newsapplication;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapplication.fragments.AgricultureFragment;
import com.example.newsapplication.fragments.EducationFragment;
import com.example.newsapplication.fragments.HealthFragment;
import com.example.newsapplication.fragments.HomeFragment;
import com.example.newsapplication.fragments.NationalFragment;
import com.example.newsapplication.fragments.ScienceFragment;
import com.example.newsapplication.fragments.SportsFragment;
import com.example.newsapplication.fragments.TechFragment;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private BottomNavigationView bottomNavigationView;

    private static final int ACCOUNTS_ACTIVITY_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Hide the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );


        FloatingActionButton btnLive = findViewById(R.id.live);
        btnLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the SecondaryActivity
                //Intent intent = new Intent(getApplicationContext(), VideoPlayer.class);
                // Start the SecondaryActivity
                //startActivity(intent);
                Toast.makeText(MainActivity.this, "coming soon ", Toast.LENGTH_SHORT).show();
            }
        });

        // Find TabLayout and ViewPager by their IDs
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Create a new instance of the ViewPagerAdapter
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add fragments to the adapter
        pagerAdapter.addFragment(new HomeFragment(), "Home");
        pagerAdapter.addFragment(new NationalFragment(), "National");
        pagerAdapter.addFragment(new SportsFragment(), "Sports");
        pagerAdapter.addFragment(new AgricultureFragment(), "Agriculture");
        pagerAdapter.addFragment(new HealthFragment(), "Health");
        pagerAdapter.addFragment(new EducationFragment(), "Education");
        pagerAdapter.addFragment(new TechFragment(), "Tech");
        pagerAdapter.addFragment(new ScienceFragment(), "Science");

        // Set the adapter to the ViewPager
        viewPager.setAdapter(pagerAdapter);

        // Connect the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_account:
                        // Start the desired activity when "Account" is clicked
                        Intent accountIntent = new Intent(MainActivity.this, Accounts.class);
                        startActivity(accountIntent);
                        return true;
                    case R.id.menu_home:
                        viewPager.setCurrentItem(0); // Open the desired tab by setting its index
                        return true;
                    case R.id.menu_search:
                        // Create an Intent to start the SecondaryActivity
                        Intent intent = new Intent(getApplicationContext(), VideoPlayer.class);
                        // Start the SecondaryActivity
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeLt), (v, insets) -> {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCOUNTS_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // The user is coming back from the "Accounts" activity
                // Select the "Home" item in the BottomNavigationView
                bottomNavigationView.setSelectedItemId(R.id.menu_home);

                // Change the color of the "Account" icon back to "Home" color
                Menu menu = bottomNavigationView.getMenu();
                MenuItem accountMenuItem = menu.findItem(R.id.menu_account);
                accountMenuItem.setIcon(R.drawable.ic_home); // Change to the "Home" icon
                // You may also change the tint/color if needed
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_menu_bar, menu);
        return true;
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }



}
