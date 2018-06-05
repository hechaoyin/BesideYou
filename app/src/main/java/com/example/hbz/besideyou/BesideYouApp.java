package com.example.hbz.besideyou;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.example.hbz.besideyou.im.MyIMManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * @ClassName: com.example.besideyou.BesideYouApp
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/16 10:01
 */

public class BesideYouApp extends MultiDexApplication {

    private static BesideYouApp context;
    private static RefWatcher refWatcher;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        // 启动内存检测
        refWatcher = LeakCanary.install(this);

        // 初始化腾讯云云语音
        MyIMManager.getInstance().initTIM();
    }

    public static RefWatcher getRefWatcher() {
        return refWatcher;
    }

    @Override
    public void startActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        super.startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
