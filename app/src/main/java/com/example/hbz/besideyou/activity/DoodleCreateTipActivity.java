package com.example.hbz.besideyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.utils.StatusBarUtils;

public class DoodleCreateTipActivity extends BastActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_doodle_tip);
        StatusBarUtils.setStatus(this, findViewById(R.id.top_view)); // 融合状态栏
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());// 返回

        ImageView iv_setting = (ImageView) findViewById(R.id.iv_setting);
        LinearLayout ll_offline_doodle = (LinearLayout) findViewById(R.id.ll_offline_doodle);
        ll_offline_doodle.setOnClickListener(v -> {// 离线涂鸦
            startActivity(new Intent(this, DoodleActivity.class));
            finish();
        });
        LinearLayout ll_create_room = (LinearLayout) findViewById(R.id.ll_create_room);
        ll_create_room.setOnClickListener(v -> {// 创建房间
            startActivity(new Intent(this, DoodleCreateRoomActivity.class));
            finish();
        });
        LinearLayout ll_add_room = (LinearLayout) findViewById(R.id.ll_add_room); // 添加房间
        ll_add_room.setOnClickListener(v -> {// 加入房间
            startActivity(new Intent(this, DoodleJoinRoomActivity.class));
            finish();
        });

    }
}
