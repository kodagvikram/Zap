package com.zaparound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.gsm.SmsMessage;
import android.util.DisplayMetrics;
import android.util.Log;
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

import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
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
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.List;

public class VerificationActivity extends ActionBarActivity implements View.OnFocusChangeListener {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    public ImageView iv_logoimage, iv_usericon, iv_apptext;
    public LinearLayout ll_bottombtnlayout,ll_resendlayout;
    public TextView tv_logotitle, tv_createaccount, tv_validationtext,tv_resend,tv_mobileno;
    public Button bt_sendcode;
    EditText et_username;
    RelativeLayout rl_validation;
    Toolbar toolbar;
    public String userid="",mobile="",username="";
    BroadcastReceiver smsReceiver;
    @Override
    public void onPause() {
        super.onPause();
        try {
            if(smsReceiver!=null)
            this.unregisterReceiver(smsReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(this);
        }catch (Exception e){e.printStackTrace();}

        try{
                if(appsingleton.readSMSPermission(this)) {
                    IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
                    smsReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            // Retrieves a map of extended data from the intent.
                            final Bundle bundle = intent.getExtras();
                            boolean verify=false;
                            try {
                                if (bundle != null) {
                                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                                    for (int i = 0; i < pdusObj.length; i++) {
                                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                                        String message = currentMessage.getDisplayMessageBody();

                                        String[] inputSplitNewLine = message.split("\\n");
                                        for (int j = 0; j < inputSplitNewLine.length; j++) {
                                            System.out.println(inputSplitNewLine[j]);
                                            if (inputSplitNewLine[j].contains("Your Zaparound verification code is")) {
                                                String verifyCode = inputSplitNewLine[j].replaceAll("[^0-9]", "");
                                                et_username.setText(verifyCode);
                                                verify=true;
                                                //Verify(new View(VerificationActivity.this));
                                                break;
                                            }
                                        }//end of for
                                    }//end of for
                                    if(verify)
                                    Verify(new View(VerificationActivity.this));
                                }
                            } catch (Exception e) {
                                Log.e("SmsReceiver", "Exception smsReceiver" + e);
                            }
                        }
                    };

                    this.registerReceiver(smsReceiver, filter);
                }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            setTheme(R.style.LandingTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_verification);

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
            et_username = (EditText) findViewById(R.id.et_username);
            tv_createaccount = (TextView) findViewById(R.id.tv_titlemessage);
            bt_sendcode = (Button) findViewById(R.id.bt_Loginbtn);
            iv_usericon = (ImageView) findViewById(R.id.iv_userimage);
            tv_validationtext = (TextView) findViewById(R.id.tv_validation);
            rl_validation = (RelativeLayout) findViewById(R.id.rl_validation);
            iv_apptext = (ImageView) findViewById(R.id.iv_logintitle);
            tv_resend = (TextView) findViewById(R.id.tv_resend);
            tv_mobileno = (TextView) findViewById(R.id.tv_usernumber);
            ll_resendlayout = (LinearLayout) findViewById(R.id.ll_Bottomlayout);
            /*set custom fonts*/
            tv_logotitle.setTypeface(appsingleton.boldtype);
            et_username.setTypeface(appsingleton.regulartype);
            tv_createaccount.setTypeface(appsingleton.lighttype);
            bt_sendcode.setTypeface(appsingleton.boldtype);
            tv_validationtext.setTypeface(appsingleton.regulartype);
            tv_resend.setTypeface(appsingleton.lighttype);
            tv_mobileno.setTypeface(appsingleton.boldtype);

            iv_logoimage.requestFocus();
             /*setheight width Programatically*/
            SetHeightWidth(iv_logoimage, 0.37, 0.22);
            SetHeightWidth(iv_apptext, 0.50, 0.13);

            /*Set Margins*/
            setMargins(ll_bottombtnlayout, 0.07, 0, 0.07, 0);
            setMargins(iv_logoimage, 0, 0.01, 0, 0);
            setMargins(ll_resendlayout,0.07, 0, 0.07, 0);

            try{
                Bundle bundle=getIntent().getExtras();
                userid=bundle.getString("USERID","0");
                mobile=bundle.getString("MOBILE","");
                username=bundle.getString("USERNAME","");
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
            tv_mobileno.setText(mobile);

            //focus change listoner
            et_username.setOnFocusChangeListener(this);
            et_username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        Verify(new View(VerificationActivity.this));
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

    /*
    *REsend Code
    */
    public void reSend(View view)
    {
        try{
            if (AppUtils.isNetworkAvailable(this)) {
                new ForgotPassword(username).execute();
            }//end of network if
            else
            {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",et_username);
            }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            switch (v.getId()) {
                case R.id.et_username:
                    if (hasFocus) {
                        iv_usericon.setImageResource(R.drawable.key_active_icon);
                        et_username.setHintTextColor(getResources().getColor(R.color.white));
                    } else {
                        if (et_username.getText().toString().trim().isEmpty()) {
                            iv_usericon.setImageResource(R.drawable.key_inactive_icon);
                            et_username.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
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
    public void Verify(View view) {
        try {
            if (!appsingleton.isvalidateLength(et_username,6)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorOtplength));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            else
            {
                rl_validation.setVisibility(View.GONE);
            }

            if (AppUtils.isNetworkAvailable(this)) {
                new  OTPVerification(et_username.getText().toString()).execute();

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
     * Otpverification  web service
     *
     * @author user
     */
    public class OTPVerification extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getOtpVerificatioUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String otp = "";

        public OTPVerification(String otp) {
            try {
                this.otp = otp;

            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(VerificationActivity.this);
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
                        .key("otp").value(otp)
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
                    JSONObject object = new JSONObject(result);
                    String resultCode = object.getString("response");
                    int code = Integer.parseInt(resultCode);
                    switch (code) {
                        case 101:
                            try {
                                String userid=object.getString("user_id");
                                Intent intent = new Intent(VerificationActivity.this, SetPasswordActivity.class);
                                intent.putExtra("USERID",userid);
                                startActivity(intent);
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                finish();
                                appsingleton.ToastMessage("User id="+userid);
                            } catch (Exception e) {
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }

                            break;
                        case 102:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_parameter_missing));
                            break;
                        case 103:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invalid_privatekey));
                            break;
                        case 104:

                            break;
                        case 105:

                            break;
                        case 106:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_pagenotfound));
                            break;
                        case 107:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduseroremail));
                            break;
                        case 108:
                            break;
                        case 109:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invalid_verificationcode));
                            break;
                        case 110:
                            ShowValidation(getResources().getString(R.string.st_ivalidotp));
                            appsingleton.ToastMessage(getResources().getString(R.string.st_ivalidotp));
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

    /**
     * Sendverification  web service
     *
     * @author user
     */
    public class ForgotPassword extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getForgotPasswordUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String username = "";

        public ForgotPassword(String username) {
            try {
                this.username = username;

            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(VerificationActivity.this);
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
                    JSONObject object = new JSONObject(result);
                    String resultCode = object.getString("response");
                    int code = Integer.parseInt(resultCode);
                    switch (code) {
                        case 101:
                            try {
//                                String userid=object.getString("user_id");
//                                String mobile=object.getString("mobile");
//                                Intent intent = new Intent(VerificationActivity.this, VerificationActivity.class);
//                                intent.putExtra("USERID",userid);
//                                intent.putExtra("MOBILE",mobile);
//                                intent.putExtra("USERNAME",username);
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
//                                finish();
                                appsingleton.ToastMessage("User id="+userid);
                            } catch (Exception e) {
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }

                            break;
                        case 102:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_parameter_missing));
                            break;
                        case 103:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invalid_privatekey));
                            break;
                        case 104:

                            break;
                        case 105:

                            break;
                        case 106:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_pagenotfound));
                            break;
                        case 107:
                            ShowValidation(getResources().getString(R.string.st_invaliduseroremail));
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduseroremail));
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

