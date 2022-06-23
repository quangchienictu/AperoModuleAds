package com.ads.control.ads.nativeAds;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AperoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_AD_VIEW = 0;
    private final int TYPE_CONTENT_VIEW = 1;
    private final AperoAdPlacerSettings settings;

    private RecyclerView.Adapter adapterOriginal;
    private Activity activity;
    private AperoAdPlacer adPlacer;


    public AperoRecyclerAdapter(AperoAdPlacerSettings settings, RecyclerView.Adapter adapterOriginal, Activity activity) {
        this.adapterOriginal = adapterOriginal;
        this.activity = activity;
        this.settings = settings;
        adPlacer = new AperoAdPlacer(settings, adapterOriginal, activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_AD_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(settings.getLayoutAdPlaceHolder(), parent, false);
            return new AperoViewHolder(view);
        } else {
            return adapterOriginal.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (adPlacer.isAdPosition(position)) {

            adPlacer.renderAd(position,holder);

        } else {
            adapterOriginal.onBindViewHolder(holder, adPlacer.getOriginalPosition(position));
        }
    }

    public void loadAds(){
        adPlacer.loadAds();
    }

    @Override
    public int getItemViewType(int position) {
        if (adPlacer.isAdPosition(position)) {
            return TYPE_AD_VIEW;
        } else {
            return TYPE_CONTENT_VIEW;
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
