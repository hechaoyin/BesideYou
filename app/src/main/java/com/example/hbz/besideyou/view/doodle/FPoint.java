package com.example.hbz.besideyou.view.doodle;

/**
 * @ClassName: com.example.besideyou.view.doodle
 * @Description: float型的坐标点
 * @Author: HBZ
 * @Date: 2018/3/16 0:19
 */

public class FPoint {
    private float x;
    private float y;

    public FPoint() {
    }

    public FPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setXY(float x, float y) {
        this.x=x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "FPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
