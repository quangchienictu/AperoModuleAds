package com.example.andmoduleads.fan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ads.control.ads.FanManagerApp;
import com.ads.control.billing.AppPurchase;
import com.ads.control.funtion.BillingListener;
import com.ads.control.funtion.FanCallback;
import com.example.andmoduleads.MyApplication;
import com.example.andmoduleads.R;
import com.example.andmoduleads.admob.MainActivity;
import com.facebook.ads.AdError;

import java.util.ArrayList;
import java.util.List;

public class SplashFanActivity extends AppCompatActivity {
    private String TAG = "SplashFanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_fan);
        AppPurchase.getInstance().setBillingListener(new BillingListener() {
            @Override
            public void onInitBillingListener(int code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initAds();
                    }
                });
            }
        },5000);

    }


    private void initBilling() {
        List<String> listINAPId = new ArrayList<>();
        listINAPId.add(MainActivity.PRODUCT_ID);
        List<String> listSubsId = new ArrayList<>();

        AppPurchase.getInstance().initBilling(getApplication(),listINAPId,listSubsId);

    }

    private void initAds() {
        MyApplication.getApplication().getStorageCommon().setInterstitialSplashAd(
                FanManagerApp.getInstance().getInterstitialAds(this, getString(R.string.fan_interstitial_id), new FanCallback() {
                    @Override
                    public void onAdFailedToLoad(AdError adError) {
                        super.onAdFailedToLoad(adError);
                        Log.d(TAG, "onAdFailedToLoad: " + adError.getErrorMessage());
                        startMain();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.d(TAG, "onAdLoaded: ");
                        startMain();
                    }
                })
        );
    }

    private void startMain() {
        startActivity(new Intent(this,MainFanActivity.class));
        finish();
    }
}