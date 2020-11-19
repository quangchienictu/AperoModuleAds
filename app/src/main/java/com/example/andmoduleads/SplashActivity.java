package com.example.andmoduleads;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.Admod;
import com.ads.control.funtion.AdCallback;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Admod.getInstance().init(this, list);
        Admod.getInstance().loadSplashInterstitalAds(this, getString(R.string.admod_interstitial_id), 10000, new AdCallback() {
            @Override
            public void onAdClosed() {
                startMain();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                startMain();
            }
        });
    }

    private void startMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}