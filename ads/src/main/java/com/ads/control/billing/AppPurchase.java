package com.ads.control.billing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.ads.control.event.AperoLogEventManager;
import com.ads.control.funtion.BillingListener;
import com.ads.control.funtion.PurchaseListener;
import com.ads.control.util.AppUtil;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.common.collect.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppPurchase {
    private static final String LICENSE_KEY = null;
    private static final String MERCHANT_ID = null;
    private static final String TAG = "PurchaseEG";

    public static final String PRODUCT_ID_TEST = "android.test.purchased";
    @SuppressLint("StaticFieldLeak")
    private static AppPurchase instance;

    @SuppressLint("StaticFieldLeak")
    private String price = "1.49$";
    private String oldPrice = "2.99$";
    private String productId;
    private ArrayList<QueryProductDetailsParams.Product> listSubscriptionId;
    private ArrayList<QueryProductDetailsParams.Product> listINAPId;
    private PurchaseListener purchaseListener;
    private BillingListener billingListener;
    private Boolean isInitBillingFinish = false;
    private BillingClient billingClient;
    private List<ProductDetails> skuListINAPFromStore;
    private List<ProductDetails> skuListSubsFromStore;
    final private Map<String, ProductDetails> skuDetailsINAPMap = new HashMap<>();
    final private Map<String, ProductDetails> skuDetailsSubsMap = new HashMap<>();
    private boolean isAvailable;
    private boolean isListGot;
    private boolean isConsumePurchase = false;

    //tracking purchase adjust
    private String idPurchaseCurrent = "";
    private int typeIap;
    private boolean verified = false;

    private boolean isPurchase = false;//state purchase on app
    private String idPurchased = "";//id purchased

    public void setPurchaseListener(PurchaseListener purchaseListener) {
        this.purchaseListener = purchaseListener;
    }

    /**
     * Listener init billing app
     * When init available auto call onInitBillingFinish with resultCode = 0
     * @param billingListener
     */
    public void setBillingListener(BillingListener billingListener) {
        this.billingListener = billingListener;
        if (isAvailable) {
            billingListener.onInitBillingFinished(0);
            isInitBillingFinish = true;
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public Boolean getInitBillingFinish() {
        return isInitBillingFinish;
    }

    public void setEventConsumePurchaseTest(View view) {
        view.setOnClickListener(view1 -> {
            if (AppUtil.VARIANT_DEV) {
                Log.d(TAG, "setEventConsumePurchaseTest: success");
                AppPurchase.getInstance().consumePurchase(PRODUCT_ID_TEST);
            }
        });
    }

    /**
     * Listener init billing app with timeout
     * When init available auto call onInitBillingFinish with resultCode = 0
     * @param billingListener
     * @param timeout
     */
    public void setBillingListener(BillingListener billingListener, int timeout) {
        this.billingListener = billingListener;
        if (isAvailable) {
            billingListener.onInitBillingFinished(0);
            isInitBillingFinish = true;
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isInitBillingFinish) {
                    Log.e(TAG, "setBillingListener: timeout ");
                    isInitBillingFinish = true;
                    billingListener.onInitBillingFinished(BillingClient.BillingResponseCode.ERROR);
                }
            }
        }, timeout);
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setConsumePurchase(boolean consumePurchase) {
        isConsumePurchase = consumePurchase;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> list) {
            Log.e(TAG, "onPurchasesUpdated code: " + billingResult.getResponseCode());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                for (Purchase purchase : list) {

                    List<String> sku = purchase.getSkus();
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                if (purchaseListener != null)
                    purchaseListener.onUserCancelBilling();
                Log.d(TAG, "onPurchasesUpdated:USER_CANCELED ");
            } else {
                Log.d(TAG, "onPurchasesUpdated:... ");
            }
        }
    };

    BillingClientStateListener purchaseClientStateListener = new BillingClientStateListener() {
        @Override
        public void onBillingServiceDisconnected() {
            isAvailable = false;
        }

        @Override
        public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
            Log.d(TAG, "onBillingSetupFinished:  " + billingResult.getResponseCode());

            if (!isInitBillingFinish) {
                verifyPurchased(true);
            }

            isInitBillingFinish = true;
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                isAvailable = true;
                QueryProductDetailsParams paramsINAP = QueryProductDetailsParams.newBuilder()
                        .setProductList(listINAPId)
                        .build();

                billingClient.queryProductDetailsAsync(
                        paramsINAP,
                        new ProductDetailsResponseListener() {
                            public void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> productDetailsList) {
                                if (productDetailsList != null) {
                                    Log.d(TAG, "onSkuINAPDetailsResponse: " + productDetailsList.size());
                                    skuListINAPFromStore = productDetailsList;
                                    isListGot = true;
                                    addSkuINAPToMap(productDetailsList);
                                }
                            }
                        });

                QueryProductDetailsParams paramsSUBS = QueryProductDetailsParams.newBuilder()
                        .setProductList(listSubscriptionId)
                        .build();

                billingClient.queryProductDetailsAsync(
                        paramsSUBS,
                        new ProductDetailsResponseListener() {
                            public void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> productDetailsList) {
                                if (productDetailsList != null) {
                                    Log.d(TAG, "onSkuSubsDetailsResponse: " + productDetailsList.size());
                                    skuListSubsFromStore = productDetailsList;
                                    isListGot = true;
                                    addSkuSubsToMap(productDetailsList);
                                }
                            }
                        });
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE || billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR) {
                Log.e(TAG, "onBillingSetupFinished:ERROR ");

            }
        }
    };

    public static AppPurchase getInstance() {
        if (instance == null) {
            instance = new AppPurchase();
        }
        return instance;
    }

    private AppPurchase() {

    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void addSubcriptionId(String id) {
        if (listSubscriptionId == null)
            listSubscriptionId = new ArrayList<QueryProductDetailsParams.Product>();

        listSubscriptionId.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.SUBS)
                .build());
    }

    public void addProductId(String id) {
        if (listINAPId == null)
            listINAPId = new ArrayList<QueryProductDetailsParams.Product>();

        listSubscriptionId.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());
    }

    public void initBilling(final Application application) {
        listSubscriptionId =new ArrayList<QueryProductDetailsParams.Product>();
        listINAPId = new ArrayList<QueryProductDetailsParams.Product>();
        if (AppUtil.VARIANT_DEV) {
            listSubscriptionId.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID_TEST)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build());
        }
        billingClient = BillingClient.newBuilder(application)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(purchaseClientStateListener);
    }

    public void initBilling(final Application application, List<
            String> listINAPId, List<String> listSubsId) {

        if (AppUtil.VARIANT_DEV) {
            listINAPId.add(PRODUCT_ID_TEST);
        }
        this.listSubscriptionId = listIdToListProduct(listSubsId, BillingClient.ProductType.SUBS);
        this.listINAPId = listIdToListProduct(listINAPId, BillingClient.ProductType.INAPP);

        billingClient = BillingClient.newBuilder(application)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(purchaseClientStateListener);
    }


    private void addSkuSubsToMap(List<ProductDetails> skuList) {
        for (ProductDetails skuDetails : skuList) {
            skuDetailsSubsMap.put(skuDetails.getProductId(), skuDetails);
        }
    }

    private void addSkuINAPToMap(List<ProductDetails> skuList) {
        for (ProductDetails skuDetails : skuList) {
            skuDetailsINAPMap.put(skuDetails.getProductId(), skuDetails);
        }
    }

    public boolean isPurchased() {
        return isPurchase;
    }

    public boolean isPurchased(Context context) {
        return isPurchase;
    }

    public String getIdPurchased(){
        return  idPurchased;
    }

    private boolean verifiedINAP = false;
    private boolean verifiedSUBS = false;

    // kiểm tra trạng thái purchase
    public void verifyPurchased(boolean isCallback) {
        Log.d(TAG, "isPurchased : " + listSubscriptionId.size());
        verified = false;
        if (listINAPId != null) {
            billingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                    new PurchasesResponseListener() {
                        public void onQueryPurchasesResponse(
                                BillingResult billingResult,
                                List<Purchase> list) {
                            Log.d(TAG, "verifyPurchased INAPP  code:" + billingResult.getResponseCode() + " ===   size:" + list.size());
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase : list) {
                                    for (QueryProductDetailsParams.Product id : listINAPId) {
                                        if (purchase.getProducts().contains(id)) {
                                            Log.d(TAG, "verifyPurchased INAPP: true");
                                            isPurchase = true;
                                            if (!verified) {
                                                if (billingListener != null && isCallback)
                                                    billingListener.onInitBillingFinished(billingResult.getResponseCode());
                                                verified = true;
                                                verifiedINAP = true;
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                            if (verifiedSUBS && !verified) {
                                // chưa mua subs và IAP
                                billingListener.onInitBillingFinished(billingResult.getResponseCode());
                            }
                            verifiedINAP = true;
                        }
                    }
            );
        }

        if (listSubscriptionId != null) {
            billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, new PurchasesResponseListener() {
                @Override
                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {

                }
            });
            billingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                    new PurchasesResponseListener() {
                        public void onQueryPurchasesResponse(
                                BillingResult billingResult,
                                List<Purchase> list) {
                            Log.d(TAG, "verifyPurchased SUBS  code:" + billingResult.getResponseCode() + " ===   size:" + list.size());
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase : list) {
                                    for (QueryProductDetailsParams.Product id : listSubscriptionId) {
                                        if (purchase.getProducts().contains(id)) {
                                            Log.d(TAG, "verifyPurchased SUBS: true");
                                            isPurchase = true;
                                            if (!verified) {
                                                if (billingListener != null && isCallback)
                                                    billingListener.onInitBillingFinished(billingResult.getResponseCode());
                                                verified = true;
                                                verifiedINAP = true;
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                            if (verifiedINAP && !verified) {
                                // chưa mua subs và IAP
                                if (billingListener != null && isCallback) {
                                    billingListener.onInitBillingFinished(billingResult.getResponseCode());
                                }
                            }
                            verifiedSUBS = true;
                        }
                    }
            );
        }
    }


/*    private String logResultBilling(Purchase.PurchasesResult result) {
        if (result == null || result.getPurchasesList() == null)
            return "null";
        StringBuilder log = new StringBuilder();
        for (Purchase purchase : result.getPurchasesList()) {
            for (String s : purchase.getSkus()) {
                log.append(s).append(",");
            }
        }
        return log.toString();
    }*/


    public void purchase(Activity activity) {
        if (productId == null) {
            Log.e(TAG, "Purchase false:productId null");
            Toast.makeText(activity, "Product id must not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        purchase(activity, productId);
    }


    public String purchase(Activity activity, String productId) {
        if (skuListINAPFromStore == null) {
            if (purchaseListener != null)
                purchaseListener.displayErrorMessage("Billing error init");
            return "";
        }
        if (AppUtil.VARIANT_DEV) {
            // Auto using id purchase test in variant dev
            productId = PRODUCT_ID_TEST;
        }

        ProductDetails productDetails = skuDetailsINAPMap.get(productId);


        if (productDetails == null) {
            return "Product ID invalid";
        }

        idPurchaseCurrent = productId;
        typeIap = TYPE_IAP.PURCHASE;

/*        int selectedOfferIndex = 0;// undefined variable for what
        String offerToken = productDetails
                .getSubscriptionOfferDetails()
                .get(selectedOfferIndex)
                .getOfferToken();*/

        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
//                                .setOfferToken(offerToken)
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);

        switch (billingResult.getResponseCode()) {

            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Billing not supported for type of request");
                return "Billing not supported for type of request";

            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                return "";

            case BillingClient.BillingResponseCode.ERROR:
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Error completing request");
                return "Error completing request";

            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                return "Error processing request.";

            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                return "Selected item is already owned";

            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                return "Item not available";

            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                return "Play Store service is not connected now";

            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                return "Timeout";

            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Network error.");
                return "Network Connection down";

            case BillingClient.BillingResponseCode.USER_CANCELED:
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Request Canceled");
                return "Request Canceled";

            case BillingClient.BillingResponseCode.OK:
                return "Subscribed Successfully";
            //}

        }
        return "";
    }

    public String subscribe(Activity activity, String SubsId) {

        if (skuListSubsFromStore == null) {
            if (purchaseListener != null)
                purchaseListener.displayErrorMessage("Billing error init");
            return "";
        }

        if (AppUtil.VARIANT_DEV) {
            // sử dụng ID Purchase test
            purchase(activity, PRODUCT_ID_TEST);
            return "Billing test";
        }
        ProductDetails productDetails = skuDetailsINAPMap.get(productId);
        if (productDetails == null) {
            return "Product ID invalid";
        }
/*        int selectedOfferIndex = 0;// undefined variable for what
        String offerToken = productDetails
                .getSubscriptionOfferDetails()
                .get(selectedOfferIndex)
                .getOfferToken();*/

        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
//                                .setOfferToken(offerToken)
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);

        switch (billingResult.getResponseCode()) {

            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Billing not supported for type of request");
                return "Billing not supported for type of request";

            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                return "";

            case BillingClient.BillingResponseCode.ERROR:
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Error completing request");
                return "Error completing request";

            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                return "Error processing request.";

            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                return "Selected item is already owned";

            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                return "Item not available";

            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                return "Play Store service is not connected now";

            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                return "Timeout";

            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Network error.");
                return "Network Connection down";

            case BillingClient.BillingResponseCode.USER_CANCELED:
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Request Canceled");
                return "Request Canceled";

            case BillingClient.BillingResponseCode.OK:
                return "Subscribed Successfully";

            //}

        }
        return "";
    }

    public void consumePurchase() {
        if (productId == null) {
            Log.e(TAG, "Consume Purchase false:productId null ");
            return;
        }
        consumePurchase(productId);
    }

    public void consumePurchase(String productId) {
        billingClient.queryPurchasesAsync(BillingClient.ProductType.INAPP, (billingResult, list) -> {
            Purchase pc = null;
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                for (Purchase purchase : list) {
                    if (purchase.getSkus().contains(productId)) {
                        pc = purchase;
                    }
                }
            }
            if (pc == null)
                return;
            try {
                ConsumeParams consumeParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(pc.getPurchaseToken())
                                .build();

                ConsumeResponseListener listener = new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            Log.e(TAG, "onConsumeResponse: OK");
                            verifyPurchased(false);
                        }
                    }
                };

                billingClient.consumeAsync(consumeParams, listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void handlePurchase(Purchase purchase) {

        //tracking adjust
        double price = getPriceWithoutCurrency(idPurchaseCurrent, typeIap);
        String currency = getCurrency(idPurchaseCurrent, typeIap);
        AperoLogEventManager.onTrackRevenuePurchase((float) price, currency, idPurchaseCurrent,typeIap);

        if (purchaseListener != null)
            isPurchase = true;
        purchaseListener.onProductPurchased(purchase.getOrderId(), purchase.getOriginalJson());
        if (isConsumePurchase) {
            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

            ConsumeResponseListener listener = new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                    Log.d(TAG, "onConsumeResponse: " + billingResult.getDebugMessage());

                }
            };

            billingClient.consumeAsync(consumeParams, listener);
        } else {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                if (!purchase.isAcknowledged()) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                        @Override
                        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                            Log.d(TAG, "onAcknowledgePurchaseResponse: " + billingResult.getDebugMessage());
                        }
                    });
                }
            }
        }
    }

    //    public boolean handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        return bp.handleActivityResult(requestCode, resultCode, data);
