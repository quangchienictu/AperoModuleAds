package com.example.andmoduleads.fan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.ads.control.ads.FanManagerApp;
import com.ads.control.funtion.FanCallback;
import com.example.andmoduleads.MyApplication;
import com.example.andmoduleads.R;
import com.facebook.ads.NativeAdLayout;

public class ContentFanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_fan);
        FanManagerApp.getInstance().loadNative(this, getString(R.string.fan_native_id));
        NativeAdLayout adView = (NativeAdLayout) LayoutInflater.from(this)
                .inflate(R.layout.fb_native_ad_small, null);

        FanManagerApp.getInstance().populateNativeBannerAdView(MyApplication.getApplication().getStorageCommon().getNativeBannerAd(), adView);
        FanManagerApp.getInstance().loadBanner(this, getString(R.string.fan_banner_id));
        FrameLayout frameLayout = findViewById(R.id.frAds);
        frameLayout.addView(adView);


        FanManagerApp.getInstance().forceShowInterstitial(
                this,
                MyApplication.getApplication().getStorageCommon().getInterstitialContentAd(),
                new FanCallback() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }
                }, false);

    }
}