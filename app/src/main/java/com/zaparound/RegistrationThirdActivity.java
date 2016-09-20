package com.zaparound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.IntentServices.FrindListIntentService;
import com.zaparound.ModelVo.InterestVO;
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
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RegistrationThirdActivity extends AppCompatActivity {

    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    public ImageView iv_apptext;
    public LinearLayout ll_bottombtnlayout, ll_termslayout;
    public TextView tv_logotitle, tv_validationtext, tv_titlemessage, tv_termstitle;
    public Button bt_next;
    RelativeLayout rl_validation;
    Toolbar toolbar;
    public ToggleButton tb_terms;
    private GridView intrestgridView;
    IntrestAdapter adapter;
    public int count = 0;
    private ArrayList<InterestVO> intrestlist;
    public String gcm_registration_id="";

    @Override
    protected void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(this);
        }catch (Exception e){e.printStackTrace();}
        try{
            callLocationListner();
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
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
            setTheme(R.style.LandingTheme);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registration_third);

            //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            appsingleton = Appsingleton.getinstance(this);
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
            tv_titlemessage = (TextView) findViewById(R.id.tv_titlemessage);
            ll_termslayout = (LinearLayout) findViewById(R.id.ll_termslayout);
            ll_bottombtnlayout = (LinearLayout) findViewById(R.id.ll_Buttonlayout);
            bt_next = (Button) findViewById(R.id.bt_Loginbtn);
            tv_validationtext = (TextView) findViewById(R.id.tv_validation);
            rl_validation = (RelativeLayout) findViewById(R.id.rl_validation);
            iv_apptext = (ImageView) findViewById(R.id.iv_logintitle);
            intrestgridView = (GridView) findViewById(R.id.gridView);
            tv_termstitle = (TextView) findViewById(R.id.tv_titleterms);
            tb_terms = (ToggleButton) findViewById(R.id.chkState);
            /*set custom fonts*/
            tv_logotitle.setTypeface(appsingleton.regulartype);
            tv_titlemessage.setTypeface(appsingleton.regulartype);
            bt_next.setTypeface(appsingleton.boldtype);
            tv_validationtext.setTypeface(appsingleton.regulartype);
            tv_termstitle.setTypeface(appsingleton.lighttype);



             /*setheight width Programatically*/
            SetHeightWidth(iv_apptext, 0.80, 0.10);
            /*Set Margins*/
            setMargins(ll_bottombtnlayout, 0.07, 0, 0.07, 0);
            setMargins(ll_termslayout, 0.08, 0, 0.07, 0.03);
            setMargins(bt_next, 0.07, 0.01, 0.07, 0);
            setMargins(iv_apptext, 0, 0.02, 0., 0);

            //Load Adapter
            reloadListData();

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            tb_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {

                }
            });

