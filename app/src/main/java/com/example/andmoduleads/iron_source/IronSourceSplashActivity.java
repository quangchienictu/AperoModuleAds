package com.example.andmoduleads.iron_source;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ads.control.AppIronSource;
import com.ads.control.funtion.AdCallback;
import com.example.andmoduleads.R;
import com.google.android.gms.ads.LoadAdError;

public class IronSourceSplashActivity extends BaseActivity {
    public static String IRON_SOURCE_APP_KEY = "85460dcd";
    String TAG = "IronSourceSplashActivity";
    private AdCallback adListener = new AdCallback() {
        @Override
        public void onAdFailedToLoad(LoadAdError error) {
            Log.e(TAG, "onAdFailedToLoad: " );
            startMain();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            Log.d(TAG, "onAdLoaded");
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "onAdClosed");
            startMain();
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iron_source_splash);

        AppIronSource.getInstance().init(this, IRON_SOURCE_APP_KEY,true);
        loadAndShowInterAds();
    }

    private void loadAndShowInterAds() {
        Log.e(TAG, "start loadAndShowInterAds");
        AppIronSource.getInstance().loadSplashInterstitial(this, adListener,30000);
    }

    private void startMain() {
        startActivity(new Intent(this, TestISActivity.class));
        finish();
    }
}