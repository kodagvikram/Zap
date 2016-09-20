package com.zaparound;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.util.Attributes;
import com.google.firebase.messaging.FirebaseMessaging;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.SubscribeCallbacks;
import com.inscripts.utils.Logger;
import com.zaparound.Adapters.ChatUserListSwipAdapter;
import com.zaparound.Adapters.InviteFriendHeaderAdapter;
import com.zaparound.Adapters.InviteFriendsAdapter;
import com.zaparound.Adapters.PersonAdapter;
import com.zaparound.IntentServices.FrindListIntentService;
import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.InviteFriendsVO;
import com.zaparound.ModelVo.SingleChatMessage;
import com.zaparound.ModelVo.SingleUser;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.data.PersonDataProvider;
import com.zaparound.helper.DatabaseHandler;
import com.zaparound.helper.Keys;
import com.zaparound.helper.PushNotificationsManager;
import com.zaparound.helper.SharedPreferenceHelper;
import com.zaparound.helper.Utils;
import com.zaparound.stickyheaders.StickyHeadersBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class ChatListActivity extends AppCompatActivity {
    public DisplayMetrics metrics;
    public Appsingleton appsingleton;
    Toolbar toolbar;
    RelativeLayout rl_setting, rl_clearalllayout, rlcanclelayout, rl_selecteditem, rl_deleteitem;
    public TextView tv_toolbartitle, tv_clearalltext, tv_searchtext,
            tv_cancletext, tv_selectecount, tv_selectedtext;
    public ImageView iv_delete, iv_searchicon;
    public EditText searchedittext;
    private RecyclerView mRecyclerView;
    ArrayList<ChatUserListVO> chatuserlist = new ArrayList<>();
    ArrayList<ChatUserListVO> tempachatuserlist = new ArrayList<>();
    public ChatUserListSwipAdapter mAdapter;
    LinearLayout ll_searchiconlayout;
    public SwipeRefreshLayout mSwipeRefreshLayout,emptySwipeRefreshLayout;
    public MyChatUserRequestReceiver receiver;
    RelativeLayout rl_nocontacts;
    public TextView tv_nocontacts;

    /*CometChatCloud cloud;*/
    public static final ArrayList<String> logs = new ArrayList<String>();

    /* Modify the URL to point to the site you desire. */
    private static final String SITE_URL = "http://192.168.0.159/";

	/* Change this value to a valid user ID on the above site. */
    //public static final String USER_ID = "6";

    public static String myId;

    @Override
    protected void onRestart() {
        super.onRestart();
        changeFouus();
        hidekeyboard();
        try {
            appsingleton=Appsingleton.getinstance(this);
            appsingleton.finishAllSession();
            getDatabaseList();
            CometChatSubscribe();
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
    protected void onResume() {
        super.onResume();
        //changeFouus();
        hidekeyboard();
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
            setContentView(R.layout.activity_chat_list);

            //appsingleton instance
            //get height width
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            appsingleton.deviceheight = metrics.heightPixels;
            appsingleton.devicewidth = metrics.widthPixels;
            appsingleton.activityArrayList.add(this);
            toolbar = (Toolbar) findViewById(R.id.tool_bar1);

            setSupportActionBar(toolbar);
            rl_setting = (RelativeLayout) toolbar.findViewById(R.id.iv_checksetting);
            rl_clearalllayout = (RelativeLayout) toolbar.findViewById(R.id.rl_clearalllayout);
            rlcanclelayout = (RelativeLayout) toolbar.findViewById(R.id.rl_canclelayout);
            tv_toolbartitle = (TextView) toolbar.findViewById(R.id.tv_title);
            tv_clearalltext = (TextView) toolbar.findViewById(R.id.tv_clearall);
            tv_cancletext = (TextView) toolbar.findViewById(R.id.tv_cancle);
            iv_delete = (ImageView) toolbar.findViewById(R.id.iv_deleteimage);
            tv_toolbartitle.setTypeface(appsingleton.regulartype);
            tv_clearalltext.setTypeface(appsingleton.regulartype);
            tv_cancletext.setTypeface(appsingleton.regulartype);

            //initialize views
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            rl_selecteditem = (RelativeLayout) findViewById(R.id.rl_Selecteditem);
            rl_deleteitem = (RelativeLayout) findViewById(R.id.rl_deleteitem);
            tv_selectecount = (TextView) findViewById(R.id.tv_itemcount);
            tv_selectedtext = (TextView) findViewById(R.id.tv_selected);
            searchedittext = (EditText) findViewById(R.id.et_search);
            tv_searchtext = (TextView) findViewById(R.id.tv_closebracket);
            iv_searchicon = (ImageView) findViewById(R.id.iv_searchicon);
            ll_searchiconlayout = ((LinearLayout) findViewById(R.id.ll_searchiconlayout));
            mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
            tv_nocontacts= (TextView) findViewById(R.id.tv_logintitle);
            rl_nocontacts = (RelativeLayout) findViewById(R.id.rl_nolocationfoundlayout);
            emptySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_emptyView);

            //setfonts
            tv_selectecount.setTypeface(appsingleton.regulartype);
            tv_selectedtext.setTypeface(appsingleton.regulartype);
            tv_searchtext.setTypeface(appsingleton.regulartype);
            tv_nocontacts.setTypeface(appsingleton.regulartype);
            // Layout Managers:
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Item Decorator:
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
            // mRecyclerView.setItemAnimator(new FadeInLeftAnimator());

            try{
                //sendNotificationInmylocation("",);
                Intent msgIntent = new Intent(this, FrindListIntentService.class);
                msgIntent.putExtra(FrindListIntentService.USER_ID, "" + appsingleton.getUserid());
                msgIntent.putExtra(FrindListIntentService.LAST_ID, "" + "0");
                startService(msgIntent);
            }catch(Exception e){
                e.printStackTrace();
            }
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


        /* Scroll Listeners */
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.e("RecyclerView", "onScrollStateChanged");
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
            rl_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if(appsingleton.getCheckinsession())
                            appsingleton.getZaptabhostcontext().SettingClick();
                        else
                            appsingleton.getTabhostcontext().SettingClick();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            rlcanclelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        HideShowToolbarlayout(false);
                        mAdapter.HideshowAdapterView(false);
                        changeFouus();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if(mAdapter!=null) {
                            HideShowToolbarlayout(true);
                            mAdapter.HideshowAdapterView(true);
                        }
                        changeFouus();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
            rl_deleteitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mAdapter.deleteUser();
                        changeFouus();
                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }

                }
            });
            rl_clearalllayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        changeFouus();
                        String title = getResources().getString(R.string.st_clearallpopup);
                        String message = getResources().getString(R.string.st_chatclear_subtext);
                        String leftbtn = getResources().getString(R.string.st_no);
                        String rightbtn = getResources().getString(R.string.st_yes);
                        mAdapter.showDialog(title, message, leftbtn, rightbtn, R.drawable.clear_icon, 0, "clear");
                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            searchedittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (hasFocus) {
                            ll_searchiconlayout.setVisibility(View.GONE);
                        } else {
                            if (searchedittext.getText().toString().trim().equals(""))
                                ll_searchiconlayout.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }

                }
            });

           /*
           *Search functionality
           */
            searchedittext.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    // Abstract Method of TextWatcher Interface.
                }

                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // Abstract Method of TextWatcher Interface.
                }

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    try {
                        int textlength = searchedittext.getText().length();
                        tempachatuserlist.clear();
                        for (int i = 0; i < chatuserlist.size(); i++) {
                            ChatUserListVO Vo = chatuserlist.get(i);
                            String compairstr = Vo.getUsername();
                            if (textlength <= compairstr.length()) {

                                if (searchedittext.getText().toString().equalsIgnoreCase(
                                        (String) compairstr.subSequence(
                                                0, textlength))) {
                                    tempachatuserlist.add(Vo);
                                    // image_sort.add(listview_images[i]);
                                }
                            }
                        }// end of for
//                        if(mAdapter.isshowDelete)
//                        {
//                            HideShowToolbarlayout(false);
//                            mAdapter.HideshowAdapterView(false);
//                        }
                        //SetCustomAdapter(tempachatuserlist);
                        if(mAdapter!=null) {
                            mAdapter.notifyDataSetChanged();
                        }
                        changeFouus();

                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });//end of textchange listner

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Refresh items
                    refreshItems();
                }
            });
            emptySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Refresh items
                    refreshItems();
                }
            });

            try{
                appsingleton.initDialog(this);
                //appsingleton.showDialog();
                CometChatSubscribe();
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
            try{
                getDatabaseList();
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }




            IntentFilter filter = new IntentFilter(MyChatUserRequestReceiver.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            receiver = new MyChatUserRequestReceiver();
            registerReceiver(receiver, filter);
        } catch (Exception e) {
            changeFouus();
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of oncreate

    /*
    *Subscribe Cometchat Users
    */
    public void CometChatSubscribe()
    {
        try{
            DatabaseHandler handler = new DatabaseHandler(this);
            //**-----------------------------------------------------Call commechat SubscribeCallbacks
            CometChat.setDevelopmentMode(false);

            final SubscribeCallbacks subCallbacks = new SubscribeCallbacks() {

                @Override
                public void onMessageReceived(JSONObject receivedMessage) {
                    LogsActivity.addToLog("One-On-One onMessageReceived");
                    Log.e("abc","msg "+receivedMessage);
                    try {
                        String messagetype = receivedMessage.getString("message_type");
                        Intent intent = new Intent();
                        intent.setAction("NEW_SINGLE_MESSAGE");
                        boolean imageMessage = false, videomessage = false, ismyMessage = false;
                        if (messagetype.equals("12")) {
                            intent.putExtra("imageMessage", "1");
                            imageMessage = true;
                            if (receivedMessage.getString("self").equals("1")) {
                                intent.putExtra("myphoto", "1");
                                ismyMessage = true;
                            }
                        } else if (messagetype.equals("14")) {
                            intent.putExtra("videoMessage", "1");
                            videomessage = true;
                            if (receivedMessage.getString("self").equals("1")) {
                                intent.putExtra("myVideo", "1");
                                ismyMessage = true;
                            }
                        }
                        intent.putExtra("message_type", messagetype);
                        intent.putExtra("user_id", receivedMessage.getInt("from"));
                        intent.putExtra("message", receivedMessage.getString("message").trim());
                        intent.putExtra("time", receivedMessage.getString("sent"));
                        intent.putExtra("message_id", receivedMessage.getString("id"));
                        intent.putExtra("from", receivedMessage.getString("from"));
                        String to = null;
                        if (receivedMessage.has("to")) {
                            to = receivedMessage.getString("to");
                        } else {
                            to = SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId);
                        }
                        intent.putExtra("to", to);
                        String time = Utils.convertTimestampToDate(Utils.correctTimestamp(Long.parseLong(receivedMessage
                                .getString("sent"))));
                        Date date = new Date();
                        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                        time = dateFormat.format(date);

                                SingleChatMessage newMessage = new SingleChatMessage(receivedMessage.getString("id"),
                                receivedMessage.getString("message").trim(), time, ismyMessage?"1":"0",
                                receivedMessage.getString("from"), to, messagetype, 0);
                       appsingleton.chatdbhelper.insertOneOnOneMessage(newMessage);
                        sendBroadcast(intent);
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

                        try{
                            for (int i = 0; i <chatuserlist.size() ; i++) {
                                ChatUserListVO  vo=chatuserlist.get(i);
                                    if(vo.getUserid().equalsIgnoreCase(""+newMessage.getFrom())||vo.getUserid().equalsIgnoreCase(""+newMessage.getTo()))
                                    {
                                        try{
                                            chatuserlist.get(i).setLastmessage(newMessage.getMessage());
                                             if(mAdapter!=null)
                                                mAdapter.notifyDataSetChanged();
                                        }catch(Exception e){
                                            e.printStackTrace();
                                            appsingleton.ToastMessage("" + e.getMessage());
                                        }

                                    }//end of if
                            }//end of for
                        }catch(Exception e){
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }//end of on messagereceive

                @Override
                public void onError(JSONObject errorResponse) {
                    LogsActivity.addToLog("One-On-One onError");
                    Logger.debug("Some error: " + errorResponse);
                }

                @Override
                public void gotProfileInfo(JSONObject profileInfo) {
                    LogsActivity.addToLog("One-On-One gotProfileInfo");
                    Logger.error("profile infor " + profileInfo);
                    appsingleton.getCometchatObject().getPluginInfo(new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            Log.d("abc", "PLugin infor =" + response);
                        }

                        @Override
                        public void failCallback(JSONObject response) {

                        }
                    });
                    JSONObject j = profileInfo;
                    try {
                        myId = j.getString("id");
                        SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.myId, myId);
                        if (j.has("push_channel")) {
                            PushNotificationsManager.subscribe(j.getString("push_channel"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


				/*
				 * cometchat.getOnlineUsers(new Callbacks() {
				 *
				 * @Override public void successCallback(JSONObject response) {
				 * Logger.debug("online users =" + response.toString());
				 *
				 * }
				 *
				 * @Override public void failCallback(JSONObject response) {
				 *
				 * } });
				 */
                }

                @Override
                public void gotOnlineList(JSONObject onlineUsers) {
                    try {

                        LogsActivity.addToLog("One-On-One gotOnlineList");
					/* Store the list for later use. */
                        SharedPreferenceHelper.save(Keys.SharedPreferenceKeys.USERS_LIST, onlineUsers.toString());
                        populateList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void gotAnnouncement(JSONObject announcement) {

                }

                @Override
                public void onActionMessageReceived(JSONObject response) {
                    try {
                        String action = response.getString("action");
                        Intent i = new Intent("NEW_SINGLE_MESSAGE");
                        if (action.equals("typing_start")) {
                            i.putExtra("action", "typing_start");
                        } else if (action.equals("typing_stop")) {
                            i.putExtra("action", "typing_stop");
                        } else if (action.equals("message_read")) {
                            i.putExtra("action", "message_read");
                            i.putExtra("from", response.getString("from"));
                            i.putExtra("message_id", response.getString("message_id"));
                            Utils.msgtoTickList.put(response.getString("message_id"), Keys.MessageTicks.read);
                        } else if (action.equals("message_deliverd")) {
                            i.putExtra("action", "message_deliverd");
                            i.putExtra("from", response.getString("from"));
                            i.putExtra("message_id", response.getString("message_id"));
                            Utils.msgtoTickList.put(response.getString("message_id"), Keys.MessageTicks.deliverd);
                        }
                        sendBroadcast(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };//end of SubscribeCallbacks-----------------


            if (CometChat.isLoggedIn()) {
                appsingleton.getCometchatObject().subscribe(true, subCallbacks);
            }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }//end

    /*
    *PARSE DATA AND RELOAD DATA
    */
    public  void populateList() {
        try {
            ArrayList<ChatUserListVO> templist = new ArrayList<>();
            if ( null != chatuserlist) {
                JSONObject onlineUsers;
                if (SharedPreferenceHelper.contains(Keys.SharedPreferenceKeys.USERS_LIST)) {
                    onlineUsers = new JSONObject(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.USERS_LIST));
                } else {
                    onlineUsers = new JSONObject();
                }
                String channel = "";
                Iterator<String> keys = onlineUsers.keys();
                templist.clear();
                while (keys.hasNext()) {
                    JSONObject user = onlineUsers.getJSONObject(keys.next().toString());
                    String username = user.getString("n");
                    channel = "";
                    if (user.has("ch")) {
                        channel = user.getString("ch");
                    }
                    ChatUserListVO vo=new ChatUserListVO();
                    vo.setUsername(username);
                    vo.setId(user.getInt("id"));
                    vo.setStatusMessage(user.getString("m"));
                    vo.setStatus(user.getString("s"));
                    vo.setChannel(channel);
                    vo.setLastmessage(user.getString("m"));
                    templist.add(vo);

                }
                appsingleton.tempchatuserlist.clear();
                appsingleton.tempchatuserlist.addAll(templist);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ChatListActivity.MyChatUserRequestReceiver.PROCESS_RESPONSE);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                sendBroadcast(broadcastIntent);



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void refreshItems() {
        // Load items
        // ...
        // Load complete
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onItemsLoadComplete();
            }
        }, 1000);
        try{
            getDatabaseList();
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
        try{
            appsingleton.initDialog(this);
            //appsingleton.showDialog();
            CometChatSubscribe();
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        // Stop refresh animation
        try{
            if(mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
        try{
            if(emptySwipeRefreshLayout.isRefreshing())
                emptySwipeRefreshLayout.setRefreshing(false);
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }
    /*
    * function to hide show toolbar layout
    */
    public void HideShowToolbarlayout(boolean isshow) {
        try {
            if (isshow) {
                iv_delete.setVisibility(View.GONE);
                rl_setting.setVisibility(View.GONE);
                rl_clearalllayout.setVisibility(View.VISIBLE);
                rlcanclelayout.setVisibility(View.VISIBLE);
                rl_selecteditem.setVisibility(View.VISIBLE);
                updateCount();
            } else {
                iv_delete.setVisibility(View.VISIBLE);
                rl_setting.setVisibility(View.VISIBLE);
                rl_clearalllayout.setVisibility(View.GONE);
                rlcanclelayout.setVisibility(View.GONE);
                rl_selecteditem.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
    *Get data from database
    */
    public void getDatabaseList()
    {
        try{
            ArrayList<ChatUserListVO> list=appsingleton.mDatabase.getAllConnections();
            if(list.size()>0)
            {
                for (int i = 0; i <list.size() ; i++) {
                    try{
                        ChatUserListVO vo=list.get(i);
                        SingleChatMessage msg=appsingleton.chatdbhelper.getLastMessage(appsingleton.getUserid(),Integer.parseInt(vo.userid));
                        list.get(i).setLastmessage(msg.getMessage());
                        list.get(i).setLastmessage_time(msg.getTimestamp());
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }//end of for


            }//end of list
            reloadListData(list);
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }

    /*
    * Refresh list without changing current position
    */
    public void reloadListData(ArrayList<ChatUserListVO> list) {
        try {
            chatuserlist = list;
            tempachatuserlist.clear();
            tempachatuserlist.addAll(chatuserlist);
            //isShowInviteLayout(false);
            if (list.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                rl_nocontacts.setVisibility(View.VISIBLE);
                emptySwipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                rl_nocontacts.setVisibility(View.GONE);
                emptySwipeRefreshLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);

                if (mAdapter != null) {
                    mAdapter.reload(list);
                } else {
                    bindList(list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /**
     * Bind all orders to list view
     */
    public void bindList(ArrayList<ChatUserListVO> list) {
        try {
            chatuserlist = list;
            if (list.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                rl_nocontacts.setVisibility(View.VISIBLE);
                emptySwipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                rl_nocontacts.setVisibility(View.GONE);
                emptySwipeRefreshLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);

                SetCustomAdapter(list);
            }//end of else
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }
    /*
    *function to update count
    */
    public void updateCount() {
        try {
            int count = 0;
            for (ChatUserListVO vo : tempachatuserlist) {
                if (vo.isselected) {
                    count++;
                }
                tv_selectecount.setText("" + count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }


    /*
     * set adapter
     */
    public void SetCustomAdapter(ArrayList<ChatUserListVO> zaparoundlist) {
        try {
            // Creating Adapter object
            mAdapter = new ChatUserListSwipAdapter(this, zaparoundlist);

            // Setting Mode to Single to reveal bottom View for one item in List
            // Setting Mode to Mutliple to reveal bottom Views for multile items in List
            ((ChatUserListSwipAdapter) mAdapter).setMode(Attributes.Mode.Single);

            mRecyclerView.setAdapter(mAdapter);
            YoYo.with(Techniques.FadeInUp).duration(appsingleton.animationduration).interpolate(new AccelerateDecelerateInterpolator()).playOn(mRecyclerView);
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }


    /*
    *function to change fouus
    */
    public void changeFouus()
    {
        ((EditText)findViewById(R.id.et_samplefocus)).requestFocus();
//        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        mgr.showSoftInput(((EditText)findViewById(R.id.et_samplefocus)), InputMethodManager.RESULT_UNCHANGED_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(((EditText)findViewById(R.id.et_samplefocus)).getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        try{
            if(searchedittext.hasFocus())
            {
                searchedittext.setText("");
                changeFouus();
            }
            else
                super.onBackPressed();

        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }

    }
    //----------------------------------------------
    public class MyChatUserRequestReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "com.zaparound.intent.action.CHATUSERRECEIVER";

        @Override
        public void onReceive(Context context, Intent intent) {
//             String responseString = intent.getStringExtra(ZapWhosIntentService.RESPONSE_STRING);
//             String reponseMessage = intent.getStringExtra(ZapWhosIntentService.RESPONSE_MESSAGE);
            try{
//                if(appsingleton.sharedPreferences.getBoolean("CHANNELUPDATE", true))
//                {
                ArrayList<ChatUserListVO> templist = new ArrayList<>();
                templist.addAll(appsingleton.tempchatuserlist);

                for (int i = 0; i <chatuserlist.size() ; i++) {
                    ChatUserListVO  vo=chatuserlist.get(i);
                    for (int j = 0; j <templist.size() ; j++) {
                        ChatUserListVO  vo2=templist.get(j);
                        if(vo.getUserid().equalsIgnoreCase(""+vo2.getId()))
                        {
                            try{
                                if(vo.getChannel().equals("")) {
                                    vo.setChannel(vo2.getChannel());
                                    appsingleton.mDatabase.updateConnectionDetails(vo);
                                    chatuserlist.get(i).setChannel(vo2.getChannel());

                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }
                            chatuserlist.get(i).setStatus(vo2.getStatus());
                            chatuserlist.get(i).setStatusMessage(vo2.getStatusMessage());
                        }
                    }//end of for
                }//end of for
//                    SharedPreferences.Editor editor = appsingleton.sharedPreferences.edit();
//                    editor.putBoolean("CHANNELUPDATE", false);
//                    editor.apply();
//                }//end of if

                //reloadListData(templist);
                if(mAdapter!=null)
                mAdapter.notifyDataSetChanged();
                appsingleton.dismissDialog();
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }

        }
    }
    //-----------------------------------------------

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendAcceptNotification(String messagetitle,String messageBody) {
        Intent intent = new Intent(this, LandingActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    public void hidekeyboard()
    {
        try{
            // Check if no view has focus:
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of hide focus
}//end of Activity
