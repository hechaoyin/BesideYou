package com.example.hbz.besideyou.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by HBZ on 2018/1/24.
 */

public class StatusBarUtils {

    /**
     * 设置系统状态栏为透明,并且将顶部的View留出状态栏空间，（即：状态栏背景与顶部View一致）
     *
     * @param activity 当前活动
     * @param topView  顶部view
     */
    public static void setStatus(Activity activity, View topView) {
        // 判断当前设备版本号是否为4.4以上，如果是，则通过调用setTranslucentStatus让状态栏变透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarUtils.setTranslucentStatus(activity, true);
            int statusHeight = StatusBarUtils.getStatusBarHeight(activity);
            if (topView != null) {
                topView.setPadding(topView.getPaddingLeft(), topView.getPaddingTop() + statusHeight,
                        topView.getPaddingRight(), topView.getPaddingBottom());

                ViewGroup.LayoutParams layoutParams = topView.getLayoutParams();
                layoutParams.height += statusHeight;
            }
        }
    }

    /**
     * 设置系统状态栏为透明，并且系统会取消状态栏原本所占的空间，普通布局会上移并占用状态栏所在位置的空间
     *
     * @param activity
     * @param on
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 获取当前设备状态栏高度（获取系统状态栏高度）
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}