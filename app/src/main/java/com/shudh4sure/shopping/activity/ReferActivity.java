package com.shudh4sure.shopping.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import retrofit.RestAdapter;


public class ReferActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView imgReferfriend;
    TextView tvReferAmount, tvReferTermsConditions, tvReferlink, tvReferlines;
    LinearLayout sharewp, sharefb, sharemail, sharmore;
    ProgressDialog loading;
    Global global;
    SharedPreferences mprefs;
    String strUserId = "";
    LinearLayout relativeMain;
    String strimage = "", strmessage = "", strshare_image = "", strref_key = "", stryou_get = "", stryou_friend_get = "";
    String WhichShareType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        global = new Global(this);
        initToolbar();
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        initComponent();

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(ReferActivity.this, "", "Please wait...", false, false);
            LoadReferDetail task = new LoadReferDetail();
            task.execute();
        } else {
            retryInternet();
        }

        tvReferlink.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() <= tvReferlink.getRight()) {
                        Utils.ShowSnakBar("Refer Link Copied", relativeMain, ReferActivity.this);
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", strmessage);
                        clipboard.setPrimaryClip(clip);
                        return true;
                    }
                }
                return true;
            }
        });

        sharewp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhichShareType = "WhatsApp";
                shareMore();
            }
        });
        sharefb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhichShareType = "facebook";
                shareMore();
            }
        });
        sharemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhichShareType = "gmail";
                shareMore();
            }
        });

        sharmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhichShareType = "All";
                shareMore();
            }
        });
    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_refer));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        imgReferfriend = (ImageView) findViewById(R.id.imgReferfriend);
        tvReferAmount = (TextView) findViewById(R.id.tvReferAmount);
        tvReferlink = (TextView) findViewById(R.id.tvReferlink);
        tvReferlines = (TextView) findViewById(R.id.tvReferlines);
        tvReferTermsConditions = (TextView) findViewById(R.id.tvReferTermsConditions);
        sharewp = (LinearLayout) findViewById(R.id.sharewp);
        sharefb = (LinearLayout) findViewById(R.id.sharefb);
        sharemail = (LinearLayout) findViewById(R.id.sharemail);
        sharmore = (LinearLayout) findViewById(R.id.sharmore);
        relativeMain = (LinearLayout) findViewById(R.id.relativeMain);
    }

    public void shareMore() {
        String ImgListPath = strimage;
        DownloadSelctedIMG d = new DownloadSelctedIMG();
        d.execute(ImgListPath);
    }

    private class LoadReferDetail extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.RferDetail("refer_friend", strUserId);
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
                        for (Api_Model.share_data dataset : curators.share_data) {
                            strimage = dataset.image;
                            strmessage = dataset.message;
                            strshare_image = dataset.share_image;
                            strref_key = dataset.ref_key;
                            stryou_get = dataset.you_get;
                            stryou_friend_get = dataset.you_friend_get;

                            Glide.with(getApplicationContext())
                                    .load(strimage)
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(R.drawable.ic_default_notification)
                                    .into(imgReferfriend);
                            tvReferAmount.setText(stryou_get + " And " + stryou_friend_get);
                            tvReferlink.setText(strref_key);
                            //  tvReferlines.setText("Both you and your friend get");
                        }

                    } catch (Exception e) {

                    }
                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, ReferActivity.this);
                }
            }
        }
    }

    class DownloadSelctedIMG extends AsyncTask<String, String, Void> {

        String ImgPath = "";
        TextView txtProgressText;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(ReferActivity.this);
            loading.setMessage("Please Wait..");
            loading.setCancelable(false);
            loading.show();
        }

        protected Void doInBackground(String... arg0) {
            File f = new File(arg0[0]);
            String filename = "Shudh4sure.jpg";
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/Shudh4sure/Event/"
            );
//							  				 + photoList.get(i).get(TAG_PhotoName)
//							  				);

            wallpaperDirectory.mkdirs();
            ImgPath = wallpaperDirectory.getPath() + filename;

            String myu_recivedimage = arg0[0];
            int count;
            try {
                URL myurl = new URL(myu_recivedimage);
                URLConnection conection = myurl.openConnection();

                int lenghtOfFile = conection.getContentLength();
                conection.connect();
                int filelength = conection.getContentLength();
                InputStream inpit = new BufferedInputStream(myurl.openStream());
                OutputStream output = new FileOutputStream(ImgPath);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = inpit.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                inpit.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (loading != null && loading.isShowing()) {
                loading.dismiss();
            }
            try {

                if (WhichShareType.equalsIgnoreCase("WhatsApp")) {
                    String shareBody = strmessage;
                    File file = new File(ImgPath);
                    Uri imageUri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setPackage("com.whatsapp");
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shudh4sure");
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Share on.."));

                } else if (WhichShareType.equalsIgnoreCase("gmail")) {
                    try {
                        String shareBody = strmessage;
                  /*Uri imageUri = Uri.parse(imgPath);
                    File file = new File(ImgPath);
                    Uri imageUri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setPackage("com.gmail");
                    intent.setType("image*//*");
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Share on.."));*/
                        File file = new File(ImgPath);
                        Uri imageUri = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shudh4sure");
                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(intent, "Share on.."));
                    } catch (Exception ex) {
                    }

                } else if (WhichShareType.equalsIgnoreCase("facebook")) {
                    try {
                        String shareBody = strmessage;
                        File file = new File(ImgPath);
                        Uri imageUri = Uri.fromFile(file);

                        Intent intent = new Intent(Intent.ACTION_SEND);

                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shudh4sure");
                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(intent, "Share on.."));


                    } catch (Exception ex) {
                        //Log.e("fb", "msg:-" + ex.getMessage());
                    }

                } else {

                    String shareBody = strmessage;
                    File file = new File(ImgPath);
                    Uri imageUri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shudh4sure");
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Share on.."));
                }
            } catch (Exception e) {
            }
        }
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(ReferActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(ReferActivity.this, "", "Please wait...", false, false);
                    LoadReferDetail task = new LoadReferDetail();
                    task.execute();

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, ReferActivity.this);
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
