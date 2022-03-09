package com.example.andmoduleads;

import com.ads.control.ads.Admod;
import com.ads.control.ads.AppOpenManager;
import com.ads.control.ads.application.AdsApplication;
import com.example.andmoduleads.admob.SplashActivity;

import java.util.Collections;
import java.util.List;


public class MyApplication extends AdsApplication {

    protected StorageCommon storageCommon;
    private static MyApplication context;

    public static MyApplication getApplication() {
        return context;
    }

    public StorageCommon getStorageCommon() {
        return storageCommon;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //        AppOpenManager.getInstance().setSplashActivity(SplashActivity.class, AppOpenManager.AD_UNIT_ID_TEST, 10000);
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        Admod.getInstance().setOpenActivityAfterShowInterAds(false);
        Admod.getInstance().setNumToShowAds(0);
//        Admod.getInstance().setNumToShowAds(3,3);
        storageCommon = new StorageCommon();

    }

    @Override
    public boolean enableAdsResume() {
        return true;
    }

    @Override
    public List<String> getListTestDeviceId() {
        // thêm device test ( trong log, bắt buộc với FAN)
        return Collections.singletonList("c75c6b77-92c5-4a63-b581-fa8bcbebbcf2");
    }


    @Override
    public String getOpenAppAdId() {
        return AppOpenManager.AD_UNIT_ID_TEST;
    }

    @Override
    public Boolean buildDebug() {
        return BuildConfig.DEBUG;
    }
}
