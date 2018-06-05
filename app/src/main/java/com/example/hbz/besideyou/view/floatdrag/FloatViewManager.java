package com.example.hbz.besideyou.view.floatdrag;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
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
            int w = WindowManager.LayoutParams.MATCH_PARENT;
            int h = WindowManager.LayoutParams.MATCH_PARENT;
            int flags = 0;
            int type;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //解决Android 7.1.1起不能再用Toast的问题（先解决crash）
                if (Build.VERSION.SDK_INT > 24) {
                    type = WindowManager.LayoutParams.TYPE_PHONE;
                } else {
                    type = WindowManager.LayoutParams.TYPE_TOAST;
                }
            } else {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            floatBallParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);

            floatBallParams.gravity = Gravity.LEFT | Gravity.TOP;
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


    private int getStatusBarHeight() {
        int height = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            height = context.getResources().getDimensionPixelSize(resId);
        }
        return height;
    }
}