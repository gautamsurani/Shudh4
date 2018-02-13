package com.getprofitam.android.activity;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getprofitam.android.R;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.IApiMethods;
import com.getprofitam.android.utils.Utils;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;

import retrofit.RestAdapter;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener {

    Global global;
    Toolbar toolbar;
    EditText etAmount;
    Button btnContinueWallet;
    TextView tv100, tv200, tv500, tv1000, tvWalletMoney;
    ImageView imgAddPaytm, imgAddPayUbizbtn;
    Button imgAddPayUbiz;
    String walletbala = "";
    ProgressDialog loading;
    LinearLayout relativeMain;
    SharedPreferences mprefs;
    String strUserId = "", strMobile = "";
    PayuHashes payuHashes;
    String strWalletBalance = "";
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    String payu_wallet_fail = "", payu_wallet_success = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Payu.setInstance(WalletActivity.this);
        global = new Global(this);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        strMobile = mprefs.getString(AppConstant.USER_MOBILE, null);


        initToolbar();
        initComponent();

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(WalletActivity.this, "", "Please wait...", false, false);
            GetWalletBalance getWalletBalance = new GetWalletBalance();
            getWalletBalance.execute();
        } else {
            retryInternetGetWalletBalance();
        }

        imgAddPayUbizbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(WalletActivity.this, "Under Construction...", Toast.LENGTH_SHORT).show();
                if (etAmount.getText().toString().equals("")) {
                    Toast.makeText(WalletActivity.this, "Please select the amount", Toast.LENGTH_SHORT).show();
                } else {
                    if (global.isNetworkAvailable()) {
                        loading = ProgressDialog.show(WalletActivity.this, "", "Please wait...", false, false);
                        AddWalletAsync mAddWalletMoney = new AddWalletAsync();
                        mAddWalletMoney.execute();
                    } else {
                        retryInternet();
                    }
                }
            }
        });
    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_addmoney));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        tv100 = (TextView) findViewById(R.id.tv100);
        tv200 = (TextView) findViewById(R.id.tv200);
        tv500 = (TextView) findViewById(R.id.tv500);
        tv1000 = (TextView) findViewById(R.id.tv1000);
        tvWalletMoney = (TextView) findViewById(R.id.tvWalletMoney);
        etAmount = (EditText) findViewById(R.id.etAmount);
        imgAddPayUbiz = (Button) findViewById(R.id.imgAddPayUbiz);
        imgAddPayUbizbtn = (ImageView) findViewById(R.id.imgAddPayUbizbtn);
        imgAddPaytm = (ImageView) findViewById(R.id.imgAddPaytm);
        btnContinueWallet = (Button) findViewById(R.id.btnContinueWallet);
        relativeMain = (LinearLayout) findViewById(R.id.relativeMain);
        tv100.setOnClickListener(this);
        tv200.setOnClickListener(this);
        tv500.setOnClickListener(this);
        tv1000.setOnClickListener(this);
        imgAddPayUbiz.setOnClickListener(this);
        imgAddPaytm.setOnClickListener(this);

        btnContinueWallet.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == tv100) {
            etAmount.setText("100");
        } else if (v == tv200) {
            etAmount.setText("200");
        } else if (v == tv500) {
            etAmount.setText("500");
        } else if (v == tv1000) {
            etAmount.setText("1000");
        } else if (v == imgAddPayUbiz) {
            Utils.showToastShort("Under Construction...", WalletActivity.this);
            //  Toast.makeText(WalletActivity.this, "Under Construction...", Toast.LENGTH_SHORT).show();
            /*if (etAmount.getText().toString().equals("")) {
                Toast.makeText(WalletActivity.this, "Please select the amount", Toast.LENGTH_SHORT).show();
            } else {
                if (global.isNetworkAvailable()) {
                    loading = ProgressDialog.show(WalletActivity.this, "", "Please wait...", false, false);
                    AddWalletAsync mAddWalletMoney = new AddWalletAsync();
                    mAddWalletMoney.execute();
                } else {
                    retryInternet();
                }
            }*/

        } else if (v == imgAddPaytm) {
            if (etAmount.getText().toString().equals("")) {
                Toast.makeText(WalletActivity.this, "Please select the amount", Toast.LENGTH_SHORT).show();
            } else {
                final Intent intent = new Intent(WalletActivity.this, AddMoneyPaytmActivity.class);
                intent.putExtra("amt", etAmount.getText().toString());
                startActivityForResult(intent, 1001);
                //  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //* intent.putExtra("id", Utils.userID);
//                intent.putExtra("amt", etAmount.getText().toString());
//                startActivityForResult(intent, 1001);

            }
        } else if (v == btnContinueWallet) {
            Intent intentWallet = new Intent(WalletActivity.this, MainActivity.class);
            startActivity(intentWallet);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }

    private class AddWalletAsync extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;
        String selectedAmount = "";

        @Override
        protected void onPreExecute() {

            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL)
                    .build();
            selectedAmount = etAmount.getText().toString().trim();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {

                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.AddWalletMoney("add_money", strUserId, "add_money", strMobile, selectedAmount, "payu");

                return curators;
            } catch (Exception E) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            if (curators == null) {

            } else {
                if (curators.msgcode.equals("0")) {
                    payu_wallet_fail = curators.payu_wallet_fail;
                    payu_wallet_success = curators.payu_wallet_success;
                    try {
                        for (Api_Model.add_money_data dataset : curators.add_money_data) {


                            Intent intent = new Intent(WalletActivity.this, com.payu.payuui.Activity.PayUBaseActivity.class);
                            mPaymentParams = new PaymentParams();
                            payuConfig = new PayuConfig();
                            payuHashes = new PayuHashes();
                            mPaymentParams.setKey("3lJHAf");
                            mPaymentParams.setAmount(dataset.amount);
                            mPaymentParams.setProductInfo(dataset.product_info);
                            mPaymentParams.setFirstName(dataset.first_name);
                            mPaymentParams.setEmail(dataset.email);
                            mPaymentParams.setTxnId(dataset.trans_id);
                            mPaymentParams.setSurl("http://www.getprofitam.com/sabzi/index.php?view=walletpayu_result");
                            mPaymentParams.setFurl("http://www.getprofitam.com/sabzi/index.php?view=walletpayu_result");
                            mPaymentParams.setUdf2("");
                            mPaymentParams.setUdf3("");
                            mPaymentParams.setUdf4("");
                            mPaymentParams.setUdf5("");
                            mPaymentParams.setUdf1(dataset.udf1);
                            mPaymentParams.setCardBin("");
                            mPaymentParams.setPhone(strMobile);
                            mPaymentParams.setUserCredentials(dataset.payu_key + ":" + dataset.email);
                            // payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);
                            payuConfig.setEnvironment(Integer.parseInt("0"));
                            payuHashes.setPaymentHash(dataset.payment_hash);

                            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(dataset.payment_related_details_for_mobile_sdk_hash);
                            payuHashes.setVasForMobileSdkHash(dataset.vas_for_mobile_sdk_hash);
                            mPaymentParams.setHash(dataset.payment_hash);
                            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                            intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
                            intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
                            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);


                        }
                    } catch (Exception e) {
                    }
                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, WalletActivity.this);

                }
            }
        }
    }

    private class GetWalletBalance extends AsyncTask<Void, Void,
            Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.getWalletHistory("wallet_transction", strUserId, strMobile, Integer.toString(0));

                return curators;
            } catch (Exception E) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            if (curators == null) {
            } else {
                if (curators.msgcode.equals("0")) {
                    try {
                        strWalletBalance = curators.walletBal;
                        tvWalletMoney.setText(getResources().getString(R.string.Rs) + " " + strWalletBalance);
                    } catch (Exception e) {
                    }
                } else {
                    tvWalletMoney.setText(getResources().getString(R.string.Rs) + " " + "0");
                    Utils.ShowSnakBar(curators.message, relativeMain, WalletActivity.this);

                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                //  new GetWalletBal();
                if (data.getStringExtra("status").equals("userCancelled")) {
                    Utils.displayDialog(getString(R.string.app_name), "Transaction Cancelled!", WalletActivity.this, false);
                } else if (data.getStringExtra("status").equals("captured")) {
                    Intent intent = new Intent(WalletActivity.this, SuccessWalletActivity.class);
                    intent.putExtra("msg1", data.getStringExtra("msg1"));
                    intent.putExtra("msg2", data.getStringExtra("msg2"));
                    intent.putExtra("wallettype", "PAYTM");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (data.getStringExtra("status").equals("failed")) {
                    Intent intent = new Intent(WalletActivity.this, FailWalletActivity.class);
                    intent.putExtra("msg1", data.getStringExtra("msg1"));
                    intent.putExtra("msg2", data.getStringExtra("msg2"));
                    intent.putExtra("wallettype", "PAYTM");

                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            }
        }
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null)
                    Log.e("Success", "Success");
                String[] itemsDate = payu_wallet_success.split("_");
                Intent intent = new Intent(WalletActivity.this, SuccessWalletActivity.class);
                intent.putExtra("wallettype", "PAYUWALLET");
                intent.putExtra("msg1", itemsDate[0] + "");
                intent.putExtra("msg2", itemsDate[1] + "");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                if (data != null)
                    Log.e("Fail", "Fail");
                String[] itemsDate = payu_wallet_fail.split("_");
                Intent intent = new Intent(WalletActivity.this, FailWalletActivity.class);
                intent.putExtra("wallettype", "PAYUWALLET");
                intent.putExtra("msg1", itemsDate[0] + "");
                intent.putExtra("msg2", itemsDate[1] + "");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        }
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(WalletActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(WalletActivity.this, "", "Please wait...", false, false);

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, WalletActivity.this);

                }
            }
        });
        dialog.show();
    }

    public void retryInternetGetWalletBalance() {
        final Dialog dialog = new Dialog(WalletActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(WalletActivity.this, "", "Please wait...", false, false);
                    GetWalletBalance getWalletBalance = new GetWalletBalance();
                    getWalletBalance.execute();
                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, WalletActivity.this);
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
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
