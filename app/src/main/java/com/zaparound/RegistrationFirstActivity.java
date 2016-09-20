package com.zaparound;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.List;

public class RegistrationFirstActivity extends ActionBarActivity implements View.OnFocusChangeListener {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    public ImageView iv_usericon, iv_apptext, iv_userlasticon, iv_userprivate, iv_lastprivate, iv_emailicon, iv_emailprivate,
            iv_gendericon, iv_genderprivate, iv_dateofbirthicon, iv_dateofbirthprivate, iv_mobileicon, iv_mobileprivate;
    public LinearLayout ll_bottombtnlayout;
    public TextView tv_logotitle, tv_validationtext;
    public Button bt_next;
    EditText et_userfirstname, et_userlastname, et_email, et_gender, et_dateofbirth, et_mobile;
    RelativeLayout rl_validation;
    Toolbar toolbar;
    private Calendar calendar;
    private int year, month, day;
    Animation zoom_in_animation;

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
            setContentView(R.layout.activity_registration_first);

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
            et_email = (EditText) findViewById(R.id.et_email);
            et_gender = (EditText) findViewById(R.id.et_gender);
            et_dateofbirth = (EditText) findViewById(R.id.et_dateofbirth);
            et_mobile = (EditText) findViewById(R.id.et_mobile);

            iv_usericon = (ImageView) findViewById(R.id.iv_userfirstimage);
            iv_userprivate = (ImageView) findViewById(R.id.iv_userfirstprivateicon);
            iv_userlasticon = (ImageView) findViewById(R.id.iv_userlastimage);
            iv_lastprivate = (ImageView) findViewById(R.id.iv_userlastprivateicon);
            iv_emailicon = (ImageView) findViewById(R.id.iv_emailimage);
            iv_emailprivate = (ImageView) findViewById(R.id.iv_emailprivateicon);
            iv_gendericon = (ImageView) findViewById(R.id.iv_genderimage);
            iv_genderprivate = (ImageView) findViewById(R.id.iv_genderprivateicon);
            iv_dateofbirthicon = (ImageView) findViewById(R.id.iv_dateofbirthimage);
            iv_dateofbirthprivate = (ImageView) findViewById(R.id.iv_dateofbirthprivateicon);
            iv_mobileicon = (ImageView) findViewById(R.id.iv_mobileimage);
            iv_mobileprivate = (ImageView) findViewById(R.id.iv_mobileprivateicon);
            bt_next = (Button) findViewById(R.id.bt_Loginbtn);
            tv_validationtext = (TextView) findViewById(R.id.tv_validation);
            rl_validation = (RelativeLayout) findViewById(R.id.rl_validation);
            iv_apptext = (ImageView) findViewById(R.id.iv_logintitle);


            /*set custom fonts*/
            tv_logotitle.setTypeface(appsingleton.regulartype);
            et_userfirstname.setTypeface(appsingleton.regulartype);
            et_userlastname.setTypeface(appsingleton.regulartype);
            et_email.setTypeface(appsingleton.regulartype);
            et_gender.setTypeface(appsingleton.regulartype);
            et_dateofbirth.setTypeface(appsingleton.regulartype);
            et_mobile.setTypeface(appsingleton.regulartype);
            bt_next.setTypeface(appsingleton.boldtype);
            tv_validationtext.setTypeface(appsingleton.regulartype);

