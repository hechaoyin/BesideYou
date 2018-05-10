package com.example.hbz.besideyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.hbz.besideyou.utils.ToastUtil;
import com.tencent.qalsdk.QALSDKManager;
import com.tencent.qcloud.sdk.Constant;

import java.util.regex.Pattern;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSHelper;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSUserInfo;

import static com.example.hbz.besideyou.Constant.CHECK_PHONE_RULE;

public class RegisterPhoneActivity extends BastActivity implements TextWatcher {

    private EditText et_user;
    private ImageView iv_clear_user;

    private EditText et_verification_code;
    private Button btn_vertify_code;

    private EditText et_password;
    private ImageView iv_clear_password;
    private EditText et_password_02;
    private ImageView iv_clear_password_02;
    private Button btn_login;

    private TLSHelper tlsHelper;
    private TLSPwdRegListener pwdResetListener;
    private boolean firstRequest = true;// 是否第一次请求检验码
    private boolean requestSucceed = false;//请求检验码是否成功
    private int codeTime = 0; // 可以重新获取检验码的时间限制（秒）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        initView();
        initQAL();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int phone = et_user.length();
        int pw = et_password.length();
        int pw2 = et_password_02.length();

        btn_login.setEnabled(!(phone == 0 || pw == 0 || pw2 == 0));

        iv_clear_user.setVisibility(phone == 0 ? View.GONE : View.VISIBLE);
        iv_clear_password.setVisibility(pw == 0 ? View.GONE : View.VISIBLE);
        iv_clear_password_02.setVisibility(pw2 == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void initView() {
        et_user = (EditText) findViewById(R.id.et_user);
        et_user.addTextChangedListener(this);
        et_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_user.getText().length() == 11) {
                    if (codeTime <= 0) {
                        btn_vertify_code.setEnabled(true);
                    }
                    firstRequest = true;
                } else {
                    btn_vertify_code.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        iv_clear_user = (ImageView) findViewById(R.id.iv_clear_user);
        iv_clear_user.setOnClickListener(v -> et_user.setText(""));

        //检验码
        et_verification_code = (EditText) findViewById(R.id.et_verification_code);
        et_verification_code.addTextChangedListener(this);
        btn_vertify_code = (Button) findViewById(R.id.btn_vertify_code);
        btn_vertify_code.setOnClickListener(v -> requestVertifyCode());

        et_password = (EditText) findViewById(R.id.et_password);
        et_password.addTextChangedListener(this);
        iv_clear_password = (ImageView) findViewById(R.id.iv_clear_password);
        iv_clear_password.setOnClickListener(v -> et_password.setText(""));

        et_password_02 = (EditText) findViewById(R.id.et_password_02);
        et_password_02.addTextChangedListener(this);
        iv_clear_password_02 = (ImageView) findViewById(R.id.iv_clear_password_02);
        iv_clear_password_02.setOnClickListener(v -> et_password_02.setText(""));

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v -> submit());
        btn_login.setEnabled(false);

        findViewById(R.id.tv_register_type).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterStrActivity.class));
            finish();
        });
    }

    private void submit() {
        String user = et_user.getText().toString().trim();
        if (TextUtils.isEmpty(user)) {
            Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String code = et_verification_code.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "检验码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String password2 = et_password_02.getText().toString().trim();
        if (TextUtils.isEmpty(password2)) {
            Toast.makeText(this, "重复密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(password2)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8 || password.length() > 16) {
            Toast.makeText(this, "密码需要8-16位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (user.length() != 11 || !Pattern.matches(CHECK_PHONE_RULE, user)) {
            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO 请求检验码、更改账号密码
        register(code);
    }

    private void initQAL() {
        // 务必检查IMSDK已做以下初始化
        QALSDKManager.getInstance().setEnv(0);
        QALSDKManager.getInstance().init(getApplicationContext(), Constant.SDK_APPID);

        // 初始化TLSSDK
        tlsHelper = TLSHelper.getInstance().init(getApplicationContext(), Constant.SDK_APPID);
        pwdResetListener = new TLSPwdRegListener() {
            @Override
            public void OnPwdRegAskCodeSuccess(int reaskDuration, int expireDuration) {
            /* 请求下发短信成功    reaskDuration: 可以重新发送限时（秒）； expireDuration：检验码有效时间*/
                LogUtil.d("请求下发短信成功" + reaskDuration + " , " + expireDuration);
                runOnUiThread(() -> refreshCodeTime(reaskDuration));
                requestSucceed = true;
            }

            @Override
            public void OnPwdRegReaskCodeSuccess(int reaskDuration, int expireDuration) {
            /* 重新请求下发短信成功    reaskDuration: 可以重新发送限时（秒）； expireDuration：检验码有效时间*/
                LogUtil.d("重新请求下发短信成功" + reaskDuration + " , " + expireDuration);
                runOnUiThread(() -> refreshCodeTime(reaskDuration));
                requestSucceed = true;
            }

            @Override
            public void OnPwdRegVerifyCodeSuccess() {
            /* 短信验证成功*/
                LogUtil.d("短信验证成功");
                tlsHelper.TLSPwdRegCommit(et_password.getText().toString(), pwdResetListener);// 重置密码

            }

            @Override
            public void OnPwdRegCommitSuccess(TLSUserInfo userInfo) {
            /* 重置密码成功，接下来可以引导用户进行新密码登录了，登录流程请查看相应章节*/
                LogUtil.d("注册成功");
                ToastUtil.showLongToast("注册成功");
                finish();
            }

            @Override
            public void OnPwdRegFail(TLSErrInfo tlsErrInfo) {
             /* 重置密码过程中任意一步都可以到达这里，可以根据tlsErrInfo 中ErrCode, Title, Msg 给用户弹提示语，引导相关操作*/
                LogUtil.d("注册错误: " + tlsErrInfo.Msg);
                ToastUtil.showLongToast("注册错误: " + tlsErrInfo.Msg);
            }

            @Override
            public void OnPwdRegTimeout(TLSErrInfo tlsErrInfo) {
            /* 网络超时，一般让用户重试即可*/
                LogUtil.d("注册错误: " + tlsErrInfo.Msg);
                ToastUtil.showLongToast("注册错误: " + tlsErrInfo.Msg);
            }
        };

    }

    /**
     * 刷新检验码按钮状态
     */
    private void refreshCodeTime(int time) {
        codeTime = time;
        if (codeTime > 0) {
            btn_vertify_code.setText(("" + codeTime + "秒"));
            btn_vertify_code.setEnabled(false);
            btn_vertify_code.postDelayed(() -> refreshCodeTime(time - 1), 1000);
        } else {
            btn_vertify_code.setEnabled(true);
            btn_vertify_code.setText(getString(R.string.get_verify_code));
        }
    }


    /**
     * 请求检验码
     */
    private void requestVertifyCode() {
        if (tlsHelper == null) {
            return;
        }
        if (firstRequest) {
            tlsHelper.TLSPwdRegAskCode("86-" + et_user.getText(), pwdResetListener);
        } else {
            tlsHelper.TLSPwdRegReaskCode(pwdResetListener);
        }
    }

    /**
     * 改变密码
     *
     * @param code
     */
    private void register(String code) {
        if (requestSucceed) {
            tlsHelper.TLSPwdRegVerifyCode(code, pwdResetListener);
        } else {
            ToastUtil.showLongToast("请先获取检验码");
        }
    }
}
