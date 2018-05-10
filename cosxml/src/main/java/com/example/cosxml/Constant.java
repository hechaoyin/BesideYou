package com.example.cosxml;

/**
 * @ClassName: com.example.cosxml
 * @Description:
 * @Author: HBZ
 * @Date: 2018/4/28 15:50
 */

public class Constant {
    static final String appid = "1252028025"; //"对象存储的服务 APPID"
    static final String secretId = "AKIDJiChIrfdXO5hLSzgr4DByhPAnS7nR3d4";//"云 API 密钥 SecretId"
    static final String secretKey = "TnHcr8b5aJvOF3gIrGh3FQgt6770vtz4";//"云 API 密钥 SecretKey"
    static final String region = "ap-guangzhou";  //	广州(华南) （gz）"存储桶所在的地域"

    static final String bucket = "hebizhi-1252028025"; //存储桶名称 cos v5 的 bucket格式为：xxx-appid, 如 test-1253960454

    static final long keyDuration = 600; //SecretKey 的有效时间，单位秒


    String cosPath = "远端路径，即存储到 COS 上的绝对路径"; //格式如 cosPath = "/test.txt";
    String srcPath = "本地文件的绝对路径"; // 如 srcPath = Environment.getExternalStorageDirectory().getPath() + "/test.txt";
    long signDuration = 600; //签名的有效期，单位为秒

    // 其他辅助
    public static final String FACE_ROOT_PATH = "http://hebizhi-1252028025.cosgz.myqcloud.com/face/"; // 头像文件夹


    //访问域名(XML API)：hebizhi-1252028025.cos.ap-guangzhou.myqcloud.com
    //访问域名(JSON API)：hebizhi-1252028025.cosgz.myqcloud.com
    //加速域名：hebizhi-1252028025.file.myqcloud.com
    //hebizhi


}
