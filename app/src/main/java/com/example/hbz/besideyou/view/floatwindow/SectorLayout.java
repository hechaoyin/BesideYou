package com.example.hbz.besideyou.view.floatwindow;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.hbz.besideyou.BesideYouApp;
import com.example.hbz.besideyou.utils.DensityUtil;

/**
 * @ClassName: com.example.hbz.besideyou.view.floatwindow
 * @Description:
 * @Author: HBZ
 * @Date: 2018/5/18 15:23
 */
public class SectorLayout extends ViewGroup {
    private int viewDistance; // 周围View与中心View的距离
    private int viewSize; // 周围View与中心View的距离

    private Type type = Type.LEFT_SEMICIRCLE;

    private int halfSize;// view 高度的一半
    private int mHeight = 0; // 容器高度
    private int mWidth = 0; // 容器宽度

    Path pathCircle;
    PathMeasure pathMeasure;
    float[] position = new float[2];
    float[] tan = new float[2];

    public enum Type {
        LEFT_SEMICIRCLE, // 左半圆
        RIGHT_SEMICIRCLE, // 右半圆
        CIRCLE // 圆
    }

    public SectorLayout(Context context) {
        super(context);
        init();
    }

    public SectorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SectorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        viewSize = DensityUtil.dip2px(BesideYouApp.getAppContext(), 44f); // view 44dp
        viewDistance = DensityUtil.dip2px(BesideYouApp.getAppContext(), 66f); // view 44dp

        halfSize = viewSize / 2;

        pathCircle = new Path();// 子菜单项的分布路径
        pathMeasure = new PathMeasure();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (type == Type.CIRCLE) {
            mWidth = 4 * viewSize;
            mHeight = 4 * viewSize;
        } else {
            mHeight = 4 * viewSize;
            mWidth = viewSize + viewDistance;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }
        float circleX;
        float circleY;
        if (type == Type.RIGHT_SEMICIRCLE) {
            circleX = viewSize / 2;
            circleY = mHeight / 2;
        } else if (type == Type.LEFT_SEMICIRCLE) {
            circleX = mWidth - viewSize / 2;
            circleY = mHeight / 2;
        } else {
            circleX = mWidth / 2;
            circleY = mHeight / 2;
        }

        // 布局中间返回的控件
        layoutChildView(getChildAt(0), (int) circleX, (int) circleY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 安卓版本高于 21
            layoutMenuAbove21(childCount, circleX, circleY);
        } else {
            // 安卓版本低于 21
            layoutMenuBelow21(childCount, circleX, circleY);
        }
    }

    private void layoutMenuBelow21(float childCount, float circleX, float circleY) {
        pathCircle.reset();
        pathCircle.addCircle(circleX, circleY, viewDistance, Path.Direction.CW);

        float allLength;
        float beginLength;
        float viewLength;// 求出每个view的间隔长度

        pathMeasure.setPath(pathCircle, false);
        allLength = pathMeasure.getLength();

        if (type == Type.CIRCLE) {
            viewLength = allLength / (childCount - 1);
            beginLength = allLength * 3 / 4;
        } else {
            viewLength = allLength / 2 / childCount;
            if (type == Type.LEFT_SEMICIRCLE) {
                beginLength = allLength / 4;
            } else {
                beginLength = allLength * 3 / 4;
            }
        }

        for (int index = 1; index < childCount; index++) {
            float distance = beginLength + viewLength * index;
            while (distance > allLength) {
                distance -= allLength;
            }

            pathMeasure.getPosTan(distance, position, tan);//通过比getPosTan得到位置和偏移量
            layoutChildView(getChildAt(index), (int) position[0], (int) position[1]);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void layoutMenuAbove21(float childCount, float circleX, float circleY) {
        pathCircle.reset();
        if (type == Type.CIRCLE) {
            pathCircle.arcTo(circleX - viewDistance, circleY - viewDistance, circleX + viewDistance, circleY + viewDistance,
                    -90, 359, true);
        } else {
            if (type == Type.LEFT_SEMICIRCLE) {
                pathCircle.arcTo(circleX - viewDistance, circleY - viewDistance, circleX + viewDistance, circleY + viewDistance,
                        90, 180, true);
            } else {
                pathCircle.arcTo(circleX - viewDistance, circleY - viewDistance, circleX + viewDistance, circleY + viewDistance,
                        270, 180, true);
            }
        }

        float allLength;
        float viewLength;// 求出每个view的间隔长度
        pathMeasure.setPath(pathCircle, false);
        allLength = pathMeasure.getLength();
        if (type == Type.CIRCLE) {
            viewLength = allLength / (childCount - 1);
        } else {
            viewLength = allLength / childCount;
        }

        for (int index = 1; index < childCount; index++) {
            float distance = viewLength * index;
            pathMeasure.getPosTan(distance, position, tan);//通过比getPosTan得到位置和偏移量
            layoutChildView(getChildAt(index), (int) position[0], (int) position[1]);
        }
    }

    /**
     * 根据中心点位置布局
     *
     * @param view view
     * @param x    view中心点x坐标
     * @param y    view中心点y坐标
     */
    private void layoutChildView(View view, int x, int y) {
        view.layout(x - halfSize, y - halfSize, x + halfSize, y + halfSize);
    }

    public void setType(Type type) {
        this.type = type;
        requestLayout();
    }
}
