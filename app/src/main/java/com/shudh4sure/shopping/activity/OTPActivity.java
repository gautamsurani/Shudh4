package com.shudh4sure.shopping.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.RequestMethod;
import com.shudh4sure.shopping.utils.RestClient;
import com.shudh4sure.shopping.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.RestAdapter;

public class OTPActivity extends AppCompatActivity {

    EditText etOne;
    public String strUserOTPOTP = "";
    Global global;
    SharedPreferences mprefs;
    Toolbar toolbar;
    ImageView imgenterOtp;
    String strOne = "";
    String reg_otp = "", reg_phone = "", reg_userid = "";
    TextView tvResendOtp;
    ProgressDialog loading;
    LinearLayout relativeMain;
    boolean IsGoToManul = true;
    ProgressDialog progressDialog;
    String mobile_type;
    Dialog dialog;
    String Mobile = "", resMessage = "", resCode = "";

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
            mobile_type = bundle.getString("mobile_type");
        }

        if (mobile_type.equals("Yes")) {
            openPopup();
        }

        progressDialog = new ProgressDialog(OTPActivity.this);

        initComponent();

        IsGoToManul = false;

        imgenterOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strOne = etOne.getText().toString().trim();
                if (strOne.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else if (!strOne.equalsIgnoreCase(reg_otp)) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        }
        registerReceiver(smsReceiver, intentFilter);
    }

    private void openPopup() {
        dialog = new Dialog(OTPActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.setContentView(R.layout.add_number_layout);
        dialog.setCanceledOnTouchOutside(false);

        final EditText etMobile = (EditText) dialog.findViewById(R.id.etMobile);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mobile = etMobile.getText().toString();
                if (Mobile.equals("")) {
                    Toast.makeText(OTPActivity.this, "Enter Mobile No!!!", Toast.LENGTH_SHORT).show();
                } else {
                    new AddMobileNo().execute();
                }
            }
        });

        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class AddMobileNo extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OTPActivity.this);
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_URL + "/index.php?view=add_mobile&page=add_phone&userID=" + reg_userid
                    + "&phone=" + Mobile;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("Register", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("user_detail1");
                            JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                            if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                reg_userid = jsonObjectList.getString("userID");
                                reg_phone = jsonObjectList.getString("phone");
                                reg_otp = jsonObjectList.getString("otp");
                                /*phone = jsonObjectList.getString("type");*/
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (resCode.equalsIgnoreCase("0")) {
                Toast.makeText(OTPActivity.this, resMessage, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(OTPActivity.this, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle myBundle = intent.getExtras();
            if (myBundle != null) {
                final Object[] pdusObj = (Object[]) myBundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                    final Pattern p = Pattern.compile("(\\d{4})");
                    final Matcher m = p.matcher(message);
                    String TSrMsg = "";
                    if (m.find()) {
                        TSrMsg = m.group();
                    }

                    Log.d("message", message);
                    try {
                        if (senderNum.contains("SHUDHS")) {
                            if (reg_otp.equalsIgnoreCase(TSrMsg)) {
                                if (IsGoToManul) {
                                    IsGoToManul = false;
                                    Utils.hideKeyboard(OTPActivity.this);
                                    FillFourCharacterOTP(TSrMsg);
                                }
                            }
                            Log.d("otpmessage", TSrMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public void FillFourCharacterOTP(String message) {
        Log.e("TSrMsg", message + "   ");
        try {
            progressDialog.dismiss();
            etOne.setText(message);
            Log.d("strUserOTPOTP", strUserOTPOTP);
        } catch (Exception e) {
            e.printStackTrace();
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

    @SuppressLint("StaticFieldLeak")
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
                return methods.GetOTPVerified("otp_verify", "verify", reg_phone, reg_userid, reg_otp);
            } catch (Exception E) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            if (curators != null) {
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
                        e.printStackTrace();
                    }
                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, OTPActivity.this);
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
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
                return methods.ResendOTPVerified("otp_verify", "send_otp", reg_phone, reg_userid);
            } catch (Exception E) {

                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            if (curators != null) {
                if (curators.msgcode.equals("0")) {
                    try {
                        reg_otp = curators.otp;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, OTPActivity.this);
                }
            }
        }
    }
}
