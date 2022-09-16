package com.ads.control.event;

import android.content.Context;


/**
 * Created by lamlt on 13/09/2022.
 */
public class AperoAppsflyer   {

    private Context context;

    public AperoAppsflyer() {
    }

    public void init(Context context,String devKey){
        this.context = context;
//        AppsFlyerLib.getInstance().init(devKey, null, context);
    }
}
