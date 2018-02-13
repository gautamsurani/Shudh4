package com.getprofitam.android.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.getprofitam.android.R;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.IApiMethods;
import com.getprofitam.android.utils.Tools;

import retrofit.RestAdapter;


public class MyProfileActivity extends AppCompatActivity {

    public static final String EXTRA_OBJCT = "com.app.sample.chatting";
    Global global;
    Context context;
    SharedPreferences mprefs;
    String strUserId = "";
    String strName = "";
    String strMobile = "";
    String strUserImage = "";
    String strEmail = "";
    String strAddress = "";
    String strArea = "";
    String strCity = "";
    String strPincode = "";
    String strWallet = "";
    String strArea_id = "";
    String strWhatsapp_number = "";
    String strDOA = "";
    String strDOB = "";


    ProgressDialog loading;
    // give preparation animation activity transition
    String strCurrentDay = "";
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imgeditdetails;
    private ImageView imgUserprofile;
    private TextView etUserProfilename;
    private TextView etProfilenumber;
    private TextView etProfileemail;
    private TextView tvMyAddress;
    private TextView tvUseraddress;
    private RelativeLayout relEdit;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        strMobile = mprefs.getString(AppConstant.USER_MOBILE, null);
        strCurrentDay = global.getCurrentDay();

        //   strName = mprefs.getString(AppConstant.USER_NAME, null);

        //     strEmail = mprefs.getString(AppConstant.USER_EMAIl, null);


        // animation transition
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_OBJCT);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //   getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor((R.color.toolbarcolor))));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        initComp();
        Tools.systemBarLolipop(this);

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(MyProfileActivity.this, "", "Please wait...", false, false);
            GetUserInformation getUserInformation = new GetUserInformation();
            getUserInformation.execute();
        } else {
            retryInternet();
        }


        //     tvUseraddress.setText(getString(R.string.app_name));

        relEdit.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyProfileActivity.this, UpdateProfileActivity.class);
                intent.putExtra("resUserID", strUserId);
                intent.putExtra("resName", strName);
                intent.putExtra("resEmail", strEmail);
                intent.putExtra("resMobile", strMobile);
                intent.putExtra("resUserImage", strUserImage);
                intent.putExtra("resAddress", strAddress);
                intent.putExtra("resArea", strArea);
                intent.putExtra("resAreaId", strArea_id);
                intent.putExtra("resCityname", strCity);
                intent.putExtra("resPincode", strPincode);
                intent.putExtra("resWallet", strWallet);
                intent.putExtra("resWhatspp", strWhatsapp_number);
                intent.putExtra("resAnniversary", strDOA);
                intent.putExtra("resBirthdate", strDOB);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


            }
        });

    }

    public void initComp() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        imgeditdetails = (ImageView) findViewById(R.id.imgeditdetails);
        imgUserprofile = (ImageView) findViewById(R.id.imgUserprofile);
        etUserProfilename = (TextView) findViewById(R.id.tvUserProfilename);
        etProfilenumber = (TextView) findViewById(R.id.tvProfilenumber);
        etProfileemail = (TextView) findViewById(R.id.tvProfileemail);
        tvMyAddress = (TextView) findViewById(R.id.tvMyAddress);
        tvUseraddress = (TextView) findViewById(R.id.tvUseraddress);
        relEdit = (RelativeLayout) findViewById(R.id.relEdit);
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(MyProfileActivity.this, "", "Please wait...", false, false);
                    GetUserInformation getUserInformation = new GetUserInformation();
                    getUserInformation.execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(MyProfileActivity.this, "", "Please wait...", false, false);
            GetUserInformation getUserInformation = new GetUserInformation();
            getUserInformation.execute();
        } else {
            retryInternet();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
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
        /*Intent intent = new Intent(MyProfileActivity.this, MyAccountActivity.class);
        startActivity(intent);*/
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    private class GetUserInformation extends AsyncTask<Void, Void, Api_Model> {

        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.UserInfo("get_info", strUserId, strMobile, strCurrentDay);

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

                    for (final Api_Model.get_detail contact : curators.get_detail) {
                        strUserImage = contact.userimage;
                        strName = contact.name;
                        strMobile = contact.phone;
                        strEmail = contact.email;
                        strAddress = contact.address;
                        strArea = contact.area;
                        strArea_id = contact.area_id;
                        strCity = contact.city;
                        strAddress = contact.address;
                        strPincode = contact.pincode;
                        strWallet = contact.wallet;
                        strWhatsapp_number = contact.whatsapp_no;
                        strDOA = contact.mrg_anniversary;
                        strDOB = contact.date_of_birth;
                        Glide.with(context)
                                .load(strUserImage)
                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .placeholder(R.drawable.pic)
                                .into(imgUserprofile);
                        collapsingToolbarLayout.setTitle(strName);
                        etUserProfilename.setText(strName);
                        etProfilenumber.setText(strMobile);
                        etProfileemail.setText(strEmail);
                        tvMyAddress.setText(strAddress + ", " + strArea + ", " + strCity + " - " + strPincode);
                        mprefs.edit().putString(AppConstant.USER_ID, strUserId).apply();
                        mprefs.edit().putString(AppConstant.USER_NAME, strName).apply();
                        mprefs.edit().putString(AppConstant.USER_EMAIl, strEmail).apply();
                        mprefs.edit().putString(AppConstant.USER_MOBILE, strMobile).apply();
                        mprefs.edit().putString(AppConstant.USER_IMAGE, strUserImage).apply();

                        mprefs.edit().putString(AppConstant.USER_AREA, strArea).apply();
                        mprefs.edit().putString(AppConstant.USER_ADDRESS, strAddress).apply();
                        mprefs.edit().putString(AppConstant.USER_CITYNAME, strCity).apply();
                        mprefs.edit().putString(AppConstant.USER_PINCODE, strPincode).apply();
                        mprefs.edit().putString(AppConstant.USER_WHATSAPPNO, strWhatsapp_number).apply();
                        mprefs.edit().putString(AppConstant.USER_DOA, strDOA).apply();
                        mprefs.edit().putString(AppConstant.USER_DOB, strDOB).apply();


                    }
                    if (curators.data.equals("Yes")) {
                        for (final Api_Model.timeslot timeslot : curators.timeslot) {

                        }
                    }

                }
            }
        }

    }

}
