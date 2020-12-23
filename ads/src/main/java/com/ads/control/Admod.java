package com.ads.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.ads.control.dialog.PrepareLoadingAdsDialog;
import com.ads.control.funtion.AdCallback;
import com.ads.control.funtion.AdmodHelper;
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
import com.google.android.gms.ads.RequestConfiguration;
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

import java.util.List;

public class Admod {
    private static Admod instance;
    private static int currentClicked = 0;
    private String nativeId;
    private int numShowAds = 3;

    private int maxClickAds = 100;
    private Handler handler;
    private Runnable rd;
    private PrepareLoadingAdsDialog dialog;
    private boolean isTimeLimited;

    /**
     * Giới hạn số lần click trên 1 admod tren 1 ngay
     *
     * @param maxClickAds
     */
    public void setMaxClickAdsPerDay(int maxClickAds) {
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
    public void init(Context context, List<String> testDeviceList) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(testDeviceList).build());
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
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
     *
     * @param context
     * @param id
     * @param timeOut    : thời gian chờ ads, timeout <= 0 tương đương với việc bỏ timeout
     * @param adListener
     */
    public void loadSplashInterstitalAds(final Context context, String id, long timeOut, final AdCallback adListener) {
        if (Purchase.getInstance().isPurcharsed(context)) {
            if (adListener != null) {
                adListener.onAdClosed();
            }
            return;
        }
        final InterstitialAd mInterstitialAd = getInterstitalAds(context, id);
        if (mInterstitialAd == null) {
            if (adListener != null) {
                adListener.onAdFailedToLoad(0);
            }
            return;
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (adListener != null) {
                    if (handler != null && rd != null) {
                        handler.removeCallbacks(rd);
                    }
                    adListener.onAdFailedToLoad(i);
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (adListener != null) {
                    if (handler != null && rd != null) {
                        handler.removeCallbacks(rd);
                    }
                    adListener.onAdFailedToLoad(loadAdError);
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (handler != null && rd != null) {
                    handler.removeCallbacks(rd);
                }
                if (isTimeLimited) {
                    return;
                }
                forceShowInterstitial(context, mInterstitialAd, adListener, false);
            }


        });

        if (timeOut > 0) {
            handler = new Handler();
            rd = new Runnable() {
                @Override
                public void run() {
                    isTimeLimited = true;
                    if (mInterstitialAd.isLoaded()) {
                        forceShowInterstitial(context, mInterstitialAd, adListener, false);
                        return;
                    }
                    if (adListener != null) {
                        mInterstitialAd.setAdListener(null);
                        adListener.onAdClosed();
                    }
                }
            };
            handler.postDelayed(rd, timeOut);
        }
    }

