package com.ads.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ads.control.funtion.AdmodHelper;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class Purchase {
    private static final String LICENSE_KEY = null;
    private static final String MERCHANT_ID = null;
    private BillingProcessor bp;
    //    public static final String PRODUCT_ID = "android.test.purchased";
    @SuppressLint("StaticFieldLeak")
    private static Purchase instance;

    @SuppressLint("StaticFieldLeak")
    private String price = "1.49$";
    private String oldPrice = "2.99$";
    private String productId;

    public void setPrice(String price) {
        this.price = price;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public static Purchase getInstance() {
        if (instance == null) {
            instance = new Purchase();
        }
        return instance;
    }

    private Purchase() {

    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void initBilling(final Context context) {
        bp = new BillingProcessor(context, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
            }

            @Override
            public void onBillingInitialized() {

            }

            @Override
            public void onPurchaseHistoryRestored() {

            }
        });
        bp.initialize();
    }

    public boolean isPurchased(Context context) {
        return isPurchased(context, productId);
    }

    public boolean isPurchased(Context context, String productId) {
        if (bp == null) {
            initBilling(context);
        }
        return bp.isPurchased(productId) || bp.isSubscribed(productId);
    }

    public void purchase(Activity activity) {
        purchase(activity, productId);
    }


    public void purchase(Activity activity, String productId) {
        if (bp == null) {
            initBilling(activity);
        }
        bp.purchase(activity, productId);
    }

    public void subscribe(Activity activity, String productId) {
        if (bp == null) {
            initBilling(activity);
        }
        bp.subscribe(activity, productId);
    }


    public void consumePurchase(String productId) {
        try {
            bp.consumePurchase(productId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        bp.handleActivityResult(requestCode, resultCode, data);
    }

    public String getPrice(String productId) {
        return bp.getPurchaseListingDetails(productId).priceText;
    }

    public String getOldPrice(String productId) {
        return oldPrice;
    }
}
