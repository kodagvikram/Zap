package com.zaparound;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zaparound.LoginActivity;

public class CheckoutReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //appsingleton.setCheckinsession(false);
        try {
            Intent intentone = new Intent(context.getApplicationContext(), LandingActivity.class);
            intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentone);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}