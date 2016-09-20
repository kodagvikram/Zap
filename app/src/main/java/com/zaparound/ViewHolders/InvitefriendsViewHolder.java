package com.zaparound.ViewHolders;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zaparound.R;

public class InvitefriendsViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_itemtitle;
    public ImageView iv_checkinprofile;
    public RelativeLayout rl_maillayout,rl_backcolor,rl_profileimagelayout;
    public TextView tv_contactnumber;
    public ToggleButton tb_invitetoggle;

    public InvitefriendsViewHolder(View itemView) {
        super(itemView);
        try {
            tv_itemtitle = (TextView) itemView.findViewById(R.id.tv_checkintitle);
            tv_contactnumber = (TextView) itemView.findViewById(R.id.tv_contactnumber);
            iv_checkinprofile = (ImageView) itemView.findViewById(R.id.iv_checkinlogo);
            tb_invitetoggle = (ToggleButton) itemView.findViewById(R.id.tb_invite);
            rl_maillayout = (RelativeLayout) itemView.findViewById(R.id.rl_checkin_adaptermaillayout);
            rl_backcolor = (RelativeLayout) itemView.findViewById(R.id.rl_backcolor);
            rl_profileimagelayout = (RelativeLayout) itemView.findViewById(R.id.rl_imageclick);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}