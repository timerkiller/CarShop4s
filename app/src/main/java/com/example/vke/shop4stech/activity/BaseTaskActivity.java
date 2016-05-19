package com.example.vke.shop4stech.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.vke.shop4stech.R;
/*
* 任务列表分：1.未开始的任务 2.正在进行中的任务 3.已完成的任务
* */
public abstract class BaseTaskActivity extends BaseSwipeBackActivity {

    private RelativeLayout mMainContentRelativeLayout;
    private Button mBackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_task);
        initToolBar();
    }

    private void initToolBar(){
        mBackButton = (Button)findViewById(R.id.tech_task_step_back_button);
        mMainContentRelativeLayout = (RelativeLayout)findViewById(R.id.tech_task_main_content_relative_layout);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackEvent();
            }
        });
    }

    public abstract void onBackEvent();
}
