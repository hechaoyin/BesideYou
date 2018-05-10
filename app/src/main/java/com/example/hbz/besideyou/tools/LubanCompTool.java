package com.example.hbz.besideyou.tools;

import android.content.Context;

import com.example.hbz.besideyou.utils.FileUtil;
import com.example.hbz.besideyou.utils.LogUtil;

import java.io.File;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * @ClassName: com.example.hbz.besideyou.tools
 * @Description: Luban图片压缩框架进行封装
 * @Author: HBZ
 * @Date: 2018/4/23 9:23
 */

public class LubanCompTool {

    /**
     * 压缩图片到临时文件夹（外部私有cache目录下ImageCache目录）
     * @param context 上下文
     * @param path 源文件目录路径
     * @param compressListener 压缩监听器
     */
    public static void compressToTem(Context context, String path, CompressListener compressListener) {
        Luban.with(context)
                .load(path)                                     // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小（100k）
                .setTargetDir(FileUtil.getImageCacheDir(context))    // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        LogUtil.i("开始压缩");
                        compressListener.onStart();
                    }

                    @Override
                    public void onSuccess(File file) {
                        LogUtil.i("压缩成功后调用");
                        compressListener.onSuccess(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e("当压缩过程出现问题时调用");
                        compressListener.onError(e);
                    }
                }).launch();    //启动压缩

    }

    /**
     * 压缩监听器。
     */
    public interface CompressListener {

        /**
         * 当压缩启动时触发
         */
        void onStart();

        /**
         * 当压缩返回成功时触发
         */
        void onSuccess(File file);

        /**
         * 当压缩失败时触发
         */
        void onError(Throwable e);
    }
}
