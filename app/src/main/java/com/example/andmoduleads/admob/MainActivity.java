package com.example.andmoduleads.admob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.ads.AperoAd;
import com.ads.control.ads.AperoAdCallback;
import com.ads.control.ads.AperoAdConfig;
import com.ads.control.ads.wrapper.ApInterstitialAd;
import com.ads.control.util.AdjustApero;
import com.ads.control.admob.Admob;
import com.ads.control.billing.AppPurchase;
import com.ads.control.funtion.DialogExitListener;
import com.ads.control.dialog.DialogExitApp1;
import com.ads.control.dialog.InAppDialog;
import com.ads.control.funtion.AdCallback;
import com.ads.control.funtion.PurchaseListioner;
import com.example.andmoduleads.R;
import com.google.android.gms.ads.nativead.NativeAd;

public class MainActivity extends AppCompatActivity {
    public static final String PRODUCT_ID = "android.test.purchased";

    //adjust
    private static final String EVENT_TOKEN_SIMPLE = "g3mfiw";
    private static final String EVENT_TOKEN_REVENUE = "a4fd35";


    private FrameLayout frAds;
    private NativeAd unifiedNativeAd;
    private ApInterstitialAd mInterstitialAd;

    private boolean isShowDialogExit = false;

    private String idBanner = "";
    private String idNative = "";
    private String idInter = "";

    private int layoutNativeCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frAds = findViewById(R.id.fl_adplaceholder);
        configMediationProvider();
//        Admob.getInstance().initRewardAds(this,getString(R.string.admod_app_reward_id));

        Admob.getInstance().setNumToShowAds(4,3);

        AperoAd.getInstance().loadNativeAd(this, idNative, layoutNativeCustom );

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

        AperoAd.getInstance().loadBanner(this,idBanner);

        loadAdInterstitial();


//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        }


        findViewById(R.id.btShowAds).setOnClickListener(v -> {
            AperoAd.getInstance().forceShowInterstitial(this, mInterstitialAd, new AperoAdCallback() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(MainActivity.this, ContentActivity.class));
                    loadAdInterstitial();
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

    private void configMediationProvider() {
        if (AperoAd.getInstance().getMediationProvider() == AperoAdConfig.PROVIDER_ADMOB) {
            idBanner = getString(R.string.admod_banner_id);
            idNative = getString(R.string.admod_native_id);
            idInter = getString(R.string.admod_interstitial_id);
            layoutNativeCustom = com.ads.control.R.layout.custom_native_admod_medium;
        } else {
            idBanner = getString(R.string.applovin_test_banner);
            idNative = getString(R.string.applovin_test_native);
            idInter = getString(R.string.applovin_test_inter);
            layoutNativeCustom = com.ads.control.R.layout.custom_native_max_medium;
        }
    }

    private void loadAdInterstitial() {

        mInterstitialAd = AperoAd.getInstance().getInterstitialAds(this, idInter);
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
        Admob.getInstance().loadNativeAd(this, getString(R.string.admod_native_id), new AdCallback() {
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