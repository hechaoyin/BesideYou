package com.example.webrtc;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @ClassName: com.example.webrtc
 * @Description:
 * @Author: HBZ
 * @Date: 2018/4/2 16:24
 */

public class PeerManage {
    private static final String TAG = "PeerManage";

    private static volatile PeerManage instance = null;


    private HashMap<String, Peer> rtcClients = new HashMap<>();

    private EglBase rootEglBase;
    private MediaStream mLocalMS;// 本地媒体流（包含视频流：localVideoTrack 和 音频流：localAudioSource）

    private VideoCapturer videoCapturer;// 本地视频捕获者
    private VideoSource videoSource;// 本地视频源
    private AudioSource localAudioSource;// 本地音频源

    private PeerConnectionFactory factory;
    private PeerConnectionParameters pcParams;
    private MediaConstraints pcConstraints; // 媒体通道
    private List<PeerConnection.IceServer> iceServers = new ArrayList<>();

    private ConnectionMessageListener connectionMessage;
    private SendCommandListener sendCommand;//发送控制命令监听器

    private PeerManage(Context context, PeerConnectionParameters pcParams) {
        context = context.getApplicationContext();
        if (pcParams == null) {
            this.pcParams = new PeerConnectionParameters();
        }

        //初始化PeerConnectionFactory
        initPeerConnectionFactory(context);

        //初始化VideoTrack
        initVideoTrack();

        //初始化连接通道
        initPeerConnection();

        //初始化Servers
        initIceServers();
    }

    /**
     * 带参数初始化。
     *
     * @param context  上下文
     * @param pcParams WebRtc配置参数
     */
    public static void init(Context context, PeerConnectionParameters pcParams) {
        if (instance == null) {
            synchronized (PeerManage.class) {
                if (instance == null) {
                    instance = new PeerManage(context, pcParams);
                }
            }
        }
    }

    /**
     * 获取单例ConnectionClientManage实例
     *
     * @param context 上下文
     * @return
     */
    public static PeerManage getInstance(Context context) {
        if (instance == null) {
            synchronized (PeerManage.class) {
                if (instance == null) {
                    instance = new PeerManage(context, null);
                }
            }
        }
        return instance;
    }

    private void initPeerConnectionFactory(Context context) {
        PeerConnectionFactory.initializeAndroidGlobals(
                /*上下文，可自定义监听*/context,
                /*是否支持硬件加速*/pcParams.videoCodecHwAcceleration);
        PeerConnectionFactory.Options opt = null;
        factory = new PeerConnectionFactory(opt);
    }

    private void initVideoTrack() {

        // 创建本地媒体流
        mLocalMS = factory.createLocalMediaStream("ARDAMS");

        // 本地媒体流添加视频流
        if (pcParams.videoEnabled) {
            if (rootEglBase == null) {
                rootEglBase = EglBase.create();
            }
            EglBase.Context renderEGLContext = rootEglBase.getEglBaseContext();

            factory.setVideoHwAccelerationOptions(renderEGLContext, renderEGLContext);
            MediaConstraints videoConstraints = new MediaConstraints();
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));

            videoCapturer = createCameraCapturer(new Camera1Enumerator(true));

            videoSource = factory.createVideoSource(videoCapturer/*, videoConstraints*/);

            videoCapturer.startCapture(pcParams.videoWidth, pcParams.videoHeight, pcParams.videoFps);//视频源

            // 本地视频流
            VideoTrack localVideoTrack = factory.createVideoTrack("ARDAMSv0", videoSource);
            localVideoTrack.setEnabled(true);

            // ------------ 添加视频流 --------------
            mLocalMS.addTrack(localVideoTrack);
        }

        // 本地媒体流添加音频流
        if (pcParams.audioEnabled) {
            localAudioSource = factory.createAudioSource(new MediaConstraints());
            AudioTrack localAudioTrack = factory.createAudioTrack("ARDAMSa0", localAudioSource);

            // ------------ 添加音频流 --------------
            mLocalMS.addTrack(localAudioTrack);
        }

    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        //寻找相机

        final String[] deviceNames = enumerator.getDeviceNames();
        // First, try to find front facing camera
