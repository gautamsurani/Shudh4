package com.shudh4sure.shopping.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.adapter.OfferZoneAdapter;
import com.shudh4sure.shopping.model.OfferModel;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Tools;
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
import java.util.ArrayList;

import retrofit.RestAdapter;


public class OffersActivity extends AppCompatActivity {


    public OfferZoneAdapter mUpdatesAdapter;
    Global global;
    RecyclerView rvOfferlist;
    RelativeLayout relativeMain;
    String strUserId = "", StrShreImageUrl = "", StrShareMsg = "";
    SharedPreferences mprefs;
    ProgressDialog loading;
    private ArrayList<OfferModel> userOfferList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        global = new Global(this);

        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

        initToolbar();
        initComponent();
        Tools.systemBarLolipop(this);

        if (global.isNetworkAvailable()) {

            loading = ProgressDialog.show(OffersActivity.this, "", "Please wait...", false, false);
            GetOfferList task = new GetOfferList();
            task.execute();
        } else {
            retryInternet();
        }

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(OffersActivity.this);
        rvOfferlist.setLayoutManager(mLayoutManager);
        rvOfferlist.setHasFixedSize(true);

        mUpdatesAdapter = new OfferZoneAdapter(OffersActivity.this, OffersActivity.this, userOfferList);
        rvOfferlist.setAdapter(mUpdatesAdapter);
        mUpdatesAdapter.setOnItemClickListener(new OfferZoneAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {
                OfferModel offerModel = userOfferList.get(position);
                if (which == 0) {
                    Intent intent = new Intent(OffersActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("catID", offerModel.getProduct());
                    intent.putExtra("catName", "Offers");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (which == 2) {
                    Intent in = new Intent(OffersActivity.this, OfferDetailsActivity.class);
                    in.putExtra("IMG", offerModel.getImage().trim());
                    startActivity(in);
                } else {
                    StrShreImageUrl = offerModel.getImage();
                    StrShareMsg = offerModel.getMessage();
                    shareMore();
                }
            }
        });
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_offers));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        rvOfferlist = (RecyclerView) findViewById(R.id.rvOfferlist);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(OffersActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(OffersActivity.this, "", "Please wait...", false, false);
                    GetOfferList task = new GetOfferList();
                    task.execute();

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, OffersActivity.this);
                }
            }
        });
        dialog.show();
    }

    public void shareMore() {

        String ImgListPath = StrShreImageUrl;
        DownloadSelctedIMG d = new DownloadSelctedIMG();
        d.execute(ImgListPath);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class GetOfferList extends AsyncTask<Void, Void,
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
                Api_Model curators = methods.getAllUserOffers("offer_list", strUserId);
                return curators;
            } catch (Exception E) {

                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();


            if (curators == null) {
                Utils.showToastShort("Connection Refused.. try again!", OffersActivity.this);
            } else {

                if (curators.msgcode.equals("0")) {
                    try {
                        for (Api_Model.offer_list dataset : curators.offer_list) {
                            OfferModel offerModel;
                            offerModel = new OfferModel(dataset.offer, dataset.product, dataset.image,
                                    dataset.message, dataset.added_on);
                            userOfferList.add(offerModel);
                        }
                        mUpdatesAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                    }
                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, OffersActivity.this);

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
            loading = new ProgressDialog(OffersActivity.this);
            loading.setMessage("Please Wait...");
            loading.setCancelable(false);
            loading.show();
        }

        protected Void doInBackground(String... arg0) {
            File f = new File(arg0[0]);
            String filename = "Shudh4sure.jpg";
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/Shudh4sure_new/Event/"
            );
//							  				 + photoList.get(i).get(TAG_PhotoName)
//							  				);

            wallpaperDirectory.mkdirs();
            ImgPath = wallpaperDirectory.getPath().toString() + filename;
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

                String shareBody = StrShareMsg;

                //Uri imageUri = Uri.parse(imgPath);
                File file = new File(ImgPath);
                Uri imageUri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Shudh4sure");
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Share on.."));
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (global.getPrefBoolean("Verify", false)) {
            Intent intent = new Intent(OffersActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {

            Intent intent = new Intent(OffersActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }


}
