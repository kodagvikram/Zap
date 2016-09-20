package com.zaparound;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
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

import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.ModelVo.InterestVO;
import com.zaparound.Singleton.Appsingleton;

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


public class RegistrationSecondActivity extends ActionBarActivity implements View.OnFocusChangeListener {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    public ImageView iv_usericon, iv_apptext, iv_userlasticon, iv_userprivate, iv_lastprivate, iv_emailicon, iv_emailprivate;
    public LinearLayout ll_bottombtnlayout;
    public TextView tv_logotitle, tv_validationtext;
    public Button bt_next;
    EditText et_userfirstname, et_userlastname, et_email;
    RelativeLayout rl_validation;
    Toolbar toolbar;

    @Override
    protected void onRestart() {
        super.onRestart();
        appsingleton = Appsingleton.getinstance(this);
    }

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
            appsingleton = Appsingleton.getinstance(this);
            setTheme(R.style.LandingTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registration_second);

            //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            appsingleton = Appsingleton.getinstance(this);
            toolbar.setTitle("");
            toolbar.setSubtitle("");
            setSupportActionBar(toolbar);
            // add back arrow to toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            //initialize views
            tv_logotitle = (TextView) findViewById(R.id.tv_logintitle);
            ll_bottombtnlayout = (LinearLayout) findViewById(R.id.ll_Buttonlayout);
            et_userfirstname = (EditText) findViewById(R.id.et_userfirstname);
            et_userlastname = (EditText) findViewById(R.id.et_userlastname);
            et_email = (EditText) findViewById(R.id.et_confirmpassword);
            iv_usericon = (ImageView) findViewById(R.id.iv_userfirstimage);
            iv_userprivate = (ImageView) findViewById(R.id.iv_userfirstprivateicon);
            iv_userlasticon = (ImageView) findViewById(R.id.iv_userlastimage);
            iv_lastprivate = (ImageView) findViewById(R.id.iv_userlastprivateicon);
            iv_emailicon = (ImageView) findViewById(R.id.iv_emailimage);
            iv_emailprivate = (ImageView) findViewById(R.id.iv_emailprivateicon);
            bt_next = (Button) findViewById(R.id.bt_Loginbtn);
            tv_validationtext = (TextView) findViewById(R.id.tv_validation);
            rl_validation = (RelativeLayout) findViewById(R.id.rl_validation);
            iv_apptext = (ImageView) findViewById(R.id.iv_logintitle);


            /*set custom fonts*/
            tv_logotitle.setTypeface(appsingleton.regulartype);
            et_userfirstname.setTypeface(appsingleton.regulartype);
            et_userlastname.setTypeface(appsingleton.regulartype);
            et_email.setTypeface(appsingleton.regulartype);

            bt_next.setTypeface(appsingleton.boldtype);
            tv_validationtext.setTypeface(appsingleton.regulartype);



             /*setheight width Programatically*/
            SetHeightWidth(iv_apptext, 0.80, 0.10);
            /*Set Margins*/
            setMargins(ll_bottombtnlayout, 0.07, 0, 0.07, 0);
            setMargins(bt_next, 0.07, 0.01, 0.07, 0);
            setMargins(iv_apptext, 0, 0.05, 0., 0);

            //focus change listoner
            et_userfirstname.setOnFocusChangeListener(this);
            et_userlastname.setOnFocusChangeListener(this);
            et_email.setOnFocusChangeListener(this);

