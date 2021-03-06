package com.example.vke.shop4stech.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.helper.PreferencesHelper;

import java.util.zip.Inflater;

/*
* 任务列表分：1.未开始的任务 2.正在进行中的任务 3.已完成的任务
* */
public abstract class BaseTaskActivity extends BaseSwipeBackActivity {

    private static final String mTag = "BaseTaskActivity";

    private RelativeLayout mTaskPartRelativeLayout[] = new RelativeLayout[4];
    private ImageView  mCutLine3;

    private RelativeLayout mBaseContainerRelativeLayout;
    private ProgressBar mProgressBar;
    private Button mBackButton;
    protected String mAccessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_task);
        Toolbar toolbar = (Toolbar)this.findViewById(R.id.tech_task_tool_bar);
        this.setSupportActionBar(toolbar);
        initBaseContentView();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAccessToken = PreferencesHelper.getPreferenceAccessToken(BaseTaskActivity.this);
                if(mAccessToken == null){
                    Log.e(mTag,"get AccessToken error");
                }
            }
        }).start();
    }

    private void initBaseContentView(){
        mBackButton = (Button)findViewById(R.id.tech_task_step_back_button);
        mTaskPartRelativeLayout[0] = (RelativeLayout)findViewById(R.id.tech_task_part_1_relative_layout);
        mTaskPartRelativeLayout[1] = (RelativeLayout)findViewById(R.id.tech_task_part_2_relative_layout);
        mTaskPartRelativeLayout[2] = (RelativeLayout)findViewById(R.id.tech_task_part_3_relative_layout);
        mTaskPartRelativeLayout[3] = (RelativeLayout)findViewById(R.id.tech_task_part_4_relative_layout);

        mCutLine3 = (ImageView)findViewById(R.id.tech_cut_off_line_3);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackEvent();
            }
        });

        mBaseContainerRelativeLayout = (RelativeLayout)findViewById(R.id.tech_base_task_container_relative);
        mProgressBar = (ProgressBar)findViewById(R.id.tech_base_task_process_bar);
    }


    protected void setContentViewVisibility(boolean flag){
        if(flag){
            AnimationSet animationSet = new AnimationSet(true);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(600);
            animationSet.addAnimation(alphaAnimation);

            mProgressBar.setVisibility(View.GONE);
            mBaseContainerRelativeLayout.setVisibility(View.VISIBLE);
            //mBaseContainerRelativeLayout.setAnimation(animationSet);

        }
        else {
            mProgressBar.setVisibility(View.VISIBLE);
            mBaseContainerRelativeLayout.setVisibility(View.INVISIBLE);
        }
    }


    protected void initContentView(int layoutResID[]){
        for(int i = 0; i< layoutResID.length; i++){
            if(layoutResID[i] != -1){
                Log.i(mTag,"init view:" + i +"resId:" + layoutResID[i]);

                LayoutInflater.from(this).inflate(layoutResID[i], mTaskPartRelativeLayout[i], true);
            }
            else {
                mCutLine3.setVisibility(View.GONE);
                mTaskPartRelativeLayout[i].setVisibility(View.GONE);
            }
        }
    }
    public abstract void onBackEvent();
}
