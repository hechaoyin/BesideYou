package com.example.hbz.besideyou.utils;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * @ClassName: com.example.hbz.besideyou.utils
 * @Description:
 * @Author: HBZ
 * @Date: 2018/5/10 16:51
 */

public class DialogUtil {

    private static ProgressDialog dialog;

    public static void showProgressDialog(Activity context, String message) {
        if (context == null) {
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void closeProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
                dialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
