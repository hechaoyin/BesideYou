package com.example.hbz.besideyou.application.base;

import android.content.Context;
import android.content.res.Configuration;

/**
 * @Description:
 * @Author: HBZ
 * @Date: 2018/11/1 23:36
 */
public interface ApplicationLifecycleCallbacks {
    /**
     * 程序创建的时候执行
     */
    void onCreate();

    /**
     * 程序终止的时候执行
     */
    void onTerminate();

    /**
     * 低内存的时候执行
     */
    void onLowMemory() ;

    /**
     * 程序在内存清理的时候执行
     */
    void onTrimMemory(int level);

    void onConfigurationChanged(Configuration newConfig) ;

    void attachBaseContext(Context context) ;
}
