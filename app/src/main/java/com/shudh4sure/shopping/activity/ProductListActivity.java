package com.shudh4sure.shopping.activity;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.adapter.ProductListNewAdapter;
import com.shudh4sure.shopping.dbhelper.Shudh4sureDB;
import com.shudh4sure.shopping.pojo.Data;
import com.shudh4sure.shopping.pojo.ProductList;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Constants;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Utils;

import java.util.ArrayList;

import retrofit.RestAdapter;

public class ProductListActivity extends AppCompatActivity {
    Toolbar toolbar;
    Context context;
    Global global;
    SharedPreferences mprefs;
    ProgressDialog progressDialog;
    ArrayList<ProductList> listOfProduct = new ArrayList<>();
    LinearLayoutManager mLayoutManager;
    RecyclerView rvProductList;
    ProgressBar progressBar1;
    int pagecode = 0;
    boolean IsLAstLoading = true;
    RelativeLayout relativeMain, relListGrid;
    ProgressDialog loading;
    Dialog dialog;
    String strUserId = "";
    TextView tvmytotalitems, noooderss, txtToastCountMsg;
    String ordertype = "";
    String strcatID = "", strcatName = "";
    Shudh4sureDB shudh4sureDB;
    Cursor cursorCart;
    LinearLayout linearShowToastMsg;
    TextView tvRefine;
    LinearLayout tvSortby;
    RadioButton rblowtohigh, rbhightolow, rbnameatoz, rbnameztoa;
    String filtertype = "";
    int visibleItemCount, totalItemCount, pastVisiblesItems;
    String itemCount = "";
    ImageView imglistgridview;
    GridLayoutManager gridLayoutManager;
    int ThisvisibleItemCount = 0;
    boolean isLayout = false;
    private ProductListNewAdapter productListNewAdapter;
    private LinearLayout lyt_not_found;
    Cursor cursorWish;
    ArrayList<String> WishLocalArrFOrStartView = new ArrayList<>();
    TextView totalCount;
    RelativeLayout stickLinear, resetRelative;
    TextView toolbartitle;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlistnew);

        context = this;
        global = new Global(context);

        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, "");

        String LocalDbUserSocityIdName = strUserId + "_Shudh4sure_local.db";
        String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
        shudh4sureDB = new Shudh4sureDB(ProductListActivity.this, FinalLocalDBName);
        shudh4sureDB.OpenDB();
        cursorCart = shudh4sureDB.ShowTableCartList();

        cursorWish = shudh4sureDB.ShowTableWishList();
        initToolbar();
        initComponent();
        if (cursorWish.getCount() > 0) {
            for (cursorWish.moveToFirst(); !cursorWish.isAfterLast(); cursorWish.moveToNext()) {
                WishLocalArrFOrStartView.add(cursorWish.getString(1));
            }
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strcatID = bundle.getString("catID");
            strcatName = bundle.getString("catName");
            toolbartitle.setText(strcatName);
        }
        tvmytotalitems.setText("" + cursorCart.getCount() + "");

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(ProductListActivity.this, "", "Please wait...", false, false);
            ProductListAsync mGetMyOrderList = new ProductListAsync();
            mGetMyOrderList.execute();
        } else {
            retryInternet();
        }
        ProductListNewAdapter.viewFormatProductList = 1;
        gridLayoutManager = new GridLayoutManager(context, 2);
        mLayoutManager = new LinearLayoutManager(context);
        rvProductList.setLayoutManager(gridLayoutManager);
        rvProductList.setHasFixedSize(true);
        rvProductList.addItemDecoration(new Constants.SimpleDividerItemDecoration(getApplicationContext()));
        ((SimpleItemAnimator) rvProductList.getItemAnimator()).setSupportsChangeAnimations(false);
        productListNewAdapter = new ProductListNewAdapter(ProductListActivity.this, ProductListActivity.this, listOfProduct, WishLocalArrFOrStartView);
        rvProductList.setAdapter(productListNewAdapter);

        rvProductList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                linearShowToastMsg.setVisibility(View.VISIBLE);
                if (dy > 0) {
                    if (ProductListNewAdapter.viewFormatProductList == 0) {
                        ThisvisibleItemCount = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if (ThisvisibleItemCount != -1) {
                            txtToastCountMsg.setText("Showing " + String.valueOf(ThisvisibleItemCount + "/" + itemCount + " items"));
                        }

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                linearShowToastMsg.setVisibility(View.GONE);
                            }
                        }, 3000);

                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    } else {
                        ThisvisibleItemCount = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if (ThisvisibleItemCount != -1) {
                            txtToastCountMsg.setText("Showing " + String.valueOf(ThisvisibleItemCount + "/" + itemCount + " items"));
                        }

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                linearShowToastMsg.setVisibility(View.GONE);
                            }
                        }, 3000);

                        visibleItemCount = gridLayoutManager.getChildCount();
                        totalItemCount = gridLayoutManager.getItemCount();
                        pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
                    }

                    if (IsLAstLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                            IsLAstLoading = false;
                            progressBar1.setVisibility(View.VISIBLE);
                            pagecode++;

                            ProductListAsync mGetMyOrderlist = new ProductListAsync();
                            mGetMyOrderlist.execute();
                        }
                    }
                } else {
                    if (ProductListNewAdapter.viewFormatProductList == 0) {
                        ThisvisibleItemCount = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if (ThisvisibleItemCount != -1) {
                            txtToastCountMsg.setText("Showing " + String.valueOf(ThisvisibleItemCount + "/" + itemCount + " items"));
                        }

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                linearShowToastMsg.setVisibility(View.GONE);
                            }
                        }, 3000);
                    } else {
                        ThisvisibleItemCount = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if (ThisvisibleItemCount != -1) {
                            txtToastCountMsg.setText("Showing " + String.valueOf(ThisvisibleItemCount + "/" + itemCount + " items"));
                        }

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                linearShowToastMsg.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                }
            }
        });

        productListNewAdapter.setOnItemClickListener(new ProductListNewAdapter.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(int position, View view, int i) {
                switch (i) {
                    case 0:
                        break;
                }
                if (i == 1) {
                    Cursor c = shudh4sureDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");
                    Toast.makeText(ProductListActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                } else if (i == 2) {
                    Cursor c = shudh4sureDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");
                } else if (i == 3) {
                    Cursor c = shudh4sureDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");
                }
            }
        });

        linearShowToastMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvProductList.scrollToPosition(0);
            }
        });

        tvRefine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog();
            }
        });

        tvSortby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortByPriceDialog();
            }
        });
        relListGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProductListNewAdapter.viewFormatProductList == 1) {
                    ProductListNewAdapter.viewFormatProductList = 0;
                    rvProductList.setLayoutManager(mLayoutManager);
                    imglistgridview.setImageDrawable(getResources().getDrawable(R.drawable.ic_listitemlist));
                    productListNewAdapter.notifyDataSetChanged();
                    rvProductList.scrollToPosition(ThisvisibleItemCount);
                    isLayout = false;
                } else {
                    ProductListNewAdapter.viewFormatProductList = 1;
                    rvProductList.setLayoutManager(gridLayoutManager);
                    imglistgridview.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_item_small));
                    productListNewAdapter.notifyDataSetChanged();
                    rvProductList.scrollToPosition(ThisvisibleItemCount);
                    isLayout = true;
                }
            }
        });
    }


    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        tvmytotalitems = (TextView) toolbar.findViewById(R.id.tvmytotalitems);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        relMyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(ProductListActivity.this, CartActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        imgSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ProductListActivity.this, SearchActivity.class);
                startActivity(it);
            }
        });

        toolbartitle.setText(strcatName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        rvProductList = (RecyclerView) findViewById(R.id.rvProductlist);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        noooderss = (TextView) findViewById(R.id.noooderss);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
        linearShowToastMsg = (LinearLayout) findViewById(R.id.linearShowToastMsg);
        txtToastCountMsg = (TextView) findViewById(R.id.txtToastCountMsg);
        tvRefine = (TextView) findViewById(R.id.tvRefine);
        tvSortby = (LinearLayout) findViewById(R.id.tvSortby);
        relListGrid = (RelativeLayout) findViewById(R.id.relListGrid);
        imglistgridview = (ImageView) findViewById(R.id.imglistgridview);
        totalCount = (TextView) findViewById(R.id.totalCount);
        stickLinear = (RelativeLayout) findViewById(R.id.stickLinear);
        resetRelative = (RelativeLayout) findViewById(R.id.resetRelative);
        stickLinear.setVisibility(View.GONE);
        resetRelative.setVisibility(View.GONE);
    }

    private void FilterDialog() {
        if (ProductListNewAdapter.viewFormatProductList == 0) {
            filtertype = "";
            visibleItemCount = mLayoutManager.getChildCount();
            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

            if ((visibleItemCount + pastVisiblesItems) >= 0) {
                filtertype = "";
                pagecode = 0;
                listOfProduct.clear();
                rvProductList.scrollToPosition(1);
                loading = ProgressDialog.show(ProductListActivity.this, "", "Please wait...", false, false);
                ProductListAsync mGetMyOrderList = new ProductListAsync();
                mGetMyOrderList.execute();
            }
        } else {
            filtertype = "";
            visibleItemCount = gridLayoutManager.getChildCount();
            pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

            if ((visibleItemCount + pastVisiblesItems) >= 0) {
                filtertype = "";
                pagecode = 0;
                listOfProduct.clear();
                rvProductList.scrollToPosition(1);
                loading = ProgressDialog.show(ProductListActivity.this, "", "Please wait...", false, false);
                ProductListAsync mGetMyOrderList = new ProductListAsync();
                mGetMyOrderList.execute();
            }
        }
    }

    private void SortByPriceDialog() {

        if (listOfProduct.size() == 0) {
            Utils.ShowSnakBar("No Products Found", relativeMain, ProductListActivity.this);
        } else {
            dialog = new BottomSheetDialog(ProductListActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_filter);
            dialog.show();
            rblowtohigh = (RadioButton) dialog.findViewById(R.id.rblowtohigh);
            rbhightolow = (RadioButton) dialog.findViewById(R.id.rbhightolow);
            rbnameatoz = (RadioButton) dialog.findViewById(R.id.rbnameatoz);
            rbnameztoa = (RadioButton) dialog.findViewById(R.id.rbnameztoa);

            rblowtohigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    filtertype = "price_l_h";
                    pagecode = 0;
                    listOfProduct.clear();
                    rvProductList.scrollToPosition(1);
                    loading = ProgressDialog.show(ProductListActivity.this, "", "Please wait...", false, false);
                    ProductListAsync mGetMyOrderList = new ProductListAsync();
                    mGetMyOrderList.execute();
                    dialog.dismiss();
                }
            });

            rbhightolow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    filtertype = "price_h_l";
                    pagecode = 0;
                    listOfProduct.clear();
                    rvProductList.scrollToPosition(1);
                    loading = ProgressDialog.show(ProductListActivity.this, "", "Please wait...", false, false);
                    ProductListAsync mGetMyOrderList = new ProductListAsync();
                    mGetMyOrderList.execute();
                    dialog.dismiss();
                }
            });

            rbnameatoz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    filtertype = "name_a_z";
                    pagecode = 0;
                    listOfProduct.clear();
                    rvProductList.scrollToPosition(1);
                    loading = ProgressDialog.show(ProductListActivity.this, "", "Please wait...", false, false);
                    ProductListAsync mGetMyOrderList = new ProductListAsync();
                    mGetMyOrderList.execute();
                    dialog.dismiss();
                }
            });

            rbnameztoa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    filtertype = "name_z_a";
                    pagecode = 0;
                    listOfProduct.clear();
                    rvProductList.scrollToPosition(1);
                    loading = ProgressDialog.show(ProductListActivity.this, "", "Please wait...", false, false);
                    ProductListAsync mGetMyOrderList = new ProductListAsync();
                    mGetMyOrderList.execute();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        WishLocalArrFOrStartView.clear();
        cursorWish = shudh4sureDB.ShowTableWishList();
        if (cursorWish.getCount() > 0) {
            for (cursorWish.moveToFirst(); !cursorWish.isAfterLast(); cursorWish.moveToNext()) {
                WishLocalArrFOrStartView.add(cursorWish.getString(1));
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        cursorCart = shudh4sureDB.ShowTableCartList();
        tvmytotalitems.setText("" + cursorCart.getCount() + "");
        ProductListNewAdapter.viewFormatProductList = 1;
        rvProductList.setLayoutManager(gridLayoutManager);
        productListNewAdapter.notifyDataSetChanged();
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(ProductListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(ProductListActivity.this, "", "Please wait...", false, false);
                    ProductListAsync mGetMyOrderList = new ProductListAsync();
                    mGetMyOrderList.execute();
                } else {
                    Utils.ShowSnakBar("No internet available", relativeMain, ProductListActivity.this);
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
        ProductListNewAdapter.viewFormatProductList = 0;
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    private class ProductListAsync extends AsyncTask<Void, Void, Data> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Data doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                return methods.Productlist("product", strUserId, strcatID, Integer.toString(pagecode), filtertype);
            } catch (Exception E) {
                return null;
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Data curators) {
            loading.dismiss();
            IsLAstLoading = true;
            progressBar1.setVisibility(View.GONE);
            if (curators != null) {
                if (curators.getMsgcode().equals("0")) {
                    itemCount = curators.getTotal_product();
                    stickLinear.setVisibility(View.VISIBLE);
                    totalCount.setText(itemCount + " Products");
                    if (filtertype.equalsIgnoreCase("")) {
                        resetRelative.setVisibility(View.GONE);
                    } else {
                        resetRelative.setVisibility(View.VISIBLE);
                    }
                    listOfProduct.addAll(curators.getProductList());
                    productListNewAdapter.notifyDataSetChanged();
                } else {
                    if (listOfProduct.size() == 1) {
                        lyt_not_found.setVisibility(View.VISIBLE);
                        stickLinear.setVisibility(View.GONE);
                    }
                    Utils.ShowSnakBar(curators.getMessage(), relativeMain, ProductListActivity.this);
                }
            }
        }
    }
}