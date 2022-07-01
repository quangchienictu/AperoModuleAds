package com.ads.control.ads.nativeAds;

import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.nativeAds.adPlacer.MaxRecyclerAdapter;

public class AperoAdAdapter {
    private AdmobRecyclerAdapter admobRecyclerAdapter;
    private MaxRecyclerAdapter maxRecyclerAdapter;

    public AperoAdAdapter(AdmobRecyclerAdapter admobRecyclerAdapter) {
        this.admobRecyclerAdapter = admobRecyclerAdapter;
    }

    public AperoAdAdapter(MaxRecyclerAdapter maxRecyclerAdapter) {
        this.maxRecyclerAdapter = maxRecyclerAdapter;
    }

    public RecyclerView.Adapter getAdapter(){
        if (admobRecyclerAdapter!=null) return admobRecyclerAdapter;
        return maxRecyclerAdapter;
    }

    public void destroy(){
        if (maxRecyclerAdapter !=null)
            maxRecyclerAdapter.destroy();
    }
}
