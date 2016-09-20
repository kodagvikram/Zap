package com.zaparound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zaparound.Singleton.Appsingleton;

public class LegalPolicyActivity extends AppCompatActivity {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    Toolbar toolbar;
    public TextView tv_toolbartitle;
    public RelativeLayout rl_myprofilelayout, rl_invitefriendslayout;
    public TextView tv_myprofile, tv_invitefrinds;

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
            setContentView(R.layout.activity_legal_policy);

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
            tv_toolbartitle.setText(getResources().getString(R.string.st_legaltollbar_title));
            tv_toolbartitle.setTypeface(appsingleton.regulartype);
            appsingleton.setMargins(tv_toolbartitle,0.20,0,0,0);

            //initialize views
            rl_myprofilelayout=(RelativeLayout)findViewById(R.id.rl_myprofilelayout);
            rl_invitefriendslayout=(RelativeLayout)findViewById(R.id.rl_invitefrindslayout);
            tv_myprofile=(TextView) findViewById(R.id.tv_myprofile);
            tv_invitefrinds=(TextView) findViewById(R.id.tv_invitefrinds);

            //set custom fonts
            tv_myprofile.setTypeface(appsingleton.regulartype);
            tv_invitefrinds.setTypeface(appsingleton.regulartype);

            //click listeners
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            rl_myprofilelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent3 = new Intent(LegalPolicyActivity.this, PrivacypolicyActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            rl_invitefriendslayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent3 = new Intent(LegalPolicyActivity.this, TermsofuseActivity.class);
                        startActivity(intent3);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }catch(Exception e){
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
