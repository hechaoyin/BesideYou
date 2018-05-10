package com.example.im.event;

import com.tencent.TIMConnListener;
import com.tencent.TIMManager;

import java.util.Observable;

/**
 * @ClassName: com.example.im.event
 * @Description: 网络事件通知
 * @Author: HBZ
 * @Date: 2018/3/22 16:45
 */

public class ConnectionEvent extends Observable implements TIMConnListener {

    private volatile static ConnectionEvent instance;

    private ConnectionEvent() {
        //注册消息监听器
        TIMManager.getInstance().setConnectionListener(this);
    }

    public static ConnectionEvent getInstance() {
        if (instance == null) {
            synchronized (ConnectionEvent.class) {
                if (instance == null) {
                    instance = new ConnectionEvent();
                }
            }
        }
        return instance;
    }

    @Override
    public void onConnected() {//连接建立
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.Connected, null));
    }

    @Override
    public void onDisconnected(int code, String desc) {//连接断开
        //接口返回了错误码code和错误描述desc，可用于定位连接断开原因
        //错误码code含义请参见错误码表
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.Disconnected, "错误码:" + code + "\n错误描述:" + desc));
    }

    @Override
    public void onWifiNeedAuth(String s) {
        //无线网络需要身份验证
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.WifiNeedAuth, s));
    }

    public class NotifyCmd {
        public final NotifyType type;
        public final Object data;

        NotifyCmd(NotifyType type, Object data) {
            this.type = type;
            this.data = data;
        }

    }

    public enum NotifyType {
        Connected,//网络连接错误
        Disconnected,//网络连接错误
        WifiNeedAuth,//无线网络需要身份验证
    }

}
