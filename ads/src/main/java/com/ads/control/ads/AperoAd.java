package com.ads.control.ads;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEventFailure;
import com.adjust.sdk.AdjustEventSuccess;
import com.adjust.sdk.AdjustSessionFailure;
import com.adjust.sdk.AdjustSessionSuccess;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.OnEventTrackingFailedListener;
import com.adjust.sdk.OnEventTrackingSucceededListener;
import com.adjust.sdk.OnSessionTrackingFailedListener;
import com.adjust.sdk.OnSessionTrackingSucceededListener;
import com.ads.control.admob.Admob;
import com.ads.control.admob.AppOpenManager;
import com.ads.control.ads.wrapper.ApAdError;
import com.ads.control.applovin.AppLovin;
import com.ads.control.applovin.AppLovinCallback;
import com.ads.control.funtion.AdCallback;
import com.ads.control.util.AdjustApero;
import com.ads.control.util.AppUtil;
import com.applovin.mediation.MaxError;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;

public class AperoAd {
    public static final String TAG_ADJUST = "AperoAdjust";
    private static volatile AperoAd INSTANCE;
    private AperoAdConfig adConfig;
    private  AperoInitCallback initCallback;
    private Boolean initAdSuccess = false;
    public static synchronized AperoAd getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AperoAd();
        }
        return INSTANCE;
    }


    /**
     * @param context
     * @param adConfig AperoAdConfig object used for SDK initialisation
     */
    public void init(Context context, AperoAdConfig adConfig) {
        init(context, adConfig, false);
    }

    /**
     * @param context
     * @param adConfig             AperoAdConfig object used for SDK initialisation
     * @param enableDebugMediation set show Mediation Debugger - use only for Max Mediation
     */
    public void init(Context context, AperoAdConfig adConfig, Boolean enableDebugMediation) {
        if (adConfig == null) {
            throw new RuntimeException("cant not set AperoAdConfig null");
        }
        this.adConfig = adConfig;
        AppUtil.VARIANT_DEV = adConfig.isVariantDev();
        Log.i("Application", " is run dev: " + AppUtil.VARIANT_DEV);
        if (adConfig.isEnableAdjust()){
            AdjustApero.enableAdjust = true;
            setupAdjust(adConfig.isVariantDev(), adConfig.getAdjustToken());
        }
        switch (adConfig.getMediationProvider()) {
            case AperoAdConfig.MEDIATION_MAX:
                AppLovin.getInstance().init(context, new AppLovinCallback() {
                    @Override
                    public void initAppLovinSuccess() {
                        super.initAppLovinSuccess();
                        initAdSuccess = true;
                        if (initCallback!=null)
                            initCallback.initAdSuccess();
                    }
                }, enableDebugMediation);
                break;
            case AperoAdConfig.MEDIATION_ADMOB:
                Admob.getInstance().init(context);
                if (adConfig.isEnableAdResume())
                    AppOpenManager.getInstance().init(adConfig.getApplication(), adConfig.getIdAdResume());

                initAdSuccess = true;
                if (initCallback!=null)
                    initCallback.initAdSuccess();
                break;
        }
    }

    public void setInitCallback(AperoInitCallback initCallback) {
        this.initCallback = initCallback;
        if (initAdSuccess)
            initCallback.initAdSuccess();
    }

    private void setupAdjust(Boolean buildDebug, String adjustToken) {

        String environment = buildDebug ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
        Log.i("Application", "setupAdjust: " + environment);
        AdjustConfig config = new AdjustConfig(adConfig.getApplication(), adjustToken, environment);

        // Change the log level.
        config.setLogLevel(LogLevel.VERBOSE);
        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution) {
                Log.d(TAG_ADJUST, "Attribution callback called!");
                Log.d(TAG_ADJUST, "Attribution: " + attribution.toString());
            }
        });

        // Set event success tracking delegate.
        config.setOnEventTrackingSucceededListener(new OnEventTrackingSucceededListener() {
            @Override
            public void onFinishedEventTrackingSucceeded(AdjustEventSuccess eventSuccessResponseData) {
                Log.d(TAG_ADJUST, "Event success callback called!");
                Log.d(TAG_ADJUST, "Event success data: " + eventSuccessResponseData.toString());
            }
        });
        // Set event failure tracking delegate.
        config.setOnEventTrackingFailedListener(new OnEventTrackingFailedListener() {
            @Override
            public void onFinishedEventTrackingFailed(AdjustEventFailure eventFailureResponseData) {
                Log.d(TAG_ADJUST, "Event failure callback called!");
                Log.d(TAG_ADJUST, "Event failure data: " + eventFailureResponseData.toString());
            }
        });

        // Set session success tracking delegate.
        config.setOnSessionTrackingSucceededListener(new OnSessionTrackingSucceededListener() {
            @Override
            public void onFinishedSessionTrackingSucceeded(AdjustSessionSuccess sessionSuccessResponseData) {
                Log.d(TAG_ADJUST, "Session success callback called!");
                Log.d(TAG_ADJUST, "Session success data: " + sessionSuccessResponseData.toString());
            }
        });

        // Set session failure tracking delegate.
        config.setOnSessionTrackingFailedListener(new OnSessionTrackingFailedListener() {
            @Override
            public void onFinishedSessionTrackingFailed(AdjustSessionFailure sessionFailureResponseData) {
                Log.d(TAG_ADJUST, "Session failure callback called!");
                Log.d(TAG_ADJUST, "Session failure data: " + sessionFailureResponseData.toString());
            }
        });


        config.setSendInBackground(true);
        Adjust.onCreate(config);
        adConfig.getApplication().registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
    }

    private static final class AdjustLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }
    }

    public void loadSplashInterstitialAds(final Context context, String id, long timeOut, long timeDelay, AperoAdCallback adListener) {
        loadSplashInterstitialAds(context, id, timeOut, timeDelay, true, adListener);
    }

    public void loadSplashInterstitialAds(final Context context, String id, long timeOut, long timeDelay, boolean showSplashIfReady, AperoAdCallback adListener) {
        switch (adConfig.getMediationProvider()) {
            case AperoAdConfig.MEDIATION_ADMOB:
                Admob.getInstance().loadSplashInterstitalAds(context, id, timeOut, timeDelay, showSplashIfReady, new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        adListener.onAdClosed();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        adListener.onAdLoaded();
                    }

                    @Override
                    public void onAdSplashReady() {
                        super.onAdSplashReady();
                        adListener.onAdSplashReady();
                    }

                    @Override
                    public void onAdClosedByUser() {
                        super.onAdClosedByUser();
                        adListener.onAdClosedByUser();
                    }
                });
                break;
            case AperoAdConfig.MEDIATION_MAX:
                AppLovin.getInstance().loadSplashInterstitialAds(context, id, timeOut, timeDelay, showSplashIfReady, new AppLovinCallback() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        adListener.onAdClosed();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        adListener.onAdLoaded();
                    }

                    @Override
                    public void onAdSplashReady() {
                        super.onAdSplashReady();
                        adListener.onAdSplashReady();
                    }

                    @Override
                    public void onAdClosedByUser() {
                        super.onAdClosedByUser();
                        adListener.onAdClosedByUser();
                    }
                });
        }
    }


    /**
     * Called  on Resume - SplashActivity
     * It call reshow ad splash when ad splash show fail in background
     * @param activity
     * @param callback
     * @param timeDelay
     */
    public void onCheckShowSplashWhenFail(Activity activity, AperoAdCallback callback, int timeDelay) {
        switch (adConfig.getMediationProvider()) {
            case AperoAdConfig.MEDIATION_ADMOB:
                Admob.getInstance().onCheckShowSplashWhenFail(activity, new AdCallback(){
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        callback.onAdClosed();
                    }

                    @Override
                    public void onAdClosedByUser() {
                        super.onAdClosedByUser();
                        callback.onAdClosedByUser();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        callback.onAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        callback.onAdFailedToLoad(new ApAdError(i));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable AdError adError) {
                        super.onAdFailedToShow(adError);
                        callback.onAdFailedToShow(new ApAdError(adError));
                    }
                }, timeDelay);
                break;
            case AperoAdConfig.MEDIATION_MAX:
                AppLovin.getInstance().onCheckShowSplashWhenFail(activity, new AppLovinCallback(){
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        callback.onAdClosed();
                    }

                    @Override
                    public void onAdClosedByUser() {
                        super.onAdClosedByUser();
                        callback.onAdClosedByUser();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        callback.onAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable MaxError i) {
                        super.onAdFailedToLoad(i);
                        callback.onAdFailedToLoad(new ApAdError(i));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable MaxError adError) {
                        super.onAdFailedToShow(adError);
                        callback.onAdFailedToShow(new ApAdError(adError));
                    }
                }, timeDelay);
        }
    }

}
