package com.getprofitam.android.activity;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getprofitam.android.R;
import com.getprofitam.android.dbhelper.GetprofitamDB;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.IApiMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit.RestAdapter;


public class AddressActivity extends AppCompatActivity {
    public static final int REQUEST_ID = 2;
    private static final int REQUEST_CHECK_SETTINGS = 151;
    SharedPreferences mprefs;
    GetprofitamDB getprofitamDB;
    Cursor cursorCart;
    String strUserId = "";
    String strName = "";
    String strMobile = "";
    String strUserImage = "";
    String strEmail = "";
    String strAddress = "";
    String strArea = "";
    String strCity = "";
    String strState = "";
    String strPincode = "";
    String strWallet = "";
    String strAreaId = "";
    ProgressDialog loading;
    Global global;
    Toolbar toolbar;
    Button changeBTn;
    TextView proceed, addMainadd, addMobile, tvAreadeliveryCharge;
    TextView tvDateSlots, tvTimeSlots, tvAddressOrderamount, tvTimeslotMessage;
    int TotalCount = 0;
    Dialog dialog;
    ArrayList<String> arrTimeSlots = new ArrayList<>();
    String Str_Date = "";
    String strCurrentDay = "";
    String strTimeslotDay = "";
    ListView rvSelectTimeslot;
    CustomListAdapter customListAdapter;
    Calendar myCalendar = Calendar.getInstance();
    String weekDay = "";
    String next_days = "";
    String strShipCharge = "";
    String activityType = "", newUsername = "", newEmail = "", newMobile = "", newAddress = "",
            newArea = "", newPincode = "", newCity = "", newState = "", newAreaId = "", newAreaShipCharge = "",
            strNextDays = "", cart_amount = "", cart_msg = "", date_format = "",
            strWhatsapp_number = "", strDOA = "", strDOB = "";

