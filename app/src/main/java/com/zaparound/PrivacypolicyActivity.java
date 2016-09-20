package com.zaparound;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zaparound.Singleton.Appsingleton;

public class PrivacypolicyActivity extends AppCompatActivity {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    Toolbar toolbar;
    public TextView tv_toolbartitle;
    public TextView tv_privacytitle,tv_updated,tv_updateddate,tv_description;

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
        try{
            appsingleton = Appsingleton.getinstance(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_privacypolicy);
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
            tv_toolbartitle.setText(getResources().getString(R.string.st_Privacytollbar_title));
            tv_toolbartitle.setTypeface(appsingleton.regulartype);
            appsingleton.setMargins(tv_toolbartitle,0.20,0,0,0);

            //initialize views
            tv_privacytitle = (TextView) findViewById(R.id.tv_firstnamevalue);
            tv_updated = (TextView) findViewById(R.id.tv_lastnametitle);
            tv_updateddate = (TextView) findViewById(R.id.tv_updateddate);
            tv_description = (TextView) findViewById(R.id.tv_desctiptiontext);


            /*set custom fonts*/
            tv_privacytitle.setTypeface(appsingleton.regulartype);
            tv_updated.setTypeface(appsingleton.regulartype);
            tv_updateddate.setTypeface(appsingleton.regulartype);
            tv_description.setTypeface(appsingleton.regulartype);
            //click listeners
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of oncreate
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
