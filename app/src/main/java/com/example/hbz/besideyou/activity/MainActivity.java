package com.example.hbz.besideyou.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hbz.besideyou.BastActivity;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.adapter.MainPagerAdapter;
import com.example.hbz.besideyou.fragment.ContactsFragment;
import com.example.hbz.besideyou.fragment.HomeFragment;
import com.example.hbz.besideyou.fragment.ConversationFragment;
import com.example.hbz.besideyou.fragment.MineFragment;
import com.example.im.data.FriendContactsData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HBZ
 */
public class MainActivity extends BastActivity {

    private ViewPager vp_main;
    private TabLayout tl_main;

    private MainPagerAdapter pagerAdapter;
    // 保存Fragment
    private List<Fragment> fragments = new ArrayList<>();
    // 保存Fragment的导航栏标题
    private List<String> titles = new ArrayList<>();
    // 保存Fragment的导航栏图标
    private List<Integer> tabSelectIcons = new ArrayList<>(); //选中时图标
    private List<Integer> tabUnSelectIcons = new ArrayList<>();//没选中时图标

    private long timebegin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 加载联系人数据
        FriendContactsData.getInstance();

        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        long timeEnd = System.currentTimeMillis();
        if (timeEnd - timebegin < 1000) {
            super.onBackPressed();
        } else {
            Snackbar.make(tl_main, "退出应用？", Snackbar.LENGTH_LONG)
                    .setAction("退出", v -> finish())
                    .setDuration(1000)
                    .show();
            timebegin = timeEnd;
        }
    }

    private void initData() {
        // fragments数据
        fragments.clear();
        fragments.add(new HomeFragment());
        fragments.add(new ConversationFragment());
        fragments.add(new ContactsFragment());
        fragments.add(new MineFragment());
        pagerAdapter.notifyDataSetChanged();

        // TabLayout中标题
        titles.clear();
        titles.add(getString(R.string.tab_main_home));
        titles.add(getString(R.string.tab_main_message));
        titles.add(getString(R.string.tab_main_address));
        titles.add(getString(R.string.tab_main_mine));

        // TabLayout中未选中图标
        tabUnSelectIcons.clear();
        tabUnSelectIcons.add(R.drawable.tab_home_gray);
        tabUnSelectIcons.add(R.drawable.tab_message_gray);
        tabUnSelectIcons.add(R.drawable.tab_address_gray);
        tabUnSelectIcons.add(R.drawable.tab_mine_gray);

        // TabLayout中选中图标
        tabSelectIcons.clear();
        tabSelectIcons.add(R.drawable.tab_home_blue);
        tabSelectIcons.add(R.drawable.tab_message_blue);
        tabSelectIcons.add(R.drawable.tab_address_blue);
        tabSelectIcons.add(R.drawable.tab_mine_blue);

        initTab();
    }

    private void initTab() {
        tl_main.setupWithViewPager(vp_main);
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            TabLayout.Tab tab = tl_main.getTabAt(i);//获得每一个tab
            if (tab == null) {
                continue;
            }

            tab.setCustomView(R.layout.item_main_tab);//给每一个tab设置view

            View customView = tab.getCustomView();

            if (customView == null) {
                continue;
            }

            TextView textView = (TextView) customView.findViewById(R.id.tv_tab_home_item_name);
            if (textView != null && i < titles.size()) {
                textView.setText(titles.get(i));//设置tab上的文字
                if (i == 0) {
                    textView.setTextColor(getResources().getColorStateList(R.color.blue));
//                    textView.setTextColor(R.color.blue);
                }
            }

            ImageView imageView = (ImageView) tab.getCustomView().findViewById(R.id.iv_tab_home_item_icon);
            if (imageView != null && i < tabUnSelectIcons.size()) {
                if (i == 0) {
                    imageView.setImageResource(tabSelectIcons.get(i));
                } else {
                    imageView.setImageResource(tabUnSelectIcons.get(i));
                }
            }

        }
    }

    private void initView() {
        vp_main = (ViewPager) findViewById(R.id.vp_main);
        tl_main = (TabLayout) findViewById(R.id.tl_main);
        if (pagerAdapter == null) {
            pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        }
        vp_main.setAdapter(pagerAdapter);
        vp_main.setOffscreenPageLimit(3);

        tl_main.setupWithViewPager(vp_main);//将TabLayout和ViewPager关联起来。
        tl_main.addOnTabSelectedListener(tabListener);
        tl_main = (TabLayout) findViewById(R.id.tl_main);
    }

    private TabLayout.OnTabSelectedListener tabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getCustomView() == null) {
                return;
            }
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tv_tab_home_item_name);
            if (textView != null) {
                textView.setTextColor(getResources().getColorStateList(R.color.blue));
            }

            ImageView imageView = (ImageView) tab.getCustomView().findViewById(R.id.iv_tab_home_item_icon);
            int position = tab.getPosition();
            if (imageView != null && position < tabSelectIcons.size()) {
                imageView.setImageResource(tabSelectIcons.get(position));
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            if (tab.getCustomView() == null) {
                return;
            }
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tv_tab_home_item_name);
            if (textView != null) {
                textView.setTextColor(getResources().getColorStateList(R.color.main_tab_text));
            }

            ImageView imageView = (ImageView) tab.getCustomView().findViewById(R.id.iv_tab_home_item_icon);
            int position = tab.getPosition();
            if (imageView != null && position < tabUnSelectIcons.size()) {
                imageView.setImageResource(tabUnSelectIcons.get(position));
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    };

}
