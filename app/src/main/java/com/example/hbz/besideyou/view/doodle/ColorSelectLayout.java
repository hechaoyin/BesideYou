package com.example.hbz.besideyou.view.doodle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @ClassName: com.example.aatest
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/30 15:40
 */

public class ColorSelectLayout extends ViewGroup implements View.OnClickListener {

    //    private List<Integer> colorList;
    private ColorSelectView lastSelect = null;

    private int spanCount = 6;
    private int childWeight;


    public ColorSelectLayout(Context context) {
        super(context);
    }

    public ColorSelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorSelectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        childWeight = width / spanCount;

        if (getChildCount() > 0) {
            setMeasuredDimension(width, (getChildCount() + 1) / spanCount * childWeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            view.layout(
                    i % spanCount * childWeight,
                    i / spanCount * childWeight,
                    i % spanCount * childWeight + childWeight,
                    i / spanCount * childWeight + childWeight);

            if (view instanceof ColorSelectView) {
                ((ColorSelectView) view).setWeight(childWeight);
            }
        }
    }

    public void setColorList(List<Integer> colorList) {
        removeAllViews();
        if (colorList == null) {
            return;
        }
        for (int i = 0; i < colorList.size(); i++) {
            ColorSelectView view = new ColorSelectView(getContext());
            view.setBgColor(colorList.get(i));
            view.setOnClickListener(this);
            addView(view);
        }
        requestLayout();

    }

    @Override
    public void onClick(View v) {
        if (v instanceof ColorSelectView) {
            if (lastSelect != null && lastSelect != v) {
                lastSelect.setSelect(false);
            }
            ColorSelectView v1 = (ColorSelectView) v;
            v1.setSelect(!v1.isSelect());
            lastSelect = v1;
        }
    }

    public ColorSelectView getLastSelectView() {
        return lastSelect;
    }

    public int getSelectColor() {
        if (lastSelect != null) {
            return lastSelect.getBgColor();
        }
        return Integer.MIN_VALUE;
    }
}
