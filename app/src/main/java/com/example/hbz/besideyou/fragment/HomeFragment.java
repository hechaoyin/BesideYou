package com.example.hbz.besideyou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.activity.DoodleCreateTipActivity;
import com.example.hbz.besideyou.adapter.recycleviewadapter.MainCardAdapter;
import com.example.hbz.besideyou.adapter.recycleviewadapter.listener.OnItemClickListener;
import com.example.hbz.besideyou.bean.CardItemBean;
import com.example.hbz.besideyou.utils.StatusBarUtils;
import com.example.hbz.besideyou.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: com.example.besideyou.fragment
 * @Description: 主页
 * @Author: HBZ
 * @Date: 2018/3/17 11:45
 */

public class HomeFragment extends BaseFragment implements OnItemClickListener {
    private View rootView;
    private View top_view;

    private RecyclerView rv_main_card;
    private MainCardAdapter cardAdapter;
    private List<CardItemBean> cardList = new ArrayList<>();
    private List<Intent> cardOnClickIntents = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main_home, container, false);
            initView();
            StatusBarUtils.setStatus(getActivity(), top_view);
            initCardData();
        }
        return rootView;
    }

    private void initView() {
        top_view = rootView.findViewById(R.id.top_view);
        rv_main_card = (RecyclerView) rootView.findViewById(R.id.rv_main_card);
        cardAdapter = new MainCardAdapter(cardList);
        rv_main_card.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        rv_main_card.setAdapter(cardAdapter);
        cardAdapter.setOnItemClickListener(this);
    }

    private void initCardData() {
        cardList.clear();
        cardList.add(new CardItemBean(R.drawable.ic_graffiti, "互动涂鸦", "同屏绘画，分解问题。"));
        cardList.add(new CardItemBean(R.drawable.ic_read, "互动阅读", "同屏读书，共同互动。"));
        cardList.add(new CardItemBean(R.drawable.ic_remote_assistance, "远程协助", "远程操控手机，协助处理问题。"));
        cardList.add(new CardItemBean(R.drawable.ic_game, "互娱游戏", "同屏游戏，娱乐放松"));


        //添加Item点击事件
        cardOnClickIntents.add(new Intent(getContext(), DoodleCreateTipActivity.class));

        cardAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(View view, int position) {
        if (cardOnClickIntents == null || cardOnClickIntents.size() <= position) {
            ToastUtil.showShortToast("功能未开放" + position);
            return;
        }
        Intent intent = cardOnClickIntents.get(position);
        if (intent == null) {
            return;
        }
        startActivity(intent);
    }
}
