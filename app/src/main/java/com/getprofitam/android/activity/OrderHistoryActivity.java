package com.getprofitam.android.activity;


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

import com.getprofitam.android.R;
import com.getprofitam.android.adapter.OrderHistroyAdapter;
import com.getprofitam.android.model.MyOrderModel;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.IApiMethods;
import com.getprofitam.android.utils.Utils;

import java.util.ArrayList;

import retrofit.RestAdapter;

public class OrderHistoryActivity extends AppCompatActivity {

    Toolbar toolbar;

    Context context;
    Global global;
    SharedPreferences mprefs;
    ProgressDialog progressDialog;
    ArrayList<MyOrderModel> myOrderlist = new ArrayList<MyOrderModel>();
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerView;
    ProgressBar progressBar1;
    String subCatName = "";
    int pagecode = 0;
    boolean IsLAstLoading = true;
    RelativeLayout relativeMain;
    ProgressDialog loading;
    Dialog dialog;
    String strUserId = "";
    TextView tvmytotalitems, noooderss;
    String ordertype = "";
    String strActivitytype = "";
    private OrderHistroyAdapter myOrderAdapter;
    private LinearLayout lyt_not_found;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layouthistory);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strActivitytype = bundle.getString("IntentType");
        }

        initToolbar();
        initComponent();

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(OrderHistoryActivity.this, "", "Please wait...", false, false);
            GetMyOrderList mGetMyOrderlist = new GetMyOrderList();
            mGetMyOrderlist.execute();

        } else {
            retryInternet();
        }

        mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        myOrderAdapter = new OrderHistroyAdapter(OrderHistoryActivity.this, OrderHistoryActivity.this, myOrderlist);
        recyclerView.setAdapter(myOrderAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (IsLAstLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount &&
                                recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                            IsLAstLoading = false;
                            progressBar1.setVisibility(View.VISIBLE);
                            pagecode++;

                            GetMyOrderList mGetMyOrderlist = new GetMyOrderList();
                            mGetMyOrderlist.execute();
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });

    }


    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_orderhistory));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerProductList);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        noooderss = (TextView) findViewById(R.id.noooderss);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(OrderHistoryActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(OrderHistoryActivity.this, "", "Please wait...", false, false);
                    GetMyOrderList mGetMyOrderlist = new GetMyOrderList();
                    mGetMyOrderlist.execute();
                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, OrderHistoryActivity.this);
                }
            }
        });
        dialog.show();
    }
    private class GetMyOrderList extends AsyncTask<Void, Void,
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
                Api_Model curators = methods.MyOrderlist("orders", "list", strUserId, Integer.toString(pagecode));

                return curators;
            } catch (Exception E) {

                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            IsLAstLoading = true;
            progressBar1.setVisibility(View.GONE);
            if (curators == null) {

            } else {


                if (curators.msgcode.equals("0")) {
                    try {
                        for (Api_Model.order_list dataset : curators.order_list) {
                            MyOrderModel mMyordermodel;
                            mMyordermodel = new MyOrderModel(dataset.orderID, dataset.orderNo, dataset.date, dataset.status, dataset.amount);
                            myOrderlist.add(mMyordermodel);

                        }
                        myOrderAdapter.notifyDataSetChanged();

                    } catch (Exception e) {

                    }

                } else if (curators.msgcode.equals("2")) {
                    global.setPrefBoolean("Verify", false);
                    SharedPreferences preferences = getSharedPreferences(AppConstant.PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    if (myOrderlist.size() == 0) {
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }
                    Utils.ShowSnakBar(curators.message, relativeMain, OrderHistoryActivity.this);
                }
            }
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
        if (strActivitytype.equalsIgnoreCase("OrderSuccess")) {
            Intent intent = new Intent(OrderHistoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (strActivitytype.equalsIgnoreCase("MyAccount")) {
            Intent intent = new Intent(OrderHistoryActivity.this, MyAccountActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            Intent intent = new Intent(OrderHistoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

    }



}
