package com.shudh4sure.shopping.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.adapter.BestNewProductAdapter;
import com.shudh4sure.shopping.dbhelper.Shudh4sureDB;
import com.shudh4sure.shopping.model.BestNewProductModel;
import com.shudh4sure.shopping.model.PricelistModel;
import com.shudh4sure.shopping.pojo.PriceList;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.SmallBang;
import com.shudh4sure.shopping.utils.Utils;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit.RestAdapter;


public class ProductDetailsActivity extends AppCompatActivity {

    Global global;
    SharedPreferences mprefs;
    String strUserId = "";
    ArrayList<String> ArrImgListSmall = new ArrayList<>();
    ArrayList<String> ArrImgListLarge = new ArrayList<>();

    ArrayList<String> ArrPrice = new ArrayList<>();

    ArrayList<String> Arrmrp = new ArrayList<>();
    ArrayList<String> ArrPriceIdList = new ArrayList<>();
    ArrayList<String> Arrweight = new ArrayList<>();
    ArrayList<String> Arrdis = new ArrayList<>();
    ArrayList<PricelistModel> ArrPriceList;
    String strProductimage = "";
    HorizontalScrollView horizontal;
    Toolbar toolbar;
    ViewPager pager;
    LinearLayout thumbnails, addingitemLinear;
    FrameLayout detailItemIMG;
    TextView detailItemName, des, prodDesc, detailItemlMrp, checkOut, tvDetailsAmount, detailItemNameHindi,
            pOptions, itmQUAN, detailItemPrice, tvDetailOutofStock, detailItemStock,
            tvDetailSelectedweight, tvmytotalitems, discount;
    Button pAddcartbtn;
    ImageView minusItem, plu;
    CustomListAdapter customListAdapter;
    Dialog dialog;
    int subCatQuanity = 0;
    RecyclerView rvRelatedProducts;
    BestNewProductAdapter relatedProducts;
    ArrayList<BestNewProductModel> relatedProductList = new ArrayList<>();
    ProgressDialog loading;
    RelativeLayout relativeMain;
    String strcatID = "", strcatName = "";
    String strPrice = "";
    String strMrp = "";
    String strPriceId = "";
    String strWeight = "";
    String strDiscount = "";
    String strCheckSoldOut = "";
    String strProductId = "", strProductCaption = "", strProductName = "";
    LinearLayout lvWeightCounter;
    ImageView imgAddtoWishlist;
    Shudh4sureDB shudh4sureDB;
    Cursor cursorCart, cursoWish;
    double TotalCount = 0;
    ArrayList<String> WishLocalArrFOrStartView = new ArrayList<>();
    GifImageView loading_image;
    SmallBang mSmallBang;
    ArrayList<String> ArrForvIEW = new ArrayList<>();
    private GalleryPagerAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail_view);
        global = new Global(ProductDetailsActivity.this);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

        String LocalDbUserSocityIdName = strUserId + "_Shudh4sure_local.db";
        String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
        shudh4sureDB = new Shudh4sureDB(ProductDetailsActivity.this, FinalLocalDBName);
        shudh4sureDB.OpenDB();
        cursorCart = shudh4sureDB.ShowTableCartList();
        cursoWish = shudh4sureDB.ShowTableWishList();
        for (cursoWish.moveToFirst(); !cursoWish.isAfterLast(); cursoWish.moveToNext()) {
            ArrForvIEW.add(cursoWish.getString(1));
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strcatID = bundle.getString("catID");
            strcatName = bundle.getString("catName");
            Log.e("product", "click:-" + strcatName);
        }

        initToolbar();
        initComponent();

        //  Layout Manager for Related products In Home Screen
        RecyclerView.LayoutManager mLayoutManagerNewProducts = new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvRelatedProducts.setLayoutManager(mLayoutManagerNewProducts);
        rvRelatedProducts.setHasFixedSize(true);

        // BestNewProductAdapter used for New Products
        relatedProducts = new BestNewProductAdapter(ProductDetailsActivity.this, relatedProductList, shudh4sureDB, WishLocalArrFOrStartView);
        rvRelatedProducts.setAdapter(relatedProducts);

        relatedProducts.setOnItemClickListener(new BestNewProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {

                BestNewProductModel homeCategoryModel = relatedProductList.get(position);
                if (which == 1) {
                    //     Utils.ShowSnakBar("Category "+homeCategoryModel.getName(), relativeMain, ProductDetailsActivity.this);
                    Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("catID", homeCategoryModel.getProductID());
                    intent.putExtra("catName", homeCategoryModel.getName());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (which == 2) {
                    Cursor c = shudh4sureDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");
                } else if (which == 3) {
                    Cursor c = shudh4sureDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");
                } else if (which == 4) {
                    Cursor c = shudh4sureDB.ShowTableCartList();
                    tvmytotalitems.setText(c.getCount() + "");
                    Toast.makeText(ProductDetailsActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // API to load Banners, Offer and Categories
        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(ProductDetailsActivity.this, "", "Please wait...", false, false);
            GetProductDetail getProductDetail = new GetProductDetail();
            getProductDetail.execute();
        } else {
            retryInternet();
        }

        tvmytotalitems.setText("" + cursorCart.getCount() + "");
        Cursor MyCurserThis = shudh4sureDB.ShowTableCartList();
        for (MyCurserThis.moveToFirst(); !MyCurserThis.isAfterLast(); MyCurserThis.moveToNext()) {
            TotalCount = TotalCount + (MyCurserThis.getInt(7) * Double.parseDouble(MyCurserThis.getString(5)));

        }

        if (TotalCount == 0) {
            tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + 0 + "");
        } else

        {
            tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");
        }


        pAddcartbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                // Log.e("First", "First");

                addingitemLinear.setVisibility(View.VISIBLE);
                pAddcartbtn.setVisibility(View.GONE);
                shudh4sureDB.InsertCartListData(strProductId, ArrPriceIdList.get(0), strProductName, ArrImgListSmall.get(0), ArrPrice.get(0), Arrmrp.get(0), 1, Arrweight.get(0), strProductCaption);
                itmQUAN.setText(String.valueOf(1));
                TotalCount = 0;
                Cursor c = shudh4sureDB.ShowTableCartList();
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                    // Double d= c.getDouble(7)*Double.parseDouble(c.getString(5));
                }

                tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");
                tvmytotalitems.setText(c.getCount() + "");
                Toast.makeText(ProductDetailsActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                //  findViewById(addingitemLinear).setVisibility(View.VISIBLE);
                Answers.getInstance().logAddToCart(new AddToCartEvent()
                        .putCurrency(Currency.getInstance("INR"))
                        .putItemName(strProductName)
                        .putItemType(strcatName)
                        .putItemId(strProductId));
            }
        });
        plu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                subCatQuanity = subCatQuanity + 1;
                itmQUAN.setText(String.valueOf(subCatQuanity));
                int counter = shudh4sureDB.getQuentyty(ArrPriceIdList.get(0));
                int total = counter + 1;
                shudh4sureDB.UpdateQuantityByPriceIdInDB(ArrPriceIdList.get(0), total);
                itmQUAN.setText(String.valueOf(total));
                TotalCount = 0;
                Cursor c = shudh4sureDB.ShowTableCartList();

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                }

                tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");

                Toast.makeText(ProductDetailsActivity.this, "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();

            }
        });


        minusItem.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                //   Log.e("FirstMinus", "FirstMinus");
                int counter = shudh4sureDB.getQuentyty(ArrPriceIdList.get(0));

                if (counter == 1) {
                    itmQUAN.setText(String.valueOf(counter));
                    pAddcartbtn.setVisibility(View.VISIBLE);
                    addingitemLinear.setVisibility(View.GONE);
                    shudh4sureDB.DeleteCartListByPriceID(ArrPriceIdList.get(0));
                    shudh4sureDB.UpdateQuantityByPriceIdInDB(ArrPriceIdList.get(0), 0);
                    TotalCount = 0;

                    Cursor c = shudh4sureDB.ShowTableCartList();
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                    }

                    tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");
                    tvmytotalitems.setText(c.getCount() + "");
                } else if (counter > 1) {
                    int total = counter - 1;
                    itmQUAN.setText(String.valueOf(total));
                    shudh4sureDB.UpdateQuantityByPriceIdInDB(ArrPriceIdList.get(0), total);
                    TotalCount = 0;
                    Cursor c = shudh4sureDB.ShowTableCartList();
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                    }
                    tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");

                    Toast.makeText(getApplicationContext(), "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursorCart = shudh4sureDB.ShowTableCartList();
                if (cursorCart.getCount() == 0) {
                    Toast.makeText(ProductDetailsActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(ProductDetailsActivity.this, AddressActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }

            }
        });

        pOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(ProductDetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                dialog.setContentView(R.layout.product_options_view);
                ListView packList = (ListView) dialog.findViewById(R.id.packList);

                customListAdapter = new CustomListAdapter(ArrPriceList, 0);
                packList.setAdapter(customListAdapter);
                customListAdapter.notifyDataSetChanged();
              /*  Log.e("packList", "packList");
                Log.e("_packsizes", String.valueOf(ArrPriceList.size()));*/
                dialog.show();
            }
        });

        boolean baa = ArrForvIEW.contains(strcatID);
        if (baa) {
            imgAddtoWishlist.setImageResource(R.drawable.f4);
        } else {
            imgAddtoWishlist.setImageResource(R.drawable.f0);

        }
        imgAddtoWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ArrayList<String> WishLocalArr = new ArrayList<String>();
                ArrForvIEW.clear();
                Cursor c = shudh4sureDB.ShowTableWishList();
                if (c.getCount() > 0) {
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        ArrForvIEW.add(c.getString(1));
                    }

                    boolean b = ArrForvIEW.contains(strcatID);
                    if (b) {
                        imgAddtoWishlist.setImageResource(R.drawable.f0);
                        shudh4sureDB.DeleteWishListByID(strcatID);
                        ArrForvIEW.remove(strcatID);
                    } else {
                        imgAddtoWishlist.setImageResource(R.drawable.f4);
                        mSmallBang.bang(view);
                        shudh4sureDB.InsertWishListData(strProductId, strProductName,
                                ArrImgListSmall.get(0), ArrPrice.get(0), Arrmrp.get(0), strProductCaption);
                        ArrForvIEW.add(strcatID);
                    }
                } else {
                    imgAddtoWishlist.setImageResource(R.drawable.f4);
                    mSmallBang.bang(view);
                    shudh4sureDB.InsertWishListData(strProductId, strProductName,
                            ArrImgListSmall.get(0), ArrPrice.get(0), Arrmrp.get(0), strProductCaption);
                    ArrForvIEW.add(strcatID);
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
                Intent it = new Intent(ProductDetailsActivity.this, CartActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        imgSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ProductDetailsActivity.this, SearchActivity.class);
                startActivity(it);
            }
        });
        toolbartitle.setText(strcatName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        pager = (ViewPager) findViewById(R.id.pager);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
        thumbnails = (LinearLayout) findViewById(R.id.thumbnails);
        detailItemName = (TextView) findViewById(R.id.detailItemName);
        detailItemNameHindi = (TextView) findViewById(R.id.detailItemNameHindi);
        lvWeightCounter = (LinearLayout) findViewById(R.id.lvWeightCounter);
        des = (TextView) findViewById(R.id.des);
        prodDesc = (TextView) findViewById(R.id.prodDesc);
        pOptions = (TextView) findViewById(R.id.pOptions);
        tvDetailsAmount = (TextView) findViewById(R.id.tvDetailsAmount);
        detailItemPrice = (TextView) findViewById(R.id.detailItemPrice);
        addingitemLinear = (LinearLayout) findViewById(R.id.addingitemLinear);
        detailItemlMrp = (TextView) findViewById(R.id.detailItemlMrp);
        horizontal = (HorizontalScrollView) findViewById(R.id.horizontal);
        detailItemStock = (TextView) findViewById(R.id.detailItemStock);
        tvDetailOutofStock = (TextView) findViewById(R.id.tvDetailOutofStock);
        tvDetailSelectedweight = (TextView) findViewById(R.id.tvDetailSelectedweight);
        discount = (TextView) findViewById(R.id.discount);
        detailItemIMG = (FrameLayout) findViewById(R.id.detailItemIMG);
        checkOut = (TextView) findViewById(R.id.checkOut);
        itmQUAN = (TextView) findViewById(R.id.itmQUAN);
        rvRelatedProducts = (RecyclerView) findViewById(R.id.rvRelatedProducts);
        minusItem = (ImageView) findViewById(R.id.minusItem);
        plu = (ImageView) findViewById(R.id.plu);
        adapter = new GalleryPagerAdapter(this);
        customListAdapter = new CustomListAdapter(ArrPriceList, 0);
        pAddcartbtn = (Button) findViewById(R.id.pAddcartbtn);
        loading_image = (GifImageView) findViewById(R.id.loading_image);
        imgAddtoWishlist = (ImageView) findViewById(R.id.imgAddtoWishlist);
        mSmallBang = SmallBang.attach2Window(ProductDetailsActivity.this);
        detailItemlMrp.setPaintFlags(detailItemlMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(ProductDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(ProductDetailsActivity.this, "", "Please wait...", false, false);
                    GetProductDetail getproductDetails = new GetProductDetail();
                    getproductDetails.execute();

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, ProductDetailsActivity.this);

                }
            }
        });
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onRestart() {
        super.onRestart();

        if (ArrPriceIdList.size() > 0) {
            if (shudh4sureDB.ifExistsPriceId(ArrPriceIdList.get(0))) {
                int counter = shudh4sureDB.getQuentyty(ArrPriceIdList.get(0));
                pOptions.setText(getResources().getString(R.string.Rs) + " " + ArrPrice.get(0) + "/" + Arrweight.get(0));
                itmQUAN.setText(String.valueOf(counter));
                pAddcartbtn.setVisibility(View.GONE);
                addingitemLinear.setVisibility(View.VISIBLE);
            } else {
                pAddcartbtn.setVisibility(View.VISIBLE);
                addingitemLinear.setVisibility(View.GONE);
            }

        } else {
            pAddcartbtn.setVisibility(View.VISIBLE);
            addingitemLinear.setVisibility(View.GONE);
        }


      /*  Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            strcatID = bundle.getString("catID");
            strcatName = bundle.getString("catName");
        }
        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(ProductDetailsActivity.this, "", "Please wait...", false, false);
            GetProductDetail getProductDetail = new GetProductDetail();
            getProductDetail.execute();
        } else {
            retryInternet();
        }*/

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        cursorCart = shudh4sureDB.ShowTableCartList();
        tvmytotalitems.setText("" + cursorCart.getCount() + "");
        TotalCount = 0;
        for (cursorCart.moveToFirst(); !cursorCart.isAfterLast(); cursorCart.moveToNext()) {
            TotalCount = TotalCount + (cursorCart.getInt(7) * Double.parseDouble(cursorCart.getString(5)));
        }
        if (TotalCount == 0) {
            tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + 0 + "");
        } else {
            tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetProductDetail extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();

        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators;
                curators = methods.RelatedProducts("product_detail", strUserId, strcatID);
                return curators;
            } catch (Exception E) {

                return null;
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Api_Model curators) {
            if (loading != null && loading.isShowing()) {
                loading.dismiss();
            }
            ArrPriceList = new ArrayList<>();
            ArrPriceIdList = new ArrayList<>();
            if (curators != null) {
                if (curators.msgcode.equals("0")) {
                    try {
                        for (Api_Model.product dataset : curators.product) {
                            strProductId = dataset.productID;
                            strProductName = dataset.name;
                            strProductCaption = dataset.caption;
                            strCheckSoldOut = dataset.sold_out;
                            detailItemName.setText(strProductName);
                            detailItemNameHindi.setText(strProductCaption);

                            prodDesc.setText(dataset.description);
                            for (Api_Model.product.image_list datasetnew : dataset.image_list) {
                                strProductimage = datasetnew.small_image;
                                ArrImgListSmall.add(datasetnew.small_image);
                                ArrImgListLarge.add(datasetnew.large_image);
                            }

                            for (Api_Model.product.price_list datasetnews : dataset.price_list) {
                                PricelistModel mPricelistmodel;
                                mPricelistmodel = new PricelistModel(datasetnews.sr, datasetnews.price_ID, datasetnews.price, datasetnews.mrp,
                                        datasetnews.weight, datasetnews.dis);
                                ArrPriceList.add(mPricelistmodel);
                            }

                        }
                        for (int i = 0; i < ArrPriceList.size(); i++) {
                            PricelistModel mPricelistmodel = ArrPriceList.get(i);
                            strPrice = mPricelistmodel.getPrice();
                            strMrp = mPricelistmodel.getMrp();
                            strPriceId = mPricelistmodel.getPriceID();
                            strWeight = mPricelistmodel.getWeight();
                            strDiscount = mPricelistmodel.getDis();
                            // strProductWeight = mPricelistmodel.getWeight();
                            ArrPrice.add(strPrice);
                            Arrmrp.add(strMrp);
                            ArrPriceIdList.add(strPriceId);
                            Arrweight.add(strWeight);
                            // Arrweight.add(mPricelistmodel.getWeight());
                            Arrdis.add(strDiscount);
                        }
                        //   Log.e("ArrPriceIdList: ", ArrPriceIdList.toString());


                        detailItemPrice.setText(getResources().getString(R.string.Rs) + " " + ArrPrice.get(0));
                        detailItemlMrp.setText(getResources().getString(R.string.Rs) + " " + Arrmrp.get(0));
                        if (!Arrdis.get(0).equalsIgnoreCase("0")) {
                            discount.setText(Arrdis.get(0) + " % off");
                        }

                        if (ArrPriceList.size() > 1) {
                            pOptions.setText(getResources().getString(R.string.Rs) + " " + ArrPrice.get(0) + "/" + Arrweight.get(0));
                            tvDetailSelectedweight.setVisibility(View.GONE);
                        } else {
                            pOptions.setVisibility(View.GONE);
                            tvDetailSelectedweight.setVisibility(View.VISIBLE);
                            tvDetailSelectedweight.setText(Arrweight.get(0));
                        }
                        //tvDetailSelectedweight
                        if (strCheckSoldOut.equalsIgnoreCase("No")) {
                            detailItemStock.setVisibility(View.VISIBLE);
                            tvDetailOutofStock.setVisibility(View.GONE);
                            if (shudh4sureDB.ifExistsPriceId(ArrPriceIdList.get(0))) {
                                //    Log.e("VisibleDetails", "VisibleDetails");
                                int counter = shudh4sureDB.getQuentyty(ArrPriceIdList.get(0));
                                itmQUAN.setText(String.valueOf(counter));
                                pAddcartbtn.setVisibility(View.GONE);
                                addingitemLinear.setVisibility(View.VISIBLE);
                            } else {
                                //     Log.e("GoneDetails", "GoneDetails");
                                pAddcartbtn.setVisibility(View.VISIBLE);
                                addingitemLinear.setVisibility(View.GONE);
                            }

                        } else {
                            tvDetailOutofStock.setVisibility(View.VISIBLE);
                            detailItemStock.setVisibility(View.GONE);
                            lvWeightCounter.setVisibility(View.GONE);

                        }

                        Assert.assertNotNull(ArrImgListSmall);
                        pager.setAdapter(adapter);
                        pager.setOffscreenPageLimit(6);
                        GetRelatedProduct getRelatedProduct = new GetRelatedProduct();
                        getRelatedProduct.execute();
                    } catch (Exception e) {
                        //    Log.e("Exception e", e.toString());
                    }

                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, ProductDetailsActivity.this);
                }
            }
        }
    }

    class GalleryPagerAdapter extends PagerAdapter {

        Context _context;
        LayoutInflater _inflater;

        GalleryPagerAdapter(Context context) {
            _context = context;
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return ArrImgListSmall.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = _inflater.inflate(R.layout.detail_single_image, container, false);
            container.addView(itemView);


            int borderSize = thumbnails.getPaddingTop();
            int thumbnailSize = ((FrameLayout.LayoutParams)
                    pager.getLayoutParams()).bottomMargin - (borderSize * 2);

            // Set the thumbnail layout parameters. Adjust as required
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
            params.setMargins(0, 0, borderSize, 0);


            final ImageView thumbView = new ImageView(_context);
            thumbView.setBackgroundResource(R.drawable.border);
            thumbView.setScaleType(ImageView.ScaleType.FIT_XY);
            thumbView.setLayoutParams(params);
            thumbView.setPadding(6, 6, 6, 6);
            thumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  Log.d("CurrentImage", "Thumbnail clicked");
                    pager.setCurrentItem(position);
                }
            });
            thumbnails.addView(thumbView);

            final ImageView imageView = (ImageView) itemView.findViewById(R.id.imagedetail);

            if (ArrImgListLarge.size() == 1) {
                horizontal.setVisibility(View.GONE);

                FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                llp.setMargins(0, 0, 0, 10); // llp.setMargins(left, top, right, bottom);
                pager.setLayoutParams(llp);


                detailItemIMG.requestLayout();
            }

            Glide.with(_context)
                    .load(ArrImgListLarge.get(position)).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                    imageView.setImageBitmap(bitmap);
                    thumbView.setImageBitmap(bitmap);
                    loading_image.setVisibility(View.GONE);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(ProductDetailsActivity.this, ImageFinalZommWithThumb.class);
                    it.putExtra("productName", detailItemName.getText().toString());
                    it.putStringArrayListExtra("imageListLarge", ArrImgListLarge);
                    it.putStringArrayListExtra("imageListSmall", ArrImgListSmall);
                    //    Log.e("imageListLarge Size", String.valueOf(ArrImgListLarge.size()));
                    //     Log.e("imageListSmall Size", String.valueOf(ArrImgListSmall.size()));


                    startActivity(it);
                    // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private class CustomListAdapter extends BaseAdapter {

        ArrayList<PricelistModel> mCounting;
        int mainPos;

        CustomListAdapter(ArrayList<PricelistModel> counting, int adapterPosition) {

            mCounting = counting;
            mainPos = adapterPosition;
        }

        @Override
        public int getCount() {
            return mCounting.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.product_options_items_details, parent, false);
                holder = new CustomListAdapter.ViewHolder();

                holder.pSize = (TextView) convertView.findViewById(R.id.pSize);
                convertView.setTag(holder);
                holder.position = position;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.pSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCounting.get(mainPos).setListPos(holder.position);
                    //       Log.e("Selected_DD_Price", mCounting.get(holder.position).getPrice());
                    pOptions.setText(getResources().getString(R.string.Rs) + " " + mCounting.get(holder.position).getPrice() + "/" + mCounting.get(holder.position).getWeight());

                    if (!mCounting.get(holder.position).getDis().equalsIgnoreCase("0")) {
                        discount.setText(mCounting.get(holder.position).getDis() + " % off");
                    }
                    detailItemPrice.setText(getResources().getString(R.string.Rs) + "" + mCounting.get(holder.position).getPrice());
                    detailItemlMrp.setText(getResources().getString(R.string.Rs) + "" + mCounting.get(holder.position).getMrp());

                    if (shudh4sureDB.ifExistsPriceId(mCounting.get(holder.position).getPriceID())) {
                        //  Log.e("Visible", "Visible");
                        int i = shudh4sureDB.getQuentyty(mCounting.get(holder.position).getPriceID());
                        pAddcartbtn.setVisibility(View.GONE);
                        addingitemLinear.setVisibility(View.VISIBLE);
                        itmQUAN.setText(String.valueOf(i));

                        plu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //     Log.e("VisiblePlus", "VisiblePlus");
                                int counter = shudh4sureDB.getQuentyty(mCounting.get(holder.position).getPriceID());
                                int total = counter + 1;
                                shudh4sureDB.UpdateQuantityByPriceIdInDB(mCounting.get(holder.position).getPriceID(), total);
                                itmQUAN.setText(String.valueOf(total));
                                TotalCount = 0;
                                Cursor c = shudh4sureDB.ShowTableCartList();
                                tvmytotalitems.setText(c.getCount() + "");

                                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                    TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                                }
                                tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");

                                Toast.makeText(ProductDetailsActivity.this, "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });

                        minusItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //  Log.e("DPMinue", "DPMinus");
                                int counter = shudh4sureDB.getQuentyty(mCounting.get(holder.position).getPriceID());
                                if (counter == 1) {
                                    itmQUAN.setText(String.valueOf(counter));
                                    pAddcartbtn.setVisibility(View.VISIBLE);
                                    addingitemLinear.setVisibility(View.GONE);
                                    shudh4sureDB.DeleteCartListByPriceID(mCounting.get(holder.position).getPriceID());
                                    shudh4sureDB.UpdateQuantityByPriceIdInDB(mCounting.get(holder.position).getPriceID(), 0);
                                    TotalCount = 0;
                                    Cursor c = shudh4sureDB.ShowTableCartList();
                                    tvmytotalitems.setText(c.getCount() + "");
                                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                        TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                                    }
                                    tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");

                                } else if (counter > 1) {

                                    int total = counter - 1;
                                    itmQUAN.setText(String.valueOf(total));
                                    shudh4sureDB.UpdateQuantityByPriceIdInDB(mCounting.get(holder.position).getPriceID(), total);
                                    TotalCount = 0;
                                    Cursor c = shudh4sureDB.ShowTableCartList();
                                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                        TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                                    }
                                    tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");
                                    Toast.makeText(getApplicationContext(), "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


                    } else {
                        //  Log.e("Gone", "Gone");
                        pAddcartbtn.setVisibility(View.VISIBLE);
                        addingitemLinear.setVisibility(View.GONE);

                        pAddcartbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addingitemLinear.setVisibility(View.VISIBLE);
                                pAddcartbtn.setVisibility(View.GONE);
                                shudh4sureDB.InsertCartListData(strProductId, mCounting.get(holder.position).getPriceID(),
                                        strProductName, ArrImgListSmall.get(0), mCounting.get(holder.position).getPrice(), mCounting.get(holder.position).getMrp(), 1, mCounting.get(holder.position).getWeight(), strProductCaption);
                                itmQUAN.setText(String.valueOf(1));
                                TotalCount = 0;
                                Cursor c = shudh4sureDB.ShowTableCartList();
                                tvmytotalitems.setText(c.getCount() + "");

                                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                    TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                                }
                                tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");

                                Toast.makeText(ProductDetailsActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();

                            }
                        });


                        plu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //    Log.e("DPPlues", "DPPlues");
                                int counter = shudh4sureDB.getQuentyty(mCounting.get(holder.position).getPriceID());
                                int total = counter + 1;
                                shudh4sureDB.UpdateQuantityByPriceIdInDB(mCounting.get(holder.position).getPriceID(), total);
                                itmQUAN.setText(String.valueOf(total));
                                TotalCount = 0;
                                Cursor c = shudh4sureDB.ShowTableCartList();
                                tvmytotalitems.setText(c.getCount() + "");

                                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                    TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                                }
                                tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");

                                Toast.makeText(ProductDetailsActivity.this, "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });

                        minusItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //   Log.e("DPMinue", "DPMinus");
                                int counter = shudh4sureDB.getQuentyty(mCounting.get(holder.position).getPriceID());
                                if (counter == 1) {
                                    itmQUAN.setText(String.valueOf(counter));
                                    pAddcartbtn.setVisibility(View.VISIBLE);
                                    addingitemLinear.setVisibility(View.GONE);
                                    shudh4sureDB.DeleteCartListByPriceID(mCounting.get(holder.position).getPriceID());
                                    shudh4sureDB.UpdateQuantityByPriceIdInDB(mCounting.get(holder.position).getPriceID(), 0);
                                    TotalCount = 0;
                                    Cursor c = shudh4sureDB.ShowTableCartList();
                                    tvmytotalitems.setText(c.getCount() + "");
                                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                        TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                                    }
                                    tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");

                                } else if (counter > 1) {
                                    int total = counter - 1;
                                    itmQUAN.setText(String.valueOf(total));
                                    shudh4sureDB.UpdateQuantityByPriceIdInDB(mCounting.get(holder.position).getPriceID(), total);
                                    TotalCount = 0;
                                    Cursor c = shudh4sureDB.ShowTableCartList();
                                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                                        TotalCount = TotalCount + (c.getInt(7) * Double.parseDouble(c.getString(5)));
                                    }
                                    tvDetailsAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");
                                    Toast.makeText(getApplicationContext(), "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                    dialog.dismiss();
                }
            });
            holder.pSize.setTag(holder.position);
            holder.pSize.setText(getResources().getString(R.string.Rs) + " " + mCounting.get(holder.position).getPrice() + "/" + mCounting.get(holder.position).getWeight());

            if (holder.position == mCounting.get(mainPos).getListPos()) {
                //     Log.e("aaaa", "aaaa");
                holder.pSize.setTextColor(Color.WHITE);
                holder.pSize.setBackgroundColor(ContextCompat.getColor(ProductDetailsActivity.this, R.color.dropdown_bg));
            } else {
                //   Log.e("aaaa___", "aaaa___");
                holder.pSize.setBackgroundColor(Color.WHITE);
                holder.pSize.setTextColor(Color.BLACK);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView pSize;
            int position;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetRelatedProduct extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();

        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators;
                curators = methods.RelatedProducts("related_product", strUserId, strcatID);
                return curators;
            } catch (Exception E) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {

            if (loading != null && loading.isShowing()) {
                loading.dismiss();
            }
            if (curators != null) {
                if (curators.msgcode.equals("0")) {
                    try {
                        for (Api_Model.related_product dataSet : curators.related_product) {
                            BestNewProductModel myOrderModel;

                            List<PriceList> priceLists = new ArrayList<>();

                            for (Api_Model.related_product.price_list price_list : dataSet.price_list) {
                                PriceList priceList1 = new PriceList();
                                priceList1.setDis(price_list.dis);
                                priceList1.setMrp(price_list.mrp);
                                priceList1.setPrice(price_list.price);
                                priceList1.setPriceID(price_list.price_ID);
                                priceList1.setSr(price_list.sr);
                                priceList1.setWeight(price_list.weight);
                                priceLists.add(priceList1);
                            }

                            myOrderModel = new BestNewProductModel(dataSet.productID, dataSet.name, dataSet.caption, dataSet.image, dataSet.discount
                                    , dataSet.price, dataSet.mrp, dataSet.sold_out, priceLists);
                            relatedProductList.add(myOrderModel);
                        }
                        relatedProducts.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, ProductDetailsActivity.this);
                }
            }
        }
    }
}
