package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.constant.Prompt;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.helper.PreferencesHelper;
import com.example.vke.shop4stech.model.OrderDetailModel;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public class TaskMixExecuteActivity extends BaseTaskActivity implements View.OnClickListener{

    private static final String mTag = "TaskMixExecuteActivity";
    private static final String KEY_ACTIVITY_TYPE = "TaskMixExecuteActivity.Type";
    private static final String KEY_INDEX = "TaskMixExecuteActivity.Index";
    private static final String KEY_ORDER_SERIAL_NUM = "TaskMixExecuteActivity.OrderSerialNum";
    private int mActivityType;

    private MixExecuteWidgets mMixExecuteWidgets;
    private MixDoneOrUnStartWidgets mMixDoneOrUnStartWidgets;

    private Handler mHandler;
    private String mIndex,mOrderSerialNum,mCurrentStep;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tech_pre_task_button:
                break;
            case R.id.tech_next_task_button:
                break;
            case R.id.tech_mix_func_button:
                if(mActivityType == ActivityType.TYPE_PAUSE){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            resumeTask();
                        }
                    }).start();
                }
                else if(mActivityType == ActivityType.TYPE_EXECUTING){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            pauseTask("有点事情","213123");
                        }
                    }).start();

                }
                else{
                    Log.e(mTag,"onClick tech_task_part4_operation_button --> unsupported click");
                }

                break;
            case R.id.tech_task_part4_operation_button:
                if(mActivityType == ActivityType.TYPE_DONE){
                    reeditTaskComponents();
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
        Chronometer mPauseTime;
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

        ArrayAdapter<String> mAdapter;
        SimpleAdapter mComponentsSimpleAdapter;
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
        mMixDoneOrUnStartWidgets = new MixDoneOrUnStartWidgets();

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
        if(mHandler == null){
            mHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case MessageType.TYPE_GET_TASK_DETAIL_SUCCESS:
                            updateViewData((OrderDetailModel)msg.obj);
                            break;
                        case MessageType.TYPE_PAUSE_TASK_SUCCESS:
                            TaskMixExecuteActivity.this.finish();
                            TaskMixExecuteActivity.start(TaskMixExecuteActivity.this,ActivityType.TYPE_PAUSE,mIndex,mOrderSerialNum);
                            break;
                        case MessageType.TYPE_RESUME_TASK_SUCCESS:
                            TaskMixExecuteActivity.this.finish();
                            TaskMixExecuteActivity.start(TaskMixExecuteActivity.this,ActivityType.TYPE_EXECUTING,mIndex,mOrderSerialNum);
                            break;
                        case MessageType.TYPE_NEXT_TASK_SUCCESS:
                            break;
                        case MessageType.TYPE_PRE_TASK_SUCCESS:
                            break;
                        case MessageType.TYPE_ACCESS_TOKEN_INVALID:
                            break;

                        case MessageType.TYPE_PRE_TASK_FAILED:
                        case MessageType.TYPE_NEXT_TASK_FAILED:
                        case MessageType.TYPE_PAUSE_TASK_FAILED:
                        case MessageType.TYPE_START_TASK_FAILED:
                        case MessageType.TYPE_RESUME_TASK_FAILED:
                        case MessageType.TYPE_GET_TASK_DETAIL_FAILED:
                            Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            };
        }
    }

    private void updateViewData(OrderDetailModel orderDetailModel){
        mCurrentStep = orderDetailModel.getmCurrentStep();
        switch (mActivityType){
            case ActivityType.TYPE_DONE_EDITOR:
                break;
            case ActivityType.TYPE_DONE_VIEW:
                break;
            case ActivityType.TYPE_EXECUTING:
                updateCommData(mMixExecuteWidgets,orderDetailModel);
                mMixExecuteWidgets.mTimeBox.setBase(SystemClock.elapsedRealtime() - orderDetailModel.getmCurrentStepSpendTime()*1000);
                mMixExecuteWidgets.mTimeBox.start();
                break;
            case ActivityType.TYPE_PAUSE:
                updateCommData(mMixExecuteWidgets,orderDetailModel);

                mMixExecuteWidgets.mTimeBox.setBase(SystemClock.elapsedRealtime() - orderDetailModel.getmCurrentStepSpendTime()*1000);
                mMixExecuteWidgets.mPauseReason.setText(orderDetailModel.getmPauseTitle());
                mMixExecuteWidgets.mPauseTime.setBase(SystemClock.elapsedRealtime() - orderDetailModel.getmPauseTime()*1000);
                break;
            case ActivityType.TYPE_DONE:

                updateCommData(mMixDoneOrUnStartWidgets,orderDetailModel);

                mMixDoneOrUnStartWidgets.mTimeBox.setBase(SystemClock.elapsedRealtime()-orderDetailModel.getmTotalSpendTime()*1000);
                String executePerson = "执行人: "+orderDetailModel.getmPrincipal();
                mMixDoneOrUnStartWidgets.mRelativePerson.setText(executePerson);

                //step list
                mMixDoneOrUnStartWidgets.mAdapter = new ArrayAdapter<String>(this,R.layout.task_step_item,orderDetailModel.getmStepsList());
                mMixDoneOrUnStartWidgets.mStepListView.setAdapter(mMixDoneOrUnStartWidgets.mAdapter);

                //component list
                mMixDoneOrUnStartWidgets.mComponentsSimpleAdapter = new SimpleAdapter(this,orderDetailModel.getmComponentsList(),
                        R.layout.task_component_item,new String[] { "tech_task_component_title_text_view", "tech_task_component_num_text_view" },
                        new int[]{R.id.tech_task_component_title_text_view,R.id.tech_task_component_num_text_view});
                mMixDoneOrUnStartWidgets.mComponentListView.setAdapter(mMixDoneOrUnStartWidgets.mComponentsSimpleAdapter);

                break;
            case ActivityType.TYPE_UNSTART:
                if(mMixDoneOrUnStartWidgets !=null){
                    updateCommData(mMixDoneOrUnStartWidgets,orderDetailModel);

                    String resonsiblePerson = "负责人: "+orderDetailModel.getmPrincipal();
                    mMixDoneOrUnStartWidgets.mRelativePerson.setText(resonsiblePerson);

                    //step list
                    mMixDoneOrUnStartWidgets.mAdapter = new ArrayAdapter<String>(this,R.layout.task_step_item,orderDetailModel.getmStepsList());
                    mMixDoneOrUnStartWidgets.mStepListView.setAdapter(mMixDoneOrUnStartWidgets.mAdapter);
                }

                break;
            default:
                throw new UnsupportedOperationException("updateViewData, But get error type ");
        }
    }

    private void updateCommData(Object object,OrderDetailModel orderDetailModel){
        if(object instanceof MixDoneOrUnStartWidgets){
            mMixDoneOrUnStartWidgets.mOrderSerialNum.setText(orderDetailModel.getmOrderSerialNum());
            mMixDoneOrUnStartWidgets.mStationJob.setText(orderDetailModel.getmOrderType());
            mMixDoneOrUnStartWidgets.mStation.setText(orderDetailModel.getmOrderStation());
        }
        else if(object instanceof  MixExecuteWidgets){
            String currentStepTitle = "步骤: " +orderDetailModel.getmCurrentStep() +"/" + orderDetailModel.getmStepAll();
            mMixExecuteWidgets.mCurrentStepTitle.setText(currentStepTitle);
            mMixExecuteWidgets.mCurrentStepContent.setText(orderDetailModel.getmCurrentStepTitle());
            String person ="执行人: " +  orderDetailModel.getmPrincipal();
            mMixExecuteWidgets.mExecutingMan.setText(person);
        }
    }

    private void startThreadToGetOrderData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                getOrderDetail();
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
            mMixExecuteWidgets.mButtonMixFunction.setText("恢复");

            //part_pause_reason
            mMixExecuteWidgets.mPauseReason = (TextView)this.findViewById(R.id.tech_task_mix_pause_reason_content_text_view);
            mMixExecuteWidgets.mPauseTime = (Chronometer)this.findViewById(R.id.tech_task_mix_pause_reason_time_content_text_view);

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
            mMixDoneOrUnStartWidgets.mOrderSerialNum = (TextView)this.findViewById(R.id.tech_task_part1_desc_title_1_text_view);
            mMixDoneOrUnStartWidgets.mStationJob = (TextView)this.findViewById(R.id.tech_task_part1_desc_title_2_text_view);
            mMixDoneOrUnStartWidgets.mStation = (TextView)this.findViewById(R.id.tech_task_part1_desc_title_3_text_view);
            mMixDoneOrUnStartWidgets.mRelativePerson =(TextView)this.findViewById(R.id.tech_task_part1_desc_title_4_text_view);
            mMixDoneOrUnStartWidgets.mTimeBox =(Chronometer)this.findViewById(R.id.tech_task_part1_desc_title_5_text_view);
            mMixDoneOrUnStartWidgets.mDoneImageView = (ImageView)this.findViewById(R.id.tech_task_part1_desc_done_image_view);

            //task_part2_station_progress
            mMixDoneOrUnStartWidgets.mStepListView = (ListView)this.findViewById(R.id.task_part2_staion_step_list_view);

            //task_part4_operation
            mMixDoneOrUnStartWidgets.mOperationButton = (Button)this.findViewById(R.id.tech_task_part4_operation_button);
            if(mMixDoneOrUnStartWidgets.mOperationButton !=null){
                mMixDoneOrUnStartWidgets.mOperationButton.setOnClickListener(this);
            }
            else {
                throw new UnsupportedOperationException("Operation button not init");
            }
        }
        else{
            throw new UnsupportedOperationException("This should not be here");
        }
    }

    private void initDoneView(){
        if(mMixDoneOrUnStartWidgets != null){
            initTaskMixCommonView(mMixDoneOrUnStartWidgets);
            //task_part3_component
            mMixDoneOrUnStartWidgets.mComponentListView = (ListView)this.findViewById(R.id.task_part3_component_list_view);
        }
    }

    private void initUnStartView(){
        if(mMixDoneOrUnStartWidgets != null){
            initTaskMixCommonView(mMixDoneOrUnStartWidgets);
            mMixDoneOrUnStartWidgets.mDoneImageView.setVisibility(View.GONE);
            mMixDoneOrUnStartWidgets.mTimeBox.setVisibility(View.GONE);
            mMixDoneOrUnStartWidgets.mOperationButton.setText("开始任务");
        }

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

    private void reeditTaskComponents(){

    }

    private void startTask(){

    }

    private void getOrderDetail(){
        HashMap<String,Object> map = new HashMap<String,Object>();
        if(mAccessToken == null){
            mAccessToken = PreferencesHelper.getPreferenceAccessToken(TaskMixExecuteActivity.this);

        }
        map.put(RequestDataKey.ACCESS_TOKEN,mAccessToken);
        map.put(RequestDataKey.INFO, "detail");
        map.put(RequestDataKey.TYPE, "all");
        map.put(RequestDataKey.INDEX, mIndex);

        HashMap<String,Object> resultMap = NetOperationHelper.getOrderDetail(map);
        Message msg = mHandler.obtainMessage();
        if(resultMap!= null){
            OrderDetailModel result = (OrderDetailModel)resultMap.get(NetOperationHelper.KEY_RESULT);
            if(result !=null){
                msg.what = MessageType.TYPE_GET_TASK_DETAIL_SUCCESS;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
            else {
                String errorInfo = (String)resultMap.get(NetOperationHelper.KEY_ERROR);
                if(errorInfo != null){
                    msg.what = MessageType.TYPE_GET_TASK_DETAIL_FAILED;
                    msg.obj = errorInfo;
                    mHandler.sendMessage(msg);
                }else {
                    Log.e(mTag,"Should not be here");
                }
            }
        }
        else{
            msg.what = MessageType.TYPE_GET_TASK_DETAIL_FAILED;
            msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
            mHandler.sendMessage(msg);
        }

    }

    private void getNextTask(int step){

    }

    private void getPreTask(int step){

    }

    private void pauseTask(String pauseReason,String pauseTime){
        JSONArray array = new JSONArray();

        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,mAccessToken);
        map.put(RequestDataKey.SERIAL_NUM,mOrderSerialNum);
        map.put(RequestDataKey.CURRENT_STEP,mCurrentStep);
        map.put(RequestDataKey.TITLE,pauseReason);
        map.put(RequestDataKey.ESTIMATED_TIME,pauseTime);
        map.put(RequestDataKey.COMPONENT_LIST,array);

        Message msg = mHandler.obtainMessage();
        HashMap<String,Object> respMap = NetOperationHelper.pauseTask(map);
        if(respMap!=null){
            String result = (String)respMap.get(NetOperationHelper.KEY_RESULT);
            if (result != null){
                mHandler.sendEmptyMessage(MessageType.TYPE_PAUSE_TASK_SUCCESS);
            }
            else {
                String errorInfo = (String)respMap.get(NetOperationHelper.KEY_ERROR);

                msg.what = MessageType.TYPE_PAUSE_TASK_FAILED;
                msg.obj = errorInfo;
                mHandler.sendMessage(msg);
            }
        }
        else{
            msg.what = MessageType.TYPE_PAUSE_TASK_FAILED;
            msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
            mHandler.sendMessage(msg);
        }
    }

    private void resumeTask(){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,mAccessToken);
        map.put(RequestDataKey.SERIAL_NUM,mOrderSerialNum);
        map.put(RequestDataKey.CURRENT_STEP,mCurrentStep);

        HashMap<String,Object> respMap = NetOperationHelper.resumeTask(map);
        Message msg = mHandler.obtainMessage();
        if(respMap!=null){
            String result = (String)respMap.get(NetOperationHelper.KEY_RESULT);
            if (result != null){
                mHandler.sendEmptyMessage(MessageType.TYPE_RESUME_TASK_SUCCESS);
            }
            else {
                String errorInfo = (String)respMap.get(NetOperationHelper.KEY_ERROR);

                msg.what = MessageType.TYPE_RESUME_TASK_FAILED;
                msg.obj = errorInfo;
                mHandler.sendMessage(msg);
            }
        }
        else{
            msg.what = MessageType.TYPE_RESUME_TASK_FAILED;
            msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
            mHandler.sendMessage(msg);
        }

    }

    @Override
    public void onBackEvent() {
        finish();
    }
}
