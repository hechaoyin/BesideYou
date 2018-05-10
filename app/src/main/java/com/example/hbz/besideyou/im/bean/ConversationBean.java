package com.example.hbz.besideyou.im.bean;

import android.text.TextUtils;

import com.example.cosxml.Constant;
import com.example.im.data.FriendContactsData;
import com.tencent.TIMConversation;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ConversationBean
 * @Description: 会话界面中 会话item
 * @Author: HBZ
 * @Date: 2018/3/28 14:39
 */

public class ConversationBean {

    private TIMConversation conversation;
    private String faceUrl;
    private String name;
    private String lastMsg;
    private String lastMsgTime;

    public ConversationBean(TIMConversation conversation) {
        initData(conversation);
    }

    public void initData(TIMConversation conversation) {
        if (conversation == null) {
            return;
        }
        this.conversation = conversation;
        List<TIMMessage> lastMsgs = conversation.getLastMsgs(1);
        if (lastMsgs == null || lastMsgs.size() < 1) {
            return;
        }
        TIMMessage timMessage = lastMsgs.get(0);

        // 对方头像
        TIMUserProfile friendProfile = FriendContactsData.getInstance().getFriendProfile(conversation.getPeer());
        if (friendProfile!=null){
            faceUrl = friendProfile.getFaceUrl();
            if (!TextUtils.isEmpty(friendProfile.getNickName())){
                name = friendProfile.getNickName();
            }else {
                name = conversation.getPeer();
            }
        }else {
            //对方名字
            name = conversation.getPeer();
        }


        // 最后的消息str
        lastMsg = getLastMessageSummary(timMessage);

        //最后一条消息的时间
        long time = getLastMessageTime(conversation, timMessage);
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日  HH:mm:ss     ");
        Date curDate = new Date(time);
        lastMsgTime = formatter.format(curDate);
    }

    /**
     * 获取最后一条消息摘要
     */
    public String getLastMessageSummary(TIMMessage timMessage) {
        if (conversation.hasDraft()) {
            return "[草稿]";
        } else {
            TIMElem element = timMessage.getElement(0);
            if (element.getType().equals(TIMElemType.Text)) {
                TIMTextElem element1 = (TIMTextElem) element;
                return element1.getText();
            }
        }
        return "";
    }

    private long getLastMessageTime(TIMConversation conversation, TIMMessage timMessage) {
        if (conversation.hasDraft()) {
            if (timMessage == null || timMessage.timestamp() < conversation.getDraft().getTimestamp()) {
                return conversation.getDraft().getTimestamp();
            } else {
                return timMessage.timestamp();
            }
        }
        if (timMessage == null) return 0;
        return timMessage.timestamp();
    }

    public TIMConversation getConversation() {
        return conversation;
    }

    public void setConversation(TIMConversation conversation) {
        this.conversation = conversation;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }
}
