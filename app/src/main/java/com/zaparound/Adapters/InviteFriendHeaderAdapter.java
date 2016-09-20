package com.zaparound.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zaparound.ModelVo.InviteFriendsVO;
import com.zaparound.R;
import com.zaparound.Singleton.Appsingleton;
import com.zaparound.stickyheaders.StickyHeadersAdapter;

import java.util.List;

public class InviteFriendHeaderAdapter implements StickyHeadersAdapter<InviteFriendHeaderAdapter.ViewHolder> {

    private List<InviteFriendsVO> items;
    public Appsingleton appsingleton;

    public InviteFriendHeaderAdapter(List<InviteFriendsVO> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_header, parent, false);
        appsingleton=Appsingleton.getinstance(parent.getContext());
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder headerViewHolder, int position) {
        headerViewHolder.title.setText(items.get(position).getUsername().subSequence(0, 1));
        headerViewHolder.title.setTypeface(appsingleton.boldtype);
    }

    @Override
    public long getHeaderId(int position) {
        return items.get(position).getUsername().subSequence(0, 1).hashCode();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
