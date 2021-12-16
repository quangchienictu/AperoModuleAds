package com.example.andmoduleads.iron_source;

import static com.example.andmoduleads.iron_source.IronSourceSplashActivity.IRON_SOURCE_APP_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ads.control.AppIronSource;
import com.example.andmoduleads.R;

public class Test2Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
//        AppIronSource.getInstance().initBanner(this,IRON_SOURCE_APP_KEY,true);
        AppIronSource.getInstance().loadBanner(this);
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

    @Override
    public void onBackPressed() {
        AppIronSource.getInstance().destroyBanner();// destroy banner nếu không còn sử dụng
        super.onBackPressed();
    }
}