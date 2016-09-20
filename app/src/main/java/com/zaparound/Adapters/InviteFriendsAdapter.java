package com.zaparound.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zaparound.InviteFriendsActivity;
import com.zaparound.ModelVo.ContactsLivelistVO;
import com.zaparound.ModelVo.InviteFriendsVO;
import com.zaparound.R;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.ViewHolders.InvitefriendsViewHolder;

import java.util.ArrayList;

public class InviteFriendsAdapter extends RecyclerView.Adapter<InvitefriendsViewHolder> {

    private ArrayList<InviteFriendsVO> labels;
    public Context context;
    Appsingleton appsingleton;
    public InviteFriendsActivity activity;
    public int count=0;
    public InviteFriendsAdapter(Context context, ArrayList<InviteFriendsVO> labels) {
        this.context = context;
        this.labels = labels;
        appsingleton=Appsingleton.getinstance(context);
        activity=(InviteFriendsActivity)context;
    }

    @Override
    public InvitefriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invitefriends_stickyheader_adapterxml, parent, false);
        return new InvitefriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InvitefriendsViewHolder holder, final int position) {
        try {
            InviteFriendsVO item = labels.get(position);
            final String itemtitle = item.getUsername();
            final String contact = item.getContact();


            holder.tv_itemtitle.setText(itemtitle);
            holder.tv_contactnumber.setText(contact);

            if (item.getProfileurl() != null) {
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver() , Uri.parse(item.getProfileurl()));
                    holder.iv_checkinprofile.setImageBitmap(bitmap);
                }catch(Exception e){
                    e.printStackTrace();
                    appsingleton.ToastMessage("" + e.getMessage());
                }
            } else {

                holder.iv_checkinprofile.setImageResource(R.drawable.dummy_user_icon);
            }

            if (item.isselected) {
                holder.tb_invitetoggle.setChecked(true);
            } else {
                holder.tb_invitetoggle.setChecked(false);
            }

             holder.tv_itemtitle.setTypeface(appsingleton.regulartype);
             holder.tv_contactnumber.setTypeface(appsingleton.regulartype);


            holder.rl_maillayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final Handler handler = new Handler();

                        if( !holder.tb_invitetoggle.isChecked()) {
                            holder.tb_invitetoggle.setChecked(true);
                            labels.get(position).setIsselected(true);
                            count++;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            }, 100);
                        }
                        else
                        {
                            holder.tb_invitetoggle.setChecked(false);
                            labels.get(position).setIsselected(false);
                            if(count>0)
                                count--;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            }, 100);
                        }
                        activity.updateCount(count);

                    } catch (Exception e) {
                        appsingleton.ToastMessage("" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void reload(ArrayList<InviteFriendsVO> post) {
        try {
            labels.clear();
            labels.addAll(post);
            count=0;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return labels.size();
    }

}//end of class