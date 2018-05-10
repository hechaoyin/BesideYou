package com.example.hbz.besideyou.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by HBZ on 2017/12/31.
 */

public class SDCardPathUtil {

    /**
     * 判断是否已经挂载SDCard
     *
     * @return true：挂载、false；不挂载
     */
    public static boolean isHadSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机自身内存路径
     */
    public static String getPhoneCardPath() {
        return Environment.getDataDirectory().getPath();
    }

    /**
     * 获取sd卡路径
     * 双sd卡时，根据”设置“里面的数据存储位置选择，获得的是内置sd卡或外置sd卡
     *
     * @return 路径
     */
    public static String getNormalSDCardPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //判断外部存储是否转载
            return Environment.getExternalStorageDirectory().getPath();
        }
        return Environment.getDataDirectory().getPath();
    }

    /**
     * 获取外置sd卡路径
     * 双sd卡时，获得的是外置sd卡
     *
     * @return 外置sd卡路径
     */
    @SuppressLint("LongLogTag")
    public static String getSDCardPath() {
        String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
        BufferedInputStream in = null;
        BufferedReader inBr = null;
        try {
            Process p = run.exec(cmd);// 启动另一个进程来执行命令
            in = new BufferedInputStream(p.getInputStream());
            inBr = new BufferedReader(new InputStreamReader(in));
            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                // 获得命令执行后在控制台的输出信息
                Log.i("CommonUtil:getSDCardPath", lineStr);
                if (lineStr.contains("sdcard")
                        && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray != null && strArray.length >= 5) {
                        String result = strArray[1].replace("/.android_secure",
                                "");
                        return result;
                    }
                }
                // 检查命令是否执行失败。
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    // p.exitValue()==0表示正常结束，1：非正常结束
                    Log.e("CommonUtil:getSDCardPath", "命令执行失败!");
                }
            }
        } catch (Exception e) {
            Log.e("CommonUtil:getSDCardPath", e.toString());
            //return Environment.getExternalStorageDirectory().getPath();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inBr != null) {
                    inBr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 查看所有的sd路径
     *
     * @return 每个路径占用一行，以*开始，
     */
    public static String getSDCardPathEx() {
        String mount = new String();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                Log.e("***", line);

                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;
                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat("*" + columns[1] + "\n");
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat(columns[1] + "\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mount;
    }

    /**
     * 获取当前路径，可用空间
     *
     * @param path 文件路径
     * @return 可用空间大小
     */
    public static long getAvailableSize(String path) {
        try {
            File base = new File(path);
            StatFs stat = new StatFs(base.getPath());
            long nAvailableCount = stat.getBlockSize() * ((long) stat.getAvailableBlocks());
            return nAvailableCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
