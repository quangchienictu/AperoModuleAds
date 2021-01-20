package com.example.andmoduleads;

import com.ads.control.AdsApplication;
import com.ads.control.AppOpenManager;

import java.util.Collections;
import java.util.List;


public class MyApplication extends AdsApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
    }

    @Override
    public boolean enableAdsResume() {
        return true;
    }

    @Override
    public List<String> getListTestDeviceId() {
        return Collections.singletonList("25D0EDA72688913D9FC256A6EB2E6DEF");
    }

    @Override
    public String getOpenAppAdId() {
        return "ca-app-pub-3940256099942544/3419835294";
    }
}
