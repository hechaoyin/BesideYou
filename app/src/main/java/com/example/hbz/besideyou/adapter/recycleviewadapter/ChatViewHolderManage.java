package com.example.hbz.besideyou.adapter.recycleviewadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.hbz.besideyou.R;
import com.example.im.MessageConstant;

/**
 * @ClassName: com.example.hbz.besideyou.adapter.recycleviewadapter
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/27 14:42
 */
public class ChatViewHolderManage {

    public static ChatViewHolder createViewHolder(View view, int type) {
        switch (type) {
            case MessageConstant.MESSAGE_TYPE_TEXT:
                return new TextViewHolder(view);
            case MessageConstant.MESSAGE_TYPE_TEXT << 10:
                return new TextViewHolder(view);
            default:
                return new ChatViewHolder(view) {
                };
        }
    }


    abstract static class ChatViewHolder extends RecyclerView.ViewHolder {
        ChatViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class TextViewHolder extends ChatViewHolder {
        public TextView tv_msg_text;

        public TextViewHolder(View itemView) {
            super(itemView);
            tv_msg_text = itemView.findViewById(R.id.tv_msg_text);
        }
    }


}

