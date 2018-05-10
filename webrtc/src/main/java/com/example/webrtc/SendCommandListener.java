package com.example.webrtc;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * @ClassName: com.example.webrtc
 * @Description: 发送指令的接口, WebRtc通过这个接口向连接端发送指令。
 * （需要在连接之前实例化，否则指令发不出去，连接不会成功）
 * @Author: HBZ
 * @Date: 2018/4/3 10:17
 */

public interface SendCommandListener {
    /**
     * 发送Offer/Answer 指令
     */
    void sendSdp(String id, SessionDescription sessionDescription);

    /**
     * 发送Candidate 指令
     */
    void sendCandidate(String id, IceCandidate iceCandidate);
}
