package com.shudh4sure.shopping.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.adapter.AreaSelectAdapter;
import com.shudh4sure.shopping.adapter.PincodeSelectAdapter;
import com.shudh4sure.shopping.model.ArealistModel;
import com.shudh4sure.shopping.model.PincodelistModel;
import com.shudh4sure.shopping.utils.AndroidMultiPartEntity;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.Tools;
import com.shudh4sure.shopping.utils.Utils;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RestAdapter;


public class UpdateProfileActivity extends AppCompatActivity {

    static final int REQUEST_ID = 2;
    ProgressDialog loading;
    Context context;
    Global global;
    RelativeLayout relativeMain;
    File fileImage;
    SharedPreferences mprefs;
    String strUserId = "";
    String strName = "";
    String strMobile = "";
    String strUserImage = "";
    String strEmail = "";
    String strAddress = "";
    String strArea = "";
    String strCity = "";
    String strPincode = "";
    String strAreaId = "";
    String strWhatsapp_number = "";
    String strDOA = "";
    String strDOB = "";
    CircleImageView imgUpdateimage;
    ImageView imgChangeImage;
    EditText etUname, etUemail, etUphone, etUAddress, etUWhatsappnumber;
    TextView etUArea, etUPincode, tvdob, tvdoa;
    EditText etUCity;

