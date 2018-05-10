package com.example.hbz.besideyou.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.cosxml.CosManage;
import com.example.cosxml.ResultListener;
import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.utils.FileUtil;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.hbz.besideyou.utils.PhotoUtils;
import com.example.hbz.besideyou.utils.StatusBarUtils;
import com.example.hbz.besideyou.utils.ToastUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.example.hbz.besideyou.Constant.CHECK_PHONE_RULE;

public class MyInfoActivity extends BastActivity {

    private static final int CHOICE_FROM_PICTURES = 0;//选择图片
    private static final int CUT_PICTURES_RESULT = 1;//裁剪图片
    private Uri desUri;// 裁剪后图片存放的位置

    private LinearLayout top_view;
    private ImageView iv_face;
    private TextView tv_name;
    private TextView tv_self_signature;
    private TextView tv_id_identification;
    private TextView tv_gender;
    private TextView tv_birthday;
    private TextView tv_phone_number;
    private TIMUserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        initView();
        StatusBarUtils.setStatus(this, top_view);

        initData();
    }

    private void initView() {
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> finish());
        ImageView iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_setting.setOnClickListener(v -> setting());
        top_view = (LinearLayout) findViewById(R.id.top_view);

        iv_face = (ImageView) findViewById(R.id.iv_face);
        iv_face.setOnClickListener(v -> changeFace());
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setOnClickListener(v -> showInputDialog(v.getId()));
        tv_self_signature = (TextView) findViewById(R.id.tv_self_signature);
        tv_self_signature.setOnClickListener(v -> showInputDialog(v.getId()));
        tv_id_identification = (TextView) findViewById(R.id.tv_id_identification);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_gender.setOnClickListener(v -> changeGender());
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_birthday.setOnClickListener(v -> changeBirthday());
        tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
        tv_phone_number.setOnClickListener(v -> showInputDialog(v.getId()));


    }

    private void changeGender() {
        RadioGroup sexView = new RadioGroup(this);
        sexView.setPadding(40, 40, 40, 0);
        RadioButton male = new RadioButton(this);
        male.setPadding(20, 20, 20, 20);
        male.setText("男");
        male.setSelected(true);
        sexView.addView(male);
        RadioButton female = new RadioButton(this);
        female.setPadding(20, 20, 20, 20);
        female.setText("女");
        sexView.addView(female);
        RadioButton unSet = new RadioButton(this);
        unSet.setPadding(20, 20, 20, 20);
        unSet.setText("不设置");
        sexView.addView(unSet);

        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(this);
        inputDialog.
                setView(sexView).
                setTitle("").
                setPositiveButton("确定",
                        (dialog, which) -> {
                            TIMFriendGenderType type;
                            if (male.isChecked()) {
                                type = TIMFriendGenderType.Male;
                            } else if (female.isChecked()) {
                                type = TIMFriendGenderType.Female;
                            } else {
                                type = TIMFriendGenderType.Unknow;
                            }
                            TIMFriendshipManager.getInstance().setGender(type, new TIMCallBack() {
                                @Override
                                public void onError(int code, String desc) {
                                    LogUtil.e("设置性别失败: " + code + " desc:" + desc);
                                }

                                @Override
                                public void onSuccess() {
                                    if (male.isChecked()) {
                                        tv_gender.setText("男");
                                    } else if (female.isChecked()) {
                                        tv_gender.setText("女");
                                    } else {
                                        tv_gender.setText("");
                                    }
                                }
                            });
                        }).
                show();
    }

    private void showInputDialog(int viewId) {
        EditText editText = new EditText(this);
        switch (viewId) {
            case R.id.tv_name:
                editText.setHint("请输入新昵称");
                break;
            case R.id.tv_self_signature:
                editText.setHint("请输入新个性签名");
                break;
            case R.id.tv_phone_number:
                editText.setHint("请输入新手机号");
                break;
        }
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(this);
        inputDialog.
                setView(editText).
                setTitle("　").
                setPositiveButton("确定",
                        (dialog, which) -> {
                            String text = editText.getText().toString();
                            switch (viewId) {
                                case R.id.tv_name:
                                    changeNickName(text);
                                    break;
                                case R.id.tv_self_signature:
                                    changeSelfSignature(text);
                                    break;
                                case R.id.tv_phone_number:
                                    changePhoneNumber(text);
                                    break;
                                default:
                                    break;
                            }

                        }).
                show();
    }

    /**
     * 改变手机号码
     *
     * @param text
     */
    private void changePhoneNumber(String text) {
        if (true) {
            ToastUtil.showShortToast("暂时不支持手机号码设置");
            return;
        }
        TIMFriendshipManager.getInstance().setCustomInfo("Tag_Profile_Custom_Phone", text.getBytes(), new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                LogUtil.e("设置手机号码失败: " + code + " desc:" + desc);
            }

            @Override
            public void onSuccess() {
                runOnUiThread(() -> tv_phone_number.setText(text));
            }
        });
    }

    /**
     * 改变生日
     */
    private void changeBirthday() {
        Calendar date = Calendar.getInstance();
        //点击日期按钮布局 设置日期
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, (view, year, month, day) -> {
            //更新EditText控件日期 小于10加0
            Date selectDate = new Date(year, month, day);
            long time = selectDate.getTime();
            TIMFriendshipManager.getInstance().setBirthday(time, new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    LogUtil.e("设置个生日错误: " + code + " desc" + desc);
                }

                @Override
                public void onSuccess() {
                    runOnUiThread(() -> tv_birthday.setText((year + "年" + (month + 1) + "月" + day + "日")));
                }
            });
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
        datePickerDialog.show();

    }

    /**
     * 个性签名
     *
     * @param text
     */
    private void changeSelfSignature(String text) {
        TIMFriendshipManager.getInstance().setSelfSignature(text, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                LogUtil.e("设置个性签名失败: " + code + " desc" + desc);
            }

            @Override
            public void onSuccess() {
                runOnUiThread(() -> tv_self_signature.setText(text));
            }
        });
    }

    /**
     * 设置新昵称
     *
     * @param text
     */
    private void changeNickName(String text) {
        TIMFriendshipManager.getInstance().setNickName(text, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                LogUtil.e("设置昵称失败: " + code + " desc" + desc);
            }

            @Override
            public void onSuccess() {
                runOnUiThread(() -> tv_name.setText(text));
            }
        });
    }

    /**
     * 改变头像
     */
    private void changeFace() {
        PhotoUtils.openExternalImages(this, CHOICE_FROM_PICTURES);
    }

    /**
     * 设置
     */
    private void setting() {

    }

    private void initData() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("获取好友的identifier失败,\n错误码：" + i + "\n错误信息：" + s);
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                userProfile = timUserProfile;
                notifyUI();
            }
        });
    }

    private void notifyUI() {
        if (userProfile == null) {
            return;
        }

        String faceUrl = userProfile.getFaceUrl();//头像地址
        if (!TextUtils.isEmpty(faceUrl)) {
            Glide.with(this).applyDefaultRequestOptions(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).load(faceUrl).into(iv_face);
        }
        String nickName = userProfile.getNickName();//昵称
        String identifier = userProfile.getIdentifier();

        if (!TextUtils.isEmpty(nickName)) {
            tv_name.setText(nickName);
        } else if (!TextUtils.isEmpty(identifier)) {
            tv_name.setText(identifier);
        }

        if (!TextUtils.isEmpty(identifier)) {
            tv_id_identification.setText(identifier);
        }

        String selfSignature = userProfile.getSelfSignature();// 个人签名
        if (!TextUtils.isEmpty(selfSignature)) {
            tv_self_signature.setText(selfSignature);
        }

        TIMFriendGenderType gender = userProfile.getGender();// 性别
        if (gender != null) {
            if (gender.equals(TIMFriendGenderType.Male)) {
                tv_gender.setText(getString(R.string.male));
            } else if (gender.equals(TIMFriendGenderType.Female)) {
                tv_gender.setText(getString(R.string.female));
            }
        }
        long birthday = userProfile.getBirthday();// 生日
        if (birthday > 0) {
            Date currentTime = new Date(birthday);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年 MM月 dd日");
            String dateString = formatter.format(currentTime);
            tv_birthday.setText(dateString);
        }
        // tv_phone_number;手机号码
        String phoneStr = null;
        Map<String, byte[]> customInfo = userProfile.getCustomInfo();
        if (customInfo != null) {
            byte[] phones = customInfo.get("Tag_Profile_Custom_Phone");
            if (phones != null) {
                phoneStr = new String(phones);
                if (!TextUtils.isEmpty(phoneStr)) {
                    tv_phone_number.setText(phoneStr);
                }
            }
        }
        if (TextUtils.isEmpty(phoneStr)) {
            phoneStr = identifier.substring(3);
            if (Pattern.matches(CHECK_PHONE_RULE, phoneStr)) {
                //用户名是手机号
                tv_phone_number.setText(phoneStr);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            // 相册
            switch (requestCode) {
                case CHOICE_FROM_PICTURES: // 来自相册/拍照
                    Uri orgUri = data.getData();
                    desUri = Uri.parse("file://" + "/" + FileUtil.getAppRootPath() + "cut_temp.jpg");
                    // 发起裁剪
                    PhotoUtils.cropImageUri(this, orgUri, desUri, 1, 1, 160, 160, CUT_PICTURES_RESULT);
                    break;
                case CUT_PICTURES_RESULT:
                    setFace();
                    break;
            }
        }
    }

    private void setFace() {
        if (desUri == null) {
            return;
        }
        String path = PhotoUtils.getPath(this, desUri);// 获取裁剪后的图片路径
        File compFile = FileUtil.getAppCompressFile();
        // 压缩文件
        Luban.with(this)
                .load(path)                                     // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setTargetDir(compFile.getAbsolutePath() + "/") // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        LogUtil.i("开始压缩");
                    }

                    @Override
                    public void onSuccess(File file) {
                        LogUtil.i("压缩成功后,上传图片");
                        // 上传图片
                        String srcPath = file.getAbsolutePath();
                        String cosPath = "/face/" + userProfile.getIdentifier() + ".jpg";
                        CosManage.getInstance(MyInfoActivity.this).upload(srcPath, cosPath, new ResultListener() {
                            @Override
                            public void onSuccess(String accessUrl) {
                                LogUtil.i("上传图片成功，设置头像地址");
                                TIMFriendshipManager.getInstance().setFaceUrl(accessUrl, new TIMCallBack() {
                                    @Override
                                    public void onError(int code, String desc) {
                                        //错误码 code 和错误描述 desc，可用于定位请求失败原因
                                        //错误码 code 列表请参见错误码表
                                        LogUtil.e("设置头像图片失败: " + code + " desc" + desc);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        LogUtil.d("设置头像图片成功");
                                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                        iv_face.setImageBitmap(bitmap);
                                    }
                                });
                            }

                            @Override
                            public void onFail(String errorMsg) {
                                LogUtil.e("上传图片失败" + errorMsg);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e("当压缩过程出现问题时调用");
                    }
                }).launch();    //启动压缩
    }
}
