package com.example.hbz.besideyou.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @ClassName: FragmentPagerAdapter
 * @Description: 主页面适配器
 * @Author: HBZ
 * @Date: 2018/1/18 14:29
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }
}
