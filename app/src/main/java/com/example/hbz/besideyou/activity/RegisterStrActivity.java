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

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSHelper;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

import static com.example.hbz.besideyou.activity.LoginActivity.REGISTER_RETURN_NAME;

public class RegisterStrActivity extends BastActivity implements TextWatcher {

    private EditText et_user;
    private ImageView iv_clear_user;
    private EditText et_password;
    private ImageView iv_clear_password;
    private EditText et_password_02;
    private ImageView iv_clear_password_02;
    private Button btn_login;

    private TLSHelper tlsHelper;
    private TLSStrAccRegListener strAccRegListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_str);
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
        iv_clear_user = (ImageView) findViewById(R.id.iv_clear_user);
        iv_clear_user.setOnClickListener(v -> et_user.setText(""));

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

        findViewById(R.id.tv_register_type).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterPhoneActivity.class));
            finish();
        });
    }

    private void submit() {
        String user = et_user.getText().toString().trim();
        if (TextUtils.isEmpty(user)) {
            Toast.makeText(this, "user不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "password不能为空", Toast.LENGTH_SHORT).show();
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
        if (user.length() > 24) {
            Toast.makeText(this, "用户名必须小于24位", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO 注册账号
        register(user, password);

    }

    private void initQAL() {
        // 务必检查IMSDK已做以下初始化
        QALSDKManager.getInstance().setEnv(0);
        QALSDKManager.getInstance().init(getApplicationContext(), Constant.SDK_APPID);

        // 初始化TLSSDK
        tlsHelper = TLSHelper.getInstance().init(getApplicationContext(), Constant.SDK_APPID);

        // 字符串注册监听器
        strAccRegListener = new TLSStrAccRegListener() {
            @Override
            public void OnStrAccRegSuccess(TLSUserInfo tlsUserInfo) {
                /* 成功注册了一个字符串帐号， 可以引导用户使用刚注册的用户名和密码登录 */
                LogUtil.d("注册成功");
                ToastUtil.showLongToast("注册成功");

                Intent intent = new Intent();
                intent.putExtra(REGISTER_RETURN_NAME, et_user.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void OnStrAccRegFail(TLSErrInfo tlsErrInfo) {
            /* 注册失败，请提示用户失败原因 */
                LogUtil.d("注册失败: " + tlsErrInfo.Msg);
                ToastUtil.showLongToast("注册失败：该用户" + tlsErrInfo.Msg);
            }

            @Override
            public void OnStrAccRegTimeout(TLSErrInfo tlsErrInfo) {
            /* 网络超时，可能是用户网络环境不稳定，一般让用户重试即可。*/
                LogUtil.d("网络超时");
            }
        };
    }

    private void register(String userID, String password) {
        // 引导用户输入合法的用户名和密码
        int result = tlsHelper.TLSStrAccReg(userID, password, strAccRegListener);
        if (result == TLSErrInfo.INPUT_INVALID) {
            ToastUtil.showLongToast("请输入合法的用户名或密码");
        }
    }
}
