package com.example.andmoduleads.iron_source;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.AppIronSource;

public class BaseActivity  extends AppCompatActivity {


    @Override
    protected void onResume() {
        super.onResume();
        AppIronSource.getInstance().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppIronSource.getInstance().onPause(this);
    }
}
