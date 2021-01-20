package com.example.andmoduleads;

import com.ads.control.AdsApplication;
import com.ads.control.AppOpenManager;

import java.util.Collections;
import java.util.List;


public class MyApplication extends AdsApplication {

    @Override
    public void onCreate() {
        super.onCreate();
//        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        AppOpenManager.getInstance().setAppOpenAdIdWithActivity(MainActivity.class, "ca-app-pub-3940256099942544/3419835294");
        AppOpenManager.getInstance().setAppOpenAdId("ca-app-pub-3940256099942544/3419835295");
    }

    @Override
    public boolean enableAdsResume() {
        return true;
    }

    @Override
    public List<String> getListTestDeviceId() {
        return Collections.singletonList("25D0EDA72688913D9FC256A6EB2E6DEF");
    }
}
