package com.shudh4sure.shopping.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Tools;
import com.shudh4sure.shopping.utils.Utils;

import retrofit.RestAdapter;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;
    TextView tvSubmitnewpassword;
    EditText etforgotemailphone;
    String StrUserNmae = "";
    Global global;
    RelativeLayout relativeMain;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgorpwd);
        getWindow().setBackgroundDrawableResource(R.drawable.ic_loginbg);
        global = new Global(this);
        FetchXMLID();
        tvSubmitnewpassword.setOnClickListener(this);
        Tools.systemBarLolipop(this);
    }
    private void FetchXMLID() {
        tvSubmitnewpassword = (TextView) findViewById(R.id.tvSubmitnewpassword);
        etforgotemailphone = (EditText) findViewById(R.id.etforgotemailphone);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
    }
    @Override
    public void onClick(View v) {

        if (global.isNetworkAvailable()) {
            if (!etforgotemailphone.getText().toString().equalsIgnoreCase("")) {
                StrUserNmae = etforgotemailphone.getText().toString();
                Utils.hideKeyboard(ForgotActivity.this);
                loading = ProgressDialog.show(ForgotActivity.this, "", "Please wait...", false, false);
                ForgotPassTask mForgotPassTask = new ForgotPassTask();
                mForgotPassTask.execute();
            } else {
                Utils.ShowSnakBar("Please Enter Email/Phone", relativeMain, ForgotActivity.this);
            }
        } else {
            retryInternet();
        }
    }
    private class ForgotPassTask extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;
        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(AppConstant.API_URL)
                    .build();
        }
        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.ForgotPassword("login", "forgot_password", StrUserNmae);

                return curators;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            if (curators != null) {
                if (curators.msgcode.equals("0")) {
                    Toast.makeText(ForgotActivity.this, curators.message, Toast.LENGTH_SHORT).show();
                    // Utils.ShowSnakBar(curators.message, relativeMain, ForgotActivity.this);
                    Intent mainIntent = new Intent(ForgotActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    Toast.makeText(ForgotActivity.this, curators.message, Toast.LENGTH_SHORT).show();
                    //  Utils.ShowSnakBar(curators.message, relativeMain, ForgotActivity.this);

                }
            }
        }
    }
    public void retryInternet() {
        final Dialog dialog = new Dialog(ForgotActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(ForgotActivity.this, "", "Please wait...", false, false);
                    ForgotPassTask mForgotPassTask = new ForgotPassTask();
                    mForgotPassTask.execute();

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, ForgotActivity.this);


                }
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}
