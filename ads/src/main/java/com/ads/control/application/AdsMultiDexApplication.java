package com.ads.control.application;

import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.ads.control.ads.AperoAdConfig;
import com.ads.control.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class AdsMultiDexApplication extends MultiDexApplication {
    public static String TAG = "AdjustApero";
    protected AperoAdConfig aperoAdConfig;
    protected List<String> listTestDevice ;
    @Override
    public void onCreate() {
        super.onCreate();
        listTestDevice = new ArrayList<String>();
        aperoAdConfig = new AperoAdConfig();
        aperoAdConfig.setApplication(this);

    }


}
