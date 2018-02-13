package com.shudh4sure.shopping.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.dbhelper.Shudh4sureDB;
import com.shudh4sure.shopping.model.CartData;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;

public class PaymentPageActivity extends AppCompatActivity {
    Global global;
    String strUserId = "";
    String strPhone = "";
    SharedPreferences mprefs;
    Shudh4sureDB shudh4sureDB;
    Cursor cursorCart;
    Toolbar toolbar;
    TextView checkOut, orderHideCoupon, tvPaymentAmount, tvPaymentItemtotal, tvPaymentSubtotal, tvPaymentDeliveryCharge, tvPaymentGrandTotal, tvPaymentWalletbalance, tvPaymentCODCharge, tvPaymentCouponcode, tvPaymentCODChargeDropdown;
    TextView tvPaymentWalletCharge, tvTotalPayable, tvTotalPayableamountDropDown, tvFailCoupon, tvSucessCoupon, tvExCharge;
    CheckBox cbCashOnDelivery, cbPaytmWallet, cbPayUmoney, szwalletCheck, cbExDel;
    Button btnApplyCoupon;
    EditText etPaymentCoupencode;
    LinearLayout couponLayout;
    LinearLayout lvTotalpayableamount, lvOrderAmountSummary, llExCharge;
    double areaChargeCheck = 0;
    ProgressDialog loading;
    String pay_cod = "";
    String pay_paytm = "";
    String pay_payu = "";
    double original_sub_total = 0;
    double original_grand_total = 0;
    double original_wallet_bal = 0;
    double original_cod_charges = 0;
    double original_coupon_value = 0;
    String coupon_msg = "", express_charges = "";
    double intAreshipcharge = 0;
    String cart_amount = "";
    String cart_msg = "";
    RelativeLayout relSabzilanaWallet, relCashOnDelivery, relPaytmWallet, relPayumoney, rlExDel;
    PayuHashes payuHashes;
    String isExpress = "No";
    String strPaymentMode = "";
    boolean boolWallet = false;
    boolean boolCOD = false;
    String pay_user_name = "", pay_user_email = "", pay_user_phone = "", pay_user_address1 = "",
            pay_user_area = "", pay_user_city = "", pay_user_state = "", pay_zipcode = "",
            pay_map_address = "", pay_user_flat = "", pay_google_location = "", pay_dicount_id = "",
            pay_discount_amount = "", address_area_id = "", address_area_shipcharge = "",
            pay_delivery_date = "", pay_delivery_time = "", strSpecialIntraction = "";
    PaymentParams mPaymentParams;
    PayuConfig payuConfig;
    List<CartData> CartList = new ArrayList<>();
    String payu_order_fail = "", payu_order_success = "", cod_success = "";
    boolean paytmOrder = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Payu.setInstance(PaymentPageActivity.this);
        setContentView(R.layout.activity_paymentpageview);
        global = new Global(this);

        try {
            mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
            strUserId = mprefs.getString(AppConstant.USER_ID, null);
            strPhone = mprefs.getString(AppConstant.USER_MOBILE, null);
            cart_amount = mprefs.getString(AppConstant.CART_AMOUNT, null);
            cart_msg = mprefs.getString(AppConstant.CART_MESSAGE, null);
            strSpecialIntraction = mprefs.getString(AppConstant.SPECIAL_INSTRUCTION, null);
            String LocalDbUserSocityIdName = strUserId + "_Shudh4sure_local.db";
            String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
            shudh4sureDB = new Shudh4sureDB(PaymentPageActivity.this, FinalLocalDBName);
            shudh4sureDB.OpenDB();

            areaChargeCheck = Double.parseDouble(cart_amount);
            cursorCart = shudh4sureDB.ShowTableCartList();

            Cursor MyCurserThis = shudh4sureDB.ShowTableCartList();
            for (MyCurserThis.moveToFirst(); !MyCurserThis.isAfterLast(); MyCurserThis.moveToNext()) {
                original_sub_total = original_sub_total + (MyCurserThis.getInt(7) * Double.parseDouble(MyCurserThis.getString(5)));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pay_user_name = bundle.getString("pay_user_name");
            pay_user_email = bundle.getString("pay_user_email");
            pay_user_phone = bundle.getString("pay_user_phone");
            pay_user_address1 = bundle.getString("pay_user_address1");
            pay_user_area = bundle.getString("pay_user_area");
            pay_user_city = bundle.getString("pay_user_city");
            pay_user_state = bundle.getString("pay_user_state");
            pay_zipcode = bundle.getString("pay_zipcode");
            pay_map_address = bundle.getString("pay_map_address");
            pay_user_flat = bundle.getString("pay_user_flat");
            pay_google_location = bundle.getString("pay_google_location");
            pay_dicount_id = bundle.getString("pay_dicount_id");
            pay_discount_amount = bundle.getString("pay_discount_amount");
            address_area_id = bundle.getString("address_area_id");
            address_area_shipcharge = bundle.getString("address_area_shipcharge");
            pay_delivery_date = bundle.getString("pay_delivery_date");
            pay_delivery_time = bundle.getString("pay_delivery_time");
        }
        try {
            intAreshipcharge = Integer.valueOf(address_area_shipcharge);


        } catch (Exception ex) {
            Log.e("Payment", "Activity" + ex.getMessage());
        }
        initToolbar();
        initComponent();
        DisplyLocalDB();

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(PaymentPageActivity.this, "", "Please wait...", false, false);
            OrderFinalCharges getUserInformation = new OrderFinalCharges();
            getUserInformation.execute();
        } else {
            retryInternet();
        }

        tvPaymentAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PaymentPageActivity.this, CartActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        btnApplyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etPaymentCoupencode.getText().toString().trim())) {
                    if (global.isNetworkAvailable()) {
                        Utils.hideKeyboard(PaymentPageActivity.this);
                        loading = ProgressDialog.show(PaymentPageActivity.this, "", getResources().getString(R.string.string_plzwait), false, false);
                        applyCouponasync task = new applyCouponasync();
                        task.execute();
                    } else {
                        retryInternetCoupon();
                    }

                } else {
                    Toast.makeText(PaymentPageActivity.this, "Please enter code first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        orderHideCoupon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if (couponLayout.getVisibility() == View.VISIBLE) {
                            Drawable img = ContextCompat.getDrawable(PaymentPageActivity.this, R.drawable.downsmall);
                            orderHideCoupon.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                            couponLayout.setVisibility(View.GONE);
                        } else {
                            Drawable img = ContextCompat.getDrawable(PaymentPageActivity.this, R.drawable.upsmall);
                            orderHideCoupon.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                            couponLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                return false;
            }
        });

        lvTotalpayableamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lvOrderAmountSummary.getVisibility() == View.VISIBLE) {
                    lvOrderAmountSummary.setVisibility(View.GONE);
                    Drawable img = ContextCompat.getDrawable(PaymentPageActivity.this, R.drawable.downsmall);
                    tvTotalPayableamountDropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                } else {
                    //     tvTotalPayableamount.setdr
                    Drawable img = ContextCompat.getDrawable(PaymentPageActivity.this, R.drawable.upsmall);
                    tvTotalPayableamountDropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);

                    lvOrderAmountSummary.setVisibility(View.VISIBLE);
                }
            }
        });
        szwalletCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    boolWallet = b;
                    cbCashOnDelivery.setEnabled(true);
                    cbPaytmWallet.setChecked(false);
                    cbPayUmoney.setChecked(false);

                    String wallet_bal = tvPaymentWalletbalance.getText().toString().replace("Available balance:" + " " + getResources().getString(R.string.Rs), "").trim();

                    if (Double.parseDouble(wallet_bal) > Double.parseDouble(getMainTotal())) {
                        tvPaymentWalletbalance.setText("Available balance:" + " " + getResources().getString(R.string.Rs) + " " + (Double.parseDouble(wallet_bal) - Double.parseDouble(getMainTotal())));
                        tvPaymentWalletCharge.setText(getResources().getString(R.string.Rs) + " " + Double.parseDouble(getMainTotal()) + "");
                        tvPaymentAmount.setText(getResources().getString(R.string.Rs) + " 0");
                        tvTotalPayable.setText(getResources().getString(R.string.Rs) + " 0");
                        tvTotalPayableamountDropDown.setText(getResources().getString(R.string.Rs) + " 0");
                    } else {
                        tvPaymentWalletbalance.setText("Available balance:" + " " + getResources().getString(R.string.Rs) + " 0");
                        tvPaymentWalletCharge.setText(getResources().getString(R.string.Rs) + " " + wallet_bal);
                        tvPaymentAmount.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(getMainTotal()) - Double.parseDouble(wallet_bal)));
                        tvTotalPayable.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(getMainTotal()) - Double.parseDouble(wallet_bal)));
                        tvTotalPayableamountDropDown.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(getMainTotal()) - Double.parseDouble(wallet_bal)));
                    }

                } else {
                    boolWallet = false;
                    String wallet_bal = tvPaymentWalletbalance.getText().toString().replace("Available balance:" + " " + getResources().getString(R.string.Rs), "").trim();

                    if (wallet_bal.equals("0")) {
                        tvPaymentWalletCharge.setText(getResources().getString(R.string.Rs) + " " + "0");
                        tvPaymentWalletbalance.setText("Available balance:" + " " + getResources().getString(R.string.Rs) + " " + original_wallet_bal + "");
                        tvPaymentAmount.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(getMainTotal()) + original_wallet_bal));
                        tvTotalPayable.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(getMainTotal()) + original_wallet_bal));
                        tvTotalPayableamountDropDown.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(getMainTotal()) + original_wallet_bal));
                    } else {
                        tvPaymentWalletCharge.setText(getResources().getString(R.string.Rs) + " " + "0");
                        tvPaymentWalletbalance.setText("Available balance:" + " " + getResources().getString(R.string.Rs) + " " + original_wallet_bal + "");
                        tvPaymentAmount.setText(getResources().getString(R.string.Rs) + " " + (original_wallet_bal - Double.parseDouble(wallet_bal)));
                        tvTotalPayable.setText(getResources().getString(R.string.Rs) + " " + (original_wallet_bal - Double.parseDouble(wallet_bal)));
                        tvTotalPayableamountDropDown.setText(getResources().getString(R.string.Rs) + " " + (original_wallet_bal - Double.parseDouble(wallet_bal)));
                    }
                }
            }
        });

        cbCashOnDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    boolCOD = b;
                    if (original_wallet_bal != 0) {
                        szwalletCheck.setEnabled(true);
                    }
                    cbPaytmWallet.setChecked(false);
                    cbPayUmoney.setChecked(false);

                } else {
                    boolCOD = false;
                }
            }
        });
        cbPaytmWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cbPaytmWallet.setChecked(true);
                    cbCashOnDelivery.setChecked(false);
                    szwalletCheck.setChecked(false);
                    cbPayUmoney.setChecked(false);
                }
            }
        });

        cbExDel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isExpress = "Yes";
                    llExCharge.setVisibility(View.VISIBLE);
                    tvExCharge.setText(getResources().getString(R.string.Rs) + " " + express_charges);
                    String grand_total = tvTotalPayableamountDropDown.getText().toString().replace(getResources().getString(R.string.Rs), "").trim();

                    if (grand_total.equals("0")) {

                        if (szwalletCheck.isChecked()) {

                            String Walletbalance = tvPaymentWalletbalance.getText().toString().replace("Available balance:" + " " + getResources().getString(R.string.Rs), "").trim();

                            String WalletCharge = tvPaymentWalletCharge.getText().toString().replace(getResources().getString(R.string.Rs), "").trim();

                            tvPaymentWalletbalance.setText("Available balance:" + " " + getResources().getString(R.string.Rs) + " " + (Double.parseDouble(Walletbalance) - Double.parseDouble(express_charges)));
                            tvPaymentWalletCharge.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(WalletCharge) + Double.parseDouble(express_charges)));
                        }

                    } else {

                        tvTotalPayableamountDropDown.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(express_charges) + Double.parseDouble(grand_total)));
                        tvPaymentAmount.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(express_charges) + Double.parseDouble(grand_total)));
                        tvTotalPayable.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(express_charges) + Double.parseDouble(grand_total)));
                    }
                } else {
                    isExpress = "No";
                    llExCharge.setVisibility(View.GONE);
                    String grand_total = tvTotalPayableamountDropDown.getText().toString().replace(getResources().getString(R.string.Rs), "").trim();

                    if (grand_total.equals("0")) {

                        if (szwalletCheck.isChecked()) {

                            String Walletbalance = tvPaymentWalletbalance.getText().toString().replace("Available balance:" + " " + getResources().getString(R.string.Rs), "").trim();

                            String WalletCharge = tvPaymentWalletCharge.getText().toString().replace(getResources().getString(R.string.Rs), "").trim();

                            tvPaymentWalletbalance.setText("Available balance:" + " " + getResources().getString(R.string.Rs) + " " + (Double.parseDouble(Walletbalance) + Double.parseDouble(express_charges)));
                            tvPaymentWalletCharge.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(WalletCharge) - Double.parseDouble(express_charges)));
                        }

                    } else {
                        tvTotalPayableamountDropDown.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(grand_total) - Double.parseDouble(express_charges)));
                        tvPaymentAmount.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(grand_total) - Double.parseDouble(express_charges)));
                        tvTotalPayable.setText(getResources().getString(R.string.Rs) + " " + (Double.parseDouble(grand_total) - Double.parseDouble(express_charges)));
                    }
                }
            }
        });

        cbPayUmoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // cbPayUmoney.setChecked(true);
                if (b) {
                    cbPayUmoney.setChecked(true);
                    cbPaytmWallet.setChecked(false);
                    cbCashOnDelivery.setChecked(false);
                    szwalletCheck.setChecked(false);
                }
            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (szwalletCheck.isChecked()) {
                    if (global.isNetworkAvailable()) {
                        strPaymentMode = "wallet";
                        System.out.println(generateJson());
                        loading = ProgressDialog.show(PaymentPageActivity.this, "", "Please wait...", false, false);
                        PlaceOrderAsync placeOrderAsync = new PlaceOrderAsync();
                        placeOrderAsync.execute();
                    } else {
                        retryInternetPlaceorder();
                    }
                } else if (cbCashOnDelivery.isChecked()) {

                    if (global.isNetworkAvailable()) {
                        strPaymentMode = "cod";
                        System.out.println(generateJson());
                        System.out.println(" " + strPaymentMode);
                        loading = ProgressDialog.show(PaymentPageActivity.this, "", "Please wait...", false, false);
                        PlaceOrderAsync placeOrderAsync = new PlaceOrderAsync();
                        placeOrderAsync.execute();
                    } else {
                        retryInternetPlaceorder();
                    }
                } else if (cbPaytmWallet.isChecked()) {
                    if (global.isNetworkAvailable()) {
                        strPaymentMode = "paytm";

                        System.out.println(generateJson());
                        loading = ProgressDialog.show(PaymentPageActivity.this, "", "Please wait...", false, false);
                        PlaceOrderAsync placeOrderAsync = new PlaceOrderAsync();
                        placeOrderAsync.execute();
                    } else {
                        retryInternetPlaceorder();
                    }
                } else if (cbPayUmoney.isChecked()) {
                    if (global.isNetworkAvailable()) {
                        strPaymentMode = "payu";

                        System.out.println(generateJson());
                        loading = ProgressDialog.show(PaymentPageActivity.this, "", "Please wait...", false, false);
                        PlaceOrderAsync placeOrderAsync = new PlaceOrderAsync();
                        placeOrderAsync.execute();
                    } else {
                        retryInternetPlaceorder();
                    }
                } else if (szwalletCheck.isChecked() & cbCashOnDelivery.isChecked()) {
                    if (global.isNetworkAvailable()) {
                        strPaymentMode = "wallet";
                        Log.e("strPaymentMode", "wallet");
                        System.out.println(generateJson());
                        loading = ProgressDialog.show(PaymentPageActivity.this, "", "Please wait...", false, false);
                        PlaceOrderAsync placeOrderAsync = new PlaceOrderAsync();
                        placeOrderAsync.execute();
                    } else {
                        retryInternetPlaceorder();
                    }
                } else {
                    Toast.makeText(PaymentPageActivity.this, "Please select payment option", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getMainTotal() {
        return tvTotalPayableamountDropDown.getText().toString().replace(getResources().getString(R.string.Rs), "").trim();
    }

    private void initComponent() {
        btnApplyCoupon = (Button) findViewById(R.id.btnApplyCoupon);
        etPaymentCoupencode = (EditText) findViewById(R.id.etPaymentCoupencode);
        orderHideCoupon = (TextView) findViewById(R.id.orderHideCoupon);
        tvPaymentAmount = (TextView) findViewById(R.id.tvPaymentAmount);
        tvPaymentWalletbalance = (TextView) findViewById(R.id.tvPaymentWalletbalance);
        tvTotalPayableamountDropDown = (TextView) findViewById(R.id.tvTotalPayableamountDropDown);
        tvPaymentItemtotal = (TextView) findViewById(R.id.tvPaymenttotalItem);
        tvPaymentSubtotal = (TextView) findViewById(R.id.tvPaymentSubtotal);
        tvPaymentCODCharge = (TextView) findViewById(R.id.tvPaymentCODcharge);
        tvPaymentCODChargeDropdown = (TextView) findViewById(R.id.tvPaymentCODChargeDropdown);
        tvPaymentCouponcode = (TextView) findViewById(R.id.tvPaymentCouponcode);
        tvPaymentDeliveryCharge = (TextView) findViewById(R.id.tvPaymentDeliveryCharge);
        tvPaymentGrandTotal = (TextView) findViewById(R.id.tvPaymentGrandTotal);
        tvPaymentWalletCharge = (TextView) findViewById(R.id.tvPaymentWalletCharge);
        tvTotalPayable = (TextView) findViewById(R.id.tvTotalPayable);
        couponLayout = (LinearLayout) findViewById(R.id.couponLayout);
        lvTotalpayableamount = (LinearLayout) findViewById(R.id.lvTotalpayableamount);
        lvOrderAmountSummary = (LinearLayout) findViewById(R.id.lvOrderAmountSummary);
        checkOut = (TextView) findViewById(R.id.checkOut);
        tvFailCoupon = (TextView) findViewById(R.id.tvFailCoupon);
        tvSucessCoupon = (TextView) findViewById(R.id.tvSucessCoupon);
        tvTotalPayable = (TextView) findViewById(R.id.tvTotalPayable);
        relSabzilanaWallet = (RelativeLayout) findViewById(R.id.relSabzilanaWallet);
        relCashOnDelivery = (RelativeLayout) findViewById(R.id.relCashOnDelivery);
        relPaytmWallet = (RelativeLayout) findViewById(R.id.relPaytmWallet);
        relPayumoney = (RelativeLayout) findViewById(R.id.relPayumoney);
        szwalletCheck = (CheckBox) findViewById(R.id.szwalletCheck);
        cbCashOnDelivery = (CheckBox) findViewById(R.id.cbCashOnDelivery);
        cbPaytmWallet = (CheckBox) findViewById(R.id.cbPaytmWallet);
        cbPayUmoney = (CheckBox) findViewById(R.id.cbPayUmoney);
        cbExDel = (CheckBox) findViewById(R.id.cbExDel);
        rlExDel = (RelativeLayout) findViewById(R.id.rlExDel);
        llExCharge = (LinearLayout) findViewById(R.id.llExCharge);
        tvExCharge = (TextView) findViewById(R.id.tvExCharge);
    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_paymnetoption));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void DisplyLocalDB() {
        if (CartList.size() > 0) {
            CartList.clear();
        }
        try {
            Cursor c = shudh4sureDB.ShowTableCartList();

            if (c.getCount() > 0) {
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                    CartData mWishListData;
                    mWishListData = new CartData(c.getString(1), c.getString(2), c.getString(3), c.getString(4),
                            c.getString(5), c.getString(6), c.getInt(7), c.getString(8), c.getString(9));
                    CartList.add(mWishListData);
                }
            }
        } catch (Exception e) {
            Utils.showToastShort(e.getMessage(), PaymentPageActivity.this);

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class OrderFinalCharges extends AsyncTask<Void, Void, Api_Model> {

        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);

                return methods.OrderFinalCharges("final_ship_charges", strUserId, strPhone, String.valueOf(original_sub_total), address_area_id);
            } catch (Exception E) {

                return null;
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();

            if (curators != null) {
                if (curators.msgcode.equals("0")) {

                    for (final Api_Model.final_charges dataset : curators.final_charges) {
                        pay_cod = dataset.cod;
                        pay_paytm = dataset.paytm;
                        pay_payu = dataset.payu;
                        original_wallet_bal = Double.parseDouble(dataset.wallet_bal);
                        original_cod_charges = Double.parseDouble(dataset.cod_charges);

                        if (dataset.express.equals("No")) {
                            rlExDel.setVisibility(View.GONE);
                        }

                        cbExDel.setText(dataset.express_label);

                        express_charges = dataset.express_charges;

                        tvPaymentWalletbalance.setText("Available balance:" + " " + getResources().getString(R.string.Rs) + " " + original_wallet_bal + "");
                        if (original_wallet_bal == 0) {
                            szwalletCheck.setEnabled(false);
                        }
                        if (pay_cod.equalsIgnoreCase("Yes")) {
                            tvPaymentCODCharge.setText("COD Charges: " + " " + getResources().getString(R.string.Rs) + " " + original_cod_charges + "");
                            tvPaymentCODChargeDropdown.setText(getResources().getString(R.string.Rs) + " " + "0" + "");
                        } else {
                            //  tvPaymentCODChargeDropdown.setText(getResources().getString(R.string.Rs) + " " +"0"+"");
                            relCashOnDelivery.setVisibility(View.GONE);
                        }
                        if (!pay_paytm.equalsIgnoreCase("Yes")) {
                            relPaytmWallet.setVisibility(View.GONE);
                        }
                        if (!pay_payu.equalsIgnoreCase("Yes")) {
                            relPayumoney.setVisibility(View.GONE);
                        }

                        tvPaymentSubtotal.setText(getResources().getString(R.string.Rs) + " " + original_sub_total + "");

                        if (original_sub_total < areaChargeCheck) {
                            tvPaymentDeliveryCharge.setText(getResources().getString(R.string.Rs) + " " + intAreshipcharge + "");
                            original_grand_total = (original_sub_total + intAreshipcharge);
                        } else {
                            tvPaymentDeliveryCharge.setText(getResources().getString(R.string.Rs) + " " + 0 + "");
                            original_grand_total = (original_sub_total + 0);
                        }

                        tvTotalPayableamountDropDown.setText(getResources().getString(R.string.Rs) + " " + original_grand_total + "");
                        tvPaymentGrandTotal.setText(getResources().getString(R.string.Rs) + " " + original_grand_total + "");
                        tvPaymentAmount.setText(getResources().getString(R.string.Rs) + " " + original_grand_total + "");
                        tvTotalPayable.setText(getResources().getString(R.string.Rs) + " " + original_grand_total + "");
                        //tvPaymentCODCharge.setText(getResources().getString(R.string.Rs) + " " + original_cod_charges + "");
                        if (cursorCart.getCount() > 1) {
                            tvPaymentItemtotal.setText("" + cursorCart.getCount() + " " + "Items");
                        } else {
                            tvPaymentItemtotal.setText("" + cursorCart.getCount() + " " + "Item");
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PlaceOrderAsync extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL)
                    .build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                return methods.PlaceOrder("checkout", generateJson());
            } catch (Exception E) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            if (curators != null) {
                switch (curators.msgcode) {
                    case "9":
                        payu_order_fail = curators.payu_order_fail;
                        payu_order_success = curators.payu_order_success;
                        try {
                            for (Api_Model.online_payment_data dataset : curators.online_payment_data) {
                                Intent intent = new Intent(PaymentPageActivity.this, com.payu.payuui.Activity.PayUBaseActivity.class);
                                mPaymentParams = new PaymentParams();
                                payuConfig = new PayuConfig();
                                payuHashes = new PayuHashes();
                                mPaymentParams.setKey(dataset.payu_key);
                                mPaymentParams.setAmount(dataset.amount);
                                mPaymentParams.setProductInfo(dataset.product_info);
                                mPaymentParams.setFirstName(dataset.first_name);
                                mPaymentParams.setEmail(dataset.email);
                                mPaymentParams.setTxnId(dataset.trans_id);
                                mPaymentParams.setSurl("http://www.viragtraders.com/sabzi18/sabzi/index.php?view=order_result");
                                mPaymentParams.setFurl("http://www.viragtraders.com/sabzi18/sabzi/index.php?view=order_result");
                                mPaymentParams.setUdf2(dataset.udf2);
                                mPaymentParams.setUdf3("");
                                mPaymentParams.setUdf4("");
                                mPaymentParams.setUdf5("");
                                mPaymentParams.setUdf1(dataset.udf1);
                                mPaymentParams.setCardBin("");
                                mPaymentParams.setPhone("");
                                mPaymentParams.setUserCredentials(dataset.payu_key + ":" + dataset.email);
                                // payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);
                                payuConfig.setEnvironment(Integer.parseInt("0"));
                                payuHashes.setPaymentHash(dataset.payment_hash);

                                payuHashes.setPaymentRelatedDetailsForMobileSdkHash(dataset.payment_related_details_for_mobile_sdk_hash);
                                payuHashes.setVasForMobileSdkHash(dataset.vas_for_mobile_sdk_hash);
                                mPaymentParams.setHash(dataset.payment_hash);
                                intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                                intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
                                intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
                                try {
                                    startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case "8": {

                        paytmOrder = true;
                        Intent intent = new Intent(PaymentPageActivity.this, PaymentPaytmActivity.class);
                        intent.putExtra("userID", curators.userID);
                        intent.putExtra("Amount", curators.Amount);
                        intent.putExtra("orderID", curators.orderID);
                        startActivityForResult(intent, 1001);
                        break;
                    }
                    case "0": {

                        cod_success = curators.cod_success;
                        String[] itemsDate = cod_success.split("_");
                        Toast.makeText(PaymentPageActivity.this, curators.message, Toast.LENGTH_SHORT).show();
                        shudh4sureDB.deleteAll();
                        Intent intent = new Intent(PaymentPageActivity.this, SuccessorderActivity.class);
                        intent.putExtra("ordertype", "COD");
                        intent.putExtra("msg1", itemsDate[0] + "");
                        intent.putExtra("msg2", itemsDate[1] + "");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        break;
                    }
                    case "1":
                        Toast.makeText(PaymentPageActivity.this, curators.message, Toast.LENGTH_SHORT).show();
                        break;
                    case "2":
                        shudh4sureDB.deleteAll();
                        Toast.makeText(PaymentPageActivity.this, curators.message, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class applyCouponasync extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;
        String applyCoupon = etPaymentCoupencode.getText().toString();

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();

        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                return methods.ApplyCoupon("coupon", strUserId, String.valueOf(original_sub_total), applyCoupon);
            } catch (Exception E) {

                return null;
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();

            if (curators != null) {
                if (curators.msgcode.equals("0")) {
                    original_coupon_value = Double.parseDouble(curators.coupon_value);
                    coupon_msg = curators.coupon_msg;
                    tvSucessCoupon.setVisibility(View.VISIBLE);
                    tvFailCoupon.setVisibility(View.GONE);
                    tvSucessCoupon.setText(curators.coupon_msg);
                    cbCashOnDelivery.setChecked(false);
                    szwalletCheck.setChecked(false);
                    tvPaymentCouponcode.setText("- " + getResources().getString(R.string.Rs) + " " + original_coupon_value + "");
                    tvTotalPayableamountDropDown.setText(getResources().getString(R.string.Rs) + " " + (original_grand_total - original_coupon_value) + "");
                    tvPaymentGrandTotal.setText(getResources().getString(R.string.Rs) + " " + (original_grand_total - original_coupon_value) + "");
                    tvPaymentAmount.setText(getResources().getString(R.string.Rs) + " " + (original_grand_total - original_coupon_value) + "");
                    tvTotalPayable.setText(getResources().getString(R.string.Rs) + " " + (original_grand_total - original_coupon_value) + "");

                } else {
                    original_coupon_value = 0;
                    tvSucessCoupon.setVisibility(View.GONE);
                    tvFailCoupon.setVisibility(View.VISIBLE);
                    tvFailCoupon.setText(curators.message);
                    tvPaymentCouponcode.setText("--- ");
                    cbCashOnDelivery.setChecked(false);
                    szwalletCheck.setChecked(false);
                    tvTotalPayableamountDropDown.setText(getResources().getString(R.string.Rs) + " " + (original_grand_total - original_coupon_value) + "");
                    tvPaymentGrandTotal.setText(getResources().getString(R.string.Rs) + " " + (original_grand_total - original_coupon_value) + "");
                    tvPaymentAmount.setText(getResources().getString(R.string.Rs) + " " + (original_grand_total - original_coupon_value) + "");
                    tvTotalPayable.setText(getResources().getString(R.string.Rs) + " " + (original_grand_total - original_coupon_value) + "");

                }
            }
        }
    }


    public String generateJson() {

        String json = "";
        try {
            String order_total = tvPaymentSubtotal.getText().toString().replace(getResources().getString(R.string.Rs), "").trim();
            //  Log.e("order_total",order_total.toString());

            String grand_total = tvPaymentAmount.getText().toString().replace(getResources().getString(R.string.Rs), "").trim();
            //  Log.e("grand_total",grand_total.toString());


            JSONObject jsonUserBasic = new JSONObject();

            jsonUserBasic.put("userID", strUserId);
            jsonUserBasic.put("payment_method", strPaymentMode);
            jsonUserBasic.put("DIS_ID", pay_dicount_id);
            jsonUserBasic.put("DIS_AMOUNT", pay_discount_amount);
            jsonUserBasic.put("order_total", order_total);
            jsonUserBasic.put("grand_total", grand_total);
            if (original_sub_total < areaChargeCheck) {
                jsonUserBasic.put("ship_charge", address_area_shipcharge);
            } else {
                jsonUserBasic.put("ship_charge", "0");
            }
            jsonUserBasic.put("del_date", pay_delivery_date);
            jsonUserBasic.put("del_time", pay_delivery_time);
            jsonUserBasic.put("instruction", strSpecialIntraction);//CartActivity.etSpecialIntruction.getText().toString()
            jsonUserBasic.put("latitude", "");
            jsonUserBasic.put("longitude", "");
            jsonUserBasic.put("exp_delivery", isExpress);

            JSONObject jsonObjectUserDetails = new JSONObject();
            jsonObjectUserDetails.put("name", pay_user_name);
            jsonObjectUserDetails.put("email", pay_user_email);
            jsonObjectUserDetails.put("phone", pay_user_phone);
            jsonObjectUserDetails.put("address_1", pay_user_address1);
            jsonObjectUserDetails.put("area", pay_user_area);
            jsonObjectUserDetails.put("city", pay_user_city);
            jsonObjectUserDetails.put("state", pay_user_state);
            jsonObjectUserDetails.put("zipcode", pay_zipcode);
            jsonObjectUserDetails.put("map_address", pay_map_address);
            jsonObjectUserDetails.put("flat", pay_user_flat);
            jsonObjectUserDetails.put("google_location", "No");

            jsonUserBasic.put("user_details", jsonObjectUserDetails);

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < CartList.size(); i++) {
                JSONObject jsonOrder = new JSONObject();
                jsonOrder.put("productID", CartList.get(i).getProductID());
                jsonOrder.put("price_ID", CartList.get(i).getStrPriceId());
                jsonOrder.put("quantity", String.valueOf(CartList.get(i).getCountThis()));
                jsonOrder.put("price", CartList.get(i).getPrice());
                jsonArray.put(jsonOrder);
            }
            jsonUserBasic.put("cart_items", jsonArray);

            json = jsonUserBasic.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(PaymentPageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(PaymentPageActivity.this, "", "Please wait...", false, false);
                    OrderFinalCharges getUserInformation = new OrderFinalCharges();
                    getUserInformation.execute();
                } else {
                    Toast.makeText(PaymentPageActivity.this, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }


    public void retryInternetPlaceorder() {
        final Dialog dialog = new Dialog(PaymentPageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(PaymentPageActivity.this, "", "Please wait...", false, false);
                    PlaceOrderAsync placeOrderAsync = new PlaceOrderAsync();
                    placeOrderAsync.execute();
                } else {
                    Toast.makeText(PaymentPageActivity.this, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    public void retryInternetCoupon() {
        final Dialog dialog = new Dialog(PaymentPageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(PaymentPageActivity.this, "", getResources().getString(R.string.string_plzwait), false, false);
                    applyCouponasync task = new applyCouponasync();
                    task.execute();
                } else {
                    Toast.makeText(PaymentPageActivity.this, R.string.nonetwork, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Control will come back to this  place when transaction completed(for both fail and success)
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null)
                    Log.e("Success", "Success");
                String[] itemsDate = payu_order_success.split("_");
                shudh4sureDB.deleteAll();
                Intent intent = new Intent(PaymentPageActivity.this, SuccessorderActivity.class);
                intent.putExtra("ordertype", "PAYU");
                intent.putExtra("msg1", itemsDate[0] + "");
                intent.putExtra("msg2", itemsDate[1] + "");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                if (data != null)
                    Log.e("Fail", "Fail");
                String[] itemsDate = payu_order_fail.split("_");
                shudh4sureDB.deleteAll();
                Intent intensdfcvsdvct = new Intent(PaymentPageActivity.this, FailOrderActivity.class);
                intensdfcvsdvct.putExtra("ordertype", "PAYU");

                try {
                    intensdfcvsdvct.putExtra("msg1", itemsDate[0] + "");
                    if (itemsDate.length > 1) {
                        intensdfcvsdvct.putExtra("msg2", itemsDate[1] + "");
                    } else {
                        intensdfcvsdvct.putExtra("msg2", "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intensdfcvsdvct);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        }
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getStringExtra("status").equals("success")) {
                    shudh4sureDB.deleteAll();
                    Intent intent = new Intent(PaymentPageActivity.this, SuccessorderActivity.class);
                    intent.putExtra("msg1", data.getStringExtra("msg1"));
                    intent.putExtra("msg2", data.getStringExtra("msg2"));
                    intent.putExtra("ordertype", "PAYTM");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (data.getStringExtra("status").equals("failed")) {
                    shudh4sureDB.deleteAll();
                    Intent intent = new Intent(PaymentPageActivity.this, FailOrderActivity.class);
                    intent.putExtra("msg1", data.getStringExtra("msg1"));
                    intent.putExtra("msg2", data.getStringExtra("msg2"));
                    intent.putExtra("ordertype", "PAYTM");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            }
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
        Utils.hideKeyboard(PaymentPageActivity.this);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
