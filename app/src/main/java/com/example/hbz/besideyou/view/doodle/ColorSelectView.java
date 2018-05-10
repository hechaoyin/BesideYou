package com.example.hbz.besideyou.view.doodle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @ClassName: com.example.aatest
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/29 17:25
 */

public class ColorSelectView extends View {

    private Paint mPaint = new Paint();
    private int bgColor = Color.BLACK;
    private int height;
    private int width;
    private boolean isSelect = false;

    {
        mPaint.setAntiAlias(true);// 抗锯齿
    }

    public ColorSelectView(Context context) {
        super(context);
    }

    public ColorSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() > 0) {
            height = getMeasuredHeight();
        }
        if (getMeasuredWidth() > 0) {
            width = getMeasuredWidth();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isSelect) {
            canvas.scale(0.8f, 0.8f, width / 2, height / 2);

        }

        // 内圆背景。
        mPaint.setColor(bgColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(width / 2, height / 2, width / 2 * 0.9f, mPaint);

        // 外宽大小。
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(height * 0.1f);
        canvas.drawCircle(width / 2, height / 2, width / 2 * 0.9f - 2, mPaint);
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
        invalidate();
    }

    public void setWeight(int weight) {
        height = width = weight;
    }
}
