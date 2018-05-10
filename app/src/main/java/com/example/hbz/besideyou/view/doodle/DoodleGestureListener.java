package com.example.hbz.besideyou.view.doodle;

/**
 * @ClassName: com.example.besideyou.view.doodle
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/15 22:51
 */

public interface DoodleGestureListener {

    /**
     * 单指移动
     *
     * @param x 当前点的x坐标
     * @param y 当前点的y坐标
     */
    void onSingleMove(float x, float y);

    /**
     * 双指移动
     *
     * @param k  放大系数
     * @param cx 中心点x坐标
     * @param cy 中心点y坐标
     * @param dx x轴偏移量
     * @param cy y轴偏移量
     */
    void onDoubleMove(float k, float cx, float cy, float dx, float dy);

    /**
     * 单击事件
     *
     * @param x 单击事件x坐标
     * @param y 单击事件y坐标
     */
    void onClick(float x, float y);

    /**
     * 单指滑动手指按下事件（单击时不会触发）
     *
     * @param x 单击坐标x
     * @param y 单击坐标y
     */
    void onSingleDown(float x, float y);

    /**
     * 双指滑动最后一个手指按下事件
     * @param x1 手指1坐标x
     * @param y1 手指1坐标y
     * @param x2 手指2坐标x
     * @param y2 手指2坐标y
     */
    void onDoubleDown(float x1, float y1, float x2, float y2);

    /**
     * 单指滑动抬起
     *
     * @param x 抬起手指坐标x
     * @param y 抬起手指坐标y
     */
    void onSingleUp(float x, float y);

    /**
     * 双指滑动抬起
     *
     * @param x1 抬起手指1坐标x
     * @param y1 抬起手指1坐标y
     * @param x2 抬起手指2坐标x
     * @param y2 抬起手指2坐标y
     */
    void onDoubleUp(float x1, float y1, float x2, float y2);

}
