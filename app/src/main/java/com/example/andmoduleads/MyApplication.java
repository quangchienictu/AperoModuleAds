package com.example.andmoduleads;

import android.util.Log;

import com.ads.control.AdjustApero;
import com.ads.control.Admod;
import com.ads.control.AdsApplication;
import com.ads.control.AdsMultiDexApplication;
import com.ads.control.AppOpenManager;
import com.ads.control.AppPurchase;
import com.ads.control.funtion.PurchaseListioner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyApplication extends AdsApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        //        AppOpenManager.getInstance().setSplashActivity(SplashActivity.class, AppOpenManager.AD_UNIT_ID_TEST, 10000);
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        Admod.getInstance().setOpenActivityAfterShowInterAds(true);
        AdjustApero.setEventNamePurchase("gzel1k");
//        Admod.getInstance().setNumToShowAds(3,3);
    }

    @Override
    public boolean enableAdsResume() {
        return true;
    }

    @Override
    public List<String> getListTestDeviceId() {
        return Collections.singletonList("0C415A270DF82EA01C765E69924B9327");
    }


    @Override
    public String getOpenAppAdId() {
        return AppOpenManager.AD_UNIT_ID_TEST;
    }

    @Override
    public boolean enableAdjust() {
        return false;
    }

    @Override
    public String getAdjustToken() {
        return "cc4jvudppczk";
    }
}
