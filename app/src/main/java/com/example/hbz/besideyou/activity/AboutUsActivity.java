package com.example.hbz.besideyou.activity;

import android.os.Bundle;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.utils.StatusBarUtils;

public class AboutUsActivity extends BastActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        StatusBarUtils.setStatus(this, findViewById(R.id.top_view)); // 融合状态栏
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());// 返回
    }
}