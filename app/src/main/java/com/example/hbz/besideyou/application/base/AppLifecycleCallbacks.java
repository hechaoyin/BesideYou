package com.example.hbz.besideyou.application.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

/**
 * @Description: Application的扩展对象
 * @Author: HBZ
 * @Date: 2018/11/1 23:17
 */
public abstract class AppLifecycleCallbacks implements ApplicationLifecycleCallbacks {
    protected Application mApplication;

    public AppLifecycleCallbacks(Application mApplication) {
        this.mApplication = mApplication;
    }

    public Application getmApplication() {
        return mApplication;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void attachBaseContext(Context context) {

    }
}
