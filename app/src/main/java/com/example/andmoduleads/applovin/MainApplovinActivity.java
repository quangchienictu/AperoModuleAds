package com.example.andmoduleads.applovin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.GoalRow;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class MainApplovinActivity extends AppCompatActivity {

    private FrameLayout frAds;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_applovin);
        frAds = findViewById(R.id.fl_adplaceholder);
        shimmerFrameLayout = findViewById(R.id.shimmer_container_native);

        findViewById(R.id.btnLoadInter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainApplovinActivity.this, SimpleListActivity.class));
            }
        });

//        AppLovin.getInstance().loadNative(this, "c810c577b4c36ee5");
//        AppLovin.getInstance().loadNativeAd(this, "c810c577b4c36ee5", com.ads.control.R.layout.max_native_custom_ad_view, new AppLovinCallback() {
//            @Override
//            public void onUnifiedNativeAdLoaded(MaxNativeAdView unifiedNativeAd) {
//                super.onUnifiedNativeAdLoaded(unifiedNativeAd);
//                findViewById(R.id.shimmer_container_native).setVisibility(View.GONE);
//                FrameLayout fl = findViewById(R.id.fl_adplaceholder);
//                fl.setVisibility(View.VISIBLE);
//                fl.addView(unifiedNativeAd);
//            }
//        });

        AppLovin.getInstance().loadBanner(this, getString(R.string.applovin_test_banner));

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLovin.getInstance().loadNativeAd(
                this,
                shimmerFrameLayout,
                frAds,
                getString(R.string.applovin_test_native),
                com.ads.control.R.layout.max_native_custom_ad_view
        );
    }
}