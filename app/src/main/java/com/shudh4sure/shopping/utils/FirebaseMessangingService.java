package com.shudh4sure.shopping.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.shudh4sure.shopping.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class FirebaseMessangingService extends FirebaseMessagingService {


    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    public static int count = 0;
    Global global;
    private Boolean isLogin = true;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("CheckFCM", "Check_DONE");

        global = new Global(getApplicationContext());
        isLogin = global.getPrefBoolean("Verify", false);

        Map<String, String> data = remoteMessage.getData();

        String title = data.get("title");
        String message = data.get("message");
        String image = data.get("image");
        String notificationType = data.get("type");
        Log.e("image..", image);
        Log.e("notificationType..", notificationType);


        if (notificationType.equalsIgnoreCase("")) {
            final Intent offerIntent = new Intent();
            offerIntent.putExtra("message", message);
            offerIntent.putExtra("imageurl", image);
            if (isLogin) {
                offerIntent.setClassName(getApplicationContext(), "com.shudh4sure.shopping.activity.OffersActivity");
            } else {
                offerIntent.setClassName(getApplicationContext(), "com.shudh4sure.shopping.activity.LoginActivity");
            }

            ShowCommonNotificationWithImage(image, offerIntent, title, message);
        } else if ((notificationType.equalsIgnoreCase("Order"))) {
            final Intent intAlert = new Intent();
            intAlert.putExtra("notificationType", "order");
            intAlert.putExtra("notificationMessage", message);
            intAlert.putExtra("notificationTitle", title);
            intAlert.putExtra("notificationImage", image);

            if (isLogin) {
                intAlert.setClassName(getApplicationContext(), "com.shudh4sure.shopping.activity.MainActivity");
            } else {
                intAlert.setClassName(getApplicationContext(), "com.shudh4sure.shopping.activity.LoginActivity");
            }
            sendNotificationBlank(intAlert, title, message);
        } else if ((notificationType.equalsIgnoreCase("Order_delivery"))) {
            final Intent intOrderList = new Intent();
            intOrderList.putExtra("IntentType", "Order_delivery");

            if (isLogin) {
                intOrderList.setClassName(getApplicationContext(), "com.shudh4sure.shopping.activity.OrderHistoryActivity");
            } else {
                intOrderList.setClassName(getApplicationContext(), "com.shudh4sure.shopping.activity.LoginActivity");

            }

            intOrderList.setClassName(getApplicationContext(), "com.shudh4sure.shopping.activity.OrderHistoryActivity");//Start activity when user taps on notification.
            sendNotificationBlank(intOrderList, title, message);
        }


    }


    private void ShowCommonNotificationWithImage(String img, Intent gotoIntent, String StrTitle, String StrDescip) {

        Bitmap remote_picture = null;
        try {
            NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
            notiStyle.setSummaryText(StrDescip);
            remote_picture = getBitmapFromURL(img);


            int imageWidth = remote_picture.getWidth();
            int imageHeight = remote_picture.getHeight();

            DisplayMetrics metrics = this.getResources().getDisplayMetrics();

            int newWidth = metrics.widthPixels;
            float scaleFactor = (float) newWidth / (float) imageWidth;
            int newHeight = (int) (imageHeight * scaleFactor);

            remote_picture = Bitmap.createScaledBitmap(remote_picture, newWidth, newHeight, true);
            notiStyle.bigPicture(remote_picture);
            notificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent contentIntent = null;

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            contentIntent = PendingIntent.getActivity(getApplicationContext(),
                    (int) (Math.random() * 100), gotoIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
            android.app.Notification notification = mBuilder.setSmallIcon(R.drawable.ic_pushiconn)./*setTicker(strMsgTitle).*/setWhen(0)
                    .setColor(getResources().getColor(R.color.colorPrimary)).setAutoCancel(true).setContentTitle(StrTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(StrDescip))
                    .setContentIntent(contentIntent)
                    .setVibrate(new long[]{100, 250})
                    // .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSound(defaultSoundUri)
                    .setContentText(StrDescip).setStyle(notiStyle).build();
            count++;
            notificationManager.notify(count, notification);//This will generate seperate notification each time server sends.

        } catch (Exception e) {
            Log.e("This", e.getMessage() + "  0");
        }
    }


    private void sendNotificationBlank(Intent gotoIntent, String title, String message) {
        int icon = R.drawable.ic_pushiconn;

        try {
            PendingIntent contentIntent = null;
            contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
            notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
            notificationBuilder.setSmallIcon(icon);
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setContentText(message);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.setContentIntent(contentIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            count++;
            notificationManager.notify(count, notificationBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            //    Log.e("This Second", e.getMessage().toString());
            return null;
        }
    }
}
