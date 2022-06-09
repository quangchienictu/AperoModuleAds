package com.ads.control.ads;

import com.ads.control.admob.AppOpenManager;

public class AperoAd {
    private static volatile AperoAd INSTANCE;
    private Boolean isUseAdmob = true;


    public static synchronized AperoAd getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AperoAd();
        }
        return INSTANCE;
    }

    public void setUseAdmob(Boolean useAdmob) {
        isUseAdmob = useAdmob;
    }
}
