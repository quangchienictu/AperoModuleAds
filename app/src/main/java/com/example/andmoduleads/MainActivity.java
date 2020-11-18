package com.example.andmoduleads;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ads.control.Admod;
import com.ads.control.funtion.AdCallback;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Admod.getInstance().loadBanner(this, getString(R.string.admod_banner_id));
        Admod.getInstance().loadNative(this, getString(R.string.admod_native_id));
        Admod.getInstance().setNumToShowAds(3);
        InterstitialAd mInterstitialAd = Admod.getInstance().getInterstitalAds(this, getString(R.string.admod_interstitial_id));


        findViewById(R.id.btShowAds).setOnClickListener(v -> {
            Admod.getInstance().showInterstitialAdByTimes(this, mInterstitialAd, new AdCallback() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(MainActivity.this, ContentActivity.class));
                }
            });
        });
        findViewById(R.id.btForceShowAds).setOnClickListener(v -> {
            Admod.getInstance().forceShowInterstitial(this, mInterstitialAd, new AdCallback() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(MainActivity.this, ContentActivity.class));
                }
            });
        });
    }
}