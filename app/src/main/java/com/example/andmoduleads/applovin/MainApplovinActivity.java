package com.example.andmoduleads.applovin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.ads.control.ads.Admod;
import com.ads.control.applovin.AppLovin;
import com.ads.control.funtion.AdCallback;
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

        AppLovin.getInstance().loadNative(this, "c810c577b4c36ee5");
        AppLovin.getInstance().loadBanner(this, "51999ea397c63f9c");

    }
}