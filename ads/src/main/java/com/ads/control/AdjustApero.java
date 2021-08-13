package com.ads.control;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustEvent;
import com.google.android.gms.ads.AdValue;

import java.util.Map;

public class AdjustApero {


    public static Map<String, String> eventIds;
    public static boolean enableAdjust = false;


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

    public static void onTrackRevenue(String eventName, float revenue, String currency) {
        AdjustEvent event = new AdjustEvent(eventName);
        // Add revenue 1 cent of an euro.
        event.setRevenue(revenue, currency);
        Adjust.trackEvent(event);
    }

    public static void pushTrackEventAdmod(String adId, AdValue adValue) {
        if (AdjustApero.enableAdjust) {
            String eventId = AdjustApero.eventIds.get(adId);
            if (eventId == null || eventId.isEmpty()) {

                new android.os.Handler().post(
                        new Runnable() {
                            public void run() {
                                throw new RuntimeException("Adjust event id null at :" + adId);
                            }
                        });
            }
            AdjustApero.onTrackRevenue(eventId, adValue.getValueMicros(), adValue.getCurrencyCode());
        }
    }
}
