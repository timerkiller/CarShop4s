package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.helper.NetOperationHelper;

import java.util.HashMap;

public class TaskMixExecuteActivity extends BaseTaskActivity implements View.OnClickListener{

    private static final String mTag = "TaskMixExecuteActivity";
    private static final String KEY_ACTIVITY_TYPE = "TaskMixExecuteActivity.Type";
    private static final String KEY_INDEX = "TaskMixExecuteActivity.Index";
    private static final String KEY_ORDER_SERIAL_NUM = "TaskMixExecuteActivity.OrderSerialNum";
    private int mActivityType;

    private MixExecuteWidgets mMixExecuteWidgets;
    private MixDoneOrUnStartWidgets mMixDoneOrUnStartWidgets;

    private Handler mHandler;
    private String mIndex,mOrderSerialNum;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tech_pre_task_button:
                break;
            case R.id.tech_next_task_button:
                break;
            case R.id.tech_mix_func_button:
                if(mActivityType == ActivityType.TYPE_PAUSE){
                    resumeTask();
                }
                else if(mActivityType == ActivityType.TYPE_EXECUTING){
                    pauseTask();
                }
                else{
                    Log.e(mTag,"onClick tech_task_part4_operation_button --> unsupported click");
                }

                break;
            case R.id.tech_task_part4_operation_button:
                if(mActivityType == ActivityType.TYPE_DONE){
                    reEditTask();
                }
                else if(mActivityType == ActivityType.TYPE_UNSTART){
                    startTask();
                }
                else{
                    Log.e(mTag,"onClick tech_task_part4_operation_button --> unsupported click");
                }
                break;
            case R.id.tech_task_mix_part2_container_relative_layout://add component
                break;
        }
    }

    /*
    * 正在执行中的任务，包括1.暂停 2.执行中 3.已完成进行查看具体步骤 4.已完成编辑具体步骤零件
    */
    public class MixExecuteWidgets{

        //part1
        TextView mCurrentStepTitle;
        TextView mCurrentStepContent;
        TextView mExecutingMan;
        Chronometer mTimeBox;
        ImageView mDoneImageView;
        //part2
        ListView mComponentListView;
        RelativeLayout mAddComponentRelativeLayout;

        //part3
        Button mButtonPre,mButtonNext,mButtonMixFunction;

        //part_pause_reason
        TextView mPauseReason;
        TextView mPauseTime;
    }

    /*
    * 容纳完成界面的一些组件，包括1.已完成的页面 2.未开始的页面
    */
    public class MixDoneOrUnStartWidgets{
        //task_part1_description
        TextView mOrderSerialNum;
        TextView mStationJob;
        TextView mStation;
        TextView mRelativePerson;
        Chronometer mTimeBox;
        ImageView mDoneImageView;

        //task_part2_station_progress
        ListView mStepListView;

        //task_part3_component
        ListView mComponentListView;

        //task_part4_operation
        Button mOperationButton;
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

        mIndex = getIntent().getStringExtra(KEY_INDEX);
        mOrderSerialNum = getIntent().getStringExtra(KEY_ORDER_SERIAL_NUM);
        mActivityType = getIntent().getIntExtra(KEY_ACTIVITY_TYPE,ActivityType.TYPE_ERROR);
        if(mIndex == null || mOrderSerialNum == null || mActivityType == ActivityType.TYPE_ERROR){
            throw new UnsupportedOperationException("mIndex or mOrderSerialNum or mActivityType should not be null");
        }

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
        initHandler();
        startThreadToGetOrderData();
    }

    private void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
            }
        };
    }

    private void startThreadToGetOrderData(){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    private void initView(){
        switch (mActivityType){
            case ActivityType.TYPE_DONE_EDITOR:
                initDoneEditorView();
                break;
            case ActivityType.TYPE_DONE_VIEW:
                initDoneCheckView();
                break;
            case ActivityType.TYPE_EXECUTING:
                initExecutingView();
                break;
            case ActivityType.TYPE_PAUSE:
                initPauseView();
                break;
            case ActivityType.TYPE_DONE:
                initDoneView();
                break;
            case ActivityType.TYPE_UNSTART:
                initUnStartView();
                break;
            default:
                throw new UnsupportedOperationException("on Create View, But get error type ");
        }
    }

    private void initExecutingView(){
        if(mMixExecuteWidgets !=null){
            initTaskMixCommonView(mMixExecuteWidgets);
            mMixExecuteWidgets.mTimeBox.setBackgroundResource(R.drawable.bg_timer_green);
            //mMixExecuteWidgets.mTimeBox.setBase(1);
            mMixExecuteWidgets.mAddComponentRelativeLayout =(RelativeLayout)this.findViewById(R.id.tech_task_mix_part2_container_relative_layout);
            mMixExecuteWidgets.mTimeBox.setTextColor(getResources().getColor(R.color.colorGreen));
            mMixExecuteWidgets.mTimeBox.start();

        }
        else {
            throw new KeyCharacterMap.UnavailableException("mMixExecuteWidgets is not init,it is null,so must init mMixExecuteWidgets firstly");
        }
    }

    private void initPauseView(){
        if(mMixExecuteWidgets !=null){
            initTaskMixCommonView(mMixExecuteWidgets);
            mMixExecuteWidgets.mTimeBox.setBase(123545343);
            //part_pause_reason
            mMixExecuteWidgets.mPauseReason = (TextView)this.findViewById(R.id.tech_task_mix_pause_reason_content_text_view);
            mMixExecuteWidgets.mPauseTime = (TextView)this.findViewById(R.id.tech_task_mix_pause_reason_time_content_text_view);
        }
        else {
            throw new KeyCharacterMap.UnavailableException("mMixExecuteWidgets is not init,it is null,so must init mMixExecuteWidgets firstly");
        }
    }

    private void initTaskMixCommonView(Object object){
        //part1
        if(object instanceof MixExecuteWidgets){
            Log.i(mTag,"init MixExecuteWidgets ");
            mMixExecuteWidgets.mCurrentStepTitle = (TextView)this.findViewById(R.id.tech_task_part_1_title_1_text_view) ;
            mMixExecuteWidgets.mCurrentStepContent = (TextView)this.findViewById(R.id.tech_task_part_1_title_2_text_view);
            mMixExecuteWidgets.mExecutingMan = (TextView)this.findViewById(R.id.tech_task_part_1_title_3_text_view);
            mMixExecuteWidgets.mTimeBox =(Chronometer)this.findViewById(R.id.tech_task_part_1_title_4_text_view);
            mMixExecuteWidgets.mDoneImageView = (ImageView)this.findViewById(R.id.tech_task_part1_done_image_view);

            //part2
            mMixExecuteWidgets.mAddComponentRelativeLayout =(RelativeLayout)this.findViewById(R.id.tech_task_mix_part2_container_relative_layout);

            //part3
            mMixExecuteWidgets.mButtonPre = (Button)this.findViewById(R.id.tech_pre_task_button);
            mMixExecuteWidgets.mButtonNext = (Button)this.findViewById(R.id.tech_next_task_button);
            mMixExecuteWidgets.mButtonMixFunction = (Button)this.findViewById(R.id.tech_mix_func_button);

            mMixExecuteWidgets.mButtonPre.setOnClickListener(this);
            mMixExecuteWidgets.mButtonNext.setOnClickListener(this);
            mMixExecuteWidgets.mButtonMixFunction.setOnClickListener(this);
            mMixExecuteWidgets.mAddComponentRelativeLayout.setOnClickListener(this);
        }
        else if(object instanceof MixDoneOrUnStartWidgets){
            Log.i(mTag,"init MixDoneOrUnStartWidgets ");
            //task_part1_description
//            TextView mOrderSerialNum = ;
//            TextView mStationJob = ;
//            TextView mStation = ;
//            TextView mRelativePerson;
//            Chronometer mTimeBox;
//            ImageView mDoneImageView;

            //task_part2_station_progress
//            ListView mStepListView;

            //task_part3_component
//            ListView mComponentListView;

            //task_part4_operation
//            Button mOperationButton;
        }
        else{
            throw new UnsupportedOperationException("This should not be here");
        }
    }

    private void initDoneView(){

    }

    private void initUnStartView(){
    }

    private void initDoneEditorView(){
        if(mMixExecuteWidgets != null){
            initTaskMixCommonView(mMixExecuteWidgets);
            mMixExecuteWidgets.mDoneImageView.setVisibility(View.VISIBLE);
        }
    }

    private void initDoneCheckView(){
        if(mMixExecuteWidgets != null){
            initTaskMixCommonView(mMixExecuteWidgets);
            mMixExecuteWidgets.mDoneImageView.setVisibility(View.VISIBLE);
        }
    }

    private void reEditTask(){

    }

    private void startTask(){

    }

    private void getOrderDetail(){

    }

    private void getNextTask(int step){

    }

    private void getPreTask(int step){

    }

    private void pauseTask(){
        HashMap<String,Object> reqDataMap = new HashMap<String,Object>();
        reqDataMap.put(RequestDataKey.ACCESS_TOKEN,mAccessToken);

    }

    private void resumeTask(){

    }

    @Override
    public void onBackEvent() {
        finish();
    }
}
