package com.example.andmoduleads;

import com.ads.control.ads.AperoAd;
import com.ads.control.ads.AperoAdConfig;
import com.ads.control.application.AdsMultiDexApplication;
import com.ads.control.billing.AppPurchase;
import com.ads.control.admob.Admob;
import com.ads.control.admob.AppOpenManager;
import com.example.andmoduleads.admob.MainActivity;
import com.example.andmoduleads.admob.SplashActivity;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends AdsMultiDexApplication {

    private final String ADJUST_TOKEN = "cc4jvudppczk";
    private final String EVENT_PURCHASE_ADJUST = "gzel1k";

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
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        Admob.getInstance().setNumToShowAds(0);

        storageCommon = new StorageCommon();
        initAds();

        initBilling();
    }

    private void initAds() {
        aperoAdConfig.setMediationProvider(AperoAdConfig.PROVIDER_ADMOB);
        aperoAdConfig.setVariant(BuildConfig.DEBUG);
        aperoAdConfig.enableAdjust(ADJUST_TOKEN,EVENT_PURCHASE_ADJUST);
        aperoAdConfig.setIdAdResume(AppOpenManager.AD_UNIT_ID_TEST);
        listTestDevice.add("EC25F576DA9B6CE74778B268CB87E431");
        aperoAdConfig.setListDeviceTest(listTestDevice);

        AperoAd.getInstance().init(this, aperoAdConfig, false);
    }

    private void initBilling() {
        List<String> listINAPId = new ArrayList<>();
        listINAPId.add(MainActivity.PRODUCT_ID);
        List<String> listSubsId = new ArrayList<>();
        Admob.getInstance().setOpenActivityAfterShowInterAds(true);
        AppPurchase.getInstance().initBilling(getApplication(),listINAPId,listSubsId);

    }

}
