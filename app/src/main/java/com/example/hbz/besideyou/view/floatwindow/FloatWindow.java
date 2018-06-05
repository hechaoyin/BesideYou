package com.example.hbz.besideyou.view.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hbz.besideyou.R;


public class FloatWindow {

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager.LayoutParams mToastLayoutParams;


    private DisplayMetrics mDisplayMetrics;
    private WindowManager mWindowManager;
    private Context mContext;

    private View mContentView; // 正在显示的View
    private View mFloatView; // 收起的view
    private View mPlayerView;  // 展开的view
    private View mPopWindowView;  // 展开的view

    private final float DISTANCE = 15.0f;  //  点击偏移量   在上、下、左、右这个范围之内都会触发点击事件
    private float offsetX, offsetY;

    private long lastTouchTimeMillis;

    private boolean mIsShowing;
    private boolean mPopIsShowing;
    private float downX, downY; // 按下手指的点
    private EventType eventType = EventType.NULL; // 当前产生的事件

    private ScreenPositionChange screenPositionChange;

    // 检查长按事件的 Runnable
    private Runnable longClickCheck = () -> {
        if (eventType == EventType.NULL && System.currentTimeMillis() - lastTouchTimeMillis > 1200) {
            eventType = EventType.LONG_CLICK;
            longClick();
            lastTouchTimeMillis = System.currentTimeMillis();
        }
    };

    enum EventType {
        NULL, // 无事件
        MOVE, // 移动事件
        CLICK, // 点击事件
        LONG_CLICK // 长按事件

    }

    /**
     * 带布局参数的构造方法
     */
    public FloatWindow(Context context, View playerView, View floatView) {
        this.mContext = context.getApplicationContext();
        this.mPlayerView = playerView;

        this.mPopWindowView = LayoutInflater.from(mContext).inflate(R.layout.float_menu_view_pop, null);

        if (floatView != null) {
            WindowTouchListener floatTouchListener = new WindowTouchListener();
            floatView.setOnTouchListener(floatTouchListener);
        }
        initWindowManager();
        setFloatView(floatView);
    }

    public void showPopwindow(String msg) {
        if (mPopWindowView == null) {
            return;
        }
        View viewById = mPopWindowView.findViewById(R.id.tv_menu_content);
        if (viewById != null && viewById instanceof TextView) {
            ((TextView) viewById).setText(msg != null ? msg : "");
        }
        if (!mPopIsShowing) {
            getWindowManager().addView(mPopWindowView, mToastLayoutParams);
            mPopIsShowing = true;
        }
    }

    public void dismissPopwindow() {
        try {
            if (mPopIsShowing) {
                getWindowManager().removeView(mPopWindowView);
                mPopIsShowing = false;
            }
        } catch (Exception ex) {
            Log.e("---", ex.getMessage());
        }
    }

    /**
     * 显示窗口
     */
    public void show() {
        try {
            if (getContentView() != null && !isShowing()) {
                getWindowManager().addView(getContentView(), getLayoutParams());
                mIsShowing = true;
            }
        } catch (Exception ex) {
            Log.e("---", ex.getMessage());
        }
    }

    public void changeShowType() {
        if (mContentView == mFloatView) {// 展开
            if (mPlayerView != null) {
                setContentView(mPlayerView);
            }
        } else {// 收起
            if (mFloatView != null) {
                setContentView(mFloatView);
            }
        }

    }

    /**
     * 隐藏当前显示窗口
     */
    public void dismiss() {
        try {
            if (getContentView() != null && isShowing()) {
                getWindowManager().removeView(getContentView());
                mIsShowing = false;
            }
        } catch (Exception ex) {
            Log.e("---", ex.getMessage());
        }
    }

    /**
     * 判断当前是否有显示窗口
     */
    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * 设置关闭状态的布局视图
     */
    private void setFloatView(View floatView) {
        if (floatView != null) {
            this.mFloatView = floatView;
            setContentView(mFloatView);
        }
    }

    /**
     * 设置窗口当前布局
     */
    private void setContentView(View contentView) {
        if (contentView == this.mContentView) {
            return;
        }
        if (contentView != null) {
            contentView.removeCallbacks(longClickCheck);
            if (isShowing()) {
                float x = offsetX, y = offsetY;// 上一个视图偏移量

                getWindowManager().removeView(mContentView);
                createContentView(contentView);
                getWindowManager().addView(mContentView, getLayoutParams());
                updateLocation(getLayoutParams().x + x, getLayoutParams().y + y, true);
            } else {
                createContentView(contentView);
            }
        }
    }

