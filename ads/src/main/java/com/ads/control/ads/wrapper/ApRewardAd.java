package com.ads.control.ads.wrapper;

import com.applovin.mediation.ads.MaxRewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAd;

public class ApRewardAd extends ApAdBase{
    private RewardedAd admobReward;
    private MaxRewardedAd maxReward;

    public ApRewardAd() {
    }

    public ApRewardAd(StatusAd status) {
        super(status);
    }

    public void setAdmobReward(RewardedAd admobReward) {
        this.admobReward = admobReward;
        status = StatusAd.AD_LOADED;
    }

    public void setMaxReward(MaxRewardedAd maxReward) {
        this.maxReward = maxReward;
        status = StatusAd.AD_LOADED;
    }

    public ApRewardAd(MaxRewardedAd maxReward) {
        this.maxReward = maxReward;
        status = StatusAd.AD_LOADED;
    }

    public ApRewardAd(RewardedAd admobReward) {
        this.admobReward = admobReward;
        status = StatusAd.AD_LOADED;
    }

    public RewardedAd getAdmobReward() {
        return admobReward;
    }

    public MaxRewardedAd getMaxReward() {
        return maxReward;
    }

    /**
     * Clean reward when shown
     */
    public void clean(){
        maxReward = null;
        admobReward = null;
    }

    @Override
    public boolean isReady(){
        return admobReward != null || maxReward !=null && maxReward.isReady();
    }
}
