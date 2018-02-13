package com.shudh4sure.shopping.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.adapter.AreaSelectAdapter;
import com.shudh4sure.shopping.adapter.PincodeSelectAdapter;
import com.shudh4sure.shopping.model.ArealistModel;
import com.shudh4sure.shopping.model.PincodelistModel;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Utils;

import java.util.ArrayList;

import retrofit.RestAdapter;


/**
 * Created by welcome on 13-10-2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    Global global;
    SharedPreferences mprefs;
    EditText edtUserName, edtEmail, edtMobile, edtPassword, edyCities, edtRefercode, edtOtherArea;
    String StredtUserName = "", StredtEmail = "", StredtMobile = "", StredtPassword = "", strCities = "", strPincode = "", strRefercode = "", strArea = "", strAreaId = "", strOtherArea = "", emailid = "";
    TextView txtConditionRed, txtCreateAccount, txtsignin, tvSpinnerArea, tvSpinnerPincode, showhide, whatsappcontactTv;
    LinearLayout lvOtherArealayout;
    CheckBox checkTurmCondition;
    ArrayList<ArealistModel> arealistModels = new ArrayList<>();
    ArrayList<PincodelistModel> pincodelistModels = new ArrayList<>();
    ArrayList<ArealistModel> arealistresults = new ArrayList<>();
    ArrayList<PincodelistModel> pincodelistresults = new ArrayList<>();

    ProgressDialog loading;
    RelativeLayout relativeMain;
    RecyclerView rvSelectAreaPincode;
    Dialog dialog;
    AreaSelectAdapter adapterArea;
    PincodeSelectAdapter pincodeSelectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setBackgroundDrawableResource(R.drawable.ic_signupbg);
        global = new Global(this);
        //  mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        FetchXmlID();
        txtCreateAccount.setOnClickListener(this);
        txtsignin.setOnClickListener(this);
        txtConditionRed.setOnClickListener(this);
        showhide.setOnClickListener(this);

        try {
            FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(RegisterActivity.this, "", "Please wait...", false, false);
            AreaPincodeAsync mGetProductDetailDataTask = new AreaPincodeAsync();
            mGetProductDetailDataTask.execute();
        } else {
            retryInternet();
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            emailid = bundle.getString("email");
            edtEmail.setText(emailid);
        }
        edyCities.setText("Faridabad");
        whatsappcontactTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + whatsappcontactTv.getText().toString()));
                    startActivity(intent);
                } catch (Exception ex) {
                    Log.e("Tel", "Ex___" + ex.getMessage());
                }
            }
        });
        tvSpinnerArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(RegisterActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                dialog.setContentView(R.layout.row_selectarepincode);
                TextView tvHeadername = (TextView) dialog.findViewById(R.id.tvHeadername);
                final LinearLayout lyt_other_area = (LinearLayout) dialog.findViewById(R.id.lyt_other_area);
                ImageView imgClose = (ImageView) dialog.findViewById(R.id.imgClose);
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        dialog.dismiss();
                    }
                });
                tvHeadername.setText("Choose Area");
                final EditText etSelectedarea = (EditText) dialog.findViewById(R.id.etSelectedarea);
                rvSelectAreaPincode = (RecyclerView) dialog.findViewById(R.id.rvSelectAreaPincode);
                RecyclerView.LayoutManager mLayoutManagermain = new LinearLayoutManager(RegisterActivity.this);
                rvSelectAreaPincode.setLayoutManager(mLayoutManagermain);
                rvSelectAreaPincode.setHasFixedSize(true);

                adapterArea = new AreaSelectAdapter(RegisterActivity.this, arealistModels);
                rvSelectAreaPincode.setAdapter(adapterArea);
                etSelectedarea.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        arealistresults.clear();
                        arealistresults = new ArrayList<ArealistModel>();
                        try {
                            for (ArealistModel c : arealistModels) {
                                if (c.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                    arealistresults.add(c);
                                }
                            }
                            adapterArea = new AreaSelectAdapter(RegisterActivity.this, arealistresults);
                            rvSelectAreaPincode.setAdapter(adapterArea);

                            adapterArea.setOnItemClickListener(new AreaSelectAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position, View view, int which) {

                                    if (which == 1) {
                                        Utils.hideKeyboard(RegisterActivity.this);
                                        lvOtherArealayout.setVisibility(View.GONE);
                                        tvSpinnerArea.setText(arealistresults.get(position).getName());
                                        strAreaId = arealistresults.get(position).getAreaID();
                                        AreaPincodeAsync mGetProductDetailDataTask = new AreaPincodeAsync();
                                        mGetProductDetailDataTask.execute();
                                        dialog.dismiss();
                                    }
                                }
                            });


                            if (arealistresults.size() == 0) {

                                lyt_other_area.setVisibility(View.VISIBLE);
                                lyt_other_area.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Utils.hideKeyboard(RegisterActivity.this);
                                        tvSpinnerArea.setText("Other");
                                        strAreaId = "";
                                        lvOtherArealayout.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                });
                                rvSelectAreaPincode.setVisibility(View.GONE);
                            } else {
                                rvSelectAreaPincode.setVisibility(View.VISIBLE);
                                lyt_other_area.setVisibility(View.GONE);
                                lvOtherArealayout.setVisibility(View.GONE);
                            }
                           /* if (etSelectedarea.length() == 0) {
                                Log.e("etSelectedarea","etSelectedarea");
                                arealistModels.clear();
                            }*/
                            // searchAdapter.notifyDataSetChanged();

                        } catch (NullPointerException ne) {
                            ne.getMessage();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                adapterArea.setOnItemClickListener(new AreaSelectAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view, int which) {

                        if (which == 1) {
                            Utils.hideKeyboard(RegisterActivity.this);
                            lvOtherArealayout.setVisibility(View.GONE);
                            tvSpinnerArea.setText(arealistModels.get(position).getName());
                            strAreaId = arealistModels.get(position).getAreaID();
                            AreaPincodeAsync mGetProductDetailDataTask = new AreaPincodeAsync();
                            mGetProductDetailDataTask.execute();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        tvSpinnerPincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(RegisterActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                dialog.setContentView(R.layout.row_selectarepincode);
                rvSelectAreaPincode = (RecyclerView) dialog.findViewById(R.id.rvSelectAreaPincode);
                TextView tvHeadername = (TextView) dialog.findViewById(R.id.tvHeadername);
                final LinearLayout lyt_not_found = (LinearLayout) dialog.findViewById(R.id.lyt_not_found);
                tvHeadername.setText("Choose Pincode");
                final EditText etSelectedarea = (EditText) dialog.findViewById(R.id.etSelectedarea);
                etSelectedarea.setHint("Search Pincode");
                etSelectedarea.setInputType(InputType.TYPE_CLASS_PHONE);
                ImageView imgClose = (ImageView) dialog.findViewById(R.id.imgClose);
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                RecyclerView.LayoutManager mLayoutManagermain = new LinearLayoutManager(RegisterActivity.this);
                rvSelectAreaPincode.setLayoutManager(mLayoutManagermain);
                rvSelectAreaPincode.setHasFixedSize(true);
                pincodeSelectAdapter = new PincodeSelectAdapter(RegisterActivity.this, pincodelistModels);
                rvSelectAreaPincode.setAdapter(pincodeSelectAdapter);

                etSelectedarea.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        pincodelistresults.clear();
                        pincodelistresults = new ArrayList<PincodelistModel>();
                        try {
                            for (PincodelistModel c : pincodelistModels) {
                                if (c.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                    pincodelistresults.add(c);
                                }
                            }
                            pincodeSelectAdapter = new PincodeSelectAdapter(RegisterActivity.this, pincodelistresults);
                            rvSelectAreaPincode.setAdapter(pincodeSelectAdapter);

                            pincodeSelectAdapter.setOnItemClickListener(new PincodeSelectAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position, View view, int which) {

                                    if (which == 1) {
                                        Utils.hideKeyboard(RegisterActivity.this);
                                        tvSpinnerPincode.setText(pincodelistresults.get(position).getName());
                                        dialog.dismiss();
                                    }
                                }
                            });

                            if (pincodelistresults.size() == 0) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                                rvSelectAreaPincode.setVisibility(View.GONE);
                            } else {
                                rvSelectAreaPincode.setVisibility(View.VISIBLE);
                                lyt_not_found.setVisibility(View.GONE);
                            }

                           /* if (etSelectedarea.length() == 0) {
                                arealistModels.clear();
                            }*/
                            // searchAdapter.notifyDataSetChanged();
                        } catch (NullPointerException ne) {
                            ne.getMessage();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                pincodeSelectAdapter.setOnItemClickListener(new PincodeSelectAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view, int which) {
                        if (which == 1) {
                            Utils.hideKeyboard(RegisterActivity.this);
                            tvSpinnerPincode.setText(pincodelistModels.get(position).getName());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private void FetchXmlID() {
        whatsappcontactTv = (TextView) findViewById(R.id.whatsappcontactTv);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtMobile = (EditText) findViewById(R.id.edtMobile);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        showhide = (TextView) findViewById(R.id.showhide);
        edyCities = (EditText) findViewById(R.id.edyCities);
        edtRefercode = (EditText) findViewById(R.id.edtRefercode);
        edtOtherArea = (EditText) findViewById(R.id.edtOtherArea);
        tvSpinnerArea = (TextView) findViewById(R.id.tvSpinnerArea);
        tvSpinnerPincode = (TextView) findViewById(R.id.tvSpinnerPincode);
        txtConditionRed = (TextView) findViewById(R.id.txtConditionRed);
        txtCreateAccount = (TextView) findViewById(R.id.txtCreateAccount);
        lvOtherArealayout = (LinearLayout) findViewById(R.id.lvOtherArealayout);
        txtsignin = (TextView) findViewById(R.id.txtsignin);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
        checkTurmCondition = (CheckBox) findViewById(R.id.checkTurmCondition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txtConditionRed:
                Intent idd = new Intent(RegisterActivity.this, TermsAndConditionActivity.class);
                startActivity(idd);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.txtCreateAccount:

                StredtUserName = edtUserName.getText().toString();
                StredtEmail = edtEmail.getText().toString();
                StredtMobile = edtMobile.getText().toString();
                StredtPassword = edtPassword.getText().toString();
                strCities = edyCities.getText().toString();
                strRefercode = edtRefercode.getText().toString();
                strPincode = tvSpinnerPincode.getText().toString();
                strArea = tvSpinnerArea.getText().toString();

                if (Utils.isNetworkAvailable(RegisterActivity.this)) {
                    if (TextUtils.isEmpty(StredtUserName)) {
                        Utils.ShowSnakBar("Please enter username", relativeMain, RegisterActivity.this);
                    } else if (TextUtils.isEmpty(StredtEmail)) {
                        Utils.ShowSnakBar("Please enter email", relativeMain, RegisterActivity.this);
                    } else if (!Utils.isValidEmail(StredtEmail)) {
                        Utils.ShowSnakBar("Invalid email", relativeMain, RegisterActivity.this);
                    } else if (TextUtils.isEmpty(StredtMobile)) {
                        Utils.ShowSnakBar("Please enter mobile", relativeMain, RegisterActivity.this);
                    } else if (StredtMobile.length() != 10) {
                        Utils.ShowSnakBar("Invalid mobile", relativeMain, RegisterActivity.this);
                    } else if (TextUtils.isEmpty(StredtPassword)) {
                        Utils.ShowSnakBar("Please enter password", relativeMain, RegisterActivity.this);
                    } else if (StredtPassword.length() < 7) {
                        Utils.ShowSnakBar("Password is short!", relativeMain, RegisterActivity.this);
                    } else if (TextUtils.isEmpty(strArea)) {
                        Utils.ShowSnakBar("Please select area", relativeMain, RegisterActivity.this);
                    } else if (TextUtils.isEmpty(strPincode)) {
                        Utils.ShowSnakBar("Please enter pincode", relativeMain, RegisterActivity.this);
                    } else if (TextUtils.isEmpty(strCities)) {
                        Utils.ShowSnakBar("Please enter city", relativeMain, RegisterActivity.this);
                    } else if (!checkTurmCondition.isChecked()) {
                        Utils.ShowSnakBar("Select " + getResources().getString(R.string.tandc), relativeMain, RegisterActivity.this);
                    } else {
                        Utils.hideKeyboard(RegisterActivity.this);
                        if (global.isNetworkAvailable()) {
                            loading = ProgressDialog.show(RegisterActivity.this, "", "Please wait...", false, false);
                            RegisterAsynTask mRegisterAsynTask = new RegisterAsynTask();
                            mRegisterAsynTask.execute();
                        } else {
                            retryInternetRegister();
                        }
                    }

                } else {
                }
                break;
            case R.id.showhide:
                if (showhide.getText().equals("Hide")) {
                    showhide.setText("Show");
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else if (showhide.getText().equals("Show")) {
                    showhide.setText("Hide");
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                break;
            case R.id.txtsignin:

                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right);
                break;

        }
    }


    private class RegisterAsynTask extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
            strOtherArea = edtOtherArea.getText().toString().trim();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {

                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.GetRegister("register", "signup", StredtUserName, StredtEmail,
                        StredtMobile, StredtPassword, "", strPincode, strAreaId, strCities, strArea, strRefercode, strOtherArea);

                return curators;
            } catch (Exception E) {

                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            if (curators == null) {

            } else {
                if (curators.msgcode.equals("0")) {
                    try {
                        for (Api_Model.user_detail1 dataset : curators.user_detail1) {

                            if (dataset.type.equalsIgnoreCase("OTP_SCREEN")) {
                                Toast.makeText(RegisterActivity.this, curators.message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, OTPActivity.class);
                                intent.putExtra("reg_otp", dataset.otp);
                                intent.putExtra("reg_phone", dataset.phone);
                                intent.putExtra("reg_userid", dataset.userID);
                                intent.putExtra("mobile_type", dataset.mobile_type);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else {
                                global.setPrefBoolean("Verify", true);
                                mprefs.edit().putString(AppConstant.USER_ID, dataset.userID).apply();
                                mprefs.edit().putString(AppConstant.USER_MOBILE, dataset.phone).apply();
                                Intent id = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(id);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }
                    } catch (Exception e) {
                    }

                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, RegisterActivity.this);
                }
            }
        }
    }

    private class AreaPincodeAsync extends AsyncTask<Void, Void,
            Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            arealistModels.clear();
            pincodelistModels.clear();
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {

                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.AreaPincodeList("area_pincode", strAreaId);

                return curators;
            } catch (Exception E) {

                return null;
            }
        }


        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            if (curators == null) {

            } else {
                if (curators.msgcode.equals("0")) {
                    try {
                        for (Api_Model.area_list dataset : curators.area_list) {
                            ArealistModel arealistModel;
                            arealistModel = new ArealistModel(dataset.areaID, dataset.name, dataset.shipping, dataset.on_order);
                            arealistModels.add(arealistModel);
                        }

                        for (Api_Model.pincode_list dataset : curators.pincode_list) {
                            PincodelistModel pincodelistModel;
                            pincodelistModel = new PincodelistModel(dataset.pincodeID, dataset.name);
                            pincodelistModels.add(pincodelistModel);
                        }

                    } catch (Exception e) {
                    }
                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, RegisterActivity.this);
                }
            }
        }
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(RegisterActivity.this, "", "Please wait...", false, false);
                    AreaPincodeAsync mGetProductDetailDataTask = new AreaPincodeAsync();
                    mGetProductDetailDataTask.execute();
                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, RegisterActivity.this);

                }
            }
        });
        dialog.show();
    }

    public void retryInternetRegister() {
        final Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(RegisterActivity.this, "", "Please wait...", false, false);
                    RegisterAsynTask mRegisterAsynTask = new RegisterAsynTask();
                    mRegisterAsynTask.execute();
                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, RegisterActivity.this);
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);*/
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

