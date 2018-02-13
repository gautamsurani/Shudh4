package com.getprofitam.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getprofitam.android.R;
import com.getprofitam.android.adapter.ProductListNewAdapter;
import com.getprofitam.android.adapter.SearchAdapter;
import com.getprofitam.android.fragment.Dashboard;
import com.getprofitam.android.model.SearchModel;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.Tools;
import com.getprofitam.android.utils.Utils;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {
    Global global;
    ImageView imgNext;
    ArrayList<SearchModel> searchModels = new ArrayList<>();
    SharedPreferences mprefs;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    LinearLayout lyt_not_found, liSearch;
    LinearLayoutManager mLayoutManager;
    private Context context;
    private EditText searchResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = this;
        global = new Global(this);

        initComp();
        Tools.systemBarLolipop(this);
        mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        searchResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchModels.clear();
                searchModels = new ArrayList<>();
                try {
                    for (SearchModel c : Dashboard.searchArrayList) {
                        if (c.getName().toLowerCase().contains(s.toString())) {
                            searchModels.add(c);
                        }
                    }
                    searchAdapter = new SearchAdapter(SearchActivity.this, searchModels);
                    recyclerView.setAdapter(searchAdapter);
                    searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, View view, int i) {
                            if (i == 1) {
                                if (searchModels.get(position).getType().equalsIgnoreCase("category")) {
                                    Utils.hideKeyboard(SearchActivity.this);
                                    Intent it = new Intent(SearchActivity.this, ProductListActivity.class);
                                    it.putExtra("catID", searchModels.get(position).getID());
                                    it.putExtra("catName", searchModels.get(position).getName());
                                    startActivity(it);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                } else {
                                    Utils.hideKeyboard(SearchActivity.this);
                                    Intent it = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                                    it.putExtra("catID", searchModels.get(position).getID());
                                    it.putExtra("catName", searchModels.get(position).getName());
                                    startActivity(it);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            }
                        }
                    });
                    if (searchResult.length() == 0) {
                        searchModels.clear();
                    }
                } catch (NullPointerException ne) {
                    ne.getMessage();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        liSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchResult.length() == 0) {
                    Toast.makeText(SearchActivity.this, "Enter any product name", Toast.LENGTH_SHORT).show();
                } else {
                    Utils.hideKeyboard(SearchActivity.this);
                    ProductListNewAdapter.viewFormatProductList = 0;
                    Intent it = new Intent(context, ProductListSearchActivity.class);
                    it.putExtra("catName", searchResult.getText().toString());
                    startActivity(it);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
            }
        });
        searchResult.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (searchResult.length() == 0) {
                        Toast.makeText(SearchActivity.this, "Enter any product name", Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.hideKeyboard(SearchActivity.this);
                        Intent it = new Intent(context, ProductListSearchActivity.class);
                        it.putExtra("catName", searchResult.getText().toString());
                        startActivity(it);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void initComp() {
        searchResult = (EditText) findViewById(R.id.searchresult);
        searchResult.setFocusableInTouchMode(true);
        requestFocus(searchResult);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSearch);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        liSearch = (LinearLayout) findViewById(R.id.liserach);
        imgNext = (ImageView) findViewById(R.id.imgNext);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        ProductListNewAdapter.viewFormatProductList = 0;
        Utils.hideKeyboard(SearchActivity.this);
        super.onBackPressed();
    }
}
