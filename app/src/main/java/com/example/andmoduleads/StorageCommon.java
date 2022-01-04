package com.example.andmoduleads;


import com.facebook.ads.InterstitialAd;
import com.facebook.ads.NativeBannerAd;

public class StorageCommon {
    private InterstitialAd interstitialSplashAd;
    private InterstitialAd interstitialContentAd;

    private NativeBannerAd  nativeBannerAd;


    public NativeBannerAd getNativeBannerAd() {
        return nativeBannerAd;
    }

    public void setNativeBannerAd(NativeBannerAd nativeBannerAd) {
        this.nativeBannerAd = nativeBannerAd;
    }

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
