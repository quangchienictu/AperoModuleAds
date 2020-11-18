package com.example.andmoduleads;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ads.control.Admod;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Admod.getInstance().init(this);
        Admod.getInstance().loadBanner(this,getString(R.string.admod_banner_id));
        Admod.getInstance().loadNative(this,getString(R.string.admod_native_id));
    }
}