    /**
     * Trả về 1 InterstitialAd và request Ads
     *
     * @param context
     * @param id
     * @return
     */
    public InterstitialAd getInterstitalAds(Context context, String id) {
        if (Purchase.getInstance().isPurcharsed(context) || AdmodHelper.getNumClickAdsPerDay(context, id) >= maxClickAds) {
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
     * AdmodHelper.setupAdmodData(context) -> kiểm tra xem app đc hoạt động đc 1 ngày chưa nếu YES thì reset lại số lần click vào ads
     *
     * @param context
     * @param mInterstitialAd
     * @param callback
     */
    public void showInterstitialAdByTimes(final Context context, final InterstitialAd mInterstitialAd, final AdCallback callback) {
        showInterstitialAdByTimes(context, mInterstitialAd, callback, true);
    }

    /**
     * Hiển thị ads theo số lần được xác định trước và callback result
     * vd: click vào 3 lần thì show ads full.
     * AdmodHelper.setupAdmodData(context) -> kiểm tra xem app đc hoạt động đc 1 ngày chưa nếu YES thì reset lại số lần click vào ads
     *
     * @param context
     * @param mInterstitialAd
     * @param callback
     * @param shouldReloadAds
     */
    public void showInterstitialAdByTimes(final Context context, final InterstitialAd mInterstitialAd, final AdCallback callback, final boolean shouldReloadAds) {
        AdmodHelper.setupAdmodData(context);
        if (Purchase.getInstance().isPurcharsed(context)) {
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
                    if (shouldReloadAds) {
                        requestInterstitialAds(mInterstitialAd);
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (callback != null) {
                    callback.onAdClicked();
                }
                AdmodHelper.increaseNumClickAdsPerDay(context, mInterstitialAd.getAdUnitId());
            }
        });

        if (AdmodHelper.getNumClickAdsPerDay(context, mInterstitialAd.getAdUnitId()) < maxClickAds) {
            showInterstitialAd(context, mInterstitialAd, callback);
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
        forceShowInterstitial(context, mInterstitialAd, callback, true);
    }

    /**
     * Bắt buộc hiển thị  ads full và callback result
     *
     * @param context
     * @param mInterstitialAd
     * @param callback
     */
    public void forceShowInterstitial(Context context, final InterstitialAd mInterstitialAd, final AdCallback callback, boolean shouldReload) {
        currentClicked = numShowAds;
        showInterstitialAdByTimes(context, mInterstitialAd, callback, shouldReload);
    }

    /**
     * Kiểm tra và hiện thị ads
     *
     * @param context
     * @param mInterstitialAd
     * @param callback
     */
    private void showInterstitialAd(Context context, final InterstitialAd mInterstitialAd, AdCallback callback) {
        currentClicked++;
        if (currentClicked >= numShowAds && mInterstitialAd.isLoaded()) {
            if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                try {
                    dialog = new PrepareLoadingAdsDialog(context);
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        callback.onAdClosed();
                        return;
                    }
                } catch (Exception e) {
                    dialog = null;
                    e.printStackTrace();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mInterstitialAd.show();
                    }
                }, 800);

            }
            currentClicked = 0;
        } else if (callback != null) {
            if (dialog != null) {
                dialog.dismiss();
            }
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
        final FrameLayout adContainer = mActivity.findViewById(R.id.banner_container);
        final ShimmerFrameLayout containerShimmer = mActivity.findViewById(R.id.shimmer_container_banner);
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
        final FrameLayout adContainer = rootView.findViewById(R.id.shimmer_container_banner);
        final ShimmerFrameLayout containerShimmer = rootView.findViewById(R.id.shimmer_container_native);
        loadBanner(mActivity, id, adContainer, containerShimmer);
    }

    private void loadBanner(final Activity mActivity, String id, final FrameLayout adContainer, final ShimmerFrameLayout containerShimmer) {
        if (Purchase.getInstance().isPurcharsed(mActivity)) {
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
                    containerShimmer.stopShimmer();
                    adContainer.setVisibility(View.GONE);
                    containerShimmer.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    containerShimmer.stopShimmer();
                    containerShimmer.setVisibility(View.GONE);
                    adContainer.setVisibility(View.VISIBLE);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
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
        final FrameLayout frameLayout = mActivity.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer = mActivity.findViewById(R.id.shimmer_container_native);
        loadNative(mActivity, containerShimmer, frameLayout, id);
    }


    public void loadSmallNative(final Activity mActivity, String adUnitId) {
        final FrameLayout frameLayout = mActivity.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer = mActivity.findViewById(R.id.shimmer_container_small_native);
        loadNative(mActivity, containerShimmer, frameLayout, adUnitId);
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
                rootView.findViewById(R.id.shimmer_container_native);
        final FrameLayout frameLayout =
                rootView.findViewById(R.id.fl_adplaceholder);
        loadNative(mActivity, containerShimmer, frameLayout, id);
    }

    private void loadNative(final Activity mActivity, final ShimmerFrameLayout containerShimmer, final FrameLayout frameLayout, final String id) {
        if (Purchase.getInstance().isPurcharsed(mActivity)) {
            containerShimmer.setVisibility(View.GONE);
            return;
        }
        frameLayout.removeAllViews();
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
        if (Purchase.getInstance().isPurcharsed(context)) {
            return;
        }
        this.nativeId = id;
        if (Purchase.getInstance().isPurcharsed(context)) {
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

    public RewardedAd getRewardedAd() {
        return rewardedAd;
    }

    /**
     * Show quảng cáo reward và nhận kết quả trả về
     *
     * @param context
     * @param adCallback
     */

    public void loadVideoAds(final Activity context, final RewardedAdCallback adCallback) {
        if (Purchase.getInstance().isPurcharsed(context)) {
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
                    rewardedAd = null;
                }
            });
        } else {
            adCallback.onRewardedAdFailedToShow(0);
        }
    }


    private AppOpenAd appOpenAd = null;

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
        AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
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
