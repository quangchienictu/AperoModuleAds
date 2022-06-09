package com.example.andmoduleads.admob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.admob.Admob;
import com.ads.control.admob.AppOpenManager;
import com.ads.control.applovin.AppLovin;
import com.ads.control.applovin.AppLovinCallback;
import com.ads.control.funtion.AdCallback;
import com.ads.control.billing.AppPurchase;
import com.ads.control.funtion.BillingListener;
import com.applovin.mediation.MaxError;
import com.example.andmoduleads.R;
import com.example.andmoduleads.applovin.MainApplovinActivity;
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
                        AppPurchase.getInstance().consumePurchase(AppPurchase.PRODUCT_ID_TEST);
                        loadSplashAdOpenApp();
                    }
                });
            }
        }, 5000);

        initBilling();
    }

    private void initBilling() {
        List<String> listINAPId = new ArrayList<>();
        listINAPId.add(MainActivity.PRODUCT_ID);
        List<String> listSubsId = new ArrayList<>();

        AppPurchase.getInstance().initBilling(getApplication(), listINAPId, listSubsId);
//        AppPurchase.getInstance().addProductId(MainActivity.PRODUCT_ID);

    }

    AppLovinCallback adCallback = new AppLovinCallback() {
        @Override
        public void onAdFailedToLoad(@Nullable MaxError i) {
            super.onAdFailedToLoad(i);
            startActivity(new Intent(SplashActivity.this, MainApplovinActivity.class));
            finish();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            Log.d(TAG, "onAdLoaded");
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "onAdClosed");
            startActivity(new Intent(SplashActivity.this, MainApplovinActivity.class));
            finish();
        }
    };

    private void loadSplash() {
        Log.d(TAG, "onCreate: show splash ads");
        Admob.getInstance().loadSplashInterstitalAds(this, getString(R.string.admod_interstitial_id), 30000, 5000, true, new AdCallback() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "Close ads splash ");
                startMain();
            }

            @Override
            public void onAdSplashReady() {
                super.onAdSplashReady();
                Log.d(TAG, "onAdSplashReady");
                Admob.getInstance().onShowSplash(SplashActivity.this, this);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError i) {
                startMain();
            }

            @Override
            public void onAdClosedByUser() {
                super.onAdClosedByUser();
                Log.d(TAG, "onAdClosedByUser");
            }
        });


    }

    private void loadSplashAdOpenApp() {
        loadAppLovinAd();
    }


    private void loadAppLovinAd() {
        AppLovin.getInstance().init(this, new AppLovinCallback() {
            @Override
            public void initAppLovinSuccess() {
                super.initAppLovinSuccess();
//                startActivity(new Intent(SplashActivity.this, MainApplovinActivity.class));
//                finish();
                AppLovin.getInstance().loadSplashInterstitialAds(SplashActivity.this, getString(R.string.applovin_test_inter), 30000, 7000, adCallback);
            }
        }, false);
    }

    private void loadAdmobAd() {
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
    protected void onResume() {
        super.onResume();
        AppLovin.getInstance().onCheckShowSplashWhenFail(this, adCallback, 1000);
    }

    @Override
    protected void onDestroy() {
//        AppOpenManager.getInstance().removeFullScreenContentCallback();
        super.onDestroy();
    }
}