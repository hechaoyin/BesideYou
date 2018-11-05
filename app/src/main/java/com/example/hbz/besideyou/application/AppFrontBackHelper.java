package com.example.hbz.besideyou.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.util.Observable;

/**
 * 前后台切换检测
 *
 * @author HCY
 * @date 2018/8/1
 */

public class AppFrontBackHelper extends Observable implements Application.ActivityLifecycleCallbacks {

    private static AppFrontBackHelper instance = new AppFrontBackHelper();
    private int activityStartCount = 0;

    public enum AppStart {
        /**
         * App状态为前台
         */
        FRONT,
        /**
         * App状态为后台
         */
        BACK
    }

    private AppFrontBackHelper() {
    }

    public static AppFrontBackHelper getInstance() {
        return instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        activityStartCount++;
        Log.d(this.getClass().getName(), "activityStartCount"+activityStartCount);
        //数值从0变到1说明是从后台切到前台
        if (activityStartCount == 1) {
            //从后台切到前台
            Log.d(this.getClass().getName(), "从后台切到前台");
            setChanged();
            notifyObservers(AppStart.FRONT);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityStartCount--;
        Log.d(this.getClass().getName(), "onActivityStopped"+activityStartCount);
        //数值从1到0说明是从前台切到后台
        if (activityStartCount == 0) {
            //从前台切到后台
            Log.d(this.getClass().getName(), "从前台切到后台");
            setChanged();
            notifyObservers(AppStart.BACK);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
