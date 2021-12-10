package com.example.andmoduleads.iron_source;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.ads.control.AppIronSource;
import com.ads.control.funtion.AdCallback;
import com.example.andmoduleads.R;

public class TestISActivity extends BaseActivity {

    Button btnLoadAds;
    Button btForceShowAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_isactivity);
        btnLoadAds = findViewById(R.id.btnLoadAds);
        btForceShowAds = findViewById(R.id.btForceShowAds);


//        btForceShowAds.setEnabled(false);
        AppIronSource.getInstance().loadBanner(this);
        btnLoadAds.setOnClickListener(v -> {
            if (AppIronSource.getInstance().isInterstitialReady()){
                Toast.makeText(this, "Ad is loaded", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Ad  loading", Toast.LENGTH_SHORT).show();
                AppIronSource.getInstance().loadInterstitial(this,new AdCallback(){
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Toast.makeText(TestISActivity.this, "Ad  loaded", Toast.LENGTH_SHORT).show();
                        btForceShowAds.setEnabled(true);
                    }
                });
            }
        });

        btForceShowAds.setOnClickListener(v ->{
            if (AppIronSource.getInstance().isInterstitialReady())
            AppIronSource.getInstance().showInterstitial();
            else
                Toast.makeText(this, "Ad not loaded", Toast.LENGTH_SHORT).show();
        });
    }
}