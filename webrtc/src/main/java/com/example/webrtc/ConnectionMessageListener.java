package com.example.webrtc;

import org.webrtc.DataChannel;
import org.webrtc.MediaStream;

/**
 * @ClassName: com.example.webrtc
 * @Description:
 * @Author: HBZ
 * @Date: 2018/4/3 10:36
 */

public interface ConnectionMessageListener {
    /**
     * 接收到远程媒体流
     */
    void onAddStream(String id, MediaStream mediaStream);

    /**
     * 收到通道消息。
     */
    void onReceiveDataChannelMessage(String id, DataChannel.Buffer buffer);

}
