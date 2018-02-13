package com.shudh4sure.shopping.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.dbhelper.Shudh4sureDB;
import com.shudh4sure.shopping.utils.AppConstant;

public class FailOrderActivity extends AppCompatActivity {


    TextView txtohh, txtBack, tvExpression;
    ImageView imgTopImage;
    String statusMsg = "";
    Shudh4sureDB shudh4sureDB;

    String strUserId = "";
    SharedPreferences mprefs;
    String msg1="",msg2="",ordertype="";
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_succsess_failed_screen);
        mprefs = getSharedPreferences(AppConstant.PREFS_NAME, MODE_PRIVATE);
        strUserId = mprefs.getString(AppConstant.USER_ID, null);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            msg1 = bundle.getString("msg1");
            msg2 = bundle.getString("msg2");
            ordertype= bundle.getString("ordertype");
        }
        String LocalDbUserSocityIdName = strUserId + "_Shudh4sure_local.db";
        String FinalLocalDBName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + LocalDbUserSocityIdName;
        shudh4sureDB = new Shudh4sureDB(FailOrderActivity.this, FinalLocalDBName);
        shudh4sureDB.OpenDB();
        shudh4sureDB.deleteAll();

        txtohh = (TextView) findViewById(R.id.txtohh);
        txtBack = (TextView) findViewById(R.id.txtBack);
        imgTopImage = (ImageView) findViewById(R.id.imgTopImage);
        tvExpression = (TextView) findViewById(R.id.tvExpression);

        imgTopImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_failorder));

        if(ordertype.equalsIgnoreCase("PAYTM")){
            tvExpression.setText(msg1);
            txtohh.setText(msg2);
        }else if(ordertype.equalsIgnoreCase("PAYU")) {
             tvExpression.setText(msg1);
             txtohh.setText(msg2);
        }else {
            tvExpression.setText(getResources().getString(R.string.failexp));
            txtohh.setText(getResources().getString(R.string.failordermsg));
        }

        txtBack.setText(getResources().getString(R.string.successorderback));
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FailOrderActivity.this, OrderHistoryActivity.class);
        intent.putExtra("IntentType", "OrderSuccess");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
