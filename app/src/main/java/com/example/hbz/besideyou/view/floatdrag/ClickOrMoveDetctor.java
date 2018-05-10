package com.example.hbz.besideyou.view.floatdrag;

import android.view.MotionEvent;
import android.view.View;

/**
 * @ClassName: com.example.hbz.besideyou.view.floatdrag
 * @Description:
 * @Author: HBZ
 * @Date: 2018/4/27 10:30
 */

public abstract class ClickOrMoveDetctor implements View.OnTouchListener {

    private static final float IGNORE_MOVE = 30;
    private float lastX, lastY;
    private float viewDownX = -1, viewDownY = -1;
    private boolean isOnMove = false;

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(event);
                break;
            case MotionEvent.ACTION_OUTSIDE:
                return true;
            default:
                break;
        }
        return false;
    }

    private void touchUp(MotionEvent event) {
        if (!isOnMove) {
            onClick();
        } else {
            isOnMove = false;
        }
    }

    private void touchMove(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (!isOnMove) {
            if (Math.abs(lastX - x) < IGNORE_MOVE && Math.abs(lastX - y) < IGNORE_MOVE) {
                return;
            }
            isOnMove = true;
        }
        onMoveTo(lastX, lastY);
        onMoveBy(x - lastX, y - lastY);
        lastX = x;
        lastY = y;
    }

    private void touchDown(MotionEvent event) {
        lastX = event.getRawX();
        lastY = event.getRawY();
        viewDownX = event.getX();
        viewDownY = event.getY();
    }

    public float getViewDownX() {
        return viewDownX;
    }

    public float getViewDownY() {
        return viewDownY;
    }

    abstract void onMoveTo(float x, float y);

    abstract void onMoveBy(float dx, float dy);

    abstract void onClick();
}
