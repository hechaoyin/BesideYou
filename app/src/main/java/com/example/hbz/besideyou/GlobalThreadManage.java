package com.example.hbz.besideyou;


import com.example.hbz.besideyou.utils.LogUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import static com.example.hbz.besideyou.Constant.WEB_SOCKET_URL;


/**
 * @author HBZ
 * @date 2018/3/7
 * 全局线程
 */

public class GlobalThreadManage {

    static class GlobalThread extends Thread {
        private Object control = "";//只是任意的实例化一个对象而已
        private boolean suspend = false;//线程暂停标识

        private static WebSocketClient mWebSocketClient;

        static {
            // 初始化WebSocketClient
            initWebSocket();
        }

        private static void initWebSocket() {
            URI uri = null;
            try {
                uri = new URI(WEB_SOCKET_URL);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (uri == null) {
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
                    // TODO: 处理收到的消息
//                    handleMessages(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    LogUtil.d("关闭连接: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    LogUtil.d("连接异常: " + ex.toString());
                }
            };
            mWebSocketClient.connect();
        }

        private static boolean sendWebSocketMessage(String message) {
            if (message == null) {
                return false;
            }
            if (mWebSocketClient == null) {
                initWebSocket();
            }
            try {
                mWebSocketClient.send(message);
            } catch (Exception ex) {
                LogUtil.d("发送消息异常: " + ex.toString());
                return false;
            }
            return true;
        }

        @Override
        public void run() {
            while (true) {
                if (false) {//当前任务完成
                    LogUtil.i("埋点线程暂停");
                    suspend = true;
                }
                synchronized (control) {
                    if (suspend) {
                        try {
                            control.wait();
                            LogUtil.i("埋点线程继续");
                            continue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //TODO: 其他任务操作
            }
        }

        /**
         * 使线程暂停状态
         *
         * @param suspend true是暂停 false是继续
         */
        public void setSuspend(boolean suspend) {
            if (!suspend) {
                synchronized (control) {
                    control.notifyAll();
                }
            }
            this.suspend = suspend;
        }

        /**
         * @return 线程状态（是否是暂停状态）
         */
        public boolean isSuspend() {
            return suspend;
        }
    }
}
