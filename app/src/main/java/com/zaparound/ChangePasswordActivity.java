package com.zaparound;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class ChangePasswordActivity extends AppCompatActivity {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    Toolbar toolbar;
    public TextView tv_toolbartitle;
    public LinearLayout ll_mainlayout;
    public TextView  tv_validationtext;
    public Button bt_update;
    EditText et_oldpassword, et_newpassword, et_confirmpassword;
    RelativeLayout rl_validation;
    public boolean isoldpass=false,isnewpass=false,isconpass=false;
    ImageView iv_olbpass,iv_newpass,iv_conpass;
    public int userid=0;
    public String gcm_registration_id="";

    @Override
    protected void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
        }catch (Exception e){e.printStackTrace();}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            appsingleton = Appsingleton.getinstance(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_change_password);
            //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);
            appsingleton.activityArrayList.add(this);
            toolbar = (Toolbar) findViewById(R.id.tool_bar1);
            setSupportActionBar(toolbar);
            // add back arrow to toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            tv_toolbartitle = (TextView) toolbar.findViewById(R.id.tv_title);
            tv_toolbartitle.setText(getResources().getString(R.string.st_changepassword));
            tv_toolbartitle.setTypeface(appsingleton.regulartype);
            appsingleton.setMargins(tv_toolbartitle, 0.15, 0, 0, 0);

            //initialize views
            ll_mainlayout = (LinearLayout) findViewById(R.id.ll_MainLayout);
            et_oldpassword = (EditText) findViewById(R.id.et_oldpassword);
            et_newpassword = (EditText) findViewById(R.id.et_newpassword);
            et_confirmpassword = (EditText) findViewById(R.id.et_confirmpasswordword);
            bt_update = (Button) findViewById(R.id.bt_Loginbtn);
            tv_validationtext = (TextView) findViewById(R.id.tv_validation);
            rl_validation = (RelativeLayout) findViewById(R.id.rl_validation);
            iv_olbpass = (ImageView) findViewById(R.id.iv_userfirstprivateicon);
            iv_newpass = (ImageView) findViewById(R.id.iv_newpasswordicon);
            iv_conpass = (ImageView) findViewById(R.id.iv_confirmpasswordicon);


            /*set custom fonts*/
            et_oldpassword.setTypeface(appsingleton.regulartype);
            et_newpassword.setTypeface(appsingleton.regulartype);
            et_confirmpassword.setTypeface(appsingleton.regulartype);
            bt_update.setTypeface(appsingleton.boldtype);
            tv_validationtext.setTypeface(appsingleton.regulartype);
            /*Set Margins*/
            appsingleton.setMargins(ll_mainlayout, 0.07, 0.03, 0.07, 0);

            userid=appsingleton.sharedPreferences.getInt("USER_ID",0);

            //click listeners
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            iv_olbpass.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!isoldpass) {
                        et_oldpassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        isoldpass = true;

                    } else {
                        et_oldpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isoldpass = false;
                    }
                    et_oldpassword.setSelection(et_oldpassword.getText().length());

                }
            });

            iv_newpass.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!isnewpass) {
                        et_newpassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        isnewpass = true;

                    } else {
                        et_newpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isnewpass = false;
                    }
                    et_newpassword.setSelection(et_newpassword.getText().length());
                }
            });
            iv_conpass.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!isconpass) {
                        et_confirmpassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        isconpass = true;

                    } else {
                        et_confirmpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isconpass = false;
                    }
                    et_confirmpassword.setSelection(et_confirmpassword.getText().length());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }//end of oncreate

    /* Function to Update
    */
    public void Updateaccount(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (appsingleton.isEmpty(et_oldpassword) || appsingleton.isEmpty(et_newpassword)
                    || appsingleton.isEmpty(et_confirmpassword)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errormandatory));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (!appsingleton.isvalidaPasswordLength(et_newpassword)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorpasswordlength));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                //et_userlastname.requestFocus();
                return;
            }
            if (!appsingleton.isPasswordMatch(et_newpassword,et_confirmpassword)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorpasswordimssmatch));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                // et_email.requestFocus();
                return;
            }
            else {
                rl_validation.setVisibility(View.GONE);
            }
            //Check internet permission
            if (AppUtils.isNetworkAvailable(this)) {
                new SetPassword(appsingleton.md5(et_oldpassword.getText().toString()),appsingleton.md5(et_newpassword.getText().toString())).execute();
            }//end of network if
            else
            {
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",et_oldpassword);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
                appsingleton.initDialog(ChangePasswordActivity.this);
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
                        .key("password").value(newpassword)
                        .key("old_password").value(oldpassword)
                        .key("type").value("change")
                        .key("device_token").value("")
                        .key("gcm_registration_id").value(gcm_registration_id)
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
                                onBackPressed();
                                appsingleton.UserToastMessage(getResources().getString(R.string.st_successfullyupdated));
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
                            //ShowValidation(getResources().getString(R.string.st_ivalidotp));
                            appsingleton.ToastMessage(getResources().getString(R.string.st_ivalidotp));
                            break;
                        case 111:
                            ShowValidation(getResources().getString(R.string.st_failstatus));
                            appsingleton.ToastMessage(getResources().getString(R.string.st_failstatus));
                            break;
                        case 112:
                            ShowValidation(getResources().getString(R.string.st_failtoupdatepassword));
                            appsingleton.UserToastMessage(getResources().getString(R.string.st_failtoupdatepassword));
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
    }//end of SetPassword Asynctask
}//end of activity
