package com.example.hbz.besideyou.view.floatdrag;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.example.hbz.besideyou.utils.LogUtil;

/**
 * @ClassName: ViewManager
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/10 7:12
 */

public class FloatViewManager {
    FloatingView floatBall;
    WindowManager windowManager;
    public static FloatViewManager manager;
    Context context;
    private WindowManager.LayoutParams floatBallParams;

    private FloatViewManager(Context context) {
//        context = context.getApplicationContext();
        this.context = context;
    }

    public static FloatViewManager getInstance(Context context) {
        if (manager == null) {
            manager = new FloatViewManager(context);
        }
        return manager;
    }

    public void showFloatBall() {
        floatBall = new FloatingView(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);// 活动页面Windows
        if (windowManager == null) {
            return;
        }
        //初始化状态栏的方法
        initStatusBarHelperView();

        //初始化LayoutParams
        getFloatBallParams();

        windowManager.addView(floatBall, floatBallParams);

        floatBall.setOnTouchListener(new ClickOrMoveDetctor() {
            @Override
            void onMoveTo(float x, float y) {
                float dx = getViewDownX();
                float dy = getViewDownY();
                updateLocation((int) (x - dx), (int) (y - dy - getStatusBarHeight()));
            }

            @Override
            protected void onMoveBy(float dx, float dy) {

            }

            @Override
            void onClick() {
                LogUtil.d("点击事件");
            }
        });

    }


    public WindowManager.LayoutParams getFloatBallParams() {
        if (floatBallParams == null) {
            floatBallParams = new WindowManager.LayoutParams();
            floatBallParams.width = floatBall.width;
            floatBallParams.height = floatBall.height;
            floatBallParams.gravity = Gravity.LEFT | Gravity.TOP;
            floatBallParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            floatBallParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // 不阻塞事件传递到后面的窗口
            floatBallParams.format = PixelFormat.RGBA_8888;

        }
        return floatBallParams;
    }

    private void updateLocation(int x, int y) {
        floatBallParams.x = x;
        floatBallParams.y = y;
        windowManager.updateViewLayout(floatBall, floatBallParams);
    }

    public int getScreenWidth() {
        return windowManager.getDefaultDisplay().getWidth();
    }

    //=====================获取状态栏宽高方法begin============================//
    private View mStatusBarHelperView;

    private void initStatusBarHelperView() {
        mStatusBarHelperView = new View(context);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        // 不可触摸，不可获得焦点
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        //放在左上角
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        // 需要在manifest里申明android.permission.SYSTEM_ALERT_WINDOW权限
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        windowManager.addView(mStatusBarHelperView, lp);
    }
    private void removeStatusBarHelperView(){
        windowManager.removeView(mStatusBarHelperView);
        mStatusBarHelperView = null;
    }

    private int getStatusBarHeight(){
        int[] windowParams = new int[2];
        int[] screenParams = new int[2];
        mStatusBarHelperView.getLocationInWindow(windowParams);
        mStatusBarHelperView.getLocationOnScreen(screenParams);
        // 如果状态栏隐藏，返回0，如果状态栏显示则返回高度
        return screenParams[1] - windowParams[1];
    }
    //=====================获取状态栏宽高方法end============================//
}