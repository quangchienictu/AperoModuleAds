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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String TAG = "AppOpenManager";
    public static final String AD_UNIT_ID_TEST = "ca-app-pub-3940256099942544/3419835294";

    private static volatile AppOpenManager INSTANCE;
    private AppOpenAd appOpenAd = null;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private FullScreenContentCallback fullScreenContentCallback;

    private String appOpenAdId;

    private Activity currentActivity;

    private Application myApplication;

    private static boolean isShowingAd = false;
    private long loadTime = 0;

    private final List<Class> disabledAppOpenList;
    private final Set<Class> splashActivityList;
    private final Map<Class, String> classAdMap;

    /**
     * Constructor
     */
    private AppOpenManager() {
        disabledAppOpenList = new ArrayList<>();
        splashActivityList = new HashSet<>();
        classAdMap = new HashMap<>();
    }

    public static synchronized AppOpenManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppOpenManager();
        }
        return INSTANCE;
    }

    /**
     * Init AppOpenManager
     *
     * @param application
     */
    public void init(Application application, String appOpenAdId) {
        this.myApplication = application;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        this.appOpenAdId = appOpenAdId;
        if (!Purchase.getInstance().isPurchased(application.getApplicationContext()) && !isAdAvailable() && appOpenAdId != null) {
            fetchAd(appOpenAdId);
        }
    }

    /**
     * Check app open ads is showing
     *
     * @return
     */
    public boolean isShowingAd() {
        return isShowingAd;
    }

    /**
     * Disable app open app on specific activity
     *
     * @param activityClass
     */
    public void disableAppResumeWithActivity(Class activityClass) {
        disabledAppOpenList.add(activityClass);
    }

    public void addSplashActivity(Class splashActivity, String adId) {
        splashActivityList.add(splashActivity);
        classAdMap.put(splashActivity, adId);
    }

    public void removeSplashActivity(Class splasActivity) {
        splashActivityList.remove(splasActivity);
    }

    public void setAppOpenAdId(String appOpenAdId) {
        this.appOpenAdId = appOpenAdId;
    }

    public void setFullScreenContentCallback(FullScreenContentCallback callback) {
        this.fullScreenContentCallback = callback;
    }

    public void removeFullScreenContentCallback() {
        this.fullScreenContentCallback = null;
    }

    /**
     * Request an ad
     */
    public void fetchAd(String adId) {
        if (isAdAvailable()) {
            return;
        }

        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    @Override
                    public void onAppOpenAdLoaded(AppOpenAd ad) {
                        Log.d(TAG, "onAppOpenAdLoaded: ");
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
                        Log.d(TAG, "onAppOpenAdFailedToLoad: " + loadAdError.getMessage());
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
        boolean wasLoadTimeLessThanNHoursAgo = wasLoadTimeLessThanNHoursAgo(4);
        Log.d(TAG, "isAdAvailable: " + wasLoadTimeLessThanNHoursAgo);
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }

    public void showAdIfAvailable(final String adId) {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (currentActivity != null && Purchase.getInstance().isPurchased(currentActivity)) {
            if (fullScreenContentCallback != null) {
                fullScreenContentCallback.onAdDismissedFullScreenContent();
            }
            return;
        }

        Log.d(TAG, "showAdIfAvailable: " +ProcessLifecycleOwner.get().getLifecycle().getCurrentState());
        if (!ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            Log.d(TAG, "showAdIfAvailable: return");
            return;
        }

        if (!isShowingAd && isAdAvailable()) {
            Log.d(TAG, "Will show ad.");

            FullScreenContentCallback callback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null;
                            if (fullScreenContentCallback != null) {
                                fullScreenContentCallback.onAdDismissedFullScreenContent();
                            }
                            isShowingAd = false;
                            fetchAd(adId);

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            if (fullScreenContentCallback != null) {
                                fullScreenContentCallback.onAdFailedToShowFullScreenContent(adError);
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };

            appOpenAd.show(currentActivity, callback);

        } else {
            Log.d(TAG, "Ad is not ready");
            fetchAd(adId);
        }
    }

    public void loadAndShowSplashAds(final String adId) {
        if (currentActivity != null && Purchase.getInstance().isPurchased(currentActivity)) {
            if (fullScreenContentCallback != null) {
                fullScreenContentCallback.onAdDismissedFullScreenContent();
            }
            return;
        }

        if (isAdAvailable()) {
            showAdIfAvailable(adId);
        }

        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    @Override
                    public void onAppOpenAdLoaded(AppOpenAd ad) {
                        Log.d(TAG, "onAppOpenAdLoaded: splash");
                        AppOpenManager.this.appOpenAd = ad;
                        AppOpenManager.this.loadTime = (new Date()).getTime();
                        showAdIfAvailable(adId);
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    @Override
                    public void onAppOpenAdFailedToLoad(LoadAdError loadAdError) {
                        if(fullScreenContentCallback != null){
                            fullScreenContentCallback.onAdDismissedFullScreenContent();
                        }
                    }

                };
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, adId, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        for (Class activity : disabledAppOpenList) {
            if (activity.getName().equals(currentActivity.getClass().getName())) {
                return;
            }
        }

        for (Class activity : splashActivityList) {
            if (activity.getName().equals(currentActivity.getClass().getName())) {
                String adId = classAdMap.get(activity);
                if(adId == null) {
                    adId = appOpenAdId;
                }
                loadAndShowSplashAds(adId);
                return;
            }

        }
        showAdIfAvailable(appOpenAdId);
        Log.d(TAG, "onStart");
    }

}

