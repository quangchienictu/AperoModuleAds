package com.example.andmoduleads;

import com.ads.control.AdsApplication;
import com.ads.control.AppOpenManager;

import java.util.Collections;
import java.util.List;


public class MyApplication extends AdsApplication {

    @Override
    public void onCreate() {
        super.onCreate();
//        AppOpenManager.getInstance().setSplashActivity(SplashActivity.class, AppOpenManager.AD_UNIT_ID_TEST, 10000);
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
    }

    @Override
    public boolean enableAdsResume() {
        return true;
    }

    @Override
    public List<String> getListTestDeviceId() {
        return Collections.singletonList("07F6B55DA3A08766A4465F36354C7EF6");
    }

    @Override
    public String getOpenAppAdId() {
        return AppOpenManager.AD_UNIT_ID_TEST;
    }
}
