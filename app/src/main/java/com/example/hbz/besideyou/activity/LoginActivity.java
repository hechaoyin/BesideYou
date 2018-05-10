package com.example.hbz.besideyou.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.tools.AccountSPTool;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.hbz.besideyou.utils.ToastUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.tencent.qalsdk.QALSDKManager;
import com.tencent.qcloud.sdk.Constant;

import java.util.regex.Pattern;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSLoginHelper;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

import static com.example.hbz.besideyou.Constant.CHECK_PHONE_RULE;


/**
 * @author HBZ
 * @date 2018/3/7
 */
public class LoginActivity extends BastActivity implements View.OnClickListener, TextWatcher {

    private static final int REGISTER_REQUEST_CODE = 100;//注册请求代码
    public static final String REGISTER_RETURN_NAME = "user_id";//注册请求代码

    private View root_view;

    //账号密码
    private EditText et_login_user;
    private EditText et_login_password;
    private Button btn_login;

    //注册、忘记密码按钮
    private TextView tv_new_user_register;
    private TextView tv_forget_password;

    //清除按钮
    private ImageView iv_clear_user;
    private ImageView iv_clear_password;

    //检验码
    private LinearLayout ll_login_vertical_identify;
    private EditText et_login_verification_code;
    private ImageView iv_login_img_code;
    private boolean isNeedInputImgCode = false;

    // 账户、密码SP
    private AccountSPTool accountSPTool;

