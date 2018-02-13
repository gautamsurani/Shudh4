package com.shudh4sure.shopping.activity;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.adapter.WalletHistoryAdapter;
import com.shudh4sure.shopping.model.WalletModel;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Utils;

import java.util.ArrayList;

import retrofit.RestAdapter;

public class WalletHistory extends AppCompatActivity {

    Toolbar toolbar;
    Context context;
    Global global;
    SharedPreferences mprefs;
    ProgressDialog progressDialog;

    ArrayList<WalletModel> walletlist = new ArrayList<>();
    LinearLayoutManager mLayoutManager;
    RecyclerView rvWalletlist;
    ProgressBar progressBar1;
    int pagecode = 0;
    boolean IsLAstLoading = true;
    LinearLayout relativeMain;
    ProgressDialog loading;
    String strUserId = "", strMobile = "";
    TextView tvAddmoney, tvAvailablebal;
    String strWalletBalance = "";
    String activityType = "";
    private WalletHistoryAdapter walletHistoryAdapter;
    private LinearLayout lyt_not_found;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallethistory);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        strMobile = mprefs.getString(AppConstant.USER_MOBILE, null);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            activityType = bundle.getString("activityType");
        }
        initToolbar();
        initComponent();

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(WalletHistory.this, "", getResources().getString(R.string.string_plzwait), false, false);
            GetWalletHistoryDetails getMyWalletHistory = new GetWalletHistoryDetails();
            getMyWalletHistory.execute();

        } else {
            retryInternet();
        }

        mLayoutManager = new LinearLayoutManager(context);
        rvWalletlist.setLayoutManager(mLayoutManager);
        rvWalletlist.setHasFixedSize(true);
        walletHistoryAdapter = new WalletHistoryAdapter(WalletHistory.this, WalletHistory.this, walletlist);
        rvWalletlist.setAdapter(walletHistoryAdapter);


        rvWalletlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (IsLAstLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount &&
                                recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                            IsLAstLoading = false;
                            progressBar1.setVisibility(View.VISIBLE);
                            pagecode++;

                            GetWalletHistoryDetails getMyWalletHistory = new GetWalletHistoryDetails();
                            getMyWalletHistory.execute();

                        }
                    }
                }
            }
        });

        tvAddmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walletlist.clear();
                pagecode = 0;
                Intent intentWallet = new Intent(WalletHistory.this, WalletActivity.class);
                intentWallet.putExtra("WalletBalance", strWalletBalance);
                startActivity(intentWallet);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                /*Utils.showToastLong("Under Construction", WalletHistory.this);*/
            }
        });

    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_wallethistory));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        rvWalletlist = (RecyclerView) findViewById(R.id.rvWalletHistory);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        relativeMain = (LinearLayout) findViewById(R.id.relativeMain);
        tvAddmoney = (TextView) findViewById(R.id.tvAddmoney);
        tvAvailablebal = (TextView) findViewById(R.id.tvAvailablebal);

    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(WalletHistory.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(WalletHistory.this, "", getResources().getString(R.string.string_plzwait), false, false);
                    GetWalletHistoryDetails getMyWalletHistory = new GetWalletHistoryDetails();
                    getMyWalletHistory.execute();
                } else {
                    Utils.ShowSnakBar(getResources().getString(R.string.string_nointernet), relativeMain, WalletHistory.this);
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (global.isNetworkAvailable()) {
            walletlist.clear();
            loading = ProgressDialog.show(WalletHistory.this, "", "Please wait...", false, false);
            GetWalletHistoryDetails getMyWalletHistory = new GetWalletHistoryDetails();
            getMyWalletHistory.execute();

        } else {
            retryInternet();
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

        if (activityType.equalsIgnoreCase("WalletSuccess")) {
            Intent intent = new Intent(WalletHistory.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (activityType.equalsIgnoreCase("MyAccount")) {
            Intent intent = new Intent(WalletHistory.this, MyAccountActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            Intent intent = new Intent(WalletHistory.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetWalletHistoryDetails extends AsyncTask<Void, Void,
            Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
            //  walletlist.clear();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);

                return methods.getWalletHistory("wallet_transction", strUserId, strMobile, Integer.toString(pagecode));
            } catch (Exception E) {

                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            IsLAstLoading = true;
            progressBar1.setVisibility(View.GONE);

            if (curators != null) {


                if (curators.msgcode.equals("0")) {
                    try {
                        strWalletBalance = curators.walletBal;
                        tvAvailablebal.setText(getResources().getString(R.string.Rs) + " " + strWalletBalance);

                        for (Api_Model.transction_data dataset : curators.transction_data) {
                            WalletModel walletModel;

                            walletModel = new WalletModel(dataset.OrderID, dataset.Remark, dataset.symbol,
                                    dataset.Amount, dataset.type, dataset.TransactionDate);
                            walletlist.add(walletModel);
                        }
                        walletHistoryAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    tvAvailablebal.setText(getResources().getString(R.string.Rs) + " " + "0");
                    if (walletlist.size() == 0) {
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }
                    Utils.ShowSnakBar(curators.message, relativeMain, WalletHistory.this);

                }
            }
        }
    }


}
