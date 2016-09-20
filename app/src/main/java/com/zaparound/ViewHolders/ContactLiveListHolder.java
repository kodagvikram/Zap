package com.zaparound.ViewHolders;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.zaparound.R;

public class ContactLiveListHolder extends RecyclerView.ViewHolder {
    public ImageView iv_checkinprofile;

    public ContactLiveListHolder(View itemView) {
        super(itemView);
        try {
            iv_checkinprofile = (ImageView) itemView.findViewById(R.id.iv_hor_profileimage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}