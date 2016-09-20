/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaparound;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.zaparound.IntentServices.CheckoutIntentService;
import com.zaparound.IntentServices.InMyLocationIntentService;
import com.zaparound.IntentServices.ZapWhosIntentService;
import com.zaparound.IntentServices.ZapsIntentservice;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.helper.Keys;
import com.zaparound.helper.SharedPreferenceHelper;

import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Appsingleton appsingleton;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        try {
            appsingleton=Appsingleton.getinstance(this);
            Log.d(TAG, "From: " + remoteMessage.getData());

            JSONObject object = new JSONObject(remoteMessage.getData());
//            String resultArray = object.getString("message");
//            JSONObject arrayObject = new JSONObject(object.getString("message"));
              String message= object.getString("message");
              String type= object.getString("type");
           Log.d(TAG, "Notification Message Body: "+message);

            if(type.equals("zaprequest")||type.equals("zapclear")||type.equals("checkinremove"))
            {
                if(type.equals("zaprequest"))
                    sendNotificationZapRequest("Zap Request",message);
                try {
                    CallWhosIntentservice();
                }catch (Exception e){e.printStackTrace();}
                try {
                    CallZapsIntentservice();
                }catch (Exception e){e.printStackTrace();}
            }
           else if(type.equals("zaprequestaccept"))
            {
                try {
                    sendAcceptNotification("It's a Match",message);
                }catch (Exception e){e.printStackTrace();}

            }else if(type.equals("newcheckin"))
            {
                try {
                    CallWhosIntentservice();
                }catch (Exception e){e.printStackTrace();}
            }else if(type.equals("autocheckout"))
            {
                //sendNotification(type,message);
                try {
                    CallCheckoutIntentservice();
                }catch (Exception e){e.printStackTrace();}
            }else if(type.equals("logout"))
            {
                //sendNotification(type,message);
                try {
                    CallLogoutIntentservice();
                }catch (Exception e){e.printStackTrace();}
            }
            else if(type.equals("broadcast"))
            {
                try {
                    String description= object.getString("mdesc");
                    String title= object.getString("mtitle");
                    String colorcode= object.getString("color");
                    SendBroadcastMessage(title,description,colorcode);
                }catch (Exception e){e.printStackTrace();}
            }
//            else
//            {
//                if(!type.equals("zaprequestdeny"))
//                    sendNotification(type,message);
//            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }//end of on receive

    /*
    *Function to call Zap Whos intent service
    */
    public void CallWhosIntentservice()
    {
        try {
            Intent msgIntent = new Intent(this, ZapWhosIntentService.class);
            startService(msgIntent);
        }catch (Exception e){e.printStackTrace();}

    }

    /*
   *Function to call Zap's intent service
   */
    public void CallZapsIntentservice()
    {
        try {
            Intent msgIntent = new Intent(this, ZapsIntentservice.class);
            startService(msgIntent);
        }catch (Exception e){e.printStackTrace();}

    }

    /*
   *Function to call Checkout
   */
    public void CallCheckoutIntentservice()
    {
        try{
            appsingleton.setCheckinsession(false);
            Intent intent = new Intent();
            intent.setAction("com.zaparound.CHECKOUT");
            sendBroadcast(intent);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
   *Function to call LIveuserList
   */
    public void CallLiveUserdataIntentservice()
    {
        try{
            //sendNotificationInmylocation("",);
                Intent msgIntent = new Intent(this, InMyLocationIntentService.class);
                msgIntent.putExtra(CheckoutIntentService.REQUEST_STRING, "" + appsingleton.getUserid());
                startService(msgIntent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
   *Function to call SendBroadcastMessage
   */
    public void SendBroadcastMessage(String title,String message,String colorcode)
    {
        try {
            Intent intent = new Intent();
            intent.setAction("com.zaparound.BROADCASTMESSAGE");
            intent.putExtra("BROADCASTMESSAGETITLE",message);
            intent.putExtra("BROADCASTMESSAGE",message);
            intent.putExtra("BACKGROUNDCOLORCODE",colorcode);
            sendBroadcast(intent);
        }catch (Exception e){e.printStackTrace();}

    }

    /*
  *Function to call Checkout
  */
    public void CallLogoutIntentservice()
    {
        try{
            try{
                if(appsingleton.getCheckinsession()) {
                    Intent msgIntent = new Intent(this, CheckoutIntentService.class);
                    msgIntent.putExtra(CheckoutIntentService.REQUEST_STRING, "" + appsingleton.getUserid());
                    startService(msgIntent);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            appsingleton.Logout();
            appsingleton.setCheckinsession(false);
            Intent intent = new Intent();
            intent.setAction("com.zaparound.CHECKOUT");
            sendBroadcast(intent);

            try{
                appsingleton.getCometchatObject().logout(new Callbacks() {
                    @Override
                    public void successCallback(JSONObject response) {
                        SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.IS_LOGGEDIN);
                        SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.USER_NAME);
                        SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.PASSWORD);
                        SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.LOGIN_TYPE);
                        SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.USERS_LIST);
                        SharedPreferenceHelper.removeKey(Keys.SharedPreferenceKeys.CHATROOMS_LIST);
                    }
                    @Override
                    public void failCallback(JSONObject response) {
                        Logger.debug("logout failed");
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotificationZapRequest(String messagetitle,String messageBody) {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("CHECKIN_TAB_POS",1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);
        Notification notification = mBuilder.setSmallIcon(R.drawable.applogo).setTicker(messagetitle).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(messagetitle)
                //.setNumber(++count)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                //.setSubText("\n "+count+" new messages\n")
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.applogo))
                .setContentText(messageBody).build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendAcceptNotification(String messagetitle,String messageBody) {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("CHATTAB_POSITION",2);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);
        Notification notification = mBuilder.setSmallIcon(R.drawable.applogo).setTicker(messagetitle).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(messagetitle)
                //.setNumber(++count)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                //.setSubText("\n "+count+" new messages\n")
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.applogo))
                .setContentText(messageBody).build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
    /**
     * Create and show a Inmylocation Notification.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotificationInmylocation(String messagetitle,String messageBody) {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("CHATTAB_POSITION",3);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);
        Notification notification = mBuilder.setSmallIcon(R.drawable.applogo).setTicker(messagetitle).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(messagetitle)
                //.setNumber(++count)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                //.setSubText("\n "+count+" new messages\n")
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.applogo))
                .setContentText(messageBody).build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
