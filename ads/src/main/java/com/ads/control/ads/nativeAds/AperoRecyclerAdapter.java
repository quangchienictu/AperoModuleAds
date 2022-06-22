package com.ads.control.ads.nativeAds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ads.control.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.nativead.NativeAdView;

public class AperoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_AD_VIEW = 0;
    private final int TYPE_DATA_VIEW = 1;

    private RecyclerView.Adapter adapterOriginal;
    private Activity activity;
    private AperoAdPlacer adPlacer;


    public AperoRecyclerAdapter(AperoAdPlacerSettings settings, RecyclerView.Adapter adapterOriginal, Activity activity) {
        this.adapterOriginal = adapterOriginal;
        this.activity = activity;
        adPlacer = new AperoAdPlacer(settings, adapterOriginal, activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_AD_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_small_native_control, parent, false);
            return new AperoViewHolder(view);
        } else {
            return adapterOriginal.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (adPlacer.isAdPosition(position)) {
            @SuppressLint("InflateParams") NativeAdView adView = (NativeAdView) LayoutInflater.from(activity)
                    .inflate(R.layout.small_native_admod_ad, null);
            FrameLayout adPlace = holder.itemView.findViewById(R.id.fl_adplaceholder);
            ShimmerFrameLayout containerShimmer = holder.itemView.findViewById(R.id.shimmer_container_small_native);
            containerShimmer.stopShimmer();
            containerShimmer.setVisibility(View.GONE);
            adPlace.setVisibility(View.VISIBLE);
            adPlacer.renderAd(position, adView);
            adPlace.removeAllViews();
            adPlace.addView(adView);
        } else {

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (adPlacer.isAdPosition(position)) {
            return TYPE_AD_VIEW;
        } else {
            return TYPE_DATA_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return adPlacer.getAdjustedCount();
    }

    private class AperoViewHolder extends RecyclerView.ViewHolder {
        public AperoViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}
