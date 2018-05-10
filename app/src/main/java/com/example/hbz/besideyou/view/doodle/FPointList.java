package com.example.hbz.besideyou.view.doodle;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: com.example.hbz.besideyou.view.doodle
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/29 11:07
 */

public class FPointList {
    private List<FPoint> points = new ArrayList<>();
    private int begin = 0;
    private int end = 0;

    public FPointList() {
    }

    public void clear() {
        begin = end = 0;
    }

    public FPoint get(int index) {
        return points.get((begin + index) % points.size());
    }

    public FPoint add(float x, float y) {
        FPoint fPoint;
        if (freeSpace() > 0) {
            fPoint = points.get(end++);
            fPoint.setXY(x, y);
        } else {
            fPoint = new FPoint(x, y);
            points.add(end++, fPoint);
        }
        return fPoint;
    }

    public void remove() {
        if (begin == end) {
            return;
        }
        begin++;
        if (begin == points.size()) {
            begin = 0;
        }
    }

    public int size() {
        if (begin == end) {
            return 0;
        }
        if (begin > end) {
            return points.size() - (begin - end);
        }
        if (begin < end) {
            return end - begin;
        }
        return 0;
    }

    private int freeSpace() {
        if (begin == end) {
            return points.size();
        }
        if (begin < end) {
            return points.size() - (end - begin);
        }
        if (begin > end) {
            return begin - end;
        }
        return 0;
    }
}
