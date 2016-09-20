package com.zaparound;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zaparound.AppUtils.ConnectionsURL;
import com.zaparound.ModelVo.CheckinZapmeVO;
import com.zaparound.Singleton.Appsingleton;

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

public class CheckinTabscriptActivity extends AppCompatActivity implements View.OnClickListener {
    PagerSlidingTabStrip tabs;
    CustomViewPager pager;
    Appsingleton appsingleton;
    MyPagerAdapter adapter1;
    Toolbar toolbar;
    RelativeLayout rl_setting, rl_recyclelayout, rl_locationlayout;
    public RelativeLayout rl_ckechinlayout;
    public ImageView iv_checkinicon, iv_gendericon;
    public TextView tv_tollbartitle, tv_locationtitle, tv_zapmecount;
    public DisplayMetrics metrics;
    LinearLayout ll_genderAnimationlayout;
    RelativeLayout ll_gendermainlayout;
    public ImageView iv_maingender,iv_gendermale,iv_genderfemail;
    Handler handler = new Handler();

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
        }catch (Exception e){e.printStackTrace();}
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_checkin_tabscript);

            //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            appsingleton = Appsingleton.getinstance(this);
            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);


            //initialize toolbar
            toolbar = (Toolbar) findViewById(R.id.tool_bar1);
            setSupportActionBar(toolbar);

            //initialize tollbar views
            rl_setting = (RelativeLayout) toolbar.findViewById(R.id.iv_checksetting);
            tv_tollbartitle = (TextView) toolbar.findViewById(R.id.tv_title);
            tv_locationtitle = (TextView) toolbar.findViewById(R.id.tv_checkintitle);
            iv_checkinicon = (ImageView) toolbar.findViewById(R.id.iv_Checkinicon);
            iv_gendericon = (ImageView) toolbar.findViewById(R.id.iv_gendericon);

            try{
                String checkinplace="";

                try {
                    if(appsingleton.getCheckinPlacename().length()>9)
                    checkinplace=appsingleton.getCheckinPlacename().substring(0,Math.min(appsingleton.getCheckinPlacename().length(),9))+"..";
                     else
                     checkinplace=appsingleton.getCheckinPlacename();
                }catch (Exception e){
                    checkinplace=appsingleton.getCheckinPlacename();
                    e.printStackTrace();}

                tv_locationtitle.setText(checkinplace);
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }

            //initialize views
            pager = (CustomViewPager) findViewById(R.id.pager);
            tv_zapmecount = (TextView) findViewById(R.id.tvzapmecount);
            pager.setPagingEnabled(false);
            tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            ll_gendermainlayout=(RelativeLayout) findViewById(R.id.ll_genderdropdownlayout);
            ll_genderAnimationlayout=(LinearLayout)findViewById(R.id.ll_animationlayout);
            iv_maingender = (ImageView) findViewById(R.id.iv_maingender);
            iv_gendermale = (ImageView) findViewById(R.id.iv_gendermale);
            iv_genderfemail = (ImageView) findViewById(R.id.iv_genderfemale);

            //Adapters
            adapter1 = new MyPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(adapter1);
            tabs.setTypeface(appsingleton.regulartype);
            tabs.setTextColor(getResources().getColor(R.color.white));
            tabs.setDividerColor(Color.parseColor("#FFFFFF"));
            tabs.setDividerWidth(0);
            tabs.setIndicatorHeight(20);
            tabs.setIndicatorColor(getResources().getColor(R.color.loginbtn_color));
            tabs.setViewPager(pager);
            final int pageMargin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            pager.setPageMargin(pageMargin);
            Intent intent = getIntent();
            int currentPosition = intent.getIntExtra("CHECKIN_TAB_POS", 0);
            pager.setCurrentItem(currentPosition);
            getIntent().removeExtra("CHECKIN_TAB_POS");
            //set fonts
            tv_tollbartitle.setTypeface(appsingleton.regulartype);
            tv_locationtitle.setTypeface(appsingleton.lighttype);
            tv_zapmecount.setTypeface(appsingleton.lighttype);

            try{
                appsingleton.CometChatSubscribe();
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
            //disable sliding view pager
            pager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pager.setCurrentItem(pager.getCurrentItem());
                    return true;
                }
            });

            iv_checkinicon.setOnClickListener(this);
            iv_gendericon.setOnClickListener(this);
            iv_maingender.setOnClickListener(this);
            iv_gendermale.setOnClickListener(this);
            iv_genderfemail.setOnClickListener(this);

            rl_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        appsingleton.getZaptabhostcontext().SettingClick();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            ll_gendermainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        ll_genderAnimationlayout.startAnimation(appsingleton.gender_up_animation);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ll_gendermainlayout.setVisibility(View.GONE);
                            }
                        },350);
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of oncreate

    @Override
    public void onClick(View v) {
        try{
        switch (v.getId()) {
            case R.id.iv_Checkinicon:
                    String title=getResources().getString(R.string.st_checkout_text);
                    String message=getResources().getString(R.string.st_checkout_subtext);
                    String leftbtn=getResources().getString(R.string.st_no);
                    String rightbtn=getResources().getString(R.string.st_yes);
                    showDialog(title,message,leftbtn,rightbtn,R.drawable.checkout_icon);
                break;
            case R.id.iv_gendericon:
                showGenderView();
                break;

            case R.id.iv_maingender:
                hideGenderView("all");
                iv_gendericon.setImageResource(R.drawable.gender_icon);
                break;
            case R.id.iv_gendermale:
                hideGenderView("m");
                iv_gendericon.setImageResource(R.drawable.male_icon);
                break;
            case R.id.iv_genderfemale:
                hideGenderView("f");
                iv_gendericon.setImageResource(R.drawable.female_icon);
                break;

        }//end of switch
        }catch(Exception e){
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
    *function to show genderview
    */
    public void showGenderView()
    {
        try{
            ll_genderAnimationlayout.startAnimation(appsingleton.gender_down_animation);
            ll_gendermainlayout.setVisibility(View.VISIBLE);

        }catch(Exception e){
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }
    /*
       *function to Hide genderview
       */
    public void hideGenderView(String gender)
    {
        try{
            int k=pager.getCurrentItem();
            if(pager.getCurrentItem() == 0) //First fragment
            {
                CheckinZaparoundFragment frag1 = (CheckinZaparoundFragment)pager.getAdapter().instantiateItem(pager, pager.getCurrentItem());
                frag1.FilterList(gender);
            }
            else
            {
                CheckinZappedMeFragment frag1 = (CheckinZappedMeFragment)pager.getAdapter().instantiateItem(pager, pager.getCurrentItem());
                frag1.FilterList(gender);
            }
            ll_genderAnimationlayout.startAnimation(appsingleton.gender_up_animation);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ll_gendermainlayout.setVisibility(View.GONE);
                }
            },350);


        }catch(Exception e){
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }
    /*
    *function to update count
    */
    public void updateCount(int count) {
        try {
            tv_zapmecount.setText("" + count);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }

    /*
   *function tonotification count count
   */
    public int getCount() {
        try {
            return Integer.parseInt( tv_zapmecount.getText().toString());
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
            return 0;
        }
    }

    /**
     * fragments adapter for Owner/admin application
     *
     * @author user
     */
    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = {getResources().getString(R.string.st_zaparound),
                getResources().getString(R.string.st_zappedme)};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = CheckinZaparoundFragment.newInstance();
                    break;
                case 1:
                    fragment = CheckinZappedMeFragment.newInstance();
                    break;
                default:
                    fragment = CheckinZaparoundFragment.newInstance();
            }
            return fragment;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }//end of adapter

    /*
   * to dosplay dialog
   * */
    public void showDialog(String title, String message, String btnleft, String btnright, int imageid) {
        try {
            final Dialog dialog = new Dialog(CheckinTabscriptActivity.this);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.pop_up_check_out);
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
                        dialog.dismiss();
                        new Checkout().execute();
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
        * Checkin read web service
        *
        * @author user
        */
    public class Checkout extends AsyncTask<String, String, String> {
        String URL = ConnectionsURL.getCheckoutUrl();
        String postdataKey = ConnectionsURL.getPOSTDATAKey();
        public Checkout() {

        }

        @Override
        public void onPreExecute() {
            try {
                appsingleton.initDialog(CheckinTabscriptActivity.this);
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
                              appsingleton.setCheckinsession(false);
                                Intent intent5 = new Intent(CheckinTabscriptActivity.this, LandingActivity.class);
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
    }//end of Checkin Pullto refresh Asynctask
}//end of Activity
