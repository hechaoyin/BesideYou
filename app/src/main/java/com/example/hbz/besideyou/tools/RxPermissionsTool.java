package com.example.hbz.besideyou.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import com.example.hbz.besideyou.utils.LogUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * @ClassName: com.example.hbz.besideyou.tools
 * @Description:
 * @Author: HBZ
 * @Date: 2018/6/1 1:22
 */
public class RxPermissionsTool {

    @SuppressLint("CheckResult")
    public static void initRxPermissions(Activity activity) {
        // where this is an Activity instance
        RxPermissions rxPermissions = new RxPermissions(activity);
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
                        LogUtil.d(permission.name + " is granted（用户已经同意该权限）.");
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        LogUtil.d(permission.name + " is denied. More info should be provided.（用户拒绝了该权限，没有选中『不再询问』）");
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        LogUtil.d(permission.name + " is denied.（用户拒绝了该权限，并且选中『不再询问』）");
                    }
                });
    }
}
