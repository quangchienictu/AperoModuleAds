package com.example.andmoduleads;

import com.ads.control.billing.AppPurchase;
import com.ads.control.util.AdjustApero;
import com.ads.control.ads.Admod;
import com.ads.control.ads.application.AdsApplication;
import com.ads.control.ads.AppOpenManager;
import com.example.andmoduleads.admob.MainActivity;
import com.example.andmoduleads.admob.SplashActivity;

import java.util.ArrayList;
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
        Admod.getInstance().setOpenActivityAfterShowInterAds(true);
        Admod.getInstance().setNumToShowAds(0);
        AdjustApero.setEventNamePurchase("gzel1k");
//        Admod.getInstance().setNumToShowAds(3,3);
        storageCommon = new StorageCommon();
        initBilling();
    }
    private void initBilling() {
        List<String> listINAPId = new ArrayList<>();
        listINAPId.add(MainActivity.PRODUCT_ID);
        List<String> listSubsId = new ArrayList<>();

        AppPurchase.getInstance().initBilling(getApplication(),listINAPId,listSubsId);
//        AppPurchase.getInstance().addProductId(MainActivity.PRODUCT_ID);

    }
    @Override
    public boolean enableAdsResume() {
        return true;
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
    }
}
