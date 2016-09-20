package com.zaparound.AppUtils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.zaparound.R;
import com.zaparound.Singleton.Appsingleton;

public class AppUtils {

    // Google Sercer Key
    public static final String GOOGLE_API_KEY = "AIzaSyBE6-AxlE_53gAy9yHFU0_paLItMqyp9TY";

    //public static final String COMETCHAT_SITEURL = "http://192.168.1.33/zap1/cometchat/";
    //public static final String COMETCHAT_APIKEY = "236264c232085b98d645f3641e296298";

    //Check in location nextpage token
    public static String CHECKIN_NEXTURL_TOKEN="";
   public static Appsingleton appsingleton;
    /*
    *Envernment Variable for
     *
      * PRODUCTION for Production build
      *
      * DEVELOPMENT for Development build
    */
    public static String environment="DEVELOPMENT";
    public static boolean isNetworkAvailable(Context context) {
        try{
        appsingleton=Appsingleton.getinstance(context);
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
            return  false;
        }
    }

    public static void ShowAlertDialog(final Context context, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
        builder.setTitle(context.getResources().getString(R.string.app_name));
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                ((Activity)context).finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}