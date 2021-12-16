package com.example.andmoduleads.iron_source;

import static com.example.andmoduleads.iron_source.IronSourceSplashActivity.IRON_SOURCE_APP_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

//        AppIronSource.getInstance().initBanner(this,IRON_SOURCE_APP_KEY,true);

        btnLoadAds = findViewById(R.id.btnLoadAds);
        btForceShowAds = findViewById(R.id.btForceShowAds);


//        btForceShowAds.setEnabled(false);
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

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        AppIronSource.getInstance().destroyBanner();// destroy banner nếu xử dụng banner trong 1 activity khác
                        startActivity(new Intent(TestISActivity.this,Test2Activity.class));
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

    @Override
    protected void onStart() {
        super.onStart();

        AppIronSource.getInstance().loadBanner(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}