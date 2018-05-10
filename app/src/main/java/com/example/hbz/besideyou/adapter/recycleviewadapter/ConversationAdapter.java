package com.example.hbz.besideyou.adapter.recycleviewadapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.adapter.recycleviewadapter.listener.OnItemClickListener;
import com.example.hbz.besideyou.im.bean.ConversationBean;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @ClassName: com.example.hbz.besideyou.adapter
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/22 11:49
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>implements View.OnClickListener {

    private List<ConversationBean> timConversations;
    private OnItemClickListener onItemClickListener;

    public ConversationAdapter(List<ConversationBean> timConversations) {
        this.timConversations = timConversations;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversations, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        if (holder == null || timConversations == null || timConversations.size() <= position) {
            return;
        }
        holder.itemView.setTag(position);
        ConversationBean conversation = timConversations.get(position);
        Glide.with(holder.iv_face.getContext()).load(conversation.getFaceUrl()).apply(
                new RequestOptions().placeholder(R.drawable.ic_login_face)).into(holder.iv_face);
        holder.tv_name.setText(conversation.getName());
        holder.tv_time.setText(conversation.getLastMsgTime());
        holder.tv_message.setText(conversation.getLastMsg());

    }

    @Override
    public int getItemCount() {
        return timConversations != null ? timConversations.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener!=null){
            onItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_face;
        private TextView tv_name;
        private TextView tv_time;
        private TextView tv_message;

        ConversationViewHolder(View itemView) {
            super(itemView);
            iv_face = itemView.findViewById(R.id.iv_face);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_message = itemView.findViewById(R.id.tv_message);
            itemView.setOnClickListener(ConversationAdapter.this);
        }
    }
}
