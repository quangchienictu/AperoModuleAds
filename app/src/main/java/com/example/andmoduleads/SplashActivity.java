package com.example.andmoduleads;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.Admod;
import com.ads.control.AppOpenManager;
import com.ads.control.funtion.AdCallback;
import com.ads.control.Purchase;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Purchase.getInstance().initBilling(this);
        Purchase.getInstance().setProductId(MainActivity.PRODUCT_ID);
        Log.d(TAG, "onCreate: show splash ads");
        Admod.getInstance().loadSplashInterstitalAds(this, getString(R.string.admod_interstitial_id), 0, new AdCallback() {
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

    @Override
    protected void onDestroy() {
//        AppOpenManager.getInstance().removeFullScreenContentCallback();
        super.onDestroy();
    }
}