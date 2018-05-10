package com.example.hbz.besideyou.other;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.hbz.besideyou.utils.LogUtil;

/**
 * @ClassName: com.example.besideyou.view.doodle
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/14 19:26
 */

public class GestureDetectorListener extends GestureDetector.SimpleOnGestureListener {
    public GestureDetectorListener() {
    }

    /****************************************************
     * GesturetDetector.OnGestureListener 接口
     *
     * 快按屏幕:onDown
     * 慢按屏幕：onDown–>onShowPress
     * 按下屏幕等待一段时间onDown–>onShowPress–>onLongPress
     * 拖动屏幕：onDown–>onShowPress–>onScroll(多个)–>onFling
     * 快速滑动：onDown–>onScroll(多个)–>onFling
     ****************************************************/

    /**
     * 每按一下屏幕立即触发
     */
    @Override
    public boolean onDown(MotionEvent e) {
        LogUtil.d("onDown: ");
        return false;
    }

    /**
     * 用户按下屏幕并且没有移动或松开。主要是提供给用户一个可视化的反馈，告诉用户他们的按下操作已经
     * 被捕捉到了。如果按下的速度很快只会调用onDown(),按下的速度稍慢一点会先调用onDown()再调用onShowPress().
     */
    @Override
    public void onShowPress(MotionEvent e) {
        LogUtil.d("onShowPress: ");
    }

    /**
     * 一次单纯的轻击抬手动作时触发
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        LogUtil.d("onSingleTapUp: ");
        return false;
    }

    /**
     * 屏幕拖动事件，如果按下的时间过长，调用了onLongPress，再拖动屏幕不会触发onScroll。拖动屏幕会多次触发
     *
     * @param e1        开始拖动的第一次按下down操作,也就是第一个ACTION_DOWN
     * @param distanceX 当前的x坐标与最后一次触发scroll方法的x坐标的差值。
     * @param distanceY 当前的y坐标与最后一次触发scroll方法的y坐标的差值。
     * @parem e2        触发当前onScroll方法的ACTION_MOVE
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        LogUtil.d("onScroll: " + e2.getAction()+"  "+e2.getActionMasked());
        return false;
    }

    /**
     * 长按。在down操作之后，过一个特定的时间触发
     */
    @Override
    public void onLongPress(MotionEvent e) {
        LogUtil.d("onLongPress: ");
    }

    /**
     * 按下屏幕，在屏幕上快速滑动后松开，由一个down,多个move,一个up触发
     *
     * @param e1                      开始快速滑动的第一次按下down操作,也就是第一个ACTION_DOWN
     * @param velocityX：X轴上的移动速度，像素/秒
     * @parem e2 触发当前onFling方法的move操作,也就是最后一个ACTION_MOVE
     * @parram velocityY：Y轴上的移动速度，像素/秒
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        LogUtil.d("onFling: ");
        return false;
    }

    /****************************************************
     * GesttureDetector.OnDoubleTapListener 接口
     *
     ****************************************************/

    /**
     * 单击事件。用来判定该次点击是单纯的SingleTap而不是DoubleTap，如果连续点击两次就是DoubleTap手势，
     * 如果只点击一次，系统等待一段时间后没有收到第二次点击则判定该次点击为SingleTap而不是DoubleTap，
     * 然后触发SingleTapConfirmed事件。触发顺序是：OnDown->OnsingleTapUp->OnsingleTapConfirmed
     * 关于onSingleTapConfirmed和onSingleTapUp的一点区别：二者的区别是：onSingleTapUp，只要手抬起就会执行，
     * 而对于onSingleTapConfirmed来说，如果双击的话，则onSingleTapConfirmed不会执行。
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        LogUtil.d("onSingleTapConfirmed: ");
        return false;
    }

    /**
     * 双击事件
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        LogUtil.d("onDoubleTap: ");
        return false;
    }

    /**
     * 双击间隔中发生的动作。指触发onDoubleTap以后，在双击之间发生的其它动作，包含down、up和move事件
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        LogUtil.d("onDoubleTapEvent: ");
        return false;
    }

    /****************************************************
     *  GestureDetector.OnContextClickListener
     ****************************************************/

    @Override
    public boolean onContextClick(MotionEvent e) {
        LogUtil.d("onContextClick: ");
        return false;
    }
}
