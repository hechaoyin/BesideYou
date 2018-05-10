package com.example.im;


import com.tencent.TIMMessage;

/**
 * @ClassName: ${PACKAGE_NAME}
 * @Description:
 * @Author: HBZ
 * @Date: 2018/03/24 17:42
 */

public class MessageConstant {

    public static final int MESSAGE_TYPE_INVALID = -1;// 无效消息
    public static final int MESSAGE_TYPE_TEXT = 1;//文本消息
    public static final int MESSAGE_TYPE_IMAGE = 2;//文本消息
    public static final int MESSAGE_TYPE_SOUND = 3;// 语音消息
    public static final int MESSAGE_TYPE_LOCATION = 4;// 地理位置消息
    public static final int MESSAGE_TYPE_FILE = 5;//小文件消息
    public static final int MESSAGE_TYPE_CUSTOM = 6;
    public static final int MESSAGE_TYPE_FACE = 7;//表情消息
    public static final int MESSAGE_TYPE_GROUP_TIPS = 8;
    public static final int MESSAGE_TYPE_GROUP_SYSTEM = 9;
    public static final int MESSAGE_TYPE_SNS_TIPS = 10;
    public static final int MESSAGE_TYPE_PROFILE_TIPS = 11;
    public static final int MESSAGE_TYPE_VIDEO = 12;//视频消息

    public static int getMessageType(TIMMessage message) {
        long elementCount = message.getElementCount();
        if (elementCount == 1) {
            switch (message.getElement(0).getType()) {
                case Invalid:
                    return MESSAGE_TYPE_INVALID;
                case Text:
                    return MESSAGE_TYPE_TEXT;
                case Image:
                    return MESSAGE_TYPE_IMAGE;
                case Sound:
                    return MESSAGE_TYPE_SOUND;
                case Location:
                    return MESSAGE_TYPE_LOCATION;
                case File:
                    return MESSAGE_TYPE_FILE;
                case Custom:
                    return MESSAGE_TYPE_CUSTOM;
                case Face:
                    return MESSAGE_TYPE_FACE;
                case GroupTips:
                    return MESSAGE_TYPE_GROUP_TIPS;
                case GroupSystem:
                    return MESSAGE_TYPE_GROUP_SYSTEM;
                case SNSTips:
                    return MESSAGE_TYPE_SNS_TIPS;
                case ProfileTips:
                    return MESSAGE_TYPE_PROFILE_TIPS;
                case Video:
                    return MESSAGE_TYPE_VIDEO;
                default:
                    break;
            }
        } else {
            //自定义消息。
        }
        return Integer.MIN_VALUE;
    }
}
