package com.example.newsapplication;

import static com.example.newsapplication.Constant.ADMOB_BANNER_ID;
import static com.example.newsapplication.Constant.ADMOB_TYPE;
import static com.example.newsapplication.Constant.AD_STATUS;
import static com.example.newsapplication.Constant.AD_TYPE;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AdManager {

    Activity activity;

    public AdManager(Activity activity) {
        this.activity = activity;
    }

    public void initAds()
    {
        if (AD_STATUS)
        {
            if (AD_TYPE.equalsIgnoreCase(ADMOB_TYPE))
            {
                MobileAds.initialize(activity);
            }
        }
    }

    public void showBannerAd(Context context, LinearLayout container)
    {
        if (AD_STATUS)
        {
            if (AD_TYPE.equalsIgnoreCase(ADMOB_TYPE))
            {
                AdView adView = new AdView(context);
                adView.setAdSize(getAdSize(container));
                adView.setAdUnitId(ADMOB_BANNER_ID);
                container.removeAllViews();
                container.addView(adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }
        }
    }

    private AdSize getAdSize(LinearLayout container) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = container.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}
