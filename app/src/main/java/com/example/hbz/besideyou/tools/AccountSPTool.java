package com.example.hbz.besideyou.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.hbz.besideyou.utils.AESUtil;

import static com.example.hbz.besideyou.Constant.PASSWORD_SECRET_KEY;

/**
 * @ClassName: com.example.hbz.besideyou.tools
 * @Description:
 * @Author: HBZ
 * @Date: 2018/4/27 17:17
 */

public class AccountSPTool {

    private static final String ACCOUNT_SP_FILE_NAME = "accountInfo";            //SP 文件名称
    private static final String ACCOUNT_IDENTIFIER = "identifier";   //字段：
    private static final String ACCOUNT_PASSWORD = "password";  //字段：

    private static SharedPreferences accountSP; // 账户SP
    private static SharedPreferences.Editor accountEditor; // 账户SP编辑器

    private static AccountSPTool instance = new AccountSPTool(); //单例实例

    public static AccountSPTool getInstance(Context context) {
        if (context == null) {
            return null;
        }
        context = context.getApplicationContext();
        if (accountSP == null) {
            accountSP = context.getSharedPreferences(ACCOUNT_SP_FILE_NAME, Context.MODE_PRIVATE);
        }
        if (accountEditor == null) {
            accountEditor = accountSP.edit();
        }
        return instance;
    }

    public void saveUserAndPaw(String identifier, String password) {
        saveIdentifier(identifier);
        savePassword(password);
        accountEditor.commit();
    }


    private void saveIdentifier(String identifier) {
        if (identifier == null) {
            return;
        }
        accountEditor.putString(ACCOUNT_IDENTIFIER, identifier);
    }

    public String getIdentifier() {
        return accountSP.getString(ACCOUNT_IDENTIFIER, "");
    }

    private void savePassword(String password) {
        if (password == null) {
            return;
        }
        String encrypt = null;//密码加密
        try {
            encrypt = AESUtil.encrypt(password, PASSWORD_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (encrypt == null) {
            return;
        }
        accountEditor.putString(ACCOUNT_PASSWORD, encrypt);
    }

    public String getPassword() {

        String password = accountSP.getString(ACCOUNT_PASSWORD, "");
        if (TextUtils.isEmpty(password)) {
            return "";
        }
        String decrypt = null;
        try {
            decrypt = AESUtil.decrypt(password, PASSWORD_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (decrypt == null) {
            return "";
        }
        return decrypt;
    }


    public void resetAccountSP() {
        accountEditor.clear();
        accountEditor.commit();
    }
}
