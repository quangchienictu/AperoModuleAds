package com.ads.control.application;

import com.ads.control.admob.Admob;
import com.ads.control.admob.AppOpenManager;
import com.ads.control.ads.AperoAd;
import com.google.android.gms.ads.AdActivity;

public abstract class AdmobMultiDexApplication  extends AdsMultiDexApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        AperoAd.getInstance().setUseAdmob(true);
        Admob.getInstance().init(this, getListTestDeviceId());

        AppOpenManager.getInstance().disableAppResumeWithActivity(AdActivity.class);
    }
}