    double double_cart_amount = 0;
    ArrayList<String> availableTime = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressview);
        global = new Global(this);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        strMobile = mprefs.getString(AppConstant.USER_MOBILE, null);
        cart_amount = mprefs.getString(AppConstant.CART_AMOUNT, null);
        cart_msg = mprefs.getString(AppConstant.CART_MESSAGE, null);
        try {
            double_cart_amount = Double.parseDouble(cart_amount);
            strCurrentDay = global.getCurrentDay();
            String LocalDbUserSocityIdName = strUserId + "_getprofitam_local.db";
            String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
            getprofitamDB = new GetprofitamDB(AddressActivity.this, FinalLocalDBName);
            getprofitamDB.OpenDB();
            cursorCart = getprofitamDB.ShowTableCartList();

        } catch (Exception ex) {
            Log.e("address", "Activity" + ex.getMessage());
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            activityType = bundle.getString("activityType");
            newUsername = bundle.getString("newUsername");
            newEmail = bundle.getString("newEmail");
            newMobile = bundle.getString("newMobile");
            newAddress = bundle.getString("newAddress");
            newArea = bundle.getString("newArea");
            newPincode = bundle.getString("newPincode");
            newCity = bundle.getString("newCity");
            newState = bundle.getString("newState");
            newAreaId = bundle.getString("newAreaId");
            newAreaShipCharge = bundle.getString("newAreaShipCharge");
            strNextDays = bundle.getString("strNextDays");
            availableTime = bundle.getStringArrayList("availabaleTime");

        }

        initToolbar();
        initComponent();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat dfNew = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        date_format = dfNew.format(c.getTime());
        //  Log.e("formattedDate: ", formattedDate);
        //   Log.e("date_format: ", date_format);
        tvDateSlots.setText(formattedDate);
        Cursor MyCurserThis = getprofitamDB.ShowTableCartList();
        for (MyCurserThis.moveToFirst(); !MyCurserThis.isAfterLast(); MyCurserThis.moveToNext()) {
            TotalCount = TotalCount + (MyCurserThis.getInt(7) * Integer.parseInt(MyCurserThis.getString(5)));
        }
        tvAddressOrderamount.setText(getResources().getString(R.string.Rs) + " " + TotalCount + "");

        if (activityType.equalsIgnoreCase("newAddress")) {
            if (global.isNetworkAvailable()) {

                loading = ProgressDialog.show(AddressActivity.this, "", "Please wait...", false, false);
                GetUserInformationChange getUserInformationChange = new GetUserInformationChange();
                getUserInformationChange.execute();
            } else {
                retryInternetchange();
            }
        } else {
            if (global.isNetworkAvailable()) {
                loading = ProgressDialog.show(AddressActivity.this, "", "Please wait...", false, false);
                GetUserInformation getUserInformation = new GetUserInformation();
                getUserInformation.execute();
            } else {
                retryInternet();
            }

        }


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvTimeSlots.getText().equals("Select Timeslot")) {
                    if (!activityType.equalsIgnoreCase("newAddress")) {
                        if (!strAddress.equalsIgnoreCase("")) {
                            try {
                                Intent intent = new Intent(AddressActivity.this, PaymentPageActivity.class);
                                intent.putExtra("pay_user_name", strName);
                                intent.putExtra("pay_user_email", strEmail);
                                intent.putExtra("pay_user_phone", strMobile);
                                intent.putExtra("pay_user_address1", strAddress);
                                intent.putExtra("pay_user_area", strArea);
                                intent.putExtra("pay_user_city", strCity);
                                intent.putExtra("pay_user_state", strState);
                                intent.putExtra("pay_zipcode", strPincode);

                                intent.putExtra("pay_map_address", "");
                                intent.putExtra("pay_user_flat", "");
                                intent.putExtra("pay_google_location", "");
                                intent.putExtra("pay_dicount_id", "");
                                intent.putExtra("pay_discount_amount", "");
                                intent.putExtra("address_area_id", strAreaId);
                                intent.putExtra("address_area_shipcharge", strShipCharge);
                                intent.putExtra("pay_delivery_date", date_format);
                                intent.putExtra("pay_delivery_time", tvTimeSlots.getText().toString().trim());
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            } catch (Exception ex) {

                            }

                        } else {
                            Toast.makeText(AddressActivity.this, "Please add your address", Toast.LENGTH_SHORT).show();
                        }

                        /*Log.e("strName: ",strName);
                        Log.e("strEmail: ",strEmail);
                        Log.e("strMobile: ",strMobile);
                        Log.e("strEmail: ",strEmail);
                        Log.e("strAddress: ",strAddress);
                        Log.e("strArea: ",strArea);


                        Log.e("strPincode: ",strPincode);
                        Log.e("strAreaId: ",strAreaId);
                        Log.e("strShipCharge: ",strShipCharge);
                        Log.e("pay_delivery_date: ",tvDateSlots.getText().toString().trim());
                        Log.e("pay_delivery_time: ",tvTimeSlots.getText().toString().trim());*/

                    } else {
                        if (!newAddress.equalsIgnoreCase("")) {
                            try {
                                Intent intent = new Intent(AddressActivity.this, PaymentPageActivity.class);
                                intent.putExtra("pay_user_name", newUsername);
                                intent.putExtra("pay_user_email", newEmail);
                                intent.putExtra("pay_user_phone", newMobile);
                                intent.putExtra("pay_user_address1", newAddress);
                                intent.putExtra("pay_user_area", newArea);
                                intent.putExtra("pay_user_city", newCity);
                                intent.putExtra("pay_user_state", newState);
                                intent.putExtra("pay_zipcode", newPincode);
                                intent.putExtra("pay_map_address", "");
                                intent.putExtra("pay_user_flat", "");
                                intent.putExtra("pay_google_location", "");
                                intent.putExtra("pay_dicount_id", "");
                                intent.putExtra("pay_discount_amount", "");
                                intent.putExtra("address_area_id", newAreaId);
                                intent.putExtra("address_area_shipcharge", newAreaShipCharge);
                                intent.putExtra("pay_delivery_date", date_format);
                                intent.putExtra("pay_delivery_time", tvTimeSlots.getText().toString().trim());
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            } catch (Exception ex) {

                            }

                        } else {
                            Toast.makeText(AddressActivity.this, "Please add your address", Toast.LENGTH_SHORT).show();
                        }

                        /*
                        Log.e("strName: ",newUsername);
                        Log.e("strEmail: ",newEmail);
                        Log.e("strMobile: ",newMobile);
                        Log.e("strAddress: ",newAddress);
                        Log.e("strArea: ",newArea);


                        Log.e("strPincode: ",newPincode);
                        Log.e("strAreaId: ",newAreaId);
                        Log.e("strShipCharge: ",newAreaShipCharge);
                        Log.e("pay_delivery_date: ",tvDateSlots.getText().toString().trim());
                        Log.e("pay_delivery_time: ",tvTimeSlots.getText().toString().trim());*/
                    }
                } else {
                    Toast.makeText(AddressActivity.this, "Please select another date", Toast.LENGTH_SHORT).show();
                }
            }
        });

        changeBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (activityType.equalsIgnoreCase("newAddress")) {
                    Intent intent = new Intent(AddressActivity.this, ChangeAddressActivity.class);
                    intent.putExtra("resUserID", strUserId);
                    intent.putExtra("resName", newUsername);
                    intent.putExtra("resEmail", newEmail);
                    intent.putExtra("resMobile", newMobile);
                    intent.putExtra("resUserImage", strUserImage);
                    intent.putExtra("resAddress", newAddress);
                    intent.putExtra("resArea", newArea);
                    intent.putExtra("resAreaShipCharge", newAreaShipCharge);
                    intent.putExtra("resCityname", newCity);
                    intent.putExtra("resPincode", newPincode);
                    intent.putExtra("resWallet", strWallet);
                    intent.putExtra("resNextDays", strNextDays);
                    // intent.putStringArrayListExtra("availabaleTime", arrTimeSlots);
                    //  Log.e("strNextDays:", strNextDays);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else {
                    Intent intent = new Intent(AddressActivity.this, ChangeAddressActivity.class);
                    intent.putExtra("resUserID", strUserId);
                    intent.putExtra("resName", strName);
                    intent.putExtra("resEmail", strEmail);
                    intent.putExtra("resMobile", strMobile);
                    intent.putExtra("resUserImage", strUserImage);
                    intent.putExtra("resAddress", strAddress);
                    intent.putExtra("resArea", strArea);
                    intent.putExtra("resAreaShipCharge", strShipCharge);
                    intent.putExtra("resCityname", strCity);
                    intent.putExtra("resPincode", strPincode);
                    intent.putExtra("resWallet", strWallet);
                    intent.putExtra("resNextDays", next_days);
                    // intent.putStringArrayListExtra("availabaleTime", arrTimeSlots);
                    //   Log.e("next_days:", next_days);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
        tvDateSlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(tvDateSlots);
            }
        });

        tvTimeSlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(AddressActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                dialog.setContentView(R.layout.row_selecttimeslot);

                ImageView imgClose = (ImageView) dialog.findViewById(R.id.imgClose);
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        dialog.dismiss();
                    }
                });
                customListAdapter = new CustomListAdapter(AddressActivity.this, arrTimeSlots);
                rvSelectTimeslot = (ListView) dialog.findViewById(R.id.rvSelectTimeslot);
                rvSelectTimeslot.setAdapter(customListAdapter);
                dialog.show();
                rvSelectTimeslot.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // TODO Auto-generated method stub
                        String Slecteditem = arrTimeSlots.get(position);
                        tvTimeSlots.setText(Slecteditem);
                        dialog.dismiss();
                    }
                });
            }
        });
        tvAddressOrderamount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(AddressActivity.this, CartActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_deladdress));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        changeBTn = (Button) findViewById(R.id.tvTotalPayableamountDropDown);
        proceed = (TextView) findViewById(R.id.checkOut);
        addMainadd = (TextView) findViewById(R.id.addMainadd);
        addMobile = (TextView) findViewById(R.id.addMobile);
        tvAddressOrderamount = (TextView) findViewById(R.id.tvAddressOrderamount);
        tvDateSlots = (TextView) findViewById(R.id.tvDateSlots);
        tvTimeSlots = (TextView) findViewById(R.id.tvTimeSlots);
        tvTimeslotMessage = (TextView) findViewById(R.id.tvTimeslotMessage);
        tvAreadeliveryCharge = (TextView) findViewById(R.id.tvAreadeliveryCharge);
    }

    // when change address, following api will be called...
    private void setDate(final TextView edSelectDate2) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Calendar c = Calendar.getInstance();
                Calendar c1 = Calendar.getInstance();
                c.setTime(new Date());

                try {
                    if (activityType.equalsIgnoreCase("newAddress")) {
                        if (!strNextDays.equalsIgnoreCase("")) {
                            c.add(Calendar.DATE, Integer.parseInt(strNextDays));
                        }
                    } else {
                        if (!next_days.equalsIgnoreCase("")) {
                            c.add(Calendar.DATE, Integer.parseInt(next_days));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                c1.add(Calendar.DATE, -1);

                //      Log.d("Date", "c1" + c1.getTime().toString() + "mycalender" + myCalendar.getTime().toString());
                //     Log.d("Date", String.valueOf(c1.equals(myCalendar)));

                try {
                    if (myCalendar.after(c) || myCalendar.before(c1)) {
                        Toast.makeText(AddressActivity.this, "Sorry! No time slot avilable. Please select another date.", Toast.LENGTH_SHORT).show();
                    } else {
                        updateLabel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            private void updateLabel() {

                String myFormat = "dd MMMM yyyy"; // In which you need put
                date_format = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                SimpleDateFormat sdfNew = new SimpleDateFormat(date_format, Locale.US);
                Str_Date = sdf.format(myCalendar.getTime());
                date_format = sdfNew.format(myCalendar.getTime());
                //   Log.d("Str_Date", Str_Date);
                //    Log.e("date_format", date_format);
                edSelectDate2.setText(Str_Date);
                getDayOfWeek();

            }

        };

        DatePickerDialog d = new DatePickerDialog(AddressActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        d.setCancelable(false);
        d.show();
    }

    private void getDayOfWeek() {
        if (global.isNetworkAvailable()) {
            int dayOfWeek = myCalendar.get(Calendar.DAY_OF_WEEK);

            if (Calendar.MONDAY == dayOfWeek) {
                weekDay = "monday";
            } else if (Calendar.TUESDAY == dayOfWeek) {
                weekDay = "tuesday";
            } else if (Calendar.WEDNESDAY == dayOfWeek) {
                weekDay = "wednesday";
            } else if (Calendar.THURSDAY == dayOfWeek) {
                weekDay = "thursday";
            } else if (Calendar.FRIDAY == dayOfWeek) {
                weekDay = "friday";
            } else if (Calendar.SATURDAY == dayOfWeek) {
                weekDay = "saturday";
            } else if (Calendar.SUNDAY == dayOfWeek) {
                weekDay = "sunday";
            }
            System.out.println(weekDay);
            GetAvailabaleTimeSlot getAvailabaleTimeSlot = new GetAvailabaleTimeSlot();
            getAvailabaleTimeSlot.execute();
        } else {
            retryInternetDate();
        }
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(AddressActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(AddressActivity.this, "", "Please wait...", false, false);
                    GetUserInformation getUserInformation = new GetUserInformation();
                    getUserInformation.execute();
                } else {
                    Toast.makeText(AddressActivity.this, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    public void retryInternetchange() {
        final Dialog dialog = new Dialog(AddressActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(AddressActivity.this, "", "Please wait...", false, false);
                    GetUserInformationChange getUserInformationchanege = new GetUserInformationChange();
                    getUserInformationchanege.execute();
                } else {
                    Toast.makeText(AddressActivity.this, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    public void retryInternetDate() {
        final Dialog dialog = new Dialog(AddressActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(AddressActivity.this, "", "Please wait...", false, false);
                    GetAvailabaleTimeSlot getAvailabaleTimeSlot = new GetAvailabaleTimeSlot();
                    getAvailabaleTimeSlot.execute();
                } else {
                    Toast.makeText(AddressActivity.this, R.string.nonetwork, Toast.LENGTH_SHORT).show();
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

    // For getting all user details...
    private class GetUserInformation extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
            arrTimeSlots.clear();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.UserInfo("get_info", strUserId, strMobile, strCurrentDay.toLowerCase());

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
                    strTimeslotDay = curators.time_slot_msg;
                    next_days = curators.next_days;

                    for (final Api_Model.get_detail contact : curators.get_detail) {
                        strUserImage = contact.userimage;
                        strName = contact.name;
                        strMobile = contact.phone;
                        strEmail = contact.email;
                        strAreaId = contact.area_id;
                        strArea = contact.area;
                        strCity = contact.city;
                        strState = contact.state;
                        strAddress = contact.address;
                        strPincode = contact.pincode;
                        strWallet = contact.wallet;
                        strShipCharge = contact.ship_charge;
                        strWhatsapp_number = contact.whatsapp_no;
                        strDOA = contact.mrg_anniversary;
                        strDOB = contact.date_of_birth;
                        String customName = strName.substring(0, 1).toUpperCase() + strName.substring(1).toLowerCase();

                        addMainadd.setText(customName + ",\n" + strAddress + ",\n" + strArea + ",\n" + strCity + " - " + strPincode);
                        addMobile.setText(strMobile);

                        if (TotalCount < double_cart_amount) {
                            tvAreadeliveryCharge.setVisibility(View.VISIBLE);
                            tvAreadeliveryCharge.setText("Delivery charges" + " " + getResources().getString(R.string.Rs) + " "
                                    + strShipCharge + " " + "Applied for " + strArea + " area");
                        } else {
                            tvAreadeliveryCharge.setVisibility(View.GONE);
                        }

                    }
                    if (curators.data.equals("Yes")) {
                        tvTimeSlots.setVisibility(View.VISIBLE);

                        tvTimeslotMessage.setVisibility(View.GONE);

                        for (final Api_Model.timeslot timeslot : curators.timeslot) {
                            arrTimeSlots.add(timeslot.start_time);
                        }
                        tvTimeSlots.setText(arrTimeSlots.get(0));
                    } else {
                        tvTimeSlots.setVisibility(View.GONE);
                        tvTimeslotMessage.setVisibility(View.VISIBLE);
                        tvTimeslotMessage.setText(curators.time_slot_msg);
                    }
                }
            }
        }

    }

    private class GetUserInformationChange extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL)
                    .build();
            arrTimeSlots.clear();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.UserInfo("get_info", strUserId, newMobile, strCurrentDay.toLowerCase());

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
                    strTimeslotDay = curators.time_slot_msg;
                    next_days = curators.next_days;

                    for (final Api_Model.get_detail contact : curators.get_detail) {
                      /*  strUserImage = contact.userimage;
                        strName = contact.name;
                        strMobile = contact.phone;
                        strEmail = contact.email;
                        strAreaId = contact.area_id;
                        strArea = contact.area;
                        strCity = contact.city;
                        strAddress = contact.address;
                        strPincode = contact.pincode;
                        strWallet = contact.wallet;
                        strShipCharge = contact.ship_charge;*/

                        String customName = newUsername.substring(0, 1).toUpperCase() + newUsername.substring(1).toLowerCase();

                        addMainadd.setText(customName + ",\n" + newAddress + ",\n" + newArea + ",\n" + newCity + "," + newState + " - " + newPincode);
                        addMobile.setText(newMobile);

                        if (TotalCount < double_cart_amount) {
                            tvAreadeliveryCharge.setVisibility(View.VISIBLE);
                            tvAreadeliveryCharge.setText("Delivery charges" + " " + getResources().getString(R.string.Rs) + " "
                                    + newAreaShipCharge + " " + "Applied for " + newArea + " area");
                        } else {
                            tvAreadeliveryCharge.setVisibility(View.GONE);
                        }

                    }
                    if (curators.data.equals("Yes")) {
                        tvTimeSlots.setVisibility(View.VISIBLE);

                        tvTimeslotMessage.setVisibility(View.GONE);

                        for (final Api_Model.timeslot timeslot : curators.timeslot) {
                            arrTimeSlots.add(timeslot.start_time);
                        }
                        tvTimeSlots.setText(arrTimeSlots.get(0));
                    } else {

                        tvTimeSlots.setVisibility(View.GONE);
                        tvTimeslotMessage.setVisibility(View.VISIBLE);
                        tvTimeslotMessage.setText(curators.time_slot_msg);
                    }
                }
            }
        }

    }

    private class GetAvailabaleTimeSlot extends AsyncTask<Void, Void, Api_Model> {

        RestAdapter restAdapter;
        ProgressDialog loaderTIme;

        @Override
        protected void onPreExecute() {
            loaderTIme = ProgressDialog.show(AddressActivity.this, "", "Please wait...", false, false);
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
            arrTimeSlots.clear();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.getAvailableTimeSlot("get_info", strUserId, strMobile, weekDay, "newday");

                return curators;
            } catch (Exception E) {

                return null;
            }
        }

        @Override
        protected void onPostExecute(Api_Model curators) {
            loaderTIme.dismiss();

            if (curators == null) {

            } else {
                if (curators.msgcode.equals("0")) {

                    next_days = curators.next_days;
                    if (curators.data.equals("Yes")) {
                        tvTimeSlots.setVisibility(View.VISIBLE);
                        tvTimeslotMessage.setVisibility(View.GONE);

                        for (final Api_Model.timeslot timeslot : curators.timeslot) {
                            arrTimeSlots.add(timeslot.start_time);
                        }
                        tvTimeSlots.setText(arrTimeSlots.get(0));


                    } else {
                        tvTimeSlots.setVisibility(View.GONE);
                        tvTimeslotMessage.setVisibility(View.VISIBLE);
                        tvTimeslotMessage.setText(curators.time_slot_msg);
                    }

                }
            }
        }
    }

    public class CustomListAdapter extends ArrayAdapter<String> {

        private final Activity context;
        ArrayList<String> listItem;

        public CustomListAdapter(Activity context, ArrayList<String> listItem) {
            super(context, R.layout.row_arepincode, listItem);
            // TODO Auto-generated constructor stub

            this.context = context;
            this.listItem = listItem;

        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.row_timeslots, null, true);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.productSearchtitle);
            txtTitle.setText(listItem.get(position));
            return rowView;
        }

        ;
    }
}
