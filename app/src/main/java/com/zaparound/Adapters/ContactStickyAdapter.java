package com.zaparound.Adapters;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zaparound.ContactsActivity;
import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.CheckinLocationListVO;
import com.zaparound.ModelVo.CheckinZaparoundVO;
import com.zaparound.ModelVo.ContactStickyVO;
import com.zaparound.ModelVo.InterestVO;
import com.zaparound.ProfileImageActivity;
import com.zaparound.R;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.UserChatWindowActivity;
import com.zaparound.ViewHolders.ContactsStickyListHolder;

import java.util.ArrayList;
import java.util.Arrays;

public class ContactStickyAdapter extends RecyclerView.Adapter<ContactsStickyListHolder> {

    private ArrayList<ChatUserListVO> labels;
    public Context context;
    Appsingleton appsingleton;
    public ContactStickyAdapter(Context context, ArrayList<ChatUserListVO> labels) {
        this.context = context;
        this.labels = labels;
        appsingleton = Appsingleton.getinstance(context);
    }

    @Override
    public ContactsStickyListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_stickyheader_adapter, parent, false);
        return new ContactsStickyListHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactsStickyListHolder holder, final int position) {
        try {
            final ChatUserListVO item=labels.get(position);
            final String itemtitle = item.getUsername();
            final String miles = item.getSimilar_interest_count() + " " + context.getResources().getString(R.string.st_interests);

            holder.tv_itemtitle.setText(itemtitle);
            holder.bt_miles.setText(miles);

            if (item.getGender().equalsIgnoreCase("m")) {
                holder.iv_gendericon.setImageResource(R.drawable.colormale_icon);
                // viewHolder.iv_checkinprofile.setImageResource(R.drawable.jhonnydan);

            } else {
                holder.iv_gendericon.setImageResource(R.drawable.colorfemale_icon);
                //viewHolder.iv_checkinprofile.setImageResource(R.drawable.amber_heard);
            }
            Picasso.with(context).load(item.getUser_selfie_thumb()).resize(200, 200).centerCrop().placeholder(R.drawable.dummy_user_icon).into(holder.iv_checkinprofile);

            holder.iv_checkinprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!item.getProfileurl().equalsIgnoreCase("")) {
                            Intent intent = new Intent(context, ProfileImageActivity.class);
                            intent.putExtra("IMAGEURL", item.getProfileurl());
                            context.startActivity(intent);
                        }
                        //mContext.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            holder.rl_maillayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String title = context.getResources().getString(R.string.st_similarintrests_text);
                        String message = context.getResources().getString(R.string.st_Zap_subtex);
                        String leftbtn = context.getResources().getString(R.string.st_clear);
                        String rightbtn = context.getResources().getString(R.string.st_message);
                        String time=item.getTime();
                        String address=item.getPlace_name();
                        showListDialog(title, item.getUsername(),address,time, leftbtn, rightbtn, position, setData(item.getSimilar_interest_name()),item);

                    } catch (Exception e) {
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void reload(ArrayList<ChatUserListVO> post) {
        try {
            labels.clear();
            labels.addAll(post);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return labels.size();
    }

    /*
   * to dosplay dialog
   * */
    public void showListDialog(String title, String message, String address, String time, String btnleft, String btnright, final int position, ArrayList<InterestVO> intrestlist,final ChatUserListVO vo) {
        try {
            final Dialog dialog = new Dialog(context);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.connection_popuplayout);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tv_msg, tv_title, tv_username, tv_agetext, agecount,tv_address,tv_time;
            ImageView iv_close, iv_middle,iv_gender,iv_block;
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
            iv_block = (ImageView) dialog.findViewById(R.id.iv_blockuser);

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
                tv_time.setText(appsingleton.formate_Header_Date(time));
                tv_address.setText(address);
                Picasso.with(context).load(vo.getUser_selfie_thumb()).resize(200, 200).centerCrop().placeholder(R.drawable.dummy_user_icon).into(iv_middle);

                if (vo.getShow_age().equals("1")) {
                    agelayout.setVisibility(View.GONE);
                } else {
                    agelayout.setVisibility(View.GONE);
                }

                if (vo.getGender().equals("m")) {
                    iv_gender.setImageResource(R.drawable.colormale_icon);
                } else {
                    iv_gender.setImageResource(R.drawable.colorfemale_icon);
                }

                if(intrestlist.size()<=0)
                {
                    tv_title.setText( context.getResources().getString(R.string.st_NOsimilarintrests_text));
                }
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
                        UserList(vo);
                        dialog.dismiss();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
            iv_block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        dialog.dismiss();
                        try{
                            dialog.dismiss();
                            ContactsActivity activity=(ContactsActivity)  context;
                            activity.Blockuser(vo.getUserid());
                        }catch(Exception e){
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
            final ChatUserListVO item=labels.get(position);
            iv_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!item.getProfileurl().equalsIgnoreCase("")) {
                            Intent intent = new Intent(context, ProfileImageActivity.class);
                            intent.putExtra("IMAGEURL", item.getProfileurl());
                            context.startActivity(intent);
                        }
                        //mContext.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            dialog.show();
            appsingleton.addIntrestGrid(context, gridlayout, intrestlist);

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
    private void UserList(ChatUserListVO vo) {
        try {
            Intent intent = new Intent(context, UserChatWindowActivity.class);
            intent.putExtra("USERCHATVO", vo);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

}//end of class