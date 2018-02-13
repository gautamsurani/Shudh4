package com.shudh4sure.shopping.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


public class ChangePasswordActivity extends AppCompatActivity {

    Context context;
    ProgressDialog progressDialog;
    TextView tvSubmitnewpassword;
    EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    SharedPreferences mprefs;
    ProgressDialog loading;
    String strUserId = "", strUserPhone = "";
    String currentpassword = "", newpassword = "";
    Global global;
    RelativeLayout relativeMain;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        getWindow().setBackgroundDrawableResource(R.drawable.ic_loginbg);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        strUserPhone = mprefs.getString(AppConstant.USER_MOBILE, null);

        initToolbar();
        inticomp();
        Tools.systemBarLolipop(this);
        tvSubmitnewpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePassword()) {
                    return;
                }
                if (!validateConfirmPassword()) {
                    return;
                }
                if (!validateNewConfirmPassword()) {
                    return;
                }

                if (!etNewPassword.getText().toString().equalsIgnoreCase(etConfirmNewPassword.getText().toString())) {
                    Utils.ShowSnakBar("Password not match", relativeMain, ChangePasswordActivity.this);

                } else {
                    if (global.isNetworkAvailable()) {
                        Utils.hideKeyboard(ChangePasswordActivity.this);
                        currentpassword = etCurrentPassword.getText().toString();
                        newpassword = etNewPassword.getText().toString();
                        loading = ProgressDialog.show(ChangePasswordActivity.this, "", "Please wait...", false, false);
                        ChangePasswordAsync mChangePassword = new ChangePasswordAsync();
                        mChangePassword.execute();
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
        toolbartitle.setText(getResources().getString(R.string.toolbar_change_password));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void inticomp() {
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
        tvSubmitnewpassword = (TextView) findViewById(R.id.tvChnaePassword);
        etCurrentPassword = (EditText) findViewById(R.id.etCurrentPassword);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etConfirmNewPassword = (EditText) findViewById(R.id.etConfirmNewPassword);

    }


    private boolean validatePassword() {

        if (etCurrentPassword.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Enter current password", relativeMain, ChangePasswordActivity.this);

            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword() {

        if (etNewPassword.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Enter new password", relativeMain, ChangePasswordActivity.this);

            return false;
        }
        return true;
    }

    private boolean validateNewConfirmPassword() {

        if (etConfirmNewPassword.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Confirm new password", relativeMain, ChangePasswordActivity.this);

            return false;
        }
        return true;
    }


    private class ChangePasswordAsync extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {

            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {

                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.ChangePassword("change_password", "change_password", strUserId, strUserPhone, currentpassword, newpassword);

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
                        Toast.makeText(ChangePasswordActivity.this, curators.message, Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } catch (Exception e) {
                    }
                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, ChangePasswordActivity.this);
                }
            }
        }
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(ChangePasswordActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(ChangePasswordActivity.this, "", "Please wait...", false, false);
                    ChangePasswordAsync mChangePassword = new ChangePasswordAsync();
                    mChangePassword.execute();
                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, ChangePasswordActivity.this);

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
        finish();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);


    }

}
