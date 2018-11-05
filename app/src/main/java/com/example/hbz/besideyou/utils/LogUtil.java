package com.example.hbz.besideyou.utils;

import android.util.Log;

import com.example.hbz.besideyou.Constant;


/**
 * @ClassName: ${PACKAGE_NAME}
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/8 22:45
 */

public class LogUtil {
    /**
     * 控制是否显示log日志
     */
    private static boolean isShowLog = Constant.PRINT_LOG;
    /**
     * 默认的打印信息
     */
    private static String defaultMsg = "";
    private static final int V = 1;
    private static final int D = 2;
    private static final int I = 3;
    private static final int W = 4;
    private static final int E = 5;

    /**
     * 初始化控制变量
     *
     * @param isShowLog 是否打印日志
     */
    public static void init(boolean isShowLog) {
        LogUtil.isShowLog = isShowLog;
    }

    /**
     * 初始化控制变量和默认日志
     *
     * @param isShowLog  是否打印日志
     * @param defaultMsg 默认日志
     */
    public static void init(boolean isShowLog, String defaultMsg) {
        LogUtil.isShowLog = isShowLog;
        LogUtil.defaultMsg = defaultMsg;
    }

    public static void v() {
        llog(V, null, defaultMsg);
    }

    public static void v(Object obj) {
        llog(V, null, obj);
    }

    public static void v(String tag, Object obj) {
        llog(V, null, obj);
    }

    public static void d() {
        llog(D, null, defaultMsg);
    }

    public static void d(Object obj) {
        llog(D, null, obj);
    }

    public static void d(String tag, Object obj) {
        llog(D, null, obj);
    }

    public static void i() {
        llog(I, null, defaultMsg);
    }

    public static void i(Object obj) {
        llog(I, null, obj);
    }

    public static void i(String tag, String obj) {
        llog(I, tag, obj);
    }

    public static void w() {
        llog(W, null, defaultMsg);
    }

    public static void w(Object obj) {
        llog(W, null, obj);
    }

    public static void w(String tag, Object obj) {
        llog(W, tag, obj);
    }

    public static void e() {
        llog(E, null, defaultMsg);
    }

    public static void e(Object obj) {
        llog(E, null, obj);
    }

    public static void e(String tag, Object obj) {
        llog(E, tag, obj);
    }

    /**
     * 执行打印的方法
     *
     * @param type 日志类型
     * @param obj  打印的信息对象
     */
    public static void llog(int type, String tag, Object obj) {
        String msg;
        if (!isShowLog) {
            return;
        }

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        int index = 4;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        StringBuilder tempStr = new StringBuilder();
        tempStr.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodName).append(" ] ");

        if (obj == null) {
            msg = "这是空对象。";
        } else {
            msg = obj.toString();
        }
        if (msg != null) {
            tempStr.append(msg);
        }
        if (tag == null) {
            tag = className;
        }

        String logStr = tempStr.toString();
        switch (type) {
            case V:
                Log.v(tag, logStr);
                break;
            case D:
                Log.d(tag, logStr);
                break;
            case I:
                Log.i(tag, logStr);
                break;
            case W:
                Log.w(tag, logStr);
                break;
            case E:
                Log.e(tag, logStr);
                break;
            default:
        }
    }
}
