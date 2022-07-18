package com.example.andmoduleads.admob;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.ads.AppOpenManager;
import com.ads.control.util.AdjustApero;
import com.ads.control.ads.Admod;
import com.ads.control.billing.AppPurchase;
import com.ads.control.funtion.DialogExitListener;
import com.ads.control.dialog.DialogExitApp1;
import com.ads.control.dialog.InAppDialog;
import com.ads.control.funtion.AdCallback;
import com.ads.control.funtion.PurchaseListioner;
import com.ads.control.funtion.RewardCallback;
import com.example.andmoduleads.R;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;

public class MainActivity extends AppCompatActivity {
    public static final String PRODUCT_ID = "android.test.purchased";

    //adjust
    private static final String EVENT_TOKEN_SIMPLE = "g3mfiw";
    private static final String EVENT_TOKEN_REVENUE = "a4fd35";


    private FrameLayout frAds;
    private NativeAd unifiedNativeAd;
    private InterstitialAd mInterstitialAd;

    private boolean isShowDialogExit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frAds = findViewById(R.id.fr_ads);

        Admod.getInstance().initRewardAds(this,getString(R.string.admod_app_reward_id));

        AppOpenManager.getInstance().setEnableScreenContentCallback(true);
        AppOpenManager.getInstance().setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                Log.e("AppOpenManager", "onAdShowedFullScreenContent: ");

            }
        });


        Admod.getInstance().setNumToShowAds(4,3);
//        Admod.getInstance().setNumToShowAds(3);
        Admod.getInstance().loadNativeAd(this, getString(R.string.admod_native_id), new AdCallback() {
            @Override
            public void onUnifiedNativeAdLoaded(NativeAd unifiedNativeAd) {
                @SuppressLint("InflateParams") NativeAdView adView = ( NativeAdView) LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_native, null);
                frAds.addView(adView);
                Admod.getInstance().populateUnifiedNativeAdView(unifiedNativeAd, adView);
            }
        });

        AppPurchase.getInstance().setPurchaseListioner(new PurchaseListioner() {
            @Override
            public void onProductPurchased(String productId, String transactionDetails) {
                Log.e("PurchaseListioner", "ProductPurchased:" + productId);
                Log.e("PurchaseListioner", "transactionDetails:" + transactionDetails);
                startActivity(new Intent(MainActivity.this,MainActivity.class));
                finish();
            }

            @Override
            public void displayErrorMessage(String errorMsg) {
                Log.e("PurchaseListioner", "displayErrorMessage:" + errorMsg);
            }

            @Override
            public void onUserCancelBilling() {

            }
        });

        Admod.getInstance().loadBanner(this, getString(R.string.admod_banner_id));
//        Admod.getInstance().loadNative(this, getString(R.string.admod_native_id));

        loadAdInterstial();


//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        }
        findViewById(R.id.btShowAds).setOnClickListener(v -> {
            Admod.getInstance().showInterstitialAdByTimes(this, mInterstitialAd, new AdCallback() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(MainActivity.this, ContentActivity.class));
                    loadAdInterstial();
                }
            });
        });
        findViewById(R.id.btForceShowAds).setOnClickListener(v -> {


            Admod.getInstance().forceShowInterstitial(this, mInterstitialAd, new AdCallback() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(MainActivity.this, ContentActivity.class));
                    loadAdInterstial();
                }
            });
        });

        findViewById(R.id.btnShowReward).setOnClickListener(v -> {

            Admod.getInstance().showRewardAds(this, new RewardCallback() {
                @Override
                public void onUserEarnedReward(RewardItem var1) {

                }

                @Override
                public void onRewardedAdClosed() {
                    startActivity(new Intent(MainActivity.this, ContentActivity.class));
                    Log.e("TAG", "onRewardedAdClosed ");
                }

                @Override
                public void onRewardedAdFailedToShow(int codeError) {

                }
            });
        });

        findViewById(R.id.btIap).setOnClickListener(v -> {
            AppPurchase.getInstance().consumePurchase(PRODUCT_ID);
            InAppDialog dialog = new InAppDialog(this);
            dialog.setCallback(() -> {
                AppPurchase.getInstance().consumePurchase(PRODUCT_ID);
                AppPurchase.getInstance().purchase(this, PRODUCT_ID);
                dialog.dismiss();

            });
            dialog.show();
        });

    }

    private void loadAdInterstial() {

        Admod.getInstance().getInterstitalAds(this, getString(R.string.admod_interstitial_id), new AdCallback() {

            @Override
            public void onInterstitialLoad(InterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                mInterstitialAd = interstitialAd;
            }
        });
    }


    public void onTrackSimpleEventClick(View v) {
        AdjustApero.onTrackEvent(EVENT_TOKEN_SIMPLE);
    }

    public void onTrackRevenueEventClick(View v) {
        AdjustApero.onTrackRevenue(EVENT_TOKEN_REVENUE, 1f, "EUR");
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadNativeExit();
    }

    private void loadNativeExit() {
        if (unifiedNativeAd != null)
            return;
        Admod.getInstance().loadNativeAd(this, getString(R.string.admod_native_id), new AdCallback() {
            @Override
            public void onUnifiedNativeAdLoaded(NativeAd unifiedNativeAd) {
                MainActivity.this.unifiedNativeAd = unifiedNativeAd;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (unifiedNativeAd==null)
            return;

        DialogExitApp1 dialogExitApp1 = new DialogExitApp1(this, unifiedNativeAd, 1);
        dialogExitApp1.setDialogExitListener(new DialogExitListener(){
            @Override
            public void onExit(boolean exit) {
                    MainActivity.super.onBackPressed();
            }
        });
        dialogExitApp1.setCancelable(false);
        dialogExitApp1.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        AppPurchase.getInstance().handleActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "ProductPurchased:" + data.toString());
        if (AppPurchase.getInstance().isPurchased(this)) {
            findViewById(R.id.btIap).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}