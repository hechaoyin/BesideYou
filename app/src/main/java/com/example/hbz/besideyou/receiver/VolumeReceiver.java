package com.example.hbz.besideyou.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.example.hbz.besideyou.utils.LogUtil;

import java.lang.ref.WeakReference;

import static android.content.Context.AUDIO_SERVICE;

/**
 * @ClassName: com.example.hbz.besideyou.receiver
 * @Description:
 * @Author: HBZ
 * @Date: 2018/5/25 0:34
 */
public class VolumeReceiver extends BroadcastReceiver {

    private volatile static WeakReference<VolumeReceiver> instance;

    private static VolumeReceiver getInstance() {
        VolumeReceiver volumeReceiver;
        if (instance == null) {
            synchronized (VolumeReceiver.class) {
                if (instance == null) {
                    volumeReceiver = new VolumeReceiver();
                    instance = new WeakReference<>(volumeReceiver);
                } else {
                    volumeReceiver = instance.get();
                }
            }
        } else {
            volumeReceiver = instance.get();
        }
        return volumeReceiver;
    }

    static public void reegisterReceiver(Activity context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(getInstance(), filter);
    }

    static public void unregisterReceiver(Activity context) {
        if (instance == null) {
            return;
        }
        VolumeReceiver volumeReceiver = instance.get();
        if (volumeReceiver != null) {
            context.unregisterReceiver(volumeReceiver);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        AudioManager audioManager = ((AudioManager) context.getSystemService(AUDIO_SERVICE));
        //检测是否插入耳机，是的话关闭扬声器，否则反之
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            LogUtil.d("耳机转态改变");
            if (intent.getIntExtra("state", 0) == 0) {
                audioManager.setSpeakerphoneOn(true);
            } else if (intent.getIntExtra("state", 0) == 1) {
                audioManager.setSpeakerphoneOn(false);
            }
        }
    }
}