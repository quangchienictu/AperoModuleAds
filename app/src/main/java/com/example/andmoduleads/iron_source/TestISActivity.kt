package com.example.andmoduleads.iron_source

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.ads.control.ads.AppIronSource
import android.widget.Toast
import com.ads.control.funtion.AdCallback
import com.example.andmoduleads.R

class TestISActivity : BaseActivity() {
    var btnLoadAds: Button? = null
    var btForceShowAds: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_isactivity)

//        AppIronSource.getInstance().initBanner(this,IRON_SOURCE_APP_KEY,true);
        btnLoadAds = findViewById(R.id.btnLoadAds)
        btForceShowAds = findViewById(R.id.btForceShowAds)


//        btForceShowAds.setEnabled(false);
        btnLoadAds!!.setOnClickListener(View.OnClickListener { v: View? ->
            if (AppIronSource.getInstance().isInterstitialReady) {
                Toast.makeText(this, "Ad is loaded", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ad  loading", Toast.LENGTH_SHORT).show()
                AppIronSource.getInstance().loadInterstitial(this, object : AdCallback() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Toast.makeText(this@TestISActivity, "Ad  loaded", Toast.LENGTH_SHORT).show()
                        btForceShowAds!!.setEnabled(true)
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        AppIronSource.getInstance()
                            .destroyBanner() // destroy banner nếu xử dụng banner trong 1 activity khác
                        startActivity(Intent(this@TestISActivity, Test2Activity::class.java))
                    }
                })
            }
        })
        btForceShowAds!!.setOnClickListener(View.OnClickListener { v: View? ->
            if (AppIronSource.getInstance().isInterstitialReady) AppIronSource.getInstance()
                .showInterstitial() else Toast.makeText(this, "Ad not loaded", Toast.LENGTH_SHORT)
                .show()
        })
    }

    override fun onStart() {
        super.onStart()
        AppIronSource.getInstance().loadBanner(this)
    }

    override fun onStop() {
        super.onStop()
    }
}