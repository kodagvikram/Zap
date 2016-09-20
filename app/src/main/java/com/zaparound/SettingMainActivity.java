package com.zaparound;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.IntentServices.CheckoutIntentService;
import com.zaparound.IntentServices.ZapWhosIntentService;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.helper.Keys;
import com.zaparound.helper.SharedPreferenceHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONStringer;

public class SettingMainActivity extends AppCompatActivity implements View.OnClickListener{
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    Toolbar toolbar;
    RelativeLayout rl_setting;
    public TextView tv_toolbartitle;
    public RelativeLayout rl_myprofilelayout, rl_invitefriendslayout,
            rl_appsettinglayout, rl_leagalpolicylayout, rl_supportlayout, rl_logoutlayout;
    public TextView tv_myprofile, tv_invitefrinds, tv_appsetting, tv_leagalpolicy, tv_support, tv_logout;

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
            setContentView(R.layout.activity_setting_mail);

            //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);
            toolbar = (Toolbar) findViewById(R.id.tool_bar1);
            setSupportActionBar(toolbar);
            // add back arrow to toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            rl_setting = (RelativeLayout) toolbar.findViewById(R.id.iv_checksetting);
            tv_toolbartitle = (TextView) toolbar.findViewById(R.id.tv_title);
            tv_toolbartitle.setText("Settings");
            tv_toolbartitle.setTypeface(appsingleton.regulartype);
            appsingleton.setMargins(rl_setting, 0.19, 0, 0, 0);

            //initialize views
            rl_myprofilelayout=(RelativeLayout)findViewById(R.id.rl_myprofilelayout);
            rl_invitefriendslayout=(RelativeLayout)findViewById(R.id.rl_invitefrindslayout);
            rl_appsettinglayout=(RelativeLayout)findViewById(R.id.rl_appsettinglayout);
            rl_leagalpolicylayout=(RelativeLayout)findViewById(R.id.rl_legalpolicylayout);
            rl_supportlayout=(RelativeLayout)findViewById(R.id.rl_supportlayout);
            rl_logoutlayout=(RelativeLayout)findViewById(R.id.rl_logoutlayout);
            tv_myprofile=(TextView) findViewById(R.id.tv_myprofile);
            tv_invitefrinds=(TextView) findViewById(R.id.tv_invitefrinds);
            tv_appsetting=(TextView) findViewById(R.id.tv_appsetting);
            tv_leagalpolicy=(TextView) findViewById(R.id.tv_legalpolicy);
            tv_support=(TextView) findViewById(R.id.tv_support);
            tv_logout=(TextView) findViewById(R.id.tv_logout);

            //set custom fonts
            tv_myprofile.setTypeface(appsingleton.regulartype);
            tv_invitefrinds.setTypeface(appsingleton.regulartype);
            tv_appsetting.setTypeface(appsingleton.regulartype);
            tv_leagalpolicy.setTypeface(appsingleton.regulartype);
            tv_support.setTypeface(appsingleton.regulartype);
            tv_logout.setTypeface(appsingleton.regulartype);

