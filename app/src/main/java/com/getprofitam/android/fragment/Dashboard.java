package com.getprofitam.android.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.Util;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.getprofitam.android.R;
import com.getprofitam.android.activity.Api_Model;
import com.getprofitam.android.activity.CartActivity;
import com.getprofitam.android.activity.CategoryActivity;
import com.getprofitam.android.activity.ContactusActivity;
import com.getprofitam.android.activity.FeedbackActivity;
import com.getprofitam.android.activity.LoginActivity;
import com.getprofitam.android.activity.MyAccountActivity;
import com.getprofitam.android.activity.NewCategoryActivity;
import com.getprofitam.android.activity.OffersActivity;
import com.getprofitam.android.activity.OrderHistoryActivity;
import com.getprofitam.android.activity.ProductDetailsActivity;
import com.getprofitam.android.activity.ProductListActivity;
import com.getprofitam.android.activity.SearchActivity;
import com.getprofitam.android.activity.WishlistActivity;
import com.getprofitam.android.adapter.BestNewProductAdapter;
import com.getprofitam.android.adapter.HomeCategoryAdapter;
import com.getprofitam.android.adapter.HomeCategoryOfferAdapter;
import com.getprofitam.android.dbhelper.GetprofitamDB;
import com.getprofitam.android.model.BannerModel;
import com.getprofitam.android.model.BestNewProductModel;
import com.getprofitam.android.model.HomeCategoryModel;
import com.getprofitam.android.model.OfferBannerModel;
import com.getprofitam.android.model.SearchModel;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.IApiMethods;
import com.getprofitam.android.utils.MyUpdateWishList;
import com.getprofitam.android.utils.SpacesItemDecorationGrid;
import com.getprofitam.android.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.client.HttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit.RestAdapter;

import static com.getprofitam.android.utils.AppConstant.API_URL;
import static com.getprofitam.android.utils.AppConstant.CART_MESSAGE;


public class Dashboard extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, MyUpdateWishList {
    Global global;
    SharedPreferences mprefs;
    String strUserId = "", strToken = "", strUserDeviceId = "";
    private SliderLayout mDemoSlider;
    RelativeLayout relativeMain;
    RecyclerView recycleViewMainCategory, rvOffers, rvBestProducts, rvNewProducts;
    HttpClient httpClient;
    LinearLayout liHome, liShop, liWishlist, liOffers, limyaccount;
    ImageView imgHome, imgStore, imgWishlist, imgOffer, imgmyaccount;
    public HomeCategoryAdapter mCategoryAdapter;
    public HomeCategoryOfferAdapter homeCategoryOfferAdapter;
    BestNewProductAdapter bestProductAdapter, newProductAdapter;
    ArrayList<String> bannerImage = new ArrayList<String>();
    ArrayList<BannerModel> bannerDetail = new ArrayList<BannerModel>();
    List<HomeCategoryModel> CategoryList = new ArrayList<>();
    List<OfferBannerModel> offerBannerlist = new ArrayList<>();
    ArrayList<BestNewProductModel> bestProductList = new ArrayList<BestNewProductModel>();
    ArrayList<BestNewProductModel> newProductList = new ArrayList<BestNewProductModel>();
    public static ArrayList<SearchModel> searchArrayList = new ArrayList<SearchModel>();
    ProgressDialog loading;
    String version = "";
    String version_msg = "";
    String cart_amount = "";
    String cart_msg = "";
    GetprofitamDB getprofitamDB = new GetprofitamDB(getActivity());
    Cursor cursorWish;
    AlertDialog packsizeDialog;
    String strNotitype = "", strNotiMessage = "", strNotiTitle = "", strNotiImage = "";
    String smartphone_version = "";
    String strFullOtp = "";
    ArrayList<String> WishLocalArrFOrStartView = new ArrayList<String>();
    String FinalLocalDBName = "";
    CardView categoryCard;
    TextView searchtext;
    Api_Model curators;
    GetprofitamDB getGetprofitamDB;
    int dbWishSize = 0;
    int countWishList = 0;
    private Context context;
    View view;

    public static TextView tvitemCounter;
    boolean isShow = false;

