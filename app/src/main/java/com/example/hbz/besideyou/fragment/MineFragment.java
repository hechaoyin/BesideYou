package com.example.hbz.besideyou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.activity.AboutUsActivity;
import com.example.hbz.besideyou.activity.LaboratoryActivity;
import com.example.hbz.besideyou.activity.LoginActivity;
import com.example.hbz.besideyou.activity.MyInfoActivity;
import com.example.hbz.besideyou.activity.SettingActivity;
import com.example.hbz.besideyou.utils.LogUtil;
import com.example.hbz.besideyou.utils.StatusBarUtils;
import com.example.hbz.besideyou.utils.ToastUtil;
import com.example.hbz.besideyou.view.CircleImageView;
import com.example.im.business.LoginBusiness;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;


/**
 * @ClassName: com.example.besideyou.fragment
 * @Description: 消息界面
 * @Author: HBZ
 * @Date: 2018/3/17 11:45
 */

public class MineFragment extends BaseFragment {
    private View rootView;
    private View top_view;
    private LinearLayout ll_my_info;
    private CircleImageView my_info_face;
    private TextView my_info_name;
    private TextView my_info_id;
    private LinearLayout ll_setting;
    private Button btn_account_logout;

    private TIMUserProfile userProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main_mine, container, false);
            initView(rootView);
            StatusBarUtils.setStatus(getActivity(), top_view);
            initData();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
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
            Glide.with(this).
                    applyDefaultRequestOptions(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).
                    load(faceUrl).
                    into(my_info_face);
        }

        String nickName = userProfile.getNickName();//昵称
        String identifier = userProfile.getIdentifier();

        if (!TextUtils.isEmpty(nickName)) {
            my_info_name.setText(nickName);
        } else if (!TextUtils.isEmpty(identifier)) {
            my_info_name.setText(identifier);
        }
        if (!TextUtils.isEmpty(identifier)) {
            my_info_id.setText(identifier);
        }
    }

    private void initView(View rootView) {
        top_view = rootView.findViewById(R.id.top_view);

        ll_my_info = (LinearLayout) rootView.findViewById(R.id.ll_my_info);
        ll_my_info.setOnClickListener(v -> startActivity(new Intent(getContext(), MyInfoActivity.class)));
        my_info_face = (CircleImageView) rootView.findViewById(R.id.my_info_face);
        my_info_name = (TextView) rootView.findViewById(R.id.my_info_name);
        my_info_id = (TextView) rootView.findViewById(R.id.my_info_id);

        ll_setting = (LinearLayout) rootView.findViewById(R.id.ll_setting);
        ll_setting.setOnClickListener(v -> startActivity(new Intent(getContext(), SettingActivity.class)));

        LinearLayout ll_about_us = (LinearLayout) rootView.findViewById(R.id.ll_about_us);
        ll_about_us.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutUsActivity.class)));

        LinearLayout ll_laboratory = (LinearLayout) rootView.findViewById(R.id.ll_laboratory);
        ll_laboratory.setOnClickListener(v -> startActivity(new Intent(getContext(), LaboratoryActivity.class)));

        btn_account_logout = (Button) rootView.findViewById(R.id.btn_account_logout);
        btn_account_logout.setOnClickListener(v -> accountLogout());
    }

    private void accountLogout() {
        LoginBusiness.logout(timCallBack);
        startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();
    }

    private static TIMCallBack timCallBack = new TIMCallBack() {
        @Override
        public void onError(int i, String s) {
            ToastUtil.showLongToast("退出错误:" + s);
        }

        @Override
        public void onSuccess() {
            ToastUtil.showLongToast("退出成功");
        }
    };
}
