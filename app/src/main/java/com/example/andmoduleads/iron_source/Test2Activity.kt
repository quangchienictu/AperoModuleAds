package com.example.andmoduleads.iron_source

import android.os.Bundle
import com.ads.control.ads.AppIronSource
import com.example.andmoduleads.R

class Test2Activity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)
        //        AppIronSource.getInstance().initBanner(this,IRON_SOURCE_APP_KEY,true);
        AppIronSource.getInstance().loadBanner(this)
    }

    override fun onStart() {
        super.onStart()
        AppIronSource.getInstance().loadBanner(this)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onBackPressed() {
        AppIronSource.getInstance().destroyBanner() // destroy banner nếu không còn sử dụng
        super.onBackPressed()
    }
}