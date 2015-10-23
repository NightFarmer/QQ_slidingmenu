package com.zhangfan.qqslidingmenu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 用于分发在slidingmenu主界面正确分发事件的viewpager
 * Created by zhangfan on 2015/10/23.
 */
public class SlidingContentViewPager extends ViewPager {

    public SlidingContentViewPager(Context context) {
        super(context);
    }

    public SlidingContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private SlidingMenu parent;

    public void setParent(SlidingMenu parent) {
        this.parent = parent;
        parent.scrollJudger = new SlidingMenu.ScrollJudger() {
            @Override
            public boolean couldScroll(MotionEvent ev) {
                return state==SCROLL_STATE_IDLE&&!pressed;
            }
        };
        setOverScrollMode(OVER_SCROLL_NEVER);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("xx3",""+state);
                SlidingContentViewPager.this.state = state;
            }
        });
    }

    private int state;

    /**
     * 锁定父容器的事件分发，不被拦截
     */
    private boolean locked;
    private boolean pressed;

    float preX;
    float preY;

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressed = true;
                preX = ev.getX();
                preY = ev.getY();
                Log.i("xx3","down");
                if (state!=SCROLL_STATE_IDLE){
                    Log.i("xx3","down——#");
                    parent.requestDisallowInterceptTouchEvent(true);
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("xx3","move");
                float dx = ev.getX() - preX;
                float dy = ev.getY() - preY;
                preX = ev.getX();
                preY = ev.getY();
                if (!locked && Math.abs(dy) <= Math.abs(dx) && dx > 0 && getCurrentItem() == 0 && state==SCROLL_STATE_IDLE) {
                    //
                    Log.i("xx3","move——#1");

                    parent.requestDisallowInterceptTouchEvent(false);
                    return false;
                } else {
                    Log.i("xx3","move——#2");
                    parent.requestDisallowInterceptTouchEvent(true);
                    locked = true;
                    break;
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.i("xx3","cu");
                locked = false;
                pressed = false;
        }
        return super.dispatchTouchEvent(ev);
    }



}
