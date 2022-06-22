package com.ads.control.ads.wrapper;

import com.applovin.mediation.MaxError;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;

public class ApAdError {

    private MaxError maxError;
    private LoadAdError loadAdError;
    private AdError adError;


    public ApAdError(AdError adError) {
        this.adError = adError;
    }

    public ApAdError(LoadAdError loadAdError) {
        this.loadAdError = loadAdError;
    }

    public ApAdError(MaxError maxError) {
        this.maxError = maxError;
    }

    public String getMessage(){
        if (maxError!=null)
            return maxError.getMessage();
        if (loadAdError!=null)
            return loadAdError.getMessage();
        if (adError!=null)
            return adError.getMessage();
        return "unknown error";
    }
}
