package com.example.andmoduleads.applovin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.GoalRow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ads.control.ads.Admod;
import com.ads.control.applovin.AppLovin;
import com.ads.control.applovin.AppLovinCallback;
import com.ads.control.funtion.AdCallback;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.example.andmoduleads.R;
import com.example.andmoduleads.admob.MainActivity;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class MainApplovinActivity extends AppCompatActivity {

    private FrameLayout frAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_applovin);
        frAds = findViewById(R.id.fr_ads);

//        AppLovin.getInstance().loadNative(this, "c810c577b4c36ee5");
        AppLovin.getInstance().loadNativeAd(this, "c810c577b4c36ee5", com.ads.control.R.layout.max_native_custom_ad_view, new AppLovinCallback() {
            @Override
            public void onUnifiedNativeAdLoaded(MaxNativeAdView unifiedNativeAd) {
                super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                findViewById(R.id.shimmer_container_native).setVisibility(View.GONE);
                FrameLayout fl = findViewById(R.id.fl_adplaceholder);
                fl.setVisibility(View.VISIBLE);
                fl.addView(unifiedNativeAd);
            }
        });

        AppLovin.getInstance().loadBanner(this, "51999ea397c63f9c");

    }
}