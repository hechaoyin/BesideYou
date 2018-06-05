package com.example.hbz.besideyou.aatest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.hbz.besideyou.utils.ToastUtil;
import com.example.webrtc.ConnectionMessageListener;
import com.example.webrtc.PeerManage;
import com.example.webrtc.SendCommandListener;
import com.example.webrtc.message.ReceiveChannelFile;
import com.example.webrtc.message.SendChannelFile;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.SessionDescription;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.example.hbz.besideyou.Constant.WEB_SOCKET_URL;
import static com.example.webrtc.message.ChannelFileUtil.FILE_BEGIN;
import static com.example.webrtc.message.ChannelFileUtil.FILE_END;
import static com.example.webrtc.message.ChannelFileUtil.FILE_RECEIVE_SUCCEED;
import static com.example.webrtc.message.ChannelFileUtil.FILE_SEND_COUNT;

public class WebRtcTestActivity extends AppCompatActivity {

    private EditText et_name;
    private EditText et_room;

    private static final String TAG = "WebRtcTestActivity";
    private WebSocketClient mWebSocketClient;

    private String mName = "hbz01";
    private PeerManage rtcManage;
    private Button btn_send;
    private LinearLayout ll_login_group;

//    private ByteBuffer dataBuffer = n

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_rtc_test);
        initView();

        initRxPermissions();

        //初始化信令Socket
        initSocket();

        initConnectionClient();

        initConnectionMessage();

        // 注册耳机广播
        myRegisterReceiver();
    }

    private void initConnectionMessage() {

        rtcManage.setConnectionMessage(new ConnectionMessageListener() {

            ReceiveChannelFile receiveChannelFile = new ReceiveChannelFile();
            // 接收文件块数据统计
            int count = -1; // 第一条（文件开始）要发指令

            @Override
            public void onAddStream(String id, MediaStream mediaStream) {
                // 视频流
            }

            @Override
            public void onReceiveDataChannelMessage(String id, DataChannel.Buffer buffer) {
                // 接收文件
                ByteBuffer data = buffer.data;
                byte[] bytes = new byte[data.capacity()];
                data.get(bytes);
                int type = receiveChannelFile.onMessage(bytes);
                if (type != -1) {
                    //发送收到文件反馈
                    if (type == 2 || type == 1) {
                        if (peer == null) {
                            peer = rtcManage.getRtcClients().values().iterator().next();
                        }
                        count++;
                        if (count % FILE_SEND_COUNT == 0) {
                            peer.sendBytesMessage(new byte[]{FILE_RECEIVE_SUCCEED});
                            if (count % 1000 == 0) {
                                Log.d(TAG, "收到消息量 : " + count);
                            }
                        }
                    } else if (type == 3) {
                        count = -1;
                    }
                    return;
                }

                // 接收文件反馈
                if (bytes.length == 1 && bytes[0] == FILE_RECEIVE_SUCCEED) {
                    sendFileNextData();
                    return;
                }

                // 文本消息
                try {
                    String recString = new String(bytes, "UTF-8");
                    runOnUiThread(() -> {
                        TextView tv = (TextView) findViewById(R.id.iv_file);
                        tv.setText(recString);
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 权限申请
     */
    private void initRxPermissions() {
        // where this is an Activity instance
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(
                Manifest.permission.CAMERA,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET
        )
                .subscribe(permission -> {
                    if (permission.granted) {
                        // 用户已经同意该权限
                        Log.d(TAG, permission.name + " is granted.");
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        Log.d(TAG, permission.name + " is denied. More info should be provided.");
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        Log.d(TAG, permission.name + " is denied.");
                    }
                });
    }

    private void initConnectionClient() {
        rtcManage = PeerManage.getInstance(getApplicationContext());

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

    private void initSocket() {
        URI uri = null;
        try {
            uri = new URI(WEB_SOCKET_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (uri == null) {
            Toast.makeText(this, "地址格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                //Handshake 握手
                Log.d(TAG, "握手 连接到服务器");
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "收到消息: " + message);
                handleMessages(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "关闭连接: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.d(TAG, "连接异常: " + ex);
            }
        };
        mWebSocketClient.connect();
    }

    private void handleMessages(String message) {
        Log.d(TAG, "处理收到的指令：");
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
                Log.d(TAG, "handleLogin: 登录错误");
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
            Log.d(TAG, "handleLogin: 添加一个视频回话：" + user);
            //TODO: 添加一个回话视频。
        }


        Toast.makeText(this, "登录状态：", Toast.LENGTH_LONG).show();
    }

    private void handleOffer(JSONObject data) {
        Log.d(TAG, "handleOffer");
        try {
            String name = (String) data.get("name");
            JSONObject offer = (JSONObject) data.get("offer");
            if (name != mName) {
                Log.d(TAG, "handleOffer: 交给rtcManage处理Offer");
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

    private void sendMessage(String msg) {
        Log.d(TAG, "发送： " + msg);
        if (msg != null && !msg.equals("")) {
            mWebSocketClient.send(msg);
        }
    }

    private void initView() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_room = (EditText) findViewById(R.id.et_room);
        btn_send = (Button) findViewById(R.id.btn_send);
        ll_login_group = (LinearLayout) findViewById(R.id.ll_login_group);

        btn_send.setOnClickListener(v -> submit());
        findViewById(R.id.send_fill).setOnClickListener(v -> sendFile(
                new File(Environment.getExternalStorageDirectory().getPath() +
                        "/BesideYou/photo.jpg"), (byte) 0x01
        ));

        EditText editText = findViewById(R.id.message);
        findViewById(R.id.send_message).setOnClickListener(v -> {
            PeerManage.Peer peer = rtcManage.getRtcClients().values().iterator().next();
            LogUtil.d("通道的状态", peer.getDataChannel().state());
            if (peer.getDataChannel().state() == DataChannel.State.CLOSED) {
                // TODO 通道关闭
            }

            boolean b = peer.sendBytesMessage(editText.getText().toString().getBytes());
            if (b) {
                ToastUtil.showLongToast("发送成功");
            } else {
                ToastUtil.showLongToast("发送失败");
            }
        });


    }

    private void sendFile() {
        //TODO 发送文件
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_test);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        LogUtil.d("发送文件大小" + byteArray.length);
        PeerManage.Peer peer = rtcManage.getRtcClients().values().iterator().next();
        boolean b = peer.sendBytesMessage(byteArray);
        if (b) {
            ToastUtil.showLongToast("发送成功");
        } else {
            ToastUtil.showLongToast("发送失败");
        }
    }

    private void sendFile(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return;
        }
        PeerManage.Peer peer = rtcManage.getRtcClients().values().iterator().next();
        try {
            // 创建文件输入流对象
            FileInputStream is = new FileInputStream(file);
            int N = 1024; // 设定读取的字节数

            byte buffer[] = new byte[N];

            /**发送文件开始标志*/
            peer.sendBytesMessage(new byte[]{FILE_BEGIN});

            // 读取输入流
            while (is.read(buffer, 0, N) != -1) {
                if (!peer.sendBytesMessage(buffer)) {// 发送文件
                    LogUtil.e("发送失败" + peer.getDataChannel().state());
                    break;
                }

            }

            /**发送文件结束标志*/
            peer.sendBytesMessage(new byte[]{FILE_END});

            ToastUtil.showLongToast("发送文件完成。");
            // 关闭输入流
            is.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////使用自定义类发送数据begin/////////////////
    private SendChannelFile sendChannelFile;
    PeerManage.Peer peer;
    long beginTime;

    private void sendFile(File file, byte xxxxx) {
        String compPath = Environment.getExternalStorageDirectory().getPath() +
                "/BesideYou/LuBan";
        // 压缩文件
        Luban.with(this)
                .load(file)                                     // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setTargetDir(compPath)                        // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        LogUtil.i("压缩开始前调用");
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        LogUtil.i("压缩成功后调用");

                        sendChannelFile = new SendChannelFile(file);
                        peer = rtcManage.getRtcClients().values().iterator().next();
                        peer.sendBytesMessage(sendChannelFile.getBeginFlag());// 发送开始标志
                        beginTime = System.currentTimeMillis();
                        Log.d(TAG, "发送数据开始: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        LogUtil.e("当压缩过程出现问题时调用");
                    }
                }).launch();    //启动压缩
    }

    private void sendFileNextData() {
        if (peer == null || sendChannelFile == null) {
            return;
        }

        int flag;
        for (int i = 0; i < FILE_SEND_COUNT; i++) {
            flag = sendChannelFile.readNextData();
            if (flag == Integer.MAX_VALUE) {
                Log.d(TAG, "读取文件失败: ");
                return;
            }
            if (flag != -1 && flag > 0) {
                peer.sendBytesMessage(sendChannelFile.getBuffer());// 发送文件
            } else {
                peer.sendBytesMessage(sendChannelFile.getEndFlag());// 发送结束标志
                Log.d(TAG, "发送数据结束: " + flag);
                Log.d(TAG, "发送数据耗时: " + (System.currentTimeMillis() - beginTime));
                break;
            }
        }
    }


    ////////////////使用自定义类发送数据end/////////////////


    private void submit() {
        // validate
        String name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "name不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String room = et_room.getText().toString().trim();
        if (TextUtils.isEmpty(room)) {
            Toast.makeText(this, "room不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mName = name;
        login(name, room);
    }


    /////////////////耳机广播begin//////////////
    private MyVolumeReceiver mVolumeReceiver = null;

    /**
     * 注册广播
     */
    private void myRegisterReceiver() {
        mVolumeReceiver = new MyVolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mVolumeReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mVolumeReceiver);
    }

    /**
     * 监听耳机插拔
     */
    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AudioManager audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
            //检测是否插入耳机，是的话关闭扬声器，否则反之
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

                LogUtil.d("耳机转态改变");

                if (intent.getIntExtra("state", 0) == 0) {
                    audioManager.setSpeakerphoneOn(true);
                } else if (intent.getIntExtra("state", 0) == 1) {
                    audioManager.setSpeakerphoneOn(false);
                }
            }
        }
    }

    /////////////////耳机广播end//////////////
}
