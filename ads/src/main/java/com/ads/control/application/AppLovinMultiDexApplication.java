package com.ads.control.application;

import com.ads.control.admob.AppOpenManager;
import com.ads.control.ads.AperoAd;
import com.applovin.adview.AppLovinFullscreenActivity;

public abstract class AppLovinMultiDexApplication extends AdsMultiDexApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        AperoAd.getInstance().setUseAdmob(false);
        AppOpenManager.getInstance().disableAppResumeWithActivity(AppLovinFullscreenActivity.class);
    }
}
