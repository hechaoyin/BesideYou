package com.example.hbz.besideyou.utils;

import android.widget.Toast;

import com.example.hbz.besideyou.BesideYouApp;


/**
 * @ClassName: com.example.besideyou.utils
 * @Description: 实现不管我们触发多少次Toast调用，都只会持续一次Toast显示的时长
 * @Author: HBZ
 * @Date: 2018/3/13 9:34
 */

public class ToastUtil {
    private static Toast toast = null;

    public static void showShortToast(int retId) {
        if (BesideYouApp.getAppContext() == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(BesideYouApp.getAppContext(), retId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(retId);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showShortToast(String hint) {
        if (BesideYouApp.getAppContext() == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(BesideYouApp.getAppContext(), hint, Toast.LENGTH_SHORT);
        } else {
            toast.setText(hint);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }


    public static void showLongToast(int retId) {
        if (BesideYouApp.getAppContext() == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(BesideYouApp.getAppContext(), retId, Toast.LENGTH_LONG);
        } else {
            toast.setText(retId);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }


    public static void showLongToast(String hint) {
        if (BesideYouApp.getAppContext() == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(BesideYouApp.getAppContext(), hint, Toast.LENGTH_LONG);
        } else {
            toast.setText(hint);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }
}
