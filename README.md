# AndModuleAds

	maven { url 'https://jitpack.io' }


	implementation 'com.github.eGame-Global:AndMuduleAds:1.1.18'
## IntertitialAds
### init ads module
    Admod.getInstance().init(this, idTestList);
    
  
### Create, load and show splash InterstitialAds:
     Admod.getInstance().loadSplashInterstitalAds(this,
		getString(R.string.ads_intersitial_splash_v2),
		timeoutInMilliseconds,
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
        
	  If timeoutInMilliseconds <=0 : Ignore timeout
	  
### Create and load normal InterstitialAds	  
		InterstitialAd mInterstitialAd = Admod.getInstance().getInterstitalAds(this, getString(adsId));
		
### Force Show InterstitialAds
	   Admod.getInstance().forceShowInterstitial(getContext(),
		   interstitial, 
		   new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        //TODO
                    }
           }
       );
## Banner
XML:

		<include
			layout="@layout/layout_banner_control"  
			android:layout_width="match_parent"  
			android:layout_height="wrap_content"  
			android:layout_alignParentBottom="true" />

### Banner in Activity
	Admod.getInstance().loadBanner(context, bannerId);
### Banner in Fragment
	Admod.getInstance().loadBannerFragment( mActivity, bannerID,  rootView)

## Purchasing
### Init purchase
	Purchase.getInstance().initBilling(context, PRODUCT_ID);
### Check purchase status
	Purchase.getInstance().isPurchased(context)
### Show iap dialog
	InAppDialog dialog = new InAppDialog(this);
	dialog.setCallback(() -> {  
	    Purchase.getInstance().purchase(this); 
	    dialog.dismiss();  
	});  
	dialog.show();
