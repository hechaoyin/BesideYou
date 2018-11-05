package com.example.hbz.besideyou.application;

import com.example.hbz.besideyou.application.base.LifecycleManagerApplication;

/**
 * @Description: 添加SDK初始化，其他回调
 * @Author: HBZ
 * @Date: 2018/11/2 0:15
 */
public class BaseApplication extends LifecycleManagerApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // 注册前后台检测
        registerActivityLifecycleCallbacks(AppFrontBackHelper.getInstance());
    }

    @Override
    protected void initAddCallbacks() {

    }
}
