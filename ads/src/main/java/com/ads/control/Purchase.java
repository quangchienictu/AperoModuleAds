package com.ads.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ads.control.funtion.AdmodHelper;
import com.ads.control.funtion.PurchaseListioner;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.text.NumberFormat;
import java.util.Currency;

public class Purchase {
    private static final String LICENSE_KEY = null;
    private static final String MERCHANT_ID = null;
    private static final String TAG = "PurchaseEG";
    private BillingProcessor bp;
    //    public static final String PRODUCT_ID = "android.test.purchased";
    @SuppressLint("StaticFieldLeak")
    private static Purchase instance;

    @SuppressLint("StaticFieldLeak")
    private String price = "1.49$";
    private String oldPrice = "2.99$";
    private String productId;
    private PurchaseListioner purchaseListioner;

    public void setPurchaseListioner(PurchaseListioner purchaseListioner) {
        this.purchaseListioner = purchaseListioner;
    }

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
                Log.e(TAG, "ProductPurchased:" + productId);
                if (purchaseListioner != null)
                    purchaseListioner.onProductPurchased(productId);
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
            }

            @Override
            public void onBillingInitialized() {

            }

            @Override
            public void onPurchaseHistoryRestored() {
                Log.e(TAG, "PurchaseHistoryRestored");
            }
        });

        bp.initialize();
        bp.loadOwnedPurchasesFromGoogle();
    }

    public boolean isPurchased(Context context) {
        return isPurchased(context, productId);
    }

    public boolean isPurchased(Context context, String productId) {
        if (bp == null) {
            initBilling(context);
        }
        if (productId == null)
            return false;
        TransactionDetails transactionDetails = bp.getSubscriptionTransactionDetails(productId);
//        if (transactionDetails != null)
//            Toast.makeText(context, "TransactionDetails autoRenewing:" + transactionDetails.purchaseInfo.purchaseData.autoRenewing, Toast.LENGTH_SHORT).show();
        boolean pp = bp.isPurchased(productId) || bp.isSubscribed(productId);
        Log.e(TAG, "isPurchased:" + pp);
        return pp;
    }

    public void purchase(Activity activity) {
        if (productId == null) {
            Log.e(TAG, "Purchase false:productId null");
            Toast.makeText(activity, "Product id must not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
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

    public void consumePurchase() {
        if (productId == null) {
            Log.e(TAG, "Consume Purchase false:productId null ");
            return;
        }
        consumePurchase(productId);
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

    public String getPrice() {
        return getPrice(productId);
    }

    public String getPrice(String productId) {
        SkuDetails skuDetails = bp.getPurchaseListingDetails(productId);
        if (skuDetails == null)
            return "";
        return formatCurrency(skuDetails.priceValue, skuDetails.currency);
    }

    public String getOldPrice() {
        SkuDetails skuDetails = bp.getPurchaseListingDetails(productId);
        if (skuDetails == null)
            return "";
        return formatCurrency(skuDetails.priceValue / discount, skuDetails.currency);
    }

    private String formatCurrency(double price, String currency) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance(currency));
        return format.format(price);
    }

    private double discount = 1;

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscount() {
        return discount;
    }
}
