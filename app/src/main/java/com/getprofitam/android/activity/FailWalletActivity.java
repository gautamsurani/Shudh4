package com.getprofitam.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.getprofitam.android.R;

public class FailWalletActivity extends AppCompatActivity {


    TextView txtohh, txtBack, tvExpression;
    ImageView imgTopImage;
    String statusMsg = "";


    String strUserId = "";
    SharedPreferences mprefs;
    String msg1="",msg2="",ordertype="";
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_succsess_failed_screen);
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            msg1 = bundle.getString("msg1");
            msg2 = bundle.getString("msg2");
            ordertype= bundle.getString("wallettype");
        }

        txtohh = (TextView) findViewById(R.id.txtohh);
        txtBack = (TextView) findViewById(R.id.txtBack);
        imgTopImage = (ImageView) findViewById(R.id.imgTopImage);
        tvExpression = (TextView) findViewById(R.id.tvExpression);

        if(ordertype.equalsIgnoreCase("PAYTM")){
            tvExpression.setText(msg1);
            txtohh.setText(msg2);
        }else  if(ordertype.equalsIgnoreCase("PAYUWALLET")) {
            tvExpression.setText(msg1);
            txtohh.setText(msg2);
        }
        txtBack.setText(getResources().getString(R.string.failorderback));
        imgTopImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_failorder));
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
        Intent intent = new Intent(FailWalletActivity.this, WalletActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
