package com.example.vke.shop4stech.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.vke.shop4stech.R;

import java.util.zip.Inflater;

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
        Toolbar toolbar = (Toolbar)this.findViewById(R.id.tech_task_tool_bar);
        this.setSupportActionBar(toolbar);

        initToolBar();
    }

    private void initToolBar(){
        mBackButton = (Button)findViewById(R.id.tech_task_step_back_button);
        mMainContentRelativeLayout = (RelativeLayout)findViewById(R.id.tech_task_part_1_relative_layout);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackEvent();
            }
        });
    }

    protected void initContentView(int layoutResID){
        LayoutInflater.from(this).inflate(layoutResID, mMainContentRelativeLayout, true);
    }

    public abstract void onBackEvent();
}
