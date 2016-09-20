package com.zaparound;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zaparound.Singleton.Appsingleton;

public class AppSettingActivity extends AppCompatActivity {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    Toolbar toolbar;
    public TextView tv_toolbartitle, tv_notificationtitle, tv_push, tv_agetitle, tv_public;
    public ToggleButton tb_agetoggle, tb_notificationtoggle;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            appsingleton = Appsingleton.getinstance(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_app_setting);

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
            tv_toolbartitle = (TextView) toolbar.findViewById(R.id.tv_title);
            tv_toolbartitle.setText(getResources().getString(R.string.st_appsettingtollbar));
            tv_toolbartitle.setTypeface(appsingleton.regulartype);
            setMargins(tv_toolbartitle, 0.20, 0, 0, 0);

            //initialize views
            tv_notificationtitle = (TextView) findViewById(R.id.tv_notificationtitle);
            tv_push = (TextView) findViewById(R.id.tv_notificationtext);
            tv_agetitle = (TextView) findViewById(R.id.tv_agetitle);
            tv_public = (TextView) findViewById(R.id.tv_publictext);
            tb_notificationtoggle = (ToggleButton) findViewById(R.id.tb_notification);
            tb_agetoggle = (ToggleButton) findViewById(R.id.tb_age);

            //set fonts
            tv_notificationtitle.setTypeface(appsingleton.regulartype);
            tv_push.setTypeface(appsingleton.regulartype);
            tv_agetitle.setTypeface(appsingleton.regulartype);
            tv_public.setTypeface(appsingleton.regulartype);
            //click listeners
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            if (appsingleton.sharedPreferences.getBoolean("SHOWAGE", true)) {
                tb_agetoggle.setChecked(true);
                tv_public.setText(getResources().getString(R.string.st_public));
            } else {

                tb_agetoggle.setChecked(false);
                tv_public.setText(getResources().getString(R.string.st_private));
            }
            if (appsingleton.sharedPreferences.getBoolean("SHOW_NOTIFICATION", true)) {
                tb_notificationtoggle.setChecked(true);
            } else {

                tb_notificationtoggle.setChecked(false);
            }

            tb_agetoggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        SharedPreferences.Editor editor = appsingleton.sharedPreferences.edit();
                        if (isChecked) {
                            editor.putBoolean("SHOWAGE", true);
                            tv_public.setText(getResources().getString(R.string.st_public));
                        } else {
                            editor.putBoolean("SHOWAGE", false);
                            tv_public.setText(getResources().getString(R.string.st_private));
                        }
                        editor.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            tb_notificationtoggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        SharedPreferences.Editor editor = appsingleton.sharedPreferences.edit();
                        if (isChecked) {
                            editor.putBoolean("SHOW_NOTIFICATION", true);
                        } else {
                            editor.putBoolean("SHOW_NOTIFICATION", false);
                        }
                        editor.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of oncreate

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
}//end of Activity
