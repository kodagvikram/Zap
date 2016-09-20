package com.zaparound;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.ModelVo.CheckinLocationListVO;
import com.zaparound.Singleton.Appsingleton;

import org.jsoup.Jsoup;

import java.util.regex.Pattern;

public class UserTabhostActivity extends TabActivity implements TabHost.OnTabChangeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    TabHost tabHost;
    public static Appsingleton appsingleton;
    public static TextView tv_unreadcount;

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            appsingleton = Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            setContentView(R.layout.activity_user_tabhost);

            /*Check user allready logged in or not*/
            SharedPreferences setting = getSharedPreferences("OrderManagement", MODE_PRIVATE);
            String tab_page = setting.getString("TAB_PAGE", "0");
            //app singleton instanse
            appsingleton = Appsingleton.getinstance(this);
            appsingleton.activityArrayList.add(this);
            appsingleton.setTabhostcontext(this);
            try {
                /*
                *finish all previous activities
                */
                appsingleton.finishAll();
            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
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

//            /*Check user allready logged in or not*/
            int currentPosition;
            try {
                Intent intent = getIntent();
                currentPosition = intent.getIntExtra("CHATTAB_POSITION", 0);
                getIntent().removeExtra("CHATTAB_POSITION");
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();

                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(1 * 1000); // 1 second, in milliseconds

            } catch (Exception e) {
                currentPosition = 0;
                e.printStackTrace();
            }


            tabHost = getTabHost();
            TabHost.TabSpec spec;  // Reusable TabSpec for each tab
            Intent intent;  // Reusable Intent for each tab
            tabHost.getTabWidget().setDividerDrawable(null);

            // check in Tab
            View tabView = createTabView(this, "0", R.drawable.checkin_tab_selecter, 0);
            intent = new Intent().setClass(this, CheckinListActivity.class);
            spec = tabHost.newTabSpec("tab1").setIndicator(tabView).setContent(intent);
            tabHost.addTab(spec);

            // map order Tab
            tabView = createTabView(this, "1", R.drawable.map_tab_selecter, 1);
            intent = new Intent().setClass(this, NearbyLocationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            spec = tabHost.newTabSpec("tab2").setIndicator(tabView).setContent(intent);
            tabHost.addTab(spec);

            // chat order tab Tab
            tabView = createTabView(this, "2", R.drawable.chat_tab_selecter, 2);
            intent = new Intent().setClass(this, ChatListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            spec = tabHost.newTabSpec("tab3").setIndicator(tabView).setContent(intent);
            tabHost.addTab(spec);

            // contacts order tab Tab
            tabView = createTabView(this, "3", R.drawable.contact_tab_selecter, 3);
            intent = new Intent().setClass(this, ContactsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            spec = tabHost.newTabSpec("tab4").setIndicator(tabView).setContent(intent);
            tabHost.addTab(spec);

            tabHost.setCurrentTab(currentPosition);
            tabHost.getTabWidget().getChildAt(currentPosition)
                    .setBackgroundColor(getResources().getColor(R.color.loginbtn_color));
            /*On click Listener*/
            tabHost.setOnTabChangedListener(this);
            updateMessageCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of oncreate

    /*
       *Setting Click Tranction
       */
    public void SettingClick() {
        try {
            Intent intent3 = new Intent(this, SettingMainActivity.class);
            startActivity(intent3);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    private static View createTabView(Context context, String tabText, int resId, int tabpos) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null, false);
        TextView title = (TextView) view.findViewById(R.id.title);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);//Assign custom fonts
        title.setTypeface(appsingleton.lighttype);
        //title.setText(tabText);
        if (tabpos == 2) {
            title.setVisibility(View.VISIBLE);
            tv_unreadcount=title;
        }
        icon.setImageResource(resId);
        return view;
    }

    /*
       *function to update count
       */
    public void updateMessageCount() {
        try {
            int count=0;
            try{
                count=appsingleton.chatdbhelper.getUnreadCount();
            }catch(Exception e){
                e.printStackTrace();
                count=0;
                appsingleton.ToastMessage("" + e.getMessage());
            }
            tv_unreadcount.setText("" + count);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        try{
//        }catch(Exception e){
//            appsingleton.ToastMessage("" + e.getMessage());
//        }
        finish();
    }

    @Override
    public void onTabChanged(String tabId) {
        try {
            View view;
            view = tabHost.getCurrentTabView();
            for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                tabHost.getTabWidget().getChildAt(i)
                        .setBackgroundColor(getResources().getColor(R.color.bottombackground_color)); // unselected
            }
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
                    .setBackgroundColor(getResources().getColor(R.color.loginbtn_color)); // selected
            view.startAnimation(appsingleton.zoom_in_animation);

            switch (tabId) {
                case "tab1":
                    break;
                case "tab2":
                    break;
                case "tab3":
                    break;
                case "tab4":
                    break;
            }

        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed

    @Override
    public void onConnected(Bundle bundle) {
        try {
            if (appsingleton.needPermissionCheck()) {
                if (appsingleton.locationPermission(this)) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
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
}