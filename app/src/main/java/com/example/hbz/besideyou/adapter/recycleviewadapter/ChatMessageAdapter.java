package com.example.hbz.besideyou.adapter.recycleviewadapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.adapter.recycleviewadapter.ChatViewHolderManage.ChatViewHolder;
import com.example.im.MessageConstant;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;

import java.util.LinkedList;
import java.util.List;

import static com.example.im.MessageConstant.MESSAGE_TYPE_TEXT;

/**
 * @ClassName: com.example.hbz.besideyou.adapter.recycleviewadapter
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/27 14:40
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private List<TIMMessage> messages;

    public ChatMessageAdapter(List<TIMMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case MESSAGE_TYPE_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left_text, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right_text, parent, false);
        }
        return ChatViewHolderManage.createViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        if (messages == null || position > messages.size()) {
            return;
        }
        TIMMessage message = messages.get(position);
        if (holder instanceof ChatViewHolderManage.TextViewHolder) {// 文本消息
            ChatViewHolderManage.TextViewHolder holder1 = (ChatViewHolderManage.TextViewHolder) holder;
            TIMElem e = message.getElement(0);
            if (e.getType() == TIMElemType.Text) {
                TIMTextElem textElem = (TIMTextElem) e;
                holder1.tv_msg_text.setText(textElem.getText());
            }


        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {

        TIMMessage message = messages.get(position);

        int type = Integer.MIN_VALUE;
        if (messages != null && messages.size() > position) {
            type = MessageConstant.getMessageType(message);
        }
        if (type != Integer.MIN_VALUE) {
            return message.isSelf() ? type << 10 : type;
        } else {
            return super.getItemViewType(position);
        }
    }

    public void addMessage(TIMMessage message) {
        if (messages == null) {
            messages = new LinkedList<>();
        }
        addMessage(message, messages.size());
    }

    public void addMessage(TIMMessage message, int position) {
        if (message == null) {
            return;
        }
        if (messages == null) {
            messages = new LinkedList<>();
        }
        messages.add(message);
        if (position < messages.size()) {
            notifyItemInserted(position);
        } else {
            notifyDataSetChanged();
        }
    }

    public void addMessage(List<TIMMessage> lists) {
        addMessage(lists, 0);
    }

    public void addMessage(List<TIMMessage> lists, int position) {
        if (lists == null || lists.size() == 0) {
            return;
        }
        if (messages == null) {
            messages = new LinkedList<>();
        }
        if (position < 0) {
            position = 0;
        } else if (position > messages.size()) {
            position = messages.size();
        }
        //messages.addAll(position, lists);
        for (int i = 0; i < lists.size(); i++) {
            messages.add(position, lists.get(i));
        }
        notifyItemRangeInserted(position, position + lists.size());
    }


    public void removeMessage(int position) {
        if (messages == null || position >= messages.size()) {
            return;
        }
        messages.remove(position);
        notifyItemRemoved(position);
    }

}
