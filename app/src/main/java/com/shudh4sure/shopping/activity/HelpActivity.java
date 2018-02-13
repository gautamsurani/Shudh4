package com.shudh4sure.shopping.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Tools;
import com.shudh4sure.shopping.utils.Utils;

import retrofit.RestAdapter;


public class HelpActivity extends AppCompatActivity implements View.OnClickListener {


    ProgressDialog progressDialog;
    String StredtName, StredtEmail, StredtPhone, StredtMsg;
    ProgressDialog loading;
    RelativeLayout relativeMain;
    Global global;
    String strIntentType = "";
    SharedPreferences mprefs;
    String strUserId = "", strName = "", strMobile = "", strUserImage = "", strEmail = "";
    private EditText edtName, edtEmail, edtPhone, edtMsg;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        global = new Global(this);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        strName = mprefs.getString(AppConstant.USER_NAME, null);
        strMobile = mprefs.getString(AppConstant.USER_MOBILE, null);
        strEmail = mprefs.getString(AppConstant.USER_EMAIl, null);
        initToolbar();
        initComponent();

        btnSubmit.setOnClickListener(this);
        Tools.systemBarLolipop(this);
        edtName.setText(strName);
        edtPhone.setText(strMobile);
        edtEmail.setText(strEmail);

    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_help));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        edtName = (EditText) findViewById(R.id.edtName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtMsg = (EditText) findViewById(R.id.edtMsg);
        btnSubmit = (Button) findViewById(R.id.btnSubmitComplaint);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
    }

    @Override
    public void onClick(View v) {

        StredtName = edtName.getText().toString();
        StredtEmail = edtEmail.getText().toString();
        StredtPhone = edtPhone.getText().toString();
        StredtMsg = edtMsg.getText().toString();

        if (TextUtils.isEmpty(StredtName)) {

            Utils.ShowSnakBar("Enter Name", relativeMain, HelpActivity.this);
        } else if (TextUtils.isEmpty(StredtEmail)) {
            Utils.ShowSnakBar("Enter Email", relativeMain, HelpActivity.this);
        } else if (!Utils.isValidEmail(StredtEmail)) {
            Utils.ShowSnakBar("Invalid Email", relativeMain, HelpActivity.this);
        } else if (TextUtils.isEmpty(StredtPhone)) {
            Utils.ShowSnakBar("Enter Phone", relativeMain, HelpActivity.this);
        } else if (StredtPhone.length() != 10) {
            Utils.ShowSnakBar("Invalid Phone", relativeMain, HelpActivity.this);
        } else if (TextUtils.isEmpty(StredtMsg)) {
            Utils.ShowSnakBar("Enter Message", relativeMain, HelpActivity.this);
        } else {
            if (global.isNetworkAvailable()) {
                Utils.hideKeyboard(HelpActivity.this);
                loading = ProgressDialog.show(HelpActivity.this, "", "Please wait...", false, false);
                HelpAsync task = new HelpAsync();
                task.execute();
            } else {
                retryInternet();
            }
        }
    }



    private class HelpAsync extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.HelpUsers("help", strUserId, StredtName, StredtEmail, StredtPhone, StredtMsg);

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(HelpActivity.this);
                        builder.setMessage(curators.message);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();

                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                        // Toast.makeText(getApplicationContext(),curators.message,Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {

                    }

                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, HelpActivity.this);
                }
            }
        }
    }
    public void retryInternet() {
        final Dialog dialog = new Dialog(HelpActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(HelpActivity.this, "", "Please wait...", false, false);
                    HelpAsync task = new HelpAsync();
                    task.execute();

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, HelpActivity.this);

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
        Utils.hideKeyboard(HelpActivity.this);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}
