package com.example.hbz.besideyou.view.recycleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.example.hbz.besideyou.R;

/**
 * @ClassName: com.example.hbz.besideyou.view.recycleview
 * @Description:
 * @Author: HBZ
 * @Date: 2018/5/7 10:41
 */

public class SectionDecoration extends RecyclerView.ItemDecoration {
    private GroupInfoCallback mCallback;
    private int mHeaderHeight; // 头的高度
    private int mDividerHeight; // 分割线的高度

    //用来绘制Header上的文字
    private TextPaint mTextPaint; // 绘制文本Title的画笔
    private Paint mPaint; // 绘制头矩形的画笔
    private float mTextSize;
    private Paint.FontMetrics mFontMetrics;
    private float mTextOffsetX;

    public SectionDecoration(Context context, GroupInfoCallback callback) {
        this.mCallback = callback;
        mDividerHeight = context.getResources().getDimensionPixelOffset(R.dimen.header_divider_height);
        mHeaderHeight = context.getResources().getDimensionPixelOffset(R.dimen.header_height);
        mTextSize = context.getResources().getDimensionPixelOffset(R.dimen.header_text_size);
        mTextOffsetX = context.getResources().getDimensionPixelOffset(R.dimen.header_text_offset_x);

        mHeaderHeight = (int) Math.max(mHeaderHeight,mTextSize);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(ContextCompat.getColor(context,R.color.common_font_gray));
        mTextPaint.setTextSize(mTextSize);
        mFontMetrics = mTextPaint.getFontMetrics();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ContextCompat.getColor(context,R.color.common_bg_gray));

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        if (mCallback != null) {
            GroupInfo groupInfo = mCallback.getGroupInfo(position);

            //如果是组内的第一个则将间距撑开为一个Header的高度，或者就是普通的分割线高度
            if (groupInfo != null && groupInfo.isFirstViewInGroup()) {
                outRect.top = mHeaderHeight;
            } else {
                outRect.top = mDividerHeight;
            }
        }
    }
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int childCount = parent.getChildCount();
        for ( int i = 0; i < childCount; i++ ) {
            View view = parent.getChildAt(i);
            int index = parent.getChildAdapterPosition(view);
            if ( mCallback != null ) {
                GroupInfo groupinfo = mCallback.getGroupInfo(index);
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                //屏幕上第一个可见的 ItemView 时，i == 0;
                if ( i != 0 ) {
                    //只有组内的第一个ItemView之上才绘制
                    if ( groupinfo.isFirstViewInGroup() ) {
                        int top = view.getTop() - mHeaderHeight;
                        int bottom = view.getTop();
                        drawHeaderRect(c, groupinfo, left, top, right, bottom);
                    }
                } else {
                    //当 ItemView 是屏幕上第一个可见的View 时，不管它是不是组内第一个View
                    //它都需要绘制它对应的 StickyHeader。
                    int top = parent.getPaddingTop();
                    int bottom = top + mHeaderHeight;
                    drawHeaderRect(c, groupinfo, left, top, right, bottom);
                }
            }
        }
    }

    private void drawHeaderRect(Canvas c, GroupInfo groupinfo, int left, int top, int right, int bottom) {
        //绘制Header
        c.drawRect(left,top,right,bottom,mPaint);
        float titleX =  left + mTextOffsetX;
        float titleY =  bottom - mFontMetrics.descent;
        //绘制Title
        c.drawText(groupinfo.getTitle(),titleX,titleY,mTextPaint);
    }


    /**
     * 获取每个 ItemView 对应的分组信息。
     */
    public interface GroupInfoCallback {
        GroupInfo getGroupInfo(int position);
    }
}
