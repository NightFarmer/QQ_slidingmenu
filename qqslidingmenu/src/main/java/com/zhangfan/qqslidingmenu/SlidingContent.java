package com.zhangfan.qqslidingmenu;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 用来拦截菜单打开时内容界面的点击事件
 * Created by zhangfan on 2015/10/21.
 */
public class SlidingContent extends LinearLayout {
    private final GestureDetectorCompat gestureDetector;

    public SlidingContent(Context context) {
        this(context, null);
    }

    public SlidingContent(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetectorCompat(context,
                new YScrollDetector());
    }

    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx,
                                float dy) {
            return Math.abs(dy) <= Math.abs(dx);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final SlidingMenu parent = (SlidingMenu) getParent().getParent();
        parent.requestDisallowInterceptTouchEvent(!gestureDetector.onTouchEvent(ev));
        return parent.isOpen && ev.getRawX() > parent.mMenuWidth || super.onInterceptTouchEvent(ev);
    }

}
