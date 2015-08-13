//package com.linxy.gradeorganizer;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.util.Log;
//
//import util.IabHelper;
//import util.IabResult;
//import util.Inventory;
//import util.Purchase;
//
///**
// * Created by Linxy on 12/8/2015 at 16:40
// * Working on Grade Organizer in com.linxy.gradeorganizer
// */
//public class InAppBillingActivity  extends Activity {
//
//    IabHelper mHelper;
//    static final String ITEM_SKU = "purchase_premium";
//
//    public void buyClick(){
//        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurcahseFinishedListener, "numibuy");
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,
//                                    Intent data)
//    {
//        if (!mHelper.handleActivityResult(requestCode,
//                resultCode, data)) {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
//            = new IabHelper.OnIabPurchaseFinishedListener() {
//        public void onIabPurchaseFinished(IabResult result,
//                                          Purchase purchase)
//        {
//            if (result.isFailure()) {
//                // Handle error
//                return;
//            }
//            else if (purchase.getSku().equals(ITEM_SKU)) {
//                Log.i("PREMIUM BOUGH", "PREMIUM ACTGIVE");
//                consumeItem();
//                StartupActivity.PREMIUM = true;
//            }
//
//        }
//    };
//
//    public void consumeItem() {
//        mHelper.queryInventoryAsync(mReceivedInventoryListener);
//    }
//
//    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
//            = new IabHelper.QueryInventoryFinishedListener() {
//        public void onQueryInventoryFinished(IabResult result,
//                                             Inventory inventory) {
//
//
//            if (result.isFailure()) {
//                // Handle failure
//            } else {
//                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
//                        mConsumeFinishedListener);
//            }
//        }
//    };
//
//    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
//            new IabHelper.OnConsumeFinishedListener() {
//                public void onConsumeFinished(Purchase purchase,
//                                              IabResult result) {
//
//                    if (result.isSuccess()) {
//                        Log.i("Purchsed", "ITEM");
//                    } else {
//                        // handle error
//                    }
//                }
//            };
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mHelper != null) mHelper.dispose();
//        mHelper = null;
//    }
//
//}
