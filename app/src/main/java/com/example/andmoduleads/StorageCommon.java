package com.example.andmoduleads;


import com.facebook.ads.InterstitialAd;

public class StorageCommon {
    private InterstitialAd interstitialSplashAd;
    private InterstitialAd interstitialContentAd;



    public InterstitialAd getInterstitialSplashAd() {
        return interstitialSplashAd;
    }

    public void setInterstitialSplashAd(InterstitialAd interstitialSplashAd) {
        this.interstitialSplashAd = interstitialSplashAd;
    }

    public InterstitialAd getInterstitialContentAd() {
        return interstitialContentAd;
    }

    public void setInterstitialContentAd(InterstitialAd interstitialContentAd) {
        this.interstitialContentAd = interstitialContentAd;
    }
}
