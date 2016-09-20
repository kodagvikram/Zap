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
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zaparound.AppUtils.AppUtils;
import com.zaparound.ModelVo.ContactStickyVO;
import com.zaparound.ModelVo.NearbyMapVO;
import com.zaparound.NearbyLocationActivity;
import com.zaparound.ProfileImageActivity;
import com.zaparound.R;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.ViewHolders.NearByViewHolder;

import java.util.ArrayList;
import java.util.Arrays;

public class NearbyLocationAdapter extends RecyclerView.Adapter<NearByViewHolder> {

    private ArrayList<NearbyMapVO> labels;
    public Context context;
    Appsingleton appsingleton;

    public NearbyLocationAdapter(Context context, ArrayList<NearbyMapVO> labels) {
        this.context = context;
        this.labels = labels;
        appsingleton = Appsingleton.getinstance(context);
    }

    @Override
    public NearByViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearbymap_adapter, parent, false);
        return new NearByViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NearByViewHolder holder, int position) {
        try {
            final NearbyMapVO item=labels.get(position);
            final String itemtitle = labels.get(position).getPlacename();
            final String miles = labels.get(position).getMiles()+" "+context.getResources().getString(R.string.st_miles);

            holder.tv_itemtitle.setText(itemtitle);
            holder.bt_miles.setText(miles);
            String imageurl="";
            ArrayList<String> list = new ArrayList<>();
            try{
                list=setData(item.getPlace_description());
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

            if (labels.get(position).getisselected()) {
                holder.rl_backcolor.setBackgroundColor(context.getResources().getColor(R.color.rowback_color));
            } else {
                holder.rl_backcolor.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
            holder.iv_checkinprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                       // if (!item.getProfileurl().equalsIgnoreCase("")) {
                            Intent intent = new Intent(context, ProfileImageActivity.class);
                            intent.putExtra("IMAGEURL",  imageurl1);
                            context.startActivity(intent);
                        //}
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

                        for (int i = 0; i < labels.size(); i++) {
                            //listActivity.changeMarker(position,false);
                            labels.get(i).setIsselected(false);
                            holder.rl_backcolor.setBackgroundColor(context.getResources().getColor(R.color.white));
                        }//end of for

                        labels.get(holder.getLayoutPosition()).setIsselected(true);
                        holder.rl_backcolor.setBackgroundColor(context.getResources().getColor(R.color.rowback_color));
                        notifyDataSetChanged();

                        //appsingleton.SnackbarMessage(labels.get(position).getItemtitle(), labels.get(position).getMiles(), v);
                        NearbyLocationActivity listActivity = (NearbyLocationActivity) context;
                        listActivity.changeMarker();

                        String title = itemtitle;
                        String message = item.getPlace_description();
                        String miles1 = miles;
                        String profileurl = imageurl1;
                        showDialog(title, message, miles1, profileurl,holder.getLayoutPosition());

                    } catch (Exception e) {
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

            holder.rl_getdirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+labels.get(holder.getLayoutPosition()).getLatitude()+","+labels.get(holder.getLayoutPosition()).getLongitude()));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        context.startActivity(intent);
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public void reload(ArrayList<NearbyMapVO> post) {
        try {
            labels.clear();
            labels.addAll(post);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
 * to dosplay dialog
 * */
    public void showDialog(String title, String message, String miles,final String profileurl,final int pos) {
        try {
            final Dialog dialog = new Dialog(context);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.nearby_direction_popup);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(appsingleton.devicewidth, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tv_msg, tv_title,tv_opentext;
            ImageView iv_close, iv_middle;
          final  Button bt_miles, btn_getdirection,btn_contact,btn_website;
            tv_title = (TextView) dialog.findViewById(R.id.tv_popuptitle);
            tv_msg = (TextView) dialog.findViewById(R.id.tv_popupsubtitle);
            iv_close = (ImageView) dialog.findViewById(R.id.iv_Crossimage);
            iv_middle = (ImageView) dialog.findViewById(R.id.iv_middleimage);
            bt_miles = (Button) dialog.findViewById(R.id.bt_miles);
            btn_getdirection = (Button) dialog.findViewById(R.id.btn_getdirection);
            tv_opentext = (TextView) dialog.findViewById(R.id.tv_opentext);
            btn_contact = (Button) dialog.findViewById(R.id.bt_contactnumber);
            btn_website = (Button) dialog.findViewById(R.id.bt_website);

             /*Assining fonts*/
            tv_msg.setTypeface(appsingleton.lighttype);
            tv_title.setTypeface(appsingleton.regulartype);
            bt_miles.setTypeface(appsingleton.regulartype);
            btn_getdirection.setTypeface(appsingleton.regulartype);
            tv_opentext.setTypeface(appsingleton.regulartype);
            btn_contact.setTypeface(appsingleton.regulartype);
            btn_website.setTypeface(appsingleton.regulartype);

            String opentext="",contactno="",website="";

            ArrayList<String> list = new ArrayList<>();
            try{
                list=setData(message);
                if(list.size()>0)
                {
                    contactno=list.get(0);
                    website=list.get(1);
                    message=list.get(2);
                    opentext=list.get(3);
                }

            }catch(Exception e){
                message="";
                opentext="";
                contactno="";
                website="";
                e.printStackTrace();
                appsingleton.ToastMessage("" + e.getMessage());
            }

            try {

                if(opentext.equalsIgnoreCase("1"))
                {
                    tv_opentext.setVisibility(View.VISIBLE);
                    tv_opentext.setText("Open now");
                }else  if(opentext.equalsIgnoreCase("0"))
                {
                    tv_opentext.setVisibility(View.VISIBLE);
                    tv_opentext.setText("Closed now");
                }

                if(!contactno.equalsIgnoreCase(""))
                {
                    btn_contact.setVisibility(View.VISIBLE);
                }
                if(!website.equalsIgnoreCase(""))
                {
                    btn_website.setVisibility(View.VISIBLE);
                }
                btn_contact.setText(contactno);
                btn_website.setText(website);
                tv_title.setText(title);
                tv_msg.setText(message);
                bt_miles.setText(miles);
                tv_msg.setVisibility(View.GONE);
                // btn_getdirection.setText("" + btnleft);
                //Picasso.with(context).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + profileurl + "&key=" + AppUtils.GOOGLE_API_KEY).resize(200, 200).centerCrop().placeholder(R.drawable.zaparound_gray_icon).into(iv_middle);
                Picasso.with(context).load(profileurl).resize(200, 200).centerCrop().placeholder(R.drawable.zaparound_gray_icon).into(iv_middle);

            } catch (Exception e) {
                appsingleton.ToastMessage("" + e.getMessage());
            }

            final NearbyMapVO item=labels.get(pos);
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
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        appsingleton.openURL(context,btn_website.getText().toString());
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                    dialog.dismiss();
                }
            });

            btn_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        appsingleton.callNumber(context,btn_contact.getText().toString());
                    }catch(Exception e){
                        e.printStackTrace();
                        appsingleton.ToastMessage("" + e.getMessage());
                    }
                    dialog.dismiss();
                }
            });

            btn_getdirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        dialog.dismiss();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+labels.get(pos).getLatitude()+","+labels.get(pos).getLongitude()));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        context.startActivity(intent);
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