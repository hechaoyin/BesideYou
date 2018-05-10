package com.example.hbz.besideyou.view.ninegridimageview;

import android.content.Context;
import android.widget.ImageView;

import com.example.hbz.besideyou.view.CircleImageView;


/**
 *
 * @author HBZ
 * @date 2018/1/22
 */

public abstract class NineGridImageViewAdapter<T> {
    protected abstract void onDisplayImage(Context context, ImageView imageView, T t);

    protected ImageView generateImageView(Context context) {
        ImageView imageView = new CircleImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    //这里可以添加你所需要的事件之类的方法
}