//    }
//
    public String getPrice() {
        return getPrice(productId);
    }

    public String getPrice(String productId) {

        ProductDetails skuDetails = skuDetailsINAPMap.get(productId);
        if (skuDetails == null)
            return "";

        Log.e(TAG, "getPrice: " + skuDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());

        return skuDetails.getOneTimePurchaseOfferDetails().getFormattedPrice();
    }

    public String getPriceSub(String productId) {
        ProductDetails skuDetails = skuDetailsSubsMap.get(productId);
        if (skuDetails == null)
            return "";
        Log.e(TAG, "getPrice: " + skuDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());

        return skuDetails.getOneTimePurchaseOfferDetails().getFormattedPrice();
    }

    public String getIntroductorySubPrice(String productId) {
        ProductDetails skuDetails = skuDetailsSubsMap.get(productId);
        if (skuDetails == null) {
            return "";
        }
        return skuDetails.getOneTimePurchaseOfferDetails().getFormattedPrice();
    }

    public String getCurrency(String productId, int typeIAP) {
        ProductDetails skuDetails = typeIAP == TYPE_IAP.PURCHASE ? skuDetailsINAPMap.get(productId) : skuDetailsSubsMap.get(productId);
        if (skuDetails == null) {
            return "";
        }
        return skuDetails.getOneTimePurchaseOfferDetails().getPriceCurrencyCode();
    }

    public double getPriceWithoutCurrency(String productId, int typeIAP) {
        ProductDetails skuDetails = typeIAP == TYPE_IAP.PURCHASE ? skuDetailsINAPMap.get(productId) : skuDetailsSubsMap.get(productId);
        if (skuDetails == null) {
            return 0;
        }
        return skuDetails.getOneTimePurchaseOfferDetails().getPriceAmountMicros();
    }
//
//    public String getOldPrice() {
//        SkuDetails skuDetails = bp.getPurchaseListingDetails(productId);
//        if (skuDetails == null)
//            return "";
//        return formatCurrency(skuDetails.priceValue / discount, skuDetails.currency);
//    }

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

    private ArrayList<QueryProductDetailsParams.Product> listIdToListProduct(List<String> listId, String styleBilling) {
        ArrayList<QueryProductDetailsParams.Product> listProduct = new ArrayList<QueryProductDetailsParams.Product>();
        for (String id : listId) {
            QueryProductDetailsParams.Product product = QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(id)
                    .setProductType(styleBilling)
                    .build();
            listProduct.add(product);
        }
        return listProduct;
    }

    @IntDef({TYPE_IAP.PURCHASE, TYPE_IAP.SUBSCRIPTION})
    public @interface TYPE_IAP {
        int PURCHASE = 1;
        int SUBSCRIPTION = 2;
    }
}
