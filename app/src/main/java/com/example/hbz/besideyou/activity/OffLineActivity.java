package com.example.hbz.besideyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;

public class OffLineActivity extends BastActivity implements View.OnClickListener {

    public static final String OFF_LINE_STYLE = "OFFLINESTYLE";
    public static final int FORCE_Off_LINE = 1;
    public static final int USER_SIG_EXPIRED = 2;

    private TextView offline_notification_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_line);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        int offLineStyle = intent.getIntExtra(OFF_LINE_STYLE, FORCE_Off_LINE);
        if (offLineStyle == FORCE_Off_LINE) {
            offline_notification_content.setText(R.string.offline_force_offline);
        } else if (offLineStyle == USER_SIG_EXPIRED) {
            offline_notification_content.setText(R.string.offline_user_sig_expired);
        }
    }

    private void initView() {
        offline_notification_content = (TextView) findViewById(R.id.offline_notification_content);
        TextView restart_login = (TextView) findViewById(R.id.restart_login);
        restart_login.setOnClickListener(this);
        TextView goto_login_activity = (TextView) findViewById(R.id.goto_login_activity);
        goto_login_activity.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart_login:
            case R.id.goto_login_activity:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
    }
}
