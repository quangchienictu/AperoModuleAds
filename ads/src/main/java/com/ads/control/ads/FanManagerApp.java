package com.ads.control.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.ads.control.R;
import com.ads.control.billing.AppPurchase;
import com.ads.control.dialog.PrepareLoadingAdsDialog;
import com.ads.control.funtion.AdmodHelper;
import com.ads.control.funtion.FanCallback;
import com.ads.control.util.AdjustApero;
import com.ads.control.util.FirebaseAnalyticsUtil;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FanManagerApp {
    private static final String TAG = "FanManagerApp";
    private static FanManagerApp instance;
    private static int currentClicked = 0;
    private String nativeId;
    private int numShowAds = 3;

    private int maxClickAds = 100;
    private Handler handler;
    private Runnable rd;
    private PrepareLoadingAdsDialog dialog;
    private boolean isTimeLimited;
    private boolean openActivityAfterShowInterAds = false;
    private Context context;
    private AdView adView;
    InterstitialAd interstitialSplash;

    public FanManagerApp() {
    }

    public static FanManagerApp getInstance() {
        if (instance == null) {
            instance = new FanManagerApp();
        }

        return instance;
    }

    /**
     * khởi tạo FAN
     *
     * @param context
     */
    public void init(Context context, List<String> testDeviceList) {

        AdSettings.addTestDevices(testDeviceList);
        this.context = context;
        AudienceNetworkAds.initialize(context);
    }

    public void init(Context context, List<String> testDeviceList,Boolean isDebug) {
        if (isDebug){
            Log.i(TAG, "init: enable debug");
            AdSettings.turnOnSDKDebugger(context);
        }
        AdSettings.addTestDevices(testDeviceList);
        this.context = context;
        AudienceNetworkAds.initialize(context);
    }

    public void init(Context context) {
        this.context = context;
        AudienceNetworkAds.initialize(context);
    }

    public void loadSplashInterstitialAds(final Context context, String id, long timeOut, long timeDelay, final FanCallback adListener) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            if (adListener != null) {
                adListener.onAdClosed();
            }
            return;
        }
        if (interstitialSplash != null) {
            interstitialSplash.destroy();
            interstitialSplash = null;
        }
        interstitialSplash = getInterstitialAds(context, id);
        if (interstitialSplash == null) {
            if (adListener != null) {
                adListener.onAdFailedToLoad(AdError.INTERNAL_ERROR);
            }
            return;
        }
        InterstitialAdListener listener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (adListener != null) {
                    adListener.onAdClosed();
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (adListener != null) {
                    if (handler != null && rd != null) {
                        handler.removeCallbacks(rd);
                    }
                    adListener.onAdFailedToLoad(adError);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (handler != null && rd != null) {
                    handler.removeCallbacks(rd);
                }

                if (adListener != null) {
                    adListener.onAdLoaded();
                }
                if (isTimeLimited) {
                    return;
                }
                forceShowInterstitial(context, interstitialSplash, adListener, false);
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (adListener != null) {
                    adListener.onAdClicked();
                    Log.d(TAG, "onAdClicked");
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                if (adListener != null) {
                    adListener.onAdImpression();
                }
            }
        };
        interstitialSplash.buildLoadAdConfig().withAdListener(listener);

        if (timeOut > 0) {
            handler = new Handler();
            rd = () -> {
                isTimeLimited = true;
                if (interstitialSplash.isAdLoaded()) {
                    forceShowInterstitial(context, interstitialSplash, adListener, false);
                    return;
                }
                if (adListener != null) {
                    interstitialSplash.buildLoadAdConfig().withAdListener(null);
                    adListener.onAdClosed();
                }
            };
            handler.postDelayed(rd, timeOut);
        }
    }

    public InterstitialAd getInterstitialAds(Context context, String id) {
        if (AppPurchase.getInstance().isPurchased(context) || AdmodHelper.getNumClickAdsPerDay(context, id) >= maxClickAds) {
            return null;
        }
        final InterstitialAd interstitialAd = new InterstitialAd(context, id);
        requestInterstitialAds(interstitialAd);
        return interstitialAd;
    }



    public InterstitialAd getInterstitialAds(Context context, String id, FanCallback callback) {
        if (AppPurchase.getInstance().isPurchased(context) || AdmodHelper.getNumClickAdsPerDay(context, id) >= maxClickAds) {
            return null;
        }
        final InterstitialAd interstitialAd = new InterstitialAd(context, id);
        interstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (callback != null) {
                    callback.onAdFailedToLoad(adError);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (callback != null) {
                    callback.onAdLoaded();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        if (!interstitialAd.isAdLoaded()) {
            requestInterstitialAds(interstitialAd);
        } else {
            callback.onAdFailedToLoad(AdError.INTERNAL_ERROR);
        }
        return interstitialAd;
    }

    private void requestInterstitialAds(InterstitialAd mInterstitialAd) {
        if (mInterstitialAd != null && !mInterstitialAd.isAdLoaded()) {
            mInterstitialAd.loadAd();
        }
    }

    /**
     * Bắt buộc hiển thị  ads full và callback result
     *
     * @param context
     * @param mInterstitialAd
     * @param callback
     */
    public void forceShowInterstitial(Context context, final InterstitialAd mInterstitialAd, final FanCallback callback, boolean shouldReload) {
        currentClicked = numShowAds;
        showInterstitialAdByTimes(context, mInterstitialAd, callback, shouldReload);
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
    public void showInterstitialAdByTimes(final Context context, final InterstitialAd mInterstitialAd, final FanCallback callback, final boolean shouldReloadAds) {
        AdmodHelper.setupAdmodData(context);
        if (AppPurchase.getInstance().isPurchased(context)) {
            callback.onAdClosed();
            return;
        }
        if (mInterstitialAd == null) {
            if (callback != null) {
                callback.onAdClosed();
            }
            return;
        }
        mInterstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (callback != null) {
                    if (!openActivityAfterShowInterAds) {
                        callback.onAdClosed();
                    }
                    if (shouldReloadAds) {
                        requestInterstitialAds(mInterstitialAd);
                    }
                    if (dialog != null&&!((Activity) context).isDestroyed()) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {
                if (callback != null) {
                    callback.onAdClicked();
                    Log.d(TAG, "onAdClicked");
                }
                AdmodHelper.increaseNumClickAdsPerDay(context, mInterstitialAd.getPlacementId());
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        if (AdmodHelper.getNumClickAdsPerDay(context, mInterstitialAd.getPlacementId()) < maxClickAds) {
            showInterstitialAd(context, mInterstitialAd, callback);
            return;
        }
        if (callback != null) {
            callback.onAdClosed();
        }
    }

    /**
     * Kiểm tra và hiện thị ads
     *
     * @param context
     * @param mInterstitialAd
     * @param callback
     */
    private void showInterstitialAd(Context context, final InterstitialAd mInterstitialAd, FanCallback callback) {
        currentClicked++;
        if (currentClicked >= numShowAds && mInterstitialAd.isAdLoaded()) {
            if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                try {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
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
                new Handler().postDelayed(() -> {
                    if (AppOpenManager.getInstance().isInitialized()) {
                        AppOpenManager.getInstance().disableAppResume();
                    }

                    if (openActivityAfterShowInterAds && callback != null) {
                        callback.onAdClosed();
                    }
                    mInterstitialAd.show();
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

    public void loadNative(final Activity mActivity, String id) {
        final FrameLayout frameLayout = mActivity.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer = mActivity.findViewById(R.id.shimmer_container_native);
        loadNative(mActivity, containerShimmer, frameLayout, id, R.layout.fb_native_ad);
    }

    public void loadNativeAd(final Context context, String id, final FanCallback callback) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            callback.onAdClosed();
        } else {
            NativeAd nativeAd = new NativeAd(context, id);
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e(TAG, "Native ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Native ad failed to load
                    Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                    if (callback != null) {
                        callback.onAdFailedToLoad(adError);
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Native ad is loaded and ready to be displayed
                    Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                    if (callback != null) {
                        callback.onNativeAdLoaded(nativeAd);
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d(TAG, "Native ad clicked!");
                    if (callback != null) {
                        callback.onAdClicked();
                    }
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d(TAG, "Native ad impression logged!");
                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        }
    }

    private void loadNative(final Context context, final ShimmerFrameLayout containerShimmer, final FrameLayout frameLayout, final String id, final int layout) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            containerShimmer.setVisibility(View.GONE);
            return;
        }
        frameLayout.removeAllViews();
        frameLayout.setVisibility(View.GONE);
        containerShimmer.setVisibility(View.VISIBLE);
        containerShimmer.startShimmer();

        NativeAd nativeAd = new NativeAd(context, id);
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                containerShimmer.stopShimmer();
                containerShimmer.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                containerShimmer.stopShimmer();
                containerShimmer.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                NativeAdLayout adView = (NativeAdLayout) LayoutInflater.from(context)
                        .inflate(layout, null);
                // Race condition, load() called again before last ad was displayed
                if (nativeAd != ad) {
                    return;
                }
                populateUnifiedNativeAdView(nativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        };

        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
    }

    public void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdLayout adView) {
        nativeAd.unregisterView();

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, adView);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    public void getNativeBannerAds(Context context, String id, final FanCallback callback) {
        if (AppPurchase.getInstance().isPurchased(context))
            return;

        NativeBannerAd nativeBannerAd = new NativeBannerAd(context, id);
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "NativeBanner ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "NativeBanner ad failed to load: " + adError.getErrorMessage());
                if (callback != null) {
                    callback.onAdFailedToLoad(adError);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "NativeBanner ad is loaded and ready to be displayed!");
                if (callback != null) {
                    callback.onNativeBannerAdLoaded(nativeBannerAd);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "NativeBanner ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "NativeBanner ad impression logged!");
            }
        };

        nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
    }

    public void loadNativeBannerAds(Activity activity, String id) {
        final FrameLayout frameLayout = activity.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer = activity.findViewById(R.id.shimmer_container_native);
        loadNativeBannerAds(activity, containerShimmer, frameLayout, id, R.layout.fb_native_ad_small);
    }

    private void loadNativeBannerAds(final Context context, final ShimmerFrameLayout containerShimmer, final FrameLayout frameLayout, final String id, final int layout) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            containerShimmer.setVisibility(View.GONE);
            return;
        }
        frameLayout.removeAllViews();
        frameLayout.setVisibility(View.GONE);
        containerShimmer.setVisibility(View.VISIBLE);
        containerShimmer.startShimmer();

        NativeBannerAd nativeBannerAd = new NativeBannerAd(context, id);
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                containerShimmer.stopShimmer();
                containerShimmer.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                containerShimmer.stopShimmer();
                containerShimmer.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                NativeAdLayout adView = (NativeAdLayout) LayoutInflater.from(context)
                        .inflate(layout, null);
                // Race condition, load() called again before last ad was displayed
                if (nativeBannerAd != ad) {
                    return;
                }

                populateNativeBannerAdView(nativeBannerAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        };

        nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
    }

    public void populateNativeBannerAdView(NativeBannerAd nativeBannerAd, NativeAdLayout adView) {
        if (nativeBannerAd == null || adView == null)
            return;

        // Unregister last ad
        nativeBannerAd.unregisterView();

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeBannerAd, adView);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        MediaView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);

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
     * Load quảng cáo Banner Trong Activity
     *  Size mặc đinh BANNER_HEIGHT_50
     * @param mActivity
     * @param id
     */
    private void loadBanner(final Activity mActivity, String id, final FrameLayout adContainer, final ShimmerFrameLayout containerShimmer) {

        if (AppPurchase.getInstance().isPurchased(mActivity)) {
            containerShimmer.setVisibility(View.GONE);
            return;
        }

        containerShimmer.setVisibility(View.VISIBLE);
        containerShimmer.startShimmer();


        try {
            adView = new AdView(mActivity, id, AdSize.BANNER_HEIGHT_50);
            adContainer.addView(adView);

            adView.loadAd(adView.buildLoadAdConfig().withAdListener(new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.d(TAG, "loadBanner onError: " + adError.getErrorMessage());
                    containerShimmer.stopShimmer();
                    adContainer.setVisibility(View.GONE);
                    containerShimmer.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.d(TAG, "Banner onAdLoaded   ");
                    containerShimmer.stopShimmer();
                    containerShimmer.setVisibility(View.GONE);
                    adContainer.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            }).build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroyBanner() {
        if (adView != null)
            adView.destroy();
    }
}
