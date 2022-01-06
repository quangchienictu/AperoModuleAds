
# AndModuleAds
Import Module
~~~
	maven { url 'https://jitpack.io' }

	implementation 'com.github.AperoVN:AperoModuleAds:2.4.1'
~~~	 
# Summary
* [Setup Admob](#example-admob)
	* [Setup id ads](#set_up_ads)
	* [Ads Formats](#ads_formats)
* Setup Iron Source
	* Coming soon
* Setup FAN
	* Coming soon
* [Billing App](#billing_app)
* [Ads rule](#ads_rule)

# <a id="example-admob"></a>Setup Admob
## <a id="set_up_ads"></a>Setup id ads
* Config 2 variant trong gradle test và release
* appTest: Sử dụng id admob test  trong quá trình dev,
* appRelease: Sử dụng id thật, dùng để build release (build file .aab)
~~~    
      productFlavors {
      test {
              manifestPlaceholders = [ ad_app_id:"AD_APP_ID_TEST" ]
              buildConfigField "String", "ads_inter_turn_on", "\"AD_ID_INTERSTIAL_TEST\""
              buildConfigField "String", "ads_inter_turn_off", "\"AD_ID_INTERSTIAL_TEST\""
           }
       release {
               manifestPlaceholders = [ ad_app_id:"AD_APP_ID" ]
               buildConfigField "String", "ads_inter_splash", "\"AD_ID_INTERSTIAL\""
               buildConfigField "String", "ads_inter_turn_on", "\"AD_ID_INTERSTIAL\""
           }
      }
~~~
AndroidManiafest.xml
~~~
  <meta-data
  android:name="com.google.android.gms.ads.APPLICATION_ID"
  android:value="@string/admob_app_id" />
~~~
## <a id="init_ads"></a>Init Ads
Create class Application
~~~
class App : AdsMultiDexApplication(){}
~~~
AndroidManiafest.xml
~~~
<application
android:name=".App"
................
>
~~~
Setup mediation
~~~
{code:java|title=App}
override fun onCreate() {
super.onCreate()
Admod.getInstance().setFan(false)
Admod.getInstance().setAppLovin(false)
Admod.getInstance().setColony(false)
}
~~~
## <a id="ads_formats"></a>Ads formats
### Ad Splash
SplashActivity
~~~ 
  var adCallback: AdCallback = object : AdCallback() {
  override fun onAdFailedToLoad(i: LoadAdError?) {
  startMain()
  }
  override fun onAdFailedToShow(adError: AdError?) {
  startMain()
  }
  override fun onAdClosed() {
  super.onAdClosed()
  startMain()
  }
  }
~~~
~~~
  Admod.getInstance()
  .loadSplashInterstitalAds(
  this,
  BuildConfig.ad_interstitial_splash,
  timeout,
  timeDelay,
  adCallback
  )
~~~
### Interstitial
Load ad interstital before show
~~~
  private fun loadInterCreate() {
  Admod.getInstance().getInterstitalAds(
  context,
  ID_AD_INTERSTITAL,
  object : AdCallback() {
  override fun onInterstitialLoad(interstitialAd: InterstitialAd) {
  this.createInterstitial =
  interstitialAd
  }
  })
  }
~~~
~~~
  Admod.getInstance()
  .forceShowInterstitial(
  context,
  App.getInstance().storageCommon!!.createInterstitial,
  object : AdCallback() {
  override fun onAdClosed() {
  startActivity(intent)
  //reloead ad interstitital
  loadInterCreate()
  }
  })
~~~
### Ad Banner
include layout banner
activity_main.xml
~~~
  <include
  android:id="@+id/include"
  layout="@layout/layout_banner_control"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:layout_alignParentBottom="true"
  app:layout_constraintBottom_toBottomOf="parent" />
~~~
load ad banner
~~~
  Admod.getInstance().loadBanner(this, ID_AD_BANNER)
~~~

### Ad Native
Load ad native before show
~~~
  Admod.getInstance()
  .loadNativeAd(context, ID_AD_NATIVE, object : AdCallback() {
  override fun onUnifiedNativeAdLoaded(unifiedNativeAd: NativeAd) {
  this.unifiedNativeAd = unifiedNativeAd
  }
  })
~~~
show ad native
~~~
  val adView = LayoutInflater.from(context)
  .inflate(R.layout.custom_native_home, null) as NativeAdView
  Admod.getInstance().populateUnifiedNativeAdView(unifiedNativeAd, adView)
~~~
auto load and show native contains loading
~~~
  {code:java|title=activity_main.xml|borderStyle=solid}
  <include layout="@layout/layout_native_control" />
~~~
~~~
  {code:java|title=MainActivity|borderStyle=solid}
  Admod.getInstance().loadNative(activity,ID_AD_NATIVE)
~~~

### Ad Reward
Init and show reward
~~~
  Admod.getInstance().initRewardAds(this,ID_AD_REWARD);

Admod.getInstance().showRewardAds(this, new RewardCallback() {
@Override
public void onUserEarnedReward(RewardItem var1) {

                }
                @Override
                public void onRewardedAdClosed() {
                }
                @Override
                public void onRewardedAdFailedToShow(int codeError) {

                }
            });
~~~
### Ad resume
App
~~~ 
  override fun onCreate() {
  super.onCreate()
  AppOpenManager.getInstance().enableAppResume()
  }
  override fun enableAdsResume(): Boolean = true
  override fun getOpenAppAdId(): String {
  return ID_AD_RESUME
  }
~~~

# <a id="billing_app"></a>Billing app
## Init Billing
Application
~~~
    @Override
    public void onCreate() {
        super.onCreate();
        AppPurchase.getInstance().initBilling(this,listINAPId,listSubsId);
    }
~~~
## Check status billing init
~~~
 if (AppPurchase.getInstance().getInitBillingFinish()){
            loadAdsPlash();
        }else {
            AppPurchase.getInstance().setBillingListener(new BillingListener() {
                @Override
                public void onInitBillingListener(int code) {
                         loadAdsPlash();
                }
            },5000);
        }
~~~
## Check purchase status
    //check purchase with PRODUCT_ID
	 AppPurchase.getInstance().isPurchased(this,PRODUCT_ID);
	 //check purchase all
	 AppPurchase.getInstance().isPurchased(this);
##  purchase
	 AppPurchase.getInstance().purchase(this,PRODUCT_ID);
	 AppPurchase.getInstance().subscribe(this,SUBS_ID);
## Purchase Listener
	         AppPurchase.getInstance().setPurchaseListioner(new PurchaseListioner() {
                 @Override
                 public void onProductPurchased(String productId,String transactionDetails) {

                 }

                 @Override
                 public void displayErrorMessage(String errorMsg) {

                 }
             });

## Consume purchase
	  AppPurchase.getInstance().consumePurchase(PRODUCT_ID);
## Get price
	  AppPurchase.getInstance().getPrice(PRODUCT_ID)
	  AppPurchase.getInstance().getPriceSub(SUBS_ID)
### Show iap dialog
	InAppDialog dialog = new InAppDialog(this);
	dialog.setCallback(() -> {
	     AppPurchase.getInstance().purchase(this,PRODUCT_ID);
	    dialog.dismiss();
	});
	dialog.show();



# <a id="ads_rule"></a>Ads rule
## Always add device test to idTestList with all of your team's device
To ignore invalid ads traffic
https://support.google.com/adsense/answer/16737?hl=en
## Before show full-screen ad (interstitial, app open ad), alway show a short loading dialog
To ignore accident click from user. This feature is existed in library
## Never reload ad on onAdFailedToLoad
To ignore infinite loop
