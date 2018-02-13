package com.getprofitam.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import com.getprofitam.android.R;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.Tools;
import com.getprofitam.android.utils.Utils;


public class AddMoneyPaytmActivity extends AppCompatActivity {


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
    private String payTmUrl = "http://www.getprofitam.com/sabzi/index.php?view=add_money&userID=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmoneypaytm);
        global = new Global(this);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        strName = mprefs.getString(AppConstant.USER_NAME, null);
        strMobile = mprefs.getString(AppConstant.USER_MOBILE, null);
        strEmail = mprefs.getString(AppConstant.USER_EMAIl, null);
        if (getIntent().getExtras() != null) {
            amount = getIntent().getExtras().getString("amt");
        }
        payTmUrl = payTmUrl + strUserId + "&page=add_money" + "&userPhone=" + strMobile + "&amount=" + amount + "&type=paytm";
        initToolbar();
        Tools.systemBarLolipop(this);
        txtview = (TextView) findViewById(R.id.tV1);
        Pbar = (ProgressBar) findViewById(R.id.pB1);
        final ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(AddMoneyPaytmActivity.this);
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
                view.loadUrl(url);
                return true;
            }

            // Show loader on url load
            public void onLoadResource(WebView view, String url) {
            }

            public void onPageFinished(WebView view, String url) {
                try {

                    //     Log.e("onPageFinished", "finish" + url);
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
                    if (url.contains("payment_status=captured")) {

                        Uri uri = Uri.parse(url);
                        String message1 = uri.getQueryParameter("msg1");
                        String message2 = uri.getQueryParameter("msg2");


                        Bundle b = new Bundle();
                        status = "captured";
                        b.putString("status", "captured");
                        b.putString("msg1", message1);
                        b.putString("msg2", message2);

                        Intent i = getIntent(); // gets the intent that called
                        // this intent
                        i.putExtras(b);
                        setResult(Activity.RESULT_OK, i);
                        finish();
                    } else if (url.contains("payment_status=userCancelled")) {
                        //   Log.e("userCancelled", "userCancelled");
                        Uri uri = Uri.parse(url);
                        String message1 = uri.getQueryParameter("msg1");
                        String message2 = uri.getQueryParameter("msg2");

                        Bundle b = new Bundle();
                        status = "userCancelled";
                        b.putString("status", "userCancelled");
                        b.putString("msg1", message1);
                        b.putString("msg2", message2);
                        Intent i = getIntent(); // gets the intent that called
                        // this intent
                        i.putExtras(b);
                        setResult(Activity.RESULT_OK, i);
                        finish();
                    } else if (url.contains("payment_status=failed")) {
                        //  Log.e("failed", "failed");
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
        toolbartitle.setText("");
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(AddMoneyPaytmActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(AddMoneyPaytmActivity.this, "", "Please wait...", false, false);

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, AddMoneyPaytmActivity.this);
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
        }
        else {
            /*Intent intent = new Intent(AddMoneyPaytmActivity.this, FailWalletActivity.class);
            intent.putExtra("msg1","");
            intent.putExtra("msg2","");
            intent.putExtra("wallettype","");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();*/
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
