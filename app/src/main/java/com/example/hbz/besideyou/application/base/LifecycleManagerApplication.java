package com.example.hbz.besideyou.application.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import java.util.ArrayList;

/**
 * @Description: Application生命周期 执行操作类
 * @Author: HBZ
 * @Date: 2018/11/1 23:34
 */
public abstract class LifecycleManagerApplication extends Application {
    private final ArrayList<ApplicationLifecycleCallbacks> mApplicationLifecycleCallbacks = new ArrayList<>();

    /**
     * 添加Application生命周期回调
     *
     * @param callback Application生命周期回调
     */
    public void addApplicationLifecycleCallback(ApplicationLifecycleCallbacks callback) {
        synchronized (mApplicationLifecycleCallbacks) {
            mApplicationLifecycleCallbacks.add(callback);
        }
    }

    /**
     * 除移Application生命周期回调
     *
     * @param callback Application生命周期回调
     */
    public void removeApplicationLifecycleCallback(ApplicationLifecycleCallbacks callback) {
        synchronized (mApplicationLifecycleCallbacks) {
            mApplicationLifecycleCallbacks.remove(callback);
        }
    }

    /**
     * 程序创建的时候执行
     */
    @Override
    public void onCreate() {
        super.onCreate();
        initAddCallbacks();
        ApplicationLifecycleCallbacks[] callbacks = collectApplicationLifecycleCallbacks();
        for (ApplicationLifecycleCallbacks callback : callbacks) {
            if (callback != null) {
                callback.onCreate();
            }
        }
    }

    /**
     * 初始化添加回调
     */
    protected abstract void initAddCallbacks();

    /**
     * 程序终止的时候执行
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        ApplicationLifecycleCallbacks[] callbacks = collectApplicationLifecycleCallbacks();
        for (ApplicationLifecycleCallbacks callback : callbacks) {
            if (callback != null) {
                callback.onTerminate();
            }
        }
    }

    /**
     * 低内存的时候执行
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ApplicationLifecycleCallbacks[] callbacks = collectApplicationLifecycleCallbacks();
        for (ApplicationLifecycleCallbacks callback : callbacks) {
            if (callback != null) {
                callback.onLowMemory();
            }
        }
    }

    /**
     * 程序在内存清理的时候执行
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ApplicationLifecycleCallbacks[] callbacks = collectApplicationLifecycleCallbacks();
        for (ApplicationLifecycleCallbacks callback : callbacks) {
            if (callback != null) {
                callback.onTrimMemory(level);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ApplicationLifecycleCallbacks[] callbacks = collectApplicationLifecycleCallbacks();
        for (ApplicationLifecycleCallbacks callback : callbacks) {
            if (callback != null) {
                callback.onConfigurationChanged(newConfig);
            }
        }
    }

    @Override
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        ApplicationLifecycleCallbacks[] callbacks = collectApplicationLifecycleCallbacks();
        for (ApplicationLifecycleCallbacks callback : callbacks) {
            if (callback != null) {
                callback.attachBaseContext(context);
            }
        }
    }

    /**
     * 获取所有ApplicationLifecycleCallbacks的副本
     *
     * @return 所有回调
     */
    public ApplicationLifecycleCallbacks[] collectApplicationLifecycleCallbacks() {
        // new 一个对象，防止返回空，用for(T item: expr)时出错
        ApplicationLifecycleCallbacks[] callbacks = new ApplicationLifecycleCallbacks[0];
        synchronized (mApplicationLifecycleCallbacks) {
            if (mApplicationLifecycleCallbacks.size() > 0) {
                callbacks = (ApplicationLifecycleCallbacks[]) mApplicationLifecycleCallbacks.toArray();
            }
        }
        return callbacks;
    }
}