            et_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        Createaccount(new View(RegistrationSecondActivity.this));
                    }
                    return false;
                }
            });
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
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
                case R.id.et_userfirstname:
                    if (hasFocus) {
                        iv_usericon.setImageResource(R.drawable.username_icon_active);
                        iv_userprivate.setImageResource(R.drawable.password_icon_active);
                        et_userfirstname.setHintTextColor(getResources().getColor(R.color.white));
                        tv_validationtext.setText(getResources().getString(R.string.st_usernamepublic));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } else {
                        if (et_userfirstname.getText().toString().trim().isEmpty()) {
                            iv_usericon.setImageResource(R.drawable.username_icon_inactive);
                            iv_userprivate.setImageResource(R.drawable.password_icon_inactive);
                            et_userfirstname.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
                        }
                    }
                    break;
                case R.id.et_userlastname:
                    if (hasFocus) {
                        iv_userlasticon.setImageResource(R.drawable.password_icon_active);
                        iv_lastprivate.setImageResource(R.drawable.password_icon_active);
                        et_userlastname.setHintTextColor(getResources().getColor(R.color.white));
                        tv_validationtext.setText(getResources().getString(R.string.st_privatefield));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } else {
                        if (et_userlastname.getText().toString().trim().isEmpty()) {
                            iv_userlasticon.setImageResource(R.drawable.password_icon_inactive);
                            iv_lastprivate.setImageResource(R.drawable.password_icon_inactive);
                            et_userlastname.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
                        }
                    }
                    break;
                case R.id.et_confirmpassword:
                    if (hasFocus) {
                        iv_emailicon.setImageResource(R.drawable.password_icon_active);
                        iv_emailprivate.setImageResource(R.drawable.password_icon_active);
                        et_email.setHintTextColor(getResources().getColor(R.color.white));

                        tv_validationtext.setText(getResources().getString(R.string.st_privatefield));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } else {
                        if (et_email.getText().toString().trim().isEmpty()) {
                            iv_emailicon.setImageResource(R.drawable.password_icon_inactive);
                            iv_emailprivate.setImageResource(R.drawable.password_icon_inactive);
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
    public void Createaccount(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            if (appsingleton.isEmpty(et_userfirstname) || appsingleton.isEmpty(et_userlastname)
                    || appsingleton.isEmpty(et_email)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errormandatory));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (!appsingleton.isvalidateLength(et_userfirstname,3)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorusernamelength));
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
            String firstname = et_userfirstname.getText().toString();
            appsingleton.stringArrayList2.clear();
            appsingleton.stringArrayList2.add(firstname);
            String password=et_userlastname.getText().toString();
            appsingleton.stringArrayList2.add(password);

            if (AppUtils.isNetworkAvailable(this)) {

                new ValidateUsername(firstname).execute();
            }//end of network if
            else
            {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",et_userfirstname);
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
    public void onBackPressed() {
        try {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed
    /**
     *ValidateUsername web service
     *
     * @author user
     */
    public class ValidateUsername extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getUsernameValidationUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String username = "";
        public ValidateUsername(String username) {
            this.username = username;
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(RegistrationSecondActivity.this);
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
                        .key("username").value(username)
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
                    JSONObject object = new JSONObject(result);
                    String resultCode = object.getString("response");
                    int code = Integer.parseInt(resultCode);
                    switch (code) {
                        case 101:
                            int username = 1;
                            try{
                                username = object.getInt("username");
                                appsingleton.intrestlist.clear();
                                try{
                                    appsingleton.mDatabase.deleteInterestDetails();
                                }catch(Exception e){
                                    e.printStackTrace();
                                    appsingleton.ToastMessage("" + e.getMessage());
                                }
                                String resultArray = object.getString("interests");
                                JSONArray jsonArray = new JSONArray(resultArray);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject arrayObject = jsonArray.getJSONObject(i);
                                    int intrestid = arrayObject.getInt("interest_id");
                                    String intrest = arrayObject.getString("interest_name");
                                    String status = arrayObject.getString("status");
                                    String create_date = arrayObject.getString("create_date");
                                    InterestVO vo=new InterestVO(intrestid,intrest,status,create_date,false);
                                    appsingleton.intrestlist.add(vo);
                                    try{
                                       long count= appsingleton.mDatabase.addInterestDetails(vo);
                                    appsingleton.ToastMessage(""+count);
                                    }catch(Exception e){
                                        e.printStackTrace();
                                        appsingleton.ToastMessage("" + e.getMessage());
                                    }

                                }//end of for

                            }catch(Exception e){
                                username = 1;
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }

                              if (username == 1) {
                                  ShowValidation(getResources().getString(R.string.st_usernameregistered));
                              }
                            else {
                                Intent intent = new Intent(RegistrationSecondActivity.this, RegistrationThirdActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            }
                            break;
                        case 102:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_parameter_missing));
                            break;
                        case 103:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invalid_privatekey));
                            break;
                        case 104:
                            appsingleton.UserToastMessage(getResources().getString(R.string.st_registrationfail));
                            break;
                        case 105:
                            break;
                        case 106:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_pagenotfound));
                            break;
                        case 107:
                            break;
                        case 108:
                            break;
                        case 109:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invalid_verificationcode));
                            break;
                        default:

                            break;
                    }
                }//end of if
                appsingleton.dismissDialog();
            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of ValidateUsername Asynctask
}//end of Activity


