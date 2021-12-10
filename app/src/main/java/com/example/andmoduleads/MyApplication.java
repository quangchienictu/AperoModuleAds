package com.example.andmoduleads;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

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
        return Collections.singletonList("07F6B55DA3A08766A4465F36354C7EF6");
    }



    @Override
    public String getOpenAppAdId() {
        return AppOpenManager.AD_UNIT_ID_TEST;
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
