package com.example.andmoduleads;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ads.control.Admod;
import com.ads.control.Purchase;
import com.ads.control.dialog.InAppDialog;
import com.ads.control.funtion.AdCallback;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Purchase.getInstance().initBilling(this, "android.test.purchased");
        Admod.getInstance().loadBanner(this, getString(R.string.admod_banner_id));
        Admod.getInstance().loadNative(this, getString(R.string.admod_native_id));
        Admod.getInstance().setNumToShowAds(3);
        InterstitialAd mInterstitialAd = Admod.getInstance().getInterstitalAds(this, getString(R.string.admod_interstitial_id));

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
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

        findViewById(R.id.btIap).setOnClickListener(v -> {
            Purchase.getInstance().consumePurchase();
            InAppDialog dialog = new InAppDialog(this);
            dialog.setCallback(() -> {
                Purchase.getInstance().purchase(this);
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Purchase.getInstance().handleActivityResult(requestCode, resultCode, data);
        if (Purchase.getInstance().isPurchased(this)) {
            findViewById(R.id.btIap).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}