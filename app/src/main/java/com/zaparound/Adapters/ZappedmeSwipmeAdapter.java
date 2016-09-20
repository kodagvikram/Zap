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
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.squareup.picasso.Picasso;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.CheckinTabscriptActivity;
import com.zaparound.CheckinZappedMeFragment;
import com.zaparound.ModelVo.CheckinZaparoundVO;
import com.zaparound.ModelVo.CheckinZapmeVO;
import com.zaparound.ModelVo.InterestVO;
import com.zaparound.ProfileImageActivity;
import com.zaparound.R;
import com.zaparound.Singleton.Appsingleton;

import java.util.ArrayList;
import java.util.Arrays;

public class ZappedmeSwipmeAdapter extends RecyclerSwipeAdapter<ZappedmeSwipmeAdapter.SimpleViewHolder> {
    private Context mContext;
    private ArrayList<CheckinZapmeVO> zaparoundlist;
    Appsingleton appsingleton;
    SimpleViewHolder viewHolder;
    CheckinZappedMeFragment fragment;
    public ZappedmeSwipmeAdapter(Context context,CheckinZappedMeFragment fragment, ArrayList<CheckinZapmeVO> objects) {
        this.mContext = context;
        this.zaparoundlist = objects;
        appsingleton= Appsingleton.getinstance(context);
        this.fragment=fragment;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final CheckinZapmeVO item = zaparoundlist.get(position);

        this.viewHolder= viewHolder;
        viewHolder.tv_itemtitle.setText((item.getUsername()));
        viewHolder.bt_miles.setText(item.getSimilar_interest_count() +" "+ mContext.getResources().getString(R.string.st_interests));


        //set custom fonts
        viewHolder.tv_itemtitle.setTypeface(appsingleton.regulartype);
        viewHolder.tvclear.setTypeface(appsingleton.regulartype);
        viewHolder.tvzap.setTypeface(appsingleton.regulartype);
        viewHolder.bt_miles.setTypeface(appsingleton.lighttype);

        viewHolder.tvzap.setText(mContext.getResources().getString(R.string.st_accept));
        viewHolder.tvclear.setText(mContext.getResources().getString(R.string.st_deny));

        if (item.getGender().equalsIgnoreCase("m")) {
            viewHolder.iv_gendericon.setImageResource(R.drawable.colormale_icon);

        } else {
            viewHolder.iv_gendericon.setImageResource(R.drawable.colorfemale_icon);
        }
        Picasso.with(mContext).load(item.getUser_selfie_thumb()).resize(100, 100).centerCrop().placeholder(R.drawable.dummy_user_icon).into(viewHolder.iv_checkinprofile);


        if(item.getIsread()==1)
        {
            viewHolder.tv_unreadcount.setVisibility(View.VISIBLE);
            viewHolder.rl_backcolor.setBackgroundColor(mContext.getResources().getColor(R.color.rowback_color));
        }
        else
        {
            viewHolder.tv_unreadcount.setVisibility(View.GONE);
            viewHolder.rl_backcolor.setBackgroundColor(mContext.getResources().getColor(R.color.white));
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
                try{

                        if (item.getIsread()==1) {
                            item.setIsread(0);
                            viewHolder.rl_backcolor.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                            viewHolder.tv_unreadcount.setVisibility(View.GONE);
                            notifyDataSetChanged();
                            try{
                                CheckinTabscriptActivity tabscriptActivity=(CheckinTabscriptActivity)mContext;
                                if(tabscriptActivity.getCount()>0)
                                {
                                    int count=tabscriptActivity.getCount();
                                    tabscriptActivity.updateCount(--count);
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }
                            try{
                                fragment.callCheckinZapread(""+appsingleton.getUserid(),item.getRequestid());
                            }catch(Exception e){
                                e.printStackTrace();
                                appsingleton.ToastMessage("" + e.getMessage());
                            }

                         }//end of if
                }catch(Exception e){
                    e.printStackTrace();
                    appsingleton.ToastMessage("" + e.getMessage());
                }
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

                    Toast.makeText(mContext, " onClick : " + item.getUsername() + " \n" + item.getSimilar_interest_name(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        viewHolder.rl_maillayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                if (( viewHolder.swipeLayout.getOpenStatus() == SwipeLayout.Status.Close)) {
                    //Start your activity
                    if (item.getIsread()==1) {
                        item.setIsread(0);
                        viewHolder.rl_backcolor.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        viewHolder.tv_unreadcount.setVisibility(View.GONE);
                        notifyDataSetChanged();
                     try{
                         CheckinTabscriptActivity tabscriptActivity=(CheckinTabscriptActivity)mContext;
                         if(tabscriptActivity.getCount()>0)
                         {
                             int count=tabscriptActivity.getCount();
                             tabscriptActivity.updateCount(--count);
                         }
                    }catch(Exception e){
                         e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                        try{
                            fragment.callCheckinZapread(""+appsingleton.getUserid(),item.getRequestid());
                        }catch(Exception e){
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }
                    }//end of if
                }//end of if
                }catch(Exception e){
                    e.printStackTrace();
                    appsingleton.ToastMessage("" + e.getMessage());
                }

                try {
                    if ((viewHolder.swipeLayout.getOpenStatus() == SwipeLayout.Status.Close)) {
                        //Start your activity
                        try {
                            String title = mContext.getResources().getString(R.string.st_similarintrests_text);
                            String message = mContext.getResources().getString(R.string.st_Zap_subtex);
                            String leftbtn = mContext.getResources().getString(R.string.st_deny_text);
                            String rightbtn = mContext.getResources().getString(R.string.st_accept_text);
                            showListDialog(title, item.getUsername(), leftbtn, rightbtn, position, setData(item.getSimilar_interest_name()),item);

                        } catch (Exception e) {
                            appsingleton.ToastMessage("" + e.getMessage());
                        }

                    }//end of if
                } catch (Exception e) {
                    appsingleton.ToastMessage("" + e.getMessage());
                }

            }
        });

        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, " onClick : " + item.getUsername() + " \n" + item.getSimilar_interest_name(), Toast.LENGTH_SHORT).show();
            }
        });


        viewHolder.tvzap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=mContext.getResources().getString(R.string.st_accept_text);
                String message=mContext.getResources().getString(R.string.st_accept_subtex);
                String leftbtn=mContext.getResources().getString(R.string.st_no);
                String rightbtn=mContext.getResources().getString(R.string.st_yes);
                showDialog(title,message,leftbtn,rightbtn,R.drawable.zap_icon,position,"accept");
            }
        });
        viewHolder.rl_profileimagelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ((viewHolder.swipeLayout.getOpenStatus() == SwipeLayout.Status.Close)) {
                        //Start your activity
                        try {
                            if(!item.getProfileurl().equalsIgnoreCase("")) {
                                Intent intent = new Intent(mContext, ProfileImageActivity.class);
                                intent.putExtra("IMAGEURL", item.getProfileurl());
                                mContext.startActivity(intent);
                            }
                            //mContext.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        } catch (Exception e) {
                            appsingleton.ToastMessage("" + e.getMessage());
                        }

                    }//end of if
                } catch (Exception e) {
                    appsingleton.ToastMessage("" + e.getMessage());
                }
            }
        });

        viewHolder.tvclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title=mContext.getResources().getString(R.string.st_deny_text);
                String message=mContext.getResources().getString(R.string.st_deny_subtex);
                String leftbtn=mContext.getResources().getString(R.string.st_no);
                String rightbtn=mContext.getResources().getString(R.string.st_yes);
                showDialog(title,message,leftbtn,rightbtn,R.drawable.clear_icon,position,"deny");
            }
        });

