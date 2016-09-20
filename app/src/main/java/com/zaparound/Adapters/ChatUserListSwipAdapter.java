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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.squareup.picasso.Picasso;
import com.zaparound.BlankActivity;
import com.zaparound.ChatListActivity;
import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.InviteFriendsVO;
import com.zaparound.ProfileImageActivity;
import com.zaparound.R;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.UserChatWindowActivity;

import java.util.ArrayList;

public class ChatUserListSwipAdapter extends RecyclerSwipeAdapter<ChatUserListSwipAdapter.SimpleViewHolder> {
    private Context mContext;
    private ArrayList<SimpleViewHolder> viewholderlist;
    Appsingleton appsingleton;
    SimpleViewHolder viewHolder;
    private ArrayList<ChatUserListVO> zaparoundlist;
    public ChatListActivity activity;
    public boolean isshowDelete;

    public ChatUserListSwipAdapter(Context context, ArrayList<ChatUserListVO> objects) {
        this.mContext = context;
        this.zaparoundlist = objects;
        viewholderlist = new ArrayList<>();
        appsingleton = Appsingleton.getinstance(context);
        activity = (ChatListActivity) context;
        isshowDelete = false;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_row_adapterxml, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        try {
            final ChatUserListVO item = zaparoundlist.get(position);
            this.viewHolder = viewHolder;
            viewholderlist.add(position, viewHolder);
            viewHolder.tv_itemtitle.setText((item.getUsername()));
            if(item.getLastmessage()!=null)
            viewHolder.tvlastmessage.setText(item.getLastmessage());
             else
            viewHolder.tvlastmessage.setText(item.getStatusMessage());

            viewHolder.tvtime.setText(appsingleton.formate_Date(item.getLastmessage_time()));

            //set custom fonts
            viewHolder.tv_itemtitle.setTypeface(appsingleton.regulartype);
            viewHolder.tvclear.setTypeface(appsingleton.regulartype);
            viewHolder.tv_Chatmessage.setTypeface(appsingleton.regulartype);
            viewHolder.tvlastmessage.setTypeface(appsingleton.regulartype);
            viewHolder.tvtime.setTypeface(appsingleton.lighttype);

            Picasso.with(mContext).load(item.getUser_selfie_thumb()).resize(200, 200).centerCrop().placeholder(R.drawable.zaparound_gray_icon).into(viewHolder.iv_checkinprofile);

            //viewHolder.tvclear.setText(""+mContext.getResources().getString(R.string.st_deny));
            viewHolder.iv_checkinprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!item.getProfileurl().equalsIgnoreCase("")) {
                            Intent intent = new Intent(mContext, ProfileImageActivity.class);
                            intent.putExtra("IMAGEURL", item.getProfileurl());
                            mContext.startActivity(intent);
                        }
                        //mContext.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
            if (item.getStatus().trim().equalsIgnoreCase("available")) {
                viewHolder.iv_liveicon.setVisibility(View.VISIBLE);
                //viewHolder.iv_checkinprofile.setImageResource(R.drawable.amber_heard);
            } else {
                viewHolder.iv_liveicon.setVisibility(View.INVISIBLE);
                //viewHolder.iv_checkinprofile.setImageResource(R.drawable.jhonnydan);
            }

            if (item.isselected) {
                viewHolder.tb_status.setChecked(true);
            } else {
                viewHolder.tb_status.setChecked(false);
            }

            if (isshowDelete) {
                viewHolder.rl_toggleclicked.setVisibility(View.VISIBLE);
                viewHolder.tb_status.setVisibility(View.VISIBLE);
            } else {
                viewHolder.rl_toggleclicked.setVisibility(View.GONE);
                viewHolder.tb_status.setVisibility(View.GONE);
            }

            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            // Drag From Left
            viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

            // Drag From Right
            viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));


            // Handling different events when swiping
            viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    //when the SurfaceView totally cover the BottomView.
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    //when the BottomView totally show.
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    //when user's hand released.
                }
            });

            viewHolder.swipeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if ((((SwipeLayout) v).getOpenStatus() == SwipeLayout.Status.Close)) {
                        //Start your activity

                        Toast.makeText(mContext, " onClick : " + item.getUsername() + " \n" + item.getUsername(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

            viewHolder.rl_toggleclicked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (viewHolder.tb_status.isChecked()) {
                            viewHolder.tb_status.setChecked(false);
                            zaparoundlist.get(position).setIsselected(false);
                            notifyItemChanged(position);
                            activity.updateCount();
                        } else {
                            viewHolder.tb_status.setChecked(true);
                            zaparoundlist.get(position).setIsselected(true);
                            notifyItemChanged(position);
                            activity.updateCount();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }

                }
            });

            viewHolder.rl_maillayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//                        if ((viewHolder.swipeLayout.getOpenStatus() == SwipeLayout.Status.Close)) {
