package com.example.andmoduleads;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ads.control.Admod;
import com.ads.control.AppPurchase;
import com.ads.control.dialog.DialogExitApp1;
import com.ads.control.dialog.InAppDialog;
import com.ads.control.funtion.AdCallback;
import com.ads.control.funtion.PurchaseListioner;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

public class MainActivity extends AppCompatActivity {
      static final String PRODUCT_ID = "android.test.purchased";
    private FrameLayout frAds;
    private UnifiedNativeAd unifiedNativeAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frAds = findViewById(R.id.fr_ads);


        Admod.getInstance().loadUnifiedNativeAd(this, getString(R.string.admod_native_id), new AdCallback() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                @SuppressLint("InflateParams") UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.custom_native, null);
                frAds.addView(adView);
                Admod.getInstance().populateUnifiedNativeAdView(unifiedNativeAd, adView);
            }
        });
        AppPurchase.getInstance().setPurchaseListioner(new PurchaseListioner() {
            @Override
            public void onProductPurchased(String productId,String transactionDetails) {
                Log.e("PurchaseListioner","ProductPurchased:"+ productId);
                Log.e("PurchaseListioner","transactionDetails:"+ transactionDetails);
            }

            @Override
            public void displayErrorMessage(String errorMsg) {
                Log.e("PurchaseListioner","displayErrorMessage:"+ errorMsg);
            }
        });

        Admod.getInstance().loadBanner(this, getString(R.string.admod_banner_id));
//        Admod.getInstance().loadNative(this, getString(R.string.admod_native_id));
        Admod.getInstance().setNumToShowAds(3);
        InterstitialAd mInterstitialAd = Admod.getInstance().getInterstitalAds(this, getString(R.string.admod_interstitial_id));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
            AppPurchase.getInstance().consumePurchase(PRODUCT_ID);
            AppPurchase.getInstance().consumePurchase(PRODUCT_ID);
            InAppDialog dialog = new InAppDialog(this);
            dialog.setCallback(() -> {
                    AppPurchase.getInstance().consumePurchase(PRODUCT_ID);
                AppPurchase.getInstance().purchase(this,PRODUCT_ID);
                dialog.dismiss();
            });
            dialog.show();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNativeExit();
    }

    private void loadNativeExit() {
        if (unifiedNativeAd != null)
            return;
        Admod.getInstance().loadUnifiedNativeAd(this, getString(R.string.admod_native_id), new AdCallback() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                MainActivity.this.unifiedNativeAd = unifiedNativeAd;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DialogExitApp1 dialogExitApp1 = new DialogExitApp1(this,unifiedNativeAd,1);
        dialogExitApp1.setCancelable(false);
        dialogExitApp1.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        AppPurchase.getInstance().handleActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult","ProductPurchased:"+ data.toString());
        if (AppPurchase.getInstance().isPurchased(this,PRODUCT_ID)) {
            findViewById(R.id.btIap).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}