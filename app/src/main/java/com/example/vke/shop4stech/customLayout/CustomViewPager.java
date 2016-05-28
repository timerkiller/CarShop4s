package com.example.vke.shop4stech.customLayout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by vke on 2016/5/28.
 */
public class CustomViewPager extends ViewPager{
    private boolean mNoScroll = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void scrollTo(int x, int y) {
        if (!mNoScroll) {
            super.scrollTo(x, y);
        }
    }

    /*设置是否禁止viewpage滑屏*/
    public void setNoScroll(boolean noScroll) {
        this.mNoScroll = noScroll;
    }
}
