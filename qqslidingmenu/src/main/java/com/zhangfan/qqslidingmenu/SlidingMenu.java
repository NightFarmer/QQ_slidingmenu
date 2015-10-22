package com.zhangfan.qqslidingmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;

import com.nineoldandroids.view.ViewHelper;

import java.lang.reflect.Field;

/**
 * Created by zhangfan on 2015/10/21. 布局父容器
 */
public class SlidingMenu extends HorizontalScrollView {

    private ViewGroup mMenu;
    private ViewGroup mContent;

    private OverScroller mScroller;

    private int mScreenWidth;
    protected int mMenuWidth;

    private int mMenuRightPadding;

    private boolean once = false;
    protected boolean isOpen = false;
    private View shade;

    /**
     * 使用自定义属性时，调用
     */
    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            final int attr = a.getIndex(i);
            if (attr == R.styleable.SlidingMenu_rightPadding) {
                int defaultValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
                mMenuRightPadding = a.getDimensionPixelSize(attr, defaultValue);
            }
        }
        a.recycle();
        //把dp转化为px
//        mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());

        final Field fScroller;
        try {
            fScroller = HorizontalScrollView.class.getDeclaredField("mScroller");
            fScroller.setAccessible(true);
            mScroller = (OverScroller) fScroller.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 布局文件中如果不使用自定义控件属性，则会调用此两个参数的构造方法
     */
    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 代码中创建时调用
     */
    public SlidingMenu(Context context) {
        this(context, null);
    }

    /**
     * 设置子view的宽和高，设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            ViewGroup mWrapper = (ViewGroup) getChildAt(0);
            mMenu = (ViewGroup) mWrapper.getChildAt(0);
            shade = mWrapper.getChildAt(1);
            mContent = (ViewGroup) mWrapper.getChildAt(2);

            mMenuWidth = mScreenWidth - mMenuRightPadding;
            mMenu.getLayoutParams().width = mMenuWidth;
            mContent.getLayoutParams().width = mScreenWidth;
            shade.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 通过设置偏移量，将menu隐藏
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {//这个scroll的位置被改变或者大小改变的时候才会调用，这里用做初始化时
            this.scrollTo(mMenuWidth, 0);//滑动到主界面 瞬间无动画
        }
    }

    float touchDownX;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:

                //处理点击松开
                if (isOpen && ev.getX() > mMenuWidth && ev.getX() == touchDownX) {
                    smoothScrollTo(mMenuWidth);
                    isOpen = false;
                    return true;
                }

                //处理抛掷手势，停顿小于100毫秒则识别为抛掷
                if (System.currentTimeMillis() - scrollTime < 100) {
                    if (direction > 0) {
                        //open menu
                        smoothScrollTo(mMenuWidth);
                        isOpen = false;
                    } else {
                        //close menu
                        smoothScrollTo(0);
                        isOpen = true;
                    }
                    return true;
                }

                //处理滑动松开
                int scrollX = getScrollX();
                if (scrollX >= mMenuWidth / 2) {
                    //open menu
                    smoothScrollTo(mMenuWidth);
                    isOpen = false;
                } else {
                    //close menu
                    smoothScrollTo(0);
                    isOpen = true;
                }
                return true;
            case MotionEvent.ACTION_DOWN:
                touchDownX = ev.getX();
                break;
        }


        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openMenu() {
        if (isOpen) return;
        smoothScrollTo(0);
        isOpen = true;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (!isOpen) return;
        smoothScrollTo(mMenuWidth);
        isOpen = false;
    }

    /**
     * 切换菜单
     */
    public void toggle() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    /**
     * 方向，负数向左, 正数向右，0静止
     */
    private int direction;
    private long scrollTime = System.currentTimeMillis();

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        direction = l - oldl;
        scrollTime = System.currentTimeMillis();
        ViewHelper.setTranslationX(shade, l);//设置view相对于原始位置的偏移量，实现位置锁定

        float scale = l * 1f / mMenuWidth;
        /**
         * scale 1~0
         *
         * 内容区域缩放 1~0.8
         * 0.8+ 0.2*scale
         *
         * 菜单缩放 0.7~1
         * 1-0.3*scale
         * 菜单透明0.6~1
         * 1-0.5*scale
         *
         */
        float contentScale = 0.8f + 0.2f * scale;
        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleX(mContent, contentScale);
        ViewHelper.setScaleY(mContent, contentScale);

        float menuScale = 1 - 0.3f * scale;
        ViewHelper.setScaleX(mMenu, menuScale);
        ViewHelper.setScaleY(mMenu, menuScale);

//        float menuAlpha = 1 - 0.5f * scale;
//        ViewHelper.setAlpha(mMenu, menuAlpha);

        ViewHelper.setAlpha(shade, scale);

        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);
        if (onHorizontalScrollListener != null) {
            onHorizontalScrollListener.onScroll(scale);
        }
    }


    private OnHorizontalScrollListener onHorizontalScrollListener;

    public void setOnHorizontalScrollListener(OnHorizontalScrollListener onHorizontalScrollListener) {
        this.onHorizontalScrollListener = onHorizontalScrollListener;
    }

    public interface OnHorizontalScrollListener {
        void onScroll(float scale);
    }

    public final void smoothScrollTo(int x) {
        try {
            int mScrollX = getScrollX();
            smoothScrollBy(x - mScrollX);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void smoothScrollBy(int dx) throws Exception {
        if (dx == 0) return;
        final Field fLastScroll = HorizontalScrollView.class.getDeclaredField("mLastScroll");
        fLastScroll.setAccessible(true);
        long mLastScroll;
        int mPaddingRight = getPaddingRight();
        int mPaddingLeft = getPaddingLeft();
        if (getChildCount() == 0) {
            return;
        }
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        final int width = getWidth() - mPaddingRight - mPaddingLeft;
        final int right = getChildAt(0).getWidth();
        final int maxX = Math.max(0, right - width);
        final int scrollX = getScrollX();
        dx = Math.max(0, Math.min(scrollX + dx, maxX)) - scrollX;

        mScroller.startScroll(scrollX, getScrollX(), dx, 0, 450);
        invalidate();
        mLastScroll = AnimationUtils.currentAnimationTimeMillis();
        fLastScroll.setLong(this, mLastScroll);
    }
}
