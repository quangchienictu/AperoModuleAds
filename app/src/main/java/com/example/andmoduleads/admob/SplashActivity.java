package com.example.andmoduleads.admob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.ads.Admod;
import com.ads.control.ads.AppOpenManager;
import com.ads.control.funtion.AdCallback;
import com.ads.control.billing.AppPurchase;
import com.ads.control.funtion.BillingListener;
import com.example.andmoduleads.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppPurchase.getInstance().setBillingListener(new BillingListener() {
            @Override
            public void onInitBillingListener(int code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        loadSplash();
                        loadSplashAdOpenApp();
                    }
                });
            }
        },5000);

        initBilling();
    }

    private void initBilling() {
        List<String> listINAPId = new ArrayList<>();
        listINAPId.add(MainActivity.PRODUCT_ID);
        List<String> listSubsId = new ArrayList<>();

        AppPurchase.getInstance().initBilling(getApplication(),listINAPId,listSubsId);
//        AppPurchase.getInstance().addProductId(MainActivity.PRODUCT_ID);

    }

    private void loadSplash(){
        Log.d(TAG, "onCreate: show splash ads");
        Admod.getInstance().loadSplashInterstitalAds(this, getString(R.string.admod_interstitial_id), 30000,5000,true, new AdCallback() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "Close ads splash " );
                startMain();
            }

            @Override
            public void onAdSplashReady() {
                super.onAdSplashReady();
                Log.d(TAG, "onAdSplashReady");
                Admod.getInstance().onShowSplash(SplashActivity.this,this);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError i) {
                startMain();
            }

            @Override
            public void onAdClosedByUser() {
                super.onAdClosedByUser();
                Log.d(TAG, "onAdClosedByUser" );
            }
        });


    }

    private void loadSplashAdOpenApp(){
        AppOpenManager.getInstance().setSplashActivity(SplashActivity.class, getString(R.string.admod_app_open_ad_id), 30000);
        AppOpenManager.getInstance().setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                startMain();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                startMain();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }
        });
        AppOpenManager.getInstance().loadAndShowSplashAds(getString(R.string.admod_app_open_ad_id));
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