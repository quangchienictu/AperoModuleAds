package com.ads.control.ads.nativeAds;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AperoAdPlacer {

    private List<Objects> listAd = new ArrayList<>();

    public AperoAdPlacer(Activity activity) {
    }

    public boolean isAdPosition(int pos) {

        return false;
    }

    public int getAdjustedCount(){
        return listAd.size();
    }
}
