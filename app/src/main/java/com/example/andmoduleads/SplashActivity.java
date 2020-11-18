package com.example.andmoduleads;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.Admod;
import com.ads.control.funtion.AdCallback;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Admod.getInstance().init(this);
        Admod.getInstance().splashInterstitalAds(this, getString(R.string.admod_interstitial_id), 3000, new AdCallback() {
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
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}