package com.shudh4sure.shopping.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.utils.AppConstant;
import com.shudh4sure.shopping.utils.Global;
import com.shudh4sure.shopping.utils.IApiMethods;
import com.shudh4sure.shopping.utils.RequestMethod;
import com.shudh4sure.shopping.utils.RestClient;
import com.shudh4sure.shopping.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;

/*
 * Created by welcome on 13-10-2016.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 7;

    ImageView imgLogo;

    EditText edtEmil, edtPasssword;

    TextView txtForgotPassword, txtSignIn, txtNotMember, txtSignUp, showhide, whatsappcontactTv;

    String StredtEmil, StredtPasssword;

    RelativeLayout relativeMain;

    ProgressDialog loading;

    Global global;

    SharedPreferences mprefs;

    boolean IsPermissionOK = false;

    LinearLayout lvFacebooklogin;

    String full_name = "";

    String strEmail = "";

    String strFbId = "";

    TextView fbText;

    GoogleApiClient mGoogleApiClient;

    ProgressDialog progressDialog;

    Context context;

    int RC_SIGN_IN = 2000;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    // LoginButton loginButton;
    private CallbackManager callbackManager;

    LinearLayout llGmailLogin;

    String GmailName, GmailEmail, GmailUId, resMessage = "", resCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signin);
        getWindow().setBackgroundDrawableResource(R.drawable.ic_loginbg);
        global = new Global(this);

        context = this;

        //LoginManager.getInstance().logOut();

        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            insertDummyContactWrapper();
        } else {
            IsPermissionOK = true;
        }

        FetChXmlId();

        txtForgotPassword.setOnClickListener(this);
        txtSignIn.setOnClickListener(this);
        txtNotMember.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
        showhide.setOnClickListener(this);
        lvFacebooklogin.setOnClickListener(this);
        whatsappcontactTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + whatsappcontactTv.getText().toString()));
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        llGmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.shudh4sure.shopping",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String s = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("", "onAuthStateChanged:signed_in:" + user.getUid());
                    if (user.getDisplayName() != null) {
                        GmailName = user.getDisplayName();
                        GmailEmail = user.getEmail();
                        GmailUId = user.getUid();
                        new LoginWithGmail().execute();
                        mGoogleApiClient.clearDefaultAccountAndReconnect();
                    }
                } else {
                    Log.d("", "onAuthStateChanged:signed_out");
                }
            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("GMAIL", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("1", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("2", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void FetChXmlId() {
        llGmailLogin = (LinearLayout) findViewById(R.id.llGmailLogin);
        whatsappcontactTv = (TextView) findViewById(R.id.whatsappcontactTv);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        edtEmil = (EditText) findViewById(R.id.edtEmil);
        edtPasssword = (EditText) findViewById(R.id.edtPasssword);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        txtSignIn = (TextView) findViewById(R.id.txtSignIn);
        txtNotMember = (TextView) findViewById(R.id.txtNotMember);
        txtSignUp = (TextView) findViewById(R.id.txtSignUp);
        showhide = (TextView) findViewById(R.id.showhide);
        lvFacebooklogin = (LinearLayout) findViewById(R.id.lvFacebooklogin);
        relativeMain = (RelativeLayout) findViewById(R.id.relativeMain);
        fbText = (TextView) findViewById(R.id.fbText);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtForgotPassword:
                Utils.hideKeyboard(LoginActivity.this);
                Intent idd = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(idd);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.txtSignIn:
                StredtEmil = edtEmil.getText().toString();
                StredtPasssword = edtPasssword.getText().toString();
                if (Utils.isNetworkAvailable(LoginActivity.this)) {
                    if (TextUtils.isEmpty(StredtEmil)) {
                        Utils.ShowSnakBar("Please enter username", relativeMain, LoginActivity.this);
                    } else if (TextUtils.isEmpty(StredtPasssword)) {
                        Utils.ShowSnakBar("Please enter password", relativeMain, LoginActivity.this);
                    } else {
                        if (global.isNetworkAvailable()) {
                            Utils.hideKeyboard(LoginActivity.this);
                            loading = ProgressDialog.show(LoginActivity.this, "", "Please wait...", false, false);
                            LogInAsynTask mLogInAsynTask = new LogInAsynTask();
                            mLogInAsynTask.execute();
                        } else {
                            retryInternet();
                        }
                    }
                }
                break;
            case R.id.showhide:
                if (showhide.getText().equals("Hide")) {
                    showhide.setText("Show");
                    edtPasssword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else if (showhide.getText().equals("Show")) {
                    showhide.setText("Hide");
                    edtPasssword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                break;
            case R.id.txtSignUp:
                Utils.hideKeyboard(LoginActivity.this);
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.lvFacebooklogin:
                LoginManager.getInstance().logOut();
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday"));
                callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if (global.isNetworkAvailable()) {
                            if (loginResult.getAccessToken() != null) {
                                fbText.setText(getResources().getString(R.string.signwithfb));
                                GetFbData();
                            }
                        } else {
                            retryInternet1();
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                        Toast.makeText(LoginActivity.this, "" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("facebook", "fbEx__" + exception.getMessage());
                    }
                });
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {

        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read Storage");
        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Storage");
        if (!addPermission(permissionsList, android.Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList, Manifest.permission.RECEIVE_SMS))
            permissionsNeeded.add("Receive SMS");
        if (!addPermission(permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add("Read SMS");
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();

                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                    IsPermissionOK = true;
                } else {
                    insertDummyContactWrapper();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void GetFbData() {
        fbText.setText(getResources().getString(R.string.signwithfb));
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                if (jsonObject != null) {
                    fbText.setText(getResources().getString(R.string.signwithfb));
                    new LongOperation(jsonObject).execute("");
                } else if (graphResponse.getError() != null) {
                    switch (graphResponse.getError().getCategory()) {
                        case LOGIN_RECOVERABLE:
                            break;
                        case TRANSIENT:
                            break;
                        case OTHER:
                            break;
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();

    }

    @SuppressLint("StaticFieldLeak")
    private class LoginWithGmail extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;
        String resUserID, resPhone, resName, resLastName, resEmail, resUserImage, resOtp, resType, mobile_type;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String strLogin = AppConstant.API_URL + "/index.php?view=login&page=google_login" +
                    "&user=" + GmailEmail +
                    "&auth=" + GmailUId;

            String strLoginTrim = strLogin.replaceAll(" ", "%20");
            Log.d("strLoginTrim", strLoginTrim);
            try {
                RestClient restClient = new RestClient(strLogin);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("Register", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("user_detail");
                            JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                            if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                resUserID = jsonObjectList.getString("userID");
                                resPhone = jsonObjectList.getString("phone");
                                resName = jsonObjectList.getString("name");
                                resLastName = jsonObjectList.getString("lastname");
                                resEmail = jsonObjectList.getString("email");
                                resUserImage = jsonObjectList.getString("userimage");
                                resOtp = jsonObjectList.getString("otp");
                                resType = jsonObjectList.getString("type");
                                mobile_type = jsonObjectList.getString("mobile_type");
                            }
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
            progressDialog.dismiss();
            if (resCode.equalsIgnoreCase("0")) {
                if (resType.equalsIgnoreCase("OTP_SCREEN")) {
                    Toast.makeText(LoginActivity.this, resMessage, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
                    intent.putExtra("reg_otp", resOtp);
                    intent.putExtra("reg_phone", resPhone);
                    intent.putExtra("reg_userid", resUserID);
                    intent.putExtra("mobile_type", mobile_type);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(LoginActivity.this, resMessage, Toast.LENGTH_SHORT).show();
                    global.setPrefBoolean("Verify", true);
                    mprefs.edit().putString(AppConstant.USER_ID, resUserID).apply();
                    mprefs.edit().putString(AppConstant.USER_NAME, resName).apply();
                    mprefs.edit().putString(AppConstant.USER_MOBILE, resPhone).apply();
                    mprefs.edit().putString(AppConstant.USER_IMAGE, resUserImage).apply();
                    mprefs.edit().putString(AppConstant.USER_EMAIl, resEmail).apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            } else if (resCode.equalsIgnoreCase("1")) {
                fbText.setText(getResources().getString(R.string.signwithfb));
                LoginManager.getInstance().logOut();
                Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
                it.putExtra("email", GmailEmail);
                startActivity(it);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                Utils.showToastShort(resMessage, LoginActivity.this);
            }
        }
    }

    private class LogInAsynTask extends AsyncTask<Void, Void, Api_Model> {
        RestAdapter restAdapter;

        @Override
        protected void onPreExecute() {
            restAdapter = new RestAdapter.Builder().setEndpoint(AppConstant.API_URL).build();
        }

        @Override
        protected Api_Model doInBackground(Void... params) {
            try {
                IApiMethods methods = restAdapter.create(IApiMethods.class);
                Api_Model curators = methods.GetLogin("login", "signin", StredtEmil, StredtPasssword);
                return curators;
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
                        for (Api_Model.user_detail dataset : curators.user_detail) {

                            if (dataset.type.equalsIgnoreCase("OTP_SCREEN")) {
                                edtPasssword.setText("");
                                //   Toast.makeText(LoginActivity.this, dataset.otp, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
                                intent.putExtra("reg_otp", dataset.otp);
                                intent.putExtra("reg_phone", dataset.phone);
                                intent.putExtra("reg_userid", dataset.userID);
                                intent.putExtra("mobile_type", dataset.mobile_type);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } else {
                                global.setPrefBoolean("Verify", true);
                                mprefs.edit().putString(AppConstant.USER_ID, dataset.userID).apply();
                                mprefs.edit().putString(AppConstant.USER_NAME, dataset.name).apply();
                                mprefs.edit().putString(AppConstant.USER_EMAIl, dataset.email).apply();
                                mprefs.edit().putString(AppConstant.USER_MOBILE, dataset.phone).apply();
                                mprefs.edit().putString(AppConstant.USER_IMAGE, dataset.userimage).apply();
                                Intent id = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(id);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }
                    } catch (Exception e) {
                    }

                } else {
                    Utils.ShowSnakBar(curators.message, relativeMain, LoginActivity.this);
                }
            }
        }
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        JSONObject objUser;
        JSONObject jsonObjectList;
        String resMessage = "";
        String resCode = "";
        String result = "";
        String resId = "";
        String resName = "";
        String resEmail = "";
        String resPhone = "";
        String resImage = "";
        String resOTP = "";
        String resType = "";
        String resMobile_type = "";
        ProgressDialog loading1;

        public LongOperation(JSONObject jsonObject) {
            objUser = jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                loading1 = new ProgressDialog(LoginActivity.this);
                loading1.show();
                loading1.setMessage("Please wait..");
                loading1.setCancelable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                full_name = objUser.getString("name");
                strFbId = objUser.getString("id");
                if (objUser.has("email")) {
                    if (objUser.getString("email") != null) {
                        strEmail = objUser.getString("email");
                        Log.e("Email", "StrEmail:-" + strEmail);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String strLoginURL = "http://www.viragtraders.com/sabzi18/sabzi/index.php?view=login&page=social&user=" + strEmail + "&auth=" + strFbId;
            Log.e("strLoginURL ", strLoginURL);
            try {
                RestClient restClient = new RestClient(strLoginURL);
                try {
                    restClient.Execute(RequestMethod.GET);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String VerifyUser = restClient.getResponse();
                if (VerifyUser != null && VerifyUser.length() != 0) {
                    jsonObjectList = new JSONObject(VerifyUser);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        Log.e("resMessage ", resMessage);
                        Log.e("resCode ", resCode);
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("user_detail");
                            {
                                JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                resId = jsonObjectList.getString("userID");
                                resPhone = jsonObjectList.getString("phone");
                                resName = jsonObjectList.getString("name");
                                resEmail = jsonObjectList.getString("email");
                                resImage = jsonObjectList.getString("userimage");
                                resOTP = jsonObjectList.getString("otp");
                                resType = jsonObjectList.getString("type");
                                resMobile_type = jsonObjectList.getString("mobile_type");
                            }
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
            super.onPostExecute(result);
            if (loading1.isShowing() && loading1 != null) {
                loading1.dismiss();
                if (resCode.equalsIgnoreCase("0")) {
                    if (resType.equalsIgnoreCase("OTP_SCREEN")) {
                        Toast.makeText(LoginActivity.this, resMessage, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
                        intent.putExtra("reg_otp", resOTP);
                        intent.putExtra("reg_phone", resPhone);
                        intent.putExtra("reg_userid", resId);
                        intent.putExtra("mobile_type", resMobile_type);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(LoginActivity.this, resMessage, Toast.LENGTH_SHORT).show();
                        fbText.setText(getResources().getString(R.string.signwithfb));
                        global.setPrefBoolean("Verify", true);
                        mprefs.edit().putString(AppConstant.USER_ID, resId).apply();
                        mprefs.edit().putString(AppConstant.USER_NAME, resName).apply();
                        mprefs.edit().putString(AppConstant.USER_MOBILE, resPhone).apply();
                        mprefs.edit().putString(AppConstant.USER_IMAGE, resImage).apply();
                        mprefs.edit().putString(AppConstant.USER_EMAIl, resEmail).apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                } else if (resCode.equalsIgnoreCase("1")) {
                    fbText.setText(getResources().getString(R.string.signwithfb));
                    LoginManager.getInstance().logOut();
                    Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
                    it.putExtra("email", strEmail);
                    startActivity(it);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    Utils.showToastShort(resMessage, LoginActivity.this);
                    // Toast.makeText(LoginActivity.this, resMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    loading = ProgressDialog.show(LoginActivity.this, "", "Please wait...", false, false);
                    LogInAsynTask mLogInAsynTask = new LogInAsynTask();
                    mLogInAsynTask.execute();

                } else {
                    Utils.ShowSnakBar("No internet availbale", relativeMain, LoginActivity.this);
                }
            }
        });
        dialog.show();
    }

    public void retryInternet1() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    fbText.setText(getResources().getString(R.string.signwithfb));
                    GetFbData();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        } else {
            if (data != null) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