            //set aninmation
            zoom_in_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin_checkout);

             /*setheight width Programatically*/
            SetHeightWidth(iv_apptext, 0.80, 0.10);
            /*Set Margins*/
            setMargins(ll_bottombtnlayout, 0.07, 0, 0.07, 0);
            //setMargins(iv_apptext, 0.20, 0, 0.20, 0);

            //focus change listoner
            et_userfirstname.setOnFocusChangeListener(this);
            et_userlastname.setOnFocusChangeListener(this);
            et_email.setOnFocusChangeListener(this);
            et_gender.setOnFocusChangeListener(this);
            et_dateofbirth.setOnFocusChangeListener(this);
            et_mobile.setOnFocusChangeListener(this);

            et_gender.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    try {

                        iv_gendericon.setImageResource(R.drawable.gender_icon_active);
                        iv_genderprivate.setImageResource(R.drawable.white_password_icon_active);
                        et_gender.setHintTextColor(getResources().getColor(R.color.white));

                        tv_validationtext.setText(getResources().getString(R.string.st_publicfield));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
            et_mobile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        Createaccount(new View(RegistrationFirstActivity.this));
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
                        tv_validationtext.setText(getResources().getString(R.string.st_privatefield));
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
                        iv_userlasticon.setImageResource(R.drawable.username_icon_active);
                        iv_lastprivate.setImageResource(R.drawable.password_icon_active);
                        et_userlastname.setHintTextColor(getResources().getColor(R.color.white));
                        tv_validationtext.setText(getResources().getString(R.string.st_privatefield));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } else {
                        if (et_userlastname.getText().toString().trim().isEmpty()) {
                            iv_userlasticon.setImageResource(R.drawable.username_icon_inactive);
                            iv_lastprivate.setImageResource(R.drawable.password_icon_inactive);
                            et_userlastname.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
                        }
                    }
                    break;
                case R.id.et_email:
                    if (hasFocus) {
                        iv_emailicon.setImageResource(R.drawable.email_icon_active);
                        iv_emailprivate.setImageResource(R.drawable.password_icon_active);
                        et_email.setHintTextColor(getResources().getColor(R.color.white));

                        tv_validationtext.setText(getResources().getString(R.string.st_privatefield));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } else {
                        if (et_email.getText().toString().trim().isEmpty()) {
                            iv_emailicon.setImageResource(R.drawable.email_icon_inactive);
                            iv_emailprivate.setImageResource(R.drawable.password_icon_inactive);
                            et_email.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
                        }
                    }
                    break;
                case R.id.et_gender:
                    if (hasFocus) {
                        iv_gendericon.setImageResource(R.drawable.gender_icon_active);
                        iv_genderprivate.setImageResource(R.drawable.white_password_icon_active);
                        et_gender.setHintTextColor(getResources().getColor(R.color.white));

                        tv_validationtext.setText(getResources().getString(R.string.st_publicfield));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } else {
                        if (et_gender.getText().toString().trim().isEmpty()) {
                            iv_gendericon.setImageResource(R.drawable.gender_icon_inactive);
                            iv_genderprivate.setImageResource(R.drawable.white_password_icon_inactive);
                            et_gender.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
                        }
                    }
                    break;
                case R.id.et_dateofbirth:
                    if (hasFocus) {
                        iv_dateofbirthicon.setImageResource(R.drawable.birthday_icon_active);
                        iv_dateofbirthprivate.setImageResource(R.drawable.white_password_icon_active);
                        et_dateofbirth.setHintTextColor(getResources().getColor(R.color.white));

                        tv_validationtext.setText(getResources().getString(R.string.st_publicfield));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } else {
                        if (et_dateofbirth.getText().toString().trim().isEmpty()) {
                            iv_dateofbirthicon.setImageResource(R.drawable.birthday_icon_inactive);
                            iv_dateofbirthprivate.setImageResource(R.drawable.white_password_icon_inactive);
                            et_dateofbirth.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
                        }
                    }
                    break;
                case R.id.et_mobile:
                    if (hasFocus) {
                        iv_mobileicon.setImageResource(R.drawable.mobile_icon_active);
                        iv_mobileprivate.setImageResource(R.drawable.password_icon_active);
                        et_mobile.setHintTextColor(getResources().getColor(R.color.white));

                        tv_validationtext.setText(getResources().getString(R.string.st_privatefield));
                        rl_validation.setVisibility(View.VISIBLE);
                        tv_validationtext.startAnimation(appsingleton.shake_animation);
                    } else {
                        if (et_mobile.getText().toString().trim().isEmpty()) {
                            iv_mobileicon.setImageResource(R.drawable.mobile_icon_inactive);
                            iv_mobileprivate.setImageResource(R.drawable.password_icon_inactive);
                            et_mobile.setHintTextColor(getResources().getColor(R.color.edittexthint_color));
                        }
                    }
                    break;
            }//end of switch

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//end of focus changed

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

    /* Function to Login
   * */
    public void Createaccount(View view) {

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (appsingleton.isEmpty(et_userfirstname) || appsingleton.isEmpty(et_userlastname)
                    || appsingleton.isEmpty(et_email) || appsingleton.isEmpty(et_gender)
                    /*|| appsingleton.isEmpty(et_dateofbirth)*/ || appsingleton.isEmpty(et_mobile)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errormandatory));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (!appsingleton.isvalidateLength(et_userfirstname,3)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorfirstnamelength));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (!appsingleton.isvalidateLength(et_userlastname,3)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorLastnamelength));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (appsingleton.isInValidpersonname(et_userfirstname)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorpersonname));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (appsingleton.isInValidpersonname(et_userlastname)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorpersonLastname));
                rl_validation.setVisibility(View.VISIBLE);

                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (!appsingleton.isvalidateEmail(et_email)) {
                tv_validationtext.setText(getResources().getString(R.string.st_erroremail));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (!appsingleton.isvalidatemobile(et_mobile)) {
                tv_validationtext.setText(getResources().getString(R.string.st_errormobileno));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            } else {
                rl_validation.setVisibility(View.GONE);
            }
            String firstname = et_userfirstname.getText().toString();
            String lastname = (et_userlastname.getText().toString());
            String email = et_email.getText().toString();
            String gender = (et_gender.getText().toString());
            //String dateofbirth = et_dateofbirth.getText().toString();
            String dateofbirth = "";
            String mobile = (et_mobile.getText().toString());
            appsingleton.stringArrayList.clear();
            appsingleton.stringArrayList.add(firstname);
            appsingleton.stringArrayList.add(lastname);
            appsingleton.stringArrayList.add(email);
            appsingleton.stringArrayList.add(gender);
            appsingleton.stringArrayList.add(dateofbirth);
            appsingleton.stringArrayList.add(mobile);
            if (AppUtils.isNetworkAvailable(this)) {
                if(appsingleton.isLocationEnabled(RegistrationFirstActivity.this))
                new ValidateMobileEmail(mobile, email).execute();
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
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",et_userfirstname);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pop up to choose user gender
     */
    public void selectGender(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            final CharSequence[] options = {getResources().getString(R.string.st_malegende),
                    getResources().getString(R.string.st_femalegender)};

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);

            builder.setTitle("");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    try {
                        et_gender.setText(options[item]);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            });
            builder.setCancelable(true);
            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        try {
            et_dateofbirth.setText(new StringBuilder().append(year).append("-")
                    .append(month).append("-").append(day));
            iv_dateofbirthicon.setImageResource(R.drawable.birthday_icon_active);
            iv_dateofbirthprivate.setImageResource(R.drawable.white_password_icon_active);
            et_dateofbirth.setHintTextColor(getResources().getColor(R.color.white));

            tv_validationtext.setText(getResources().getString(R.string.st_publicfield));
            rl_validation.setVisibility(View.VISIBLE);
            tv_validationtext.startAnimation(appsingleton.shake_animation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DATETIMEPICKER(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            setDate(view);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }// end of date time picker

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
     * email mobile validation web service
     *
     * @author user
     */
    public class ValidateMobileEmail extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getEmailValidationUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        String mobileno = "", emailid = "";

        public ValidateMobileEmail(String mobileno, String emailid) {
            this.mobileno = mobileno;
            this.emailid = emailid;
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(RegistrationFirstActivity.this);
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
                        .key("email").value(emailid)
                        .key("mobile").value(mobileno)
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
                            int mobile = 1;
                            int email = 1;
                            mobile = object.getInt("mobile");
                            email = object.getInt("email");

                            if (mobile == 1 && email == 1) {
                                ShowValidation(getResources().getString(R.string.st_emailmobileregistered));
                            } else if (email == 1) {
                                ShowValidation(getResources().getString(R.string.st_emailregistered));
                            } else if (mobile == 1) {
                                ShowValidation(getResources().getString(R.string.st_mobileregistered));
                            } else {
                                Intent intent = new Intent(RegistrationFirstActivity.this, RegistrationSecondActivity.class);
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
    }//end of Login Asynctask


}//end of Activity


