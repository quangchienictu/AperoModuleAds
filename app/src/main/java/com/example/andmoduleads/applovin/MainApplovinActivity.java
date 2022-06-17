package com.example.andmoduleads.applovin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ads.control.ads.AperoAd;
import com.ads.control.ads.AperoAdCallback;
import com.ads.control.ads.wrapper.ApInterstitialAd;
import com.ads.control.ads.wrapper.ApNativeAd;
import com.ads.control.applovin.AppLovin;
import com.ads.control.applovin.AppLovinCallback;
import com.ads.control.funtion.AdCallback;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.example.andmoduleads.R;
import com.example.andmoduleads.admob.ContentActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

public class MainApplovinActivity extends AppCompatActivity {

    private FrameLayout frAds;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Button btnLoadReward;
    private MaxRewardedAd maxRewardedAd;
    ApInterstitialAd apInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_applovin);
        frAds = findViewById(R.id.fl_adplaceholder);
        shimmerFrameLayout = findViewById(R.id.shimmer_container_native);
        AppLovin.getInstance().loadBanner(this, getString(R.string.applovin_test_banner));
        apInterstitialAd =  AperoAd.getInstance().getInterstitialAds(this, getString(R.string.admod_interstitial_id), new AperoAdCallback(){

            @Override
            public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                Log.e("TAG", "AperoAd onInterstitialLoad: " + apInterstitialAd.isReady() );
            }
        });


        //load reward ad
        btnLoadReward = findViewById(R.id.btnLoadReward);
        btnLoadReward.setOnClickListener(view -> {
            if (maxRewardedAd != null && maxRewardedAd.isReady()) {
                AppLovin.getInstance().showRewardAd(this, maxRewardedAd);
            } else {
                maxRewardedAd = AppLovin.getInstance().getRewardAd(this, getString(R.string.applovin_test_reward), new AppLovinCallback() {
                    @Override
                    public void onAdLoaded() {
                        Toast.makeText(MainApplovinActivity.this, "reward loaded", Toast.LENGTH_SHORT).show();
                        btnLoadReward.setText("Show Reward");
                    }

                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(MainApplovinActivity.this, SimpleListActivity.class));
                    }
                });
            }
        });
        //load interstitial
        findViewById(R.id.btnLoadInter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "AperoAd onInterstitialLoad: " + apInterstitialAd.isReady() );

                if (apInterstitialAd.isReady()) {
                    AperoAd.getInstance().forceShowInterstitial(MainApplovinActivity.this, apInterstitialAd, new AperoAdCallback() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            startActivity(new Intent(MainApplovinActivity.this, ContentActivity.class));
                        }
                    }, true);
                } else {
                    Toast.makeText(MainApplovinActivity.this, "interstitial not loaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainApplovinActivity.this, ContentActivity.class));
                }
            }
        });
        //get native and add to view
/*        AppLovin.getInstance().loadNativeAd(this, "c810c577b4c36ee5", com.ads.control.R.layout.max_native_custom_ad_view, new AppLovinCallback() {
            @Override
            public void onUnifiedNativeAdLoaded(MaxNativeAdView unifiedNativeAd) {
                super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                findViewById(R.id.shimmer_container_native).setVisibility(View.GONE);
                FrameLayout fl = findViewById(R.id.fl_adplaceholder);
                fl.setVisibility(View.VISIBLE);
                fl.addView(unifiedNativeAd);
            }
        });*/

        ShimmerFrameLayout shimmerFrameLayout =  findViewById(R.id.shimmer_container_native) ;
        FrameLayout flParentNative = findViewById(R.id.fl_adplaceholder);
        AperoAd.getInstance().loadNativeAd(this,getString(R.string.applovin_test_native),R.layout.max_native_custom_ad_view,new AperoAdCallback(){
            @Override
            public void onNativeAdLoaded(@NonNull ApNativeAd nativeAd) {
                super.onNativeAdLoaded(nativeAd);
                AperoAd.getInstance().populateNativeAdView(MainApplovinActivity.this,nativeAd,flParentNative,shimmerFrameLayout);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        AppLovin.getInstance().loadNativeAd(
//                this,
//                shimmerFrameLayout,
//                frAds,
//                getString(R.string.applovin_test_native),
//                com.ads.control.R.layout.max_native_custom_ad_view
//        );
    }
}