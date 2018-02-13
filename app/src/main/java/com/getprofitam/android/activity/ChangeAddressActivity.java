package com.getprofitam.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.getprofitam.android.R;
import com.getprofitam.android.adapter.AreaSelectAdapter;
import com.getprofitam.android.adapter.PincodeSelectAdapter;
import com.getprofitam.android.model.ArealistModel;
import com.getprofitam.android.model.PincodelistModel;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.GPSService;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.IApiMethods;
import com.getprofitam.android.utils.Tools;
import com.getprofitam.android.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.RestAdapter;


public class ChangeAddressActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    Global global;
    Context context;
    SharedPreferences mprefs;
    String strUserId = "";
    String strName = "";
    String strMobile = "";
    String strUserImage = "";
    String strEmail = "";
    String strAddress = "";
    String strArea = "";
    String strCity = "";
    String strNextDays = "";
    String strNewAreaId = "";
    String strPincode = "", stateNEW = "";
    String strNewAreaChipCharge = "";
    RelativeLayout relativeMain;
    EditText etUname, etUemail, etUphone, etUAddress;
    TextView etUArea, etUPincode, tvChangetoCurrentaddress;
    EditText etUpdateCity;
    Button btnUpdateData;
    ArrayList<ArealistModel> arealistModels = new ArrayList<>();
    ArrayList<PincodelistModel> pincodelistModels = new ArrayList<>();
    ArrayList<ArealistModel> arealistresults = new ArrayList<>();
    //ArrayList<String> availableTime = new ArrayList<>();
    ArrayList<PincodelistModel> pincodelistresults = new ArrayList<>();
    RecyclerView rvSelectAreaPincode;
    AreaSelectAdapter adapterArea;
    PincodeSelectAdapter pincodeSelectAdapter;
    Dialog dialog;
    Button btnChangeAddress;
    private int SPLASH_TIME_OUT = 2000;
    ProgressDialog loading;
    public static final int REQUEST_ID = 2;
    private static final int REQUEST_CHECK_SETTINGS = 151;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeaddress);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strName = bundle.getString("resName");
            strMobile = bundle.getString("resMobile");
            strEmail = bundle.getString("resEmail");
            strUserImage = bundle.getString("resUserImage");

            strAddress = bundle.getString("resAddress");
            strArea = bundle.getString("resArea");
            strNewAreaChipCharge = bundle.getString("resAreaShipCharge");
            strCity = bundle.getString("resCityname");
            strPincode = bundle.getString("resPincode");
            strNextDays = bundle.getString("resNextDays");
            //  availableTime = bundle.getStringArrayList("availabaleTime");
        }

        initToolbar();
        initComp();

        Tools.systemBarLolipop(this);
        etUname.setText(strName);
        etUphone.setText(strMobile);
        etUemail.setText(strEmail);
        etUAddress.setText(strAddress);
        etUpdateCity.setText(strAddress);

        etUArea.setText(strArea);
        etUPincode.setText(strPincode);


        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(ChangeAddressActivity.this, "", "Please wait...", false, false);
            AreaPincodeAsync mGetProductDetailDataTask = new AreaPincodeAsync();
            mGetProductDetailDataTask.execute();
        } else {
            retryInternetForArea();
        }
        btnChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validatefullname()) {
                    return;
                }

                if (!validateemail()) {
                    return;
                }

                if (!validatePhone()) {
                    return;
                }

                if (!validateAdde()) {
                    return;
                }

                if (!validateArea()) {
                    return;
                }

                if (!validateZipcode()) {
                    return;
                }

                if (!validateZipcode()) {
                    return;
                }
                if (!validateCity()) {
                    return;
                }

                Utils.hideKeyboard(ChangeAddressActivity.this);
                finish();
                Intent intent = new Intent(ChangeAddressActivity.this, AddressActivity.class);
                intent.putExtra("activityType", "newAddress");
                intent.putExtra("newUsername", etUname.getText().toString().trim());
                intent.putExtra("newEmail", etUemail.getText().toString().trim());
                intent.putExtra("newMobile", etUphone.getText().toString().trim());
                intent.putExtra("newAddress", etUAddress.getText().toString().trim());
                intent.putExtra("newArea", etUArea.getText().toString().trim());
                intent.putExtra("newAreaId", strNewAreaId);
                intent.putExtra("newAreaShipCharge", strNewAreaChipCharge);
                intent.putExtra("newPincode", etUPincode.getText().toString().trim());
                intent.putExtra("newCity", etUpdateCity.getText().toString().trim());
                intent.putExtra("newState", stateNEW);
                intent.putExtra("strNextDays", strNextDays);
                // intent.putExtra("availabaleTime", availableTime);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);


            }
        });

        etUArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ChangeAddressActivity.this);
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

                RecyclerView.LayoutManager mLayoutManagermain = new LinearLayoutManager(ChangeAddressActivity.this);
                rvSelectAreaPincode.setLayoutManager(mLayoutManagermain);
                rvSelectAreaPincode.setHasFixedSize(true);

                adapterArea = new AreaSelectAdapter(ChangeAddressActivity.this, arealistModels);
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
                            adapterArea = new AreaSelectAdapter(ChangeAddressActivity.this, arealistresults);
                            rvSelectAreaPincode.setAdapter(adapterArea);

                            adapterArea.setOnItemClickListener(new AreaSelectAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position, View view, int which) {

                                    if (which == 1) {
                                        Utils.hideKeyboard(ChangeAddressActivity.this);
                                        //    lvOtherArealayout.setVisibility(View.GONE);
                                        etUArea.setText(arealistresults.get(position).getName());
                                        strNewAreaId = arealistresults.get(position).getAreaID();
                                        strNewAreaChipCharge = arealistresults.get(position).getShipping();
                                        dialog.dismiss();
                                    }
                                }
                            });
                            //  Log.e("OutPut Size", String.valueOf(arealistresults.size()));

                            if (arealistresults.size() == 0) {
                                lyt_other_area.setVisibility(View.VISIBLE);
                                //   etUArea.setText("No Area Found");
                                strNewAreaId = "";
                                strNewAreaChipCharge = "";
                                //  lvOtherArealayout.setVisibility(View.VISIBLE);
                                //  dialog.dismiss();
                                rvSelectAreaPincode.setVisibility(View.GONE);
                            } else {
                                rvSelectAreaPincode.setVisibility(View.VISIBLE);
                                lyt_other_area.setVisibility(View.GONE);
                                //  lvOtherArealayout.setVisibility(View.GONE);
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
                            Utils.hideKeyboard(ChangeAddressActivity.this);
                            //  lvOtherArealayout.setVisibility(View.GONE);
                            etUArea.setText(arealistModels.get(position).getName());
                            strNewAreaId = arealistModels.get(position).getAreaID();
                            strNewAreaChipCharge = arealistModels.get(position).getShipping();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });


        etUPincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ChangeAddressActivity.this);
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
                RecyclerView.LayoutManager mLayoutManagermain = new LinearLayoutManager(ChangeAddressActivity.this);
                rvSelectAreaPincode.setLayoutManager(mLayoutManagermain);
                rvSelectAreaPincode.setHasFixedSize(true);

                pincodeSelectAdapter = new PincodeSelectAdapter(ChangeAddressActivity.this, pincodelistModels);
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
                            pincodeSelectAdapter = new PincodeSelectAdapter(ChangeAddressActivity.this, pincodelistresults);
                            rvSelectAreaPincode.setAdapter(pincodeSelectAdapter);

                            pincodeSelectAdapter.setOnItemClickListener(new PincodeSelectAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position, View view, int which) {

                                    if (which == 1) {
                                        Utils.hideKeyboard(ChangeAddressActivity.this);
                                        etUPincode.setText(pincodelistresults.get(position).getName());
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
                            Utils.hideKeyboard(ChangeAddressActivity.this);
                            etUPincode.setText(pincodelistModels.get(position).getName());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        tvChangetoCurrentaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(ChangeAddressActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChangeAddressActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_ID);
                } else {

                    LocationRequest mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(30 * 1000);
                    mLocationRequest.setFastestInterval(5 * 1000);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
                    builder.setAlwaysShow(true);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                        @Override
                        public void onResult(@NonNull LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    final ProgressDialog progressBar = new ProgressDialog(ChangeAddressActivity.this);
                                    progressBar.setCancelable(true);
                                    progressBar.setMessage("Getting Current Location...");
                                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progressBar.setIndeterminate(true);
                                    progressBar.show();

                                    new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            getUserLocation();

                                            progressBar.cancel();
                                        }
                                    }, 4000);

                                    //    getUserLocation();

                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().

                                        status.startResolutionForResult(ChangeAddressActivity.this, REQUEST_CHECK_SETTINGS);
                                    } catch (IntentSender.SendIntentException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    private class AreaPincodeAsync extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {

                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.AreaPincodeList("area_pincode");

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
                    Utils.ShowSnakBar(curators.message, relativeMain, ChangeAddressActivity.this);
                }
            }
        }
    }

    public void retryInternetForArea() {
        final Dialog dialog = new Dialog(ChangeAddressActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(ChangeAddressActivity.this, "", "Please wait...", false, false);
                    AreaPincodeAsync mGetProductDetailDataTask = new AreaPincodeAsync();
                    mGetProductDetailDataTask.execute();

                } else {
                    Utils.ShowSnakBar("No internet available", relativeMain, ChangeAddressActivity.this);

                }
            }
        });
        dialog.show();
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_change_address));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComp() {
        etUname = (EditText) findViewById(R.id.etUpdatename);
        etUemail = (EditText) findViewById(R.id.etUpdateemail);
        etUphone = (EditText) findViewById(R.id.etUpdatephone);
        etUAddress = (EditText) findViewById(R.id.etUpdateAddress);
        tvChangetoCurrentaddress = (TextView) findViewById(R.id.tvChangetoCurrentaddress);
        etUArea = (TextView) findViewById(R.id.etUpdateArea);
        etUPincode = (TextView) findViewById(R.id.etUpdatePincode);
        etUpdateCity = (EditText) findViewById(R.id.etUpdateCity);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
        btnChangeAddress = (Button) findViewById(R.id.btnChangeAddress);
    }

    private boolean validatefullname() {
        if (etUname.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Enter your name", relativeMain, ChangeAddressActivity.this);
            return false;
        }
        return true;
    }

    private boolean validateemail() {

        if (etUemail.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Enter Email", relativeMain, ChangeAddressActivity.this);

            return false;
        }
        return true;
    }

    private boolean validatePhone() {

        if (etUphone.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Enter mobile no.", relativeMain, ChangeAddressActivity.this);

            return false;
        }
        return true;
    }

    private boolean validateAdde() {

        if (etUAddress.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Enter Address", relativeMain, ChangeAddressActivity.this);

            return false;
        }
        return true;
    }

    private boolean validateArea() {
        if (etUArea.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Select area", relativeMain, ChangeAddressActivity.this);

            return false;
        }
        return true;
    }

    private boolean validateZipcode() {

        if (etUPincode.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Select pincode", relativeMain, ChangeAddressActivity.this);

            return false;
        }
        return true;
    }

    private boolean validateCity() {

        if (etUpdateCity.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Enter city", relativeMain, ChangeAddressActivity.this);

            return false;
        }
        return true;
    }

    private void getUserLocation() {
        String address = "";
        GPSService mGPSService = new GPSService(ChangeAddressActivity.this);
        mGPSService.getLocation();
        String addressaaa = "", city = "", state = "", country = "", postalCode = "", knownName = "";
        if (!mGPSService.isLocationAvailable) {

            // Here you can ask the user to try again, using return; for that
            Toast.makeText(ChangeAddressActivity.this, "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
            // getUserLocation();
            return;

            // Or you can continue without getting the location, remove the return; above and uncomment the line given below
            // address = "Location not available";
        } else {

            // Getting location co-ordinates
            double latitude = mGPSService.getLatitude();
            double longitude = mGPSService.getLongitude();
            //      Toast.makeText(CheckOutActivity.this, "Latitude:" + latitude + " | Longitude: " + longitude, Toast.LENGTH_LONG).show();

            address = mGPSService.getLocationAddress();
          /*  Log.e("latitude..", String.valueOf(latitude));
            Log.e("longitude..", String.valueOf(longitude));*/

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                addressaaa = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();

              /*  Log.e("addressaaa..",addressaaa);
                Log.e("city..",city);
                Log.e("state..",state);
                Log.e("country..",country);
                Log.e("postalCode..",postalCode);
                Log.e("knownName..",knownName);*/

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        //  Log.e("address..",address);

        //    Toast.makeText(CheckOutActivity.this, "Your address is: " + address, Toast.LENGTH_SHORT).show();
        //    addMainadd.setText(address);

        //   addMainadd.setText("");
        //     Log.e("Current Loc: ","");
        String arr[] = address.split(",");
        etUAddress.setText("");
        etUpdateCity.setText("");

        etUAddress.append(addressaaa + "");
        etUpdateCity.append(city + "");
        stateNEW = state;

       /* for (String anArr : arr) {
            Log.e("Current Loc 2: ",anArr.trim() + "\n");
            Log.e("Current Loc Dash: ",anArr.trim() + ",");
            etUAddress.append(anArr.trim()+",");

        }*/
        /*final Pattern p = Pattern.compile( "(\\d{6})" );
        final Matcher m = p.matcher( "ok12567ol987654" );
        if ( m.find() ) {
            Toast.makeText(CheckOutActivity.this, "Your address is: " + m.group(), Toast.LENGTH_SHORT).show();
        }*/

        // make sure you close the gps after using it. Save user's battery power
        mGPSService.closeGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(ChangeAddressActivity.this, "Location Permission Granted!", Toast.LENGTH_SHORT).show();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(ChangeAddressActivity.this, "Location Permission Denied!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (mGoogleApiClient.isConnected()) {
                            Toast.makeText(ChangeAddressActivity.this, "Location enabled!", Toast.LENGTH_LONG).show();
                            final ProgressDialog progressBar = new ProgressDialog(ChangeAddressActivity.this);
                            progressBar.setCancelable(true);
                            progressBar.setMessage("Getting Location...");
                            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressBar.setIndeterminate(true);
                            progressBar.show();

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    getUserLocation();

                                    progressBar.cancel();
                                }
                            }, 4000);

                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(ChangeAddressActivity.this, "Location not enabled..", Toast.LENGTH_LONG).show();
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //  mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStop() {
        super.onStop();
        //  mGoogleApiClient.disconnect();
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
        //  super.onBackPressed();
        Utils.hideKeyboard(ChangeAddressActivity.this);
        finish();
        Intent intent = new Intent(ChangeAddressActivity.this, AddressActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);


    }


}

