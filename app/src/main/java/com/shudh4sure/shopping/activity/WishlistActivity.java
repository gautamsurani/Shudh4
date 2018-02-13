package com.shudh4sure.shopping.activity;


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
import android.util.Log;
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
import com.shudh4sure.shopping.adapter.WishlistAdapter;
import com.shudh4sure.shopping.dbhelper.Shudh4sureDB;
import com.shudh4sure.shopping.pojo.Data;
import com.shudh4sure.shopping.pojo.ProductList;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Constants;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.RestAdapter;

public class WishlistActivity extends AppCompatActivity {

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
    Shudh4sureDB shudh4sureDB;
    Cursor cursorCart, cursorWishlist;
    LinearLayout linearShowToastMsg, lvSortandFilter;
    TextView tvRefine;
    RadioButton rblowtohigh, rbhightolow, rbnameatoz, rbnameztoa;
    String filtertype = "";
    int visibleItemCount, totalItemCount, pastVisiblesItems;
    String itemCount = "";
    boolean isLayout = false;
    ImageView imglistgridview;
    int currentAdapterSize = 0;
    GridLayoutManager gridLayoutManager;
    int ThisvisibleItemCount = 0;
    private WishlistAdapter productListNewAdapter;
    private LinearLayout lyt_not_found_wishlist;

