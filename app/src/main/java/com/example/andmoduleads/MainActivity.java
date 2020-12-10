package com.example.andmoduleads;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.Admod;
import com.ads.control.Purcharse;
import com.ads.control.dialog.InAppDialog;
import com.ads.control.funtion.AdCallback;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Purcharse.getInstance().initBilling(this, "android.test.purchased");
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

        findViewById(R.id.btIap).setOnClickListener(v -> {
            Purcharse.getInstance().consumePurchase();
            InAppDialog dialog = new InAppDialog(this);
            dialog.setCallback(() -> {
                Purcharse.getInstance().purcharse(this);
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Purcharse.getInstance().handleActivityResult(requestCode, resultCode, data);
        if (Purcharse.getInstance().isPurcharsed(this)) {
            findViewById(R.id.btIap).setVisibility(View.GONE);
        }
    }
}