package com.example.hbz.besideyou.view.doodle;

import android.view.MotionEvent;

/**
 * @ClassName: com.example.besideyou.view.doodle
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/15 9:22
 */

public class DoodleGestureDetector {

    enum Type {
        NONE, DOUBLE, SINGLE
    }

    private Type type = Type.NONE;

    private float[] downPoint = new float[4];// 手指按下的起点（x1,y1,x2,y2）,只记录两个手指
    private float[] moveLast = new float[4];// 上一次手指移动的点（x1,y1,x2,y2）
    private DoodleGestureListener gestureListener;

    public DoodleGestureDetector() {
    }

    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                onTouchDown(event, action);
                break;

            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                onTouchUp(event, action);
                break;
            default:
                break;
        }
        return true;
    }

    private void onTouchDown(MotionEvent event, int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            if (event.getPointerCount() == 1) {
                downPoint[0] = event.getX(0);
                downPoint[1] = event.getY(0);
                moveLast[0] = downPoint[0];
                moveLast[1] = downPoint[1];
            }
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            if (event.getPointerCount() == 2) {
                downPoint[2] = event.getX(1);
                downPoint[3] = event.getY(1);
                moveLast[2] = downPoint[2];
                moveLast[3] = downPoint[3];
            }
        }
    }

    private void onTouchMove(MotionEvent event) {
        if (event.getPointerCount() == 1) { // 单指移动
            if (type == Type.DOUBLE) { //多指操作跳出
                return;
            }
            float x = event.getX(0);
            float y = event.getY(0);
            if (calculateTwoPoints(x, y, downPoint[0], downPoint[1]) < 20) {
                //滑动距离不足20px,不触发单点移动事件
                return;
            }
            if (type == Type.NONE) {
                gestureListener.onSingleDown(downPoint[0], downPoint[1]);
                type = Type.SINGLE;
            }

            if (gestureListener != null) {
                gestureListener.onSingleMove(x, y);
            }

        } else { // 双指移动
            if (type == Type.SINGLE) { //单指操作跳出
                return;
            } else if (type == Type.NONE) {
                type = Type.DOUBLE;
                gestureListener.onDoubleDown(downPoint[0], downPoint[1], downPoint[2], downPoint[3]);
            }
            float x1 = event.getX(0);
            float y1 = event.getY(0);
            float x2 = event.getX(1);
            float y2 = event.getY(1);
            float centreX = (x1 + x2) / 2f;
            float centreY = (y1 + y2) / 2f;

            float lastCX = (moveLast[0] + moveLast[2]) / 2f;
            float lastCY = (moveLast[1] + moveLast[3]) / 2f;

            float k = calculateTwoPoints(x1, y1, x2, y2) / calculateTwoPoints(moveLast);// 放大系数
            if (k < 0 || k > 10) {
                k = 1f;
            }
            if (gestureListener != null) {//触发双指移动事件
                gestureListener.onDoubleMove(k, centreX, centreY, centreX - lastCX, centreY - lastCY);
            }
            moveLast[0] = x1;
            moveLast[1] = y1;
            moveLast[2] = x2;
            moveLast[3] = y2;
        }
    }

    private void onTouchUp(MotionEvent event, int action) {
        if (action == MotionEvent.ACTION_UP) {
            if (type == Type.NONE) {
                gestureListener.onClick(downPoint[0], downPoint[1]);
            } else {
                float x1 = event.getX(0);
                float y1 = event.getY(0);
                if (type.equals(Type.SINGLE)) {
                    gestureListener.onSingleUp(x1, y1);
                }
                type = Type.NONE;
            }
        } else {
            float[] upPoint = new float[4];// 手指抬起（x1,y1,x2,y2）,只记录两个手指
            upPoint[0] = event.getX(0);
            upPoint[1] = event.getY(0);
            upPoint[2] = event.getX(1);
            upPoint[3] = event.getY(1);
            gestureListener.onDoubleUp(upPoint[0], upPoint[1], upPoint[2], upPoint[3]);
        }
    }

    /**
     * 计算两点距离
     */
    private float calculateTwoPoints(float points[]) {
        if (points.length < 4) {
            return 0f;
        }
        return calculateTwoPoints(points[0], points[1], points[2], points[3]);
    }

    /**
     * 计算两点距离
     */
    private float calculateTwoPoints(float x1, float y1, float x2, float y2) {
        double tempA, tempB;
        tempA = x1 > x2 ? (x1 - x2) : (x2 - x1);  // 横向距离 (取正数，因为边长不能是负数)
        tempB = y1 > y2 ? (y1 - y2) : (y2 - y1);  // 竖向距离 (取正数，因为边长不能是负数)
        return (float) Math.sqrt(tempA * tempA + tempB * tempB);  // 计算
    }

    public void setGestureListener(DoodleGestureListener gestureListener) {
        this.gestureListener = gestureListener;
    }
}
