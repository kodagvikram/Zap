package com.zaparound;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.zaparound.IntentServices.FrindListIntentService;
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

public class SetPasswordActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    public ImageView iv_logoimage, iv_userlasticon, iv_emailicon,iv_apptext;
    public LinearLayout ll_bottombtnlayout;
    public TextView tv_logotitle, tv_createaccount, tv_validationtext,tv_setnewpass;
    public Button bt_sendcode;
    EditText  et_userlastname, et_email;
    RelativeLayout rl_validation;
    Toolbar toolbar;
    public String userid="";
    public String gcm_registration_id="";

    @Override
    protected void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(this);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            setTheme(R.style.LandingTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_set_password);

            //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            appsingleton = Appsingleton.getinstance(this);
            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            appsingleton=Appsingleton.getinstance(this);
            toolbar.setTitle("");
            toolbar.setSubtitle("");
            setSupportActionBar(toolbar);
            // add back arrow to toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            //initialize views
            iv_logoimage = (ImageView) findViewById(R.id.iv_LOGOIMAGE);
            tv_logotitle = (TextView) findViewById(R.id.tv_logintitle);
            ll_bottombtnlayout = (LinearLayout) findViewById(R.id.ll_Buttonlayout);
            et_userlastname = (EditText) findViewById(R.id.et_userlastname);
            et_email = (EditText) findViewById(R.id.et_confirmpassword);
            tv_createaccount = (TextView) findViewById(R.id.tv_titlemessage);
            bt_sendcode = (Button) findViewById(R.id.bt_Loginbtn);
            iv_userlasticon = (ImageView) findViewById(R.id.iv_userlastimage);
            iv_emailicon = (ImageView) findViewById(R.id.iv_emailimage);
            tv_validationtext = (TextView) findViewById(R.id.tv_validation);
            tv_setnewpass = (TextView) findViewById(R.id.tv_setnewpass);
            rl_validation = (RelativeLayout) findViewById(R.id.rl_validation);
            iv_apptext = (ImageView) findViewById(R.id.iv_logintitle);
            /*set custom fonts*/
            tv_logotitle.setTypeface(appsingleton.boldtype);
            et_userlastname.setTypeface(appsingleton.regulartype);
            et_email.setTypeface(appsingleton.regulartype);
            tv_createaccount.setTypeface(appsingleton.lighttype);
            bt_sendcode.setTypeface(appsingleton.boldtype);
            tv_setnewpass.setTypeface(appsingleton.boldtype);
            tv_validationtext.setTypeface(appsingleton.regulartype);

            iv_logoimage.requestFocus();
             /*setheight width Programatically*/
            SetHeightWidth(iv_logoimage, 0.37, 0.22);
            SetHeightWidth(iv_apptext, 0.50, 0.13);

            /*Set Margins*/
            setMargins(ll_bottombtnlayout, 0.07, 0, 0.07, 0);
            setMargins(iv_logoimage, 0, 0.01, 0, 0);
            try{
                Bundle bundle=getIntent().getExtras();
                userid=bundle.getString("USERID","0");
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
            //focus change listoner
            et_userlastname.setOnFocusChangeListener(this);
            et_email.setOnFocusChangeListener(this);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            et_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        Update(new View(SetPasswordActivity.this));
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of oncreate

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            switch (v.getId()) {

                case R.id.et_userlastname:
                    if (hasFocus) {
                        iv_userlasticon.setImageResource(R.drawable.password_icon_active);
                        et_userlastname.setHintTextColor(getResources().getColor(R.color.white));
                        tv_validationtext.setText(getResources().getString(R.string.st_privatefield));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } else {
                        if (et_userlastname.getText().toString().trim().isEmpty()) {
                            iv_userlasticon.setImageResource(R.drawable.password_icon_inactive);
                            et_userlastname.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
                        }
                    }
                    break;
                case R.id.et_confirmpassword:
                    if (hasFocus) {
                        iv_emailicon.setImageResource(R.drawable.password_icon_active);
                        et_email.setHintTextColor(getResources().getColor(R.color.white));

                        tv_validationtext.setText(getResources().getString(R.string.st_privatefield));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } else {
                        if (et_email.getText().toString().trim().isEmpty()) {
                            iv_emailicon.setImageResource(R.drawable.password_icon_inactive);
                            et_email.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
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
    public void Update(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if ( appsingleton.isEmpty(et_userlastname)
                    || appsingleton.isEmpty(et_email)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errormandatory));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (!appsingleton.isvalidaPasswordLength(et_userlastname)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorpasswordlength));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                //et_userlastname.requestFocus();
                return;
            }
            if (!appsingleton.isPasswordMatch(et_userlastname,et_email)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorpasswordimssmatch));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                // et_email.requestFocus();
                return;
            }
            else {
                rl_validation.setVisibility(View.GONE);
            }

            if (AppUtils.isNetworkAvailable(this)) {
                if(appsingleton.isLocationEnabled(SetPasswordActivity.this))
                    new SetPassword("",et_userlastname.getText().toString()).execute();
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
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",et_userlastname);
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
     * SetPassword web service
     *
     * @author user
     */
    public class SetPassword extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getUpdatePasswordUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String oldpassword = "";
        String newpassword = "";

        public SetPassword(String oldpassword,String newpassword) {
            try {
                this.newpassword = newpassword;
                this.oldpassword = oldpassword;

            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(SetPasswordActivity.this);
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
                HttpConnectionParams.setConnectionTimeout(parameters, 10000);
                HttpConnectionParams.setSoTimeout(parameters, 20000);
                client = new DefaultHttpClient(parameters);

                JSONStringer item = new JSONStringer()
                        .object()
                        .key("user_id").value(userid)
                        .key("password").value(appsingleton.md5(newpassword))
                        .key("old_password").value(appsingleton.md5(oldpassword))
                        .key("type").value("set")
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
                                String resultArray = object.getString("result");
                                JSONArray jsonArray = new JSONArray(resultArray);
                                JSONObject arrayObject = jsonArray.getJSONObject(0);
                              final  UserProfileDetailVO vo = new UserProfileDetailVO();
                                vo.setUsername(arrayObject.getString("username"));

                                try{
                                    appsingleton.getCometchatObject().login(ConnectionsURL.getCometchatSiteurl(), vo.getUsername(),newpassword, new Callbacks() {

                                        @Override
                                        public void successCallback(JSONObject response) {
                                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USER_NAME, vo.getUsername());
                                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.PASSWORD,newpassword);
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

                            break;
                        case 105:

                            break;
                        case 106:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_pagenotfound));
                            appsingleton.dismissDialog();
                            break;
                        case 107:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduseroremail));
                            appsingleton.dismissDialog();
                            break;
                        case 108:
                            break;
                        case 109:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invalid_verificationcode));
                            appsingleton.dismissDialog();
                            break;
                        case 110:
                            ShowValidation(getResources().getString(R.string.st_ivalidotp));
                            appsingleton.ToastMessage(getResources().getString(R.string.st_ivalidotp));
                            appsingleton.dismissDialog();
                            break;
                        case 111:
                            ShowValidation(getResources().getString(R.string.st_failstatus));
                            appsingleton.ToastMessage(getResources().getString(R.string.st_failstatus));
                            appsingleton.dismissDialog();
                            break;
                        default:

                            break;
                    }
                }//end of if
              //  appsingleton.dismissDialog();
            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of SetPassword Asynctask


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
            Intent intent = new Intent(SetPasswordActivity.this, UserTabhostActivity.class);
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
            Intent intent = new Intent(this, SendVerificationCodeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed
}//end of Activity


