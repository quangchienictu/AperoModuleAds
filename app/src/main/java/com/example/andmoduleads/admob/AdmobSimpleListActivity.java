package com.example.andmoduleads.admob;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ads.control.ads.nativeAds.AperoAdPlacerSettings;
import com.ads.control.ads.nativeAds.AperoRecyclerAdapter;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.nativeAds.adPlacer.MaxAdPlacer;
import com.applovin.mediation.nativeAds.adPlacer.MaxRecyclerAdapter;
import com.example.andmoduleads.R;
import com.example.andmoduleads.applovin.ListSimpleAdapter;

public class AdmobSimpleListActivity extends AppCompatActivity {
    private static final String TAG = "SimpleListActivity";
    AperoRecyclerAdapter aperoRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);

        // init adapter origin
        ListSimpleAdapter originalAdapter = new ListSimpleAdapter();
        RecyclerView recyclerView = findViewById(R.id.rvListSimple);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AperoAdPlacerSettings settings = new AperoAdPlacerSettings(com.ads.control.R.layout.small_native_admod_ad,com.ads.control.R.layout.layout_small_native_control);
        settings.setAdUnitId(getString(R.string.admod_native_id));
        settings.setRepeatingInterval(3);
        aperoRecyclerAdapter = new AperoRecyclerAdapter(settings,originalAdapter,this);
        recyclerView.setAdapter(aperoRecyclerAdapter);
//        aperoRecyclerAdapter.loadAds();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}