    TextView totalCount;
    RelativeLayout stickLinear, resetRelative;
    LinearLayout tvSortby;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlistnew);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

        String LocalDbUserSocityIdName = strUserId + "_Shudh4sure_local.db";
        String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
        shudh4sureDB = new Shudh4sureDB(WishlistActivity.this, FinalLocalDBName);
        shudh4sureDB.OpenDB();
        cursorCart = shudh4sureDB.ShowTableCartList();
        cursorWishlist = shudh4sureDB.ShowTableWishList();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strcatID = bundle.getString("catID");
            strcatName = bundle.getString("catName");
        }


        initToolbar();
        initComponent();
        try {
            if (global.isNetworkAvailable()) {

                loading = ProgressDialog.show(WishlistActivity.this, "", "Please wait...", false, false);
                addToWishlistAsync mGetMyOrderlist = new addToWishlistAsync();
                mGetMyOrderlist.execute();
            } else {
                retryInternet();
            }

        } catch (Exception ex) {
        }

        gridLayoutManager = new GridLayoutManager(context, 2);
        mLayoutManager = new LinearLayoutManager(context);
        rvProductlist.setLayoutManager(mLayoutManager);
        rvProductlist.setHasFixedSize(true);
        rvProductlist.addItemDecoration(new Constants.SimpleDividerItemDecoration(getApplicationContext()));
        ((SimpleItemAnimator) rvProductlist.getItemAnimator()).setSupportsChangeAnimations(false);

        productListNewAdapter = new WishlistAdapter(WishlistActivity.this, WishlistActivity.this, listOfProduct);
        rvProductlist.setAdapter(productListNewAdapter);
        rvProductlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                linearShowToastMsg.setVisibility(View.VISIBLE);
                if (dy > 0) //check for scroll down
                {
                    if (WishlistAdapter.viewFormatProductList == 0) {
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

                            addToWishlistAsync mGetMyOrderlist = new addToWishlistAsync();
                            mGetMyOrderlist.execute();
                        }
                    }
                } else {
                    if (WishlistAdapter.viewFormatProductList == 0) {
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

        productListNewAdapter.setOnItemClickListener(new WishlistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int i) {
                if (i == 1) {
                    Cursor c = shudh4sureDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");
                    Toast.makeText(WishlistActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                } else if (i == 2) {
                    Cursor c = shudh4sureDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");
                } else if (i == 3) {
                    Cursor c = shudh4sureDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");
                } else if (i == 4) {
                    itemCount = String.valueOf(Integer.valueOf(itemCount) - 1);
                    shudh4sureDB.DeleteWishlistItems(listOfProduct.get(position).getProductID());
                    listOfProduct.remove(position);
                    productListNewAdapter.notifyDataSetChanged();
                    if (listOfProduct.size() == 0) {
                        lyt_not_found_wishlist.setVisibility(View.VISIBLE);
                    }
                    //generateJson();
                    //Log.e("cursorWishlist..", generateJson());

                   /* if (global.isNetworkAvailable()) {

                        loading = ProgressDialog.show(WishlistActivity.this, "", "Please wait...", false, false);
                        addToWishlistAsync mGetMyOrderlist = new addToWishlistAsync();
                        mGetMyOrderlist.execute();
                    } else {
                        Utils.ShowSnakBar("No internet availbale", relativeMain, WishlistActivity.this);
                    }*/
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

                if (WishlistAdapter.viewFormatProductList == 1) {
                    WishlistAdapter.viewFormatProductList = 0;
                    rvProductlist.setLayoutManager(mLayoutManager);
                    imglistgridview.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid_item_small));
                    productListNewAdapter.notifyDataSetChanged();
                    rvProductlist.scrollToPosition(ThisvisibleItemCount);
                    isLayout = false;
                } else {
                    WishlistAdapter.viewFormatProductList = 1;
                    rvProductlist.setLayoutManager(gridLayoutManager);
                    imglistgridview.setImageDrawable(getResources().getDrawable(R.drawable.ic_listitemlist));
                    Log.e("Grid.", String.valueOf(currentAdapterSize));
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
                Intent it = new Intent(WishlistActivity.this, CartActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        imgSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(WishlistActivity.this, SearchActivity.class);
                startActivity(it);
            }
        });

        toolbartitle.setText("My Wishlist");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        rvProductlist = (RecyclerView) findViewById(R.id.rvProductlist);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        lyt_not_found_wishlist = (LinearLayout) findViewById(R.id.lyt_not_found_wishlist);
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

        //  cartIMG=(ImageView)findViewById(R.id.cartIMG);
    }

    private String generateJson() {
        String json = "";

        try {
            JSONObject jObjJson = new JSONObject();

            JSONArray jArrayCart = new JSONArray();
            ArrayList<String> listWish = shudh4sureDB.getAllProduct();
            for (int i = 0; i < listWish.size(); i++) {
                JSONObject jOBj = new JSONObject();
                jOBj.put("p_id", listWish.get(i));
                jArrayCart.put(jOBj);
            }
            jObjJson.put("productID", jArrayCart);

            json = jObjJson.toString();


        } catch (Exception e) {

        }

        return json;
    }

    private void FilterDialog() {
        if (WishlistAdapter.viewFormatProductList == 0) {
            filtertype = "";
            visibleItemCount = mLayoutManager.getChildCount();
            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

            if ((visibleItemCount + pastVisiblesItems) >= 0) {
                filtertype = "";
                pagecode = 0;
                listOfProduct.clear();

                rvProductlist.scrollToPosition(1);
                loading = ProgressDialog.show(WishlistActivity.this, "", "Please wait...", false, false);
                addToWishlistAsync mGetMyOrderlist = new addToWishlistAsync();
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
                loading = ProgressDialog.show(WishlistActivity.this, "", "Please wait...", false, false);
                addToWishlistAsync mGetMyOrderlist = new addToWishlistAsync();
                mGetMyOrderlist.execute();

            }
        }

    }

    private void SortByPriceDialog() {

        if (listOfProduct.size() == 0) {
            Utils.ShowSnakBar("No Products Found", relativeMain, WishlistActivity.this);

        } else {
            dialog = new BottomSheetDialog(WishlistActivity.this);
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

                    loading = ProgressDialog.show(WishlistActivity.this, "", "Please wait...", false, false);
                    addToWishlistAsync mGetMyOrderlist = new addToWishlistAsync();
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
                    loading = ProgressDialog.show(WishlistActivity.this, "", "Please wait...", false, false);
                    addToWishlistAsync mGetMyOrderlist = new addToWishlistAsync();
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
                    loading = ProgressDialog.show(WishlistActivity.this, "", "Please wait...", false, false);
                    addToWishlistAsync mGetMyOrderlist = new addToWishlistAsync();
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
                    loading = ProgressDialog.show(WishlistActivity.this, "", "Please wait...", false, false);
                    addToWishlistAsync mGetMyOrderlist = new addToWishlistAsync();
                    mGetMyOrderlist.execute();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(WishlistActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(WishlistActivity.this, "", "Please wait...", false, false);
                    addToWishlistAsync mGetMyOrderlist = new addToWishlistAsync();
                    mGetMyOrderlist.execute();
                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, WishlistActivity.this);
                }
            }
        });
        dialog.show();
    }

    private class addToWishlistAsync extends AsyncTask<Void, Void, Data> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL)
                    .build();
        }

        @Override
        protected Data doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Data curators = methods.AddtoWishlist("wish_list", "wish_data_id", strUserId, Integer.toString(pagecode),
                        generateJson(), filtertype);
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
                    totalCount.setText(itemCount + " Products");

                    if (filtertype.equalsIgnoreCase("")) {
                        resetRelative.setVisibility(View.GONE);
                    } else {
                        resetRelative.setVisibility(View.VISIBLE);
                    }


                    listOfProduct.addAll(curators.getProductList());
                    productListNewAdapter.notifyDataSetChanged();
                } else {
                    if (listOfProduct.size() == 0) {
                        lyt_not_found_wishlist.setVisibility(View.VISIBLE);
                        stickLinear.setVisibility(View.GONE);
                    }
                    Utils.ShowSnakBar(curators.getMessage(), relativeMain, WishlistActivity.this);
                }


            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(new Intent(WishlistActivity.this, WishlistActivity.class));

        //    productListNewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursorCart = shudh4sureDB.ShowTableCartList();
        try {
            tvmytotalitems.setText("" + cursorCart.getCount() + "");
        } catch (Exception e) {
            e.printStackTrace();
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
        WishlistAdapter.viewFormatProductList = 0;
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }


}
