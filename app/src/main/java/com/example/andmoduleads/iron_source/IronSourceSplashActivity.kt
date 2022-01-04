package com.example.andmoduleads.iron_source

import android.content.Intent
import com.ads.control.funtion.AdCallback
import android.os.Bundle
import android.util.Log
import com.ads.control.ads.AppIronSource
import com.example.andmoduleads.R
import com.google.android.gms.ads.LoadAdError

class IronSourceSplashActivity : BaseActivity() {
    var TAG = "IronSourceSplashActivity"
    private val adListener: AdCallback = object : AdCallback() {
        override fun onAdFailedToLoad(error: LoadAdError?) {
            Log.e(TAG, "onAdFailedToLoad: ")
            startMain()
        }

        override fun onAdLoaded() {
            super.onAdLoaded()
            Log.d(TAG, "onAdLoaded")
        }

        override fun onAdClosed() {
            super.onAdClosed()
            Log.d(TAG, "onAdClosed")
            startMain()
        }

        override fun onAdClicked() {
            super.onAdClicked()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iron_source_splash)
        AppIronSource.getInstance().init(this, IRON_SOURCE_APP_KEY, true)
        loadAndShowInterAds()
    }

    private fun loadAndShowInterAds() {
        Log.e(TAG, "start loadAndShowInterAds")
        AppIronSource.getInstance().loadSplashInterstitial(this, adListener, 30000)
    }

    private fun startMain() {
        startActivity(Intent(this, TestISActivity::class.java))
        finish()
    }

    companion object {
        @JvmField
        var IRON_SOURCE_APP_KEY = "85460dcd"
    }
}