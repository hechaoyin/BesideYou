package com.example.webrtc.message;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @ClassName: com.example.webrtc
 * @Description:
 * @Author: HBZ
 * @Date: 2018/4/10 15:04
 */

public class ReceiveChannelFile {

    private FileOutputStream fileOutputStream;
    private File file;
    private boolean isFile = false;
    private long beginTime;

    /**
     * 处理文件消息
     *
     * @param byteMsg 处理完毕
     * @return false未处理, true处理完毕
     */
    public int onMessage(byte[] byteMsg) {
        if (isFile) {
            if (ChannelFileUtil.isFileEnd(byteMsg)) {// 文件结束
                release();
                isFile = false;
                return 3;// 文件结束消息
            }
            write(byteMsg);//写入数据
            return 2;// 文件写入
        } else if (ChannelFileUtil.isFileBegin(byteMsg)) {// 文件开始
            init(byteMsg);
            isFile = true;
            return 1;// 文件开始消息
        }
        return -1;//不是文件消息
    }

    /**
     * 初始化写入文件的输出流
     */
    private void init(byte[] byteMsg) {
//        if (byteMsg.length <= 1) {
//            return;
//        }
        beginTime = System.currentTimeMillis();

        String fileName = new String(byteMsg, 1, byteMsg.length - 1);
        if (fileName.equals("")) {
            fileName = "" + System.currentTimeMillis();
        }
        File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/BesideYou/");
        if (!directory.exists()) {
            boolean mkdirs = directory.mkdirs();
            if (!mkdirs) {
                Log.e("---", "创建文件夹失败");
                return;
            }
        }

        try {
            file = new File(directory.getAbsolutePath() + "/" + fileName);
            if (file.exists()) {
                file = new File(directory.getAbsolutePath() + "/" + System.currentTimeMillis() + fileName);
            }
            fileOutputStream = new FileOutputStream(file);
            Log.d("---", "创建文件输出流" + file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入文件数据
     */
    private void write(byte[] byteMsg) {
        if (fileOutputStream == null || byteMsg.length <= 0) {
            return;
        }
        try {
            fileOutputStream.write(byteMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    private void release() {
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileOutputStream = null;
            Log.d("---", "文件创建完成");
        }
        Log.d("---", "传输文件耗时 : " + (System.currentTimeMillis() - beginTime));
        Log.d("---", "传输文件大小 : " + file.length());
    }

}
