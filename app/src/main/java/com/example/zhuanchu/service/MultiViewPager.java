package com.example.zhuanchu.service;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 这个使用的时候放置在里层，外层使用正常的ViewPager，
 * */
public class MultiViewPager extends ViewPager {
    float mDownPosX=0;
    float mDownPosY=0;
    public MultiViewPager(Context context) {
        this(context, null);
    }

    public MultiViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return super.onInterceptTouchEvent(ev);
    }


//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        final float x = ev.getX();
//        final float y = ev.getY();
//
//        final int action = ev.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                 mDownPosX = x;
//                 mDownPosY = y;
//
//                break;
//            case MotionEvent.ACTION_MOVE:
//                final float deltaX = Math.abs(x - mDownPosX);
//                final float deltaY = Math.abs(y - mDownPosY);
//                // 这里是够拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
//                if (deltaX > deltaY) {
//                    return true;
//                }
//        }
//
//        return super.onInterceptTouchEvent(ev);
//    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {



        return super.onTouchEvent(ev);
    }




    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this && v instanceof ViewPager) {   //判断当前的View是不是ViewPager
            int currentItem = ((ViewPager) v).getCurrentItem();   //当前的条目
            int countItem = ((ViewPager) v).getAdapter().getCount();  //总的条目
            if ((currentItem == (countItem - 1) && dx < 0) || (currentItem == 0 && dx > 0)) {   //判断当前条目以及滑动方向
                return false;
            }
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


}


