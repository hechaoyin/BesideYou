package com.example.hbz.besideyou;

/**
 * @ClassName: com.example.besideyou
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/13 0:24
 */

public class Constant {

    public static final boolean PRINT_LOG = true;// 是否打印日志

    public static final String WEB_SOCKET_URL = "http://192.168.1.52:1655";// WebSocket服务器地址

    public static final String CHECK_PHONE_RULE = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";

    public static final String PASSWORD_SECRET_KEY = "BesideYou1234567";//此处使用AES-128-CBC加密模式，key需要为16位

}
