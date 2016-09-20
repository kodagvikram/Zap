package com.zaparound;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.IntentServices.CheckoutIntentService;
import com.zaparound.IntentServices.FrindListIntentService;
import com.zaparound.IntentServices.InMyLocationIntentService;
import com.zaparound.ModelVo.UserProfileDetailVO;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.helper.Keys;
import com.zaparound.helper.SharedPreferenceHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    public ImageView iv_logoimage, iv_usericon, iv_passwordicon, iv_apptext;
    public LinearLayout ll_bottombtnlayout, ll_createaccount;
    public TextView tv_logotitle, tv_forgotpass, tv_newhere, tv_createaccount, tv_validationtext;
    public Button bt_login;
    EditText et_username, et_password;
    RelativeLayout rl_validation;
    public String gcm_registration_id="";

    @Override
    protected void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(this);

        }catch (Exception e){e.printStackTrace();}
        try {
            appsingleton.locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1200000, 0,
                    appsingleton.networkLocationListener);
            appsingleton. locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0, appsingleton.gpsLocationListener);
        }catch (Exception e){e.printStackTrace();}
    }
    @Override
    protected void onPause() {
        super.onPause();
        try{
            appsingleton.locationManager.removeUpdates(appsingleton.networkLocationListener);
            appsingleton. locationManager.removeUpdates(appsingleton.gpsLocationListener);
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            appsingleton = Appsingleton.getinstance(this);
            setTheme(R.style.LandingTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);
            //initialize views
            iv_logoimage = (ImageView) findViewById(R.id.iv_LOGOIMAGE);
            tv_logotitle = (TextView) findViewById(R.id.tv_logintitle);
            ll_bottombtnlayout = (LinearLayout) findViewById(R.id.ll_Buttonlayout);
            ll_createaccount = (LinearLayout) findViewById(R.id.ll_Bottomlayout);
            et_username = (EditText) findViewById(R.id.et_username);
            et_password = (EditText) findViewById(R.id.et_password);
            tv_forgotpass = (TextView) findViewById(R.id.tv_forgotpassword);
            tv_newhere = (TextView) findViewById(R.id.tv_new);
            tv_createaccount = (TextView) findViewById(R.id.tv_createaccount);
            bt_login = (Button) findViewById(R.id.bt_Loginbtn);
            iv_usericon = (ImageView) findViewById(R.id.iv_userimage);
            iv_passwordicon = (ImageView) findViewById(R.id.iv_password);
            tv_validationtext = (TextView) findViewById(R.id.tv_validation);
            rl_validation = (RelativeLayout) findViewById(R.id.rl_validation);
            iv_apptext = (ImageView) findViewById(R.id.iv_logintitle);
            /*set custom fonts*/
            tv_logotitle.setTypeface(appsingleton.boldtype);
            et_username.setTypeface(appsingleton.regulartype);
            et_password.setTypeface(appsingleton.regulartype);
            tv_forgotpass.setTypeface(appsingleton.lighttype);
            tv_newhere.setTypeface(appsingleton.lighttype);
            tv_createaccount.setTypeface(appsingleton.boldtype);
            bt_login.setTypeface(appsingleton.boldtype);
            tv_validationtext.setTypeface(appsingleton.regulartype);

            iv_logoimage.requestFocus();
            tv_createaccount.setText(tv_createaccount.getText().toString());
             /*setheight width Programatically*/
            SetHeightWidth(iv_logoimage, 0.37, 0.22);
            SetHeightWidth(iv_apptext, 0.50, 0.13);

            /*Set Margins*/
            setMargins(ll_bottombtnlayout, 0.07, 0, 0.07, 0);
            setMargins(iv_logoimage, 0, 0.04, 0, 0);
            setMargins(ll_createaccount, 0.02, 0.02, 0.07, 0);

            //focus change listoner
            et_username.setOnFocusChangeListener(this);
            et_password.setOnFocusChangeListener(this);
            et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        Loginuser(new View(LoginActivity.this));
                    }
                    return false;
                }
            });

            if(appsingleton.needPermissionCheck())
                if(appsingleton.externalreadPermission(this)) ;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of oncreate

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            switch (v.getId()) {
                case R.id.et_username:
                    if (hasFocus) {
                        iv_usericon.setImageResource(R.drawable.username_icon_active);
                        et_username.setHintTextColor(getResources().getColor(R.color.white));
                    } else {
                        if (et_username.getText().toString().trim().isEmpty()) {
                            iv_usericon.setImageResource(R.drawable.username_icon_inactive);
                            et_username.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
                        }
                    }
                    break;
                case R.id.et_password:
                    if (hasFocus) {
                        iv_passwordicon.setImageResource(R.drawable.password_icon_active);
                        et_password.setHintTextColor(getResources().getColor(R.color.white));
                    } else {
                        if (et_password.getText().toString().trim().isEmpty()) {
                            iv_passwordicon.setImageResource(R.drawable.password_icon_inactive);
                            et_password.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
                        }
                    }
                    break;

            }//end of switch

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//end of focus changed

    /* Function to Login
    * */
    public void Loginuser(View view) {
        try {
            if (appsingleton.isEmpty(et_username)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorusernamehint));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (appsingleton.isEmpty(et_password)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorpasswirdhint));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }

                rl_validation.setVisibility(View.GONE);

            String username = et_username.getText().toString();
            String password=et_password.getText().toString();
            if (AppUtils.isNetworkAvailable(this)) {

                if(appsingleton.isLocationEnabled(LoginActivity.this))
                    new UserLogin(username,password).execute();
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            this);
                    builder.setTitle(this.getResources().getString(R.string.app_name));
                    builder.setMessage(getResources().getString(R.string.st_gpsoffmess))
                            .setCancelable(true)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            final int LOCATION_SETTINGS = 4;
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, LOCATION_SETTINGS);

                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }//end of network if
            else
            {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",et_username);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    *Login Click
    */
    public void Clickview(View view) {
        try {
            if (view.getId() == tv_forgotpass.getId()) {
                Intent intent = new Intent(this, SendVerificationCodeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.us_up_from_bottom, R.anim.stay_as_it);
            } else if (view.getId() == ll_createaccount.getId()) {
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
            final float scale = this.getResources().getDisplayMetrics().density;
            int lpx = (int) ((appsingleton.devicewidth) * l);
            int tpx = (int) ((appsingleton.deviceheight) * t);
            int rpx = (int) ((appsingleton.devicewidth) * r);
            int bpx = (int) ((appsingleton.deviceheight) * b);
            p.setMargins(lpx, tpx, rpx, bpx);
            v.requestLayout();
        }
    }//end of margins

    /*
     *Show validation message
     */
    public void ShowValidation(String msg) {
        try {
            tv_validationtext.setText(msg);
            rl_validation.setVisibility(View.VISIBLE);
            tv_validationtext.startAnimation(appsingleton.shake_animation);
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
   *Hide validation message
   */
    public void HideValidation() {
        try {
            rl_validation.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /**
     * Login  web service
     *
     * @author user
     */
    public class UserLogin extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getUserLoginUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String username = "";
        String password = "";

        public UserLogin(String username, String password) {
            try {
                this.username = username;
                this.password = password;

            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(LoginActivity.this);
                appsingleton.showDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public String doInBackground(String... params) {
            try {
                HttpPost request = new HttpPost(URL);
                // request.setHeader("Accept", "application/json");
                // request.setHeader("content-type", "application/json");
                HttpClient client;

                HttpParams parameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(parameters, 60000);
                HttpConnectionParams.setSoTimeout(parameters, 60000);
                client = new DefaultHttpClient(parameters);

                JSONStringer item = new JSONStringer()
                        .object()
                        .key("username").value(username)
                        .key("password").value(appsingleton.md5(password))
                        .key("lat").value("0")
                        .key("long").value("0")
                        .key("device_platform").value("1")
                        .key("fcm_registration_id").value(appsingleton.getFCMtoken())
                        .endObject();

                List param = new ArrayList();
                param.add(new BasicNameValuePair("key", postdataKey));
                param.add(new BasicNameValuePair("json_data", item.toString()));

                request.setEntity(new UrlEncodedFormEntity(param));

                HttpResponse response = client.execute(request);
                publishProgress("Connected...");

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    publishProgress("Loading...");
                    String result = EntityUtils.toString(response.getEntity());
                    return result;
                }
                appsingleton.ToastMessage("" + statusCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(String... values) {
        }

        @Override
        public void onPostExecute(String result) {
            try {
                appsingleton.ToastMessage(result);
                if (result != null) {
                    final JSONObject object = new JSONObject(result);
                    String resultCode = object.getString("response");
                    int code = Integer.parseInt(resultCode);
                    switch (code) {
                        case 101:
                            try {
                                try {
                                    appsingleton.mDatabase.deleteUserprofileDetails();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    appsingleton.ToastMessage("" + e.getMessage());
                                }

                                try{
//                                    appsingleton.getCometchatObject().login(ConnectionsURL.getCometchatSiteurl(), "anutosh",appsingleton.md5("123456"), new Callbacks() {
//
//                                        @Override
//                                        public void successCallback(JSONObject response) {
//                                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USER_NAME, "anutosh");
//                                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.PASSWORD, appsingleton.md5("123456"));
//
//                                           SaveData(object);
//                                        }
//
//                                        @Override
//                                        public void failCallback(JSONObject response) {
//                                            Logger.debug("fresponse->" + response);
//                                            LogsActivity.addToLog("Login failCallback");
//                                            Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_LONG).show();
//                                            //appsingleton.dismissDialog();
//                                        }
//                                    });

                                    appsingleton.getCometchatObject().login(ConnectionsURL.getCometchatSiteurl(), username,password, new Callbacks() {

                                        @Override
                                        public void successCallback(JSONObject response) {
                                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USER_NAME, username);
                                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.PASSWORD,password);

                                            SaveData(object);
                                        }

                                        @Override
                                        public void failCallback(JSONObject response) {
                                            Logger.debug("fresponse->" + response);
                                            LogsActivity.addToLog("Login failCallback");
                                            Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_LONG).show();
                                            appsingleton.dismissDialog();
                                        }
                                    });
                                }catch(Exception e){
                                    e.printStackTrace();
                                    appsingleton.ToastMessage("" + e.getMessage());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }

                            break;
                        case 102:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_parameter_missing));
                            appsingleton.dismissDialog();
                            break;
                        case 103:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invalid_privatekey));
                            appsingleton.dismissDialog();
                            break;
                        case 104:
                            ShowValidation(getResources().getString(R.string.st_invaliduser));
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduser));
                            appsingleton.dismissDialog();
                            break;
                        case 105:
                            ShowValidation(getResources().getString(R.string.st_invaliduser));
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduser));
                            appsingleton.dismissDialog();
                            break;
                        case 106:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_pagenotfound));
                            appsingleton.dismissDialog();
                            break;
                        case 107:
                            break;
                        case 108:
                            break;
                        case 109:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invalid_verificationcode));
                            appsingleton.dismissDialog();
                            break;
                        default:

                            break;
                    }
                }//end of if
               // appsingleton.dismissDialog();
            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of ValidateUsername Asynctask

    /*
    *Save data
    */
    public void SaveData(JSONObject object)
    {
        try{
            String resultArray = object.getString("result");
            JSONArray jsonArray = new JSONArray(resultArray);
            JSONObject arrayObject = jsonArray.getJSONObject(0);
            UserProfileDetailVO vo = new UserProfileDetailVO();
            vo.setUserid(arrayObject.getInt("user_id"));
            vo.setFirstname(arrayObject.getString("first_name"));
            vo.setLastname(arrayObject.getString("last_name"));
            vo.setEmail(arrayObject.getString("email"));
            vo.setGender(arrayObject.getString("gender"));
            vo.setDob(arrayObject.getString("dob"));
            vo.setMobile(arrayObject.getString("mobile"));
            vo.setUsername(arrayObject.getString("username"));
            vo.setIntrest(arrayObject.getString("interest"));
            vo.setCountryid("");
            vo.setNotification(arrayObject.getString("notification"));
            vo.setShow_age(arrayObject.getString("show_age"));
            vo.setIsonline(arrayObject.getInt("isonline"));
            long count = appsingleton.mDatabase.addUserprofileDetails(vo);

            SharedPreferences.Editor editor = appsingleton.sharedPreferences.edit();
            editor.putInt("ZAPUSER_ID", vo.getUserid());
            editor.apply();

            try{
                //sendNotificationInmylocation("",);
                Intent msgIntent = new Intent(this, FrindListIntentService.class);
                msgIntent.putExtra(FrindListIntentService.USER_ID, "" + appsingleton.getUserid());
                msgIntent.putExtra(FrindListIntentService.LAST_ID, "" + "0");
                startService(msgIntent);

            }catch(Exception e){
                e.printStackTrace();
            }
            appsingleton.dismissDialog();
            Intent intent = new Intent(LoginActivity.this, UserTabhostActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            finish();
            appsingleton.ToastMessage("" + count);


        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }//end of save data

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed

}//end of Activity
