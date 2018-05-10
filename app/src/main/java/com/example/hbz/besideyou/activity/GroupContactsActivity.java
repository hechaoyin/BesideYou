package com.example.hbz.besideyou.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.utils.StatusBarUtils;

public class GroupContactsActivity extends BastActivity {

    private ImageView iv_add;
    private RecyclerView rv_group_contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_contacts);
        StatusBarUtils.setStatus(this, findViewById(R.id.top_view));// 状态栏融合
        initView();
    }

    private void initView() {
        iv_add = (ImageView) findViewById(R.id.iv_add);
        iv_add.setOnClickListener(v -> addGroup());

        SwipeRefreshLayout srl_refresh = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        // 设置下拉进度的背景颜色，默认就是白色的
        srl_refresh.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        srl_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        srl_refresh.setOnRefreshListener(() -> {
            initGroupContacts();
            srl_refresh.setRefreshing(false);
        });

        rv_group_contacts = (RecyclerView) findViewById(R.id.rv_group_contacts);
    }

    private void addGroup() {

    }

    private void initGroupContacts() {
        //TODO 初始化组联系人
    }

}
