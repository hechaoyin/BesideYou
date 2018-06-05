package com.example.hbz.besideyou.aatest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.activity.DoodleWebRtcActivity;
import com.example.hbz.besideyou.activity.LoginActivity;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.hbz.besideyou.view.floatwindow.FloatWindow;
import com.example.hbz.besideyou.view.floatwindow.SectorLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;

import static com.example.hbz.besideyou.activity.DoodleWebRtcActivity.ROOM_NAME;

public class AAActivity extends FragmentActivity implements View.OnClickListener {

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
                requestDrawOverLays();
                break;
            case R.id.btn_test:
//                initRxPermissions();// 申请权限
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
            default:
        }
    }

    public void requestDrawOverLays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(getApplicationContext())) {
                //有悬浮窗权限开启服务绑定 绑定权限
                showFloat();
            } else {
                //没有悬浮窗权限m,去开启悬浮窗权限
                try {
                    new AlertDialog.Builder(this)
                            .setTitle("打开悬浮窗失败")
                            .setMessage("请设悬浮窗权限。")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("去打开", (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, 1);
                            }).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // 6.0以下
            showFloat();
        }
    }

    void showFloat() {
        View floatView = LayoutInflater.from(getApplication()).inflate(R.layout.float_menu_view, null);
        View playerView = LayoutInflater.from(getApplication()).inflate(R.layout.float_menu_view_open, null);
        FloatWindow floatWindow = new FloatWindow(getApplication(), playerView, floatView);
        PlayerListener playerListener = new PlayerListener(floatWindow);
        playerView.findViewById(R.id.iv_menu_back).setOnTouchListener(playerListener);
        playerView.findViewById(R.id.tv_one).setOnTouchListener(playerListener);
        playerView.findViewById(R.id.tv_two).setOnTouchListener(playerListener);
        playerView.findViewById(R.id.tv_three).setOnTouchListener(playerListener);

        SectorLayout sectorLayout = playerView.findViewById(R.id.menu_open);
        sectorLayout.setType(SectorLayout.Type.CIRCLE);
        floatWindow.setScreenPositionChange(new MScreenPositionChange(sectorLayout));

        floatWindow.show();
    }

    public static class MScreenPositionChange implements FloatWindow.ScreenPositionChange {
        private SectorLayout sectorLayout;

        MScreenPositionChange(SectorLayout sectorLayout) {
            this.sectorLayout = sectorLayout;
        }

        @Override
        public void onChange(boolean isLeft) {
            if (sectorLayout != null) {
                if (isLeft) {
                    sectorLayout.setType(SectorLayout.Type.RIGHT_SEMICIRCLE);
                } else {
                    sectorLayout.setType(SectorLayout.Type.LEFT_SEMICIRCLE);
                }
            }
        }
    }

    public static class PlayerListener implements View.OnTouchListener {
        private FloatWindow floatWindow;
        private float downX, downY;

        PlayerListener(FloatWindow floatWindow) {
            this.floatWindow = floatWindow;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                downX = event.getRawX();
                downY = event.getRawY();
                showPop(v);
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                float x = event.getRawX();
                float y = event.getRawY();
                if (Math.abs(x - downX) < 20 && Math.abs(y - downY) < 20) {
                    onClick(v);
                }
                if (floatWindow != null) {
                    floatWindow.dismissPopwindow();
                }
            }
            return true;
        }

        private void showPop(View v) {
            if (floatWindow == null) {
                return;
            }
            String msg = "";
            switch (v.getId()) {
                case R.id.iv_menu_back:
                    msg = "返回";
                    break;
                case R.id.tv_one:
                    msg = "one";
                    break;
                case R.id.tv_two:
                    msg = "two";
                    break;
                case R.id.tv_three:
                    msg = "two";
                    break;
                default:
                    break;
            }
            floatWindow.showPopwindow(msg);
        }

        private void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_menu_back:
                    floatWindow.changeShowType();
                    break;
                case R.id.tv_one:
                default:
                    break;
            }
        }
    }


    /**
     * 权限申请
     */
    @SuppressLint("CheckResult")
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
