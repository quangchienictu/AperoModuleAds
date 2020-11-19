# AndMuduleAds

maven { url 'https://jitpack.io' }


implementation 'com.github.eGame-Global:AndMuduleAds:1.0.0'

// init admod
    Admod.getInstance().init(this, idTestList);
    Pucharse.getInstance(this).initBilling(PRODUCT_ID);
  
  
  
// loading splash:
     Admod.getInstance()
     .loadSplashInterstitalAds(this, 
     getString(R.string.ads_intersitial_splash_v2),
     4000,
     new AdCallback() {
            @Override
            public void onAdClosed() {
                startMain();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                startMain();
            }
        });
        
        
// init a interstialAds
   InterstitalAds mInterstitalAds=Admod.getInstance().getInterstitalAds(getContext(), getString(R.string.ads_intersitial_file_v2));
       
        
// force Show Ads
   Admod.getInstance().forceShowInterstitial(getContext(), getStorageCommon().getmInterstitialAdFile(), new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        //TODO
                    }
                });
                
//set limit  to show a Ads 
    Admod.getInstance().setNumToShowAds(int));
    
// show ads follow time
     Admod.getInstance().showInterstitialAdByTimes(getContext(), getStorageCommon().getmInterstitialAdFile(), new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        //TODO
                    }
                });
    
