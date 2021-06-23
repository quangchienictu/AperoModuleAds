
# AndModuleAds

	maven { url 'https://jitpack.io' }


	 implementation 'com.github.AperoVN:AperoModuleAds:1.6.0'
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

# App open and resume ad
Coming soon

## Purchasing
### Init purchase
	AppPurchase.getInstance().initBilling(this,listINAPId,listSubsId);
### Check purchase status
	 AppPurchase.getInstance().isPurchased(this,PRODUCT_ID);
###  purchase
	 AppPurchase.getInstance().purchase(this,PRODUCT_ID);
	 AppPurchase.getInstance().subscribe(this,SUBS_ID);
### Purchase Listioner
	         AppPurchase.getInstance().setPurchaseListioner(new PurchaseListioner() {
                 @Override
                 public void onProductPurchased(String productId,String transactionDetails) {

                 }

                 @Override
                 public void displayErrorMessage(String errorMsg) {

                 }
             });

### Consume purchase
	  AppPurchase.getInstance().consumePurchase(PRODUCT_ID);
### Get price
	  AppPurchase.getInstance().getPrice(PRODUCT_ID)
	  AppPurchase.getInstance().getPriceSub(SUBS_ID)
### Show iap dialog
	InAppDialog dialog = new InAppDialog(this);
	dialog.setCallback(() -> {  
	     AppPurchase.getInstance().purchase(this,PRODUCT_ID);
	    dialog.dismiss();  
	});  
	dialog.show();


# Ads rule
## Always add device test to idTestList with all of your team's device
To ignore invalid ads traffic
https://support.google.com/adsense/answer/16737?hl=en
## Before show full-screen ad (interstitial, app open ad), alway show a short loading dialog
To ignore accident click from user. This feature is existed in library
## Never reload ad on onAdFailedToLoad
To ignore infinite loop
