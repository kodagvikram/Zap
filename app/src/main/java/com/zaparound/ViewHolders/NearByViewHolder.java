package com.zaparound.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zaparound.R;

public class NearByViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_itemtitle;
    public ImageView iv_checkinprofile, iv_arrowimage;
    public RelativeLayout rl_maillayout,rl_backcolor,rl_getdirection;
    public TextView bt_miles;

    public NearByViewHolder(View itemView) {
        super(itemView);
        try {
            tv_itemtitle = (TextView) itemView.findViewById(R.id.tv_checkintitle);
            bt_miles = (TextView) itemView.findViewById(R.id.bt_miles);
            iv_checkinprofile = (ImageView) itemView.findViewById(R.id.iv_checkinlogo);
            iv_arrowimage = (ImageView) itemView.findViewById(R.id.iv_rightarrow);
            rl_maillayout = (RelativeLayout) itemView.findViewById(R.id.rl_checkin_adaptermaillayout);
            rl_backcolor = (RelativeLayout) itemView.findViewById(R.id.rl_backcolor);
            rl_getdirection = (RelativeLayout) itemView.findViewById(R.id.rl_getdirection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}