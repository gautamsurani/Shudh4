package com.getprofitam.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.getprofitam.android.R;
import com.getprofitam.android.adapter.CartAdapter;
import com.getprofitam.android.dbhelper.GetprofitamDB;
import com.getprofitam.android.model.CartData;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    public static TextView tvCartTotalAmount;
    EditText etSpecialIntruction;
    public CartAdapter mCartAdapter;
    Toolbar toolbar;
    Button placeOrderBtn;
    String strUserId = "";
    SharedPreferences mprefs;
    RecyclerView rvMyCart;
    ProgressDialog progressDialog;
    GetprofitamDB getprofitamDB;
    Cursor cursorCart;
    RelativeLayout relativeMain;
    TextView tvCartTotalItems, tvShippingchargeInfo;
    TextView tvCartcharge;
    int TotalCount = 0;
    String cart_amount = "";
    String cart_msg = "";
    double double_cart_amount = 0;
    LinearLayout lvSpecialIntraction;
    private ArrayList<CartData> CartList = new ArrayList<CartData>();
    private ProgressBar progressBar1;
    private LinearLayout lyt_not_found, infoLin;
    boolean isShow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_items);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        try {
            cart_amount = mprefs.getString(AppConstant.CART_AMOUNT, null);
            cart_msg = mprefs.getString(AppConstant.CART_MESSAGE, null);
            Answers.getInstance().logContentView(new ContentViewEvent().putContentId(strUserId));
            if (cart_amount != null && !cart_amount.isEmpty()) {
                double_cart_amount = Double.parseDouble(cart_amount);
            }

            String LocalDbUserSocityIdName = strUserId + "_getprofitam_local.db";
            String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
            getprofitamDB = new GetprofitamDB(CartActivity.this, FinalLocalDBName);
            getprofitamDB.OpenDB();
            cursorCart = getprofitamDB.ShowTableCartList();
        } catch (NumberFormatException e) {
            Log.e("cart", "Activity" + e.getMessage());
        }

        initComp();
        initToolbar();
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(CartActivity.this);
        rvMyCart.setLayoutManager(mLayoutManager);
        rvMyCart.setHasFixedSize(true);
        rvMyCart.addItemDecoration(new SpacesItemDecoration());
