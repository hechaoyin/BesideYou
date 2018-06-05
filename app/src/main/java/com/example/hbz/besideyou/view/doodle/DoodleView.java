package com.example.hbz.besideyou.view.doodle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.example.hbz.besideyou.utils.LogUtil;

/**
 * @ClassName: com.example.besideyou.view
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/14 12:55
 */

public class DoodleView extends View {
    private static final float MAX_SCALE = 20f;
    private static final float MIN_SCALE = 1f;

    private static final float MIN_OFFSET = 3; // 最小偏移量，超过这个值将做贝塞尔处理

    private DoodleGestureDetector mDoodleGestureDetector;
    private DoodleOnClickListener doodleOnClickListener;

    private Canvas bitmapCanvas;
    private Bitmap createBitmap;

    private Bitmap mBitmap; // 源图片
    private Rect bitmapRect; // 源图片的区域大小
    private Rect viewRect; // 图片相对控件的位置
    private Paint mPaint; // 画笔
    private Paint mBitmapPaint; // 图片的画笔
    private Path mPath; //当前的路径
    private FPointList mPoints = new FPointList();

    private float mScale = 1f; // 放大比例
    private float mTranslationX = 0; // X平移(原图)
    private float mTranslationY = 0; // Y平移(原图平移的量，图片放大并未放大)
    private long lastPathTime;

    private DoodleGestureListener gestureListener = new DoodleGestureListener() {
        @Override
        public void onSingleMove(float x, float y) {
            float x1 = src2Point(x, mTranslationX);
            float y1 = src2Point(y, mTranslationY);

            mPath.lineTo(x1, y1);
            mPoints.add(view2BitmapPointX(x1), view2BitmapPointY(y1));

            invalidate();
        }


        @Override
        public void onDoubleMove(float k, float cx, float cy, float dx, float dy) {

            float tX1 = src2Point(cx, mTranslationX); // (x1,y1) 对应的原图片的坐标X
            float tY1 = src2Point(cy, mTranslationY); // (x1,y1) 对应的原图片的坐标Y

            mScale *= k; // 放大系数
            mTranslationX = cx / mScale - tX1;// 放大后，相对原来的偏移量也放大
            mTranslationY = cy / mScale - tY1;

            mTranslationX += dx / mScale;// 实际图片的偏移量
            mTranslationY += dy / mScale;

            invalidate();
        }

        @Override
        public void onClick(float x, float y) {
            /*
            float x1 = src2Point(x, mTranslationX); // 对应的原图片的坐标X
            float y1 = src2Point(y, mTranslationY); // 对应的原图片的坐标Y
            mPath.moveTo(x1, y1);
            mPath.lineTo(x1 + 1, y1);
            invalidate();
            resetCanvas();
            */
            if (System.currentTimeMillis() - lastPathTime > 500) {
                if (doodleOnClickListener != null) {
                    doodleOnClickListener.onClick();
                }
            } else {
                mPoints.clear();
                float x1 = src2Point(x, mTranslationX); // 对应的原图片的坐标X
                float y1 = src2Point(y, mTranslationY); // 对应的原图片的坐标Y
                mPoints.add(view2BitmapPointX(x1), view2BitmapPointY(y1));
                mPoints.add(view2BitmapPointX(x1) + 1, view2BitmapPointY(y1));
                pointsToBitmap();

                lastPathTime = System.currentTimeMillis();
            }

        }

        @Override
        public void onSingleDown(float x, float y) {
            float x1 = src2Point(x, mTranslationX);
            float y1 = src2Point(y, mTranslationY);
            mPath.moveTo(x1, y1);

            mPoints.clear();
            mPoints.add(view2BitmapPointX(x1), view2BitmapPointY(y1));
        }

        @Override
        public void onDoubleDown(float x1, float y1, float x2, float y2) {

            LogUtil.d("onDoubleDown");
        }

        @Override
        public void onSingleUp(float x, float y) {
            pointsToBitmap();
            // 上一笔操作的时间
            lastPathTime = System.currentTimeMillis();
        }

        @Override
        public void onDoubleUp(float x1, float y1, float x2, float y2) {

            if (mScale > MAX_SCALE) {
                mScale = MAX_SCALE;
            } else if (mScale < MIN_SCALE) {
                mScale = MIN_SCALE;
                mTranslationX = 0;
                mTranslationY = 0;
            }
            if (viewRect.left * mScale + (mTranslationX * mScale) > 0) {
                mTranslationX = viewRect.left / mScale - viewRect.left;
            } else if (viewRect.right * mScale + (mTranslationX * mScale) < viewRect.right) {
                mTranslationX = viewRect.right / mScale - viewRect.right;
            }

            if (viewRect.top * mScale + (mTranslationY * mScale) > viewRect.top) {
                mTranslationY = viewRect.top / mScale - viewRect.top;
            } else if (viewRect.bottom * mScale + (mTranslationY * mScale) < viewRect.bottom) {
                mTranslationY = viewRect.bottom / mScale - viewRect.bottom;
            }

        }
    };

    private void pointsToBitmap() {
        // 将保存的点集合绘画到图片中
        mPath.reset();

        if (mPoints.size() <= 1) {
            return;
        }
        FPoint fPoint = mPoints.get(0);
        mPath.moveTo(fPoint.getX(), fPoint.getY());


        // 偏移量
        float dx;
        float dy;
        // 上一个绘画的点
        FPoint lastFPoint = fPoint;

        for (int i = 1; i < mPoints.size(); i++) {
            fPoint = mPoints.get(i);

            dx = Math.abs(fPoint.getX() - lastFPoint.getX());
            dy = Math.abs(fPoint.getY() - lastFPoint.getY());
            dy = Math.abs(fPoint.getY() - lastFPoint.getY());

            float cX = (fPoint.getX() + lastFPoint.getX()) / 2;
            float cY = (fPoint.getY() + lastFPoint.getY()) / 2;
            if (dx > MIN_OFFSET || dy > MIN_OFFSET) {
                // 贝塞尔曲线的控制点为起点和终点的中点
                mPath.quadTo(lastFPoint.getX(), lastFPoint.getY(), cX, cY);
            } else {
                mPath.lineTo(cX, cY);
            }
            lastFPoint = fPoint;
        }
        bitmapCanvas.drawPath(mPath, mBitmapPaint);
        mPath.reset();
        invalidate();
    }

