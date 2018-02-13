package com.shudh4sure.shopping.activity;

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

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.adapter.ProductListNewAdapter;
import com.shudh4sure.shopping.adapter.SearchAdapter;
import com.shudh4sure.shopping.fragment.Dashboard;
import com.shudh4sure.shopping.model.SearchModel;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.Tools;
import com.shudh4sure.shopping.utils.Utils;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {
    Global global;
    ImageView imgNext;
    ArrayList<SearchModel> listuserresults = new ArrayList<SearchModel>();
    SharedPreferences mprefs;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    LinearLayout lyt_not_found, liserach;
    LinearLayoutManager mLayoutManager;
    private Context context;
    private EditText searchresult;
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

        searchresult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listuserresults.clear();
                listuserresults = new ArrayList<SearchModel>();
                try {
                    for (SearchModel c : Dashboard.searchArrayList) {
                        if (c.getName().toLowerCase().contains(s.toString())) {
                            listuserresults.add(c);
                        }
                    }
                    searchAdapter = new SearchAdapter(SearchActivity.this, listuserresults);
                    recyclerView.setAdapter(searchAdapter);
                    searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, View view, int i) {
                            if (i == 1) {
                                if (listuserresults.get(position).getType().equalsIgnoreCase("category")) {
                                    Utils.hideKeyboard(SearchActivity.this);
                                    Intent it = new Intent(SearchActivity.this, ProductListActivity.class);
                                    it.putExtra("catID", listuserresults.get(position).getID());
                                    it.putExtra("catName", listuserresults.get(position).getName());
                                    startActivity(it);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                } else {
                                    Utils.hideKeyboard(SearchActivity.this);
                                    Intent it = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                                    it.putExtra("catID", listuserresults.get(position).getID());
                                    it.putExtra("catName", listuserresults.get(position).getName());
                                    startActivity(it);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            } else {
                            }
                        }
                    });
                    if (searchresult.length() == 0) {
                        listuserresults.clear();
                    }
                    // searchAdapter.notifyDataSetChanged();
                } catch (NullPointerException ne) {
                    ne.getMessage();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        liserach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchresult.length() == 0) {
                    Toast.makeText(SearchActivity.this, "Enter any product name", Toast.LENGTH_SHORT).show();
                } else {
                    Utils.hideKeyboard(SearchActivity.this);
                    ProductListNewAdapter.viewFormatProductList = 0;
                    Intent it = new Intent(context, ProductListSearchActivity.class);
                    it.putExtra("catName", searchresult.getText().toString());
                    startActivity(it);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
            }
        });
        searchresult.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (searchresult.length() == 0) {
                        Toast.makeText(SearchActivity.this, "Enter any product name", Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.hideKeyboard(SearchActivity.this);
                        Intent it = new Intent(context, ProductListSearchActivity.class);
                        it.putExtra("catName", searchresult.getText().toString());
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
        searchresult = (EditText) findViewById(R.id.searchresult);
        searchresult.setFocusableInTouchMode(true);
        requestFocus(searchresult);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSearch);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        liserach = (LinearLayout) findViewById(R.id.liserach);
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
