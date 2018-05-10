package com.example.im.data;

import android.util.Log;

import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;

/**
 * @ClassName: 存储所有好友的信息
 * @Description:
 * @Author: HBZ
 * @Date: 2018/5/10 10:44
 */

public class FriendContactsData extends Observable {
    private static final String TAG = "FriendContactsData";
    private volatile static FriendContactsData instance;

    private HashMap<String, TIMUserProfile> friendContacts = new HashMap<>();

    private FriendContactsData() {
        initContacts();
    }

    public static FriendContactsData getInstance() {
        if (instance == null) {
            synchronized (FriendContactsData.class) {
                if (instance == null) {
                    instance = new FriendContactsData();
                }
            }
        }
        return instance;
    }

    private void initContacts() {
        Log.d(TAG, "获取好友列表: ");
        //获取好友列表
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int code, String desc) {
                //，错误码code和错误描述desc可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e(TAG, "获取好友列表失败: " + code + " desc");
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {
                if (result == null) {
                    return;
                }
                friendContacts.clear();
                for (TIMUserProfile timUserProfile : result) {
                    friendContacts.put(timUserProfile.getIdentifier(), timUserProfile);
                }
                setChanged();
                notifyObservers(new NotifyCmd(NotifyType.REFRESH, null));
            }
        });
    }

    /**
     * 主动刷新联系人
     */
    public void onRefreshData() {
        initContacts();
    }

    public HashMap<String, TIMUserProfile> getFriendContacts() {
        return friendContacts;
    }

    public TIMUserProfile getFriendProfile(String identifier) {
        return friendContacts.get(identifier);
    }

    public class NotifyCmd {
        public NotifyType type;
        public Object data;

        public NotifyCmd(NotifyType type, Object data) {
            this.type = type;
            this.data = data;
        }
    }

    public enum NotifyType {
        REFRESH,//刷新数据
        ADD,//添加好友
        DEL//删除好友
    }
}
