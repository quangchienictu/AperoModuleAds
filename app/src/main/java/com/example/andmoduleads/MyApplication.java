package com.example.andmoduleads;

import androidx.multidex.MultiDexApplication;

import com.ads.control.ads.AperoAd;
import com.ads.control.ads.AperoAdConfig;
import com.ads.control.application.AdsMultiDexApplication;
import com.ads.control.billing.AppPurchase;
import com.ads.control.util.AdjustApero;
import com.ads.control.admob.Admob;
import com.ads.control.admob.AppOpenManager;
import com.applovin.impl.sdk.c;
import com.example.andmoduleads.admob.MainActivity;
import com.example.andmoduleads.admob.SplashActivity;

import java.util.ArrayList;
import java.util.Collections;
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
//        AppOpenManager.getInstance().setSplashActivity(SplashActivity.class, AppOpenManager.AD_UNIT_ID_TEST, 5000);
//        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);

        Admob.getInstance().setNumToShowAds(0);

//        AdjustApero.setEventNamePurchase("gzel1k");
        storageCommon = new StorageCommon();
        initBilling();
        initAds();
    }

    private void initAds() {
        aperoAdConfig.setMediationProvider(AperoAdConfig.MEDIATION_ADMOB);
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

        AppPurchase.getInstance().initBilling(getApplication(),listINAPId,listSubsId);
//        AppPurchase.getInstance().addProductId(MainActivity.PRODUCT_ID);

    }

   /* @Override
    public boolean enableAdsResume() {
        return false;
    }

    @Override
    public List<String> getListTestDeviceId() {
        // thêm device test ( trong log, bắt buộc với FAN)
        return Collections.singletonList("EC25F576DA9B6CE74778B268CB87E431");
    }


    @Override
    public String getOpenAppAdId() {
        return AppOpenManager.AD_UNIT_ID_TEST;
    }

    @Override
    public Boolean buildDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public boolean enableAdjust() {
        return true;
    }

    @Override
    public String getAdjustToken() {
        return "cc4jvudppczk";
    }*/
}
