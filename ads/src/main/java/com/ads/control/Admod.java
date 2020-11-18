package com.ads.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.ads.control.funtion.AdCallback;
import com.ads.control.funtion.AdmodHelper;
import com.ads.control.widget.NativeTemplateStyle;
import com.ads.control.widget.TemplateView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class Admod {
    private static Admod instance;
    private static int currentClicked = 0;
    private String nativeId;
    private boolean isLoadAds = true;
    private int numShowAds = 3;

    private int maxClickAds = 3;

    public void setMaxClickAds(int maxClickAds) {
        this.maxClickAds = maxClickAds;
    }

    public static Admod getInstance() {
        if (instance == null) {
            instance = new Admod();
        }
        return instance;
    }

    private Admod() {

    }

    public void setNumToShowAds(int numShowAds) {
        this.numShowAds = numShowAds;
    }

    /**
     * khởi tạo admod
     *
     * @param context
     */
    public void init(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }


    private AdRequest getAdRequest() {
//        Bundle extras = new FacebookExtras()
//                .setNativeBanner(true)
//                .build();
        return new AdRequest.Builder()
//                .addNetworkExtrasBundle(FacebookAdapter.class, extras)
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("3C94990AA9A387A256D3B2BBBFEA51EA")
                .addTestDevice("6F599887BC401CFB1C7087F15D7C0834")
                .addTestDevice("B543DCF2C7591C7FB8B52A3A1E7138F6")
                .addTestDevice("8619926A823916A224795141B93B7E0B")
                .addTestDevice("6399D5AEE5C75205B6C0F6755365CF21")
                .addTestDevice("2E379568A9F147A64B0E0C9571DE812D")
                .addTestDevice("A0518C6FA4396B91F82B9656DE83AFC7")
                .addTestDevice("C8EEFFC32272E3F1018FC72ECBD46F0C")
                .addTestDevice("FEECD9793CCCE1E0FF8D392B0DB65559")
                .addTestDevice("72939BB87E5DF5C9D9B9CBBC1BCC607F")
                .addTestDevice("F2896A8563D5980884F86D46CDB83A7D")
                .addTestDevice("28FF4F316894AEAB2563A7F1E48071CF")
                .addTestDevice("BE2ACB822383D47366B73B13ADCA3A49")
                .addTestDevice("107C4A0D40F4C1AA61E49A5654876F59")
                .addTestDevice("3AFAB3D471440BF4CE5B77676B1EB89A")
                .addTestDevice("EDAD373DD1386523618352C812180436")
                .addTestDevice("7EA7F782CC0C164F7033F51403E4DD80")
                .addTestDevice("282C15C8E8D5E71264437133CCE91852")
                .addTestDevice("5865B7BE5322DF51F3CF6C1EBFB6E161")
                .addTestDevice("75F769D7FE2708AF5C74CBBA495F2BCF")
                .addTestDevice("FD248C9F2C2870BF180FE9721AAC8554")
                .addTestDevice("42B8D5D1D2E9208FD400BA395BBB2A76")
                .addTestDevice("3D2AFE68C63AB5B40C60FCEDE028E18D")
                .addTestDevice("E9F47CFACAA8BDFC9B2009D5D4BA84FB")
                .build();
    }

    private void requestInterstitialAds(InterstitialAd mInterstitialAd) {
        if (mInterstitialAd != null && !mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            mInterstitialAd.loadAd(getAdRequest());
        }
    }

    /**
     * Load quảng cáo Full tại màn SplashActivity
     * Sau khoảng thời gian timeout thì load ads và callback về cho View
     * @param context
     * @param id
     * @param timeOut
     * @param adListener
     */
    public void splashInterstitalAds(final Context context, String id, long timeOut, final AdCallback adListener) {
        if (Pucharse.getInstance(context).isPucharsed()) {
            if (adListener != null) {
                adListener.onAdClosed();
            }
            return;
        }
        final InterstitialAd mInterstitialAd = getInterstitalAds(context, id);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (adListener != null) {
                    isLoadAds = false;
                    adListener.onAdFailedToLoad(i);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLoadAds) {
                    return;
                }
                if (mInterstitialAd.isLoaded()) {
                    isLoadAds = true;
                    forceShowInterstitial(context, mInterstitialAd, adListener);
                    return;
                }
                if (adListener != null) {
                    isLoadAds = true;
                    adListener.onAdClosed();
                }
            }
        }, timeOut);
    }


    /**
     * Trả về 1 InterstitialAd và request Ads
     *
     * @param context
     * @param id
     * @return
     */
    public InterstitialAd getInterstitalAds(Context context, String id) {
        if (Pucharse.getInstance(context).isPucharsed()) {
            return null;
        }
        final InterstitialAd mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(id);
        requestInterstitialAds(mInterstitialAd);
        return mInterstitialAd;
    }


    /**
     * Hiển thị ads theo số lần được xác định trước và callback result
     * vd: click vào 3 lần thì show ads full.
     *
     * @param context
     * @param mInterstitialAd
     * @param callback
     */
    public void showInterstitialAdByTimes(final Context context, final InterstitialAd mInterstitialAd, final AdCallback callback) {
        AdmodHelper.configTime(context);
        if (Pucharse.getInstance(context).isPucharsed()) {
            callback.onAdClosed();
            return;
        }
        if (mInterstitialAd == null) {
            if (callback != null) {
                callback.onAdClosed();
            }
            return;
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (callback != null) {
                    callback.onAdClosed();
                    requestInterstitialAds(mInterstitialAd);
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                AdmodHelper.increaseNumClickAdsPerDay(context, mInterstitialAd.getAdUnitId());
            }
        });

        if (AdmodHelper.getNumClickAdsPerDay(context, mInterstitialAd.getAdUnitId()) < maxClickAds) {
            showInterstitialAd(mInterstitialAd, callback);
            return;
        }

        if (callback != null) {
            callback.onAdClosed();
        }
    }

    /**
     * Bắt buộc hiển thị  ads full và callback result
     *
     * @param context
     * @param mInterstitialAd
     * @param callback
     */
    public void forceShowInterstitial(Context context, final InterstitialAd mInterstitialAd, final AdCallback callback) {
        currentClicked = numShowAds;
        showInterstitialAdByTimes(context, mInterstitialAd, callback);
    }

    /**
     * Kiểm tra và hiện thị ads
     *
     * @param mInterstitialAd
     * @param callback
     */
    private void showInterstitialAd(InterstitialAd mInterstitialAd, AdCallback callback) {
        currentClicked++;
        if (currentClicked >= numShowAds && mInterstitialAd.isLoaded()) {
            if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                mInterstitialAd.show();
            }
            currentClicked = 0;
        } else if (callback != null) {
            callback.onAdClosed();
        }
    }

    /**
     * Load quảng cáo Banner Trong Activity
     *
     * @param mActivity
     * @param id
     */
    public void loadBanner(final Activity mActivity, String id) {
        final LinearLayout adContainer = mActivity.findViewById(R.id.banner_container);
        adContainer.removeAllViews();
        final ShimmerFrameLayout containerShimmer =
                mActivity.findViewById(R.id.shimmer_container);
        loadBanner(mActivity, id, adContainer, containerShimmer);
    }

    /**
     * Load Quảng Cáo Banner Trong Fragment
     *
     * @param mActivity
     * @param id
     * @param rootView
     */
    public void loadBannerFragment(final Activity mActivity, String id, final View rootView) {
        final ShimmerFrameLayout containerShimmer =
                rootView.findViewById(R.id.shimmer_container);
        containerShimmer.setVisibility(View.VISIBLE);
        containerShimmer.startShimmer();
        final LinearLayout adContainer = (LinearLayout) rootView.findViewById(R.id.banner_container);
        loadBanner(mActivity, id, adContainer, containerShimmer);
    }

    private void loadBanner(final Activity mActivity, String id, final LinearLayout adContainer, final ShimmerFrameLayout containerShimmer) {
        if (Pucharse.getInstance(mActivity).isPucharsed()) {
            containerShimmer.setVisibility(View.GONE);
            return;
        }
        containerShimmer.setVisibility(View.VISIBLE);
        containerShimmer.startShimmer();
        try {
            AdView adView = new AdView(mActivity);
            adView.setAdUnitId(id);
            adContainer.addView(adView);
            AdSize adSize = getAdSize(mActivity);
            adView.setAdSize(adSize);
            adView.loadAd(getAdRequest());
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    adContainer.setVisibility(View.GONE);
                    containerShimmer.stopShimmer();
                    containerShimmer.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    containerShimmer.stopShimmer();
                    containerShimmer.setVisibility(View.GONE);
                    adContainer.setVisibility(View.VISIBLE);
                }
            });


        } catch (Exception ignored) {
        }
    }

    private AdSize getAdSize(Activity mActivity) {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth);

    }


    /**
     * load quảng cáo big native
     *
     * @param mActivity
     * @param id
     */
    public void loadNative(final Activity mActivity, String id) {
        final FrameLayout frameLayout =
                mActivity.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer =
                mActivity.findViewById(R.id.shimmer_container);
        loadNative(mActivity, containerShimmer, frameLayout, id);
    }

    /**
     * load quảng cáo small native
     *
     * @param context
     * @param view
     * @param adUnitId
     */
    public void loadSmallNative(final Context context, final ViewGroup view, String adUnitId) {
        if (Pucharse.getInstance(context).isPucharsed()) {
            ViewGroup.LayoutParams param = view.getLayoutParams();
            param.width = 0;
            param.height = 0;
            view.requestLayout();
            return;
        }
        AdLoader adLoader = new AdLoader.Builder(context, adUnitId)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(new ColorDrawable()).build();
                        TemplateView template = view.findViewById(R.id.ads_small);
                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int i) {
                        ViewGroup.LayoutParams param = view.getLayoutParams();
                        param.width = 0;
                        param.height = 0;
                        view.requestLayout();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();
        adLoader.loadAd(getAdRequest());
    }

    /**
     * load quảng cáo native trong fragment
     *
     * @param mActivity
     * @param id
     * @param rootView
     */
    public void loadNativeFragment(final Activity mActivity, String id, final View rootView) {
        final ShimmerFrameLayout containerShimmer =
                rootView.findViewById(R.id.shimmer_container);
        final FrameLayout frameLayout =
                rootView.findViewById(R.id.fl_adplaceholder);
        loadNative(mActivity, containerShimmer, frameLayout, id);
    }

    private void loadNative(final Activity mActivity, final ShimmerFrameLayout containerShimmer, final FrameLayout frameLayout, final String id) {
        if (Pucharse.getInstance(mActivity).isPucharsed()) {
            containerShimmer.setVisibility(View.GONE);
            return;
        }
        frameLayout.removeAllViews();
        containerShimmer.removeAllViews();
        containerShimmer.setVisibility(View.VISIBLE);
        containerShimmer.startShimmer();

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();


        AdLoader adLoader = new AdLoader.Builder(mActivity, id)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        containerShimmer.stopShimmer();
                        containerShimmer.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.VISIBLE);
                        @SuppressLint("InflateParams") UnifiedNativeAdView adView = (UnifiedNativeAdView) mActivity.getLayoutInflater()
                                .inflate(R.layout.native_admob_ad, null);
                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);

                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        containerShimmer.stopShimmer();
                        containerShimmer.setVisibility(View.GONE);
                    }
                })
                .withNativeAdOptions(adOptions)
                .build();

        adLoader.loadAd(getAdRequest());
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {


        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));

        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

    }

    private RewardedAd rewardedAd;

    /**
     * Khởi tạo quảng cáo reward
     *
     * @param context
     * @param id
     */
    public void initVideoAds(Context context, String id) {
        if (Pucharse.getInstance(context).isPucharsed()) {
            return;
        }
        this.nativeId = id;
        if (Pucharse.getInstance(context).isPucharsed()) {
            return;
        }
        rewardedAd = new RewardedAd(context, id);
        rewardedAd.loadAd(getAdRequest(), new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                rewardedAd = null;
            }
        });

    }

    /**
     * Show quảng cáo reward và nhận kết quả trả về
     *
     * @param context
     * @param adCallback
     */
    public void loadVideoAds(final Activity context, final RewardedAdCallback adCallback) {
        if (Pucharse.getInstance(context).isPucharsed()) {
            adCallback.onUserEarnedReward(null);
            adCallback.onRewardedAdClosed();
            return;
        }
        if (rewardedAd == null) {
            initVideoAds(context, nativeId);
            adCallback.onRewardedAdFailedToShow(0);
            return;
        }
        if (rewardedAd.isLoaded()) {
            rewardedAd.show(context, new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    if (adCallback != null) {
                        adCallback.onUserEarnedReward(rewardItem);
                    }
                }

                @Override
                public void onRewardedAdClosed() {
                    if (adCallback != null) {
                        adCallback.onRewardedAdClosed();
                    }
                    initVideoAds(context, nativeId);
                }

                @Override
                public void onRewardedAdFailedToShow(int i) {
                    if (adCallback != null) {
                        adCallback.onRewardedAdFailedToShow(i);
                    }
                }
            });
        } else {
            adCallback.onRewardedAdFailedToShow(0);
        }
    }


    private AppOpenAd appOpenAd = null;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;

    /**
     * Hiển thị quảng cáo App Open
     *
     * @param activity
     * @param id
     */
    public void showAppOpenAds(final Activity activity, final String id) {
        if (isAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            appOpenAd = null;
                            initAppOpenAds(activity, id, null);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                        }
                    };
            appOpenAd.show(activity, fullScreenContentCallback);
        }
    }

    /**
     * Khởi tạo quảng cáo App Open
     *
     * @param activity
     * @param id
     * @param callback
     */
    public void initAppOpenAds(Activity activity, String id, final AdCallback callback) {
        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAppOpenAdLoaded(AppOpenAd ad) {
                appOpenAd = ad;
                if (callback != null) {
                    callback.onAdLoaded();
                }
            }

            @Override
            public void onAppOpenAdFailedToLoad(LoadAdError loadAdError) {
            }

        };
        AppOpenAd.load(
                activity, id, getAdRequest(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    public boolean isAdAvailable() {
        return appOpenAd != null;
    }

}
