package com.example.hbz.besideyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.adapter.recycleviewadapter.ChatMessageAdapter;
import com.example.hbz.besideyou.utils.LogUtil;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;

import java.util.LinkedList;
import java.util.List;

public class ChatActivity extends BastActivity implements View.OnClickListener {

    public static final String CONVERSATION_TYPE_STR = "ConversationType";
    public static final String CONVERSATION_IDENTIFIER_STR = "ConversationIdentifier";
    public static final int CONVERSATION_TYPE_GROUP = 1;//群聊
    public static final int CONVERSATION_TYPE_C2C = 2;//单聊

    private int conversationType = CONVERSATION_TYPE_C2C;//会话类型
    private String peer;//会话标识
    private TIMConversation conversation;

    private ChatMessageAdapter adapter;
    private List<TIMMessage> messages = new LinkedList<>();// 保存所有消息

    private RecyclerView rv_chat_message;
    private SwipeRefreshLayout srl_refresh;
    private EditText et_chat_send_message;
    private Button btn_chat_send_message;

    // 发送消息回调
    private TIMValueCallBack sendMessageCallBack = new TIMValueCallBack<TIMMessage>() {
        @Override
        public void onError(int code, String desc) {//发送消息失败
            //错误码code和错误描述desc，可用于定位请求失败原因
            //错误码code含义请参见错误码表
            Log.d("***", "send message failed. code: " + code + " errmsg: " + desc);
        }

        @Override
        public void onSuccess(TIMMessage msg) {//发送消息成功
            Log.e("***", "SendMsg ok");
            adapter.addMessage(msg);
            rv_chat_message.smoothScrollToPosition(messages.size());
        }
    };

    // 加载消息回调
    private TIMValueCallBack loadMessageCallBack = new TIMValueCallBack<List<TIMMessage>>() {
        @Override
        public void onError(int code, String desc) {//获取消息失败
            // 接口返回了错误码code和错误描述desc，可用于定位请求失败原因
            // 错误码code含义请参见错误码表
            LogUtil.e("get message failed. code: " + code + " errmsg: " + desc);
        }

        @Override
        public void onSuccess(List<TIMMessage> msgs) {//获取消息成功
            adapter.addMessage(msgs, 0);
            if (adapter.getItemCount() <= 5) {
                if (adapter.getItemCount() > 1) {
                    rv_chat_message.scrollToPosition(adapter.getItemCount() - 1);
                }
            } else {
                rv_chat_message.smoothScrollBy(0, -100);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initConversationData();
        initView();
        initConversation();
        getMoreMessage(null);
    }

    private void initConversationData() {
        Intent intent = getIntent();
        conversationType = intent.getIntExtra(CONVERSATION_TYPE_STR, CONVERSATION_TYPE_C2C);
        peer = intent.getStringExtra(CONVERSATION_IDENTIFIER_STR);
    }

    private void initView() {
        rv_chat_message = (RecyclerView) findViewById(R.id.rv_chat_message);
        adapter = new ChatMessageAdapter(messages);
        rv_chat_message.setLayoutManager(new LinearLayoutManager(this));
        rv_chat_message.setAdapter(adapter);

        srl_refresh = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        srl_refresh.setOnRefreshListener(() -> {
            getMoreMessage(messages.get(0));
            srl_refresh.setRefreshing(false);
        });

        et_chat_send_message = (EditText) findViewById(R.id.et_chat_send_message);
        btn_chat_send_message = (Button) findViewById(R.id.btn_chat_send_message);
        btn_chat_send_message.setOnClickListener(this);
    }

    private void initConversation() {
        if (conversationType == CONVERSATION_TYPE_C2C) {// 单聊会话
            conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peer);
        } else if (conversationType == CONVERSATION_TYPE_GROUP) {// 群聊会话
            conversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, peer);
        }
    }

    private void getMoreMessage(TIMMessage lastMsg) {//已获取的最后一条消息，当传null的时候，从最新的消息开始读取
        //获取此会话的消息
        conversation.getMessage(5, lastMsg, loadMessageCallBack);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chat_send_message:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        String message = et_chat_send_message.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "message不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (conversation == null) {
            return;
        }
        /**会话TIMConversation后，可发送消息和获取会话缓存消息*/
        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText(message);

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Log.d("***", "addElement failed");
            return;
        }
        //发送消息
        conversation.sendMessage(msg, sendMessageCallBack);
        et_chat_send_message.setText("");
    }

}
