package com.zaparound;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.inscripts.Keyboards.SmileyKeyBoard;
import com.inscripts.Keyboards.StickerKeyboard;
import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.EmojiClickInterface;
import com.inscripts.interfaces.StickerClickInterface;
import com.inscripts.utils.Logger;
import com.squareup.picasso.Picasso;
import com.zaparound.Adapters.SingleChatAdapter;
import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.InterestVO;
import com.zaparound.ModelVo.SingleChatMessage;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.helper.Keys;
import com.zaparound.helper.SharedPreferenceHelper;
import com.zaparound.helper.Utils;
import org.json.JSONObject;
import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class UserChatWindowActivity extends ActionBarActivity implements EmojiClickInterface {
    public Appsingleton appsingleton;
    private long friendId;
    public static String friendName, channel,statusmessage;
    private ListView listview;
    private EditText messageField;
    private Button sendButton;
    private ArrayList<SingleChatMessage> messages;
    private SingleChatAdapter adapter;
    private CometChat cometchat;
    private BroadcastReceiver receiver;
    private static Uri fileUri;
    private boolean flag = true;
    private SmileyKeyBoard smiliKeyBoard;
    //private ImageButton smilieyButton, stickerButton;
    private StickerKeyboard stickerKeyboard;
    private TextView stickyViewSpacer;
    private int scrollY; //1
    ChatUserListVO vo =new ChatUserListVO();
    private Map<Integer, Integer> listViewItemHeights = new Hashtable<>();
    Toolbar toolbar;
    RelativeLayout rl_setting,rl_dataclick,rl_imageckilc;
    public TextView tv_toolbartitle, tv_username, tv_city;
    public ImageView iv_userlogo;
    RelativeLayout rl_nocontacts;
    public TextView tv_nocontacts;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hidekeyboard();
        try{
            appsingleton.chatdbhelper.updateReadstatus(vo.getUserid(),appsingleton.getUserid());
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            appsingleton.chatdbhelper.updateReadstatus(vo.getUserid(),appsingleton.getUserid());
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            appsingleton = Appsingleton.getinstance(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.sample_activity_chat);

            appsingleton.activityArrayList.add(this);
            toolbar = (Toolbar) findViewById(R.id.tool_bar1);
            setSupportActionBar(toolbar);
            // add back arrow to toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            rl_setting = (RelativeLayout) toolbar.findViewById(R.id.iv_checksetting);
            tv_toolbartitle = (TextView) toolbar.findViewById(R.id.tv_title);
            tv_toolbartitle.setText(getResources().getString(R.string.st_chat));
            tv_toolbartitle.setTypeface(appsingleton.regulartype);
            appsingleton.setMargins(rl_setting, 0.19, 0, 0, 0);

            //click listeners
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            try{
//                Intent intent = getIntent();
//                friendId = intent.getLongExtra("user_id", 0);
//                friendName = intent.getStringExtra("user_name");
//                channel = intent.getStringExtra("channel");
//                statusmessage = intent.getStringExtra("statusmessage");

                vo = (ChatUserListVO)getIntent().getSerializableExtra("USERCHATVO");
                friendId =Integer.parseInt(vo.getUserid());
                friendName = vo.getUsername();
                channel = vo.getChannel();
                statusmessage = vo.getStatusMessage();
//                vo.setSimilar_interest_name("cricket,football");
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }

		/* Get the singleton CometChat instance for use. */
            cometchat = CometChat.getInstance(getApplicationContext(),
                    SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));

            //getSupportActionBar().setTitle("Chat with " + friendName);
            messages = new ArrayList<>();
            listview = (ListView) findViewById(R.id.listViewChatMessages);
            tv_username = (TextView) findViewById(R.id.tv_usernametitle);
            tv_city = (TextView) findViewById(R.id.tv_city);
            rl_dataclick = (RelativeLayout) findViewById(R.id.rl_dataclick);
            rl_imageckilc = (RelativeLayout) findViewById(R.id.rl_imageclick);
            iv_userlogo = (ImageView) findViewById(R.id.img_round);
            tv_nocontacts= (TextView) findViewById(R.id.tv_logintitle);
            rl_nocontacts = (RelativeLayout) findViewById(R.id.rl_nolocationfoundlayout);


            //set fonts
            tv_username.setTypeface(appsingleton.regulartype);
            tv_city.setTypeface(appsingleton.regulartype);
            tv_nocontacts.setTypeface(appsingleton.regulartype);

            tv_username.setText(friendName);
            tv_city.setText(vo.getPlace_name());
            Picasso.with(this).load(vo.getUser_selfie_thumb()).resize(200, 200).centerCrop().placeholder(R.drawable.zaparound_gray_icon).into(iv_userlogo);

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listHeader = inflater.inflate(R.layout.listview_headerr, null);
            stickyViewSpacer = (TextView) listHeader.findViewById(R.id.header_textt);

        /* Add list view header */
            Calendar c = Calendar.getInstance();


//            SimpleDateFormat df = new SimpleDateFormat("yyyy-M-dd");
//            Date dateobj = new Date();
//            System.out.println(df.format(dateobj));
//
//            String s = df.format(dateobj);
//            SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM dd ");
//            String TotalString;
//            try {
//                TotalString = sdf.format(new SimpleDateFormat("yyyy-M-dd").parse(s));
//                stickyViewSpacer.setText(TotalString.toString());
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            try{
                //listview.addHeaderView(listHeader);
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }



        /* Handle list View scroll events */// Tobe done(Vishal)
            listview.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                /* Check if the first item is already reached to top.*/
                    if (listview.getFirstVisiblePosition() == 0) {
                        View firstChild = listview.getChildAt(0);
                        int topY = 0;
                        if (firstChild != null) {
                            topY = firstChild.getTop();
                        }
                        int heroTopY = stickyViewSpacer.getTop();
                        stickyViewSpacer.setY(Math.max(0, heroTopY + topY));
                    /* Set the image to scroll half of the amount that of ListView */
                    }
                }
            });

            try{
                appsingleton.chatdbhelper.updateReadstatus(vo.getUserid(),appsingleton.getUserid());
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }


            messages = appsingleton.chatdbhelper.getAllMessages(Long.parseLong(SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId)),
                    friendId);

             ArrayList<SingleChatMessage> tempmessages=new ArrayList<>();

             appsingleton.headerdate="2016-05-06 13:01:11";
            for (int i = 0; i < messages.size(); i++) {
                try{

                    SingleChatMessage vo=messages.get(i);

                    if(!appsingleton.checkdates(appsingleton.headerdate,vo.getTimestamp()))
                    {
                        appsingleton.headerdate=vo.getTimestamp();
                        SingleChatMessage tempvo=new SingleChatMessage();
                        tempvo.setIsMyMessage("2");
                        tempvo.setFrom(vo.getFrom());
                        tempvo.setMessage(vo.getMessage());
                        tempvo.setMessageId(vo.getMessageId());
                        tempvo.setMessageType(vo.getMessageType());
                        tempvo.setTickStatus(vo.getTickStatus());
                        tempvo.setTimestamp(vo.getTimestamp());
                        tempvo.setTo(vo.getTo());
                        tempmessages.add(tempvo);
                    }
                    tempmessages.add(vo);

                }catch(Exception e){
                    e.printStackTrace();
                    appsingleton.ToastMessage("" + e.getMessage());
                }

            }//end of for
            messages.clear();
            messages.addAll(tempmessages);
            adapter = new SingleChatAdapter(this, messages);
            listview.setAdapter(adapter);
            if(messages.size()>0)
            {
                rl_nocontacts.setVisibility(View.GONE);
            }


            messageField = (EditText) findViewById(R.id.editTextChatMessage);
            sendButton = (Button) findViewById(R.id.buttonSendMessage);

            //smilieyButton = (ImageButton) findViewById(R.id.buttonSendSmiley);
            // stickerButton = (ImageButton) findViewById(R.id.buttonSendSticker);
            smiliKeyBoard = new SmileyKeyBoard();
            smiliKeyBoard.enable(this, this, R.id.footer_for_emoticons, messageField);
            final RelativeLayout chatFooter = (RelativeLayout) findViewById(R.id.relativeBottomArea);
            smiliKeyBoard.checkKeyboardHeight(chatFooter);
            smiliKeyBoard.enableFooterView(messageField);

            stickerKeyboard = new StickerKeyboard();
            stickerKeyboard.enable(this, new StickerClickInterface() {
                @Override
                public void getClickedSticker(int gridviewItemPosition) {
                    final String data = stickerKeyboard.getClickedSticker(gridviewItemPosition);
                    cometchat.sendSticker(data, String.valueOf(friendId), new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            try {
                                Date date = new Date();
                                String strDateFormat = "yyyy-MM-dd HH:mm:ss";
                                SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                                String formattedDate2 = dateFormat.format(date);


                                SingleChatMessage newmessage = new SingleChatMessage(response.getString("id"), data, formattedDate2, "1", SharedPreferenceHelper
                                        .get(Keys.SharedPreferenceKeys.myId), String.valueOf(friendId), "18",
                                        Keys.MessageTicks.sent);

                                addMessage(newmessage);
                                appsingleton.chatdbhelper.insertOneOnOneMessage(newmessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failCallback(JSONObject response) {

                        }
                    });
                }
            }, R.id.footer_for_emoticons, messageField);
            stickerKeyboard.checkKeyboardHeight(chatFooter);
            //StickerKeyboard.setStickerSize(400);
            stickerKeyboard.enableFooterView(messageField);

            sendButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    final String message = messageField.getText().toString().trim();
                    if (message.length() > 0) {
                        messageField.setText("");

					/* Send a message to the current user */
                        cometchat.sendMessage(String.valueOf(friendId), message, new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                try {

                                    Date date = new Date();
                                    String strDateFormat = "yyyy-MM-dd HH:mm:ss";
                                    SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                                    String formattedDate = dateFormat.format(date);


                                    SingleChatMessage newmessage = new SingleChatMessage(response.getString("id"), message,
                                            formattedDate, "1",
                                            SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId),
                                            String.valueOf(friendId), "10", 1);
                                    if (Utils.msgtoTickList.containsKey(response.getString("id"))) {
                                        newmessage.setTickStatus(Utils.msgtoTickList.get(response.getString("id")));
                                        Utils.msgtoTickList.remove(response.getString("id"));
                                    }
                                    addMessage(newmessage);
                                    appsingleton.chatdbhelper.insertOneOnOneMessage(newmessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Toast.makeText(UserChatWindowActivity.this, "Error in sending message", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    } else {
                        Toast.makeText(UserChatWindowActivity.this, "Blank message", Toast.LENGTH_SHORT).show();
                    }
                }
            });

       /*   smilieyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                smiliKeyBoard.showKeyboard(chatFooter);
            }
        });

      stickerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stickerKeyboard.showKeyboard(chatFooter);
            }
        });

		/* Receiver for updating messages. */
            receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.hasExtra("action")) {
                        String action = intent.getStringExtra("action");
                        ActionBar ab = getSupportActionBar();
                        if (action.equals("typing_start")) {
                            ab.setSubtitle("typing...");
                        } else if (action.equals("typing_stop")) {
                            ab.setSubtitle("");
                        } else if (action.equals("message_deliverd")) {
                            String from = intent.getStringExtra("from");
                            String message_id = intent.getStringExtra("message_id");
                            if (appsingleton.chatdbhelper != null) {
                                // SingleChatMessage msg = dbhelper.getMessageDetails(message_id);
                                for (SingleChatMessage msg : messages) {
                                    if (msg.getMessageId().equals(message_id)) {
                                        msg.setTickStatus(Keys.MessageTicks.deliverd);
                                        appsingleton.chatdbhelper.updateMessageDetails(msg);
                                        adapter.notifyDataSetChanged();
                                        return;
                                    }
                                }
                            }
                        } else if (action.equals("message_read")) {
                            String from = intent.getStringExtra("from");
                            String message_id = intent.getStringExtra("message_id");
                            for (SingleChatMessage msg : messages) {
                                if (message_id.equals("0")) {
                                    // Message id 0 means mark all the message you sent as read
                                    if (msg.getIsMyMessage().equalsIgnoreCase("1")) {
                                        if (msg.getTickStatus() != Keys.MessageTicks.read) {
                                            msg.setTickStatus(Keys.MessageTicks.read);
                                            appsingleton.chatdbhelper.updateMessageDetails(msg);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    if (msg.getMessageId().equals(message_id)) {
                                        if (msg.getIsMyMessage().equalsIgnoreCase("1")) {
                                            msg.setTickStatus(Keys.MessageTicks.read);
                                            appsingleton.chatdbhelper.updateMessageDetails(msg);
                                            adapter.notifyDataSetChanged();
                                            return;
                                        }
                                    }
                                }
                            }
                        }//end of message read
                    } else {
                        int senderId = intent.getIntExtra("user_id", 0);
                        String messageId = intent.getStringExtra("message_id");
                        String from = intent.getStringExtra("from");
                        String to = intent.getStringExtra("to");
                        String messagetype = intent.getStringExtra("message_type");
                        String time = Utils.convertTimestampToDate(Utils.correctTimestamp(Long.parseLong(intent
                                .getStringExtra("time"))));
                        Date date = new Date();
                        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                        time = dateFormat.format(date);
                        SingleChatMessage newMessage = null;
                        if (0 != senderId && senderId == friendId) {

                            String message = intent.getStringExtra("message");
                            newMessage = new SingleChatMessage(messageId, message, time, "0", from, to, messagetype,
                                    Keys.MessageTicks.notick);
                        } else if (intent.hasExtra("myphoto")) {
                            String message = intent.getStringExtra("message");
                            newMessage = new SingleChatMessage(messageId, message, time, "1", from, to, messagetype,
                                    Keys.MessageTicks.sent);
                        } else if (intent.hasExtra("myVideo")) {
                            String message = intent.getStringExtra("message");
                            newMessage = new SingleChatMessage(messageId, message, time, "1", from, to, messagetype,
                                    Keys.MessageTicks.sent);
                        }
                        try{
                            cometchat.sendReadReceipt(messageId, channel, new Callbacks() {
                                @Override
                                public void successCallback(JSONObject response) {

                                }

                                @Override
                                public void failCallback(JSONObject response) {
                                }
                            });
                        }catch(Exception e){
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }

                        if (newMessage != null) {
                            addMessage(newMessage);
                        }
                    }
                }
            };

            flag = true;
            messageField.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try{


                        if (flag) {
                            flag = false;
                            try {
                                cometchat.isTyping(true, channel, new Callbacks() {

                                    @Override
                                    public void successCallback(JSONObject response) {
                                    }

                                    @Override
                                    public void failCallback(JSONObject response) {
                                        //Log.e("abc", "typing fail " + response);
                                    }
                                });
                                Timer timer = new Timer();

						/* Send stop typing message after 5 seconds */
                                timer.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        cometchat.isTyping(false, channel, new Callbacks() {

                                            @Override
                                            public void successCallback(JSONObject response) {

                                            }

                                            @Override
                                            public void failCallback(JSONObject response) {
                                            }
                                        });
                                        flag = true;
                                    }
                                }, 5000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }//end of atertext change
            });
        /*
         * Send read receipt when chat window is open, pass 0 to mark all messages as read
		 * */
            try{
                if (messages.size() > 0) {
                    cometchat.sendReadReceipt("0", channel, new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {

                        }

                        @Override
                        public void failCallback(JSONObject response) {

                        }
                    });
                }
            }catch(Exception e){
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }



            listview.setSelection(adapter.getCount() - 1);

            rl_dataclick.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String title = getResources().getString(R.string.st_similarintrests_text);
                        String message = getResources().getString(R.string.st_Zap_subtex);
                        String leftbtn = getResources().getString(R.string.st_clear);
                        String rightbtn = getResources().getString(R.string.st_message);
                        String time=vo.getTime();
                        String address=vo.getPlace_name();
                        showListDialog(title, vo.getUsername(),address,time, leftbtn, rightbtn, 0, setData(vo.getSimilar_interest_name()),vo);

                    } catch (Exception e) {
                        appsingleton.ToastMessage("" + e.getMessage());
                    }

                }
            });
            rl_imageckilc.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(UserChatWindowActivity.this, ProfileImageActivity.class);
                        intent.putExtra("IMAGEURL", vo.getProfileurl());
                        UserChatWindowActivity.this.startActivity(intent);
                    } catch (Exception e) {
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
    public void getClickedEmoji(int gridviewItemPosition) {
        try{
            smiliKeyBoard.getClickedEmoji(gridviewItemPosition);
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }


    }

    public void addMessage(SingleChatMessage newMessage) {
        try{
            boolean duplicate = false;
            for (SingleChatMessage message : messages) {
                if (message.getMessageId().equals(newMessage.getMessageId())) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {


                if(!appsingleton.checkdates(appsingleton.headerdate,newMessage.getTimestamp()))
                {
                    appsingleton.headerdate=newMessage.getTimestamp();
                    SingleChatMessage tempvo=new SingleChatMessage();
                    tempvo.setIsMyMessage("2");
                    tempvo.setFrom(newMessage.getFrom());
                    tempvo.setMessage(newMessage.getMessage());
                    tempvo.setMessageId(newMessage.getMessageId());
                    tempvo.setMessageType(newMessage.getMessageType());
                    tempvo.setTickStatus(newMessage.getTickStatus());
                    tempvo.setTimestamp(newMessage.getTimestamp());
                    tempvo.setTo(newMessage.getTo());
                    messages.add(tempvo);
                }

                messages.add(newMessage);
                adapter.notifyDataSetChanged();
                listview.setSelection(adapter.getCount() - 1);
            }
            if(messages.size()>0)
            {
                rl_nocontacts.setVisibility(View.GONE);
            }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            registerReceiver(receiver, new IntentFilter("NEW_SINGLE_MESSAGE"));
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            unregisterReceiver(receiver);
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.single_chat, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//
//            case R.id.action_call_user:
//
//            case R.id.action_start_broadcast:
//
//                break;
//            case R.id.action_block_user:
//                cometchat.blockUser(String.valueOf(friendId), new Callbacks() {
//
//                    @Override
//                    public void successCallback(JSONObject response) {
//                        finish();
//                    }
//
//                    @Override
//                    public void failCallback(JSONObject response) {
//                    }
//                });
//                break;
//            case R.id.action_send_image:
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.desert);
//                cometchat.sendImage(bitmap, String.valueOf(friendId), new Callbacks() {
//
//                    @Override
//                    public void successCallback(JSONObject response) {
//                    }
//
//                    @Override
//                    public void failCallback(JSONObject response) {
//                        Toast.makeText(getApplicationContext(), "Image send failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                break;
//            case R.id.action_pick_image:
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
//                startActivityForResult(intent, 1);
//                break;
//
//            case R.id.action_capture_photo:
//                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                fileUri = Utils.getOutputMediaFile(1, false);
//                intent1.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                startActivityForResult(intent1, 2);
//                break;
//            case R.id.action_capture_video:
//                Intent intent2 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                fileUri = Utils.getOutputMediaFile(2, false);
//                intent2.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//
//                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
//                intent2.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 15000000); // 15 Mb
//                startActivityForResult(intent2, 3);
//                break;
//            case R.id.action_share_audio:
//                AssetManager mgr = this.getAssets();
//                try {
//                    AssetManager am = getAssets();
//                    InputStream inputStream = am.open("song.aac");
//                    File file = new File(getCacheDir() + "/song.aac");
//
//                    try {
//                        OutputStream outputStream = new FileOutputStream(file);
//                        byte buffer[] = new byte[1024];
//                        int length = 0;
//
//                        while ((length = inputStream.read(buffer)) > 0) {
//                            outputStream.write(buffer, 0, length);
//                        }
//
//                        outputStream.close();
//                        inputStream.close();
//
//                    } catch (IOException e) {
//
//                    }
//                    cometchat.sendAudioFile(file, String.valueOf(friendId), new Callbacks() {
//
//                        @Override
//                        public void successCallback(JSONObject response) {
//                            try {
//                                Logger.error("success " + response);
//                                SingleChatMessage newmessage = new SingleChatMessage(response.getString("id"), response
//                                        .getString("original_file"), Utils.convertTimestampToDate(System
//                                        .currentTimeMillis()), true, SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.myId),
//                                        String.valueOf(friendId), "16", Keys.MessageTicks.sent);
//                                addMessage(newmessage);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void failCallback(JSONObject response) {
//                            Logger.error("fail " + response);
//                        }
//                    });
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.action_share_video:
//                Intent intent3 = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent3.setType("video/*");
//                startActivityForResult(intent3, 4);
//                break;
//
//            case R.id.action_share_file:
//                Intent ii = new Intent(Intent.ACTION_GET_CONTENT);
//                ii.setType("File/*");
//                startActivityForResult(ii, 1234);
//                break;
//            case R.id.action_whiteboard:
//                cometchat.sendWhiteBoardRequest(String.valueOf(friendId), new Callbacks() {
//                    @Override
//                    public void successCallback(JSONObject jsonObject) {
//                        Log.e("abc", "url " + jsonObject);
//                        try {
//                            Log.e("abc", "response " + jsonObject);
////                            Intent i = new Intent(SampleSingleChatActivity.this, WebviewActivity.class);
////                            i.putExtra("webview_url", jsonObject.get("whiteboard_url").toString());
////                            startActivity(i);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void failCallback(JSONObject jsonObject) {
//
//                    }
//                });
//            case R.id.action_writeboard:
//                cometchat.sendWriteBoardRequest(String.valueOf(friendId), new Callbacks() {
//                    @Override
//                    public void successCallback(JSONObject jsonObject) {
//                        try {
//                            Log.e("abc", "response " + jsonObject);
////                            Intent i = new Intent(SampleSingleChatActivity.this, WebviewActivity.class);
////                            i.putExtra("webview_url", jsonObject.get("writeboard_url").toString());
////                            startActivity(i);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void failCallback(JSONObject jsonObject) {
//
//                    }
//                });
//            case R.id.action_reportconversation:
//                cometchat.reportConversation(String.valueOf(friendId), "This is reason", new Callbacks() {
//                    @Override
//                    public void successCallback(JSONObject jsonObject) {
//
//                    }
//
//                    @Override
//                    public void failCallback(JSONObject jsonObject) {
//
//                    }
//                });
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    String path = Utils.getPath(data.getData(), true);

                    cometchat.sendImage(new File(path), String.valueOf(friendId), new Callbacks() {

                        @Override
                        public void successCallback(JSONObject response) {
                            Logger.debug("share video send succes = " + response);
                        }

                        @Override
                        public void failCallback(JSONObject response) {
                            Logger.debug("share video send fail = " + response);
                        }
                    });
                } else if (requestCode == 2) {
                    // Uri selectedImageUri = data.getData();
                    File newfile = new File(new URI(fileUri.toString()));
                    String filePath = newfile.getAbsolutePath();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap image = BitmapFactory.decodeFile(filePath, options);
                    if (image != null) {
                        cometchat.sendImage(image, String.valueOf(friendId), new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Logger.debug("image send succes = " + response);
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Logger.debug("image send fail = " + response);

                            }
                        });
                    }
                } else if (requestCode == 3) {
                    try {
                        File newfile = new File(new URI(fileUri.toString()));
                        cometchat.sendVideo(newfile, String.valueOf(friendId), new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Logger.debug("video send succes = " + response);
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Logger.debug("video send fail = " + response);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == 4) {
                    try {
                        String path = Utils.getPath(data.getData(), false);
                        cometchat.sendVideo(path, String.valueOf(friendId), new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Logger.debug("share video send succes = " + response);
                            }

                            @Override
                            public void failCallback(JSONObject response) {
                                Logger.debug("share video send fail = " + response);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == 1234) {
                    try {
                        File f = new File(data.getData().getPath());
                        cometchat.sendFile(f, String.valueOf(friendId), new Callbacks() {

                            @Override
                            public void successCallback(JSONObject response) {
                                Logger.error("success " + response);
                                try {
                                    SingleChatMessage newmessage = new SingleChatMessage(response.getString("id"),
                                            response.getString("original_file"), Utils.convertTimestampToDate(System
                                            .currentTimeMillis()), "1", SharedPreferenceHelper
                                            .get(Keys.SharedPreferenceKeys.myId), String.valueOf(friendId), "17",
                                            Keys.MessageTicks.sent);
                                    addMessage(newmessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failCallback(JSONObject response) {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of activity Result
    /*
          * to dosplay dialog
          * */
    public void showListDialog(String title, String message, String address, String time, String btnleft, String btnright, final int position, ArrayList<InterestVO> intrestlist, final ChatUserListVO vo) {
        try {
            final Dialog dialog = new Dialog(this);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.friends_chat_layout);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tv_msg, tv_title, tv_username, tv_agetext, agecount,tv_address,tv_time;
            ImageView iv_close, iv_middle,iv_gender,iv_frindimage;
            Button bt_yes, bt_no;
            RelativeLayout gridlayout, agelayout;
            tv_title = (TextView) dialog.findViewById(R.id.tv_popuptitle);
            tv_msg = (TextView) dialog.findViewById(R.id.tv_popupsubtitle);
            iv_close = (ImageView) dialog.findViewById(R.id.iv_Crossimage);
            iv_middle = (ImageView) dialog.findViewById(R.id.iv_middleimage);
            iv_gender = (ImageView) dialog.findViewById(R.id.iv_popupgendericon);
            bt_yes = (Button) dialog.findViewById(R.id.btn_yes);
            bt_no = (Button) dialog.findViewById(R.id.btn_no);
            gridlayout = (RelativeLayout) dialog.findViewById(R.id.rl_gridlayout);
            agelayout = (RelativeLayout) dialog.findViewById(R.id.rl_agelayout);
            tv_username = (TextView) dialog.findViewById(R.id.tv_userid);
            tv_agetext = (TextView) dialog.findViewById(R.id.tvage);
            agecount = (TextView) dialog.findViewById(R.id.tvagecount);
            tv_address = (TextView) dialog.findViewById(R.id.tv_address);
            tv_time = (TextView) dialog.findViewById(R.id.tv_time);
            iv_frindimage = (ImageView) dialog.findViewById(R.id.iv_frindimage);

             /*Assining fonts*/
            tv_msg.setTypeface(appsingleton.lighttype);
            tv_title.setTypeface(appsingleton.regulartype);
            tv_username.setTypeface(appsingleton.regulartype);
            bt_yes.setTypeface(appsingleton.regulartype);
            bt_no.setTypeface(appsingleton.regulartype);
            tv_agetext.setTypeface(appsingleton.regulartype);
            agecount.setTypeface(appsingleton.regulartype);
            tv_time.setTypeface(appsingleton.lighttype);
            tv_address.setTypeface(appsingleton.regulartype);

            try {
                tv_title.setText(title);
                tv_msg.setText(message);
                tv_username.setText(message);
                bt_yes.setText(btnright);
                bt_no.setText(btnleft);
                agecount.setText(vo.getAge());
                if(!time.equalsIgnoreCase(""))
                    tv_time.setText(appsingleton.formate_Header_Date(time));
                if(!address.equalsIgnoreCase(""))
                    tv_address.setText(address);
                Picasso.with(this).load(vo.getUser_own_selfithumb()).resize(200, 200).centerCrop().placeholder(R.drawable.dummy_user_icon).into(iv_middle);
                Picasso.with(this).load(vo.getProfileurl()).resize(400, 400).centerCrop().placeholder(R.drawable.dummy_user_icon_big).into(iv_frindimage);

                if (vo.getShow_age().equals("1")) {
                    agelayout.setVisibility(View.GONE);
                } else {
                    agelayout.setVisibility(View.GONE);
                }

                if (vo.getGender().equalsIgnoreCase("m")) {
                    iv_gender.setImageResource(R.drawable.colormale_icon);
                } else {
                    iv_gender.setImageResource(R.drawable.colorfemale_icon);
                }
                if(intrestlist!=null) {
                    if (intrestlist.size() <= 0) {
                        tv_title.setText(this.getResources().getString(R.string.st_NOsimilarintrests_text));
                    }
                }
                else
                    tv_title.setText(this.getResources().getString(R.string.st_NOsimilarintrests_text));
            } catch (Exception e) {
                appsingleton.ToastMessage("" + e.getMessage());
            }
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            bt_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        //ZapClearOperation("clear",position);
                        dialog.dismiss();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        //ZapClearOperation("zap",position);
                        dialog.dismiss();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
            iv_frindimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent = new Intent(UserChatWindowActivity.this, ProfileImageActivity.class);
                        intent.putExtra("IMAGEURL", vo.getProfileurl());
                        startActivity(intent);
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
            iv_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent = new Intent(UserChatWindowActivity.this, ProfileImageActivity.class);
                        intent.putExtra("IMAGEURL", vo.getUser_own_selfi());
                        startActivity(intent);
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });


            dialog.show();
            appsingleton.addIntrestGrid(this, gridlayout, intrestlist);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of show dialog


    public ArrayList<InterestVO> setData(String str) {
        ArrayList<InterestVO> list = new ArrayList<InterestVO>();
        list.clear();
        int x = 0;
        ArrayList aList= new ArrayList(Arrays.asList(str.split(",")));
        for(int i=0;i<aList.size();i++)
        {   if(!((""+aList.get(i)).equals("")))
        {
            InterestVO vo = new InterestVO(++x, "" + aList.get(i), "", "", false);
            list.add(vo);
        }
        }
        return list;
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
