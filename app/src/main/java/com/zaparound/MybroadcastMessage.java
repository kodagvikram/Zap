package com.zaparound;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.zaparound.Singleton.Appsingleton;

public class MybroadcastMessage extends BroadcastReceiver {
    Appsingleton appsingleton;
    @Override
    public void onReceive(Context context, Intent intent) {
        //appsingleton.setCheckinsession(false);
        try {
               appsingleton=Appsingleton.getinstance(context);
            String message="",colorcode="",title="";

            try{
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    message = extras.getString("BROADCASTMESSAGE", "");
                    colorcode = extras.getString("BACKGROUNDCOLORCODE", "");
                    title = extras.getString("BROADCASTMESSAGETITLE", "");
                }
                SharedPreferences.Editor editor=appsingleton.sharedPreferences.edit();
                editor.putString("BROADCASTMESSAGE",message);
                editor.putString("BACKGROUNDCOLORCODE",colorcode);
                editor.putString("BROADCASTMESSAGETITLE",title);
                editor.apply();
            }catch(Exception e){
                e.printStackTrace();
            }
            Intent intentone = new Intent(context.getApplicationContext(), BroadCastMessageActivity.class);
            intentone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.putExtra("BROADCASTMESSAGE",message);
            context.startActivity(intentone);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}