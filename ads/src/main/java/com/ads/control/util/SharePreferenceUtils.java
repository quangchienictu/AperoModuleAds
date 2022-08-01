package com.ads.control.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtils {
    private final static String PREF_NAME = "apero_ad_pref";

    private final static String KEY_INSTALL_TIME = "KEY_INSTALL_TIME";

    private final static String KEY_CURRENT_TOTAL_REVENUE_AD = "KEY_CURRENT_TOTAL_REVENUE_AD";

    private final static String KEY_PUSH_EVENT_REVENUE_3_DAY = "KEY_PUSH_EVENT_REVENUE_3_DAY";

    private final static String KEY_PUSH_EVENT_REVENUE_7_DAY = "KEY_PUSH_EVENT_REVENUE_7_DAY";

    public static long getInstallTime(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getLong(KEY_INSTALL_TIME, 0);
    }

    public static void setInstallTime(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putLong(KEY_INSTALL_TIME, System.currentTimeMillis() / 1000000).apply();
    }

    public static long getCurrentTotalRevenueAd(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getLong(KEY_CURRENT_TOTAL_REVENUE_AD, 0);
    }

    public static void updateCurrentTotalRevenueAd(Context context, long revenue) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long currentTotalRevenue = pre.getLong(KEY_CURRENT_TOTAL_REVENUE_AD, 0);
        currentTotalRevenue += revenue / 1000000.0;
        pre.edit().putLong(KEY_CURRENT_TOTAL_REVENUE_AD, currentTotalRevenue).apply();
    }

    public static boolean isPushRevenue3Day(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(KEY_PUSH_EVENT_REVENUE_3_DAY, false);
    }

    public static void setPushedRevenue3Day(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(KEY_PUSH_EVENT_REVENUE_3_DAY, true).apply();
    }

    public static boolean isPushRevenue7Day(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pre.getBoolean(KEY_PUSH_EVENT_REVENUE_7_DAY, false);
    }

    public static void setPushedRevenue7Day(Context context) {
        SharedPreferences pre = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pre.edit().putBoolean(KEY_PUSH_EVENT_REVENUE_7_DAY, true).apply();
    }
}
