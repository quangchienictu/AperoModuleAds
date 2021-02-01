package com.example.andmoduleads;

import com.ads.control.AdsApplication;
import com.ads.control.AppOpenManager;

import java.util.Collections;
import java.util.List;


public class MyApplication extends AdsApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AppOpenManager.getInstance().setSplashActivity(SplashActivity.class, AppOpenManager.AD_UNIT_ID_TEST, 10000);
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
        return AppOpenManager.AD_UNIT_ID_TEST;
    }
}
