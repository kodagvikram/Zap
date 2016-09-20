package com.zaparound.Singleton;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.maps.android.SphericalUtil;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.SubscribeCallbacks;
import com.inscripts.utils.Logger;
import com.zaparound.AlertSnackbar;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.Database.Database;
import com.zaparound.IntentServices.CheckoutIntentService;
import com.zaparound.LandingActivity;
import com.zaparound.LoginActivity;
import com.zaparound.LogsActivity;
import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.CheckinZaparoundVO;
import com.zaparound.ModelVo.CheckinZapmeVO;
import com.zaparound.ModelVo.ContactsLivelistVO;
import com.zaparound.ModelVo.InterestVO;
import com.zaparound.ModelVo.SingleChatMessage;
import com.zaparound.R;
import com.zaparound.TSnackbar;
import com.zaparound.UserTabhostActivity;
import com.zaparound.ZapfeedTabhostActivity;
import com.zaparound.helper.DatabaseHandler;
import com.zaparound.helper.Keys;
import com.zaparound.helper.PushNotificationsManager;
import com.zaparound.helper.SharedPreferenceHelper;
import com.zaparound.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Appsingleton {
    public static CometChat cometchat;
    public static String siteUrl = "";
    public Context context, tabhostcontext, zaptabhostcontext;
    public static Appsingleton appsingleton = null;
    public int devicewidth = 0;
    public int deviceheight = 0;
    public Typeface regulartype = null, thintype = null, boldtype = null, lighttype = null;
    public Animation zoom_in_animation, shake_animation, gender_up_animation, gender_down_animation, tabzoom_animation;
    public SharedPreferences sharedPreferences;
    public ArrayList<String> stringArrayList;
    public ArrayList<String> stringArrayList2;
    public double currentLatitude = 0;
    public double currentLongitude = 0;
    public int animationduration=1000;
    public String headerdate="";
    public int maxheight = 1000, maxwidth = 1000;
    // public ArrayList<String> stringArrayList3;
    public ArrayList<InterestVO> intrestlist;
    public Dialog dialog = null;
    private static char[] hextable = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public Database mDatabase;
    public DatabaseHandler chatdbhelper;
    public ArrayList<Activity> activityArrayList = new ArrayList<>();
    public ArrayList<CheckinZaparoundVO> zaparoundlist = new ArrayList<>();
    public ArrayList<CheckinZaparoundVO> tempzaparoundlist = new ArrayList<>();
    public ArrayList<CheckinZapmeVO> tempzapmelist = new ArrayList<>();
    public ArrayList<ChatUserListVO> tempchatuserlist = new ArrayList<>();
    public ArrayList<ContactsLivelistVO> tempLiveuserlist = new ArrayList<>();
    public static LocationManager locationManager;


    /*CometChatCloud cloud;*/
    public static final ArrayList<String> logs = new ArrayList<String>();

    /* Modify the URL to point to the site you desire. */
    private static final String SITE_URL = "http://192.168.0.159/";

	/* Change this value to a valid user ID on the above site. */
    //public static final String USER_ID = "6";

    public static String myId;

    public final LocationListener gpsLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            try {
                locationManager.removeUpdates(networkLocationListener);
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                ToastMessage("gpsLocationListener Lat=" + appsingleton.currentLatitude + "    Current Long=" + appsingleton.currentLongitude);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (getCheckinsession()) {
                    finishAllSession();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastMessage("" + e.getMessage());
            }
        }
    };
    public final LocationListener networkLocationListener =
            new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }

                @Override
                public void onLocationChanged(Location location) {
                    try {
                        try {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            ToastMessage("networkLocationListener Lat=" + appsingleton.currentLatitude + "    Current Long=" + appsingleton.currentLongitude);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (getCheckinsession()) {
                            finishAllSession();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastMessage("" + e.getMessage());
                    }
                }
            };

    public Appsingleton(Context context) {
        this.context = context;
        try {
            try {
                mDatabase = new Database(context);
                chatdbhelper = new DatabaseHandler(context);
            } catch (Exception e) {
                e.printStackTrace();
                ToastMessage("" + e.getMessage());
            }

            regulartype = Typeface.createFromAsset(context.getAssets(), "fonts/font-Regular.ttf");
            thintype = Typeface.createFromAsset(context.getAssets(), "fonts/font-Thin.ttf");
            boldtype = Typeface.createFromAsset(context.getAssets(), "fonts/font-Bold.ttf");
            lighttype = Typeface.createFromAsset(context.getAssets(), "fonts/font-Light.ttf");
            //set aninmation
            zoom_in_animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.zoomin_checkout);
            shake_animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.shake_view);
            gender_up_animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.genderup_animation);
            gender_down_animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.genderdown_animation);
            tabzoom_animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.tabzoom_animation);
            stringArrayList = new ArrayList<>();
            stringArrayList2 = new ArrayList<>();
            intrestlist = new ArrayList<>();
            sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context.getApplicationContext());
            initDialog(context);
        } catch (Exception e) {
            ToastMessage(e.getMessage());
        }
    }//end of constructor

    public static Appsingleton getinstance(Context context) {
        if (appsingleton == null) {
            appsingleton = new Appsingleton(context);
            try {
                Appsingleton.sendHeartbeat();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                locationManager = (LocationManager)
                        context.getSystemService(Context.LOCATION_SERVICE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                initCometchat(context);
            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
            return appsingleton;
        }//end iof if
        else {
            try {
                Appsingleton.sendHeartbeat();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return appsingleton;

        }
    }//end of instance

    public static void initCometchat(Context context) {
        try {
            SharedPreferenceHelper.initialize(context);
            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.API_KEY, ConnectionsURL.getCometchatSiteurl());
            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.SITE_URL, ConnectionsURL.getCometchatApikey());

            String apikey = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY);
            siteUrl = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.SITE_URL);

            cometchat = CometChat.getInstance(context.getApplicationContext(),
                    SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));
        } catch (Exception e) {
            e.printStackTrace();
            //appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
    *get Cometchat object
    */
    public CometChat getCometchatObject() {
        return cometchat;
    }

    public static void removeinstance() {
        appsingleton = null;
    }

    /*
    *to initialize dialog
    */
    public void initDialog(Context context) {
        try {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.custom_dialog);
            dialog.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *to show dialog
    */
    public void showDialog() {
        try {
            if (dialog != null && !dialog.isShowing()) {
                ImageView loader3 = (ImageView) dialog.findViewById(R.id.iv_loaderthird);
                Animation anim1 = AnimationUtils.loadAnimation(context, R.anim.rotateloader_ring_one);
                loader3.startAnimation(anim1);
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
       *to dismiss dialog
       */
    public void dismissDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * To show toast message by passing message body
     * @param msg Message to display
     */
    public void ToastMessage(String msg) {
        try {
            if (AppUtils.environment.equalsIgnoreCase("DEVELOPMENT"))
                Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * To show UserToastMessage by passing message body
     * @param msg Message to display
     */
    public void UserToastMessage(String msg) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /*
    * function to check empty check
    * */
    public boolean isEmpty(EditText editText) {
        try {
//            if (editText.getText().toString().trim().isEmpty()) {
//                return true;
//            } else
//                return false;
            return (editText.getText().toString().trim().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /*
    * to validate email
    * */
    public boolean isvalidateEmail(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            return false;
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
                return false;
            } else
                return true;
        }
    }

    /*
   * to validate mobile
   */
    public boolean isvalidatemobile(EditText editText) {
        try {

            if (editText.getText().toString().trim().isEmpty()) {
                return false;
            } else {
                if (editText.getText().toString().length() < 9) {
                    return false;
                } else {
                    return android.util.Patterns.PHONE.matcher(editText.getText().toString()).matches();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /*
   * to validate UserName
   */
    public boolean isvalidateLength(EditText editText, int range) {
        try {

            if (editText.getText().toString().trim().isEmpty()) {
                return false;
            } else {
                if (editText.getText().toString().length() < range) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
  * to validate passwordlength
  */
    public boolean isvalidaPasswordLength(EditText editText) {
        try {
            if (editText.getText().toString().trim().isEmpty()) {
                return false;
            } else {
                if (editText.getText().toString().length() > 5 && editText.getText().toString().length() < 21) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
   * to match password
   */
    public boolean isPasswordMatch(EditText editText1, EditText editText2) {
        if (editText1.getText().toString().trim().isEmpty() || editText2.getText().toString().trim().isEmpty()) {
            return false;
        } else {
            if (editText1.getText().toString().equals(editText2.getText().toString())) {
                return true;
            } else
                return false;
        }
    }

    /*
    * function for persion name
    */
    public boolean isInValidpersonname(EditText editText) {
        try {
            CharSequence inputStr = editText.getText().toString();
            Pattern pattern = Pattern.compile(new String("^[a-zA-Z\\s]*$"));
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /*
     * To show Snackbar message by passing message body
     *
     * @param title  msg and view Message to display
     */
    public void SnackbarMessage(String title, String msg, View view) {
        try {
            final TSnackbar snackbar = TSnackbar
                    .make(view, title, TSnackbar.LENGTH_LONG);
            snackbar.setActionTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(context.getResources().getColor(R.color.snackbar_background));
            //snackbarView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.menu_dropdown_panel_example));
            TextView textView = (TextView) snackbarView.findViewById(com.zaparound.R.id.snackbar_text);
            TextView subtextView = (TextView) snackbarView.findViewById(com.zaparound.R.id.snackbar_subtext);
            Button bt_cancle = (Button) snackbarView.findViewById(com.zaparound.R.id.snackbar_action);
            textView.setTextColor(context.getResources().getColor(R.color.white));
            subtextView.setTextColor(context.getResources().getColor(R.color.snackbar_subtextcolor));

            if (msg.equalsIgnoreCase("")) {
                if (Build.VERSION.SDK_INT < 23) {
                    textView.setTextAppearance(context, android.R.style.TextAppearance_Small);
                } else {
                    textView.setTextAppearance(android.R.style.TextAppearance_Small);
                }
                textView.setSingleLine(false);
                textView.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            }
            subtextView.setText(msg);
            snackbar.show();
            bt_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTextAppearance(Context context, int resId) {


    }

    /*
    * To show Snackbar message by passing message body
    *
    * @param title  msg and view Message to display
    */
    public void AlertSnackbarMessage(String title, String msg, View view) {
        try {
            final AlertSnackbar snackbar = AlertSnackbar
                    .make(view, title, AlertSnackbar.LENGTH_LONG);
            snackbar.setActionTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(context.getResources().getColor(R.color.md_deep_orange_400));
            //snackbarView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.menu_dropdown_panel_example));
            TextView textView = (TextView) snackbarView.findViewById(com.zaparound.R.id.snackbar_text);
            TextView subtextView = (TextView) snackbarView.findViewById(com.zaparound.R.id.snackbar_subtext);
            Button bt_cancle = (Button) snackbarView.findViewById(com.zaparound.R.id.snackbar_action);
            textView.setTextColor(context.getResources().getColor(R.color.white));
            subtextView.setTextColor(context.getResources().getColor(R.color.snackbar_subtextcolor));
            subtextView.setText(msg);
            snackbar.show();
            bt_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *Dynamically Add views to opoup
    */
    public void addIntrestGrid(Context context, RelativeLayout mainlayout, ArrayList<InterestVO> intrestlist) {
        try {

            int buttonwidth = (int) (devicewidth * 0.25);
            int buttonheight = (int) (deviceheight * 0.06);
            mainlayout.removeAllViews();
            int temp = 0;

            for (int i = 0; i < intrestlist.size(); i++) {
                // ******************************************

                RelativeLayout layout = new RelativeLayout(context);
                layout.setId(100 + temp);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = (int) (deviceheight * 0.02);
                if (i != 0)
                    layoutParams.addRule(RelativeLayout.BELOW, 99 + temp);

                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                layout.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(buttonwidth, buttonheight);
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(buttonwidth, buttonheight);
                RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(buttonwidth, buttonheight);


                final TextView btn1 = new TextView(context);
                btn1.setId(2 + i);
                btn1.setBackgroundResource(R.drawable.checkin_list_buttonback);
                btn1.setTextColor(context.getResources().getColor(R.color.cl_popup));
                btn1.setTypeface(regulartype);
                btn1.setGravity(Gravity.CENTER);
                btn1.setSingleLine(true);
                btn1.setText(intrestlist.get(i).getIntrest());

                final TextView btn2 = new TextView(context);
                btn2.setId(3 + i);
                btn2.setBackgroundResource(R.drawable.checkin_list_buttonback);
                btn2.setTextColor(context.getResources().getColor(R.color.cl_popup));
                btn2.setTypeface(regulartype);
                btn2.setGravity(Gravity.CENTER);
                btn2.setSingleLine(true);

                final TextView btn3 = new TextView(context);
                btn3.setId(4 + i);
                btn3.setBackgroundResource(R.drawable.checkin_list_buttonback);
                btn3.setTextColor(context.getResources().getColor(R.color.cl_popup));
                btn3.setTypeface(regulartype);
                btn3.setGravity(Gravity.CENTER);
                btn3.setSingleLine(true);

                i++;
                if (i < intrestlist.size()) {
                    btn2.setText(intrestlist.get(i).getIntrest());
                    params2.addRule(RelativeLayout.RIGHT_OF, btn1.getId());
                    params2.setMargins(20, 0, 0, 0);
                }

                i++;
                if (i < intrestlist.size()) {
                    btn3.setText(intrestlist.get(i).getIntrest());
                    params3.addRule(RelativeLayout.RIGHT_OF, btn2.getId());
                    params3.setMargins(20, 0, 0, 0);
                }

                layout.addView(btn1, params1);
                if (!btn2.getText().toString().equalsIgnoreCase(""))
                    layout.addView(btn2, params2);
                if (!btn3.getText().toString().equalsIgnoreCase(""))
                    layout.addView(btn3, params3);
                // *******************************************
                mainlayout.addView(layout, layoutParams);
                temp++;

            }// end of for

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }//end of list values

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

    /*
    *Function to MD5
    */
    public static String byteArrayToHex(byte[] array) {
        String s = "";
        for (int i = 0; i < array.length; ++i) {
            int di = (array[i] + 256) & 0xFF; // Make it unsigned
            s = s + hextable[(di >> 4) & 0xF] + hextable[di & 0xF];
        }
        return s;
    }

    public static String digest(String s, String algorithm) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return s;
        }

        m.update(s.getBytes(), 0, s.length());
        return byteArrayToHex(m.digest());
    }

    public String md5(String s) {
        return digest(s, "MD5");
    }
    //-------------End of MD5

    /*Marsh mallo Permissions*/
    public void CheckPermisions() {
        try {
            networkStatePermission(context);
            receivebootcompletedPermission(context);
            phoneStatePermission(context);
            readSMSPermission(context);
            phoneCallPermission(context);
            cameraPermission(context);
            locationPermission(context);
            externalreadPermission(context);
            phoneContactsPermission(context);
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
    }//end of permission

    /*
      *function to check Networkstate permission
      *@return boolean true for granted false for request
      */
    public boolean networkStatePermission(Context context) {
        try {

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now
                ActivityCompat.requestPermissions(((Activity) context), new String[]{
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.ACCESS_WIFI_STATE
                }, 1);
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
        return false;
    }

    /*
      *function to check RECEIVE_BOOT_COMPLETED permission
      *@return boolean true for granted false for request
      */
    public boolean receivebootcompletedPermission(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.GET_TASKS) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.KILL_BACKGROUND_PROCESSES) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now
                ActivityCompat.requestPermissions(((Activity) context), new String[]{
                        android.Manifest.permission.GET_TASKS,
                        android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        android.Manifest.permission.KILL_BACKGROUND_PROCESSES
                }, 1);
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
        return false;
    }

    /*
       *function to check phoneState permission
       *@return boolean true for granted false for request
       */
    public boolean phoneStatePermission(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now

                ActivityCompat.requestPermissions(((Activity) context), new String[]{
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.VIBRATE
                }, 1);
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
        return false;
    }

    /*
   *function to check readSMS permission
   *@return boolean true for granted false for request
   */
    public boolean readSMSPermission(Context context) {
        try {
            if(needPermissionCheck()) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // Check Permissions Now

                    ActivityCompat.requestPermissions(((Activity) context), new String[]{
                            android.Manifest.permission.READ_SMS,
                            android.Manifest.permission.RECEIVE_SMS
                    }, 1);
                } else
                    return true;
            }
            else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
        return false;
    }

    /*
   *function to check startInstalledAppDetailsActivity
   *@return boolean true for granted false for request
   */
    public void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    /*
    *function to check sdkversion
    *@return boolean true for granted false for request
    */
    public boolean needPermissionCheck() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        return (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP);
    }

    /*
    *function to check CALL_PHONE permission
    *@return boolean true for granted false for request
    */
    public boolean phoneCallPermission(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now

                ActivityCompat.requestPermissions(((Activity) context), new String[]{
                        android.Manifest.permission.CALL_PHONE
                }, 1);
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
        return false;
    }

    /*
    *function to check camera permission
    *@return boolean true for granted false for request
    */
    public boolean cameraPermission(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now

                ActivityCompat.requestPermissions(((Activity) context), new String[]{
                        android.Manifest.permission.CAMERA
                }, 1);
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
        return false;
    }

    /*
   *function to check Fine location
   *@return boolean true for granted false for request
   */
    public boolean locationPermission(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now

                ActivityCompat.requestPermissions(((Activity) context), new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, 1);
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
        return false;
    }

    /*
   *function to check externalread permission
   *@return boolean true for granted false for request
   */
    public boolean externalreadPermission(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now

                ActivityCompat.requestPermissions(((Activity) context), new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
        return false;
    }

    /*
  *function to check phoneContacts permission
  *@return boolean true for granted false for request
  */
    public boolean phoneContactsPermission(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now

                ActivityCompat.requestPermissions(((Activity) context), new String[]{
                        android.Manifest.permission.READ_CONTACTS
                }, 1);
            } else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
        return false;
    }

    /*
    *function to Logout
    */
    public void Logout() {
        try {
            try{
                mDatabase.deleteUserprofileDetails();
                mDatabase.deleteConnectionDetails();
                mDatabase.deleteInterestDetails();
                chatdbhelper.deleteallmessages();
            }catch(Exception e){
                e.printStackTrace();
                ToastMessage("" + e.getMessage());
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }

    }//end of logout

    /*
    *function to Checkout
    */

    /*
    *function to get meter to miles
    */
    public String getMiles(double i) {
        try {
            DecimalFormat formater = new DecimalFormat("#.##");
            return Double.valueOf(formater.format(i * 0.000621371192)) > 1 ? formater.format(i * 0.000621371192) + " miles" : Math.round(i) + " meters";
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage(e.getMessage());
            return "";
        }

    }

    /*
    *function to resize bitmap
    */
    public Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        try {

            if (maxHeight > 0 && maxWidth > 0) {
                int width = image.getWidth();
                int height = image.getHeight();
                float ratioBitmap = (float) width / (float) height;
                float ratioMax = (float) maxWidth / (float) maxHeight;

                int finalWidth = maxWidth;
                int finalHeight = maxHeight;
                if (ratioMax > 1) {
                    finalWidth = (int) ((float) maxHeight * ratioBitmap);
                } else {
                    finalHeight = (int) ((float) maxWidth / ratioBitmap);
                }
                image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
                return image;
            } else {
                return image;
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
            return image;
        }
    }

    /*
    *function to get userid
    */
    public int getUserid() {
        int userid = 0;
        try {
            userid = sharedPreferences.getInt("ZAPUSER_ID", 0);
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            userid = 0;
        }
        return userid;
    }

    /*
    *function to get Checkin id
    */
    public String getCheckinId() {
        try {
            return (sharedPreferences.getString("CHECKIN_ID", ""));
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            return "";
        }
    }

    /*
       *function to get Previous lat
       */
    public Double getPreLat() {

        try {
            return Double.valueOf(sharedPreferences.getString("PRE_LATITUDE", "0.0"));
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            return 0.0;
        }

    }

    /*
      *function to get Previous lang
      */
    public Double getPreLang() {
        try {
            return Double.valueOf(sharedPreferences.getString("PRE_LONGITUDE", "0.0"));
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            return 0.0;
        }
    }

    /*
     *function to get Check in placeID
     */
    public String getCheckinPlaceid() {
        try {
            return (sharedPreferences.getString("CHECKINPLACE_ID", ""));
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            return "";
        }
    }

    /*
     *function to get placename
     */
    public String getCheckinPlacename() {
        try {
            return (sharedPreferences.getString("PLACENAME", ""));
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            return "";
        }
    }

    /*
         *function to get  getCheckoutDistance
         */
    public float getCheckoutDistance() {
        try {
            return (sharedPreferences.getFloat("CHECKOUTDESTANCE", 200));
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            return 200;
        }
    }

    /*
    *function to setsession ZApfeed
    */
    public void setCheckinsession(boolean session) {
        try {
            SharedPreferences.Editor editor = appsingleton.sharedPreferences.edit();
            editor.putBoolean("CHECKIN_SESSION", session);
            if (!session) {
                editor.putString("PLACENAME", "");
                editor.putString("CHECKINPLACE_ID", "");
                editor.putString("PRE_LONGITUDE", "0.0");
                editor.putString("PRE_LATITUDE", "0.0");
                editor.putString("CHECKIN_ID", "");
            }
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
    }

    /*
    *function to get checkin session
    */
    public boolean getCheckinsession() {
        try {
            return (sharedPreferences.getBoolean("CHECKIN_SESSION", false));
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            return false;
        }
    }

    /*
    *function to setTabhostcontext
    */

    public void setTabhostcontext(Context tabhostcontext) {
        this.tabhostcontext = tabhostcontext;
    }

    /*
    *function to setZapTabhostcontext
    */

    public void setZaptabhostcontext(Context zaptabhostcontext) {
        this.zaptabhostcontext = zaptabhostcontext;
    }

    public UserTabhostActivity getTabhostcontext() {
        try {
            UserTabhostActivity activity = (UserTabhostActivity) tabhostcontext;
            return activity;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            return new UserTabhostActivity();
        }
    }

    public ZapfeedTabhostActivity getZaptabhostcontext() {
        try {
            ZapfeedTabhostActivity activity = (ZapfeedTabhostActivity) zaptabhostcontext;
            return activity;
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            return new ZapfeedTabhostActivity();
        }
    }

    /*
    *function to Get FCM DEVICE TOKEN KEY
    */
    public String getFCMtoken() {
        try {
            return FirebaseInstanceId.getInstance().getToken();
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            return "";
        }
    }

    /*
     *function to finish  Previous Activities
     */
    public void finishAll() {
        try {
            for (int i = 0; i < activityArrayList.size() - 1; i++) {
                try {
                    activityArrayList.get(i).finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     *function to finish  Previous Activities
     */
    public void finishAllSession() {
        try {
            if (getUserid() == 0) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                for (int i = 0; i < activityArrayList.size() - 1; i++) {
                    try {
                        activityArrayList.get(i).finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }//end of if
            else {
                if (getCheckinsession()) {
                    try {
                        double distance = getDistance(getPreLat(), getPreLang(), currentLatitude, currentLongitude);

                        if (distance > getCheckoutDistance()) {
                            setCheckinsession(false);
                            CallCheckoutIntentservice();
                            Intent intent = new Intent(context, LandingActivity.class);
                            context.startActivity(intent);
                            for (int i = 0; i < activityArrayList.size() - 1; i++) {
                                try {
                                    activityArrayList.get(i).finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }//end of if
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastMessage("" + e.getMessage());
                    }


                }//end of if
            }//end of else
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *function to Heartbeat Pushnotification
    */
    public static void sendHeartbeat() {
        try {
            appsingleton.context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
            appsingleton.context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
    *Get Location distance
    */
    public double getDistance(double lat1, double long1, double lat2, double long2) {
        try {
            if (lat2 == 0 || long2 == 0)
                return 0;
//           Location locationA = new Location("point A");
//           locationA.setLatitude(lat1);
//           locationA.setLongitude(long1);
//           Location locationB = new Location("point B");
//           locationB.setLatitude(lat2);
//           locationB.setLongitude(long2);
//           return Math.round(locationA.distanceTo(locationB)) ;
            LatLng latLng1 = new LatLng(lat1, long1);
            LatLng latLng2 = new LatLng(lat2, long2);
            double l = (SphericalUtil.computeDistanceBetween(latLng1, latLng2));
            return (SphericalUtil.computeDistanceBetween(latLng1, latLng2));
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
            return 0;
        }
    }

    /*
      *Function to call Checkout
      */
    public void CallCheckoutIntentservice() {
        try {
            Intent msgIntent = new Intent(context, CheckoutIntentService.class);
            msgIntent.putExtra(CheckoutIntentService.REQUEST_STRING, "" + getUserid());
            context.startService(msgIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *function to formate date
    */
    public String formate_Date(String dateinput) {
        String date = "";
        try {
            if(dateinput!=null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date newDate = format.parse(dateinput);
                format = new SimpleDateFormat("hh:mm a");
                date = format.format(newDate);

                //date = DateFormat.format("hh:mm a", Long.parseLong(dateinput)).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            date = "";

        }
        return date;
    }
    /*
       *function to compare dates
       */
    public boolean checkdates(String dateinput1,String dateinput2) {
        try{
            String date1 = "",date2="";
                if(dateinput1!=null&&dateinput2!=null) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date newDate1 = format.parse(dateinput1);
                    Date newDate2 = format.parse(dateinput2);
                    format = new SimpleDateFormat("yyyy-MM-dd");
                    date1 = format.format(newDate1);
                    date2 = format.format(newDate2);

                   if(date1.equalsIgnoreCase(date2))
                   {
                   return true;
                   }
                    else
                       return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastMessage("" + e.getMessage());
            }
            return false;
        }
    /*
       *function to formate header date date
       */
    public String formate_Header_Date(String dateinput) {
        String date = "";
        try {
            if(dateinput!=null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date newDate = format.parse(dateinput);
                format = new SimpleDateFormat("EEEE,MMMM dd");
                date = format.format(newDate);

                //date = DateFormat.format("hh:mm a", Long.parseLong(dateinput)).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
            date = "";

        }
        return date;
    }

    /*
    *open url intent
    */
    public void openURL(Context context,String url)
    {
        try{
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        }catch(Exception e){
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }

    }

    /*
    *open Call phone intent
    */
    public void callNumber(Context context,String number)
    {
        try{
            if(needPermissionCheck())
                if(phoneCallPermission(context)) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    context.startActivity(intent);
                }
        }catch(Exception e){
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }

    }
    public  boolean isLocationEnabled(Context context) {
        try{
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
        }catch(Exception e){
            e.printStackTrace();
            ToastMessage("" + e.getMessage());
        }
        return false;
    }

    /*
   *Subscribe Cometchat Users
   */
    public void CometChatSubscribe()
    {
        try{
            //**-----------------------------------------------------Call commechat SubscribeCallbacks
            CometChat.setDevelopmentMode(false);

            final SubscribeCallbacks subCallbacks = new SubscribeCallbacks() {

                @Override
                public void onMessageReceived(JSONObject receivedMessage) {
                    LogsActivity.addToLog("One-On-One onMessageReceived");
                    Log.e("abc","msg "+receivedMessage);
                    try {
                        String messagetype = receivedMessage.getString("message_type");
                        Intent intent = new Intent();
                        intent.setAction("NEW_SINGLE_MESSAGE");
                        boolean imageMessage = false, videomessage = false, ismyMessage = false;
                        if (messagetype.equals("12")) {
                            intent.putExtra("imageMessage", "1");
                            imageMessage = true;
                            if (receivedMessage.getString("self").equals("1")) {
                                intent.putExtra("myphoto", "1");
                                ismyMessage = true;
                            }
                        } else if (messagetype.equals("14")) {
                            intent.putExtra("videoMessage", "1");
                            videomessage = true;
                            if (receivedMessage.getString("self").equals("1")) {
                                intent.putExtra("myVideo", "1");
                                ismyMessage = true;
                            }
                        }
                        intent.putExtra("message_type", messagetype);
                        intent.putExtra("user_id", receivedMessage.getInt("from"));
                        intent.putExtra("message", receivedMessage.getString("message").trim());
                        intent.putExtra("time", receivedMessage.getString("sent"));
                        intent.putExtra("message_id", receivedMessage.getString("id"));
                        intent.putExtra("from", receivedMessage.getString("from"));
                        String to = null;
                        if (receivedMessage.has("to")) {
                            to = receivedMessage.getString("to");
                        } else {
                            to = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId);
                        }
                        intent.putExtra("to", to);
                        String time = Utils.convertTimestampToDate(Utils.correctTimestamp(Long.parseLong(receivedMessage
                                .getString("sent"))));
                        Date date = new Date();
                        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                        time = dateFormat.format(date);

                        SingleChatMessage newMessage = new SingleChatMessage(receivedMessage.getString("id"),
                                receivedMessage.getString("message").trim(), time, ismyMessage?"1":"0",
                                receivedMessage.getString("from"), to, messagetype, 0);
                        appsingleton.chatdbhelper.insertOneOnOneMessage(newMessage);
                        context.sendBroadcast(intent);
                        try{
                            if(appsingleton.getCheckinsession())
                            {
                                appsingleton.getZaptabhostcontext().updateMessageCount();
                            }
                            else
                            {
                                if(appsingleton.getUserid()>0)
                                {
                                    appsingleton.getTabhostcontext().updateMessageCount();
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }//end of on messagereceive

                @Override
                public void onError(JSONObject errorResponse) {
                    LogsActivity.addToLog("One-On-One onError");
                    Logger.debug("Some error: " + errorResponse);
                }

                @Override
                public void gotProfileInfo(JSONObject profileInfo) {
                    LogsActivity.addToLog("One-On-One gotProfileInfo");
                    Logger.error("profile infor " + profileInfo);
                    appsingleton.getCometchatObject().getPluginInfo(new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            Log.d("abc", "PLugin infor =" + response);
                        }

                        @Override
                        public void failCallback(JSONObject response) {

                        }
                    });
                    JSONObject j = profileInfo;
                    try {
                        myId = j.getString("id");
                        SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.myId, myId);
                        if (j.has("push_channel")) {
                            PushNotificationsManager.subscribe(j.getString("push_channel"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


				/*
				 * cometchat.getOnlineUsers(new Callbacks() {
				 *
				 * @Override public void successCallback(JSONObject response) {
				 * Logger.debug("online users =" + response.toString());
				 *
				 * }
				 *
				 * @Override public void failCallback(JSONObject response) {
				 *
				 * } });
				 */
                }

                @Override
                public void gotOnlineList(JSONObject onlineUsers) {
                    try {

                        LogsActivity.addToLog("One-On-One gotOnlineList");
					/* Store the list for later use. */
                        SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USERS_LIST, onlineUsers.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void gotAnnouncement(JSONObject announcement) {

                }

                @Override
                public void onActionMessageReceived(JSONObject response) {
                    try {
                        String action = response.getString("action");
                        Intent i = new Intent("NEW_SINGLE_MESSAGE");
                        if (action.equals("typing_start")) {
                            i.putExtra("action", "typing_start");
                        } else if (action.equals("typing_stop")) {
                            i.putExtra("action", "typing_stop");
                        } else if (action.equals("message_read")) {
                            i.putExtra("action", "message_read");
                            i.putExtra("from", response.getString("from"));
                            i.putExtra("message_id", response.getString("message_id"));
                            Utils.msgtoTickList.put(response.getString("message_id"), Keys.MessageTicks.read);
                        } else if (action.equals("message_deliverd")) {
                            i.putExtra("action", "message_deliverd");
                            i.putExtra("from", response.getString("from"));
                            i.putExtra("message_id", response.getString("message_id"));
                            Utils.msgtoTickList.put(response.getString("message_id"), Keys.MessageTicks.deliverd);
                        }
                        context.sendBroadcast(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };//end of SubscribeCallbacks-----------------


            if (CometChat.isLoggedIn()) {
                appsingleton.getCometchatObject().subscribe(true, subCallbacks);
            }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }//end


}//end of class
