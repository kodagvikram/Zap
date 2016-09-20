package com.zaparound.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.inscripts.Keyboards.StickerKeyboard;
import com.inscripts.custom.EmojiTextView;
import com.squareup.picasso.Picasso;
import com.zaparound.ModelVo.SingleChatMessage;
import com.zaparound.R;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.UserChatWindowActivity;

import java.util.ArrayList;

import static android.graphics.Color.parseColor;

public class SingleChatAdapter extends BaseAdapter {

    private ArrayList<SingleChatMessage> messageList;
    private static final int TYPES_COUNT = 3;
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;
    private static final int TYPE_HEADER = 2;
    private Context context;
    Appsingleton appsingleton;
    SharedPreferences sp;
    String strMyName,strTime;
    StickerKeyboard stickerKeyboard = new StickerKeyboard();
    static int flagMyFirst,flagOppFirst;
    public SingleChatAdapter(Context context, ArrayList<SingleChatMessage> messages) {
        messageList = messages;
        this.context = context;
        flagOppFirst=0;
        flagMyFirst=0;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        strMyName = sp.getString("USER_NAME",null);
        appsingleton=Appsingleton.getinstance(context);
        //appsingleton.headerdate="2016-05-06 13:01:11";
    }

    @Override
    public int getViewTypeCount() {
        return TYPES_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        try {
            SingleChatMessage message = (SingleChatMessage) getItem(position);
            if (message.getIsMyMessage().equalsIgnoreCase("1")) {
                return TYPE_RIGHT;
            }
            else  if (message.getIsMyMessage().equals("0")) {
                return TYPE_LEFT;
            }
            else  if (message.getIsMyMessage().equals("2"))
                return TYPE_HEADER;
        }catch (Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage(e.getMessage());

        }
        return TYPE_LEFT;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (view == null) {
            if (getItemViewType(position) == TYPE_RIGHT) {
                view = LayoutInflater.from(context).inflate(R.layout.oneonone_chat_bubble_right, parent, false);
                holder.message = (EmojiTextView) view.findViewById(R.id.textViewMessage);
                holder.timestamp = (TextView) view.findViewById(R.id.textViewTime);
                holder.image = (ImageView) view.findViewById(R.id.imageViewImageMessage);
                // holder.msgtick = (ImageView) view.findViewById(R.id.imageViewmessageTicks);
                holder.rlchat=(RelativeLayout)view.findViewById(R.id.leftbubbleContainer);
                holder.rlmain=(RelativeLayout)view.findViewById(R.id.id_main);
                holder.txtName = (TextView) view.findViewById(R.id.txt_name);
                holder.txtheaderdate = (TextView) view.findViewById(R.id.header_textt);
                holder.rlheaderdate=(RelativeLayout)view.findViewById(R.id.rl_rowtimelayout);
            }
            else if (getItemViewType(position) == TYPE_HEADER) {
                view = LayoutInflater.from(context).inflate(R.layout.one_on_one_header_layout, parent, false);
                holder.message = (EmojiTextView) view.findViewById(R.id.textViewMessage);
                holder.timestamp = (TextView) view.findViewById(R.id.textViewTime);
                holder.image = (ImageView) view.findViewById(R.id.imageViewImageMessage);
                // holder.msgtick = (ImageView) view.findViewById(R.id.imageViewmessageTicks);
                holder.rlchat=(RelativeLayout)view.findViewById(R.id.leftbubbleContainer);
                holder.rlmain=(RelativeLayout)view.findViewById(R.id.id_main);
                holder.txtName = (TextView) view.findViewById(R.id.txt_name);
                holder.txtheaderdate = (TextView) view.findViewById(R.id.header_textt);
                holder.rlheaderdate=(RelativeLayout)view.findViewById(R.id.rl_rowtimelayout);
            }

            else {
                view = LayoutInflater.from(context).inflate(R.layout.oneonone_chat_bubble_left, parent, false);
                holder.message = (EmojiTextView) view.findViewById(R.id.textViewMessage);
                holder.timestamp = (TextView) view.findViewById(R.id.textViewTime);
                holder.image = (ImageView) view.findViewById(R.id.imageViewImageMessage);
                // holder.msgtick = (ImageView) view.findViewById(R.id.imageViewmessageTicks);
                holder.rlchat=(RelativeLayout)view.findViewById(R.id.leftbubbleContainer);
                holder.rlmain=(RelativeLayout)view.findViewById(R.id.id_main);
                holder.txtName = (TextView) view.findViewById(R.id.txt_name);
                holder.txtheaderdate = (TextView) view.findViewById(R.id.header_textt);
                holder.rlheaderdate=(RelativeLayout)view.findViewById(R.id.rl_rowtimelayout);
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.txtName.setVisibility(View.GONE);

        SingleChatMessage message = messageList.get(position);
        holder.txtheaderdate.setText(appsingleton.formate_Header_Date(message.getTimestamp()));
        String messageType = message.getMessageType();
        strTime=message.getTimestamp();
        //holder.timestamp.setText(message.getTimestamp());
        try {
            String string = appsingleton.formate_Date(message.getTimestamp());
            String string1=""+"Today";
            if(string.contains("Today")){

                string=string.replaceAll(string1,"");

                holder.timestamp.setText(string);
            }else{
                holder.timestamp.setText(string);

            }

        }catch(Exception e){
            e.printStackTrace();
        }
        String MessageFrom = UserChatWindowActivity.friendName.toString();
        //String MessageFrom = message.get;
        if(messageType.equals("10")) {


            if (position == 0) {
                if (message.getIsMyMessage().equalsIgnoreCase("1")) {
                    flagMyFirst = 1;
                    holder.message.setVisibility(View.VISIBLE);
                    holder.message.setText(message.getMessage()+" ");
                    holder.txtName.setVisibility(View.VISIBLE);
                    holder.txtName.setText(strMyName);

                    holder.rlchat.setBackgroundResource(R.drawable.purple_first);


                } else {
                    holder.txtName.setVisibility(View.VISIBLE);
                    holder.txtName.setText(MessageFrom);

                    flagOppFirst = 1;
                    holder.message.setVisibility(View.VISIBLE);
                    holder.message.setText(message.getMessage()+" ");
                    holder.rlchat.setBackgroundResource(R.drawable.white_first);


                }

            } else {

                SingleChatMessage message2 = messageList.get(position - 1);

                if (message.getIsMyMessage().equalsIgnoreCase("1") == false) {

                    if (((message.getIsMyMessage().equalsIgnoreCase("1") == false) && (message2.getIsMyMessage().equalsIgnoreCase("1") == false))==true) {
                        holder.message.setVisibility(View.VISIBLE);
                        holder.message.setText(message.getMessage()+" ");

                        holder.rlchat.setBackgroundResource(R.drawable.white_other);


                    } else {
                        holder.message.setVisibility(View.VISIBLE);
                        holder.message.setText(message.getMessage()+" ");
                        holder.txtName.setVisibility(View.VISIBLE);
                        holder.txtName.setText(MessageFrom);
                        holder.rlchat.setBackgroundResource(R.drawable.white_first);

                    }

                } else {
                   /* if (((message.getIsMyMessage() == true) && (message2.getIsMyMessage() == true)) && (message.getFrom() != message2.getFrom())) {
                        holder.message.setVisibility(View.VISIBLE);
                        holder.message.setText(message.getMessage()+" ");
                        holder.rlchat.setBackgroundResource(R.drawable.other_message_purple);


                    }*/

                    if(((message.getIsMyMessage().equalsIgnoreCase("1") == true) && (message2.getIsMyMessage().equalsIgnoreCase("1") == true))){

                        holder.message.setVisibility(View.VISIBLE);
                        holder.message.setText(message.getMessage()+" ");
                        holder.rlchat.setBackgroundResource(R.drawable.purple_other);

                    }else{
                        holder.message.setVisibility(View.VISIBLE);
                        holder.message.setText(message.getMessage()+" ");
                        holder.txtName.setVisibility(View.VISIBLE);
                        holder.txtName.setText(strMyName);
                        holder.rlchat.setBackgroundResource(R.drawable.purple_first);
                    }


                }
            }
        }else if (messageType.equals("12")) {
            holder.message.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            Picasso.with(context).load(message.getMessage()).into(holder.image);
        } else if (messageType.equals("14") || messageType.equals("16") || messageType.equals("17")) {
            holder.image.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText(message.getMessage());
        } /*else if (messageType.equals("18")) {
            holder.message.setVisibility(View.VISIBLE);
			holder.image.setVisibility(View.GONE);
			holder.message.setText(StickerKeyboard.showSticker(context, message.getMessage()));
		}*/ else if (messageType.equals("19") || messageType.equals("20")) {
            holder.message.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.message.setMovementMethod(LinkMovementMethod.getInstance());
            holder.message.setText(createWebviewLink(message));
        } else if (messageType.equals("21")) {
            holder.message.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.message.setMovementMethod(LinkMovementMethod.getInstance());
            holder.message.setText(createClickableText(message));
        } else {
            holder.message.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            String msg = message.getMessage();
            if (msg.contains("|#|")) {
                holder.message.setMovementMethod(LinkMovementMethod.getInstance());
                holder.message.setText(createClickableText(message));
            } else {
                holder.message.setEmojiText(msg);
            }
        }
        int messtick = message.getTickStatus();
        /*holder.msgtick.setVisibility(View.VISIBLE);
        switch (messtick) {
            case Keys.MessageTicks.sent:
                holder.msgtick.setImageResource(R.drawable.iconsent);
                break;
            case Keys.MessageTicks.deliverd:
                holder.msgtick.setImageResource(R.drawable.icondeliverd);
                break;
            case Keys.MessageTicks.read:
                holder.msgtick.setImageResource(R.drawable.iconread);
                break;
            case Keys.MessageTicks.notick:
                holder.msgtick.setVisibility(View.GONE);
                break;
            default:
                holder.msgtick.setVisibility(View.GONE);
                break;
        }*/


        return view;
    }


    //private TextView friendName;

    private SpannableString createWebviewLink(SingleChatMessage message) {
        final String originalMessage = message.getMessage();
        String msg = "";
        if (message.getMessageType().equals("19")) {
            msg = "Has sent whiteboard request\nClick here to open";
        } else if (message.getMessageType().equals("20")) {
            msg = "Has sent writeboard request\nClick here to open";
        }
        SpannableString mystring = new SpannableString(msg);
        ClickableSpan span = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
//                Intent i = new Intent(context, WebviewActivity.class);
//                i.putExtra("webview_url", originalMessage);
//                context.startActivity(i);

            }
        };
        mystring.setSpan(span, 0, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return mystring;

    }

    private SpannableString createClickableText(final SingleChatMessage message) {
        String msg = message.getMessage();
        if (message.getMessageType().equals("21")) {
            msg = "Has shared screen|#|" + msg;
        }
        int startIndex = msg.indexOf("|#|");
        final String roomName = msg.substring(startIndex + 3);
        msg = msg.substring(0, startIndex) + "\nClick here to Join";
        SpannableString mystring = new SpannableString(msg);
        ClickableSpan span = new ClickableSpan() {

            @Override
            public void onClick(View widget) {

            }
        };
        mystring.setSpan(span, startIndex, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return mystring;


    }

    private class ViewHolder {
        TextView timestamp,txtName,txtheaderdate;
        ImageView image/* msgtick*/;
        EmojiTextView message;
        RelativeLayout rlchat,rlmain,rlheaderdate;
    }

}