//9ba4ab
        mCartAdapter = new CartAdapter(CartActivity.this, CartList);
        rvMyCart.setAdapter(mCartAdapter);
        tvShippingchargeInfo.setText(cart_msg);
        mCartAdapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {
                CartData mWishListData = CartList.get(position);
                if (which == 0) {
                    try {
                        if (CartList.size() > 0) {
                           /* Log.e("getPrice :", mWishListData.getPrice());
                            Log.e("getCountThis :", String.valueOf(mWishListData.getCountThis()));
                            Log.e("original_sub_total :", String.valueOf(TotalCount));
*/
                            //double ThisTotal=mLocalDecorntDB.getTotalPriceCount(mWishListData.getStrPriceId());
                            double ThisTotal = Double.parseDouble(mWishListData.getPrice()) * Double.parseDouble(String.valueOf(mWishListData.getCountThis()));
                            //  Log.e("ThisTotal :", String.valueOf(ThisTotal));   // Last Price

                            double LastCount = TotalCount - ThisTotal;
                            //      Log.e("LastCount :", String.valueOf(LastCount));   // Deleted Price
                            DecimalFormat df = new DecimalFormat("#.00"); // df.format(LastCount)
                            tvCartTotalAmount.setText(getResources().getString(R.string.Rs) + " " + df.format(LastCount));
                            TotalCount = (int) LastCount;
                            getprofitamDB.DeleteCartListByPriceID(mWishListData.getStrPriceId());
                            CartList.remove(position);
                            Utils.ShowSnakBar("Removed successfully", relativeMain, CartActivity.this);
                            DisplyLocalDB();
                            if (TotalCount < double_cart_amount) {
                                //  tvCartcharge.setVisibility(View.VISIBLE);
                                //   tvCartcharge.setText(cart_msg);
                            } else {
                                // tvCartcharge.setVisibility(View.GONE);
                            }

                            mCartAdapter.notifyDataSetChanged();
                        } else if (CartList.isEmpty()) {
                            tvCartTotalAmount.setText("0.00");
                            mCartAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        Log.e("Cart", "Activity" + e.getMessage());
                    }
                } else if (which == 1) {
                    if (mWishListData.getCountThis() == 1) {
                        mWishListData.countThis = 1;
                        //  holder.tvlistproductsize.setText(String.valueOf(bean.countThis));
                    } else if (mWishListData.getCountThis() > 1) {
                        mWishListData.countThis--;
                        int TotalCountsdwe = 0;
                        Cursor MyCurserThis = getprofitamDB.ShowTableCartList();
                        for (MyCurserThis.moveToFirst(); !MyCurserThis.isAfterLast(); MyCurserThis.moveToNext()) {
                            TotalCountsdwe = TotalCountsdwe + (MyCurserThis.getInt(7) * Integer.parseInt(MyCurserThis.getString(5)));
                        }
                        TotalCount = TotalCountsdwe;
                        tvCartTotalAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");
                        //    Utils.ShowSnakBar("Quantity Updated Successfully", relativeMain, CartActivity.this);
                        if (TotalCount < double_cart_amount) {
                            //   tvCartcharge.setVisibility(View.VISIBLE);
                            //  tvCartcharge.setText(cart_msg);
                        } else {
                            //   tvCartcharge.setVisibility(View.GONE);
                        }
                    }
                } else if (which == 2) {

                    mWishListData.countThis++;
                    int TotalCountxsx = 0;
                    Cursor MyCurserThis = getprofitamDB.ShowTableCartList();
                    for (MyCurserThis.moveToFirst(); !MyCurserThis.isAfterLast(); MyCurserThis.moveToNext()) {
                        TotalCountxsx = TotalCountxsx + (MyCurserThis.getInt(7) * Integer.parseInt(MyCurserThis.getString(5)));
                    }
                    TotalCount = TotalCountxsx;
                    tvCartTotalAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");
                    //   Utils.ShowSnakBar("Quantity Updated Successfully", relativeMain, CartActivity.this);
                    if (TotalCount < double_cart_amount) {
                        //   tvCartcharge.setVisibility(View.VISIBLE);
                        //   tvCartcharge.setText(cart_msg);
                    } else {
                        //   tvCartcharge.setVisibility(View.GONE);
                    }

                }
            }
        });
        DisplyLocalDB();
        Cursor MyCurserThis = getprofitamDB.ShowTableCartList();
        for (MyCurserThis.moveToFirst(); !MyCurserThis.isAfterLast(); MyCurserThis.moveToNext()) {
            TotalCount = TotalCount + (MyCurserThis.getInt(7) * Integer.parseInt(MyCurserThis.getString(5)));
        }
        tvCartTotalAmount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");
        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CartList.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(CartActivity.this, AddressActivity.class);
                    mprefs.edit().putString(AppConstant.SPECIAL_INSTRUCTION, etSpecialIntruction.getText().toString()).apply();
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

            }
        });

        if (cursorCart.getCount() > 1) {
            tvCartTotalItems.setText("" + cursorCart.getCount() + " Items");
        } else {
            tvCartTotalItems.setText("" + cursorCart.getCount() + " Item");
        }
        if (cursorCart.getCount() == 0) {
            lvSpecialIntraction.setVisibility(View.GONE);
        } else {
            lvSpecialIntraction.setVisibility(View.VISIBLE);
        }
        if (TotalCount < double_cart_amount) {
            //  tvCartcharge.setVisibility(View.VISIBLE);
            //   tvCartcharge.setText(cart_msg);
        } else {
            // tvCartcharge.setVisibility(View.GONE);
        }
        infoLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isShow) {
                    isShow = true;
                    //tvCartcharge.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //  tvCartcharge.setVisibility(View.GONE);
                                    isShow = false;
                                }
                            });

                        }
                    }, 2000);
                }

            }
        });
    }

    private void DisplyLocalDB() {
        if (CartList.size() > 0) {
            CartList.clear();
        }
        try {
            Cursor c = getprofitamDB.ShowTableCartList();

            if (c.getCount() > 1) {
                tvCartTotalItems.setText("" + c.getCount() + " Items");
            } else {
                tvCartTotalItems.setText("" + c.getCount() + " Item");
            }
            if (c.getCount() == 0) {
                lvSpecialIntraction.setVisibility(View.GONE);
            } else {
                lvSpecialIntraction.setVisibility(View.VISIBLE);
            }
            if (c.getCount() > 0) {

                lyt_not_found.setVisibility(View.GONE);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                    CartData mWishListData;
                    mWishListData = new CartData(c.getString(1), c.getString(2), c.getString(3), c.getString(4),
                            c.getString(5), c.getString(6), c.getInt(7), c.getString(8), c.getString(9));
                    CartList.add(mWishListData);
                }

                mCartAdapter.notifyDataSetChanged();

            } else {
                lyt_not_found.setVisibility(View.VISIBLE);
                mCartAdapter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            Utils.ShowSnakBar(e.getMessage(), relativeMain, CartActivity.this);

        }
    }


    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_cart));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        imgSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(CartActivity.this, SearchActivity.class);
                startActivity(it);
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComp() {
        placeOrderBtn = (Button) findViewById(R.id.placeOrderBtn);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        rvMyCart = (RecyclerView) findViewById(R.id.rvMyCart);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
        tvShippingchargeInfo = (TextView) findViewById(R.id.tvShippingchargeInfo);
        tvCartTotalItems = (TextView) findViewById(R.id.tvCartTotalItems);
        tvCartTotalAmount = (TextView) findViewById(R.id.tvCartTotalAmount);
//          tvCartcharge = (TextView) findViewById(R.id.tvCartcharge);
        etSpecialIntruction = (EditText) findViewById(R.id.etSpecialIntruction);
        lvSpecialIntraction = (LinearLayout) findViewById(R.id.lvSpecialIntraction);
        tvCartcharge = (TextView) findViewById(R.id.tvCartcharge);
        infoLin = (LinearLayout) findViewById(R.id.infoLin);
    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        DisplyLocalDB();

        Cursor MyCurserThis = getprofitamDB.ShowTableCartList();
        for (MyCurserThis.moveToFirst(); !MyCurserThis.isAfterLast(); MyCurserThis.moveToNext()) {
            original_sub_total = original_sub_total + (MyCurserThis.getInt(7) * Integer.parseInt(MyCurserThis.getString(5)));
        }
        tvCartTotalAmount.setText(getResources().getString(R.string.Rs) + " " + original_sub_total + "");

        if(cursorCart.getCount()>1){
            tvCartTotalItems.setText(""+cursorCart.getCount()+" Items");
        }else {
            tvCartTotalItems.setText(""+cursorCart.getCount()+" Item");
        }
        if(cursorCart.getCount()==0){
            lvSpecialIntraction.setVisibility(View.GONE);
        }else {
            lvSpecialIntraction.setVisibility(View.VISIBLE);
        }
        if(original_sub_total<double_cart_amount){
            tvCartcharge.setVisibility(View.VISIBLE);
            tvCartcharge.setText(cart_msg);
        }else {
            tvCartcharge.setVisibility(View.GONE);
        }

    }*/

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration() {
            this.space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                    getResources().getDisplayMetrics()); //8dp as px, value might be obtained e.g. from dimen resources...
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
                outRect.bottom = 0; //dont forget about recycling...
            }
            if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
                outRect.bottom = space;
                outRect.top = 0;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cartscreen, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_emptybasket) {
            getprofitamDB.deleteAll();
            DisplyLocalDB();
            tvCartTotalAmount.setText(getResources().getString(R.string.Rs) + " " + 0 + "");

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (CartList.isEmpty()) {
            //  Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(CartActivity.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

}
