package com.example.hbz.besideyou.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.event.ContactsEvent;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.hbz.besideyou.utils.StatusBarUtils;
import com.example.hbz.besideyou.utils.ToastUtil;
import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.example.hbz.besideyou.activity.ChatActivity.CONVERSATION_IDENTIFIER_STR;
import static com.example.hbz.besideyou.activity.ChatActivity.CONVERSATION_TYPE_C2C;
import static com.example.hbz.besideyou.activity.ChatActivity.CONVERSATION_TYPE_STR;

/**
 * 好友信息显示界面
 */
public class FriendInfoActivity extends BastActivity {

    public static final String FRIEND_INFO_IDENTIFIER_KEY = "identifier";
    public static final String FRIEND_INFO_IS_FRIEND_KEY = "is_friend";

    private String identifier;
    private boolean isFriend = true;

    private TIMUserProfile userProfile;
    private View top_view;
    private ImageView iv_back;
    private ImageView iv_setting;

    private ImageView iv_face;//头像
    private TextView tv_name;
    private TextView tv_self_signature;
    private TextView tv_id_identification;
    private TextView tv_gender;
    private TextView tv_birthday;
    private TextView tv_phone_number;
    private TextView tv_remark;
    private Button btn_send_message;
    private Button btn_add_friend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        if (intent != null) {
            identifier = intent.getStringExtra(FRIEND_INFO_IDENTIFIER_KEY);
            if (TextUtils.isEmpty(identifier)) {
                LogUtil.e("通过Intent获取好友的identifier失败");
                ToastUtil.showLongToast("通过Intent获取identifier失败");
                finish();
            }
            isFriend = intent.getBooleanExtra(FRIEND_INFO_IS_FRIEND_KEY, true);
        }

        initView();
        StatusBarUtils.setStatus(this, top_view);
        initData();

    }

    private void initData() {
        if (TextUtils.isEmpty(identifier)) {
            return;
        }

        LinkedList<String> users = new LinkedList<>(); //要获取资料的好友 identifier 列表
        users.add(identifier);

        if (isFriend) {// 获取好友信息
            TIMFriendshipManager.getInstance().getFriendsProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int i, String s) {
                    LogUtil.e("获取好友的identifier失败,\n错误码：" + i + "\n错误信息：" + s);
                }

                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    if (timUserProfiles != null && timUserProfiles.size() > 0) {
                        userProfile = timUserProfiles.get(0);
                    }
                    notifyUI();
                }
            });
        } else {// 获取任意人员信息
            TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int i, String s) {
                    LogUtil.e("获取好友的identifier失败,\n错误码：" + i + "\n错误信息：" + s);
                }

                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    if (timUserProfiles != null && timUserProfiles.size() > 0) {
                        userProfile = timUserProfiles.get(0);
                    }
                    notifyUI();
                }
            });
        }

    }

    private void initView() {
        top_view = findViewById(R.id.top_view);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> finish());
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_setting.setOnClickListener(v -> setting(v));

        iv_face = (ImageView) findViewById(R.id.iv_face);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_self_signature = (TextView) findViewById(R.id.tv_self_signature);
        tv_id_identification = (TextView) findViewById(R.id.tv_id_identification);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        btn_send_message = (Button) findViewById(R.id.btn_send_message);
        btn_send_message.setOnClickListener(v -> sendMessage());
        btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
        notifyAddFriendBtnUI();// 不是好友才显示添加好友

    }

    // 发送消息逻辑
    private void sendMessage() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(CONVERSATION_TYPE_STR, CONVERSATION_TYPE_C2C);// 会话类型
        intent.putExtra(CONVERSATION_IDENTIFIER_STR, identifier);// 会话标识
        startActivity(intent);
        finish();
    }

    private void addFriend() {
        //TODO 添加为好友逻辑
        if (identifier == null) {
            return;
        }
        java.util.List<TIMAddFriendRequest> users = new ArrayList<>();
        TIMAddFriendRequest timAddFriendRequest = new TIMAddFriendRequest();
        timAddFriendRequest.setIdentifier(identifier);
        users.add(timAddFriendRequest);

        TIMFriendshipManager.getInstance().addFriend(users, new TIMValueCallBack<List<TIMFriendResult>>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.e("添加好友请求失败：" + i + "," + s);
                ToastUtil.showShortToast("添加好友请求失败" + s);
            }

            @Override
            public void onSuccess(List<TIMFriendResult> timFriendResults) {
                ToastUtil.showShortToast("添加好友请求成功");
                isFriend = true;
                notifyAddFriendBtnUI();
                EventBus.getDefault().post(new ContactsEvent());
            }
        });
    }

    /**
     * 设置菜单
     *
     * @param v
     */
    private void setting(View v) {
        PopupMenu popup = new PopupMenu(this, v);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.friend_info_menu, popup.getMenu());
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.modify_friend_notes:

                    break;
                case R.id.delete_friend:

                    break;
                case R.id.put_on_blacklist:

                    break;
                default:
                    break;
            }
            return false;
        });
        //显示
        popup.show();
    }

    private void notifyUI() {
        if (userProfile == null) {
            return;
        }

        String faceUrl = userProfile.getFaceUrl();//头像地址
        if (!TextUtils.isEmpty(faceUrl)) {
            Glide.with(this).load(faceUrl).into(iv_face);
        }
        String nickName = userProfile.getNickName();//昵称
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
            Date currentTime = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年 MM月 dd日");
            String dateString = formatter.format(currentTime);
            tv_birthday.setText(dateString);
        }
        String remark = userProfile.getRemark();// 备注
        if (!TextUtils.isEmpty(remark)) {
            tv_remark.setText(remark);
        }
        //TODO tv_phone_number;手机号码

        //
        if (!isFriend) {// 如果当前标志不是好友，那么重新刷新，看是否是好友。
            notifyIsFriend();
        }
    }

    private void notifyIsFriend() {
        //获取好友列表
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                LogUtil.e("获取所有好友失败: " + code + " desc" + desc);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result) {
                for (TIMUserProfile res : result) {
                    if (identifier.equals(res.getIdentifier())) {
                        isFriend = true;
                        runOnUiThread(() -> notifyAddFriendBtnUI());
                        return;
                    }
                }
            }
        });
    }

    private void notifyAddFriendBtnUI() {
        if (isFriend) {// 不是好友才显示添加好友
            if (btn_add_friend.getVisibility() != View.GONE) {
                btn_add_friend.setVisibility(View.GONE);
            }
        } else {
            if (btn_add_friend.getVisibility() != View.VISIBLE) {
                btn_add_friend.setVisibility(View.VISIBLE);
            }
            btn_add_friend.setOnClickListener(v -> addFriend());
        }
    }


}