//        Log.d(TAG, "寻找前置摄像头。");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
//                Log.d(TAG, "创建前置摄像头的前置摄像头。");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        // 没有发现的前置摄像头，尝试其他的东西
//        Log.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
//                Log.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    private void initPeerConnection() {
        pcConstraints = new MediaConstraints();
        if (pcParams.audioEnabled) {
            pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        }
        if (pcParams.videoEnabled) {
            pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        }
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));

        // 要使用SCTP DataChannels，你必须让它默认 （或将其设置为 false）
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("RtpDataChannels", "false"));
    }

    private void initIceServers() {
        iceServers.add(new PeerConnection.IceServer("stun:23.21.150.121"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
    }

    /********************************************************
     * 处理控制指令。
     * @param id  对方用户的ID
     * @param type 指令类型
     * @param data 数据
     *
     */
    public void handleCommand(String id, String type, JSONObject data) {
        switch (type) {
            case "offer":
                handleOfferCommand(id, data);
                break;
            case "candidate":
                handleCandidateCommand(id, data);
                break;
            case "answer":
                handleAnswerCommand(id, data);
                break;
            default:
                break;
        }
    }

    private void handleAnswerCommand(String id, JSONObject answer) {
        SessionDescription sdp = null;
        try {
            sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(answer.getString("type")),
                    answer.getString("sdp")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (sdp == null) {
            //TODO: answer指令有误。
            return;
        } else if (id == null) {
            return;
        }
        Peer connectionClient = rtcClients.get(id);
        if (connectionClient == null) {
            return;
        }
        connectionClient.handleAnswer(sdp);

    }

    private void handleCandidateCommand(String id, JSONObject candidate) {
        IceCandidate iceCandidate = null;
        try {
            iceCandidate = new IceCandidate(candidate.getString("sdpMid"),
                    candidate.getInt("sdpMLineIndex"), candidate.getString("candidate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iceCandidate == null) {
            //TODO: iceCandidate指令有误。
            return;
        }

        Peer connectionClient = rtcClients.get(id);
        if (connectionClient == null) {
            return;
        }

        connectionClient.handleCandidate(iceCandidate);
    }

    private void handleOfferCommand(String id, JSONObject offer) {
        Log.d(TAG, "handleOfferCommand: ");
        SessionDescription sdp = null;
        try {
            sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(offer.getString("type")),
                    offer.getString("sdp")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (sdp == null) {
            //TODO: offer指令有误。
            Log.d(TAG, "handleOfferCommand: offer指令有误");
            return;
        } else if (id == null) {
            Log.d(TAG, "handleOfferCommand: Id指令有误");
            return;
        }
        Peer connectionClient = rtcClients.get(id);
        if (connectionClient == null) {
            Log.d(TAG, "handleOfferCommand: 没有此Id 客户端");
            return;
        }
        connectionClient.handleOffer(sdp);
    }


    public void addClient(String id, boolean isCreateOffer) {
        if (!rtcClients.containsKey(id)) {
            Peer client = new Peer(id);
            rtcClients.put(id, client);
            if (isCreateOffer) {
                client.createOffer();
            }
        }
    }


    public void setConnectionMessage(ConnectionMessageListener connectionMessage) {
        this.connectionMessage = connectionMessage;
    }

    public void setSendCommand(SendCommandListener sendCommand) {
        this.sendCommand = sendCommand;
    }

    public HashMap<String, Peer> getRtcClients() {
        return rtcClients;
    }

    public void release() {
        if (mLocalMS != null) {
            mLocalMS.dispose();
        }
        if (videoCapturer != null) {
            videoCapturer.dispose();
        }
        if (videoSource != null) {
            videoSource.dispose();
        }
        if (localAudioSource != null) {
            localAudioSource.dispose();
        }

        if (rtcClients != null && rtcClients.size() > 0) {
            //释放PeerConnection
            for (Peer peer : rtcClients.values()) {
                if (peer != null) {
                    peer.release();//释放资源
                }
            }
            rtcClients.clear();
        }
    }

    public void releasePeer(String peerKet) {
        if (rtcClients.containsKey(peerKet)) {
            Peer peer = rtcClients.get(peerKet);
            if (peer != null) {
                peer.release();
            }
            rtcClients.remove(peerKet);
        }

    }

    public class Peer {
        private String id;

        private PeerConnection pc;
        private PeerConnection.Observer peerConnectionObserver; // 连接通道监听器
        private SdpObserver sdpObserver;    // SDP 指令观察者
        private DataChannel.Observer dataChannelObserver; // 数据通道观察者

        private DataChannel dataChannel;

        public Peer(String id) {
            this.id = id;
            initObserver();
            //创建连接通道
            pc = factory.createPeerConnection(iceServers, pcConstraints, peerConnectionObserver);
            //添加本地媒体流localMS，也可以连接完成后添加。
            // pc.addStream(mLocalMS);
            pc.addStream(mLocalMS);

            createDataChannel();
        }

        public void createDataChannel() {
            if (dataChannel != null) {
                dataChannel.dispose();
            }
            Log.d(TAG, "createDataChannel: 创建数据通道");

            /*
            DataChannel.Init 可配参数说明：
            ordered：是否保证顺序传输；
            maxRetransmitTimeMs：重传允许的最长时间；
            maxRetransmits：重传允许的最大次数；
             */
            DataChannel.Init init = new DataChannel.Init();
            init.ordered = true;

            dataChannel = pc.createDataChannel("dataChannel", init);
        }

        private void initObserver() {
            dataChannelObserver = new DataChannel.Observer() {
                @Override
                public void onBufferedAmountChange(long l) {    // 缓存量改变
                    Log.d(TAG, "onBufferedAmountChange: 缓存量改变:" + l);
                }

                @Override
                public void onStateChange() {
                    Log.d(TAG, "onStateChange: 通道状态改变" + dataChannel.state());
                }

                @Override
                public void onMessage(DataChannel.Buffer buffer) {
                    /*
                    Log.d(TAG, "onDataChannel onMessage : " + buffer);
                    ByteBuffer data = buffer.data;
                    byte[] bytes = new byte[data.capacity()];
                    data.get(bytes);
                    String msg = new String(bytes);
                    if (webRtcListener != null) {
                        webRtcListener.onReceiveDataChannelMessage(msg);
                    }*/
//                    Log.d(TAG, "onMessage: 收到通道消息");
                    if (connectionMessage != null) {
                        connectionMessage.onReceiveDataChannelMessage(id, buffer);
                    }
                }
            };
            peerConnectionObserver = new PeerConnection.Observer() {
                @Override
                public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                    //信号改变
                    Log.d(TAG, "PeerConnection.Observer onSignalingChange\n: 信号改变"+signalingState);
                }
                @Override
                public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                    Log.d(TAG, "PeerConnection.Observer onSignalingChange: rtc状态改变" + iceConnectionState);
                    //rtc状态改变
                    switch (iceConnectionState) {
                        case DISCONNECTED:
                            //移除视频
                        case CLOSED:
                            //TODO: 退出WebRtc连接

                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onIceConnectionReceivingChange(boolean b) {
                    Log.d(TAG, "PeerConnection.Observer onIceConnectionReceivingChange: " + b);

                }

                @Override
                public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                    Log.d(TAG, "PeerConnection.Observer onIceGatheringChange: ");

                }

                @Override
                public void onIceCandidate(IceCandidate iceCandidate) {
                    // iceCandidate里面包含穿透所需的信息, 这里将IceCandidate对象的内容发送给对方
                    if (iceCandidate == null) {
                        return;
                    }

                    if (sendCommand != null) {//向对应的id发送Candidate指令
                        sendCommand.sendCandidate(id, iceCandidate);
                    }
                }

                @Override
                public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

                }

                @Override
                public void onAddStream(MediaStream mediaStream) {
                    // 接收到媒体流
                    /*
                    if (mediaStream.videoTracks.size() >= 1) {
                        Log.d("---", "接收到媒体流");
                        //TODO: setSwappedFeeds(isSwappedFeeds);

                        VideoTrack mRemoteVideoTrack = mediaStream.videoTracks.get(0);
                        mRemoteVideoTrack.setEnabled(true);
                        //加载远程视频
                        mRemoteVideoTrack.addRenderer(new VideoRenderer(remoteProxyRenderer));
                    }*/

                    if (connectionMessage != null) {
                        connectionMessage.onAddStream(id, mediaStream);
                    }
                }

                @Override
                public void onRemoveStream(MediaStream mediaStream) {
//                    pc.removeStream(mLocalMS);
                }

                @Override
                public void onDataChannel(DataChannel dataChannel) {
                    Log.d(TAG, "onDataChannel label:" + dataChannel.label());

                    dataChannel.registerObserver(dataChannelObserver);
                }

                @Override
                public void onRenegotiationNeeded() {
                    //重新谈判 需要
                    Log.d(TAG, "PeerConnection.Observer onRenegotiationNeeded: ");
                }

                @Override
                public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                    Log.d(TAG, "PeerConnection.Observer AddTrack: 添加跟踪");
                }
            };
            sdpObserver = new SdpObserver() {
                @Override
                public void onCreateSuccess(SessionDescription sessionDescription) {
                    Log.d(TAG, "创建Offer/Answer成功");
                    // offer信令创建成功后会调用
                    pc.setLocalDescription(this, sessionDescription);//offer信令（SDP描述符）赋给自己的PC对象

                    if (sendCommand != null) {// 发送offer信令给对方
                        sendCommand.sendSdp(id, sessionDescription);
                    }
                }

                @Override
                public void onSetSuccess() {
                    Log.d(TAG, "sdpObserver：" + "设置成功");
                }

                @Override
                public void onCreateFailure(String s) {
                    Log.d(TAG, "sdpObserver：" + "创建失败：" + s);
                }

                @Override
                public void onSetFailure(String s) {
                    Log.d(TAG, "sdpObserver：" + "设置失败：" + s);
                }
            };
        }

        /**
         * 收到Offer后，设置远程的SDP，同时创建Answer
         *
         * @param sdp SDP
         */
        public void handleOffer(SessionDescription sdp) {
            Log.d(TAG, "handleOffer: 收到Offer，设置远程的SDP，同时创建Answer");
            pc.setRemoteDescription(sdpObserver, sdp);
            pc.createAnswer(sdpObserver, pcConstraints);
        }

        /**
         * 收到Answer后，设置远程的SDP
         *
         * @param sdp SDP
         */
        public void handleAnswer(SessionDescription sdp) {
            pc.setRemoteDescription(sdpObserver, sdp);
        }

        /**
         * 收到Candidate后，添加IceCandidate
         *
         * @param iceCandidate IceCandidate
         */
        public void handleCandidate(IceCandidate iceCandidate) {
            pc.addIceCandidate(iceCandidate);
        }

        public void createOffer() {
            pc.createOffer(sdpObserver, pcConstraints);
        }

        public DataChannel getDataChannel() {
            return dataChannel;
        }

        public void sendStringMessage(String message) {
            byte[] msg = message.getBytes();
            sendBytesMessage(msg);
        }

        public boolean sendBytesMessage(byte[] msg) {
            DataChannel.Buffer buffer = new DataChannel.Buffer(
                    ByteBuffer.wrap(msg),
                    true);
            if (dataChannel.state().equals(DataChannel.State.CLOSED)) {
                createDataChannel();
            }
            return dataChannel.send(buffer);
        }

        void release() {
            pc.dispose();
            if (dataChannel != null) {
                if (!dataChannel.state().equals(DataChannel.State.CLOSED)) {
                    dataChannel.close();
                }
                dataChannel.dispose();
            }
        }
    }

}
