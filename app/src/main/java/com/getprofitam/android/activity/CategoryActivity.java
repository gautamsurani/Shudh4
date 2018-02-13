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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getprofitam.android.R;
import com.getprofitam.android.adapter.SubCategoryAdapter;
import com.getprofitam.android.dbhelper.GetprofitamDB;
import com.getprofitam.android.model.SubCategoryModel;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.RequestMethod;
import com.getprofitam.android.utils.RestClient;
import com.getprofitam.android.utils.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    public static ExpandableListView expListView;
    Toolbar toolbar;
    Global global;
    Context context;
    ProgressDialog progressDialog;
    List<SubCategoryModel> listDataHeader;
    List<SubCategoryModel> chilement;

    SubCategoryModel myOrderModelParent;
    SubCategoryModel myOrderModelChild;
    HashMap<SubCategoryModel, List<SubCategoryModel>> listDataChild;

    SubCategoryAdapter subCategoryAdapter;
    String subCatId = "";
    String subCatName = "";
    SharedPreferences mprefs;
    String strUserId = "";
    GetprofitamDB getprofitamDB;
    Cursor cursorCart;
    TextView tvmytotalitems;
    //  DBHandler dbHandler;
    //   int dbWishSize = 0;
    private LinearLayout lyt_not_found;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

        String LocalDbUserSocityIdName = strUserId + "_getprofitam_local.db";
        String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
        getprofitamDB = new GetprofitamDB(CategoryActivity.this, FinalLocalDBName);
        getprofitamDB.OpenDB();
        cursorCart = getprofitamDB.ShowTableCartList();
        initToolbar();
        initComponent();

        tvmytotalitems.setText("" + cursorCart.getCount() + "");


        if (global.isNetworkAvailable()) {
            new loadMainCateogry().execute();
        } else {
            retryInternet();
        }
        Tools.systemBarLolipop(this);
    }


    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        tvmytotalitems = (TextView) toolbar.findViewById(R.id.tvmytotalitems);
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        imgSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(CategoryActivity.this, SearchActivity.class);
                startActivity(it);
            }
        });

        toolbartitle.setText(getResources().getString(R.string.toolbar_category));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComponent() {
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListView.setGroupIndicator(null);

        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
    }


    private class loadMainCateogry extends AsyncTask<String, Void, String> {

        JSONObject jsonObjectList, jsonObjectListChild;
        String resMessage = "";
        String resCode = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.show();
            progressDialog.setMessage("Please wait...");
            listDataHeader = new ArrayList<SubCategoryModel>();
            chilement = new ArrayList<SubCategoryModel>();
            listDataChild = new HashMap<SubCategoryModel, List<SubCategoryModel>>();

        }

        @Override
        protected String doInBackground(String... params) {

            String strloadSubCategory = "http://www.getprofitam.com/sabzi/index.php?view=category&userID=1";


            try {

                RestClient restClient = new RestClient(strloadSubCategory);

                try {
                    restClient.Execute(RequestMethod.GET);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String MySubcategory = restClient.getResponse();


                if (MySubcategory != null && MySubcategory.length() != 0) {
                    jsonObjectList = new JSONObject(MySubcategory);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        try {
                            if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                if (resCode.equalsIgnoreCase("0")) {
                                    JSONArray jsonArraySubcat = jsonObjectList.getJSONArray("category");
                                    {
                                        if (jsonArraySubcat != null && jsonArraySubcat.length() != 0) {
                                            for (int i = 0; i < jsonArraySubcat.length(); i++) {
                                                JSONObject jsonObjectList = jsonArraySubcat.getJSONObject(i);
                                                myOrderModelParent = new SubCategoryModel();
                                                chilement = new ArrayList<SubCategoryModel>();
                                                myOrderModelParent.setCatID(jsonObjectList.getString("catID"));
                                                myOrderModelParent.setName(jsonObjectList.getString("name"));
                                                myOrderModelParent.setIcon(jsonObjectList.getString("icon"));

                                                String strSubCatcheck = jsonObjectList.getString("subcategory_list");

                                                if (!strSubCatcheck.equalsIgnoreCase("")) {
                                                    JSONArray jsonObjectSubsubCats = jsonObjectList.getJSONArray("subcategory_list");
                                                    if (jsonObjectSubsubCats != null && jsonObjectSubsubCats.length() != 0) {
                                                        for (int j = 0; j < jsonObjectSubsubCats.length(); j++) {
                                                            if (jsonObjectSubsubCats.length() != 0) {
                                                                jsonObjectListChild = jsonObjectSubsubCats.getJSONObject(j);
                                                                myOrderModelChild = new SubCategoryModel();
                                                                myOrderModelChild.setChildcatId(jsonObjectListChild.getString("catID"));
                                                                myOrderModelChild.setChildcatName(jsonObjectListChild.getString("name"));
                                                                myOrderModelChild.setChildcaticon(jsonObjectListChild.getString("icon"));
                                                                chilement.add(myOrderModelChild);

                                                            }
                                                        }
                                                    }

                                                }
                                                listDataHeader.add(myOrderModelParent);
                                                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), chilement);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (listDataHeader.size() == 0) {
                // recyclerView.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);

            } else {
                lyt_not_found.setVisibility(View.GONE);
                expListView.setVisibility(View.VISIBLE);
                //   lyt_not_found.setVisibility(View.GONE);
            }


            subCategoryAdapter = new SubCategoryAdapter(CategoryActivity.this, listDataHeader, listDataChild);
            expListView.setAdapter(subCategoryAdapter);
            subCategoryAdapter.notifyDataSetChanged();

            expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if (subCategoryAdapter.getChildrenCount(groupPosition) == 0) {
                        Intent it = new Intent(context, ProductListActivity.class);
                        it.putExtra("catID", listDataHeader.get(groupPosition).getCatID());
                        it.putExtra("catName", listDataHeader.get(groupPosition).getName());
                        startActivity(it);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                    }

                    return false;
                }
            });
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                    Intent it = new Intent(context, ProductListActivity.class);
                    it.putExtra("catID", listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getChildcatId());
                    it.putExtra("catName", listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getChildcatName());

                    startActivity(it);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);

                    return true;
                }
            });

        }
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    // new loadMainCateogry().execute();
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

}
