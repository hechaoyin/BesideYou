package com.example.hbz.besideyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.receiver.VolumeReceiver;
import com.example.hbz.besideyou.tools.RxPermissionsTool;
import com.example.hbz.besideyou.tools.WebRtcTool;
import com.example.hbz.besideyou.utils.ToastUtil;
import com.tencent.TIMFriendshipManager;

public class DoodleWebRtcActivity extends BastActivity {

    public static final String ROOM_NAME = "room_name";

    private TextView tv_room_name;
    private RecyclerView rv_room_user;
    private Button btn_leave_out_room;
    private String roomName;
    private WebRtcTool webRtcTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__doodle_web_rtc);
        Intent intent = getIntent();
        roomName = intent.getStringExtra(ROOM_NAME);
        if (roomName == null) {
            ToastUtil.showShortToast("获取房间出错。");
        }

        initView();

        // 注册耳机监听广播
        VolumeReceiver.reegisterReceiver(this);

        // 申请webRtc所需权限
        RxPermissionsTool.initRxPermissions(this);

        webRtcTool = WebRtcTool.getInstance();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 反注册耳机监听广播
        VolumeReceiver.unregisterReceiver(this);
    }

    private void initView() {
        tv_room_name = (TextView) findViewById(R.id.tv_room_name);
        tv_room_name.setText(roomName == null ? "" : roomName);
        rv_room_user = (RecyclerView) findViewById(R.id.rv_room_user);
        btn_leave_out_room = (Button) findViewById(R.id.btn_leave_out_room);
        btn_leave_out_room.setOnClickListener(v -> leaveOutRoom());
    }

    /**
     * 离开房间
     */
    private void leaveOutRoom() {

        webRtcTool.start(roomName, TIMFriendshipManager.getInstance().getIdentifier());
    }
}
