package com.example.andmoduleads.applovin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.ads.control.ads.AperoAd;
import com.ads.control.ads.AperoAdConfig;
import com.ads.control.ads.nativeAds.AperoAdPlacer;
import com.ads.control.ads.nativeAds.AperoAdAdapter;
import com.ads.control.ads.wrapper.ApAdValue;
import com.example.andmoduleads.R;

public class MaxSimpleListActivity extends AppCompatActivity {
    private static final String TAG = "SimpleListActivity";
    AperoAdAdapter adAdapter;
    int layoutCustomNative = com.ads.control.R.layout.custom_native_admod_medium;
    String idNative = "";
    SwipeRefreshLayout swRefresh;
    AperoAdPlacer.Listener listener = new AperoAdPlacer.Listener() {
        @Override
        public void onAdLoaded(int i) {
            Log.i(TAG, "onAdLoaded native list: " + i);
        }

        @Override
        public void onAdRemoved(int i) {
            Log.i(TAG, "onAdRemoved: " + i);
        }

        @Override
        public void onAdClicked() {

        }

        @Override
        public void onAdRevenuePaid(ApAdValue adValue) {

        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        swRefresh = findViewById(R.id.swRefresh);

        // init adapter origin
        ListSimpleAdapter originalAdapter = new ListSimpleAdapter();
        RecyclerView recyclerView = findViewById(R.id.rvListSimple);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (AperoAd.getInstance().getMediationProvider() == AperoAdConfig.PROVIDER_ADMOB) {
            layoutCustomNative = com.ads.control.R.layout.custom_native_admod_medium;
            idNative = getString(R.string.admod_native_id);
        } else {
            layoutCustomNative = R.layout.custom_native_max_small;
            idNative = getString(R.string.applovin_test_native);
        }

        adAdapter = AperoAd.getInstance().getNativeRepeatAdapter(this,idNative, layoutCustomNative, com.ads.control.R.layout.layout_native_medium,
                originalAdapter, listener, 5);

        recyclerView.setAdapter(adAdapter.getAdapter());

        swRefresh.setOnRefreshListener(() -> {
            originalAdapter.addItem(0);
            adAdapter.getAdapter().notifyDataSetChanged();
            swRefresh.setRefreshing(false);
        });
    }

    @Override
    public void onDestroy() {
        adAdapter.destroy();
        super.onDestroy();
    }
}