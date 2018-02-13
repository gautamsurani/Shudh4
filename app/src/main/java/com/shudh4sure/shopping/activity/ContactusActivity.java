package com.shudh4sure.shopping.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Tools;

import retrofit.RestAdapter;


public class ContactusActivity extends AppCompatActivity {

    Context context;
    String resCall = "";
    private TextView tvAddone, tvAddtwo, tvAddthree, tvAddemail, tvAddphone, tvAddwebsite, tvAddTimings;
    private Global global;
    private ProgressDialog loading;
    private ActionBar actionBar;
    private Button btnCallnow;
    // give preparation animation activity transition

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contactus);

        global = new Global(this);
        context = this;
        initToolbar();
        initComp();
        Tools.systemBarLolipop(this);

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(ContactusActivity.this, "", "Please wait...", false, false);
            ContactUsDetail task = new ContactUsDetail();
            task.execute();
        } else {
            retryInternet();
        }
    }

    private void initComp() {
        tvAddone = (TextView) findViewById(R.id.tvAddone);
        tvAddtwo = (TextView) findViewById(R.id.tvAddtwo);
        tvAddthree = (TextView) findViewById(R.id.tvAddthree);
        tvAddemail = (TextView) findViewById(R.id.tvAddemail);
        tvAddphone = (TextView) findViewById(R.id.tvAddphone);
        tvAddwebsite = (TextView) findViewById(R.id.tvAddwebsite);
        tvAddTimings = (TextView) findViewById(R.id.tvAddTimings);
        btnCallnow = (Button) findViewById(R.id.btnCallnow);
        btnCallnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:" + resCall));
                startActivity(call);
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_contactus));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class ContactUsDetail extends AsyncTask<Void, Void,
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
                Api_Model curators = methods.ContactUs("contact", "1");

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

                    for (final Api_Model.contact contact : curators.contact) {
                        resCall = contact.call;
                        tvAddone.setText(contact.address_line1);
                        tvAddtwo.setText(contact.address_line2);
                        tvAddthree.setText(contact.address_line3);
                        tvAddwebsite.setText(contact.website);
                        tvAddemail.setText(contact.email);
                        tvAddphone.setText(contact.phone);
                        tvAddTimings.setText(contact.time);
                    }
                }
            }}
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
                    loading = ProgressDialog.show(ContactusActivity.this, "", "Please wait...", false, false);
                    ContactUsDetail task = new ContactUsDetail();
                    task.execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(ContactusActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
