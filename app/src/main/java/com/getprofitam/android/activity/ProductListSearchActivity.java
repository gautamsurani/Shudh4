package com.getprofitam.android.activity;


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

import com.getprofitam.android.R;
import com.getprofitam.android.adapter.ProductListNewAdapter;
import com.getprofitam.android.dbhelper.GetprofitamDB;
import com.getprofitam.android.pojo.Data;
import com.getprofitam.android.pojo.ProductList;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Constants;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.IApiMethods;
import com.getprofitam.android.utils.Utils;

import java.util.ArrayList;

import retrofit.RestAdapter;

public class ProductListSearchActivity extends AppCompatActivity {

    Toolbar toolbar;

    Context context;
    Global global;
    SharedPreferences mprefs;
    ProgressDialog progressDialog;

    ArrayList<ProductList> listOfProduct = new ArrayList<ProductList>();
    LinearLayoutManager mLayoutManager;
    RecyclerView rvProductlist;
    ProgressBar progressBar1;
    String subCatName = "";
    int pagecode = 0;
    boolean IsLAstLoading = true;
    RelativeLayout relativeMain, relListGrid;
    ProgressDialog loading;
    Dialog dialog;
    String strUserId = "";
    TextView tvmytotalitems, noooderss, txtToastCountMsg;
    String ordertype = "";
    String strcatID = "", strcatName = "";
    GetprofitamDB getprofitamDB;
    Cursor cursorCart;
    LinearLayout linearShowToastMsg, lvSortandFilter;
    GridLayoutManager gridLayoutManager;
    int ThisvisibleItemCount = 0;
    boolean isLayout = false;
    int visibleItemCount, totalItemCount, pastVisiblesItems;
    String itemCount = "";
    String filtertype = "";
    RadioButton rblowtohigh, rbhightolow, rbnameatoz, rbnameztoa;
    ImageView imglistgridview;
    TextView  tvRefine;
    int currentAdapterSize = 0;
    private ProductListNewAdapter productListNewAdapter;
    private LinearLayout lyt_not_found;
    Cursor cursorWish;
    ArrayList<String> WishLocalArrFOrStartView = new ArrayList<String>();


