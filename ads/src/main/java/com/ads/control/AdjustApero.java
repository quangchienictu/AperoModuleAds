package com.ads.control;

import android.view.View;

import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustEvent;

public   class AdjustApero {

    public static final String KEY_REVENUE_SPLASH = "splash";
    public static final String KEY_REVENUE_INTER = "inter";
    public static final String KEY_REVENUE_BANNER = "banner";
    public static final String KEY_REVENUE_NATIVE = "native";
    public static final String KEY_REVENUE_REWARD = "reward";
    public static final String KEY_REVENUE_RESUME = "resume";


    public static   String ID_REVENUE_SPLASH = "";
    public static   String ID_REVENUE_INTER = "";
    public static   String ID_REVENUE_BANNER = "";
    public static   String ID_REVENUE_NATIVE = "";
    public static   String ID_REVENUE_REWARD = "";
    public static   String ID_REVENUE_RESUME = "";


    public static void trackAdRevenue(String id) {
        AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue(id);
        Adjust.trackAdRevenue(adjustAdRevenue);
    }

    public static void onTrackEvent(String eventName) {
        AdjustEvent event = new AdjustEvent(eventName);
        Adjust.trackEvent(event);
    }

    public static void onTrackEvent(String eventName, String id) {
        AdjustEvent event = new AdjustEvent(eventName);
        // Assign custom identifier to event which will be reported in success/failure callbacks.
        event.setCallbackId(id);
        Adjust.trackEvent(event);
    }

    public static void onTrackRevenue(String eventName,float revenue,String currency ) {
        AdjustEvent event = new AdjustEvent(eventName);
        // Add revenue 1 cent of an euro.
        event.setRevenue(revenue, currency);
        Adjust.trackEvent(event);
    }
}
