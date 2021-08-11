package com.ads.control;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEventSuccess;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.OnEventTrackingSucceededListener;

import java.util.List;

public abstract class AdsMultiDexApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Admod.getInstance().init(this, getListTestDeviceId());
        if (enableAdsResume()) {
            AppOpenManager.getInstance().init(this, getOpenAppAdId());
        }
        if (enableAdjust()){
            Adjust.setEnabled(true);
            setupAdjust();
        }else {
            Adjust.setEnabled(false);
        }
    }

    private void setupAdjust() {
        String environment = AdjustConfig.ENVIRONMENT_SANDBOX;

        AdjustConfig config = new AdjustConfig(this, getAdjustToken(), environment);

        // Change the log level.
        config.setLogLevel(LogLevel.VERBOSE);
        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution) {
                Log.d("Adjust", "Attribution callback called!");
                Log.d("example", "Attribution: " + attribution.toString());
            }
        });

        // Set event success tracking delegate.
        config.setOnEventTrackingSucceededListener(new OnEventTrackingSucceededListener() {
            @Override
            public void onFinishedEventTrackingSucceeded(AdjustEventSuccess eventSuccessResponseData) {
                Log.d("Adjust", "Event success callback called!");
                Log.d("example", "Event success data: " + eventSuccessResponseData.toString());
            }
        });

        config.setSendInBackground(true);
        Adjust.onCreate(config);
        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());

        AdjustApero.trackAdRevenue(AdjustConfig.AD_REVENUE_ADMOB);
    }

    public abstract boolean enableAdsResume();

    public abstract List<String> getListTestDeviceId();
    public abstract String getOpenAppAdId();

    public abstract boolean enableAdjust();
    public abstract String getAdjustToken();


    private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
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
}