//            intrestgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of oncreate

    /* Function to Login
   * */
    public void Createaccount(View view) {
        try {
            int count = 0;
            String intrestarray = "";
            for (InterestVO vo : intrestlist) {
                if (vo.isselected) {
                    if (count > 0)
                        intrestarray += "," + vo.getIntrestid();
                    else
                        intrestarray += vo.getIntrestid();
                    count++;

                }
            }
            if (count == 0) {
                tv_validationtext.setText("Select at least one interest.");
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }
            if (count > 7) {
                tv_validationtext.setText(getResources().getString(R.string.st_errorintrests));
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }

            if (!tb_terms.isChecked()) {
                tv_validationtext.setText("You need to accept terms and conditions");
                rl_validation.setVisibility(View.VISIBLE);
                tv_validationtext.startAnimation(appsingleton.shake_animation);
                return;
            }


            if (AppUtils.isNetworkAvailable(this)) {

                if(appsingleton.isLocationEnabled(this))
                    new UserRegistration(intrestarray).execute();
                else {
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
                appsingleton.SnackbarMessage(getResources().getString(R.string.st_internet_error),"",tv_logotitle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<InterestVO> setData() {
        Collections.sort(appsingleton.intrestlist, new Comparator<InterestVO>() {
            public int compare(InterestVO s1, InterestVO s2) {
                return s1.getIntrest().compareToIgnoreCase(s2.getIntrest());
            }
        });
        ArrayList<InterestVO> list = new ArrayList<>();
        list.addAll(appsingleton.intrestlist);
        return list;
    }

    /**
     * Refresh list without changing current position
     */
    public void reloadListData() {
        try {
            ArrayList<InterestVO> list = setData();

            if (list.isEmpty()) {
            } else {

                intrestgridView.setVisibility(ListView.VISIBLE);
                if (adapter != null) {
                    adapter.reload(list);
                } else {
                    bindList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Bind all orders to list view
     */
    public void bindList() {
        try {
            ArrayList<InterestVO> list = setData();
            if (list.isEmpty()) {

            } else {
                intrestgridView.setVisibility(ListView.VISIBLE);
                adapter = new IntrestAdapter(this, R.layout.registration_last_adapter, list);
                intrestgridView.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adapter to bind Intrest list to list view
     *
     * @author user
     */
    public class IntrestAdapter extends ArrayAdapter<InterestVO> {
        private Context context;
        private ArrayList<InterestVO> list;

        public IntrestAdapter(Context context, int resource, ArrayList<InterestVO> objects) {
            super(context, resource, objects);
            this.context = context;
            this.list = objects;
            //intrestlist.clear();
            intrestlist = list;
        }

        @SuppressLint({"InflateParams", "ViewHolder", "SimpleDateFormat"})
        public View getView(int position, View convertView, ViewGroup parent) {
            View gridView = convertView;
            final ViewHolder holder;
            final ViewHolder holder2;
            try {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (gridView == null) {
                    // get layout from picture_grid_item.xml
                    gridView = inflater.inflate(R.layout.registration_last_adapter, parent, false);
                    holder = new ViewHolder();

                    // Initialize views
                    holder.tv_intrest = (TextView) gridView.findViewById(R.id.tv_intrest);
                    holder.rl_mainlayout = (RelativeLayout) gridView.findViewById(R.id.rl_mainlayout);
                    holder.rl_mainlayout.setBackgroundResource(R.drawable.rounded_whiteborder);
                    gridView.setTag(holder);
                } else {
                    holder = (ViewHolder) gridView.getTag();
                }
                /**
                 * First of all apply custom fonts
                 */
                holder.tv_intrest.setTypeface(appsingleton.lighttype);

                /**
                 * Now set values to views
                 */
                final InterestVO pair = list.get(position);
                holder.tv_intrest.setText(pair.getIntrest());
                if (pair.isselected) {
                    holder.rl_mainlayout.setBackgroundResource(R.drawable.rounded_fillwhite);
                    holder.tv_intrest.setTextColor(getResources().getColor(R.color.black));
                } else {
                    holder.rl_mainlayout.setBackgroundResource(R.drawable.rounded_whiteborder);
                    holder.tv_intrest.setTextColor(getResources().getColor(R.color.white));
                }
                /**
                 * On click listeners
                 */
                final int rowPosition = position;
                holder.rl_mainlayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {

                            ArrayList<InterestVO> intrestlist = new ArrayList<InterestVO>();
                            if (count < 7)
                                intrestlist.clear();
                            if (pair.isselected) {
                                holder.rl_mainlayout.setBackgroundResource(R.drawable.rounded_whiteborder);
                                holder.tv_intrest.setTextColor(getResources().getColor(R.color.white));
                                pair.setIsselected(false);
                                intrestlist.addAll(list);
                                reload(intrestlist);
                                count--;
                            } else {
                                if (count < 7) {
                                    holder.rl_mainlayout.setBackgroundResource(R.drawable.rounded_fillwhite);
                                    holder.tv_intrest.setTextColor(getResources().getColor(R.color.black));
                                    pair.setIsselected(true);
                                    intrestlist.addAll(list);
                                    reload(intrestlist);
                                    count++;
                                }//end of if count
                                else {

                                    holder.rl_mainlayout.startAnimation(appsingleton.shake_animation);
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return gridView;
        }

        public void reload(ArrayList<InterestVO> post) {
            try {
                list.clear();
                list.addAll(post);
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    private static class ViewHolder {

        TextView tv_intrest;
        RelativeLayout rl_mainlayout;
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
    public void onBackPressed() {
        try {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed

    /**
     * Registration  web service
     *
     * @author user
     */
    public class UserRegistration extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getUserRegistrationUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        int userid;
        String intrestarray = "";
        String firstname = "";
        String lastname = "";
        String email = "";
        String gender = "";
        String dateofbirth = "";
        String mobile = "";
        String username = "";
        String password = "";
        String deviceplatform = "";
        String countryid = "";

        public UserRegistration(String intrestarray) {
            try {
                this.intrestarray = intrestarray;
                userid = -1;
                try {
                    firstname = appsingleton.stringArrayList.get(0);
                    lastname = appsingleton.stringArrayList.get(1);
                    email = appsingleton.stringArrayList.get(2);
                    if(appsingleton.stringArrayList.get(3).equalsIgnoreCase("male"))
                    gender ="m";
                    else
                    gender ="f";
                    dateofbirth = appsingleton.stringArrayList.get(4);
                    mobile = appsingleton.stringArrayList.get(5);
                } catch (Exception e) {
                    e.printStackTrace();
                    appsingleton.ToastMessage("" + e.getMessage());
                }
                username = appsingleton.stringArrayList2.get(0);
                password = appsingleton.stringArrayList2.get(1);
                countryid = "";
                deviceplatform = "1";
            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(RegistrationThirdActivity.this);
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
                        .key("firstname").value(firstname)
                        .key("lastname").value(lastname)
                        .key("mobile").value(mobile)
                        .key("gender").value(gender)
                        .key("dob").value(dateofbirth)
                        .key("email").value(email)
                        .key("username").value(username)
                        .key("password").value(appsingleton.md5(password))
                        .key("interest").value(intrestarray)
                        .key("country_id").value("")
                        .key("device_platform").value("1")
                        .key("fcm_registration_id").value(appsingleton.getFCMtoken())
                        .key("user_lat").value(appsingleton.currentLatitude)
                        .key("user_long").value(appsingleton.currentLongitude)
                        .key("radius").value("25")
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
                                userid = object.getInt("user_id");
                            } catch (Exception e) {
                                userid = -1;
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }
                            appsingleton.ToastMessage("Welcome user " + userid);

                            try {
                                try{
                                    appsingleton.mDatabase.deleteUserprofileDetails();
                                }catch(Exception e){
                                    e.printStackTrace();
                                    appsingleton.ToastMessage("" + e.getMessage());
                                }
                                appsingleton.getCometchatObject().login(ConnectionsURL.getCometchatSiteurl(), username,password, new Callbacks() {

                                    @Override
                                    public void successCallback(JSONObject response) {
                                        try{
                                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USER_NAME, username);
                                            SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.PASSWORD,password);
                                            UserProfileDetailVO vo = new UserProfileDetailVO();
                                            vo.setUserid(userid);
                                            vo.setFirstname(firstname);
                                            vo.setLastname(lastname);
                                            vo.setEmail(email);
                                            vo.setGender(gender);
                                            vo.setDob(dateofbirth);
                                            vo.setMobile(mobile);
                                            vo.setUsername(username);
                                            vo.setIntrest(intrestarray);
                                            vo.setCountryid("");
                                            vo.setNotification("yes");
                                            vo.setShow_age("yes");
                                            vo.setIsonline(1);
                                            try{
                                                long count = appsingleton.mDatabase.addUserprofileDetails(vo);
                                            }catch(Exception e){
                                                e.printStackTrace();
                                                appsingleton.ToastMessage("" + e.getMessage());
                                            }
                                            SharedPreferences.Editor editor = appsingleton.sharedPreferences.edit();
                                            editor.putInt("ZAPUSER_ID", userid);
                                            editor.apply();

                                            try{
                                                //sendNotificationInmylocation("",);
                                                Intent msgIntent = new Intent(RegistrationThirdActivity.this, FrindListIntentService.class);
                                                msgIntent.putExtra(FrindListIntentService.USER_ID, "" + appsingleton.getUserid());
                                                msgIntent.putExtra(FrindListIntentService.LAST_ID, "" + "0");
                                                startService(msgIntent);

                                            }catch(Exception e){
                                                e.printStackTrace();
                                            }

                                            Intent intent = new Intent(RegistrationThirdActivity.this, UserTabhostActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                            finish();
                                            appsingleton.ToastMessage("" + count);
                                            appsingleton.dismissDialog();
                                        }catch(Exception e){
                                            appsingleton.dismissDialog();
                                            e.printStackTrace();
                                            appsingleton.ToastMessage("" + e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void failCallback(JSONObject response) {
                                        Logger.debug("fresponse->" + response);
                                        LogsActivity.addToLog("Login failCallback");
                                        Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_LONG).show();
                                        appsingleton.dismissDialog();
                                    }
                                });

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
                            appsingleton.UserToastMessage(getResources().getString(R.string.st_registrationfail));
                            appsingleton.dismissDialog();
                            break;
                        case 105:
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
                //appsingleton.dismissDialog();
            } catch (Exception e) {
                e.printStackTrace();
                appsingleton.dismissDialog();
                appsingleton.ToastMessage("" + e.getMessage());
            }
        }
    }//end of ValidateUsername Asynctask
    public void callLocationListner()
    {
        try {
            appsingleton.locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 500, 0,
                    appsingleton.networkLocationListener);

            appsingleton. locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    500, 0, appsingleton.gpsLocationListener);
        }catch (Exception e){e.printStackTrace();}
    }
}//end of Activity


