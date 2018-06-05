package com.example.hbz.besideyou.tools;

import com.example.hbz.besideyou.BesideYouApp;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.hbz.besideyou.utils.ToastUtil;
import com.example.webrtc.ConnectionMessageListener;
import com.example.webrtc.PeerManage;
import com.example.webrtc.SendCommandListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static com.example.hbz.besideyou.Constant.WEB_SOCKET_URL;

/**
 * @ClassName: com.example.hbz.besideyou.tools
 * @Description:
 * @Author: HBZ
 * @Date: 2018/5/25 0:11
 */
public class WebRtcTool {

    private volatile static WebRtcTool instance;
    private WebSocketClient mWebSocketClient;

    private String mName = "hbz01"; // 自己的id

    private PeerManage rtcManage;
    private ConnectionMessageListener connectionMessageListener;

    public static WebRtcTool getInstance() {
        if (instance == null) {
            synchronized (WebRtcTool.class) {
                if (instance == null) {
                    instance = new WebRtcTool();
                }
            }
        }
        return instance;
    }

    private WebRtcTool() {
        initSocket();
        initConnectionClient();
        initConnectionMessage();
    }

    public void setConnectionMessageListener(ConnectionMessageListener connectionMessageListener) {
        this.connectionMessageListener = connectionMessageListener;
        initConnectionMessage();
    }

    private void initConnectionMessage() {
        if (connectionMessageListener != null) {
            rtcManage.setConnectionMessage(connectionMessageListener);
        }
    }

    private void initConnectionClient() {
        rtcManage = PeerManage.getInstance(BesideYouApp.getAppContext());

        //设置控制的监听器
        rtcManage.setSendCommand(new SendCommandListener() {
            @Override
            public void sendSdp(String id, SessionDescription sessionDescription) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("type", sessionDescription.type.canonicalForm());
                    payload.put("sdp", sessionDescription.description);

                    JSONObject msg = new JSONObject();
                    msg.put("name", mName);
                    msg.put("toName", id);
                    msg.put("type", sessionDescription.type.canonicalForm());
                    msg.put(sessionDescription.type.canonicalForm(), payload);
                    sendMessage(msg.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendCandidate(String id, IceCandidate iceCandidate) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
                    payload.put("sdpMid", iceCandidate.sdpMid);
                    payload.put("candidate", iceCandidate.sdp);

                    JSONObject msg = new JSONObject();
                    msg.put("type", "candidate");
                    msg.put("name", mName);
                    msg.put("toName", id);
                    msg.put("candidate", payload);
                    sendMessage(msg.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void sendMessage(String msg) {
        LogUtil.d("发送： " + msg);
        if (msg != null && !msg.equals("")) {
            mWebSocketClient.send(msg);
        }
    }

    private void initSocket() {
        URI uri = null;
        try {
            uri = new URI(WEB_SOCKET_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (uri == null) {
            ToastUtil.showShortToast("地址格式错误");
            return;
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                //Handshake 握手
                LogUtil.d("握手 连接到服务器");
            }

            @Override
            public void onMessage(String message) {
                LogUtil.d("收到消息: " + message);
                handleMessages(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                LogUtil.d("关闭连接: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                LogUtil.e("连接异常: " + ex);
            }
        };
        mWebSocketClient.connect();
    }

    private void handleMessages(String message) {
        LogUtil.d("处理收到的指令：");
        JSONObject data;
        try {
            data = new JSONObject(message);
            String type = data.getString("type");
            switch (type) {
                case "login":
                    handleLogin(data);
                    break;
                case "offer":
                    handleOffer(data);
                    break;
                case "candidate":
                    handleCandidate(data);
                    break;
                case "answer":
                    handleAnswer(data);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin(JSONObject data) {
        boolean isLogin;
        ArrayList<String> userNameList = new ArrayList<>();
        try {
            isLogin = data.getBoolean("isSuccess");
            if (!isLogin) {
                //TODO: 登录错误
                LogUtil.d("handleLogin: 登录错误");
                return;
            }
            JSONArray userJson = data.getJSONArray("message");

            for (int i = 0; i < userJson.length(); i++) {
                String name = (String) userJson.get(i);
                userNameList.add(name);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (String user : userNameList) {
            if (user.equals(mName)) {
                continue;
            }

            rtcManage.addClient(user, true);
            LogUtil.d("handleLogin: 添加一个视频回话：" + user);
            //TODO: 添加一个回话视频。
        }


        ToastUtil.showShortToast("登录状态：");
    }

    private void handleOffer(JSONObject data) {
        LogUtil.d("handleOffer");
        try {
            String name = (String) data.get("name");
            JSONObject offer = (JSONObject) data.get("offer");
            if (name != mName) {
                LogUtil.d("handleOffer: 交给rtcManage处理Offer");
                rtcManage.addClient(name, false);
                rtcManage.handleCommand(name, "offer", offer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleCandidate(JSONObject data) {
        try {
            String name = (String) data.get("name");
            JSONObject candidate = (JSONObject) data.get("candidate");
            if (name != mName) {
                rtcManage.handleCommand(name, "candidate", candidate);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleAnswer(JSONObject data) {
        try {
            String name = (String) data.get("name");
            JSONObject answer = (JSONObject) data.get("answer");
            if (name != mName) {
                rtcManage.handleCommand(name, "answer", answer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /**
     * 启动webRtc
     *
     * @param roomName 房间
     * @param identifier 唯一标识
     */
    public void start(String roomName, String identifier) {
        login(identifier,roomName );
    }

    private void login(String name, String room) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "login");
            msg.put("name", name);
            msg.put("room", room);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendMessage(msg.toString());
    }
}
