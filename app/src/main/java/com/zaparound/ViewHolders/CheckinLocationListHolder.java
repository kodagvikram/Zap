package com.zaparound.ViewHolders;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zaparound.R;

public class CheckinLocationListHolder extends RecyclerView.ViewHolder {
    public TextView tv_itemtitle;
    public ImageView iv_checkinprofile, iv_arrowimage;
    public RelativeLayout rl_maillayout,rl_backcolor;
    public TextView bt_miles;

    public CheckinLocationListHolder(View itemView) {
        super(itemView);
        try {
            tv_itemtitle = (TextView) itemView.findViewById(R.id.tv_checkintitle);
            bt_miles = (TextView) itemView.findViewById(R.id.bt_miles);
            iv_checkinprofile = (ImageView) itemView.findViewById(R.id.iv_checkinlogo);
            iv_arrowimage = (ImageView) itemView.findViewById(R.id.iv_rightarrow);
            rl_maillayout = (RelativeLayout) itemView.findViewById(R.id.rl_checkin_adaptermaillayout);
            rl_backcolor = (RelativeLayout) itemView.findViewById(R.id.rl_backcolor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}