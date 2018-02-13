package com.payu.payuui.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import com.payu.custombrowser.Bank;
import com.payu.custombrowser.CustomBrowser;
import com.payu.custombrowser.PayUCustomBrowserCallback;
import com.payu.custombrowser.PayUWebChromeClient;
import com.payu.custombrowser.PayUWebViewClient;
import com.payu.custombrowser.bean.CustomBrowserConfig;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuUtils;
import com.payu.magicretry.MagicRetryFragment;
import com.payu.payuui.R;
import java.util.HashMap;
import java.util.Map;

public class PaymentsActivity extends FragmentActivity {
    Bundle bundle;
    String url;
    boolean cancelTransaction = false;
    PayuConfig payuConfig;
    private BroadcastReceiver mReceiver = null;
    private String UTF = "UTF-8";
    private  boolean viewPortWide = false;
    private PayuUtils mPayuUtils;
    private int storeOneClickHash;
    private String merchantHash;
    MagicRetryFragment magicRetryFragment;
    String txnId = null;
    String merchantKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mPayuUtils = new PayuUtils();
            bundle = getIntent().getExtras();
            payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
            storeOneClickHash = bundle.getInt(PayuConstants.STORE_ONE_CLICK_HASH);
            url = payuConfig.getEnvironment() == PayuConstants.PRODUCTION_ENV ? PayuConstants.PRODUCTION_PAYMENT_URL : PayuConstants.TEST_PAYMENT_URL;
            String[] list = payuConfig.getData().split("&");

            for (String item : list) {
                String[] items = item.split("=");
                if (items.length >= 2) {
                    String id = items[0];
                    switch (id) {
                        case "txnid":
                            txnId = items[1];
                            break;
                        case "key":
                            merchantKey = items[1];
                            break;
                        case "pg":
                            if (items[1].contentEquals("NB")) {
                                viewPortWide = true;
                            }
                            break;

                    }
                }
            }

           /* Intent intent = new Intent();
            intent.putExtra(getString(R.string.cb_result), merchantResponse);
            intent.putExtra(getString(R.string.cb_payu_response), payuResponse);
            if(storeOneClickHash == PayuConstants.STORE_ONE_CLICK_HASH_SERVER && null != merchantHash){
                intent.putExtra(PayuConstants.MERCHANT_HASH, merchantHash);
            }
            setResult(Activity.RESULT_CANCELED, intent);
             finish();*/


            /*Intent intent = new Intent();
            intent.putExtra(getString(R.string.cb_result), merchantResponse);
            intent.putExtra(getString(R.string.cb_payu_response), payuResponse);
            if(storeOneClickHash == PayuConstants.STORE_ONE_CLICK_HASH_SERVER && null != merchantHash){
                intent.putExtra(PayuConstants.MERCHANT_HASH, merchantHash);
            }
            setResult(Activity.RESULT_OK, intent);
            finish();*/

            //set callback to track important events

            PayUCustomBrowserCallback payUCustomBrowserCallback = new PayUCustomBrowserCallback() {
                @Override
                public void onPaymentFailure(String payuResponse,String merchantResponse) {
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.cb_result), merchantResponse);
                    intent.putExtra(getString(R.string.cb_payu_response), payuResponse);
                    if(storeOneClickHash == PayuConstants.STORE_ONE_CLICK_HASH_SERVER && null != merchantHash){
                        intent.putExtra(PayuConstants.MERCHANT_HASH, merchantHash);
                    }
                    setResult(Activity.RESULT_CANCELED, intent);
                    finish();
                }

                @Override
                public void onPaymentTerminate() {
                }

                @Override
                public void onPaymentSuccess(String payuResponse,String merchantResponse) {
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.cb_result), merchantResponse);
                    intent.putExtra(getString(R.string.cb_payu_response), payuResponse);
                    if(storeOneClickHash == PayuConstants.STORE_ONE_CLICK_HASH_SERVER && null != merchantHash){
                        intent.putExtra(PayuConstants.MERCHANT_HASH, merchantHash);
                    }
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onCBErrorReceived(int code, String errormsg) {
                }

               

                @Override
                public void setCBProperties(WebView webview, Bank payUCustomBrowser) {
                    webview.setWebChromeClient(new PayUWebChromeClient(payUCustomBrowser));
                    webview.setWebViewClient(new PayUWebViewClient(payUCustomBrowser,merchantKey));
                    webview.postUrl(url, payuConfig.getData().getBytes());
                }

                @Override
                public void onBackApprove() {
                    PaymentsActivity.this.finish();
                }

                @Override
                public void onBackDismiss() {
                    super.onBackDismiss();
                }

                @Override
                public void onBackButton(AlertDialog.Builder alertDialogBuilder) {
                    super.onBackButton(alertDialogBuilder);
                }

                @Override
                public void initializeMagicRetry(Bank payUCustomBrowser,WebView webview,MagicRetryFragment magicRetryFragment){
                    webview.setWebViewClient(new PayUWebViewClient(payUCustomBrowser, magicRetryFragment,merchantKey));
                    Map<String, String> urlList = new HashMap<String, String>();
                    urlList.put(url, payuConfig.getData());
                    payUCustomBrowser.setMagicRetry(urlList);
                }

            };
            CustomBrowserConfig customBrowserConfig = new CustomBrowserConfig(merchantKey,txnId);
            customBrowserConfig.setViewPortWideEnable(viewPortWide);
            customBrowserConfig.setAutoApprove(false);
            customBrowserConfig.setAutoSelectOTP(false);
            customBrowserConfig.setDisableBackButtonDialog(false);
            customBrowserConfig.setStoreOneClickHash(CustomBrowserConfig.STOREONECLICKHASH_MODE_SERVER);
            customBrowserConfig.setMerchantSMSPermission(false);
            customBrowserConfig.setmagicRetry(true);
            new CustomBrowser().addCustomBrowser(PaymentsActivity.this,customBrowserConfig , payUCustomBrowserCallback);
        }
    }

}
