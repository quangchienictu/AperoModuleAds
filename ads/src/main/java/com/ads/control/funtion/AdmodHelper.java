package com.ads.control.funtion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class AdmodHelper {
    private static final String FILE_SETTING = "setting.pref";
    private static final String FILE_SETTING_ADMOD = "setting_admod.pref";
    private static final String IS_PURCHASE = "IS_PURCHASE";
    private static final String IS_RATE = "IS_RATE";
    private static final String IS_FIRST_OPEN = "IS_FIRST_OPEN";
    private static final String KEY_FIRST_TIME = "KEY_FIRST_TIME";

    public static void setPurchased(Activity activity, boolean isPurcharsed) {
        SharedPreferences pref = activity.getSharedPreferences(FILE_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_PURCHASE, isPurcharsed);
        editor.apply();
    }

    public static boolean isPurchased(Activity activity) {
        return activity.getSharedPreferences(FILE_SETTING, Context.MODE_PRIVATE).getBoolean(IS_PURCHASE, false);
    }

    public static void setRated(Context context, boolean isRate) {
        SharedPreferences pref = context.getSharedPreferences(FILE_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_RATE, isRate);
        editor.apply();
    }

    public static boolean isRated(Context context) {
        return context.getSharedPreferences(FILE_SETTING, Context.MODE_PRIVATE).getBoolean(IS_RATE, false);
    }


    /**
     *
     * @param context
     * @param idAds
     * @return
     */
    public static int getNumClickAdsPerDay(Context context, String idAds) {
        return context.getSharedPreferences(FILE_SETTING_ADMOD, Context.MODE_PRIVATE).getInt(idAds, 0);
    }


    public static void increaseNumClickAdsPerDay(Context context, String idAds) {
        SharedPreferences pre = context.getSharedPreferences(FILE_SETTING_ADMOD, Context.MODE_PRIVATE);
        int count = pre.getInt(idAds, 0);
        pre.edit().putInt(idAds, count + 1).apply();
    }

    public static void configTime(Context context) {
        if (isFirstOpen(context)) {
            context.getSharedPreferences(FILE_SETTING_ADMOD, Context.MODE_PRIVATE).edit().putLong(KEY_FIRST_TIME, System.currentTimeMillis()).apply();
            context.getSharedPreferences(FILE_SETTING, Context.MODE_PRIVATE).edit().putBoolean(IS_FIRST_OPEN, true).apply();
            return;
        }
        long firstTime = context.getSharedPreferences(FILE_SETTING_ADMOD, Context.MODE_PRIVATE).getLong(KEY_FIRST_TIME, System.currentTimeMillis());
        long rs = System.currentTimeMillis() - firstTime;
        if (rs > 10000) {
            resetDataAdmod(context);
        }
    }

    private static void resetDataAdmod(Context context) {
        context.getSharedPreferences(FILE_SETTING_ADMOD, Context.MODE_PRIVATE).edit().clear().apply();
        context.getSharedPreferences(FILE_SETTING_ADMOD, Context.MODE_PRIVATE).edit().putLong(KEY_FIRST_TIME, System.currentTimeMillis()).apply();
    }

    private static boolean isFirstOpen(Context context) {
        return context.getSharedPreferences(FILE_SETTING, Context.MODE_PRIVATE).getBoolean(IS_FIRST_OPEN, false);
    }
}
