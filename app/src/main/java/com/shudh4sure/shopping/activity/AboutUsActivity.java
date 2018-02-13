package com.shudh4sure.shopping.activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Tools;
import retrofit.RestAdapter;
public class AboutUsActivity extends AppCompatActivity {
    Global global;
    Context context;
    SharedPreferences mprefs;
    String strUserId = "";
    ImageView viewfb, viewgplus, viewin, viewinstagram, viewtwitter;
    TextView tvAboutusline;
    ProgressDialog loading;
    ImageView imgAboutUsbanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        initToolbar();
        initComp();
        Tools.systemBarLolipop(this);

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(AboutUsActivity.this, "", getResources().getString(R.string.string_plzwait), false, false);
            AboutUsDetail task = new AboutUsDetail();
            task.execute();
        } else {
            retryInternet();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_aboutus));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComp() {
        imgAboutUsbanner = (ImageView) findViewById(R.id.imgAboutUsbanner);
        tvAboutusline = (TextView) findViewById(R.id.tvAboutusline);
        viewfb = (ImageView) findViewById(R.id.viewfb);
        viewgplus = (ImageView) findViewById(R.id.viewgplus);
        viewin = (ImageView) findViewById(R.id.viewin);
        viewinstagram = (ImageView) findViewById(R.id.viewinstagram);
        viewtwitter = (ImageView) findViewById(R.id.viewtwitter);
    }

    private class AboutUsDetail extends AsyncTask<Void, Void,
            Api_Model> {

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
                Api_Model curators = methods.AboutUs("about", strUserId);
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
                    for (final Api_Model.about dataset : curators.about) {
                        tvAboutusline.setText(Html.fromHtml(dataset.text));
                        Glide.with(getApplicationContext())
                                .load(dataset.image)
                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .placeholder(R.drawable.ic_launcher)
                                .into(imgAboutUsbanner);
                        // imageLoader.displayImage(dataset.image, aboutus_icon, options);
                        if (dataset.facebook_link.equalsIgnoreCase("")) {
                            viewfb.setVisibility(View.GONE);
                        } else {
                            viewfb.setVisibility(View.VISIBLE);
                            viewfb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataset.facebook_link));
                                        startActivity(browserIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        if (dataset.google_link.equalsIgnoreCase("")) {
                            viewgplus.setVisibility(View.GONE);

                        } else {
                            viewgplus.setVisibility(View.VISIBLE);
                            viewgplus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataset.google_link));
                                        startActivity(browserIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        if (dataset.linkdin_link.equalsIgnoreCase("")) {
                            viewin.setVisibility(View.GONE);

                        } else {
                            viewin.setVisibility(View.VISIBLE);
                            viewin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataset.linkdin_link));
                                        startActivity(browserIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        if (dataset.twitter_link.equalsIgnoreCase("")) {
                            viewtwitter.setVisibility(View.GONE);

                        } else {
                            viewtwitter.setVisibility(View.VISIBLE);
                            viewtwitter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataset.twitter_link));
                                        startActivity(browserIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        if (dataset.insta_link.equalsIgnoreCase("")) {
                            viewinstagram.setVisibility(View.GONE);
                        } else {
                            viewinstagram.setVisibility(View.VISIBLE);
                            viewinstagram.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataset.insta_link));
                                        startActivity(browserIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
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
                    loading = ProgressDialog.show(AboutUsActivity.this, "", getResources().getString(R.string.string_plzwait), false, false);
                    AboutUsDetail task = new AboutUsDetail();
                    task.execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();
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
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}
