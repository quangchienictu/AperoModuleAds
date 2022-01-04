package com.example.andmoduleads.fan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ads.control.ads.FanManagerApp;
import com.ads.control.funtion.FanCallback;
import com.example.andmoduleads.MyApplication;
import com.example.andmoduleads.R;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeBannerAd;

public class MainFanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fan);

        FanManagerApp.getInstance().forceShowInterstitial(
                this,
                MyApplication.getApplication().getStorageCommon().getInterstitialSplashAd(),
                new FanCallback() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }
                }, false);

        FanManagerApp.getInstance().loadNativeBannerAds(this, getString(R.string.fan_native_banner_id));
        FanManagerApp.getInstance().loadBanner(this, getString(R.string.fan_banner_id));

        FanManagerApp.getInstance().getNativeBannerAds(this,getString(R.string.fan_native_banner_id),new FanCallback(){
            @Override
            public void onNativeBannerAdLoaded(NativeBannerAd nativeAd) {
                MyApplication.getApplication().getStorageCommon().setNativeBannerAd(nativeAd);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (MyApplication.getApplication().getStorageCommon().getInterstitialContentAd() == null
                || !MyApplication.getApplication().getStorageCommon().getInterstitialContentAd().isAdLoaded()) {
            loadInterstitial();
        }
    }

    private void loadInterstitial() {
        MyApplication.getApplication().getStorageCommon().setInterstitialContentAd(
                FanManagerApp.getInstance().getInterstitialAds(this, getString(R.string.fan_interstitial_id), new FanCallback() {
                    @Override
                    public void onAdFailedToLoad(AdError adError) {
                        super.onAdFailedToLoad(adError);

                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Toast.makeText(MainFanActivity.this, "ad loaded", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        loadInterstitial();
                    }
                })
        );
    }

    public void nextStep(View view) {
        startActivity(new Intent(this, ContentFanActivity.class));
    }
}