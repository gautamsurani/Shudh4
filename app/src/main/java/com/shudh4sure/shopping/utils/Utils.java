package com.shudh4sure.shopping.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.shudh4sure.shopping.R;

public class Utils {
    private Context _context;

    // constructor
    public Utils(Context context) {
        this._context = context;

    }


    public static boolean isValidEmail(String strEmail) {
        return strEmail != null && android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches();
    }

    public static boolean isNetworkAvailable(Context activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

        String sPX;

        if (String.valueOf(px).contains(".")) {
            sPX = String.valueOf(px).substring(0, String.valueOf(px).indexOf("."));
        } else {
            sPX = String.valueOf(px);
        }

        return Integer.parseInt(sPX);
    }

    public static void showToastShort(String message, Activity noticeBoard) {
        // TODO Auto-generated method stub
        LayoutInflater mylf = noticeBoard.getLayoutInflater();
        @SuppressLint("InflateParams") View myview = mylf.inflate(R.layout.toast_layout, null);
        TextView text_data = (TextView) myview.findViewById(R.id.toast_text);
        Typeface fonts1 = Typeface.createFromAsset(noticeBoard.getAssets(), "fonts/MavenPro-Regular.ttf");
        text_data.setTypeface(fonts1);
        text_data.setText(message);
        Toast mytoast = new Toast(noticeBoard);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.setView(myview);
        mytoast.show();
    }

    public static void showToastLong(String message, Activity noticeBoard) {
        // TODO Auto-generated method stub
        LayoutInflater mylf = noticeBoard.getLayoutInflater();
        @SuppressLint("InflateParams") View myview = mylf.inflate(R.layout.toast_layout, null);
        TextView text_data = (TextView) myview.findViewById(R.id.toast_text);

        Typeface fonts1 = Typeface.createFromAsset(noticeBoard.getAssets(), "fonts/MavenPro-Regular.ttf");
        text_data.setTypeface(fonts1);
        text_data.setText(message);
        Toast mytoast = new Toast(noticeBoard);
        mytoast.setDuration(Toast.LENGTH_LONG);
        mytoast.setView(myview);
        mytoast.show();
    }

    public static void hideKeyboard(Activity noticeBoard) {
        View view = noticeBoard.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) noticeBoard.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity noticeBoard) {
        View view = noticeBoard.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) noticeBoard.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void ShowSnakBar(String s, View linearLayout, Activity login) {
        Snackbar snackbar = Snackbar.make(linearLayout, s, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView tv = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        sbView.setBackgroundColor(ContextCompat.getColor(login, R.color.colorPrimary));
        snackbar.show();
    }

    public static void displayDialog(String title, final Context context, final boolean isFinish) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Transaction Cancelled!");
        alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isFinish)
                    ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        final AlertDialog dialog = alertDialog.create();
        if (!((Activity) context).isFinishing()) {
            if (!dialog.isShowing()) {
                alertDialog.show();
            }
        }
    }
}
