package com.ads.control.ads.wrapper;

import com.applovin.mediation.ads.MaxInterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;

public class ApInterstitialAd {
    private InterstitialAd interstitialAd;
    private MaxInterstitialAd maxInterstitialAd;

    public ApInterstitialAd(MaxInterstitialAd maxInterstitialAd) {
        this.maxInterstitialAd = maxInterstitialAd;
    }

    public ApInterstitialAd(InterstitialAd interstitialAd) {
        this.interstitialAd = interstitialAd;
    }

    public Boolean isReady(){
        if (maxInterstitialAd!=null && maxInterstitialAd.isReady())
            return true;
        return interstitialAd != null;
    }
}
