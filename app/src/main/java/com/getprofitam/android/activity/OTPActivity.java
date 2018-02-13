package com.getprofitam.android.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getprofitam.android.R;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.IApiMethods;
import com.getprofitam.android.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.RestAdapter;

public class OTPActivity extends AppCompatActivity {

    EditText etOne;

    public String strUserOTPOTP = "";
    // final StringBuilder sb = new StringBuilder();
    Global global;
    SharedPreferences mprefs;
    Toolbar toolbar;
    ImageView imgenterOtp;
    String strOne = "", strTwo = "", strThree = "", strFour = "";
    String reg_otp = "", reg_phone = "", reg_userid = "";
    TextView tvResendOtp;
    ProgressDialog loading;
    LinearLayout relativeMain;
    String strConcat = "";
    boolean IsName = true;
    String[] itemsDate;
    boolean IsGoToManul = true;
    private int OTP_TIME_OUT = 10000;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.ic_signupbg);
        global = new Global(this);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_otp);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            reg_otp = bundle.getString("reg_otp");
            reg_phone = bundle.getString("reg_phone");
            reg_userid = bundle.getString("reg_userid");
        }

        progressDialog = new ProgressDialog(OTPActivity.this);
        progressDialog.setMessage("Verifying mobile number...");
        progressDialog.show();
        progressDialog.setCancelable(true);


        initComponent();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                        IsGoToManul = false;
                        progressDialog.dismiss();

            }
        }, OTP_TIME_OUT);


        imgenterOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strOne = etOne.getText().toString().trim();
                if (strOne.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                }  else if (!strOne.equalsIgnoreCase(reg_otp)) {
                    Toast.makeText(getApplicationContext(), "OTP not match", Toast.LENGTH_SHORT).show();
                } else {

                    if (global.isNetworkAvailable()) {
                        Utils.hideKeyboard(OTPActivity.this);
                        loading = ProgressDialog.show(OTPActivity.this, "", "Please wait...", false, false);
                        OTPAsynTask mOtpasync = new OTPAsynTask();
                        mOtpasync.execute();

                    } else {
                        retryInternet();
                    }
                }

            }
        });

        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (global.isNetworkAvailable()) {
                    loading = ProgressDialog.show(OTPActivity.this, "", "Please wait...", false, false);
                    ResendOTPAsynTask mResendOTP = new ResendOTPAsynTask();
                    mResendOTP.execute();
                } else {
                    retryInternetResend();
                }
            }
        });

        IntentFilter intentFilter = null;
        intentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsReceiver, intentFilter);

    }
    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle myBundle = intent.getExtras();
            SmsMessage[] messages = null;
            if (myBundle != null) {
                final Object[] pdusObj = (Object[]) myBundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);


                    final Pattern p = Pattern.compile("(\\d{4})");
                    final Matcher m = p.matcher(message);
                    String TSrMsg = "";
                    if (m.find())
                    {
                        TSrMsg = m.group();
                    }

                    Log.d("message", message);
                    try {
                        if (senderNum.contains("GETPRO")) {
                            if (reg_otp.equalsIgnoreCase(TSrMsg)) {
                                if(IsGoToManul){
                                    IsGoToManul = false;
                                    Utils.hideKeyboard(OTPActivity.this);
                                    FillFourCharacterOTP(TSrMsg);
                                }
                            }
                            Log.d("otpmessage", TSrMsg);
                        }
                    } catch (Exception e) {
                    }
                }
            }

        }
    };
    public void FillFourCharacterOTP(String message) {

        Log.e("TSrMsg",message+"   ");
        try {
            progressDialog.dismiss();
            etOne.setText(message);
            Log.d("strUserOTPOTP", strUserOTPOTP);
        } catch (Exception e) {
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    private void initComponent() {
        etOne = (EditText) findViewById(R.id.etOne);
        relativeMain = (LinearLayout) findViewById(R.id.relativeMain);
        imgenterOtp = (ImageView) findViewById(R.id.imgenterOtp);
        tvResendOtp = (TextView) findViewById(R.id.tvResendOtp);
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(OTPActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(OTPActivity.this, "", "Please wait...", false, false);
                    OTPAsynTask mOtpasync = new OTPAsynTask();
                    mOtpasync.execute();

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, OTPActivity.this);
                }
            }
        });
        dialog.show();
    }

    public void retryInternetResend() {
        final Dialog dialog = new Dialog(OTPActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(OTPActivity.this, "", "Please wait...", false, false);
                    ResendOTPAsynTask mResendOTP = new ResendOTPAsynTask();
                    mResendOTP.execute();

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, OTPActivity.this);
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

    private class OTPAsynTask extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {

            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.GetOTPVerified("otp_verify","verify", reg_phone, reg_userid, reg_otp);

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
                        for (Api_Model.user_detail dataset : curators.user_detail) {
                            global.setPrefBoolean("Verify", true);
                            mprefs.edit().putString(AppConstant.USER_ID, dataset.userID).apply();
                            mprefs.edit().putString(AppConstant.USER_EMAIl, dataset.email).apply();
                            mprefs.edit().putString(AppConstant.USER_NAME, dataset.name).apply();
                            mprefs.edit().putString(AppConstant.USER_IMAGE, dataset.userimage).apply();
                            mprefs.edit().putString(AppConstant.USER_MOBILE, dataset.phone).apply();
                            Intent id = new Intent(OTPActivity.this, MainActivity.class);
                            startActivity(id);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();

                        }
                    } catch (Exception e) {
                    }

                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, OTPActivity.this);
                }
            }
        }
    }

    private class ResendOTPAsynTask extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {

            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }
        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.ResendOTPVerified("otp_verify", "send_otp", reg_phone, reg_userid);
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
                        reg_otp = curators.otp;
                    //    Toast.makeText(OTPActivity.this, reg_otp, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }

                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, OTPActivity.this);
                }
            }
        }
    }


}
