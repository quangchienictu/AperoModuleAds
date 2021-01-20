package com.ads.control;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String TAG = "AppOpenManager";
    private static volatile  AppOpenManager INSTANCE;
    private AppOpenAd appOpenAd = null;

    private String appOpenAdId;
    private final Map<String, String> appOpenAdIdMap;

    private Activity currentActivity;

    private Application myApplication;

    private static boolean isShowingAd = false;
    private long loadTime = 0;

    private final List<Class> disabledAppOpenList;

    /**
     * Constructor
     */
    private AppOpenManager() {
        disabledAppOpenList = new ArrayList<>();
        appOpenAdIdMap = new HashMap<>();
    }

    public static synchronized AppOpenManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AppOpenManager();
        }
        return INSTANCE;
    }

    /**
     * Init AppOpenManager
     * @param application
     */
    public void init(Application application) {
        this.myApplication = application;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * Check app open ads is showing
     * @return
     */
    public boolean isShowingAd() {
        return isShowingAd;
    }


    /**
     * Disable app open app on specific activity
     * @param activityClass
     */
    public void disableAppResumeWithActivity(Class activityClass) {
        disabledAppOpenList.add(activityClass);
    }

    public void setAppOpenAdId(String appOpenAdId) {
        this.appOpenAdId = appOpenAdId;
    }

    public void setAppOpenAdIdWithActivity(Class activityClass, String appOpenAdId) {
        appOpenAdIdMap.put(activityClass.getName(), appOpenAdId);
    }

    /**
     * Request an ad
     */
    public void fetchAd(String adId) {
        if (isAdAvailable()) {
            return;
        }

        /**
         * Called when an app open ad has loaded.
         *
         * @param ad the loaded app open ad.
         */
        /**
         * Called when an app open ad has failed to load.
         *
         * @param loadAdError the error.
         */
        // Handle the error.
        AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            @Override
            public void onAppOpenAdLoaded(AppOpenAd ad) {
                AppOpenManager.this.appOpenAd = ad;
                AppOpenManager.this.loadTime = (new Date()).getTime();
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            @Override
            public void onAppOpenAdFailedToLoad(LoadAdError loadAdError) {
                // Handle the error.
            }

        };
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, adId, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }

    public void showAdIfAvailable(final String adId) {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            Log.d(TAG, "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
                            fetchAd(adId);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {}

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };

            appOpenAd.show(currentActivity, fullScreenContentCallback);

        } else {
            Log.d(TAG, "Can not show ad.");
            fetchAd(adId);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        for(Class activity : disabledAppOpenList) {
            if(activity.getName().equals(currentActivity.getClass().getName())) {
                return;
            }
        }

        if(appOpenAdIdMap.get(currentActivity.getClass().getName()) != null) {
            showAdIfAvailable(appOpenAdIdMap.get(currentActivity.getClass().getName()));
        } else {
            showAdIfAvailable(appOpenAdId);
        }
    }
}

