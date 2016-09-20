package com.zaparound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zaparound.ModelVo.UserProfileDetailVO;
import com.zaparound.Singleton.Appsingleton;

public class MyProfileActivity extends AppCompatActivity {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    Toolbar toolbar;
    public TextView tv_toolbartitle;
    public TextView tv_firstnametitle,tv_firstnamevalue,tv_lasttnametitle,tv_lastnamevalue,
            tv_usernametitle,tv_usernamevalue,tv_passwordtitle,tv_passwordvalue,tv_emailtitle,tv_emailvalue,
            tv_dateofbirthtitle,tv_dateofbirthvalue,tv_gendertitle,tv_gendervalue,
            tv_mobiletitle,tv_mobilevalue;
    public Button bt_editprofile;
    public RelativeLayout rl_changepassword;
    public int userid=0;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            appsingleton=Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setDatabase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
        appsingleton = Appsingleton.getinstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
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
            tv_toolbartitle.setText(getResources().getText(R.string.st_myprofiletext));
            tv_toolbartitle.setTypeface(appsingleton.regulartype);
            appsingleton.setMargins(tv_toolbartitle, 0.19, 0, 0, 0);

            //initialize views
            tv_firstnametitle = (TextView) findViewById(R.id.tv_firstnametitle);
            tv_firstnamevalue = (TextView) findViewById(R.id.tv_firstnamevalue);
            tv_lasttnametitle = (TextView) findViewById(R.id.tv_lastnametitle);
            tv_lastnamevalue = (TextView) findViewById(R.id.tv_lastnamevalue);
            tv_usernametitle = (TextView) findViewById(R.id.tv_usernametitle);
            tv_usernamevalue = (TextView) findViewById(R.id.tv_usernamevalue);
            tv_passwordtitle = (TextView) findViewById(R.id.tv_passwordtitle);
            tv_passwordvalue = (TextView) findViewById(R.id.tv_passwordvalue);
            tv_emailtitle = (TextView) findViewById(R.id.tv_emailtitle);
            tv_emailvalue = (TextView) findViewById(R.id.tv_emailvalue);
            tv_dateofbirthtitle = (TextView) findViewById(R.id.tv_dateofbirthtitle);
            tv_dateofbirthvalue = (TextView) findViewById(R.id.tv_dateofbirthvalue);
            tv_gendertitle = (TextView) findViewById(R.id.tv_gendertitle);
            tv_gendervalue = (TextView) findViewById(R.id.tv_gendervalue);
            tv_mobiletitle = (TextView) findViewById(R.id.tv_mobilenumbertitle);
            tv_mobilevalue = (TextView) findViewById(R.id.tv_mobilenumbervalue);
            bt_editprofile = (Button) findViewById(R.id.bt_Loginbtn);
            rl_changepassword = (RelativeLayout) findViewById(R.id.rl_changepass);


            /*set custom fonts*/
            tv_firstnametitle.setTypeface(appsingleton.regulartype);
            tv_firstnamevalue.setTypeface(appsingleton.regulartype);
            tv_lasttnametitle.setTypeface(appsingleton.regulartype);
            tv_lastnamevalue.setTypeface(appsingleton.regulartype);
            tv_usernametitle.setTypeface(appsingleton.regulartype);
            tv_usernamevalue.setTypeface(appsingleton.regulartype);
            tv_passwordtitle.setTypeface(appsingleton.regulartype);
            tv_passwordvalue.setTypeface(appsingleton.regulartype);
            tv_emailtitle.setTypeface(appsingleton.regulartype);
            tv_emailvalue.setTypeface(appsingleton.regulartype);
            tv_dateofbirthtitle.setTypeface(appsingleton.regulartype);
            tv_dateofbirthvalue.setTypeface(appsingleton.regulartype);
            tv_gendertitle.setTypeface(appsingleton.regulartype);
            tv_gendervalue.setTypeface(appsingleton.regulartype);
            tv_mobiletitle.setTypeface(appsingleton.regulartype);
            tv_mobilevalue.setTypeface(appsingleton.regulartype);
            bt_editprofile.setTypeface(appsingleton.regulartype);

            setDatabase();
//            /*Set Margins*/
            appsingleton.setMargins(bt_editprofile, 0.07, 0.0, 0.07, 0);

            //click listeners
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            rl_changepassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent3 = new Intent(MyProfileActivity.this, ChangePasswordActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }

                }
            });

            bt_editprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent3 = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }

                }
            });
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of oncreate

    /*
    *to set data from database
    */
    public  void setDatabase()
    {
        try{
            appsingleton=Appsingleton.getinstance(this);
            userid=appsingleton.getUserid();
            UserProfileDetailVO vo=appsingleton.mDatabase.getUserprofileDetails(userid);
            tv_firstnamevalue.setText(vo.getFirstname());
            tv_lastnamevalue.setText(vo.getLastname());
            tv_emailvalue.setText(vo.getEmail());
            if(vo.getGender().equalsIgnoreCase("m"))
                tv_gendervalue.setText(getResources().getText(R.string.st_malegende));
            else
                tv_gendervalue.setText(getResources().getText(R.string.st_femalegender));
            tv_dateofbirthvalue.setText(vo.getDob());
            tv_mobilevalue.setText(vo.getMobile());
            tv_usernamevalue.setText(vo.getUsername());
        }catch(Exception e){
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
}//end of Activity
