package com.example.andmoduleads.applovin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.ads.control.ads.nativeAds.AperoAdPlacerSettings;
import com.ads.control.ads.nativeAds.AperoRecyclerAdapter;
import com.ads.control.applovin.AppLovin;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.nativeAds.adPlacer.MaxAdPlacer;
import com.applovin.mediation.nativeAds.adPlacer.MaxRecyclerAdapter;
import com.example.andmoduleads.R;

public class MaxSimpleListActivity extends AppCompatActivity {
    private static final String TAG = "SimpleListActivity";
    MaxRecyclerAdapter  adAdapter;
    AperoRecyclerAdapter aperoRecyclerAdapter;

    MaxAdPlacer.Listener listener =   new MaxAdPlacer.Listener() {
            @Override
            public void onAdLoaded(int i) {
                Log.i(TAG, "onAdLoaded native list: "+ i);
            }

            @Override
            public void onAdRemoved(int i) {
                Log.i(TAG, "onAdRemoved: "+ i);
            }

            @Override
            public void onAdClicked(MaxAd maxAd) {

            }

            @Override
            public void onAdRevenuePaid(MaxAd maxAd) {

            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);

        // init adapter origin
        ListSimpleAdapter originalAdapter = new ListSimpleAdapter();
        RecyclerView recyclerView = findViewById(R.id.rvListSimple);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //init max recycle view
        adAdapter = AppLovin.getInstance().getNativeRepeatAdapter(this, getString(R.string.applovin_test_native), R.layout.max_native_custom_ad_small,
                originalAdapter, listener,5);
        recyclerView.setAdapter(adAdapter);
        adAdapter.loadAds();
    }

    @Override
    public void onDestroy()
    {
        adAdapter.destroy();
        super.onDestroy();
    }
}