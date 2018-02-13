package com.shudh4sure.shopping.activity;

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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.adapter.ChildModel;
import com.shudh4sure.shopping.adapter.ItemClickListener;
import com.shudh4sure.shopping.adapter.Section;
import com.shudh4sure.shopping.adapter.SectionedExpandableLayoutHelper;
import com.shudh4sure.shopping.dbhelper.Shudh4sureDB;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Tools;
import com.shudh4sure.shopping.utils.Utils;

import java.util.ArrayList;

import retrofit.RestAdapter;


public class NewCategoryActivity extends AppCompatActivity implements ItemClickListener {

    Toolbar toolbar;
    RecyclerView mRecyclerView;
    ProgressDialog loading;
    SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;
    Shudh4sureDB shudh4sureDB;
    Cursor cursorCart;
    String StrUserId, StrCartCounter;
    SharedPreferences mprefs;
    TextView tvmytotalitems;
    String strUserId = "";
    Global global;
    Context context;
    LinearLayout lyt_not_found;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcategory);

        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

        String LocalDbUserSocityIdName = strUserId + "_Shudh4sure_local.db";
        String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
        shudh4sureDB = new Shudh4sureDB(NewCategoryActivity.this, FinalLocalDBName);
        shudh4sureDB.OpenDB();
        cursorCart = shudh4sureDB.ShowTableCartList();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        tvmytotalitems = (TextView) toolbar.findViewById(R.id.tvmytotalitems);
        ImageView imgCartProduct = (ImageView) toolbar.findViewById(R.id.imgCartProduct);
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        imgSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(NewCategoryActivity.this, SearchActivity.class);
                startActivity(it);
            }
        });
        imgCartProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(NewCategoryActivity.this, CartActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        toolbartitle.setText(getResources().getString(R.string.toolbar_category));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvmytotalitems.setText("" + cursorCart.getCount() + "");
        Tools.systemBarLolipop(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(this, mRecyclerView, this, 2);
        loading = ProgressDialog.show(NewCategoryActivity.this, "", "Please wait...", false, false);
        GetSubCategoryDataTask mGetSubCategoryDataTask = new GetSubCategoryDataTask();
        mGetSubCategoryDataTask.execute();


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
        overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right);
    }


    @Override
    public void itemClicked(ChildModel item) {

        Intent intent = new Intent(NewCategoryActivity.this, ProductListActivity.class);
        intent.putExtra("catID", item.getCatID());
        intent.putExtra("catName", item.getName());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    @Override
    public void itemClicked(Section section) {

        Intent intent = new Intent(NewCategoryActivity.this, ProductListActivity.class);
        intent.putExtra("catID", section.getCatID());
        intent.putExtra("catName", section.getName());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    private class GetSubCategoryDataTask extends AsyncTask<Void, Void,
            Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(AppConstant.API_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    /*.setEndpoint("http://www.decornt.com/mapp")*/
                    .build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.getNewCategory("category");

                return curators;
            } catch (Exception E) {
                Log.i("exception e", E.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            if (curators == null) {
                Utils.showToastShort("Connection Refused... try again!", NewCategoryActivity.this);
            } else {

                Log.i("Curator", curators.toString());

                if (curators.msgcode.equals("0")) {
                    try {

                        for (int i = 0; i < curators.category_list_new.size(); i++) {
                            Section sc = new Section(curators.category_list_new.get(i).catID,
                                    curators.category_list_new.get(i).name,
                                    curators.category_list_new.get(i).icon,
                                    curators.category_list_new.get(i).subcat
                            );

                            if (curators.category_list_new.get(i).subcat.equalsIgnoreCase("yes")) {

                                try {
                                    ArrayList<ChildModel> arrayList = new ArrayList<>();

                                    for (int j = 0; j < curators.category_list_new.get(i).subcat_list.size(); j++) {

                                        if (curators.category_list_new.get(i).subcat_list != null) {

                                            arrayList.add(new ChildModel(curators.category_list_new.get(i).subcat_list.get(j).catID,
                                                    curators.category_list_new.get(i).subcat_list.get(j).name,
                                                    curators.category_list_new.get(i).subcat_list.get(j).icon
                                            ));

                                        }

                                    }

                                    sectionedExpandableLayoutHelper.addSection(sc, arrayList);
                                } catch (Exception e) {
                                    Log.e("OK e", e.getMessage());
                                }


                            } else {
                                sectionedExpandableLayoutHelper.addSection(sc, null);
                            }

                        }

                        sectionedExpandableLayoutHelper.notifyDataSetChanged();


                    } catch (Exception e) {
                        Log.e("Exception e", e.toString());
                    }
                    lyt_not_found.setVisibility(View.GONE);
                } else {
                    lyt_not_found.setVisibility(View.VISIBLE);
                    Utils.showToastShort(curators.message, NewCategoryActivity.this);
                }
            }
        }
    }
}