    public Dashboard() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dashboard_view, container, false);
        /*countWishList = mprefs.getInt(AppConstant.USER_WISHLIST);
        Log.d("strWishCoutnt",String.valueOf(countWishList));
 */
        context = getActivity();
        initComponet(view);
        try {
            getGetprofitamDB = new GetprofitamDB(context);
            mprefs = getActivity().getSharedPreferences(AppConstant.PREFS_NAME, Context.MODE_PRIVATE);
            strUserId = mprefs.getString(AppConstant.USER_ID, null);
            dbWishSize = getGetprofitamDB.getAllProduct().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        global = new Global(getActivity());
        mprefs = getActivity().getSharedPreferences(AppConstant.PREFS_NAME, getActivity().MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        //Log.e("strUserId: ", strUserId);
        initComponet(view);
        //tvmytotalitems.setText("" + cursorCart.getCount() + "");
        // isConnectedToServer(API_URL,5);
        Log.e("update", "update:-" + getActivity().getPackageName());
        String LocalDbUserSocityIdName = strUserId + "_getprofitam_local.db";
        FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
        try {
            strToken = FirebaseInstanceId.getInstance().getToken();
        } catch (Exception e) {
            Log.e("cartActivity", "cartActivity" + e.getMessage());

        }
        countWishList = mprefs.getInt(AppConstant.USER_WISHLIST, 0);
        // Log.e("strWishCoutnt", String.valueOf(countWishList));
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 140);
        } else {
            try {
                getprofitamDB = new GetprofitamDB(getActivity(), FinalLocalDBName);
                getprofitamDB.OpenDB();
                cursorWish = getprofitamDB.ShowTableWishList();
                if (cursorWish.getCount() > 0) {
                    ArrayList<String> WishLocalArr = new ArrayList<String>();
                    for (cursorWish.moveToFirst(); !cursorWish.isAfterLast(); cursorWish.moveToNext()) {
                        WishLocalArr.add(cursorWish.getString(1));
                        // Log.e("getcount", "countfor:" + cursorWish.getCount());
                    }
                    mprefs.edit().putInt(AppConstant.USER_WISHLIST, cursorWish.getCount()).apply();
                } else {
                    mprefs.edit().remove(AppConstant.USER_WISHLIST).commit();
                }
            } catch (Exception e) {
                Log.e("cartActivity", "cartActivity" + e.getMessage());

            }
        }
        liHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickb("1");
            }
        });
        liShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickb("2");

            }
        });
        liWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickb("3");

            }
        });
        liOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickb("4");

            }
        });
        limyaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickb("5");
            }
        });
        searchtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                //   getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        categoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewCategoryActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        Log.e("FinalLocalDBName,,", FinalLocalDBName);

        if (cursorWish != null && cursorWish.getCount() > 0) {
            for (cursorWish.moveToFirst(); !cursorWish.isAfterLast(); cursorWish.moveToNext()) {
                WishLocalArrFOrStartView.add(cursorWish.getString(1));


            }
        }
      /*  strToken = FirebaseInstanceId.getInstance().getToken();*/
        strUserDeviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
       /* new checkToken().execute();*/

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            strNotitype = bundle.getString("notificationType");
            strNotiMessage = bundle.getString("notificationMessage");
            strNotiTitle = bundle.getString("notificationTitle");
            strNotiImage = bundle.getString("notificationImage");
        }
        if (strNotitype.equalsIgnoreCase("order")) {
            alertDialog();
        }
        /*new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "728310740601527",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            *//* handle the result *//*
                        Log.e("response",String.valueOf(response));
                    }
                }
        ).executeAsync();*/
        //alertDialog();

        // Layout Manager for Main Category In Home Screen
        /*RecyclerView.LayoutManager mLayoutManagermain =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);*/

       /* RecyclerView.LayoutManager mLayoutManagermain =
           new GridLayoutManager(getActivity(), 3,LinearLayoutManager.VERTICAL,false);*/

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        recycleViewMainCategory.setHasFixedSize(true);
        recycleViewMainCategory.setLayoutManager(gridLayoutManager);
        recycleViewMainCategory.addItemDecoration(new SpacesItemDecorationGrid(1));
        recycleViewMainCategory.setNestedScrollingEnabled(false);

        // Layout Manager for  Offer Banners
        RecyclerView.LayoutManager mLayoutManagerOffer = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvOffers.setLayoutManager(mLayoutManagerOffer);
        rvOffers.setHasFixedSize(true);

        // Layout Manager for Best Product In Home Screen
        RecyclerView.LayoutManager mLayoutManagerBestProduct = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvBestProducts.setLayoutManager(mLayoutManagerBestProduct);
        rvBestProducts.setHasFixedSize(true);

        //  Layout Manager for New products In Home Screen
        RecyclerView.LayoutManager mLayoutManagerNewProducts = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvNewProducts.setLayoutManager(mLayoutManagerNewProducts);
        rvNewProducts.setHasFixedSize(true);

        // Main Category Adapter
        mCategoryAdapter = new HomeCategoryAdapter(getActivity(), CategoryList);
        recycleViewMainCategory.setAdapter(mCategoryAdapter);
        //   recycleViewMainCategory.smoothScrollToPosition(rvBestProducts.getAdapter().getItemCount());

        // Offer Banner Adapter
        homeCategoryOfferAdapter = new HomeCategoryOfferAdapter(getActivity(), offerBannerlist);
        rvOffers.setAdapter(homeCategoryOfferAdapter);

        // BestNewProductAdapter used for New Products
        newProductAdapter = new BestNewProductAdapter(getActivity(), newProductList, getprofitamDB, WishLocalArrFOrStartView);
        rvNewProducts.setAdapter(newProductAdapter);

        // BestNewProductAdapter used for Best Products
        bestProductAdapter = new BestNewProductAdapter(getActivity(), bestProductList, getprofitamDB, WishLocalArrFOrStartView);
        rvBestProducts.setAdapter(bestProductAdapter);

        // Click event to view offers
        homeCategoryOfferAdapter.setOnItemClickListener(new HomeCategoryOfferAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {
                try {
                    cursorWish = getprofitamDB.ShowTableWishList();
                    if (cursorWish.getCount() > 0) {
                        ArrayList<String> WishLocalArr = new ArrayList<String>();
                        for (cursorWish.moveToFirst(); !cursorWish.isAfterLast(); cursorWish.moveToNext()) {
                            WishLocalArr.add(cursorWish.getString(1));
                        }
                        tvitemCounter.setText(String.valueOf(cursorWish.getCount()));
                    } else {
                        tvitemCounter.setText(String.valueOf(0));
                    }
                    OfferBannerModel offerBannerModel = offerBannerlist.get(position);
                    if (which == 1) {
                        Intent intent = new Intent(getActivity(), ProductListActivity.class);
                        intent.putExtra("catID", offerBannerModel.getCatID());
                        intent.putExtra("catName", offerBannerModel.getName().trim());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    }
                } catch (Exception ex) {

                }

            }
        });
        // Click event to view offers
        mCategoryAdapter.setOnItemClickListener(new HomeCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {
                try {
                    cursorWish = getprofitamDB.ShowTableWishList();
                    if (cursorWish.getCount() > 0) {
                        ArrayList<String> WishLocalArr = new ArrayList<String>();
                        for (cursorWish.moveToFirst(); !cursorWish.isAfterLast(); cursorWish.moveToNext()) {
                            WishLocalArr.add(cursorWish.getString(1));
                            //  Log.e("bestProductAdapter", "bestProductAdapter" + cursorWish.getCount());
                            // tvitemCounter.setText(String.valueOf(cursorWish.getCount()));
                        }
                        tvitemCounter.setText(String.valueOf(cursorWish.getCount()));
                    } else {
                        tvitemCounter.setText(String.valueOf(0));
                    }
                    HomeCategoryModel homeCategoryModel = CategoryList.get(position);
                    if (which == 1) {

                        Intent intent = new Intent(getActivity(), ProductListActivity.class);
                        intent.putExtra("catID", homeCategoryModel.getCatID());
                        intent.putExtra("catName", homeCategoryModel.getName());
                        // Log.e("catID: ", homeCategoryModel.getCatID());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                } catch (Exception ex) {
                }
            }
        });

        // Click event to view New Product
        newProductAdapter.setOnItemClickListener(new BestNewProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {
                cursorWish = getprofitamDB.ShowTableWishList();
                if (cursorWish.getCount() > 0) {
                    ArrayList<String> WishLocalArr = new ArrayList<String>();
                    for (cursorWish.moveToFirst(); !cursorWish.isAfterLast(); cursorWish.moveToNext()) {
                        WishLocalArr.add(cursorWish.getString(1));
                        //Log.e("bestProductAdapter", "bestProductAdapter" + cursorWish.getCount());
                    }
                    tvitemCounter.setText(String.valueOf(cursorWish.getCount()));
                } else {
                    tvitemCounter.setText(String.valueOf(0));
                }
                BestNewProductModel homeCategoryModel = newProductList.get(position);
                if (which == 1) {
                    //  Utils.ShowSnakBar("Category "+homeCategoryModel.getName(), relativeMain, getActivity());
                    Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                    intent.putExtra("catID", homeCategoryModel.getProductID());
                    intent.putExtra("catName", homeCategoryModel.getName());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
        //Click event to view best product
        bestProductAdapter.setOnItemClickListener(new BestNewProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {
                cursorWish = getprofitamDB.ShowTableWishList();
                if (cursorWish.getCount() > 0) {
                    ArrayList<String> WishLocalArr = new ArrayList<String>();
                    for (cursorWish.moveToFirst(); !cursorWish.isAfterLast(); cursorWish.moveToNext()) {
                        WishLocalArr.add(cursorWish.getString(1));
                        //  Log.e("bestProductAdapter", "bestProductAdapter" + cursorWish.getCount());
                        // tvitemCounter.setText(String.valueOf(cursorWish.getCount()));
                    }
                    tvitemCounter.setText(String.valueOf(cursorWish.getCount()));
                } else {
                    tvitemCounter.setText(String.valueOf(0));
                }
                BestNewProductModel homeCategoryModel = bestProductList.get(position);
                if (which == 1) {
                    Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                    intent.putExtra("catID", homeCategoryModel.getProductID());
                    intent.putExtra("catName", homeCategoryModel.getName());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        // API to load Banners, Offer and Categories
        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(getActivity(), "", "Please wait...", false, false);
            GetSubCategoryDataTask mGetSubCategoryDataTask = new GetSubCategoryDataTask();
            mGetSubCategoryDataTask.execute();
            mGetSubCategoryDataTask.getStatus();
            // Log.e("status", "code" + mGetSubCategoryDataTask.getStatus());
        } else {
            retryInternet();
        }

    }

    public void initComponet(View view) {
        rvBestProducts = (RecyclerView) view.findViewById(R.id.rvBestProducts);
        rvNewProducts = (RecyclerView) view.findViewById(R.id.rvNewProducts);
        relativeMain = (RelativeLayout) view.findViewById(R.id.relativeMain);
        recycleViewMainCategory = (RecyclerView) view.findViewById(R.id.recycleViewMainCategory);
        rvOffers = (RecyclerView) view.findViewById(R.id.rvOffers);
        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);
        categoryCard = (CardView) view.findViewById(R.id.categoryCard);
        searchtext = (TextView) view.findViewById(R.id.searchtext);
        liHome = (LinearLayout) view.findViewById(R.id.liHome);
        liShop = (LinearLayout) view.findViewById(R.id.liShop);
        liWishlist = (LinearLayout) view.findViewById(R.id.liWishlist);
        liOffers = (LinearLayout) view.findViewById(R.id.liOffers);
        limyaccount = (LinearLayout) view.findViewById(R.id.limyaccount);
        imgmyaccount = (ImageView) view.findViewById(R.id.imgmyaccount);
        imgHome = (ImageView) view.findViewById(R.id.imgHome);
        imgStore = (ImageView) view.findViewById(R.id.imgStore);
        imgWishlist = (ImageView) view.findViewById(R.id.imgWishlist);
        imgOffer = (ImageView) view.findViewById(R.id.imgOffer);
        tvitemCounter = (TextView) view.findViewById(R.id.tvitemCounter);
    }

    @Override
    public void updateWishCount(int Count) {
        countWishList = mprefs.getInt(AppConstant.USER_WISHLIST, 0);
        tvitemCounter.setText(String.valueOf(countWishList));
    }

    private class GetSubCategoryDataTask extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;
        PackageInfo pInfo;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(API_URL).build();
            if (getActivity() != null) {
                try {
                    pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("cartActivity", "cartActivity" + e.getMessage());

                }
            }
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                curators = methods.Home("home", strUserId, strUserDeviceId, strToken, pInfo.versionName);
                return curators;
            } catch (Exception E) {
                Log.i("exception e__", E.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {

            if (loading != null) {

                if (loading.isShowing()) {
                    loading.dismiss();
                }
            }
            if (curators == null) {
                //Utils.showToastShort("Connection Refused...try again!", getActivity());
                retryConnection();
            } else {
                Log.i("Curator", curators.message);
                version = curators.version;
                version_msg = curators.version_msg;
                cart_amount = curators.cart_amount;
                cart_msg = curators.cart_msg;

                if (curators.msgcode.equals("0")) {
                    if (version.length() > 0) {
                        try {
                            if (getActivity() != null) {
                                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                                smartphone_version = pInfo.versionName;
                            }
                            //Toast.makeText(getActivity(),smartphone_version,Toast.LENGTH_LONG).show();
                            //     Log.e("smartphone_version ",smartphone_version);
                            //     Log.e("version  ",version);

                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e("cartActivity", "cartActivity" + e.getMessage());
                        }
                        boolean new_version_avaible = compare(smartphone_version, version);
                        if (new_version_avaible) {
                            if (getActivity() != null) {
                                showUpdateDialog(version_msg);
                            }
                        }
                        try {
                            for (Api_Model.banner_list dataset : curators.banner_list) {
                                BannerModel myOrderModel;
                                myOrderModel = new BannerModel(dataset.sr, dataset.name, dataset.image, dataset.catID);
                                bannerDetail.add(myOrderModel);
                                bannerImage.add(dataset.image);
                            }
                            for (Api_Model.home_category_list dataset : curators.home_category_list) {
                                HomeCategoryModel mCategoryData;
                                mCategoryData = new HomeCategoryModel(dataset.catID, dataset.name, dataset.icon);
                                CategoryList.add(mCategoryData);
                            }
                            for (Api_Model.offer_banner dataset : curators.offer_banner) {
                                OfferBannerModel mCategoryData;
                                mCategoryData = new OfferBannerModel(dataset.sr, dataset.name, dataset.image, dataset.catID);
                                offerBannerlist.add(mCategoryData);
                            }

                            SetBanner(bannerImage);   // Method to bind Banners Images
                            mCategoryAdapter.notifyDataSetChanged();
                            homeCategoryOfferAdapter.notifyDataSetChanged();

                            mprefs.edit().putString(AppConstant.CART_AMOUNT, cart_amount).apply();
                            mprefs.edit().putString(AppConstant.CART_MESSAGE, cart_msg).apply();


                            // API to Load Best and New Product List
                            GetNewBestProduct getNewBestProduct = new GetNewBestProduct();
                            getNewBestProduct.execute();

                        } catch (Exception e) {
                            Log.e("Exception e", e.toString());
                        }
                    }
                } else if (curators.msgcode.equals("2")) {
                    global.setPrefBoolean("Verify", false);
                    SharedPreferences preferences = getActivity().getSharedPreferences(AppConstant.PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, getActivity());
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        countWishList = mprefs.getInt(AppConstant.USER_WISHLIST, 0);
        tvitemCounter.setText(String.valueOf(countWishList));
    }

    public void SetBanner(ArrayList bannercontent) {
        for (int i = 0; i < bannercontent.size(); i++) {
            //   file_maps.put(Integer.toString(i), bannercontent.get(i).toString());
            DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
            final int finalI = i;
            textSliderView.image(bannercontent.get(i).toString())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            Intent it = new Intent(getActivity(), ProductListActivity.class);
                            it.putExtra("catID", bannerDetail.get(finalI).getCatID());
                            it.putExtra("catName", bannerDetail.get(finalI).getName());
                            startActivity(it);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            /*if (bannerDetail.get(finalI).get().equalsIgnoreCase("Yes")) {
                                Intent it = new Intent(getActivity(), CategoryExpandableListView.class);
                                it.putExtra("catID", bannerDetail.get(finalI).getCatID());
                                it.putExtra("subCatName", bannerDetail.get(finalI).getName());
                                startActivity(it);
                                getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                            } else {
                                Intent it = new Intent(getActivity(), ProductListActivity.class);
                                it.putExtra("catID", bannerDetail.get(finalI).getCatID());
                                it.putExtra("catName", bannerDetail.get(finalI).getName());
                                startActivity(it);
                                getActivity().overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.slide_out_left);
                            }*/
                        }
                    });

            mDemoSlider.addSlider(textSliderView);
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(4000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 140: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        try {

                            getprofitamDB = new GetprofitamDB(getActivity(), FinalLocalDBName);
                            getprofitamDB.OpenDB();
                            cursorWish = getprofitamDB.ShowTableWishList();
                        } catch (Exception e) {
                            Log.e("cartActivity", "cartActivity" + e.getMessage());

                        }
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
                    }
                }
                break;
            }
        }
    }

    private class GetNewBestProduct extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(API_URL).build();

        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators;
                curators = methods.BestNewProducts("home_products", strUserId);
                return curators;

            } catch (Exception E) {
                Log.i("exception e", E.toString());
                return null;
            }
        }


        @Override
        protected void onPostExecute(Api_Model curators) {
            if (loading != null) {
                if (loading.isShowing()) {
                    loading.dismiss();
                }
            }
            if (curators == null) {

            } else {
                Log.i("Curator", curators.message);
                if (curators.msgcode.equals("0")) {
                    try {
                        for (Api_Model.new_product dataset : curators.new_product) {
                            BestNewProductModel myOrderModel;
                            myOrderModel = new BestNewProductModel(dataset.productID, dataset.name, dataset.caption, dataset.image, dataset.discount
                                    , dataset.price, dataset.mrp, dataset.sold_out);
                            newProductList.add(myOrderModel);
                        }
                        newProductAdapter.notifyDataSetChanged();

                        for (Api_Model.best_product dataset : curators.best_product) {
                            BestNewProductModel myOrderModel;
                            myOrderModel = new BestNewProductModel(dataset.productID, dataset.name, dataset.caption, dataset.image, dataset.discount
                                    , dataset.price, dataset.mrp, dataset.sold_out);
                            bestProductList.add(myOrderModel);

                        }
                        bestProductAdapter.notifyDataSetChanged();


                    } catch (Exception e) {
                        Log.e("Exception e", e.toString());
                    }
                    // API to Load Best and New Product List
                    GetSearchlist getSearchlist = new GetSearchlist();
                    getSearchlist.execute();
                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, getActivity());
                }
            }
        }
    }

    private class GetSearchlist extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(API_URL).build();
            searchArrayList.clear();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators;
                curators = methods.BestNewProducts("search", strUserId);
                return curators;
            } catch (Exception E) {
                Log.i("exception e", E.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {

            if (loading != null) {
                if (loading.isShowing()) {
                    loading.dismiss();
                }
            }
            if (curators == null) {

            } else {

                Log.i("Curator", "Search:" + curators.message);
                if (curators.msgcode.equals("0")) {
                    try {
                        for (Api_Model.search_list dataset : curators.search_list) {
                            SearchModel searchModel;
                            searchModel = new SearchModel(dataset.ID, dataset.name, dataset.type);
                            searchArrayList.add(searchModel);
                        }
                    } catch (Exception e) {
                        Log.e("Exception e", e.toString());
                    }

                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, getActivity());
                }
            }
        }
    }

    /*public class checkToken extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... params) {
            try {

                if (getActivity()!=null){

                    InstanceID instanceID = InstanceID.getInstance(getActivity());
                    strToken = instanceID.getToken("1072656697144",
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.e("StrUserToken", strToken);

                }

                *//* urlGCM = "http://www..com/sl_api2/category1.php?gcmtoken="+StrUserToken
                        +"&deviceId="+StrUserDeviceID
			    		+"&appVersion="+smartphone_version
			    		+"&userId="+Utils.userID;
				Log.e("urlGCM", urlGCM);*//*
            } catch (IOException e)
            {
                Log.e("Errorr xxx",e.getMessage().toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

        }

    }*/
    public void alertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View alertlayout = inflater.inflate(R.layout.offer_dialog, null);
        ImageView offerIMG = (ImageView) alertlayout.findViewById(R.id.offerIMG);
        TextView offerDetails = (TextView) alertlayout.findViewById(R.id.offerDetails);
        TextView offerTitle = (TextView) alertlayout.findViewById(R.id.offerTitle);

        TextView tvRateus = (TextView) alertlayout.findViewById(R.id.tvRateus);
        TextView tvFeedback = (TextView) alertlayout.findViewById(R.id.tvFeedback);
        TextView tvBacktomyOrders = (TextView) alertlayout.findViewById(R.id.tvBacktomyOrders);
        ImageView icon_close = (ImageView) alertlayout.findViewById(R.id.icon_close);
        offerDetails.setText(strNotiMessage);
        offerTitle.setText(strNotiTitle);
        Glide.with(getActivity())
                .load(strNotiImage)
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.ic_default_product_one)
                .into(offerIMG);

        icon_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packsizeDialog.dismiss();
                //   isIMG = false;

            }
        });

        tvRateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packsizeDialog.dismiss();
                //   isIMG = false;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
                startActivity(intent);

            }
        });

        tvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packsizeDialog.dismiss();
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                //   isIMG = false;
            }
        });

        tvBacktomyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packsizeDialog.dismiss();
                //   isIMG = false;
                Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        builder.setView(alertlayout);
        packsizeDialog = builder.create();

        packsizeDialog.getWindow().getAttributes().windowAnimations = R.style.OfferDialogTheme;
        packsizeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = packsizeDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        lp.copyFrom(window.getAttributes());

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        lp.width = size.x;
        lp.height = size.y;
        // This makes the dialog take up the full width
        window.setAttributes(lp);
        packsizeDialog.show();
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (global.isNetworkAvailable()) {
                        dialog.dismiss();
                        loading = ProgressDialog.show(getActivity(), "", "Please wait...", false, false);
                        GetSubCategoryDataTask mGetSubCategoryDataTask = new GetSubCategoryDataTask();
                        mGetSubCategoryDataTask.execute();
                    } else {
                        Utils.ShowSnakBar("No internet availbale", relativeMain, getActivity());

                    }
                } catch (Exception Ex) {
                    Log.e("hii", "hellow" + Ex.getMessage());
                }
            }
        });
        dialog.show();
    }

    private static boolean compare(String v1, String v2) {
        String s1 = normalisedVersion(v1);
        String s2 = normalisedVersion(v2);
        int cmp = s1.compareTo(s2);
        String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        System.out.printf("result: " + "'%s' %s '%s'%n", v1, cmpStr, v2);
        if (cmpStr.contains("<")) {
            return true;
        }
        if (cmpStr.contains(">") || cmpStr.contains("==")) {
            return false;
        }
        return false;
    }

    public static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    public static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }

    protected void showUpdateDialog(String msg) {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Update Available")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        getActivity().finishAffinity();
                        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            Log.e("cartActivity", "cartActivity" + e.getMessage());

                        }
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finishAffinity();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void retryConnection() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_noconnection);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curators == null) {
                    dialog.dismiss();
                    if (!isShow) {
                        isShow = true;
                        loading = ProgressDialog.show(getContext(), "", "Please wait...", false, false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            }

                            private void runOnUiThread(Runnable runnable) {
                                loading.dismiss();
                                dialog.show();
                                isShow = false;
                            }
                        }, 4000);
                    }
                } else {
                    loading.dismiss();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void clickb(String s) {
        imgHome.setColorFilter(getResources().getColor(R.color.boticon), android.graphics.PorterDuff.Mode.MULTIPLY);
        imgStore.setColorFilter(getResources().getColor(R.color.boticon), android.graphics.PorterDuff.Mode.MULTIPLY);
        imgWishlist.setColorFilter(getResources().getColor(R.color.boticon), android.graphics.PorterDuff.Mode.MULTIPLY);
        imgOffer.setColorFilter(getResources().getColor(R.color.boticon), android.graphics.PorterDuff.Mode.MULTIPLY);
        imgmyaccount.setColorFilter(getResources().getColor(R.color.boticon), android.graphics.PorterDuff.Mode.MULTIPLY);
        if (s.equalsIgnoreCase("1")) {
            Intent intent = new Intent(getActivity(), NewCategoryActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (s.equalsIgnoreCase("2")) {
            Intent intent = new Intent(getActivity(), ContactusActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (s.equalsIgnoreCase("3")) {
            Intent intent = new Intent(getActivity(), WishlistActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //    new addWishlist().execute();
        } else if (s.equalsIgnoreCase("4")) {
            getActivity().finish();
            Intent intent = new Intent(getActivity(), OffersActivity.class);
            intent.putExtra("activitytype", "");
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (s.equalsIgnoreCase("5")) {
            Intent intent = new Intent(getActivity(), MyAccountActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
