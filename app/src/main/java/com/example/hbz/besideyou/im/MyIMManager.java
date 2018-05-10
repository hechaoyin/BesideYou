package com.example.hbz.besideyou.im;

import android.content.Intent;

import com.example.hbz.besideyou.BesideYouApp;
import com.example.hbz.besideyou.activity.OffLineActivity;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.im.event.ConnectionEvent;
import com.example.im.event.FriendshipEvent;
import com.example.im.event.GroupEvent;
import com.example.im.event.MessageEvent;
import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
import com.tencent.TIMUserStatusListener;

/**
 * @ClassName: com.example.im
 * @Description: 腾讯云云语音的初始化工作（BesideYouApp 中初始化）
 * @Author: HBZ
 * @Date: 2018/3/22 16:11
 */

public class MyIMManager {
    private static MyIMManager instance = new MyIMManager();

    public static MyIMManager getInstance() {
        return instance;
    }

    public void initTIM() {
        //初始化消息监听
        MessageEvent.getInstance();

        //初始化网络事件监听器
        ConnectionEvent.getInstance();

        // 初始化好友关系链改变监听器
        FriendshipEvent.getInstance();

        // 群组改变监听器
        GroupEvent.getInstance();

        TIMManager timManager = TIMManager.getInstance();

        //初始化互踢下线
        initUserStatusListener(timManager);

        //初始化其他设置
        initSetting(timManager);

        /*通讯管理器初始化*/
        timManager.init(BesideYouApp.getAppContext());

    }

    private void initSetting(TIMManager timManager) {
        //关闭日志
        timManager.setLogLevel(TIMLogLevel.OFF);

        //禁用消息的存储
        //timManager.disableStorage();

        //禁用crash上报
        timManager.disableCrashReport();

        //开启关系链资料存储 （FriendshipEvent 中已经启动）
        //timManager.enableFriendshipStorage(true);
    }

    private void initUserStatusListener(TIMManager timManager) {
        //互踢下线逻辑
        timManager.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                //被踢下线时回调
                LogUtil.d("用户被踢下线");
                Intent intent = new Intent(BesideYouApp.getAppContext(), OffLineActivity.class);
                intent.putExtra(OffLineActivity.OFF_LINE_STYLE, OffLineActivity.FORCE_Off_LINE);
                BesideYouApp.getAppContext().startActivity(intent);
            }

            @Override
            public void onUserSigExpired() {
                //票据过期时回调
                LogUtil.d("用户登录票据过期时");
                Intent intent = new Intent(BesideYouApp.getAppContext(), OffLineActivity.class);
                intent.putExtra(OffLineActivity.OFF_LINE_STYLE, OffLineActivity.USER_SIG_EXPIRED);
                BesideYouApp.getAppContext().startActivity(intent);
            }
        });
    }
}
