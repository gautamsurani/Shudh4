package com.shudh4sure.shopping.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.adapter.ViewItemsAdapter;
import com.shudh4sure.shopping.model.OrderDetailsModel;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.Tools;

import java.util.ArrayList;


public class ViewItemsActivity extends AppCompatActivity {


    public ViewItemsAdapter viewItemsAdapter;
    Global global;
    RecyclerView rvOfferlist;
    RelativeLayout relativeMain;
    LinearLayout orderStatusDetails;
    String strUserId = "", StrShreImageUrl = "", StrShareMsg = "";
    SharedPreferences mprefs;
    ProgressDialog loading;
    String strOrderid = "";
    TextView toolbartitle, tvSub, tvShipping, tvGrandTotal;
    private ArrayList<OrderDetailsModel> orderDetailsList = new ArrayList<>();
    String productGrandtotal = "", productSubtotal = "", productShipping = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewitems);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);


        initComponent();
        Tools.systemBarLolipop(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strOrderid = bundle.getString("productName");

            productGrandtotal = bundle.getString("productGrandtotal");
            productSubtotal = bundle.getString("productSubtotal");
            productShipping = bundle.getString("productShipping");

            orderDetailsList = (ArrayList<OrderDetailsModel>) bundle.getSerializable("imageListLarge");

            orderStatusDetails.setVisibility(View.VISIBLE);
            tvSub.setText(productSubtotal);
            tvShipping.setText(productShipping);
            tvGrandTotal.setText(productGrandtotal);


        }else {
            orderStatusDetails.setVisibility(View.GONE);
        }
        initToolbar();

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(ViewItemsActivity.this);
        rvOfferlist.setLayoutManager(mLayoutManager);
        rvOfferlist.setHasFixedSize(true);

        viewItemsAdapter = new ViewItemsAdapter(ViewItemsActivity.this, ViewItemsActivity.this, orderDetailsList);
        rvOfferlist.setAdapter(viewItemsAdapter);


    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_view_items) + " - " + strOrderid);
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

        tvSub = (TextView) findViewById(R.id.tvSub);
        tvShipping = (TextView) findViewById(R.id.tvShipping);
        tvGrandTotal = (TextView) findViewById(R.id.tvGrandTotal);

        orderStatusDetails = (LinearLayout) findViewById(R.id.orderStatusDetails);
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
