package com.example.hbz.besideyou.aatest;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.cosxml.CosManage;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.activity.LoginActivity;
import com.example.hbz.besideyou.utils.FileUtil;
import com.example.hbz.besideyou.utils.LogUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class AAActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnlogin;
    private TextView tv_test;
    private Button btn_webrtc;
    private Button btn_popup;
    private Button btn_back_activity;
    private Button btn_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aa);
        initView();

    }

    private void initView() {
        btnlogin = (Button) findViewById(R.id.btnlogin);
        tv_test = (TextView) findViewById(R.id.tv_test);

        btn_back_activity = (Button) findViewById(R.id.btn_back_activity);
        btn_back_activity.setOnClickListener(this);

        btnlogin.setOnClickListener(this);
        btn_webrtc = (Button) findViewById(R.id.btn_webrtc);
        btn_webrtc.setOnClickListener(this);
        btn_popup = (Button) findViewById(R.id.btn_popup);
        btn_popup.setOnClickListener(this);

        btn_test = (Button) findViewById(R.id.btn_test);
        btn_test.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnlogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btn_webrtc:
                startActivity(new Intent(this, WebRtcTestActivity.class));
                break;
            case R.id.btn_back_activity:
//                FloatViewManager.getInstance(this).showFloatBall(); //显示悬浮按钮
                String srcPath = FileUtil.getAppRootPath() + "1122.jpg";
                String cosPath = "/face/1122.jpg";
                CosManage.getInstance(AAActivity.this).upload(srcPath, cosPath, progress -> LogUtil.d("onProgress: " + progress));


                break;
            case R.id.btn_test:
                initRxPermissions();
                break;
            case R.id.btn_popup:
                PopupWindow popupWindow = new PopupWindow(this);
                DisplayMetrics dm2 = getResources().getDisplayMetrics();//获取屏幕宽高
                popupWindow.setWidth((int) (dm2.widthPixels * 0.8f));
                popupWindow.setHeight((int) (dm2.heightPixels * 0.8f));
                View popupView = LayoutInflater.from(this).inflate(R.layout.popup_window_doodle, null);
                popupWindow.setContentView(popupView);
                popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                popupWindow.setAnimationStyle(R.style.PopupAnimation);
                popupWindow.setOutsideTouchable(false);

                //点击空白处时，隐藏掉pop窗口
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(btn_popup, Gravity.CENTER, 0, 0);
                break;
        }
    }

    /**
     * 权限申请
     */
    private void initRxPermissions() {
        // where this is an Activity instance
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(
                Manifest.permission.CAMERA,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET
        )
                .subscribe(permission -> {
                    if (permission.granted) {
                        // 用户已经同意该权限
                        LogUtil.d(permission.name + " is granted.");
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        LogUtil.d(permission.name + " 用户拒绝了该权限，没有选中『不再询问』");
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        LogUtil.d(permission.name + "用户拒绝了该权限，并且选中『不再询问』");
                    }
                });
    }
}
