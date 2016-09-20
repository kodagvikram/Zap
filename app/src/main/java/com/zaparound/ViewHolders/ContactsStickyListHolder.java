package com.zaparound.ViewHolders;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zaparound.R;


public class ContactsStickyListHolder extends RecyclerView.ViewHolder {
    public TextView tv_itemtitle;
    public ImageView iv_checkinprofile, iv_gendericon;
    public RelativeLayout rl_maillayout,rl_backcolor,rl_profileimagelayout;
    public Button bt_miles;

    public ContactsStickyListHolder(View itemView) {
        super(itemView);
        try {
            tv_itemtitle = (TextView) itemView.findViewById(R.id.tv_checkintitle);
            bt_miles = (Button) itemView.findViewById(R.id.bt_miles);
            iv_checkinprofile = (ImageView) itemView.findViewById(R.id.iv_checkinlogo);
            iv_gendericon = (ImageView) itemView.findViewById(R.id.iv_gendericon);
            rl_maillayout = (RelativeLayout) itemView.findViewById(R.id.rl_checkin_adaptermaillayout);
            rl_backcolor = (RelativeLayout) itemView.findViewById(R.id.rl_backcolor);
            rl_profileimagelayout = (RelativeLayout) itemView.findViewById(R.id.rl_imageclick);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}