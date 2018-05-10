package com.example.hbz.besideyou;

import android.support.v7.app.AppCompatActivity;

/**
 * @ClassName: com.example.besideyou
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/16 10:46
 */

public abstract class BastActivity extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        BesideYouApp.getRefWatcher().watch(this);
        super.onDestroy();
    }
}