    /**
     * 配置布局View， 需要在此处获得View的宽、高，并由此获得偏移量
     */
    private void createContentView(View contentView) {
        this.mContentView = contentView;
        mContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED); // 主动计算视图View的宽高信息
        offsetY = getStatusBarHeight(getContext()) + mContentView.getMeasuredHeight() / 2;
        offsetX = mContentView.getMeasuredWidth() / 2;

    }

    /**
     * 获得上下文信息
     */
    private Context getContext() {
        return this.mContext;
    }

    private WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 获得WindowManager.LayoutParams参数
     */
    private WindowManager.LayoutParams getLayoutParams() {
        if (mLayoutParams == null) {
            int w = WindowManager.LayoutParams.WRAP_CONTENT;
            int h = WindowManager.LayoutParams.WRAP_CONTENT;
            int flags = 0;
            int type;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//6.0+
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            mLayoutParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);
            mToastLayoutParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);

            mLayoutParams.flags = mToastLayoutParams.flags = getLayoutParams().flags
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mLayoutParams.dimAmount = mToastLayoutParams.dimAmount = 0.2f;
            mLayoutParams.format = mToastLayoutParams.format = PixelFormat.RGBA_8888;
            mLayoutParams.alpha = mToastLayoutParams.alpha = 1.0f;  //  设置整个窗口的透明度
            mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            mToastLayoutParams.gravity = Gravity.CENTER;
            offsetX = 0;
            offsetY = getStatusBarHeight(getContext());
            mLayoutParams.x = (int) (mDisplayMetrics.widthPixels - offsetX);
            mLayoutParams.y = (int) (mDisplayMetrics.heightPixels / 4 - offsetY);
            mToastLayoutParams.y = -mDisplayMetrics.heightPixels / 6;
        }
        return mLayoutParams;
    }

    /**
     * 获得显示信息
     */
    private DisplayMetrics getDisplayMetrics() {
        if (mDisplayMetrics == null) {
            mDisplayMetrics = getContext().getResources().getDisplayMetrics();
        }
        return mDisplayMetrics;
    }

    /**
     * 获得当前视图
     */
    private View getContentView() {
        return mContentView;
    }

    /**
     * 初始化窗口管理器
     */
    private void initWindowManager() {
        mWindowManager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mDisplayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    /**
     * 获取状态栏的高度
     */
    private int getStatusBarHeight(Context context) {
        int height = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            height = context.getResources().getDimensionPixelSize(resId);
        }
        return height;
    }

    /**
     * 更新窗口的位置
     */
    private void updateLocation(float x, float y, boolean offset) {
        if (getContentView() != null) {
            if (offset) {
                getLayoutParams().x = (int) (x - offsetX);
                getLayoutParams().y = (int) (y - offsetY);
            } else {
                getLayoutParams().x = (int) x;
                getLayoutParams().y = (int) y;
            }
            getWindowManager().updateViewLayout(mContentView, getLayoutParams());
        }
    }


    /**
     * 自动对齐的一个小动画（自定义属性动画），使自动贴边的时候显得不那么生硬
     */
    private ValueAnimator alignAnimator(float x, float y) {
        ValueAnimator animator;
        if (x <= getDisplayMetrics().widthPixels / 2) {
            animator = ValueAnimator.ofObject(new PointEvaluator(), new Point((int) x, (int) y), new Point(0, (int) y));
        } else {
            animator = ValueAnimator.ofObject(new PointEvaluator(), new Point((int) x, (int) y), new Point(getDisplayMetrics().widthPixels, (int) y));
        }
        animator.addUpdateListener(animation -> {
            Point point = (Point) animation.getAnimatedValue();
            updateLocation(point.x, point.y, true);
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lastTouchTimeMillis = System.currentTimeMillis();
            }
        });
        animator.setDuration(160);
        return animator;
    }

    public class PointEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object from, Object to) {
            Point startPoint = (Point) from;
            Point endPoint = (Point) to;
            float x = startPoint.x + fraction * (endPoint.x - startPoint.x);
            float y = startPoint.y + fraction * (endPoint.y - startPoint.y);
            Point point = new Point((int) x, (int) y);
            return point;
        }
    }

    class WindowTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    down(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    move(event);
                    break;
                case MotionEvent.ACTION_UP:
                    up(event);
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    return true;
                default:
                    break;
            }
            return false;
        }
    }

    /**
     * 按下事件处理
     */
    private void down(MotionEvent event) {
        eventType = EventType.NULL;
        downX = event.getRawX();
        downY = event.getRawY();
        getLayoutParams().alpha = 1.0f;
        lastTouchTimeMillis = System.currentTimeMillis();
        getWindowManager().updateViewLayout(getContentView(), getLayoutParams());

        if (mContentView != null) {
            mContentView.removeCallbacks(longClickCheck);
            mContentView.postDelayed(longClickCheck, 1200);
        }
    }

    /**
     * 移动事件处理
     */
    private void move(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (eventType == EventType.MOVE) {
            lastTouchTimeMillis = System.currentTimeMillis();
            updateLocation(x, y, true);
        } else if (eventType == EventType.NULL) {
            if ((Math.abs(x - downX) > DISTANCE) || (Math.abs(y - downY) > DISTANCE)) {
                eventType = EventType.MOVE;
            }
        }
    }

    /**
     * 抬起事件处理
     */
    private void up(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (eventType == EventType.NULL) {
            if (System.currentTimeMillis() - lastTouchTimeMillis < 1200) {
                eventType = EventType.CLICK;
                onClick();
            }
        } else if (eventType == EventType.MOVE) {
            if (screenPositionChange != null) {
                // 屏幕位置更换
                if (x <= getDisplayMetrics().widthPixels / 2) {
                    // 控件移动到左边
                    screenPositionChange.onChange(true);
                } else {
                    // 控件移动到右边
                    screenPositionChange.onChange(false);
                }
            }
            ValueAnimator animator = alignAnimator(x, y);
            animator.start();
        }
    }

    public void setScreenPositionChange(ScreenPositionChange screenPositionChange) {
        this.screenPositionChange = screenPositionChange;
    }

    public interface ScreenPositionChange {
        void onChange(boolean isLeft);
    }

    private void onClick() {
        //  TODO 点击事件
        changeShowType();
    }

    private void longClick() {
        // TODO 长按事件
        Toast.makeText(getContext(), "长按事件", Toast.LENGTH_SHORT).show();

    }
}
