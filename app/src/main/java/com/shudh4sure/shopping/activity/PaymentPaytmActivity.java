package com.shudh4sure.shopping.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.Tools;
import com.shudh4sure.shopping.utils.Utils;


public class PaymentPaytmActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getSimpleName();
    ProgressDialog progressDialog;
    ProgressDialog loading;
    RelativeLayout relativeMain;
    Global global;
    SharedPreferences mprefs;
    String strUserId = "", strName = "", strMobile = "", strEmail = "";
    ProgressBar Pbar;
    TextView txtview;
    private WebView webView;
    private String userID = "";
    private String amount = "";
    private String status = "";
    private String msg1 = "";
    private String msg2 = "";

    private String orderID = "";
    private String payTmUrl = "http://www.viragtraders.com/sabzi18/sabzi/index.php?view=order_paytm&userID=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmoneypaytm);
        global = new Global(this);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);

        if (getIntent().getExtras() != null) {
            userID = getIntent().getExtras().getString("userID");
            amount = getIntent().getExtras().getString("Amount");
            orderID = getIntent().getExtras().getString("orderID");
        }
        payTmUrl = payTmUrl + userID + "&amount=" + amount + "&orderID=" + orderID;
        //Log.e("PaymentpayTmUrl:", payTmUrl);
        initToolbar();
        Tools.systemBarLolipop(this);


        txtview = (TextView) findViewById(R.id.tV1);
        Pbar = (ProgressBar) findViewById(R.id.pB1);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(PaymentPaytmActivity.this);
        progressDialog.setMessage("Please wait..");

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && Pbar.getVisibility() == ProgressBar.GONE) {
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                    txtview.setVisibility(View.VISIBLE);

                }
                Pbar.setProgress(progress);
                if (progress == 100) {

                    Pbar.setVisibility(ProgressBar.GONE);
                    txtview.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            // If you will not use this method url links are opeen in new brower
            // not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //    Log.e(TAG, url);
                view.loadUrl(url);
                return true;
            }

            // Show loader on url load
            public void onLoadResource(WebView view, String url) {
            }

            public void onPageFinished(WebView view, String url) {
                try {

                    //    Log.e("onPageFinished", "finish" + url);
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();

                    }

                    // if (url.contains("&payment_status=captured")) {
                    // List<NameValuePair> parameters = URLEncodedUtils.parse(
                    // new URI(url), "UTF-8");
                    // for (NameValuePair p : parameters) {
                    // System.out.println(p.getName());
                    // System.out.println(p.getValue());
                    // }
                    // }
                    if (url.contains("payment_status=success")) {
                        //   Log.e("Success", "Success");
                        Uri uri = Uri.parse(url);
                        String message1 = uri.getQueryParameter("msg1");
                        String message2 = uri.getQueryParameter("msg2");

                        Bundle b = new Bundle();
                        status = "captured";

                        b.putString("status", "success");
                        b.putString("msg1", message1);
                        b.putString("msg2", message2);
                        Intent i = getIntent(); // gets the intent that called
                        // this intent
                        i.putExtras(b);
                        setResult(Activity.RESULT_OK, i);
                        finish();
                    } else if (url.contains("payment_status=failed")) {
                        //    Log.e("failed", "failed");
                        Uri uri = Uri.parse(url);
                        String message1 = uri.getQueryParameter("msg1");
                        String message2 = uri.getQueryParameter("msg2");

                        Bundle b = new Bundle();
                        status = "failed";
                        b.putString("status", "failed");
                        b.putString("msg1", message1);
                        b.putString("msg2", message2);

                        Intent i = getIntent(); // gets the intent that called
                        // this intent
                        i.putExtras(b);
                        setResult(Activity.RESULT_OK, i);
                        finish();
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });

        // Javascript inabled on webview

//		mWebView.setWebViewClient(new WebViewClient());

        // Other webview options

//		webView.getSettings().setLoadWithOverviewMode(true);
//		webView.getSettings().setUseWideViewPort(true);
//		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//		webView.setScrollbarFadingEnabled(false);
//		webView.getSettings().setBuiltInZoomControls(true);
        // Load url in webview
        webView.loadUrl(payTmUrl);
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_payment));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(PaymentPaytmActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(PaymentPaytmActivity.this, "", "Please wait...", false, false);

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, PaymentPaytmActivity.this);
                }
            }
        });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack() && status.equals("")) {
            webView.goBack();


        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(PaymentPaytmActivity.this);
            builder.setTitle("Cancel Transaction");
            builder.setMessage("Pressing back would cancel your current transaction. Proceed to cancel?");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intensdfcvsdvct = new Intent(PaymentPaytmActivity.this, FailOrderActivity.class);
                    intensdfcvsdvct.putExtra("ordertype", "0");
                    intensdfcvsdvct.putExtra("msg1", "");
                    intensdfcvsdvct.putExtra("msg2", "");
                    startActivity(intensdfcvsdvct);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();


        }
    }


}
