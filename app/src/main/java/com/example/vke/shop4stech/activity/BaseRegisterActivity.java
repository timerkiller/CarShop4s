package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vke.shop4stech.R;

/**
 * Created by vke on 2016/5/18.
 */
public abstract class BaseRegisterActivity extends BaseSwipeBackActivity implements View.OnTouchListener{

    private static final String mTag = "BaseRegisterActivity";

    private int mDownX,mDownY;
    private Button mNextButton,mBackButton;
    private TextView mTitleTextView;
    private RelativeLayout mGlobalRelativeLayout;
    private RelativeLayout mMainRelativeLayout;;
    //用于计算手指滑动的速度。
    private VelocityTracker mVelocityTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_register);
        initToolBar();
    }

    public void  initToolBar(){

        Toolbar toolbar = (Toolbar)findViewById(R.id.tech_register_step_tool_bar);
        this.setSupportActionBar(toolbar);

        mBackButton = (Button)findViewById(R.id.tech_register_step_back_button);
        mNextButton = (Button)findViewById(R.id.tech_register_step_next_button);

        mTitleTextView = (TextView)findViewById(R.id.tech_register_title_text_view);

        mMainRelativeLayout = (RelativeLayout)findViewById(R.id.tech_main_content_relative_layout);
        mGlobalRelativeLayout = (RelativeLayout)findViewById(R.id.tech_base_register_relative_layout);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackPage();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNextPage();
            }
        });

        mGlobalRelativeLayout.setOnTouchListener(this);
    }

    public void setToolBarTitle(String title){
        mTitleTextView.setText(title);
    }

    public void initContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, mMainRelativeLayout, true);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        Log.d(mTag,"onTouch event");
        final int XSPEED_MIN = 200;
        final int Y_MIN=60;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = (int)event.getRawX();
                mDownY = (int)event.getRawY();
                Log.d(mTag,"DX: " + mDownX +" DY: " + mDownY);
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                int upX = (int)event.getRawX();
                int upY =(int)event.getRawY();
                int tmpX = upX - mDownX;
                int tmpY = Math.abs(upY - mDownY);
                int speed = getScrollVelocity();

                Log.d(mTag,">tmpX :" + tmpX + " tmpY:" +tmpY);
                if(tmpX < 0){
                    //手指滑动速度大于200 并且 Y轴上的偏移小于60
                    if (speed > XSPEED_MIN && tmpY < Y_MIN){
                        goNextPage();
                    }
                }
                else{
                    Log.i(mTag,"slide ----right");
                }

                recycleVelocityTracker();
                break;
        }
        return true;
    }

    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event montion event
     *
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 获取手指在content界面滑动的速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    public abstract void goNextPage();

    public abstract void goBackPage();
}
