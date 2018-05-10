package com.example.webrtc.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.example.webrtc.message.ChannelFileUtil.FILE_BEGIN;
import static com.example.webrtc.message.ChannelFileUtil.FILE_END;

/**
 * @ClassName: com.example.webrtc.message
 * @Description:
 * @Author: HBZ
 * @Date: 2018/4/11 10:17
 */

public class SendChannelFile {

    private FileInputStream inputStream;
    private File file;

    private static int N = 1024; // 设定读取的字节数
    private byte buffer[] = new byte[N];
    private int bufferCount = 0;

    public SendChannelFile(File file) {
        this.file = file;
        if (file == null || file.isDirectory()) {
            return;
        }
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取下一条数据
     * @return 返回读入缓冲区的字节总数; -1读取文件结束； Integer.MIN_VALUE 读取出错
     */
    public int readNextData() {
        try {
            bufferCount = inputStream.read(buffer, 0, N);
        } catch (IOException e) {
            e.printStackTrace();
            bufferCount = Integer.MIN_VALUE;
        }
        return bufferCount;
    }

    /**
     * 获取缓冲区数据
     */
    public byte[] getBuffer() {
        if (bufferCount == N) {
            return buffer;
        } else if (bufferCount <= 0 || bufferCount > N) {
            //出错
            return new byte[]{FILE_END};
        } else {
            byte[] tem = new byte[bufferCount];
            System.arraycopy(buffer, 0, tem, 0, bufferCount);
            return tem;
        }
    }


    /**
     * 获取发送文件开始标志
     */
    public byte[] getBeginFlag() {

        if (file == null || file.isDirectory()) {
            return new byte[]{FILE_BEGIN};
        }
        String name = file.getName();
        byte[] nameBytes = name.getBytes();

        byte[] beginFlag = new byte[nameBytes.length + 1];
        beginFlag[0] = FILE_BEGIN;
        System.arraycopy(nameBytes, 0, beginFlag, 1, nameBytes.length);
        return beginFlag;
    }

    /**
     * 获取发送文件结束标志
     */
    public byte[] getEndFlag() {
        return new byte[]{FILE_END};
    }
}
