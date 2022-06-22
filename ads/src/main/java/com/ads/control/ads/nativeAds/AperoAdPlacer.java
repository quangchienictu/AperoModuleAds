package com.ads.control.ads.nativeAds;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ads.control.admob.Admob;
import com.ads.control.ads.wrapper.ApNativeAd;
import com.ads.control.funtion.AdCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AperoAdPlacer {
    String TAG = "AperoAdPlacer";
    private HashMap<Integer,ApNativeAd> listAd = new HashMap<>();
    private AperoAdPlacerSettings settings;
    private  RecyclerView.Adapter adapterOriginal;
    private Activity activity;


    public AperoAdPlacer(AperoAdPlacerSettings settings, RecyclerView.Adapter adapterOriginal, Activity activity) {
        this.settings = settings;
        this.adapterOriginal = adapterOriginal;
        this.activity = activity;
        configData();
    }

    private void configData(){
        if (settings.isRepeatingAd()){
            int posAddAd = 0;
            while (posAddAd<adapterOriginal.getItemCount()-settings.getPositionFixAd()){
                posAddAd += settings.getPositionFixAd();
                listAd.put(posAddAd,new ApNativeAd(StatusNative.AD_LOADING));
            }
        }else {
            listAd.put(settings.getPositionFixAd(),new ApNativeAd(StatusNative.AD_LOADING));
        }
    }

    public void renderAd(int pos, NativeAdView adPlace){
        ApNativeAd nativeAd = listAd.get(pos);
        if (nativeAd.getAdmobNativeAd()==null){
            Admob.getInstance().loadNativeAd(activity, settings.getAdUnitId(),new AdCallback(){
                @Override
                public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                    super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                    Log.e(TAG, "native loaded in pos: " + pos );

                    ApNativeAd nativeAd = new ApNativeAd(settings.getLayoutCustomAd(),unifiedNativeAd);
                    nativeAd.setStatus(StatusNative.AD_LOADED);
                    listAd.put(pos,nativeAd);
                    Admob.getInstance().populateUnifiedNativeAdView(unifiedNativeAd, adPlace);
                }
            });
        }else {
            Log.e(TAG, "reload ad pos: " + pos );
            Admob.getInstance().populateUnifiedNativeAdView(nativeAd.getAdmobNativeAd(), adPlace);
        }

    }

    public boolean isAdPosition(int pos) {
        ApNativeAd nativeAd = listAd.get(pos);
        return nativeAd!=null;
    }

    public int getAdjustedCount(){
        return adapterOriginal.getItemCount()+ listAd.size();
    }
}
