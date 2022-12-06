package com.ads.control.ads.bannerAds;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ads.control.R;
import com.ads.control.ads.AperoAd;
import com.ads.control.ads.AperoAdCallback;
import com.ads.control.ads.wrapper.ApNativeAd;
import com.facebook.shimmer.ShimmerFrameLayout;

/**
 * Created by lamlt on 28/10/2022.
 */
public class AperoBannerAdView extends RelativeLayout {

    private String TAG = "AperoBannerAdView";

    public AperoBannerAdView(@NonNull Context context) {
        super(context);
        init();
    }

    public AperoBannerAdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AperoBannerAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    public AperoBannerAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_banner_control, this);
    }

    public void loadBanner(Activity activity, String idBanner) {
        loadBanner(activity, idBanner, new AperoAdCallback());
    }

    public void loadBanner(Activity activity, String idBanner, AperoAdCallback aperoAdCallback) {
        AperoAd.getInstance().loadBanner(activity, idBanner, aperoAdCallback);
    }
}