            rl_myprofilelayout.setOnClickListener(this);
            rl_invitefriendslayout.setOnClickListener(this);
            rl_appsettinglayout.setOnClickListener(this);
            rl_leagalpolicylayout.setOnClickListener(this);
            rl_supportlayout.setOnClickListener(this);
            rl_logoutlayout.setOnClickListener(this);
            //click listeners
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }//end of oncreate

    @Override
    public void onClick(View v) {
        try{
           switch (v.getId())
           {
               case R.id.rl_myprofilelayout:
                   Intent intent = new Intent(this, MyProfileActivity.class);
                   startActivity(intent);
                   overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                   break;
               case R.id.rl_invitefrindslayout:
               Intent intent2 = new Intent(this, InviteFriendsActivity.class);
               startActivity(intent2);
               overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
               break;
               case R.id.rl_appsettinglayout:
                   Intent intent3 = new Intent(this, AppSettingActivity.class);
                   startActivity(intent3);
                   overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                   break;
               case R.id.rl_legalpolicylayout:
                   Intent intent4 = new Intent(this, LegalPolicyActivity.class);
                   startActivity(intent4);
                   overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                   break;
               case R.id.rl_supportlayout:
                   showDialog();
                   break;
               case R.id.rl_logoutlayout:

                   String title=getResources().getString(R.string.st_logouttext);
                   String message=getResources().getString(R.string.st_logoutsubtext);
                   String leftbtn=getResources().getString(R.string.st_no);
                   String rightbtn=getResources().getString(R.string.st_yes);
                   showLogoutDialog(title,message,leftbtn,rightbtn,R.drawable.clear_icon,0,"clear");

//                   Intent intent5 = new Intent(this, LandingActivity.class);
//                   startActivity(intent5);
//                   overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
//                   finish();
                   break;
           }

        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }

    /*
    * to dosplay dialog
    * */
    public void showDialog() {
        try {
            final Dialog dialog = new Dialog(this);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.support_popuplayout);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView  tv_title;
            final ImageView iv_close;
            Button btn_submit;
           final EditText et_support;
            tv_title = (TextView) dialog.findViewById(R.id.tv_popuptitle);
            iv_close = (ImageView) dialog.findViewById(R.id.iv_Crossimage);
            btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
            et_support = (EditText) dialog.findViewById(R.id.et_supportmessage);

             /*Assining fonts*/
            tv_title.setTypeface(appsingleton.regulartype);
            btn_submit.setTypeface(appsingleton.regulartype);
            et_support.setTypeface(appsingleton.regulartype);


            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!et_support.getText().toString().equals(""))
                    {
                        if (AppUtils.isNetworkAvailable(SettingMainActivity.this)) {
                            new SupportWebservice(et_support.getText().toString()).execute();
                            et_support.setText("");
                            dialog.dismiss();
                        }//end of network if
                        else
                        {
                            appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",iv_close);
                        }

                    }

                }
            });


            dialog.show();
            et_support.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (view.getId() ==R.id.et_supportmessage) {
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (event.getAction()&MotionEvent.ACTION_MASK){
                            case MotionEvent.ACTION_UP:
                                view.getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                        }
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of show dialog

    /*
 * to display logoutDialod dialog
 * */
    public void showLogoutDialog(String title, String message, String btnleft, String btnright, int imageid, final int position, final String operation) {
        try {
            final Dialog dialog = new Dialog(this);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.pop_up_check_out);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tv_msg, tv_title;
           final ImageView iv_close, iv_middle;
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

            try {
                tv_title.setText(title);
                tv_msg.setText( message);
                bt_yes.setText(btnright);
                bt_no.setText(btnleft);
                iv_middle.setImageResource(imageid);
            } catch (Exception e) {
                appsingleton.ToastMessage("" + e.getMessage());
            }
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
                        try{





                            if (AppUtils.isNetworkAvailable(SettingMainActivity.this)) {
                                try{
                                    if(appsingleton.getCheckinsession()) {
                                        Intent msgIntent = new Intent(SettingMainActivity.this, CheckoutIntentService.class);
                                        msgIntent.putExtra(CheckoutIntentService.REQUEST_STRING, "" + appsingleton.getUserid());
                                        startService(msgIntent);
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                    appsingleton.ToastMessage("" + e.getMessage());
                                }
                                appsingleton.setCheckinsession(false);

                                try{
                                    appsingleton.getCometchatObject().logout(new Callbacks() {

                                        @Override
                                        public void successCallback(JSONObject response) {
                                            SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.IS_LOGGEDIN);
                                            SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.USER_NAME);
                                            SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.PASSWORD);
                                            SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.LOGIN_TYPE);
                                            SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.USERS_LIST);
                                            SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.CHATROOMS_LIST);
                                        }

                                        @Override
                                        public void failCallback(JSONObject response) {
                                            Logger.debug("logout failed");
                                        }
                                    });
                                }catch(Exception e){
                                    e.printStackTrace();
                                    appsingleton.ToastMessage("" + e.getMessage());
                                }
                                new Logout().execute();
                            }//end of network if
                            else
                            {
                                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",iv_middle);
                            }


                        }catch(Exception e){
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                    dialog.dismiss();
                }
            });




            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }//end of show dialog
    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed
       /*
        * Logout read web service
        *
        * @author user
        */
    public class Logout extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getLogUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        public Logout() {
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(SettingMainActivity.this);
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
                        .key("user_id").value(appsingleton.getUserid())
                        .endObject();
                /*
                 * Create object of multi part class to upload status and text
                 * data
                 */
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                            }
                        });
                try {
                    // Extra parameters if you want to pass to server
                    entity.addPart("key", new StringBody(postdataKey));
                    entity.addPart("json_data", new StringBody(item.toString()));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                request.setEntity(entity);
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
                                appsingleton.Logout();
                                appsingleton.setCheckinsession(false);
                                Intent intent5 = new Intent(SettingMainActivity.this, LandingActivity.class);
                                startActivity(intent5);
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                finish();
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
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduser));
                            break;
                        case 105:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduser));
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
                        case 111:
                            appsingleton.UserToastMessage(getResources().getString(R.string.st_failstatus));
                            break;
                        case 114:
                            try {
                                appsingleton.Logout();
                                appsingleton.setCheckinsession(false);
                                Intent intent5 = new Intent(SettingMainActivity.this, LandingActivity.class);
                                startActivity(intent5);
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }
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
    }//end of Logout refresh Asynctask
    /*
       * Logout read web service
       *
       * @author user
       */
    public class SupportWebservice extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getSupportUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String message;
        public SupportWebservice(String message) {
            this.message=message;
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(SettingMainActivity.this);
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
                        .key("user_id").value(appsingleton.getUserid())
                        .key("message").value(message)
                        .endObject();
                /*
                 * Create object of multi part class to upload status and text
                 * data
                 */
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                            }
                        });
                try {
                    // Extra parameters if you want to pass to server
                    entity.addPart("key", new StringBody(postdataKey));
                    entity.addPart("json_data", new StringBody(item.toString()));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                request.setEntity(entity);
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
                                appsingleton.ToastMessage("Successfully  updated");
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
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduser));
                            break;
                        case 105:
                            appsingleton.ToastMessage(getResources().getString(R.string.st_invaliduser));
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
                        case 111:
                            appsingleton.UserToastMessage(getResources().getString(R.string.st_failstatus));
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
    }//end of Support refresh Asynctask
}//end of Activity
