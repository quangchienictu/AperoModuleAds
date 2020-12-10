package com.example.andmoduleads;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ads.control.Admod;

public class ContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Admod.getInstance().loadSmallNative(this,getString(R.string.admod_native_id));
    }
}