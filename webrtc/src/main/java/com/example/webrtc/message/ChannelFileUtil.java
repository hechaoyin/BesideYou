package com.example.webrtc.message;

/**
 * @ClassName: com.example.webrtc.message
 * @Description:
 * @Author: HBZ
 * @Date: 2018/4/11 16:13
 */

public class ChannelFileUtil {
    public static final byte FILE_BEGIN = -1; //文件开始标志
    public static final byte FILE_END = -2; // 文件结束标志
    public static final byte FILE_RECEIVE_SUCCEED = -3;  // 文件发送成功回调数据

    public static final int FILE_SEND_COUNT = 10;//每次最多发送发送量（发送文件时每100次需要一个成功发送的回调）

    public static boolean isFileBegin(byte[] msg) {
        return msg.length >= 1 && msg[0] == FILE_BEGIN;
    }

    public static boolean isFileEnd(byte[] msg) {
        return msg.length == 1 && msg[0] == FILE_END;
    }

}
