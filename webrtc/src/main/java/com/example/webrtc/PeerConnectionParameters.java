package com.example.webrtc;

/**
 * @ClassName: com.example.webrtc
 * @Description:
 * @Author: HBZ
 * @Date: 2018/4/2 16:55
 */

public class PeerConnectionParameters {


    public boolean videoEnabled; // 是否启动视频
    public boolean audioEnabled; // 是否启动音频
    public boolean dataChannelEnabled; // 是否启动数据通道

    public int videoWidth; // 视频的宽度
    public int videoHeight; // 视频的高度
    public int videoFps;//视频的帧率


    public boolean videoCodecHwAcceleration; // 是否启动视频硬件加速

    public PeerConnectionParameters() {
        videoEnabled = false;
        audioEnabled = true;
        dataChannelEnabled = true;
        videoWidth = 480;
        videoHeight = 800;
        videoFps = 30;

        videoCodecHwAcceleration = false;
    }
}
