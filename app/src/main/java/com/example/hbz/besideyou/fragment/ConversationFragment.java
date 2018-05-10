package com.example.hbz.besideyou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.activity.ChatActivity;
import com.example.hbz.besideyou.adapter.recycleviewadapter.ConversationAdapter;
import com.example.hbz.besideyou.adapter.recycleviewadapter.listener.OnItemClickListener;
import com.example.hbz.besideyou.im.bean.ConversationBean;
import com.example.hbz.besideyou.utils.StatusBarUtils;
import com.tencent.TIMConversation;
import com.tencent.TIMManager;

import java.util.LinkedList;
import java.util.List;

import static com.example.hbz.besideyou.activity.ChatActivity.CONVERSATION_IDENTIFIER_STR;
import static com.example.hbz.besideyou.activity.ChatActivity.CONVERSATION_TYPE_C2C;
import static com.example.hbz.besideyou.activity.ChatActivity.CONVERSATION_TYPE_STR;


/**
 * @ClassName: com.example.besideyou.fragment
 * @Description: 消息界面
 * @Author: HBZ
 * @Date: 2018/3/17 11:45
 */

public class ConversationFragment extends BaseFragment implements OnItemClickListener {
    private View rootView;
    private View top_view;

    private SwipeRefreshLayout srl_refresh_message;

    private RecyclerView rv_message_list;
    private ConversationAdapter conversationAdapter;
    private List<ConversationBean> timConversations = new LinkedList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main_message, container, false);
            initView(rootView);
            StatusBarUtils.setStatus(getActivity(), top_view);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshConversation();
    }

    private void initView(View rootView) {
        top_view = rootView.findViewById(R.id.top_view);
        rv_message_list = (RecyclerView) rootView.findViewById(R.id.rv_message_list);
        rv_message_list.setLayoutManager(new LinearLayoutManager(getContext()));
        conversationAdapter = new ConversationAdapter(timConversations);
        conversationAdapter.setOnItemClickListener(this);
        rv_message_list.setAdapter(conversationAdapter);

        srl_refresh_message = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_refresh_message);
        initSwipeRefresh();
    }

    private void initSwipeRefresh() {
        // 设置下拉进度的背景颜色，默认就是白色的
        srl_refresh_message.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        srl_refresh_message.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        srl_refresh_message.setOnRefreshListener(() -> {
            // 开始刷新，设置当前为刷新状态
            srl_refresh_message.setRefreshing(true);

            refreshConversation();

            srl_refresh_message.setRefreshing(false);

        });
    }

    private void refreshConversation() {

        List<TIMConversation> timConversationList = TIMManager.getInstance().getConversionList();
        timConversations.clear();
        for (TIMConversation conversation : timConversationList) {
            if (!TextUtils.isEmpty(conversation.getPeer())) {
                timConversations.add(new ConversationBean(conversation));
            }
        }
        conversationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (timConversations == null || position >= timConversations.size()) {
            return;
        }
        ConversationBean conversationBean = timConversations.get(position);
        String identifier = conversationBean.getConversation().getPeer();

        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(CONVERSATION_TYPE_STR, CONVERSATION_TYPE_C2C);// 会话类型
        intent.putExtra(CONVERSATION_IDENTIFIER_STR, identifier);// 会话标识
        startActivity(intent);
    }
}