    public DoodleView(Context context) {
        super(context);
        init();
    }

    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoodleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化View画笔
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);// 抗锯
        mPaint.setStrokeWidth(5);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//圆形线冒

        //初始化View 路径
        mPath = new Path();

        // 初始化手势侦听器
        mDoodleGestureDetector = new DoodleGestureDetector();
        mDoodleGestureDetector.setGestureListener(gestureListener);

    }

    public void setBitmap(Bitmap bitmap) {
        onDestroy();// 销毁之前的图片。
        if (bitmap == null) {
            // 创建和屏幕相同的图
            DisplayMetrics dm2 = getResources().getDisplayMetrics();//获取屏幕宽高
            bitmap = Bitmap.createBitmap(dm2.widthPixels, dm2.heightPixels, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap);
            canvas.drawColor(Color.WHITE);
        }
        bitmapCanvas = new Canvas();
        mBitmap = bitmap;
        createBitmap = Bitmap.createBitmap(bitmap);
        // 给画布设置抗锯齿标志
        //bitmapCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        bitmapCanvas.setBitmap(createBitmap);
//        bitmapCanvas.drawBitmap(mBitmap, 0, 0, null);

        mBitmapPaint = new Paint();
        mBitmapPaint.setStyle(Paint.Style.STROKE);
        mBitmapPaint.setAntiAlias(true);// 抗锯
        mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);//圆形线冒

        bitmapRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        initViewRect();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initViewRect();
    }

    private void initViewRect() {
        if (mBitmap == null) {
            return;
        }
        float viewW = getMeasuredWidth();
        float viewH = getMeasuredHeight();
        if (viewW <= 0 || viewH <= 0) {
            return;
        }

        float bitmapW = mBitmap.getWidth();
        float bitmapH = mBitmap.getHeight();
        if (bitmapW / bitmapH > viewW / viewH) {
            // 图片宽填满整个View，高有空以
            float bitmapHeight = bitmapH * viewW / bitmapW;
            float lastHeight = (viewH - bitmapHeight) / 2f;
            viewRect = new Rect(0, (int) lastHeight, (int) viewW, (int) (viewH - lastHeight));
            mBitmapPaint.setStrokeWidth(mPaint.getStrokeWidth() * bitmapRect.width() / viewRect.width());
        } else {
            // 图片高填满整个View，宽有空以
            float bitmapWidth = bitmapW * (1f * viewH / bitmapH);
            float lastWidth = (viewW - bitmapWidth) / 2f;
            viewRect = new Rect((int) lastWidth, 0, (int) (viewW - lastWidth), (int) viewH);
            mBitmapPaint.setStrokeWidth(mPaint.getStrokeWidth() * bitmapRect.width() / viewRect.width());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mTranslationX * mScale, mTranslationY * mScale);
        canvas.scale(mScale, mScale);
        if (bitmapRect != null && viewRect != null) {
            if (createBitmap != null) {
                canvas.drawBitmap(createBitmap, bitmapRect, viewRect, mPaint);
            }
        }
        canvas.drawPath(mPath, mPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDoodleGestureDetector.onTouchEvent(event);
    }


    public void setPaintColor(int color) {
        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setColor(color);
        mBitmapPaint.setColor(color);
    }

    public void setPaintStroke(float stroke) {
        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setStrokeWidth(stroke);
        mBitmapPaint.setStrokeWidth(stroke * bitmapRect.height() / viewRect.height());
    }

    public void setPaintColorAndStroke(int color, float stroke) {
        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setColor(color);
        mPaint.setStrokeWidth(stroke);
    }

    public Bitmap getCreateBitmap() {
        return createBitmap;
    }

    public void setDoodleOnClickListener(DoodleOnClickListener doodleOnClickListener) {
        this.doodleOnClickListener = doodleOnClickListener;
    }

    // 监听到的坐标转成View（放大、平移的view后）的坐标
    private float src2Point(float point, float translation) {
        return point / mScale - translation;
    }

    // View（放大、平移的view后）的X坐标转换成相对图片（mBitmap）的X坐标
    private float view2BitmapPointX(float point) {
        float scale = 1f * bitmapRect.width() / viewRect.width();
        return point * scale - viewRect.left * scale;
    }

    // View（放大、平移的view后）的Y坐标转换成相对图片（mBitmap）的Y坐标
    private float view2BitmapPointY(float point) {
        float scale = 1f * bitmapRect.height() / viewRect.height();
        return point * scale - viewRect.top * scale;
    }

    public void onDestroy() {
        if (createBitmap == null && mBitmap == null) {
            return;
        }

        if (createBitmap != null) {
            createBitmap.recycle();
        }
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        System.gc();
    }

    public void backOperation() {
        //TODO: 画布后退操作
    }

    public void advanceOperation() {
        //TODO: 画布前进操作
    }

    public void resetOperation() {
        //TODO: 画布重置操作
        bitmapCanvas.drawBitmap(mBitmap, 0, 0, mPaint);
        mPath.reset();
        invalidate();
    }

    public interface DoodleOnClickListener {
        /**
         * 涂鸦控件中的点击事件
         */
        void onClick();
    }
}
