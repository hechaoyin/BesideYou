package com.example.hbz.besideyou.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.utils.StatusBarUtils;

import static com.example.hbz.besideyou.activity.DoodleWebRtcActivity.ROOM_NAME;

public class LaboratoryActivity extends BastActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratory);
        StatusBarUtils.setStatus(this, findViewById(R.id.top_view)); // 融合状态栏
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());// 返回
        findViewById(R.id.iv_setting).setOnClickListener(v -> {
            Intent intent = new Intent(this, DoodleWebRtcActivity.class);
            intent.putExtra(ROOM_NAME, "123456789");
            startActivity(intent);
        });
    }
}
