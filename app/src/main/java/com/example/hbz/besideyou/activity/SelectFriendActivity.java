package com.example.hbz.besideyou.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;

public class SelectFriendActivity extends BastActivity {

    private RecyclerView rv_select_friend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        initView();
    }

    private void initView() {
        rv_select_friend = (RecyclerView) findViewById(R.id.rv_select_friend);
    }
}
