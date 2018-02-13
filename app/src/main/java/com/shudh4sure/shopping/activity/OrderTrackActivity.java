package com.shudh4sure.shopping.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.model.OrderDetailsModel;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;

import java.util.ArrayList;

import retrofit.RestAdapter;

public class OrderTrackActivity extends AppCompatActivity {

    Global global;
    Context context;
    SharedPreferences mprefs;
    String strUserid = "";
    String strOrderId = "";
    Toolbar toolbar;
    ProgressDialog loading;
    ArrayList<OrderDetailsModel> orderDetailsList = new ArrayList<OrderDetailsModel>();

    TextView tvOrderDetailDate, tvOrderDetailDelDate, tvOrderDetailStatus, tvOrderDetailAmount,
            tvOrderDetailPayment, tvOrderDetailPayvalue, tvOrderDetailWalletvalue, tvOrderDetailQuantity,
            tvOrderDetailAddress, tvViewOrderItems;
    TextView toolbartitle;
    String StrGrand = "", StrShipping = "", StrSubtotal = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackorder);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserid = mprefs.getString(AppConstant.USER_ID, null);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strOrderId = bundle.getString("OrderId");
        }


        initToolbar();
        initComponent();


        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(OrderTrackActivity.this, "", "Please wait...", false, false);
            OrderDetail orderdetails = new OrderDetail();
            orderdetails.execute();
        } else {
            retryInternet();
        }
        tvViewOrderItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderTrackActivity.this, ViewItemsActivity.class);
                intent.putExtra("productName", strOrderId);

                intent.putExtra("productGrandtotal", StrGrand);
                intent.putExtra("productSubtotal", StrSubtotal);
                intent.putExtra("productShipping", StrShipping);

                intent.putExtra("imageListLarge", orderDetailsList);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });
    }


    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_orderstatus) + " - " + strOrderId);
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        tvOrderDetailDate = (TextView) findViewById(R.id.tvOrderDetailDate);
        tvOrderDetailDelDate = (TextView) findViewById(R.id.tvOrderDetailDelDate);
        tvOrderDetailStatus = (TextView) findViewById(R.id.tvOrderDetailStatus);
        tvOrderDetailAmount = (TextView) findViewById(R.id.tvOrderDetailAmount);

        tvOrderDetailPayment = (TextView) findViewById(R.id.tvOrderDetailPayment);
        tvOrderDetailPayvalue = (TextView) findViewById(R.id.tvOrderDetailPayvalue);
        tvOrderDetailWalletvalue = (TextView) findViewById(R.id.tvOrderDetailWalletvalue);
        tvOrderDetailQuantity = (TextView) findViewById(R.id.tvOrderDetailQuantity);
        tvViewOrderItems = (TextView) findViewById(R.id.tvViewOrderItems);
        tvOrderDetailAddress = (TextView) findViewById(R.id.tvOrderDetailAddress);
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
                    loading = ProgressDialog.show(OrderTrackActivity.this, "", "Please wait...", false, false);
                    OrderDetail orderdetails = new OrderDetail();
                    orderdetails.execute();
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private class OrderDetail extends AsyncTask<Void, Void,
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
                Api_Model curators = methods.OrderDetails("orders", "detail", strUserid, strOrderId);

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
                    tvOrderDetailDate.setText(curators.order_date);
                    tvOrderDetailDelDate.setText(curators.del_date);
                    tvOrderDetailPayment.setText(curators.order_type);
                    tvOrderDetailAmount.setText(getResources().getString(R.string.Rs) + " " + curators.grand_total);
                    tvOrderDetailPayvalue.setText(getResources().getString(R.string.Rs) + " " + curators.pay_value);
                    tvOrderDetailWalletvalue.setText(getResources().getString(R.string.Rs) + " " + curators.wallet);
                    tvOrderDetailQuantity.setText(curators.item);
                    tvOrderDetailAddress.setText(curators.address);

                    StrGrand = curators.grand_total.trim();
                    StrShipping = curators.shipping.trim();
                    StrSubtotal = curators.subtotal.trim();

                    if (curators.status.equalsIgnoreCase("Canceled")) {
                        tvOrderDetailStatus.setTextColor(getResources().getColor(R.color.red));
                        tvOrderDetailStatus.setText(curators.status);
                    } else {
                        tvOrderDetailStatus.setTextColor(getResources().getColor(R.color.green));
                        tvOrderDetailStatus.setText(curators.status);
                    }
                    for (final Api_Model.products_list dataset : curators.products_list) {
                        OrderDetailsModel orderDetailsModel;
                        orderDetailsModel = new OrderDetailsModel(dataset.SR, dataset.name, dataset.weight, dataset.quantity, dataset.price, dataset.line_total);
                        orderDetailsList.add(orderDetailsModel);
                    }

                }
            }

        }


    }


}
