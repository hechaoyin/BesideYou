package com.example.hbz.besideyou.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

public class DoodleCreateRoomActivity extends BastActivity {

    private CheckBox cb_allow_room;
    private CheckBox cb_allow_other;
    private LinearLayout ll_invite_friends;
    private TextView tv_create_room;

    private RecyclerView rv_friend;
    private RecyclerView.Adapter friendAdapter;
    private List<String> friends = new ArrayList<>();

    private boolean allowRoomInvite = true;    // 是否允许通过房间邀请
    private boolean allowOtherInvite = true;   // 是否允许其他人邀请

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_doodle_room);
        StatusBarUtils.setStatus(this, findViewById(R.id.top_view)); // 融合状态栏
        initView();
    }

    private void initView() {
        cb_allow_room = (CheckBox) findViewById(R.id.cb_allow_room);
        cb_allow_room.setOnCheckedChangeListener((buttonView, isChecked) -> allowRoomInvite = isChecked);

        cb_allow_other = (CheckBox) findViewById(R.id.cb_allow_other);
        cb_allow_other.setOnCheckedChangeListener((buttonView, isChecked) -> allowOtherInvite = isChecked);

        ll_invite_friends = (LinearLayout) findViewById(R.id.ll_invite_friends);
        ll_invite_friends.setOnClickListener(v -> inviteFriend());

        rv_friend = (RecyclerView) findViewById(R.id.rv_friend);
        friendAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View textView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                textView.getLayoutParams().height = 120;
                return new RecyclerView.ViewHolder(textView) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText(friends.get(position));
            }

            @Override
            public int getItemCount() {
                return friends.size();
            }
        };
        rv_friend.setLayoutManager(new LinearLayoutManager(this));
        rv_friend.setAdapter(friendAdapter);

        tv_create_room = (TextView) findViewById(R.id.tv_create_room);
        tv_create_room.setOnClickListener(v -> createRoom());
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    /**
     * 邀请好友
     */
    private void inviteFriend() {
        friends.add("123");
        friends.add("456");
        friends.add("789");
        friends.add("000");
        friendAdapter.notifyDataSetChanged();
    }

    /**
     * 创建房间
     */
    private void createRoom() {

    }
}
