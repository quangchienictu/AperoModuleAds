package com.ads.control.ads.nativeAds;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;

import com.ads.control.R;
import com.ads.control.ads.AperoAd;
import com.facebook.shimmer.ShimmerFrameLayout;

/**
 * Created by lamlt on 28/10/2022.
 */
public class AperoNativeAdView extends RelativeLayout {

    private int layoutCustomNativeAd;
    private ShimmerFrameLayout layoutLoading;
    private FrameLayout layoutPlaceHolder;

    public AperoNativeAdView(@NonNull Context context) {
        super(context);
        init();
    }

    public AperoNativeAdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AperoNativeAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    public AperoNativeAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AperoNativeAdView, 0, 0);
        layoutCustomNativeAd = typedArray.getInteger(R.styleable.AperoNativeAdView_layoutCustomNativeAd, 0);
        int idLayoutLoading = typedArray.getInteger(R.styleable.AperoNativeAdView_layoutLoading, 0);
        if (idLayoutLoading != 0)
            layoutLoading = (ShimmerFrameLayout) LayoutInflater.from(getContext()).inflate(idLayoutLoading, null);
        init();
    }

    private void init() {
        layoutPlaceHolder = new FrameLayout(getContext());
        addView(layoutPlaceHolder);
        if (layoutLoading != null)
            addView(layoutLoading);

    }

    public void setLayoutCustomNativeAd(int layoutCustomNativeAd) {
        this.layoutCustomNativeAd = layoutCustomNativeAd;
    }

    public void setLayoutLoading(int idLayoutLoading) {
        this.layoutLoading  = (ShimmerFrameLayout) LayoutInflater.from(getContext()).inflate(idLayoutLoading, null);
        addView(layoutLoading);
    }

    public void setLayoutPlaceHolder(FrameLayout layoutPlaceHolder) {
        this.layoutPlaceHolder = layoutPlaceHolder;
    }

    public void loadNativeAd(Activity activity, String idAd) {
        AperoAd.getInstance().loadNativeAd(activity, idAd, layoutCustomNativeAd, layoutPlaceHolder, layoutLoading);
    }
}