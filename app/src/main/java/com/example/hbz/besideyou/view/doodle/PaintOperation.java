package com.example.hbz.besideyou.view.doodle;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: com.example.hbz.besideyou.view.doodle
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/20 18:05
 */

public class PaintOperation {

    private int colour; // 颜色
    private float strokeWidth; // 画笔大小
    private List<FPoint> pointList; // 点集合，路径。

    public PaintOperation() {
        this(Color.RED, 3);
    }

    public PaintOperation(int colour, float strokeWidth) {
        this(colour, strokeWidth, new ArrayList<>());
    }

    public PaintOperation(int colour, float strokeWidth, List<FPoint> pointList) {
        this.colour = colour;
        this.strokeWidth = strokeWidth;
        this.pointList = pointList;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public List<FPoint> getPointList() {
        return pointList;
    }

    public void setPointList(List<FPoint> pointList) {
        this.pointList = pointList;
    }
}
