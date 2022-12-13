package com.ads.control.util;

import android.app.Activity;
import android.util.DisplayMetrics;

public class AppUtil {
    public static Boolean VARIANT_DEV = true;

    /**
     * current total revenue for paid_ad_impression_value_0.01 event
     */
    public static float currentTotalRevenue001Ad;

    public static int convertDpToPixel(Activity activity, float dp) {
        return (int) (dp * (activity.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
