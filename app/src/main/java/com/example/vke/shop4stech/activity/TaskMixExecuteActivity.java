package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vke.shop4stech.R;

public class TaskMixExecuteActivity extends BaseTaskActivity {

    private static final String mTag = "TaskMixExecuteActivity";
    private static final String KEY_ACTIVITY_TYPE = "TaskMixExecuteActivity.Type";
    private static final String KEY_INDEX = "TaskMixExecuteActivity.Index";
    private static final String KEY_ORDER_SERIAL_NUM = "TaskMixExecuteActivity.OrderSerialNum";
    private int mActivityType;

    private MixExecuteWidgets mMixExecuteWidgets;

    /*
    * 正在执行中的任务，包括1.暂停 2.执行中 3.已完成进行查看具体步骤 4.已完成编辑具体步骤零件
    */
    public class MixExecuteWidgets{

        //part1
        TextView mCurrentStepTitle;
        TextView mCurrentStepContent;
        TextView mExecutingMan;
        Chronometer mTimeBox;

        //part2
        RelativeLayout mAddComponentRelativeLayout;

        //part3
        Button mButtonPre,mButtonNext,mButtonMixFunction;

        //part_pause_reason
    }

    /*
    * 容纳完成界面的一些组件，包括1.已完成的页面 2.未开始的页面
    */
    public class MixDoneOrUnStartWidgets{

    }

    public class ActivityType{
        public static final int TYPE_EXECUTING = 0;//执行中
        public static final int TYPE_PAUSE = 1;//暂停状态
        public static final int TYPE_DONE_VIEW = 2;//完成供查看
        public static final int TYPE_DONE_EDITOR = 3;//完成编辑步骤
        public static final int TYPE_DONE = 4;//完成界面
        public static final int TYPE_UNSTART = 5;//未开始
        public static final int TYPE_ERROR = -1;
    }

    public static void start(Activity context,int type,String index,String orderSerialNum) {
        Intent starter = new Intent(context, TaskMixExecuteActivity.class);
        starter.putExtra(KEY_ACTIVITY_TYPE,type);
        starter.putExtra(KEY_INDEX,index);
        starter.putExtra(KEY_ORDER_SERIAL_NUM,orderSerialNum);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMixExecuteWidgets = new MixExecuteWidgets();

        mActivityType = getIntent().getIntExtra(KEY_ACTIVITY_TYPE,ActivityType.TYPE_ERROR);

        /*根据不同的订单的状态，设置不同的view*/
        switch (mActivityType){
            case ActivityType.TYPE_DONE:
                int layoutResIdForDone[] = {R.layout.task_part1_description,R.layout.task_part2_station_progress,R.layout.task_part3_component,R.layout.task_part4_operation};
                initContentView(layoutResIdForDone);
                break;

            case ActivityType.TYPE_UNSTART:
                int layoutResIdForUnStart[] = {R.layout.task_part1_description,R.layout.task_part2_station_progress,R.layout.task_part4_operation,-1};
                initContentView(layoutResIdForUnStart);
                break;

            case ActivityType.TYPE_DONE_EDITOR:
            case ActivityType.TYPE_DONE_VIEW:
            case ActivityType.TYPE_EXECUTING:
                int layoutResId[] = {R.layout.task_mix_part1,R.layout.task_mix_part2,R.layout.task_mix_part3,-1};
                initContentView(layoutResId);
                break;

            case ActivityType.TYPE_PAUSE:
                int layoutResIdForPause[] = {R.layout.task_mix_part1,R.layout.task_mix_part2,R.layout.task_mix_pause_reason,R.layout.task_mix_part3};
                initContentView(layoutResIdForPause);
                break;

            default:
                throw new UnsupportedOperationException("on Create View, But get error type ");
        }

        initView();
    }

    private void initView(){
        switch (mActivityType){
            case ActivityType.TYPE_DONE_EDITOR:

                break;
            case ActivityType.TYPE_DONE_VIEW:


                break;
            case ActivityType.TYPE_EXECUTING:
                if(mMixExecuteWidgets !=null){
                    mMixExecuteWidgets.mCurrentStepTitle = (TextView)this.findViewById(R.id.tech_task_part_1_title_1_text_view) ;
                    mMixExecuteWidgets.mCurrentStepContent = (TextView)this.findViewById(R.id.tech_task_part_1_title_2_text_view);
                    mMixExecuteWidgets.mExecutingMan = (TextView)this.findViewById(R.id.tech_task_part_1_title_3_text_view);
                    mMixExecuteWidgets.mTimeBox =(Chronometer)this.findViewById(R.id.tech_task_part_1_title_4_text_view);
                    mMixExecuteWidgets.mButtonPre = (Button)this.findViewById(R.id.tech_pre_task_button);
                    mMixExecuteWidgets.mButtonNext = (Button)this.findViewById(R.id.tech_next_task_button);
                    mMixExecuteWidgets.mButtonMixFunction = (Button)this.findViewById(R.id.tech_mix_func_button);
                    mMixExecuteWidgets.mTimeBox.setBackgroundResource(R.drawable.bg_timer_green);
                    //mMixExecuteWidgets.mTimeBox.setBase(1);
                    mMixExecuteWidgets.mAddComponentRelativeLayout =(RelativeLayout)this.findViewById(R.id.tech_task_mix_part2_container_relative_layout);
                    mMixExecuteWidgets.mTimeBox.setTextColor(getResources().getColor(R.color.colorGreen));
                    mMixExecuteWidgets.mTimeBox.start();
                    
                    if(mMixExecuteWidgets.mButtonPre != null){
                        Log.i(mTag,"get button not null");
                    }
                }

                break;
            case ActivityType.TYPE_PAUSE:

                break;
            case ActivityType.TYPE_DONE:
                break;
            case ActivityType.TYPE_UNSTART:
                break;
            default:
                throw new UnsupportedOperationException("on Create View, But get error type ");
        }
    }

    @Override
    public void onBackEvent() {
        finish();

    }
}