    //登录监听器
    private TLSPwdLoginListener pwdLoginListener;
    //登录实例
    private TLSLoginHelper tlsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountSPTool = AccountSPTool.getInstance(this);
        initView();
        initLoginQAL();
    }

    @Override
    protected void onStart() {
        super.onStart();
        textChanged();
    }

    private void initView() {
        root_view = findViewById(R.id.root_view);
        et_login_user = (EditText) findViewById(R.id.et_login_user);
        iv_clear_user = (ImageView) findViewById(R.id.iv_clear_user);
        et_login_password = (EditText) findViewById(R.id.et_login_password);
        iv_clear_password = (ImageView) findViewById(R.id.iv_clear_password);
        et_login_verification_code = (EditText) findViewById(R.id.et_login_verification_code);
        iv_login_img_code = (ImageView) findViewById(R.id.iv_login_img_code);
        ll_login_vertical_identify = (LinearLayout) findViewById(R.id.ll_login_vertical_identify);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_new_user_register = (TextView) findViewById(R.id.tv_new_user_register);
        tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);

        btn_login.setEnabled(false);

        et_login_user.addTextChangedListener(this);
        et_login_password.addTextChangedListener(this);

        tv_new_user_register.setOnClickListener(this);
        tv_forget_password.setOnClickListener(this);
        iv_clear_user.setOnClickListener(this);
        iv_clear_password.setOnClickListener(this);

        iv_login_img_code.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        // 初始化账号密码SP
        et_login_user.setText(accountSPTool.getIdentifier());
        et_login_password.setText(accountSPTool.getPassword());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REGISTER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String returnedData = data.getStringExtra(REGISTER_RETURN_NAME);

                    if (!TextUtils.isEmpty(returnedData)) {
                        et_login_user.setText(returnedData);
                        et_login_password.setText("");
                        et_login_password.requestFocus();
                    }
                }
                break;
            default:
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textChanged();
    }

    private void textChanged() {
        int phone = et_login_user.length();
        int pw = et_login_password.length();

        btn_login.setEnabled(!(phone == 0 || pw == 0));

        iv_clear_user.setVisibility(phone == 0 ? View.GONE : View.VISIBLE);
        iv_clear_password.setVisibility(pw == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                //登录
                submit();
                break;
            case R.id.iv_clear_password:
                //清空密码输入框
                et_login_password.setText("");
                break;
            case R.id.iv_clear_user:
                //清空用户名输入框
                et_login_user.setText("");
                break;
            case R.id.tv_new_user_register:
                // 注册
                startActivityForResult(new Intent(this, RegisterPhoneActivity.class), REGISTER_REQUEST_CODE);
                break;
            case R.id.tv_forget_password:
                // 忘记密码
                startActivityForResult(new Intent(this, ForgetPasswordActivity.class), REGISTER_REQUEST_CODE);
                break;
            case R.id.iv_login_img_code:
                //刷新图片检验码
                refreshImgCode();
                break;
            default:
                break;
        }
    }


    private void upDateImgCode(byte[] picData) {
        if (ll_login_vertical_identify.getVisibility() != View.VISIBLE) {
            ll_login_vertical_identify.setVisibility(View.VISIBLE);
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(picData, 0,
                picData.length);
        iv_login_img_code.setImageBitmap(bitmap);
    }

    private void submit() {
        String user = et_login_user.getText().toString().trim();
        if (TextUtils.isEmpty(user)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = et_login_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isNeedInputImgCode) {
            //需要输入检验码
            String code = et_login_verification_code.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, "检验码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (Pattern.matches(CHECK_PHONE_RULE, user)) {
            user = "86-" + user;
            LogUtil.d("手机登录" + user);
        }

        /**登录*/
        loginQAL(user, password);
    }

    /**
     * 初始化登录实例，监听器
     */
    private void initLoginQAL() {
        //托管模式登录
        // 务必检查IMSDK已做以下初始化
        QALSDKManager.getInstance().setEnv(0);
        QALSDKManager.getInstance().init(getApplicationContext(), Constant.SDK_APPID);
        // 初始化TLSSDK
        tlsHelper = TLSLoginHelper.getInstance().init(getApplicationContext(),
                Constant.SDK_APPID, Constant.ACCOUNT_TYPE, Constant.APP_VERSION);

        pwdLoginListener = new TLSPwdLoginListener() {
            @Override
            public void OnPwdLoginSuccess(TLSUserInfo userInfo) {
                    /* 登录成功了，在这里可以获取用户票据*/
                String usersig = tlsHelper.getUserSig(userInfo.identifier);
                login(userInfo.identifier, usersig);

            }

            @Override
            public void OnPwdLoginReaskImgcodeSuccess(byte[] picData) {
                    /* 请求刷新图片验证码成功，此时需要用picData 更新验证码图片，原先的验证码已经失效*/
                LogUtil.d("刷新检验码");
                upDateImgCode(picData);
            }

            @Override
            public void OnPwdLoginNeedImgcode(byte[] picData, TLSErrInfo errInfo) {
                    /* 用户需要进行图片验证码的验证，需要把验证码图片展示给用户，
                    并引导用户输入；如果验证码输入错误，仍然会到达此回调并更新图片验证码*/
                LogUtil.d("需要检验码");
                ToastUtil.showShortToast("验证码错误,请重新输入");
                upDateImgCode(picData);
            }

            @Override
            public void OnPwdLoginFail(TLSErrInfo errInfo) {
                    /* 登录失败，比如说密码错误，用户帐号不存在等，
                    通过errInfo.ErrCode, errInfo.Title, errInfo.Msg等可以得到更具体的错误信息*/
                LogUtil.e("登录错误:" + errInfo.ErrCode + "\n" + errInfo.Title + "\n" + errInfo.Msg);
                ToastUtil.showLongToast("登录错误:" + errInfo.Msg);
            }

            @Override
            public void OnPwdLoginTimeout(TLSErrInfo errInfo) {
                    /* 密码登录过程中任意一步都可以到达这里，顾名思义，网络超时，可能是用户网络环境不稳定，
                    一般让用户重试即可*/
                LogUtil.d("网络超时");
            }
        };
    }


    /**
     * 主动刷新检验码
     */
    private void refreshImgCode() {
        tlsHelper.TLSPwdLoginReaskImgcode(pwdLoginListener);
    }

    /**
     * 登录QAL，得到登录凭证后作最后登录
     *
     * @param name     用户名
     * @param password 密码
     */
    private void loginQAL(String name, String password) {
        // 这里的"用户帐号", 如果是手机号码，格式为“86-186xxx”。
        tlsHelper.TLSPwdLogin(name, password.getBytes(), pwdLoginListener);
    }

    /**
     * 最终登录
     *
     * @param identifier 用户名
     * @param usersig    用户登录凭证
     */
    private void login(String identifier, String usersig) {
        TIMUser user = new TIMUser();
        user.setIdentifier(identifier);

        //发起登录请求
        TIMManager.getInstance().login(
                Constant.SDK_APPID,                   //sdkAppId，由腾讯分配
                user,
                usersig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
                new TIMCallBack() {//回调接口

                    @Override
                    public void onSuccess() {//登录成功
                        ToastUtil.showShortToast("登录成功");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        accountSPTool.saveUserAndPaw(et_login_user.getText().toString(), et_login_password.getText().toString());
                        finish();
                    }

                    @Override
                    public void onError(int code, String desc) {//登录失败
                        //错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code含义请参见错误码表
                        LogUtil.d("login failed. code: " + code + " errmsg: " + desc);
                    }
                });
    }
}

