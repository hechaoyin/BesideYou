package com.example.hbz.besideyou.tools;

import com.github.promeg.pinyinhelper.Pinyin;

/**
 * @ClassName: com.example.hbz.besideyou.tools
 * @Description:
 * @Author: HBZ
 * @Date: 2018/5/7 10:12
 */

public class PinyinUtil {
    /**
     * 如果c为汉字，则返回大写拼音；如果c不是汉字，则返回String.valueOf(c) ,再转大写
     */
    public static String toPinyin(char c) {
        String pinyin;
        if (isChinese(c)) {
            //如果c为汉字，则返回大写拼音；如果c不是汉字，则返回String.valueOf(c)
            pinyin = Pinyin.toPinyin(c);
        } else {
            pinyin = String.valueOf(c);
            pinyin = pinyin.toUpperCase();
        }
        return pinyin;
    }

    /**
     * 将输入字符串转为拼音，转换过程中会使用之前设置的用户词典，以字符为单位插入分隔符
     */
    public static String toPinyin(String str, String separator) {
        String pinyin = Pinyin.toPinyin(str, separator);
        pinyin = pinyin.toUpperCase();
        return pinyin;
    }

    /**
     * c为汉字，则返回true，否则返回false
     */
    private static boolean isChinese(char c) {
        return Pinyin.isChinese(c);
    }

}
