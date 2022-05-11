package com.ads.control.applovin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustConfig;
import com.ads.control.R;
import com.ads.control.billing.AppPurchase;
import com.ads.control.dialog.PrepareLoadingAdsDialog;
import com.ads.control.funtion.AdCallback;
import com.ads.control.util.AdjustApero;
import com.ads.control.util.FirebaseAnalyticsUtil;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.applovin.sdk.AppLovinSdk;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Arrays;
import java.util.Calendar;

public class AppLovin {
    private static final String TAG = "AppLovin";
    private static AppLovin instance;
    private int currentClicked = 0;
    private String nativeId;
    private int numShowAds = 3;

    private int maxClickAds = 100;
    private Handler handlerTimeout;
    private Runnable rdTimeout;
    private PrepareLoadingAdsDialog dialog;
    private boolean isTimeout; // xử lý timeout show ads

    private boolean isShowLoadingSplash = false;  //kiểm tra trạng thái ad splash, ko cho load, show khi đang show loading ads splash
    boolean isTimeDelay = false; //xử lý delay time show ads, = true mới show ads
    private Context context;
//    private AppOpenAd appOpenAd = null;
//    private static final String SHARED_PREFERENCE_NAME = "ads_shared_preference";

//    private final Map<String, AppOpenAd> appOpenAdMap = new HashMap<>();

    private MaxInterstitialAd interstitialSplash;
    private MaxInterstitialAd interstitialAd;
    MaxNativeAdView nativeAdView;

    public static AppLovin getInstance() {
        if (instance == null) {
            instance = new AppLovin();
            instance.isShowLoadingSplash = false;
        }
        return instance;
    }

    public void init(Context context, AppLovinCallback adCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = Application.getProcessName();
            String packageName = context.getPackageName();
            if (!packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }

        AppLovinSdk.getInstance(context).setMediationProvider("max");
        AppLovinSdk.getInstance(context).getSettings().setCreativeDebuggerEnabled(true);
        AppLovinSdk.initializeSdk(context, configuration -> {
            // AppLovin SDK is initialized, start loading ads
            Log.d(TAG, "init: applovin success");
            adCallback.initAppLovinSuccess();
        });
        this.context = context;
    }

    /**
     * Load quảng cáo Full tại màn SplashActivity
     * Sau khoảng thời gian timeout thì load ads và callback về cho View
     *
     * @param context
     * @param id
     * @param timeOut    : thời gian chờ ads, timeout <= 0 tương đương với việc bỏ timeout
     * @param timeDelay  : thời gian chờ show ad từ lúc load ads
     * @param adListener
     */
    public void loadSplashInterstitialAds(final Context context, String id, long timeOut, long timeDelay, AppLovinCallback adListener) {
        isTimeDelay = false;
        isTimeout = false;
        Log.i(TAG, "loadSplashInterstitialAds  start time loading:"
                + Calendar.getInstance().getTimeInMillis()
                + " ShowLoadingSplash:" + isShowLoadingSplash);

        if (AppPurchase.getInstance().isPurchased(context)) {
            if (adListener != null) {
                adListener.onAdClosed();
            }
            return;
        }
        new Handler().postDelayed(() -> {
            //check delay show ad splash
            if (interstitialSplash.isReady()) {
                Log.i(TAG, "loadSplashInterstitialAds:show ad on delay ");
                onShowSplash((Activity) context, adListener);
                return;
            }
            Log.i(TAG, "loadSplashInterstitialAds: delay validate");
            isTimeDelay = true;
        }, timeDelay);

        if (timeOut > 0) {
            handlerTimeout = new Handler();
            rdTimeout = () -> {
                Log.e(TAG, "loadSplashInterstitialAds: on timeout");
                isTimeout = true;
                if (interstitialSplash.isReady()) {
                    Log.i(TAG, "loadSplashInterstitialAds:show ad on timeout ");
                    onShowSplash((Activity) context, adListener);
                    return;
                }
                if (adListener != null) {
                    adListener.onAdClosed();
                    isShowLoadingSplash = false;
                }
            };
            handlerTimeout.postDelayed(rdTimeout, timeOut);
        }

        isShowLoadingSplash = true;

        interstitialSplash = getInterstitialAds(context, id);
        interstitialSplash.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.e(TAG, "loadSplashInterstitialAds end time loading success: "
                        + Calendar.getInstance().getTimeInMillis()
                        + " time limit:" + isTimeout);
                if (isTimeout)
                    return;
                if (isTimeDelay) {
                    onShowSplash((Activity) context, adListener);
                    Log.i(TAG, "loadSplashInterstitialAds: show ad on loaded ");
                }
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.e(TAG, "onAdLoadFailed: " + error.getMessage());
                if (isTimeout)
                    return;
                if (adListener != null) {
                    if (handlerTimeout != null && rdTimeout != null) {
                        handlerTimeout.removeCallbacks(rdTimeout);
                    }
                    Log.e(TAG, "loadSplashInterstitialAds: load fail " + error.getMessage());
                    adListener.onAdFailedToLoad(error);
                }
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
    }