    TextView totalCount;
    RelativeLayout stickLinear,resetRelative;
    LinearLayout tvSortby;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist_search);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

        String LocalDbUserSocityIdName = strUserId + "_getprofitam_local.db";
        String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
        getprofitamDB = new GetprofitamDB(ProductListSearchActivity.this, FinalLocalDBName);
        getprofitamDB.OpenDB();
        cursorCart = getprofitamDB.ShowTableCartList();

        cursorWish = getprofitamDB.ShowTableWishList();

        if (cursorWish.getCount() > 0) {
            for (cursorWish.moveToFirst(); !cursorWish.isAfterLast(); cursorWish.moveToNext()) {
                WishLocalArrFOrStartView.add(cursorWish.getString(1));
            }
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strcatName = bundle.getString("catName");
        }

        initToolbar();
        initComponent();
        tvmytotalitems.setText("" + cursorCart.getCount() + "");

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(ProductListSearchActivity.this, "", "Please wait...", false, false);
            ProductListSearchAsync mGetMyOrderlist = new ProductListSearchAsync();
            mGetMyOrderlist.execute();

        } else {
            retryInternet();
        }
        gridLayoutManager = new GridLayoutManager(context, 2);
        mLayoutManager = new LinearLayoutManager(context);
        rvProductlist.setLayoutManager(mLayoutManager);
        rvProductlist.setHasFixedSize(true);
        rvProductlist.addItemDecoration(new Constants.SimpleDividerItemDecoration(getApplicationContext()));

        ((SimpleItemAnimator) rvProductlist.getItemAnimator()).setSupportsChangeAnimations(false);

        productListNewAdapter = new ProductListNewAdapter(ProductListSearchActivity.this, ProductListSearchActivity.this, listOfProduct,WishLocalArrFOrStartView);
        rvProductlist.setAdapter(productListNewAdapter);

        rvProductlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                linearShowToastMsg.setVisibility(View.VISIBLE);
                if (dy > 0) //check for scroll down
                {

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
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount &&
                                recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                            IsLAstLoading = false;
                            progressBar1.setVisibility(View.VISIBLE);
                            pagecode++;

                            ProductListSearchAsync mGetMyOrderlist = new ProductListSearchAsync();
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
            @Override
            public void onItemClick(int position, View view, int i) {
                if(i == 0){

                }
                if (i == 1) {
                    Cursor c = getprofitamDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");

                    Toast.makeText(ProductListSearchActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                } else if (i == 2) {
                    Cursor c = getprofitamDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");

                } else if (i == 3) {
                    Cursor c = getprofitamDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");

                }
            }
        });

        linearShowToastMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rvProductlist.scrollToPosition(0);
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
                    rvProductlist.setLayoutManager(mLayoutManager);
                    imglistgridview.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_item_small));
                    productListNewAdapter.notifyDataSetChanged();
                    rvProductlist.scrollToPosition(ThisvisibleItemCount);
                    isLayout = false;
                } else {
                    ProductListNewAdapter.viewFormatProductList = 1;
                    rvProductlist.setLayoutManager(gridLayoutManager);
                    imglistgridview.setImageDrawable(getResources().getDrawable(R.drawable.ic_listitemlist));
                    productListNewAdapter.notifyDataSetChanged();
                    rvProductlist.scrollToPosition(ThisvisibleItemCount);
                    isLayout = true;
                }
            }
        });


    }


    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        tvmytotalitems = (TextView) toolbar.findViewById(R.id.tvmytotalitems);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        relMyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(ProductListSearchActivity.this, CartActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        imgSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ProductListSearchActivity.this, SearchActivity.class);
                startActivity(it);
            }
        });

        toolbartitle.setText(" " + strcatName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        rvProductlist = (RecyclerView) findViewById(R.id.rvProductlist);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        noooderss = (TextView) findViewById(R.id.noooderss);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
        linearShowToastMsg = (LinearLayout) findViewById(R.id.linearShowToastMsg);
        txtToastCountMsg = (TextView) findViewById(R.id.txtToastCountMsg);
        lvSortandFilter = (LinearLayout) findViewById(R.id.lvSortandFilter);
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

                rvProductlist.scrollToPosition(1);
                loading = ProgressDialog.show(ProductListSearchActivity.this, "", "Please wait...", false, false);
                ProductListSearchAsync mGetMyOrderlist = new ProductListSearchAsync();
                mGetMyOrderlist.execute();
            }
        } else {
            filtertype = "";
            visibleItemCount = gridLayoutManager.getChildCount();
            pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

            if ((visibleItemCount + pastVisiblesItems) >= 0) {
                filtertype = "";
                pagecode = 0;
                listOfProduct.clear();

                rvProductlist.scrollToPosition(1);
                loading = ProgressDialog.show(ProductListSearchActivity.this, "", "Please wait...", false, false);
                ProductListSearchAsync mGetMyOrderlist = new ProductListSearchAsync();
                mGetMyOrderlist.execute();

            }
        }

    }

    private void SortByPriceDialog() {

        if (listOfProduct.size() == 0) {
            Utils.ShowSnakBar("No Products Found", relativeMain, ProductListSearchActivity.this);

        } else {
            dialog = new BottomSheetDialog(ProductListSearchActivity.this);
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
                    // loading.show();
                    rvProductlist.scrollToPosition(1);

                    loading = ProgressDialog.show(ProductListSearchActivity.this, "", "Please wait...", false, false);
                    ProductListSearchAsync mGetMyOrderlist = new ProductListSearchAsync();
                    mGetMyOrderlist.execute();

                    dialog.dismiss();
                }
            });

            rbhightolow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    filtertype = "price_h_l";
                    pagecode = 0;
                    listOfProduct.clear();
                    rvProductlist.scrollToPosition(1);
                    loading = ProgressDialog.show(ProductListSearchActivity.this, "", "Please wait...", false, false);
                    ProductListSearchAsync mGetMyOrderlist = new ProductListSearchAsync();
                    mGetMyOrderlist.execute();
                    dialog.dismiss();
                }
            });


            rbnameatoz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    filtertype = "name_a_z";
                    pagecode = 0;
                    listOfProduct.clear();
                    rvProductlist.scrollToPosition(1);
                    loading = ProgressDialog.show(ProductListSearchActivity.this, "", "Please wait...", false, false);
                    ProductListSearchAsync mGetMyOrderlist = new ProductListSearchAsync();
                    mGetMyOrderlist.execute();
                    dialog.dismiss();
                }
            });

            rbnameztoa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    filtertype = "name_z_a";
                    pagecode = 0;
                    listOfProduct.clear();
                    rvProductlist.scrollToPosition(1);
                    loading = ProgressDialog.show(ProductListSearchActivity.this, "", "Please wait...", false, false);
                    ProductListSearchAsync mGetMyOrderlist = new ProductListSearchAsync();
                    mGetMyOrderlist.execute();
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
        cursorWish = getprofitamDB.ShowTableWishList();

        if (cursorWish.getCount() > 0) {
            for (cursorWish.moveToFirst(); !cursorWish.isAfterLast(); cursorWish.moveToNext()) {
                WishLocalArrFOrStartView.add(cursorWish.getString(1));
            }
        }

        productListNewAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        cursorCart = getprofitamDB.ShowTableCartList();
        tvmytotalitems.setText("" + cursorCart.getCount() + "");
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(ProductListSearchActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(ProductListSearchActivity.this, "", "Please wait...", false, false);
                    ProductListSearchAsync mGetMyOrderlist = new ProductListSearchAsync();
                    mGetMyOrderlist.execute();
                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, ProductListSearchActivity.this);
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

    private class ProductListSearchAsync extends AsyncTask<Void, Void,
            Data> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Data doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Data curators = methods.ProductlistSearch("product", strUserId, "", Integer.toString(pagecode), "", strcatName);

                return curators;
            } catch (Exception E) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Data curators) {
            loading.dismiss();
            IsLAstLoading = true;
            progressBar1.setVisibility(View.GONE);

            if (curators == null) {

            } else {

                if (curators.getMsgcode().equals("0")) {
                    itemCount = curators.getTotal_product();

                    stickLinear.setVisibility(View.VISIBLE);
                    totalCount.setText(itemCount+" Products");

                    if (filtertype.equalsIgnoreCase("")){
                        resetRelative.setVisibility(View.GONE);
                    }else {
                        resetRelative.setVisibility(View.VISIBLE);
                    }


                    listOfProduct.addAll(curators.getProductList());
                    productListNewAdapter.notifyDataSetChanged();
                } else {
                    if (listOfProduct.size() == 0) {
                        lyt_not_found.setVisibility(View.VISIBLE);
                        stickLinear.setVisibility(View.GONE);
                    }
                    Utils.ShowSnakBar(curators.getMessage(), relativeMain, ProductListSearchActivity.this);
                }
            }
        }
    }


}
