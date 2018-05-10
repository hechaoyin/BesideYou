package com.example.hbz.besideyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.hbz.besideyou.BesideYouApp;


/**
 * @ClassName: com.example.besideyou.fragment
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/18 11:52
 */

public class BaseFragment extends Fragment {
    private Activity activity;

    @Override
    public Context getContext() {
        if (activity != null) {
            return activity;
        }
        return BesideYouApp.getAppContext();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity = getActivity();
    }
}