//                            Intent intent=new Intent(mContext, BlankActivity.class);
//                            mContext.startActivity(intent);
//                        }
                        activity.changeFouus();
                    } catch (Exception e) {
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                    try {
                        UserList(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            viewHolder.rl_maillayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        viewHolder.tb_status.setChecked(true);
                        zaparoundlist.get(position).setIsselected(true);
                        notifyDataSetChanged();
                        HideshowAdapterView(true);
                        activity.HideShowToolbarlayout(true);

                    } catch (Exception e) {
                        appsingleton.ToastMessage("" + e.getMessage());
                        e.printStackTrace();
                    }
                    return false;
                }
            });
            viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(mContext, " onClick : " + item.getUsername() + " \n" + item.getUsername(), Toast.LENGTH_SHORT).show();
                }
            });


            viewHolder.tvclear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        try{
                            appsingleton.chatdbhelper.deletemessagesbyId(item.getUserid(),""+appsingleton.getUserid());

                            ChatListActivity activity=(ChatListActivity)mContext;
                            activity.getDatabaseList();
                            activity.CometChatSubscribe();
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
                        }catch(Exception e){
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }

                        mItemManger.closeAllItems();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
            viewHolder.tv_Chatmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        UserList(item);
                        viewHolder.swipeLayout.close();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            // mItemManger is member in RecyclerSwipeAdapter Class
            mItemManger.bindView(viewHolder.itemView, position);


        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }


    }//end of get view

    /*
    *function to hideShow multipal delete
    */
    public void HideshowAdapterView(boolean isshow) {
        try {
            if (isshow) {
                for (int i = 0; i < viewholderlist.size(); i++) {
                    SimpleViewHolder viewHolder = viewholderlist.get(i);
                    viewHolder.rl_toggleclicked.setVisibility(View.VISIBLE);
                    viewHolder.tb_status.setVisibility(View.VISIBLE);
                    isshowDelete = true;
                } //end of for
            } else {

                for (int i = 0; i < viewholderlist.size(); i++) {
                    SimpleViewHolder viewHolder = viewholderlist.get(i);
                    viewHolder.rl_toggleclicked.setVisibility(View.GONE);
                    viewHolder.tb_status.setVisibility(View.GONE);
                    isshowDelete = false;
                    viewHolder.tb_status.setChecked(false);
                    zaparoundlist.get(i).setIsselected(false);
                } //end of for
                notifyDataSetChanged();
                viewholderlist.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//end of hide show

    /*
    *function to delete multipal rows
    */
    public void deleteUser() {
        try {
            for (int i = 0; i < zaparoundlist.size(); i++) {

                if (zaparoundlist.get(i).isselected) {
                    mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                    zaparoundlist.remove(i);
                    notifyItemRemoved(i);
                    notifyItemRangeChanged(i, zaparoundlist.size());
                    i = -1;
                }
            }
            mItemManger.closeAllItems();
            activity.updateCount();

        } catch (Exception e) {
            activity.updateCount();
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of delete selected user user

    /*
    *function to delete all users
    */
    public void clearAll() {
        try {
            zaparoundlist.clear();
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }//end of clear

    public void reload(ArrayList<ChatUserListVO> post) {
        try {
            zaparoundlist.clear();
            zaparoundlist.addAll(post);
            //count=0;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return zaparoundlist.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    //  ViewHolder Class
    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        public TextView tv_itemtitle, tvclear, tvlastmessage, tvtime,tv_Chatmessage;
        public ImageView iv_checkinprofile, iv_liveicon;
        public RelativeLayout rl_maillayout, rl_backcolor, rl_toggleclicked;
        public ToggleButton tb_status;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tv_itemtitle = (TextView) itemView.findViewById(R.id.tv_checkintitle);
            tvclear = (TextView) itemView.findViewById(R.id.tvclear);
            tv_Chatmessage = (TextView) itemView.findViewById(R.id.tvchatmessage);
            tvlastmessage = (TextView) itemView.findViewById(R.id.tv_lastmessage);
            iv_checkinprofile = (ImageView) itemView.findViewById(R.id.iv_checkinlogo);
            iv_liveicon = (ImageView) itemView.findViewById(R.id.iv_liveicon);
            rl_maillayout = (RelativeLayout) itemView.findViewById(R.id.rl_checkin_adaptermaillayout);
            rl_backcolor = (RelativeLayout) itemView.findViewById(R.id.rl_backcolor);
            rl_toggleclicked = (RelativeLayout) itemView.findViewById(R.id.rl_toggleclick);
            tvtime = (TextView) itemView.findViewById(R.id.tvtime);
            tb_status = (ToggleButton) itemView.findViewById(R.id.chkState);
        }
    }

    private void UserList(ChatUserListVO vo) {
        try {
            Intent intent = new Intent(mContext, UserChatWindowActivity.class);
//            intent.putExtra("user_id", vo.getId());
//            intent.putExtra("user_name", vo.getUsername());
//            intent.putExtra("channel", vo.getChannel());
//            intent.putExtra("statusmessage", vo.getStatusMessage());
            intent.putExtra("USERCHATVO", vo);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }

    /*
* to dosplay dialog
* */
    public void showDialog(String title, String message, String btnleft, String btnright, int imageid, final int position, final String operation) {
        try {
            final Dialog dialog = new Dialog(mContext);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.pop_up_check_out);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tv_msg, tv_title;
            ImageView iv_close, iv_middle;
            Button bt_yes, bt_no;
            tv_title = (TextView) dialog.findViewById(R.id.tv_popuptitle);
            tv_msg = (TextView) dialog.findViewById(R.id.tv_popupsubtitle);
            iv_close = (ImageView) dialog.findViewById(R.id.iv_Crossimage);
            iv_middle = (ImageView) dialog.findViewById(R.id.iv_middleimage);
            bt_yes = (Button) dialog.findViewById(R.id.btn_yes);
            bt_no = (Button) dialog.findViewById(R.id.btn_no);

             /*Assining fonts*/
            tv_msg.setTypeface(appsingleton.lighttype);
            tv_title.setTypeface(appsingleton.regulartype);
            bt_yes.setTypeface(appsingleton.regulartype);
            bt_no.setTypeface(appsingleton.regulartype);

            try {
                tv_title.setText("" + title);
                tv_msg.setText("" + message);
                bt_yes.setText("" + btnright);
                bt_no.setText("" + btnleft);
                iv_middle.setImageResource(imageid);
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
                    dialog.dismiss();
                }
            });

            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operation.equalsIgnoreCase("clear")) {
                        clearAll();
                        activity.HideShowToolbarlayout(false);
                        activity.changeFouus();
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of show dialog
}
