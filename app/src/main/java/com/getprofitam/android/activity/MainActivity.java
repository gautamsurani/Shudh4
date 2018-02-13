package com.getprofitam.android.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.getprofitam.android.R;
import com.getprofitam.android.dbhelper.GetprofitamDB;
import com.getprofitam.android.fragment.Dashboard;
import com.getprofitam.android.utils.AppConstant;
import com.getprofitam.android.utils.Global;
import com.getprofitam.android.utils.RestClient;
import com.getprofitam.android.utils.Utils;

import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RestAdapter;

import static com.getprofitam.android.utils.AppConstant.API_URL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Fragment Dashboard;
    LinearLayout mainCAT, lvOrderhistroy, lvreferearn, lenearHeaderHome, lvmywallet, lvhelp, lvrateus, lvlogout, lvAboutus, lvContactUs, lvOffers, lvSabzilanafeed, lvLastChoice, lvFeedback, lvmywishlist;
    RelativeLayout linearUserDetail;
    TextView txtUserNameHeader, txtUserMobileHeader, tvmytotalitems;
    Toolbar toolbar;
    NavigationView navigationView;
    Global global;
    RestAdapter adapter;
    SharedPreferences mprefs;
    String strUserId = "", strName = "", strMobile = "", strUserImage = "", strEmail = "";
    GetprofitamDB getprofitamDB;
    Cursor cursorCart;
    CircleImageView myroundimage;
    private long exitTime = 0;
    Dialog packsizeDialog;
    String FinalLocalDBName = "";
    RestClient restClient;
    ProgressDialog loading;
    RelativeLayout relativeMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        global = new Global(this);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, "");
        strName = mprefs.getString(AppConstant.USER_NAME, "");
        strMobile = mprefs.getString(AppConstant.USER_MOBILE, "");
        strEmail = mprefs.getString(AppConstant.USER_EMAIl, "");
        strUserImage = mprefs.getString(AppConstant.USER_IMAGE, "");

        try {
            String LocalDbUserSocityIdName = strUserId + "_getprofitam_local.db";
            FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 147);
            } else {
                getprofitamDB = new GetprofitamDB(MainActivity.this, FinalLocalDBName);
                getprofitamDB.OpenDB();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initToolbar();
        init();
        listener();
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.post(new Runnable() {
            @Override
            public void run() {
                toggle.syncState();
            }
        });
        linearUserDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, MyAccountActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mainCAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                //   Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                Intent intent = new Intent(MainActivity.this, NewCategoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        lvLastChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, UserLastAchoiceActivity.class);
                //     it.putExtra("catID",listDataHeader.get(groupPosition).getCatID());
                //     it.putExtra("catName",listDataHeader.get(groupPosition).getName());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        lvOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, OffersActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        lvmywallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, WalletHistory.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        lvmywishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        lvOrderhistroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);

                Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        lvreferearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, ReferActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        lvContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, ContactusActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        lvhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        lvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        lvrateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    drawer.closeDrawer(Gravity.LEFT);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                    startActivity(intent);
                } catch (Exception ex) {
                    Log.e("Main Activity", "Main Activity" + ex.getMessage());
                }

            }
        });

        lvAboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        lvSabzilanafeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, GetprofitamFeedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        lvlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to logout ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        try {
                            LoginManager.getInstance().logOut();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        global.setPrefBoolean("Verify", false);
                        SharedPreferences preferences = getSharedPreferences(AppConstant.PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        Dashboard = new Dashboard();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_main, Dashboard, "DASH");
        fragmentTransaction.commit();

    }

    private void listener() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbartitle = (TextView) toolbar.findViewById(R.id.toolbartitle);
        ImageView imgCartProduct = (ImageView) toolbar.findViewById(R.id.imgCartProduct);
        tvmytotalitems = (TextView) toolbar.findViewById(R.id.tvmytotalitems);
        tvmytotalitems.setText("1");
        toolbartitle.setText(getResources().getString(R.string.app_name));

        ImageView imgSearchProduct = (ImageView) toolbar.findViewById(R.id.imgSearchProduct);
        imgSearchProduct.setVisibility(View.GONE);
       /* imgSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(it);
            }
        });*/

        imgCartProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, CartActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
       /* toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));*/
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        txtUserNameHeader = (TextView) headerView.findViewById(R.id.txtUserNameHeader);
        txtUserMobileHeader = (TextView) headerView.findViewById(R.id.txtUserMobileHeader);
        myroundimage = (CircleImageView) headerView.findViewById(R.id.myroundimage);
        if (!strName.equalsIgnoreCase("")) {
            String customName = strName.substring(0, 1).toUpperCase() + strName.substring(1).toLowerCase();
            txtUserNameHeader.setText(customName);
        } else {
            txtUserNameHeader.setText("");
        }
        txtUserMobileHeader.setText(strMobile);
        Glide.with(MainActivity.this)
                .load(strUserImage)
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.pic)
                .into(myroundimage);

        mainCAT = (LinearLayout) headerView.findViewById(R.id.mainCAT);
        lvmywallet = (LinearLayout) headerView.findViewById(R.id.lvmywallet);
        lvOrderhistroy = (LinearLayout) headerView.findViewById(R.id.lvOrderhistroy);
        lvmywishlist = (LinearLayout) headerView.findViewById(R.id.lvmywishlist);
        lvLastChoice = (LinearLayout) headerView.findViewById(R.id.lvLastChoice);
        lvOffers = (LinearLayout) headerView.findViewById(R.id.lvOffers);
        lvFeedback = (LinearLayout) headerView.findViewById(R.id.lvFeedback);
        lvreferearn = (LinearLayout) headerView.findViewById(R.id.lvreferearn);
        lvhelp = (LinearLayout) headerView.findViewById(R.id.lvhelp);
        lvAboutus = (LinearLayout) headerView.findViewById(R.id.lvAboutus);
        linearUserDetail = (RelativeLayout) headerView.findViewById(R.id.linearUserDetail);
        lvSabzilanafeed = (LinearLayout) headerView.findViewById(R.id.lvSabzilanafeed);
        lvrateus = (LinearLayout) headerView.findViewById(R.id.lvrateus);
        lvContactUs = (LinearLayout) headerView.findViewById(R.id.lvContactUs);
        lvlogout = (LinearLayout) headerView.findViewById(R.id.lvlogout);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startActivity(new Intent(MainActivity.this, MainActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 149);
            } else {
                cursorCart = getprofitamDB.ShowTableCartList();
                tvmytotalitems.setText("" + cursorCart.getCount() + "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();

        } else {
            finish();
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        doExitApp();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 147: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        try {
                            getprofitamDB = new GetprofitamDB(MainActivity.this, FinalLocalDBName);
                            getprofitamDB.OpenDB();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
                    }
                }
                break;
            }
            case 149: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        try {

                            cursorCart = getprofitamDB.ShowTableCartList();
                            tvmytotalitems.setText("" + cursorCart.getCount() + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
                    }
                }
                break;
            }
        }
    }


}
