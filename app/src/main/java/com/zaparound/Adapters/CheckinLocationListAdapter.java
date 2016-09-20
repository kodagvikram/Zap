package com.zaparound.Adapters;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;
import com.zaparound.CheckinListActivity;
import com.zaparound.ModelVo.ChatUserListVO;
import com.zaparound.ModelVo.CheckinLocationListVO;
import com.zaparound.ProfileImageActivity;
import com.zaparound.R;
import com.zaparound.SelfiCameraActivity;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.ViewHolders.CheckinLocationListHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CheckinLocationListAdapter extends RecyclerView.Adapter<CheckinLocationListHolder> {

    private ArrayList<CheckinLocationListVO> labels;
    public Context context;
    Appsingleton appsingleton;

    public CheckinLocationListAdapter(Context context, ArrayList<CheckinLocationListVO> labels) {
        this.context = context;
        this.labels = labels;
        appsingleton = Appsingleton.getinstance(context);
    }

    @Override
    public CheckinLocationListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkin_locationlist_adapterxml, parent, false);

        return new CheckinLocationListHolder(view);
    }

    @Override
    public void onBindViewHolder(final CheckinLocationListHolder holder, final int position) {
        try {
            final String itemtitle = labels.get(position).getItemtitle();
            final String miles = labels.get(position).getMiles();
            final CheckinLocationListVO item = labels.get(position);
            holder.tv_itemtitle.setText(itemtitle);
            holder.bt_miles.setText(miles + " " + context.getResources().getString(R.string.st_feets));
//            if (item.getProfileurl().equalsIgnoreCase(""))
//                Picasso.with(context).load(item.iconurl).resize(200, 200).centerCrop().placeholder(R.drawable.dummy_location_icon).into(holder.iv_checkinprofile);
//            else
//                Picasso.with(context).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + item.getProfileurl() + "&key=" + AppUtils.GOOGLE_API_KEY).resize(200, 200).centerCrop().placeholder(R.drawable.dummy_location_icon).into(holder.iv_checkinprofile);
            String imageurl="";
            ArrayList<String> list = new ArrayList<>();
            try{
                list=setData(item.getDescription());
                if(list.size()>0)
                {
                    imageurl=list.get(4);
                }

            }catch(Exception e){
                imageurl="";
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }
            if(imageurl.equalsIgnoreCase(""))
                imageurl=item.getProfileurl();
            final String imageurl1=imageurl;
             Picasso.with(context).load(imageurl).resize(200, 200).centerCrop().placeholder(R.drawable.zaparound_gray_icon).into(holder.iv_checkinprofile);

            holder.tv_itemtitle.setTypeface(appsingleton.regulartype);
            holder.bt_miles.setTypeface(appsingleton.lighttype);
            holder.iv_checkinprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!item.getProfileurl().equalsIgnoreCase("")) {
                            Intent intent = new Intent(context, ProfileImageActivity.class);
                            intent.putExtra("IMAGEURL", imageurl1);
                            context.startActivity(intent);
                        }
                        //mContext.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            if (labels.get(position).getisselected()) {
                holder.rl_backcolor.setBackgroundColor(context.getResources().getColor(R.color.rowback_color));
            } else {
                holder.rl_backcolor.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
            holder.rl_maillayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        for (int i = 0; i < labels.size(); i++) {
                            if (i == position) {
                                if (labels.get(position).getisselected()) {
                                    labels.get(position).setIsselected(false);
                                    holder.rl_backcolor.setBackgroundColor(context.getResources().getColor(R.color.white));
                                } else {
                                    labels.get(position).setIsselected(true);
                                    holder.rl_backcolor.setBackgroundColor(context.getResources().getColor(R.color.rowback_color));
                                    try {
                                        String title = itemtitle;
                                        String message = item.getDescription();
                                        String miles1 = miles;
                                        String profileurl = imageurl1;
                                        showDialog(title, message, miles1, profileurl, holder.getLayoutPosition());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        appsingleton.ToastMessage("" + e.getMessage());
                                    }
                                }
                            }//end of if
                            else {
                                labels.get(i).setIsselected(false);
                                holder.rl_backcolor.setBackgroundColor(context.getResources().getColor(R.color.white));
                            }//end of else
                        }//end of for
                        notifyDataSetChanged();

                        //appsingleton.SnackbarMessage(labels.get(position).getItemtitle(), labels.get(position).getMiles(), v);

                        CheckinListActivity listActivity = (CheckinListActivity) context;
                        listActivity.HideShowCheckinview(labels.get(position));

                    } catch (Exception e) {
                        appsingleton.ToastMessage("" + e.getMessage());
                    }


                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload(ArrayList<CheckinLocationListVO> post) {
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
    public void showDialog(String title, String message, String miles,final String profileurl, final int pos) {
        try {
            final Dialog dialog = new Dialog(context);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.checkin_location_popup);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tv_msg, tv_title,tv_opentext;
            ImageView iv_close, iv_middle, direction;
            Button bt_miles, btn_getdirection;
            RelativeLayout rl_getdirection;
            tv_title = (TextView) dialog.findViewById(R.id.tv_popuptitle);
            tv_opentext = (TextView) dialog.findViewById(R.id.tv_opentext);
            tv_msg = (TextView) dialog.findViewById(R.id.tv_popupsubtitle);
            iv_close = (ImageView) dialog.findViewById(R.id.iv_Crossimage);
            iv_middle = (ImageView) dialog.findViewById(R.id.iv_middleimage);
            direction = (ImageView) dialog.findViewById(R.id.iv_rightarrow);
            bt_miles = (Button) dialog.findViewById(R.id.bt_miles);
            btn_getdirection = (Button) dialog.findViewById(R.id.btn_getdirection);
            rl_getdirection = (RelativeLayout) dialog.findViewById(R.id.rl_getdirection);

             /*Assining fonts*/
            tv_msg.setTypeface(appsingleton.lighttype);
            tv_title.setTypeface(appsingleton.regulartype);
            bt_miles.setTypeface(appsingleton.regulartype);
            tv_opentext.setTypeface(appsingleton.regulartype);
            btn_getdirection.setTypeface(appsingleton.regulartype);
            String opentext="";
            ArrayList<String> list = new ArrayList<>();
            try{
                list=setData(message);
                if(list.size()>0)
                {
                    message=list.get(2);
                    opentext=list.get(3);
                }

            }catch(Exception e){
                message="";
                opentext="";
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }




            try {
                rl_getdirection.setVisibility(View.VISIBLE);
                tv_title.setText(title);

                //tv_opentext.setText(message);
                if(opentext.equalsIgnoreCase("1"))
                {
                    tv_opentext.setVisibility(View.VISIBLE);
                    tv_opentext.setText("Open now");
                }else  if(opentext.equalsIgnoreCase("0"))
                {
                    tv_opentext.setVisibility(View.VISIBLE);
                    tv_opentext.setText("Closed now");
                }

                tv_msg.setText(message);
                //bt_miles.setText(miles + " " + context.getResources().getString(R.string.st_feets));
                btn_getdirection.setText(context.getResources().getString(R.string.st_checkintoolbar));
                bt_miles.setVisibility(View.GONE);
                direction.setVisibility(View.GONE);
                tv_msg.setVisibility(View.GONE);
                // btn_getdirection.setText("" + btnleft);
                // Picasso.with(context).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + profileurl + "&key=" + AppUtils.GOOGLE_API_KEY).resize(200, 200).centerCrop().placeholder(R.drawable.zaparound_gray_icon).into(iv_middle);
                Picasso.with(context).load(profileurl).resize(200, 200).centerCrop().placeholder(R.drawable.zaparound_gray_icon).into(iv_middle);

            } catch (Exception e) {
                appsingleton.ToastMessage("" + e.getMessage());
            }
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_getdirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        try {
                            CheckinListActivity activity = (CheckinListActivity) context;
                            activity.Checkin(new View(context));
                        } catch (Exception e) {
                            appsingleton.ToastMessage("" + e.getMessage());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });
            final CheckinLocationListVO item = labels.get(pos);
            iv_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!item.getProfileurl().equalsIgnoreCase("")) {
                            Intent intent = new Intent(context, ProfileImageActivity.class);
                            intent.putExtra("IMAGEURL", profileurl);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end of show dialog
    public ArrayList<String> setData(String str) {
        ArrayList<String> list = new ArrayList<>();
        list.clear();
        int x = 0;
        String str2=str.replace("@#$#@","@#$#@ ");

        ArrayList aList= new ArrayList(Arrays.asList(str2.split("[@#$#@]+")));

        for(int i=0;i<aList.size();i++)
        {
            //if(!((""+aList.get(i)).equals("")))
            //{
            //InterestVO vo = new InterestVO(++x, "" + aList.get(i), "", "", false);
            list.add((String.valueOf(aList.get(i))).trim());
            // }
        }
        return list;
    }

}//end of class