//        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
//                studentList.remove(position);
//                notifyItemRemoved(position);
//                notifyItemRangeChanged(position, studentList.size());
//                mItemManger.closeAllItems();
//                Toast.makeText(view.getContext(), "Deleted " + viewHolder.tvName.getText().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });


        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(viewHolder.itemView, position);

    }
    public void reload(ArrayList<CheckinZapmeVO> post) {
        try {
            zaparoundlist.clear();
            zaparoundlist.addAll(post);
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
        public TextView tv_itemtitle,tvclear,tvzap,tv_unreadcount;
        public ImageView iv_checkinprofile, iv_gendericon;
        public Button bt_miles;
        public RelativeLayout rl_maillayout, rl_backcolor, rl_profileimagelayout;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tv_itemtitle = (TextView) itemView.findViewById(R.id.tv_checkintitle);
            tvclear = (TextView) itemView.findViewById(R.id.tvclear);
            tvzap = (TextView) itemView.findViewById(R.id.tvzap);
            bt_miles = (Button) itemView.findViewById(R.id.bt_miles);
            iv_checkinprofile = (ImageView) itemView.findViewById(R.id.iv_checkinlogo);
            iv_gendericon = (ImageView) itemView.findViewById(R.id.iv_gendericon);
            rl_maillayout = (RelativeLayout) itemView.findViewById(R.id.rl_checkin_adaptermaillayout);
            rl_backcolor = (RelativeLayout) itemView.findViewById(R.id.rl_backcolor);
            tv_unreadcount = (TextView) itemView.findViewById(R.id.tvunreadcount);
            rl_profileimagelayout = (RelativeLayout) itemView.findViewById(R.id.rl_imageclick);
        }
    }//end of holder

    /*
   *function to perform operation
   */
    public void ZapClearOperation(String operation,int position)
    {
        try{
            if (AppUtils.isNetworkAvailable(mContext)) {
                String placeid = appsingleton.getCheckinPlaceid();
                CheckinZapmeVO vo = zaparoundlist.get(position);
                String user_id_from = "" + appsingleton.getUserid();
                String user_id_to = vo.getUserid();
                String checkinid = "" + appsingleton.getCheckinId();
                String status = "";
                if (operation.equalsIgnoreCase("accept")) {
                    status = "2";
                } else {
                    status = "3";
                }
                fragment.callCheckinrequest(placeid, user_id_from, user_id_to, status,checkinid, position);
            } else {
                appsingleton.SnackbarMessage(mContext.getResources().getString(R.string.st_internet_error), "", viewHolder.swipeLayout);
            }
        }catch(Exception e){
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
    }
    /*
    *function to Remove item with animation
    */
    public void removeItemfromlist(int position,String userid) {
        try {
            mItemManger.removeShownLayouts(viewHolder.swipeLayout);

            //CheckinZapmeVO vo2 = zaparoundlist.get(position);
            try {
                for (int i = 0; i < zaparoundlist.size(); i++) {

                    CheckinZapmeVO vo = zaparoundlist.get(i);
                    if (vo.getUserid().equalsIgnoreCase(userid)) {
                        try {
                            zaparoundlist.remove(i);
                            notifyItemRemoved(i);
                            notifyItemRangeChanged(i, zaparoundlist.size());
                            i=0;
                        } catch (Exception e) {
                            e.printStackTrace();
                            appsingleton.ToastMessage("" + e.getMessage());
                        }
                    }//end of if

                }//end od for
               // notifyDataSetChanged();
            } catch (Exception e) {
                notifyDataSetChanged();
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }


            mItemManger.closeAllItems();
        } catch (Exception e) {
            e.printStackTrace();
            appsingleton.ToastMessage("" + e.getMessage());
        }
        try{
            if(zaparoundlist.isEmpty())
            fragment.reloadListData(new ArrayList<CheckinZapmeVO>());

        }catch(Exception e){
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
            final Button bt_yes, bt_no;
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
                tv_title.setText(title);
                tv_msg.setText( message);
                bt_yes.setText(btnright);
                bt_no.setText( btnleft);
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

                    try{
                        ZapClearOperation(operation,position);
                        dialog.dismiss();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }//end of show dialog
    /*
   * to dosplay dialog
   * */
    public void showListDialog(String title, String message, String btnleft, String btnright, final int position, ArrayList<InterestVO> intrestlist, CheckinZapmeVO vo) {
        try {
            final Dialog dialog = new Dialog(mContext);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.checkin_list_popip);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tv_msg, tv_title, tv_username, tv_agetext, agecount;
            ImageView iv_close, iv_middle,iv_gender;
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

             /*Assining fonts*/
            tv_msg.setTypeface(appsingleton.lighttype);
            tv_title.setTypeface(appsingleton.regulartype);
            tv_username.setTypeface(appsingleton.regulartype);
            bt_yes.setTypeface(appsingleton.regulartype);
            bt_no.setTypeface(appsingleton.regulartype);
            tv_agetext.setTypeface(appsingleton.regulartype);
            agecount.setTypeface(appsingleton.regulartype);

            try {
                tv_title.setText(title);
                tv_msg.setText(message);
                tv_username.setText(message);
                bt_yes.setText(btnright);
                bt_no.setText(btnleft);
                agecount.setText(vo.getAge());
                Picasso.with(mContext).load(vo.getUser_selfie_thumb()).resize(200, 200).centerCrop().placeholder(R.drawable.dummy_user_icon).into(iv_middle);
                final CheckinZapmeVO item = zaparoundlist.get(position);
                iv_middle.setOnClickListener(new View.OnClickListener() {
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
                    tv_title.setText( mContext.getResources().getString(R.string.st_NOsimilarintrests_text));
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
                        ZapClearOperation("deny",position);
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
                        ZapClearOperation("accept",position);
                        dialog.dismiss();
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });


            dialog.show();
            appsingleton.addIntrestGrid(mContext, gridlayout, intrestlist);

        } catch (Exception e) {
            e.printStackTrace();
            ;
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
}
