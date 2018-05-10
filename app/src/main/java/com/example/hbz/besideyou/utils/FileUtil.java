package com.example.hbz.besideyou.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @ClassName: com.example.besideyou.utils
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/13 18:44
 */

public class FileUtil {
    private static final String IMAGE_CACHE_DIR_NAME = "ImageCache";

    /**
     * 判断是否已经挂载SDCard
     *
     * @return true：挂载、false；不挂载
     */
    public static boolean isHadSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSDCardDPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String getAppRootPath() {
        if (!isHadSDCard()) {
            return "";
        }
        return getSDCardDPath() + "/BesideYou/";
    }

    public static File getAppRootFile() {
        if (!isHadSDCard()) {
            return null;
        }
        File file = new File(getAppRootPath());
        if (!file.exists()) {
            if (!file.mkdirs()) {// 创建文件夹
                LogUtil.e("获取App根目录失败: ");
                return null;
            }
        }
        return file;
    }

    public static File getAppCompressFile() {
        File file = new File(getAppRootPath() + "Compress/");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LogUtil.e("获取压缩保存文件目录失败: ");
                return null;
            }
        }
        return file;
    }

    public static File getAppSaveFile() {
        File file = new File(getAppRootPath() + "Save/");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LogUtil.e("获取保存文件目录失败: ");
                return null;
            }
        }
        return file;
    }

    public static String getImageCacheDir(Context context) {
        File cacheDir = context.getExternalCacheDir();//外部缓存目录
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();//内部缓存目录
        }
        return cacheDir.getAbsolutePath() + "/" + IMAGE_CACHE_DIR_NAME + "/";
    }
}
