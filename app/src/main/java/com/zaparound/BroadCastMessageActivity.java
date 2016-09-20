package com.zaparound;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zaparound.Singleton.Appsingleton;

public class BroadCastMessageActivity extends AppCompatActivity {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    public ImageView iv_logoimage, iv_close;
    RelativeLayout rl_validation;
    public TextView tv_logotitle,tv_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            appsingleton = Appsingleton.getinstance(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_broad_cast_message);
             //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);
            //initialize views
            iv_logoimage = (ImageView) findViewById(R.id.iv_LOGOIMAGE);
            iv_close = (ImageView) findViewById(R.id.iv_Closeimage);
            rl_validation = (RelativeLayout) findViewById(R.id.rl_Mainlayout);
            tv_logotitle = (TextView) findViewById(R.id.tv_messagetitle);
            tv_message = (TextView) findViewById(R.id.tv_message);

            tv_logotitle.setTypeface(appsingleton.regulartype);
            tv_message.setTypeface(appsingleton.regulartype);

            appsingleton.setMargins(iv_logoimage,0,0.07,0,0);
            SetHeightWidth(iv_logoimage,0.45,0.28);
            String  title="",message="",colorcode="#8e236e";
            try{
                    title = appsingleton.sharedPreferences.getString("BROADCASTMESSAGETITLE","");
                    message = appsingleton.sharedPreferences.getString("BROADCASTMESSAGE","");
                    colorcode = appsingleton.sharedPreferences.getString("BACKGROUNDCOLORCODE","#8e236e");
                }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
            tv_logotitle.setText(title);
            tv_message.setText(message);
            rl_validation.setBackgroundColor(Color.parseColor(colorcode));
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        finish();
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

}//end of Activity
