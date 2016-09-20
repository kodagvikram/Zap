package com.zaparound;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.Singleton.Appsingleton;

import org.jsoup.Jsoup;

import java.util.regex.Pattern;

public class LandingActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    public ImageView iv_logoimage,iv_apptext;
    public LinearLayout ll_bottombtnlayout;
    public TextView tv_logotitle;
    public Button bt_login,bt_signup;
    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        try {
            if(!appsingleton.getCheckinsession())
            mGoogleApiClient.connect();
        }catch (Exception e){e.printStackTrace();}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Appsingleton.removeinstance();
            appsingleton = Appsingleton.getinstance(this);
            setTheme(R.style.LandingTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_landing);
             //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);
            //check marshmallo permisions
            try{
                if(appsingleton.needPermissionCheck())
                appsingleton.locationPermission(this);
                appsingleton.finishAll();
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }

            try {
               if(!appsingleton.getCheckinsession()) {
                   mGoogleApiClient = new GoogleApiClient.Builder(this)
                           .addConnectionCallbacks(this)
                           .addOnConnectionFailedListener(this)
                           .addApi(LocationServices.API)
                           .build();

                   mLocationRequest = LocationRequest.create()
                           .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                           .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                           .setFastestInterval(1 * 1000); // 1 second, in milliseconds

               }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try{
                if (AppUtils.isNetworkAvailable(this)) {
                    CheckVersion task = new CheckVersion();
                    task.execute();
                }
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }


            if(appsingleton.getUserid()==0)
            {
               //continue
            }
            else
            {
                try {
                    appsingleton.locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, 1200000, 0,
                            appsingleton.networkLocationListener);

                    appsingleton. locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            5000, 0, appsingleton.gpsLocationListener);
                }catch (Exception e){e.printStackTrace();}
                if(appsingleton.getCheckinsession())
                {
                    int value=0;
                    int chatvalue=0;
                    try{
                        Bundle extras = getIntent().getExtras();
                        if (extras != null) {
                            value = extras.getInt("CHECKIN_TAB_POS", 0);
                            chatvalue = extras.getInt("CHATTAB_POSITION", 0);
                        }
                    }catch(Exception e){
                        value=0;
                        chatvalue=0;
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                        Intent intent=new Intent(LandingActivity.this,ZapfeedTabhostActivity.class);
                        intent.putExtra("CHECKIN_TAB_POS",value);
                        intent.putExtra("CHATTAB_POSITION",chatvalue);
                    startActivity(intent);
                    finish();
                }
                else {
                    int chatvalue=0;
                    try{
                        Bundle extras = getIntent().getExtras();
                        if (extras != null) {
                            chatvalue = extras.getInt("CHATTAB_POSITION", 0);
                        }
                    }catch(Exception e){
                        chatvalue=0;
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                    Intent intent = new Intent(LandingActivity.this, UserTabhostActivity.class);
                    intent.putExtra("CHATTAB_POSITION",chatvalue);
                    startActivity(intent);
                    finish();
                }
            }

            //initialize views
            iv_logoimage=(ImageView)findViewById(R.id.iv_LOGOIMAGE);
            tv_logotitle=(TextView)findViewById(R.id.tv_logintitle);
            ll_bottombtnlayout=(LinearLayout) findViewById(R.id.ll_Buttonlayout);
            bt_login=(Button) findViewById(R.id.bt_Loginbtn);
            bt_signup=(Button) findViewById(R.id.bt_Signupbtn);
            iv_apptext=(ImageView)findViewById(R.id.iv_logintitle);
            /*set custom fonts*/
            tv_logotitle.setTypeface(appsingleton.boldtype);
            bt_login.setTypeface(appsingleton.regulartype);
            bt_signup.setTypeface(appsingleton.regulartype);

            /*setheight width Programatically*/
            SetHeightWidth(iv_logoimage,0.55,0.32);
            SetHeightWidth(iv_apptext,0.50,0.13);
            /*Set Margins*/
            setMargins(ll_bottombtnlayout,0.07,0,0.07,0.06);
            setMargins(iv_logoimage,0,0.11,0,0);

            DisplayMetrics metrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            float yInches= metrics.heightPixels/metrics.ydpi;
            float xInches= metrics.widthPixels/metrics.xdpi;
            double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
            if (diagonalInches>=6.5){
                AppUtils.ShowAlertDialog(this,"Not Support for Tablets");
            }else{
                // smaller device
            }

        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of oncreate

    /*
    *Login Click
    */
    public void Login(View view)
    {
        try {
            if(view.getId()==bt_login.getId()) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
            else if(view.getId()==bt_signup.getId()) {
                Intent intent = new Intent(this, RegistrationFirstActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
* To set View height width programatically
* */
    public void SetHeightWidth(View view, double width, double height) {
        try {
            if (width == 0) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = (int) ((appsingleton.deviceheight) * height);
            } else if (height == 0) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = (int) ((appsingleton.devicewidth) * height);
            } else {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = (int) ((appsingleton.devicewidth) * width);
                params.height = (int) ((appsingleton.deviceheight) * height);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of heightwidth

    /*
       * To set View margins programatically
       * */
    public void setMargins(View v, double l, double t, double r, double b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v
                    .getLayoutParams();
            int lpx = (int) ((appsingleton.devicewidth) * l);
            int tpx = (int) ((appsingleton.deviceheight) * t);
            int rpx = (int) ((appsingleton.devicewidth) * r);
            int bpx = (int) ((appsingleton.deviceheight) * b);
            p.setMargins(lpx, tpx, rpx, bpx);
            v.requestLayout();
        }
    }//end of margins
    @Override
    public void onConnected(Bundle bundle) {
        try{
            if(appsingleton.needPermissionCheck()) {
                if (appsingleton.locationPermission(this)) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (location == null) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                    } else {
                        //If everything went fine lets get latitude and longitude
                        appsingleton.currentLatitude = location.getLatitude();
                       appsingleton.currentLongitude = location.getLongitude();

                    }
                } else {
                    appsingleton.ToastMessage("Location Permission Missing");
                }
            }
            else
            {
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (location == null) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                } else {
                    //If everything went fine lets get latitude and longitude
                    appsingleton.currentLatitude = location.getLatitude();
                    appsingleton.currentLongitude = location.getLongitude();

                }
            }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        appsingleton. currentLatitude = location.getLatitude();
        appsingleton. currentLongitude = location.getLongitude();
//        appsingleton.ToastMessage("Current Lat="+ appsingleton. currentLatitude+"    Current Long="+ appsingleton. currentLongitude );

    }

    /*
    *check playstotr version Async task
    */
    private class CheckVersion extends AsyncTask<String, Void, String> {
        String oldVersion;
         String appPackageName;
        public CheckVersion() {
            appPackageName = getPackageName();
            //appPackageName="ps.memeit";
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                //get play store version
                oldVersion = Jsoup
                        .connect("https://play.google.com/store/apps/details?id=" + appPackageName + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com").get()
                        .select("div[itemprop=softwareVersion]").first()
                        .ownText();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (oldVersion != null) {
                checkAPPVersion(oldVersion);
            }
        }
    }


    /*
    *function to check Appstore version
    */
    public void checkAPPVersion(String newVersion) {
        try {
            String playStoreVersion = newVersion;

            // get current app version
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String appVersion = pInfo.versionName;

            String[] array1 = playStoreVersion.split(Pattern.quote("."));
            String[] array2 = appVersion.split(Pattern.quote("."));

            if (!array1[0].equals(array2[0])) {
                showDialog2();
            } else if (!array1[1].equals(array2[1])) {
                showDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
      * to dosplay dialog minur dialog
      * */
    public void showDialog() {
        try {
            final Dialog dialog = new Dialog(this);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.update_app_popup);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tv_msg, tv_title;
            ImageView iv_close, iv_middle;
            Button bt_yes, bt_no;
            tv_title = (TextView) dialog.findViewById(R.id.tv_popuptitle);
            tv_msg = (TextView) dialog.findViewById(R.id.tv_popupsubtitle);
            iv_close = (ImageView) dialog.findViewById(R.id.iv_Crossimage);
            iv_middle = (ImageView) dialog.findViewById(R.id.iv_middleimage);
            bt_yes = (Button) dialog.findViewById(R.id.btn_yes);
            bt_no = (Button) dialog.findViewById(R.id.btn_no);

             /*Assining fonts*/
            tv_msg.setTypeface(appsingleton.lighttype);
            tv_title.setTypeface(appsingleton.regulartype);
            bt_yes.setTypeface(appsingleton.regulartype);
            bt_no.setTypeface(appsingleton.regulartype);

//            try {
//                tv_title.setText(title);
//                tv_msg.setText( message);
//                bt_yes.setText(btnright);
//                bt_no.setText(btnleft);
//                iv_middle.setImageResource(imageid);
//            } catch (Exception e) {
//                appsingleton.ToastMessage("" + e.getMessage());
//            }
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            bt_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        dialog.dismiss();
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
            // Set dialog title
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of show dialog
    /*
      * to dosplay dialog major dialog
      * */
    public void showDialog2() {
        try {
            final Dialog dialog = new Dialog(this);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.update_app_popup2);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tv_msg, tv_title;
            ImageView iv_close, iv_middle;
            Button bt_yes, bt_no;
            tv_title = (TextView) dialog.findViewById(R.id.tv_popuptitle);
            tv_msg = (TextView) dialog.findViewById(R.id.tv_popupsubtitle);
            iv_close = (ImageView) dialog.findViewById(R.id.iv_Crossimage);
            iv_middle = (ImageView) dialog.findViewById(R.id.iv_middleimage);
            bt_yes = (Button) dialog.findViewById(R.id.btn_yes);
            bt_no = (Button) dialog.findViewById(R.id.btn_no);

             /*Assining fonts*/
            tv_msg.setTypeface(appsingleton.lighttype);
            tv_title.setTypeface(appsingleton.regulartype);
            bt_yes.setTypeface(appsingleton.regulartype);
            bt_no.setTypeface(appsingleton.regulartype);

//            try {
//                tv_title.setText(title);
//                tv_msg.setText( message);
//                bt_yes.setText(btnright);
//                bt_no.setText(btnleft);
//                iv_middle.setImageResource(imageid);
//            } catch (Exception e) {
//                appsingleton.ToastMessage("" + e.getMessage());
//            }
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    dialog.dismiss();
                }
            });
            bt_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        dialog.dismiss();
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
            // Set dialog title
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of show dialog
}//end of Activity
