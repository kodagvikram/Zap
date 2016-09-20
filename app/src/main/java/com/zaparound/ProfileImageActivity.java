package com.zaparound;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zaparound.Singleton.Appsingleton;

public class ProfileImageActivity extends AppCompatActivity {
    Toolbar toolbar;
    public Appsingleton appsingleton;
    public ImageView imageView;

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
        setContentView(R.layout.activity_profile_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

           //initialize views
          imageView=(ImageView)findViewById(R.id.img);

            try{
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    String value = extras.getString("IMAGEURL");
                    //The key argument here must match that used in the other activity
                    Picasso.with(this).load(value).placeholder(R.drawable.dummy_user_icon_big).into(imageView);

                }

            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }catch(Exception e){
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of oncreate
    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
           // overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        } catch (Exception e) {
            appsingleton.ToastMessage(e.getMessage());
        }
    }//end of on back pressed
}//end of Activity