    private void onShowSplash(Activity activity, AppLovinCallback adListener) {
        isShowLoadingSplash = true;
        Log.d(TAG, "onShowSplash: ");
        if (handlerTimeout != null && rdTimeout != null) {
            handlerTimeout.removeCallbacks(rdTimeout);
        }

        if (adListener != null) {
            adListener.onAdLoaded();
        }
        interstitialSplash.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.d(TAG, "onAdDisplayed: ");
                isShowLoadingSplash = false;
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                Log.d(TAG, "onAdHidden: ");
                if (adListener != null) {
                    adListener.onAdClosed();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
                interstitialSplash = null;
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                FirebaseAnalyticsUtil.logClickAdsEvent(context, interstitialSplash.getAdUnitId());
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.d(TAG, "onAdDisplayFailed: " + error.getMessage());
                interstitialSplash = null;
                isShowLoadingSplash = false;
                if (adListener != null) {
                    adListener.onAdFailedToShow(error);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }
        });

        if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            try {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                dialog = new PrepareLoadingAdsDialog(activity);
                try {
                    dialog.show();
                } catch (Exception e) {
                    adListener.onAdClosed();
                    return;
                }
            } catch (Exception e) {
                dialog = null;
                e.printStackTrace();
            }
            new Handler().postDelayed(() -> {
                if (activity != null)
                    interstitialSplash.showAd();
                isShowLoadingSplash = false;
            }, 800);
        }
    }

    /**
     * Trả về 1 InterstitialAd và request Ads
     *
     * @param context
     * @param id
     * @return
     */
    public MaxInterstitialAd getInterstitialAds(Context context, String id) {
        if (AppPurchase.getInstance().isPurchased(context) || AppLovinHelper.getNumClickAdsPerDay(context, id) >= maxClickAds) {
            Log.d(TAG, "getInterstitialAds: ignore");
            return null;
        }
        final MaxInterstitialAd interstitialAd = new MaxInterstitialAd(id, (Activity) context);
        interstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(TAG, "onAdLoaded: getInterstitialAds");
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.e(TAG, "onAdLoadFailed: getInterstitialAds " + error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
        requestInterstitialAds(interstitialAd);
        return interstitialAd;
    }

    private void requestInterstitialAds(MaxInterstitialAd maxInterstitialAd) {
        if (maxInterstitialAd != null && !maxInterstitialAd.isReady()) {
            maxInterstitialAd.loadAd();
        }
    }

    /**
     * Bắt buộc hiển thị  ads full và callback result
     *
     * @param context
     * @param interstitialAd
     * @param callback
     */
    public void forceShowInterstitial(Context context, MaxInterstitialAd interstitialAd, final AdCallback callback, boolean shouldReload) {
        currentClicked = numShowAds;
        showInterstitialAdByTimes(context, interstitialAd, callback, shouldReload);
    }

    /**
     * Hiển thị ads theo số lần được xác định trước và callback result
     * vd: click vào 3 lần thì show ads full.
     * AdmodHelper.setupAdmodData(context) -> kiểm tra xem app đc hoạt động đc 1 ngày chưa nếu YES thì reset lại số lần click vào ads
     *
     * @param context
     * @param interstitialAd
     * @param callback
     * @param shouldReloadAds
     */
    public void showInterstitialAdByTimes(final Context context, MaxInterstitialAd interstitialAd, final AdCallback callback, final boolean shouldReloadAds) {
        AppLovinHelper.setupAppLovinData(context);
        if (AppPurchase.getInstance().isPurchased(context)) {
            callback.onAdClosed();
            return;
        }
        if (interstitialAd == null) {
            if (callback != null) {
                callback.onAdClosed();
            }
            return;
        }
        interstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                if (callback != null) {
                    callback.onAdClosed();
                    if (shouldReloadAds) {
                        requestInterstitialAds(interstitialAd);
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
                Log.d(TAG, "onAdHidden: ");
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                FirebaseAnalyticsUtil.logClickAdsEvent(context, interstitialAd.getAdUnitId());
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.e(TAG, "onAdDisplayFailed: " + error.getMessage());
                if (callback != null) {
                    callback.onAdClosed();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }
        });
        if (AppLovinHelper.getNumClickAdsPerDay(context, interstitialAd.getAdUnitId()) < maxClickAds) {
            showInterstitialAd(context, interstitialAd, callback);
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
     * @param interstitialAd
     * @param callback
     */
    private void showInterstitialAd(Context context, MaxInterstitialAd interstitialAd, AdCallback callback) {
        currentClicked++;
        if (currentClicked >= numShowAds && interstitialAd != null) {
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
                new Handler().postDelayed(interstitialAd::showAd, 800);
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

    private void loadBanner(final Activity mActivity, String id, final FrameLayout adContainer, final ShimmerFrameLayout containerShimmer) {
        if (AppPurchase.getInstance().isPurchased(mActivity)) {
            containerShimmer.setVisibility(View.GONE);
            return;
        }
        containerShimmer.setVisibility(View.VISIBLE);
        containerShimmer.startShimmer();
        MaxAdView adView = new MaxAdView(id, mActivity);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        // Banner height on phones and tablets is 50 and 90, respectively
        int heightPx = context.getResources().getDimensionPixelSize(R.dimen.banner_height);
        adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        adContainer.addView(adView);
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(TAG, "onAdLoaded: banner");
                containerShimmer.stopShimmer();
                containerShimmer.setVisibility(View.GONE);
                adContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {
                FirebaseAnalyticsUtil.logClickAdsEvent(context, id);
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.e(TAG, "onAdLoadFailed: banner " + error.getMessage());
                containerShimmer.stopShimmer();
                adContainer.setVisibility(View.GONE);
                containerShimmer.setVisibility(View.GONE);
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
        adView.loadAd();
    }



    public void loadNative(final Activity mActivity, String adUnitId) {
        final FrameLayout frameLayout = mActivity.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer = mActivity.findViewById(R.id.shimmer_container_native);
        loadNativeAd(mActivity, containerShimmer, frameLayout, adUnitId, R.layout.max_native_custom_ad_view);
    }

    public void loadNativeSmall(final Activity mActivity, String adUnitId) {
        final FrameLayout frameLayout = mActivity.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer = mActivity.findViewById(R.id.shimmer_container_small_native);
        loadNativeAd(mActivity, containerShimmer, frameLayout, adUnitId, R.layout.max_native_custom_ad_small);
    }

    public void loadNativeFragment(final Activity mActivity, String adUnitId, View parent) {
        final FrameLayout frameLayout = parent.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer = parent.findViewById(R.id.shimmer_container_native);
        loadNativeAd(mActivity, containerShimmer, frameLayout, adUnitId, R.layout.max_native_custom_ad_view);
    }
    public void loadNativeSmallFragment(final Activity mActivity, String adUnitId, View parent) {
        final FrameLayout frameLayout = parent.findViewById(R.id.fl_adplaceholder);
        final ShimmerFrameLayout containerShimmer = parent.findViewById(R.id.shimmer_container_small_native);
        loadNativeAd(mActivity, containerShimmer, frameLayout, adUnitId, R.layout.max_native_custom_ad_small);
    }
    public void loadNativeAd(Activity activity, ShimmerFrameLayout containerShimmer, FrameLayout nativeAdLayout, String id, int layoutCustomNative) {

        if (AppPurchase.getInstance().isPurchased(context)) {
            containerShimmer.setVisibility(View.GONE);
            return;
        }
        containerShimmer.setVisibility(View.VISIBLE);
        containerShimmer.startShimmer();

        nativeAdLayout.removeAllViews();
        MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(layoutCustomNative)
                .setTitleTextViewId(R.id.title_text_view)
                .setBodyTextViewId(R.id.body_text_view)
                .setAdvertiserTextViewId(R.id.advertiser_textView)
                .setIconImageViewId(R.id.icon_image_view)
                .setMediaContentViewGroupId(R.id.media_view_container)
                .setOptionsContentViewGroupId(R.id.options_view)
                .setCallToActionButtonId(R.id.cta_button)
                .build();

        nativeAdView = new MaxNativeAdView(binder, activity);

        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(id, activity);
        nativeAdLoader.setRevenueListener(AdjustApero::pushTrackEventApplovin);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                Log.d(TAG, "onNativeAdLoaded ");
                containerShimmer.stopShimmer();
                containerShimmer.setVisibility(View.GONE);
                // Add ad view to view.
                nativeAdLayout.setVisibility(View.VISIBLE);
                nativeAdLayout.addView(nativeAdView);
            }

            @Override
            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                Log.e(TAG, "onAdFailedToLoad: " + error.getMessage());
                containerShimmer.stopShimmer();
                containerShimmer.setVisibility(View.GONE);
                nativeAdLayout.setVisibility(View.GONE);
            }

            @Override
            public void onNativeAdClicked(final MaxAd ad) {
                Log.e(TAG, "onNativeAdClicked: "  );
                containerShimmer.setVisibility(View.VISIBLE);
                containerShimmer.startShimmer();
                nativeAdLayout.removeAllViews();
                nativeAdLayout.setVisibility(View.GONE);

                nativeAdView = new MaxNativeAdView(binder, activity);
                nativeAdLoader.loadAd(nativeAdView);
            }
        });
        nativeAdLoader.loadAd(nativeAdView);
    }
}