    Button btnUpdateData;
    boolean checkFile;
    Dialog dialog;
    String Str_Date = "";
    ArrayList<ArealistModel> arealistModels = new ArrayList<>();
    ArrayList<PincodelistModel> pincodelistModels = new ArrayList<>();
    ArrayList<ArealistModel> arealistresults = new ArrayList<>();
    ArrayList<PincodelistModel> pincodelistresults = new ArrayList<>();
    RecyclerView rvSelectAreaPincode;
    AreaSelectAdapter adapterArea;
    PincodeSelectAdapter pincodeSelectAdapter;
    boolean IsPermissionOK = false;
    String mCurrentPhotoPath = "";
    File imageCamera;

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_updateprofile);
        context = this;
        global = new Global(context);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);
        strUserImage = mprefs.getString(AppConstant.USER_IMAGE, null);

        // dont remove this two line

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            strName = bundle.getString("resName");
            strMobile = bundle.getString("resMobile");
            strEmail = bundle.getString("resEmail");
            strUserImage = bundle.getString("resUserImage");
            strAddress = bundle.getString("resAddress");
            strArea = bundle.getString("resArea");
            strAreaId = bundle.getString("resAreaId");
            strCity = bundle.getString("resCityname");
            strPincode = bundle.getString("resPincode");
            strWhatsapp_number = bundle.getString("resWhatspp");
            strDOA = bundle.getString("resAnniversary");
            strDOB = bundle.getString("resBirthdate");
        }
        //   Log.e("strAreaId ", strAreaId);
        initToolbar();
        inotComp();
        Tools.systemBarLolipop(this);
        fileImage = new File(android.os.Environment.getExternalStorageDirectory(), "Shudh4sure.png");
        etUname.setText(strName);
        etUemail.setText(strEmail);
        etUphone.setText(strMobile);
        etUAddress.setText(strAddress);
        etUArea.setText(strArea);
        etUCity.setText(strCity);
        etUPincode.setText(strPincode);
        etUWhatsappnumber.setText(strWhatsapp_number);
        tvdoa.setText(strDOA);
        tvdob.setText(strDOB);
        try {

            Picasso.with(UpdateProfileActivity.this)
                    .load(strUserImage.trim())
                    .noFade()
                    .placeholder(R.drawable.pic)
                    .into(imgUpdateimage);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (global.isNetworkAvailable()) {
            loading = ProgressDialog.show(UpdateProfileActivity.this, "", "Please wait...", false, false);
            AreaPincodeAsync mGetProductDetailDataTask = new AreaPincodeAsync();
            mGetProductDetailDataTask.execute();
        } else {
            retryInternetForArea();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            checkpermission();

        } else {
            IsPermissionOK = true;
        }


        tvdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(UpdateProfileActivity.this);
                setDate(tvdob);
            }
        });

        tvdoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(UpdateProfileActivity.this);
                setDate(tvdoa);
            }
        });
        etUArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(UpdateProfileActivity.this);
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

                RecyclerView.LayoutManager mLayoutManagermain = new LinearLayoutManager(UpdateProfileActivity.this);
                rvSelectAreaPincode.setLayoutManager(mLayoutManagermain);
                rvSelectAreaPincode.setHasFixedSize(true);

                adapterArea = new AreaSelectAdapter(UpdateProfileActivity.this, arealistModels);
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
                            adapterArea = new AreaSelectAdapter(UpdateProfileActivity.this, arealistresults);
                            rvSelectAreaPincode.setAdapter(adapterArea);

                            adapterArea.setOnItemClickListener(new AreaSelectAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position, View view, int which) {

                                    if (which == 1) {
                                        Utils.hideKeyboard(UpdateProfileActivity.this);
                                        //    lvOtherArealayout.setVisibility(View.GONE);
                                        etUArea.setText(arealistresults.get(position).getName());
                                        strAreaId = arealistresults.get(position).getAreaID();
                                        AreaPincodeAsync mGetProductDetailDataTask = new AreaPincodeAsync();
                                        mGetProductDetailDataTask.execute();
                                        dialog.dismiss();
                                    }
                                }
                            });
                            //           Log.e("OutPut Size", String.valueOf(arealistresults.size()));

                            if (arealistresults.size() == 0) {

                                lyt_other_area.setVisibility(View.VISIBLE);
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
                            Utils.hideKeyboard(UpdateProfileActivity.this);
                            //  lvOtherArealayout.setVisibility(View.GONE);
                            etUArea.setText(arealistModels.get(position).getName());
                            strAreaId = arealistModels.get(position).getAreaID();
                            AreaPincodeAsync mGetProductDetailDataTask = new AreaPincodeAsync();
                            mGetProductDetailDataTask.execute();
                            //Log.e("strAreaId ", strAreaId);
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
                dialog = new Dialog(UpdateProfileActivity.this);
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
                RecyclerView.LayoutManager mLayoutManagermain = new LinearLayoutManager(UpdateProfileActivity.this);
                rvSelectAreaPincode.setLayoutManager(mLayoutManagermain);
                rvSelectAreaPincode.setHasFixedSize(true);

                pincodeSelectAdapter = new PincodeSelectAdapter(UpdateProfileActivity.this, pincodelistModels);
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
                            pincodeSelectAdapter = new PincodeSelectAdapter(UpdateProfileActivity.this, pincodelistresults);
                            rvSelectAreaPincode.setAdapter(pincodeSelectAdapter);

                            pincodeSelectAdapter.setOnItemClickListener(new PincodeSelectAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position, View view, int which) {

                                    if (which == 1) {
                                        Utils.hideKeyboard(UpdateProfileActivity.this);
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
                            Utils.hideKeyboard(UpdateProfileActivity.this);
                            etUPincode.setText(pincodelistModels.get(position).getName());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        btnUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    Utils.hideKeyboard(UpdateProfileActivity.this);
                    if (global.isNetworkAvailable()) {
                        new updateProfileAsync().execute();
                    } else {
                        retryInternet();
                    }
                } else {
                    Utils.ShowSnakBar("No internet available", relativeMain, UpdateProfileActivity.this);

                }
            }
        });

        imgChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText(getResources().getString(R.string.toolbar_updateprofile));
        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        RelativeLayout relMyCart = (RelativeLayout) toolbar.findViewById(R.id.relMyCart);
        imgSearchProduct.setVisibility(View.GONE);
        relMyCart.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void inotComp() {
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
        imgUpdateimage = (CircleImageView) findViewById(R.id.imgUpdateimage);
        imgChangeImage = (ImageView) findViewById(R.id.imgChangeImage);
        etUname = (EditText) findViewById(R.id.etUname);
        etUemail = (EditText) findViewById(R.id.etUemail);
        etUphone = (EditText) findViewById(R.id.etUphone);
        etUWhatsappnumber = (EditText) findViewById(R.id.etUWhatsappnumber);
        etUCity = (EditText) findViewById(R.id.etUCity);
        tvdob = (TextView) findViewById(R.id.tvdob);
        tvdoa = (TextView) findViewById(R.id.tvdoa);
        etUAddress = (EditText) findViewById(R.id.etUAddress);
        etUArea = (TextView) findViewById(R.id.etUArea);
        etUPincode = (TextView) findViewById(R.id.etUPincode);
        btnUpdateData = (Button) findViewById(R.id.btnUpdateData);
    }

    private void setDate(final TextView edSelectDate2) {
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Calendar c = Calendar.getInstance();

                c.setTime(new Date());
                c.add(Calendar.DATE, 0);

               /* Log.d("Date", "mycalender" + myCalendar.getTime().toString());
                Log.d("Date", String.valueOf(c.equals(myCalendar)));*/
                if (myCalendar.after(c)) {
                    Toast.makeText(context, "Please select another date", Toast.LENGTH_SHORT).show();
                } else {
                    updateLabel();
                }
            }

            private void updateLabel() {

                String myFormat = "dd-MM-yyyy"; // In which you need put
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Str_Date = sdf.format(myCalendar.getTime());
                // Log.d("Str_Date", Str_Date);
                edSelectDate2.setText(Str_Date);
            }

        };
        DatePickerDialog d = new DatePickerDialog(UpdateProfileActivity.this, date, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        d.setCancelable(false);

        d.show();
    }


    @TargetApi(Build.VERSION_CODES.N)
    public void checkpermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_ID);
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_ID);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ID);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //  Log.d("Permissions", "Permission Granted: " + permissions[i]);
                        IsPermissionOK = true;
                    } else {
                        checkpermission();
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

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));
                    startActivityForResult(intent, 1);
                } else if (items[item].equals("Choose from Library")) {

                    /*Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    photoPickerIntent.setType("image*//*");
                    startActivityForResult(photoPickerIntent, 2);*/

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    Bitmap bitmap;

                    bitmap = Global.getSampleBitmapFromFile(fileImage.getAbsolutePath(), 500, 500);

                    OutputStream outFile = null;
                    try {
                        //Log.e("rrrrrtf1111", "bitmap");
                        outFile = new FileOutputStream(fileImage);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imgUpdateimage.setImageBitmap(bitmap);
                    checkFile = true;
                    if (global.isNetworkAvailable()) {
                        if (fileImage != null && fileImage.exists()) {
                            //    new UploadImage().execute();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail = null;
                    BitmapFactory.Options options;

                    try {
                        Log.e("rrrrrtf222", "bitmap");
                        thumbnail = (BitmapFactory.decodeFile(picturePath));
                    } catch (OutOfMemoryError e) {
                        try {
                            options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            thumbnail = BitmapFactory.decodeFile(picturePath, options);
                        } catch (Exception yy) {
                            //   Log.e("", yy.toString());
                        }
                    }
                    try {
                        Log.e("rrrrrtf223333", "bitmap");
                        thumbnail = Global.getSampleBitmapFromFile(picturePath, 300, 300);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    imgUpdateimage.setImageBitmap(thumbnail);
                    checkFile = true;
                    fileImage = new File(picturePath);
                    ExifInterface exif = null;
                    try {
                        File pictureFile = new File(picturePath);
                        exif = new ExifInterface(pictureFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int orientation = ExifInterface.ORIENTATION_NORMAL;
                    if (exif != null)
                        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            thumbnail = rotateBitmap(thumbnail, 90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            thumbnail = rotateBitmap(thumbnail, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            thumbnail = rotateBitmap(thumbnail, 270);
                            break;
                    }
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(UpdateProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    new updateProfileAsync().execute();

                } else {
                    Utils.ShowSnakBar("No internet available", relativeMain, UpdateProfileActivity.this);

                }
            }
        });
        dialog.show();
    }

    public void retryInternetForArea() {
        final Dialog dialog = new Dialog(UpdateProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(UpdateProfileActivity.this, "", "Please wait...", false, false);
                    AreaPincodeAsync mGetProductDetailDataTask = new AreaPincodeAsync();
                    mGetProductDetailDataTask.execute();

                } else {
                    Utils.ShowSnakBar("No internet available", relativeMain, UpdateProfileActivity.this);

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
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    @SuppressLint("StaticFieldLeak")
    private class AreaPincodeAsync extends AsyncTask<Void, Void, Api_Model> {
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
                return methods.AreaPincodeList("area_pincode", strAreaId);
            } catch (Exception E) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(Api_Model curators) {
            loading.dismiss();
            if (curators != null) {
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
                        e.printStackTrace();
                    }
                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, UpdateProfileActivity.this);

                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class updateProfileAsync extends AsyncTask<String, Void, String> {
        String resMessage = "";
        String resCode = "";
        String strUserID = "";
        String strName = "";
        String strEmail = "";
        String strMobile = "";
        String strAddress = "";
        String strArea = "";
        String strCityname = "";
        String strUserImage = "";
        String strZipcode = "";
        String strDOB = "";
        String strWhatsappNumber = "";
        String strmarriagedate = "";
        File sourceFile;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(UpdateProfileActivity.this);
            loading.show();
            loading.setMessage("Please wait..");
            loading.setCancelable(false);
            strName = etUname.getText().toString();
            strEmail = etUemail.getText().toString();
            strMobile = etUphone.getText().toString();
            strAddress = etUAddress.getText().toString();
            strAddress = etUAddress.getText().toString();
            strArea = etUArea.getText().toString();
            strZipcode = etUPincode.getText().toString();
            strCityname = etUCity.getText().toString();
            strDOB = tvdob.getText().toString();
            strWhatsappNumber = etUWhatsappnumber.getText().toString();
            strmarriagedate = tvdoa.getText().toString();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String strUpdateProfile = "http://www.viragtraders.com/sabzi18/sabzi/index.php?view=change_info";

                String restUrl = strUpdateProfile.replaceAll(" ", "%20");
                String responseString;
                Log.d("strUpdateProfile", restUrl);

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(strUpdateProfile);

                try {
                    AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                            new AndroidMultiPartEntity.ProgressListener() {

                                @Override
                                public void transferred(long num) {

                                }
                            });
                    String pathimage = fileImage.getAbsolutePath();
                    entity.addPart("userID", new StringBody(strUserId));
                    entity.addPart("userPhone", new StringBody(strMobile));
                    entity.addPart("name", new StringBody(strName));
                    entity.addPart("page", new StringBody("update_info"));
                    entity.addPart("city", new StringBody(strCityname));
                    entity.addPart("address", new StringBody(strAddress));
                    entity.addPart("area", new StringBody(strArea));
                    entity.addPart("area_id", new StringBody(strAreaId));
                    entity.addPart("pincode", new StringBody(strZipcode));
                    entity.addPart("email", new StringBody(strEmail));
                    entity.addPart("date_of_birth", new StringBody(strDOB));
                    entity.addPart("mrg_anniversary", new StringBody(strmarriagedate));
                    entity.addPart("whatsapp_no", new StringBody(strWhatsappNumber));

                    if (checkFile) {
                        sourceFile = new File(pathimage);
                        entity.addPart("file", new FileBody(sourceFile));
                    }
                    httppost.setEntity(entity);

                    // Making server call
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        responseString = EntityUtils.toString(r_entity);
                        JSONObject jsonObject = new JSONObject(responseString);
                        resMessage = jsonObject.getString("message");
                        resCode = jsonObject.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("update_detail");
                            {

                                JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                    strUserID = jsonObjectList.getString("userID");
                                    strName = jsonObjectList.getString("name");
                                    strMobile = jsonObjectList.getString("phone");
                                    strUserImage = jsonObjectList.getString("userimage");
                                    strEmail = jsonObjectList.getString("email");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (loading.isShowing() && loading != null) {
                loading.dismiss();
                if (resCode.equalsIgnoreCase("0")) {
                    Toast.makeText(UpdateProfileActivity.this, resMessage, Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else if (resCode.equalsIgnoreCase("1")) {
                    Utils.ShowSnakBar(resMessage, relativeMain, UpdateProfileActivity.this);
                }
            }

        }
    }
}
