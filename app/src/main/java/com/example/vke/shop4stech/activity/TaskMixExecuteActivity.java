package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.adapter.ComponentAdapter;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.constant.Prompt;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.customLayout.RevealTextView;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.helper.PreferencesHelper;
import com.example.vke.shop4stech.model.ComponentModel;
import com.example.vke.shop4stech.model.OrderDetailModel;
import com.example.vke.shop4stech.model.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskMixExecuteActivity extends BaseTaskActivity implements View.OnClickListener{

    private static final String mTag = "TaskMixExecuteActivity";
    public static final String KEY_ACTIVITY_TYPE = "TaskMixExecuteActivity.Type";
    public static final String KEY_INDEX = "TaskMixExecuteActivity.Index";
    public static final String KEY_ORDER_SERIAL_NUM = "TaskMixExecuteActivity.OrderSerialNum";
    public static final String KEY_TRIGGER_STEP = "TaskMixExecuteActivity.RecordStep";
    public int mActivityType;

    private MixExecuteWidgets mMixExecuteWidgets;
    private MixDoneOrUnStartWidgets mMixDoneOrUnStartWidgets;

    private Handler mHandler;
    private String mIndex,mOrderSerialNum,mCurrentStep,mOrderType,mOrderState,mExecuteMan,mStepAll;
    private String mRecordAppCurrentStep;//记录app界面当前执行到第几步，因为有可能是查看已完成的步骤
    private String mTriggerStep ;//用户记录是从第几步触发 编辑步骤
    private boolean mCanTouch = true;

    @Override
    public void onClick(View v) {
        if(!NetOperationHelper.isNetworkConnected(this)){
            Toast.makeText(getApplicationContext(),R.string.tech_network_unuseful,Toast.LENGTH_SHORT).show();
            return;
        }

        if(!mCanTouch){
            Toast.makeText(getApplicationContext(),"正在获取数据中,请稍后",Toast.LENGTH_SHORT).show();
            return;
        }

        mCanTouch = false;
        switch (v.getId()){
            case R.id.tech_pre_task_button:
                if(mRecordAppCurrentStep == null){
                    mCanTouch = true;
                    return;
                }
                if(mRecordAppCurrentStep.equals("1")){
                    Toast.makeText(getApplicationContext(),"已经是第一步",Toast.LENGTH_SHORT).show();
                    mCanTouch = true;
                    return;
                }

                mRecordAppCurrentStep = Integer.toString(Integer.parseInt(mRecordAppCurrentStep) -1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getPreTask(mRecordAppCurrentStep);
                    }
                }).start();
                break;
            case R.id.tech_next_task_button:
                if(mRecordAppCurrentStep == null){
                    mCanTouch = true;
                    return;
                }
                if(mOrderState.equals("完成") || mOrderState.equals("待评价")){
                    if(mRecordAppCurrentStep.equals(mStepAll)){
                        Toast.makeText(getApplicationContext(),"已经是最后一步",Toast.LENGTH_SHORT).show();
                        mCanTouch = true;
                        return;
                    }
                    else{//
                        mRecordAppCurrentStep = Integer.toString(Integer.parseInt(mRecordAppCurrentStep) +1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getNextTask(mRecordAppCurrentStep,"完成");
                            }
                        }).start();
                    }
                }
                else {
                    if(mRecordAppCurrentStep.equals(mCurrentStep)){//等于当前步骤时，则开始下一步

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("确认完成当前步骤吗?");
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                if(!mCanTouch){
                                    mCanTouch = true;
                                }
                            }
                        });

                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                mRecordAppCurrentStep = Integer.toString(Integer.parseInt(mRecordAppCurrentStep) +1);//查看下一步
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        editComponents(mCurrentStep);
                                        getNextTask(mCurrentStep,"执行中");
                                    }
                                }).start();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mCanTouch = true;
                            }
                        }).show();

                    }
                    else {
                        mRecordAppCurrentStep = Integer.toString(Integer.parseInt(mRecordAppCurrentStep) +1);//查看下一步
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getNextTask(mRecordAppCurrentStep,"完成");
                            }
                        }).start();
                    }
                }
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(TaskMixExecuteActivity.this);
                    View view= LayoutInflater.from(TaskMixExecuteActivity.this).inflate(R.layout.task_custom_dialog,null);
                    RevealTextView contentTV1 = (RevealTextView)view.findViewById(R.id.tech_custom_dialog_content1_text_view);
                    RevealTextView contentTV2 = (RevealTextView)view.findViewById(R.id.tech_custom_dialog_content2_text_view);
                    Button cancelBtn=(Button)view.findViewById(R.id.tech_custom_dialog_cancel_button);
                    Button confirmBtn=(Button)view.findViewById(R.id.tech_custom_dialog_confirm_button);

                    final EditText contentED1 = (EditText)view.findViewById(R.id.tech_custom_dialog_content1_edit_view);
                    final EditText contentED2 = (EditText)view.findViewById(R.id.tech_custom_dialog_content2_edit_view);
                    contentED2.setInputType(InputType.TYPE_CLASS_NUMBER );
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if(!mCanTouch){
                                mCanTouch = true;
                            }
                        }
                    });

                    builder.setView(view);

                    final AlertDialog dialog = builder.show();

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            mCanTouch = true;
                        }
                    });
                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String PauseReason = contentED1.getText().toString();
                            final String PauseTime = contentED2.getText().toString();
                            if(PauseReason.equals("") || PauseTime.equals("")){
                                Toast.makeText(getApplicationContext(),R.string.tech_pause_can_not_null,Toast.LENGTH_SHORT).show();
                                mCanTouch = true;
                                return;
                            }
                            dialog.dismiss();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    pauseTask(PauseReason,PauseTime);
                                }
                            }).start();
                        }
                    });

                }
                else if(mActivityType == ActivityType.TYPE_DONE_VIEW){

                    TaskMixExecuteActivity.start(this,ActivityType.TYPE_DONE_EDITOR,mIndex,mOrderSerialNum,mRecordAppCurrentStep);
                    mCanTouch = true;
                }else if(mActivityType == ActivityType.TYPE_DONE_EDITOR){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            editComponents(mRecordAppCurrentStep);
                        }
                    }).start();
                }
                else {
                    Log.e(mTag,"onClick tech_task_part4_operation_button --> unsupported click");
                }
                break;
            case R.id.tech_task_part4_operation_button:
                if(mActivityType == ActivityType.TYPE_DONE){
                    reeditTaskComponents();
                }
                else if(mActivityType == ActivityType.TYPE_UNSTART){
                    Log.i(mTag,"onClick tech_task_part4_operation_button --> start task");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startTask();
                        }
                    }).start();
                }
                else{
                    Log.e(mTag,"onClick tech_task_part4_operation_button --> unsupported click");
                }
                break;
            case R.id.tech_task_mix_part2_container_relative_layout://add component
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskMixExecuteActivity.this);
                View view= LayoutInflater.from(TaskMixExecuteActivity.this).inflate(R.layout.task_custom_dialog,null);
                RevealTextView title = (RevealTextView)view.findViewById(R.id.tech_custom_dialog_title_text_view);
                RevealTextView contentTV1 = (RevealTextView)view.findViewById(R.id.tech_custom_dialog_content1_text_view);
                RevealTextView contentTV2 = (RevealTextView)view.findViewById(R.id.tech_custom_dialog_content2_text_view);
                title.setText("添加零件");
                contentTV1.setText("零件名称");
                contentTV2.setText("零件数量");

                Button cancelBtn=(Button)view.findViewById(R.id.tech_custom_dialog_cancel_button);
                Button confirmBtn=(Button)view.findViewById(R.id.tech_custom_dialog_confirm_button);

                final EditText contentED1 = (EditText)view.findViewById(R.id.tech_custom_dialog_content1_edit_view);
                final EditText contentED2 = (EditText)view.findViewById(R.id.tech_custom_dialog_content2_edit_view);
                contentED1.setHint(R.string.tech_hint_input_component_name);
                contentED2.setHint(R.string.tech_hint_input_component_num);
                contentED2.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(view);

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if(!mCanTouch){
                            mCanTouch = true;
                        }
                    }
                });

                final AlertDialog dialog = builder.show();

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCanTouch = true;
                        dialog.dismiss();
                    }
                });
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String  componentName = contentED1.getText().toString();
                        final String  componentNum = contentED2.getText().toString();
                        if(componentName.equals("") || componentNum.equals("")){
                            Toast.makeText(getApplicationContext(),R.string.tech_hint_input_component,Toast.LENGTH_SHORT).show();
                            mCanTouch = true;
                            return;
                        }
                        ComponentModel newComponentModel= new ComponentModel(componentName,componentNum);
                        if(mMixExecuteWidgets.mComponentModelList == null){
                            mMixExecuteWidgets.mComponentModelList = new ArrayList<ComponentModel>();
                        }
                        mMixExecuteWidgets.mComponentModelList.add(newComponentModel);
                        mMixExecuteWidgets.mComponentAdapter.bindData(mMixExecuteWidgets.mComponentModelList);
                        mMixExecuteWidgets.mComponentAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        mCanTouch = true;
                    }
                });
                break;
        }
    }

    /*
    * 用于点击上一步和下一步后，重新渲染当前执行到的步骤的VIEW
    */
    public class OperationType{
        static final int STEP_DONE = 0;
        static final int STEP_EXECUTING = 1;
    }

    /*
    * 正在执行中的任务，包括1.暂停 2.执行中 3.已完成进行查看具体步骤 4.已完成编辑具体步骤零件
    */
    public class MixExecuteWidgets{

        //part1
        RevealTextView mCurrentStepTitle;
        RevealTextView mCurrentStepContent;
        RevealTextView mExecutingMan;
        Chronometer mTimeBox;
        ImageView mDoneImageView;
        //part2
        ListView mComponentListView;
        RelativeLayout mAddComponentRelativeLayout;
        ComponentAdapter mComponentAdapter;
        List<ComponentModel> mComponentModelList;

        //part3
        Button mButtonPre,mButtonNext,mButtonMixFunction;

        //part_pause_reason
        RevealTextView mPauseReason;
        Chronometer mPauseTime;
    }

    /*
    * 容纳完成界面的一些组件，包括1.已完成的页面 2.未开始的页面
    */
    public class MixDoneOrUnStartWidgets{
        //task_part1_description
        RevealTextView mOrderSerialNum;
        RevealTextView mStationJob;
        RevealTextView mStation;
        RevealTextView mRelativePerson;
        Chronometer mTimeBox;
        ImageView mDoneImageView;

        //task_part2_station_progress
        ListView mStepListView;

        //task_part3_component
        ListView mComponentListView;

        //task_part4_operation
        Button mOperationButton;

        ArrayAdapter<String> mAdapter;//for unstart task
        SimpleAdapter mStepsSimpleAdapter;//for done task
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

    public static void start(Activity context,int type,String index,String orderSerialNum,String recordAppCurrentStep) {
        Intent starter = new Intent(context, TaskMixExecuteActivity.class);
        starter.putExtra(KEY_ACTIVITY_TYPE,type);
        starter.putExtra(KEY_INDEX,index);
        starter.putExtra(KEY_ORDER_SERIAL_NUM,orderSerialNum);
        starter.putExtra(KEY_TRIGGER_STEP,recordAppCurrentStep);
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
        mTriggerStep = getIntent().getStringExtra(KEY_TRIGGER_STEP);

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
        setContentViewVisibility(false);
    }

    private void initHandler(){
        if(mHandler == null){
            mHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    //收到消息重新设置界面按钮可操作
                    setContentViewVisibility(true);
                    mCanTouch = true;
                    switch (msg.what){
                        case MessageType.TYPE_GET_TASK_DETAIL_SUCCESS:
                            updateViewData((OrderDetailModel)msg.obj);
                            mRecordAppCurrentStep = mCurrentStep;
                            if((mActivityType == ActivityType.TYPE_DONE_EDITOR || mActivityType == ActivityType.TYPE_DONE_VIEW) && mTriggerStep != null){
                                mRecordAppCurrentStep = mTriggerStep;
                                mCanTouch = false;
                                setContentViewVisibility(false);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getNextTask(mRecordAppCurrentStep,"完成");
                                    }
                                }).start();
                            }
                            break;
                        case MessageType.TYPE_PAUSE_TASK_SUCCESS:
                            TaskMixExecuteActivity.this.finish();
                            TaskMixExecuteActivity.start(TaskMixExecuteActivity.this,ActivityType.TYPE_PAUSE,mIndex,mOrderSerialNum);
                            break;
                        case MessageType.TYPE_EDIT_COMPONENT_SUCCESS:
                            if(mActivityType == ActivityType.TYPE_DONE_EDITOR){
                            TaskMixExecuteActivity.this.finish();
                            }
                            break;
                        case MessageType.TYPE_RESUME_TASK_SUCCESS:
                            TaskMixExecuteActivity.this.finish();
                            TaskMixExecuteActivity.start(TaskMixExecuteActivity.this,ActivityType.TYPE_EXECUTING,mIndex,mOrderSerialNum);
                            break;
                        case MessageType.TYPE_START_TASK_SUCCESS:
                            TaskMixExecuteActivity.this.finish();
                            TaskMixExecuteActivity.start(TaskMixExecuteActivity.this,ActivityType.TYPE_EXECUTING,mIndex,mOrderSerialNum);
                            break;
                        case MessageType.TYPE_NEXT_TASK_SUCCESS:
                            OrderDetailModel model = (OrderDetailModel)msg.obj;
                            mCurrentStep = model.getmCurrentStep();
                            if((model.getmOrderState().equals("待评价")|| model.getmOrderState().equals("完成"))&& mActivityType == ActivityType.TYPE_EXECUTING){
                                TaskMixExecuteActivity.this.finish();
                                TaskMixExecuteActivity.start(TaskMixExecuteActivity.this,ActivityType.TYPE_DONE,mIndex,mOrderSerialNum);
                                break;
                            }
                            updateViewData(model);
                            //当查看步骤回归到与当前步骤一致时，重新设置button可按和界面重新显示
                            if(mActivityType == ActivityType.TYPE_EXECUTING ){
                                if(mRecordAppCurrentStep.equals(mCurrentStep)){
                                    reupdateStepView(OperationType.STEP_EXECUTING);
                                }else {
                                    reupdateStepView(OperationType.STEP_DONE);
                                }
                            }
                            else if (mActivityType == ActivityType.TYPE_DONE_EDITOR || mActivityType == ActivityType.TYPE_DONE_VIEW){
                                String currentStepTitle = "步骤: " +mRecordAppCurrentStep +"/" + mStepAll;
                                mMixExecuteWidgets.mCurrentStepTitle.setAnimatedText(currentStepTitle);
                            }

                            break;
                        case MessageType.TYPE_PRE_TASK_SUCCESS:
                            OrderDetailModel detailModel = (OrderDetailModel)msg.obj;
                            updateViewData(detailModel);
                            if(mActivityType == ActivityType.TYPE_EXECUTING){//回到上一步时，不能编辑，不能查看，不能暂停，只允许下一步，和上一步
                                reupdateStepView(OperationType.STEP_DONE);
//                                String currentStepTitle = "步骤: " +mRecordAppCurrentStep +"/" + mStepAll;
//                                mMixExecuteWidgets.mCurrentStepTitle.setText(currentStepTitle);
//                                mMixExecuteWidgets.mTimeBox.setBackgroundResource(R.drawable.bg_timer_gray);
//                                mMixExecuteWidgets.mTimeBox.setTextColor(getResources().getColor(R.color.colorGray));
//                                mMixExecuteWidgets.mTimeBox.stop();
//                                mMixExecuteWidgets.mAddComponentRelativeLayout.setClickable(false);
//                                mMixExecuteWidgets.mAddComponentRelativeLayout.setVisibility(View.GONE);
//                                mMixExecuteWidgets.mButtonMixFunction.setClickable(false);
//                                mMixExecuteWidgets.mButtonMixFunction.setBackgroundResource(R.drawable.btn_gray);
                            }
                            else {
                                String currentStepTitle = "步骤: " +mRecordAppCurrentStep +"/" + mStepAll;
                                mMixExecuteWidgets.mCurrentStepTitle.setAnimatedText(currentStepTitle);
                            }

                            break;
                        case MessageType.TYPE_ACCESS_TOKEN_INVALID:
                            break;
                        case MessageType.TYPE_PRE_TASK_FAILED:
                            //失败需要回退步骤
                            mRecordAppCurrentStep = Integer.toString(Integer.parseInt(mRecordAppCurrentStep) +1);
                            Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                            break;
                        case MessageType.TYPE_NEXT_TASK_FAILED:
                            //操作失败需要回退步骤
                            mRecordAppCurrentStep = Integer.toString(Integer.parseInt(mRecordAppCurrentStep) -1);
                        case MessageType.TYPE_PAUSE_TASK_FAILED:
                        case MessageType.TYPE_START_TASK_FAILED:
                        case MessageType.TYPE_RESUME_TASK_FAILED:
                        case MessageType.TYPE_EDIT_COMPONENT_FAILED:
                        case MessageType.TYPE_GET_TASK_DETAIL_FAILED:
                            Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            };
        }
    }

    private void reupdateStepView(int type){
        if(type == OperationType.STEP_DONE){
            String currentStepTitle = "步骤: " +mRecordAppCurrentStep +"/" + mStepAll;
            mMixExecuteWidgets.mCurrentStepTitle.setAnimatedText(currentStepTitle);
            mMixExecuteWidgets.mTimeBox.setBackgroundResource(R.drawable.bg_timer_gray);
            mMixExecuteWidgets.mTimeBox.setTextColor(getResources().getColor(R.color.colorGray));
            mMixExecuteWidgets.mTimeBox.stop();
            mMixExecuteWidgets.mAddComponentRelativeLayout.setClickable(false);
            mMixExecuteWidgets.mAddComponentRelativeLayout.setVisibility(View.GONE);
            mMixExecuteWidgets.mButtonMixFunction.setClickable(false);
            mMixExecuteWidgets.mButtonMixFunction.setBackgroundResource(R.drawable.btn_gray);
        }
        else if(type == OperationType.STEP_EXECUTING){
            String currentStepTitle = "步骤: " +mRecordAppCurrentStep +"/" + mStepAll;
            mMixExecuteWidgets.mCurrentStepTitle.setAnimatedText(currentStepTitle);
            mMixExecuteWidgets.mTimeBox.setBackgroundResource(R.drawable.bg_timer_green);
            mMixExecuteWidgets.mTimeBox.setTextColor(getResources().getColor(R.color.colorGreen));
            mMixExecuteWidgets.mButtonMixFunction.setBackgroundResource(R.drawable.btn_stop);
            mMixExecuteWidgets.mAddComponentRelativeLayout.setVisibility(View.VISIBLE);
            mMixExecuteWidgets.mAddComponentRelativeLayout.setClickable(true);
            mMixExecuteWidgets.mButtonMixFunction.setClickable(true);
        }
        else {
            Log.e(mTag,"reupdateStepView error type received");
        }
    }

    private void updateViewData(OrderDetailModel orderDetailModel){
        mCurrentStep = orderDetailModel.getmCurrentStep();
        mOrderType = orderDetailModel.getmOrderType();
        mExecuteMan = orderDetailModel.getmPrincipal();
        mOrderState = orderDetailModel.getmOrderState();
        mStepAll = orderDetailModel.getmStepAll();
        switch (mActivityType){
            case ActivityType.TYPE_DONE_EDITOR:
                //updateCommData(mMixExecuteWidgets,orderDetailModel);
            case ActivityType.TYPE_DONE_VIEW:
                updateCommData(mMixExecuteWidgets,orderDetailModel);
                String[] executeMan = orderDetailModel.getmPrincipal().split(" ");
                String person ="执行人: " +  executeMan[executeMan.length-1];
                mMixExecuteWidgets.mExecutingMan.setAnimatedText(person);
                break;

            case ActivityType.TYPE_EXECUTING:
                updateCommData(mMixExecuteWidgets,orderDetailModel);
                mMixExecuteWidgets.mTimeBox.setBackgroundResource(R.drawable.bg_timer_green);
                mMixExecuteWidgets.mTimeBox.setTextColor(getResources().getColor(R.color.colorGreen));
                //mMixExecuteWidgets.mTimeBox.setBase(SystemClock.elapsedRealtime() - orderDetailModel.getmCurrentStepSpendTime()*1000);
                mMixExecuteWidgets.mTimeBox.start();

                break;
            case ActivityType.TYPE_PAUSE:
                updateCommData(mMixExecuteWidgets,orderDetailModel);

                //mMixExecuteWidgets.mTimeBox.setBase(SystemClock.elapsedRealtime() - orderDetailModel.getmCurrentStepSpendTime()*1000);
                mMixExecuteWidgets.mPauseReason.setAnimatedText(orderDetailModel.getmPauseTitle());
                mMixExecuteWidgets.mPauseTime.setBase(SystemClock.elapsedRealtime() - orderDetailModel.getmPauseTime()*1000);


                break;
            case ActivityType.TYPE_DONE:

                updateCommData(mMixDoneOrUnStartWidgets,orderDetailModel);

                mMixDoneOrUnStartWidgets.mTimeBox.setBase(SystemClock.elapsedRealtime()-orderDetailModel.getmTotalSpendTime()*1000);
                String executePerson = "执行人: "+orderDetailModel.getmPrincipal();
                mMixDoneOrUnStartWidgets.mRelativePerson.setAnimatedText(executePerson);

                //step list
                mMixDoneOrUnStartWidgets.mStepsSimpleAdapter = new SimpleAdapter(this,orderDetailModel.getmDoneStepsList(),R.layout.task_step_done_item,
                        new String[] { "tech_task_step_done_title_text_view", "tech_task_step_done_time_text_view" },
                        new int[]{R.id.tech_task_step_done_title_text_view,R.id.tech_task_step_done_time_text_view});
                mMixDoneOrUnStartWidgets.mStepListView.setAdapter(mMixDoneOrUnStartWidgets.mStepsSimpleAdapter);

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


    private void updateCommData(Object object, OrderDetailModel orderDetailModel){
        if(object instanceof MixDoneOrUnStartWidgets){
            mMixDoneOrUnStartWidgets.mOrderSerialNum.setAnimatedText(orderDetailModel.getmOrderSerialNum());
            mMixDoneOrUnStartWidgets.mStationJob.setAnimatedText(orderDetailModel.getmOrderType());
            mMixDoneOrUnStartWidgets.mStation.setAnimatedText(orderDetailModel.getmOrderStation());
        }
        else if(object instanceof  MixExecuteWidgets){
            String currentStepTitle = "步骤: " +orderDetailModel.getmCurrentStep() +"/" + orderDetailModel.getmStepAll();
            mMixExecuteWidgets.mCurrentStepTitle.setAnimatedText(currentStepTitle);
            mMixExecuteWidgets.mCurrentStepContent.setAnimatedText(orderDetailModel.getmCurrentStepTitle());
            String person ="执行人: " +  orderDetailModel.getmPrincipal();
            mMixExecuteWidgets.mExecutingMan.setAnimatedText(person);
            mMixExecuteWidgets.mTimeBox.setBase(SystemClock.elapsedRealtime() - orderDetailModel.getmCurrentStepSpendTime()*1000);

            //update component list
            mMixExecuteWidgets.mComponentModelList = orderDetailModel.getmExecutingComponentList();
            if(mMixExecuteWidgets.mComponentAdapter == null){
                mMixExecuteWidgets.mComponentAdapter = new ComponentAdapter(TaskMixExecuteActivity.this, mMixExecuteWidgets.mComponentModelList);
            }
            else{
                mMixExecuteWidgets.mComponentAdapter.bindData(mMixExecuteWidgets.mComponentModelList);
            }
            mMixExecuteWidgets.mComponentAdapter.notifyDataSetChanged();
        }
    }

    private void startThreadToGetOrderData(){
        mCanTouch = false;
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
            mMixExecuteWidgets.mPauseReason = (RevealTextView)this.findViewById(R.id.tech_task_mix_pause_reason_content_text_view);
            mMixExecuteWidgets.mPauseTime = (Chronometer)this.findViewById(R.id.tech_task_mix_pause_reason_time_content_text_view);

            //button color change
            mMixExecuteWidgets.mButtonNext.setVisibility(View.GONE);
            mMixExecuteWidgets.mButtonPre.setVisibility(View.GONE);
            mMixExecuteWidgets.mAddComponentRelativeLayout.setVisibility(View.GONE);
//            mMixExecuteWidgets.mButtonNext.setClickable(false);
//            mMixExecuteWidgets.mButtonNext.setBackgroundResource(R.drawable.btn_gray);
//            mMixExecuteWidgets.mButtonPre.setClickable(false);
//            mMixExecuteWidgets.mButtonPre.setBackgroundResource(R.drawable.btn_gray);
//            mMixExecuteWidgets.mAddComponentRelativeLayout.setClickable(false);
        }
        else {
            throw new KeyCharacterMap.UnavailableException("mMixExecuteWidgets is not init,it is null,so must init mMixExecuteWidgets firstly");
        }
    }

    private void initTaskMixCommonView(Object object){
        //part1
        if(object instanceof MixExecuteWidgets){
            Log.i(mTag,"init MixExecuteWidgets ");
            mMixExecuteWidgets.mCurrentStepTitle = (RevealTextView)this.findViewById(R.id.tech_task_part_1_title_1_text_view) ;
            mMixExecuteWidgets.mCurrentStepContent = (RevealTextView)this.findViewById(R.id.tech_task_part_1_title_2_text_view);
            mMixExecuteWidgets.mExecutingMan = (RevealTextView)this.findViewById(R.id.tech_task_part_1_title_3_text_view);
            mMixExecuteWidgets.mTimeBox =(Chronometer)this.findViewById(R.id.tech_task_part_1_title_4_text_view);
            mMixExecuteWidgets.mDoneImageView = (ImageView)this.findViewById(R.id.tech_task_part1_done_image_view);

            //part2
            mMixExecuteWidgets.mAddComponentRelativeLayout =(RelativeLayout)this.findViewById(R.id.tech_task_mix_part2_container_relative_layout);
            mMixExecuteWidgets.mComponentListView = (ListView)this.findViewById(R.id.tech_task_mix_part2_component_list_view);

            //part3
            mMixExecuteWidgets.mButtonPre = (Button)this.findViewById(R.id.tech_pre_task_button);
            mMixExecuteWidgets.mButtonNext = (Button)this.findViewById(R.id.tech_next_task_button);
            mMixExecuteWidgets.mButtonMixFunction = (Button)this.findViewById(R.id.tech_mix_func_button);

            mMixExecuteWidgets.mButtonPre.setOnClickListener(this);
            mMixExecuteWidgets.mButtonNext.setOnClickListener(this);
            mMixExecuteWidgets.mButtonMixFunction.setOnClickListener(this);
            mMixExecuteWidgets.mAddComponentRelativeLayout.setOnClickListener(this);

            mMixExecuteWidgets.mComponentModelList = new ArrayList<>();
            mMixExecuteWidgets.mComponentAdapter = new ComponentAdapter(TaskMixExecuteActivity.this,mMixExecuteWidgets.mComponentModelList);
            mMixExecuteWidgets.mComponentListView.setAdapter( mMixExecuteWidgets.mComponentAdapter);

        }
        else if(object instanceof MixDoneOrUnStartWidgets){
            Log.i(mTag,"init MixDoneOrUnStartWidgets ");
            //task_part1_description
            mMixDoneOrUnStartWidgets.mOrderSerialNum = (RevealTextView)this.findViewById(R.id.tech_task_part1_desc_title_1_text_view);
            mMixDoneOrUnStartWidgets.mStationJob = (RevealTextView)this.findViewById(R.id.tech_task_part1_desc_title_2_text_view);
            mMixDoneOrUnStartWidgets.mStation = (RevealTextView)this.findViewById(R.id.tech_task_part1_desc_title_3_text_view);
            mMixDoneOrUnStartWidgets.mRelativePerson =(RevealTextView)this.findViewById(R.id.tech_task_part1_desc_title_4_text_view);
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
            mMixExecuteWidgets.mButtonMixFunction.setText("完成");
            mMixExecuteWidgets.mAddComponentRelativeLayout.setClickable(true);
        }
    }


    private void initDoneCheckView(){
        if(mMixExecuteWidgets != null){
            initTaskMixCommonView(mMixExecuteWidgets);
            mMixExecuteWidgets.mDoneImageView.setVisibility(View.VISIBLE);
            mMixExecuteWidgets.mButtonMixFunction.setText("编辑");
            mMixExecuteWidgets.mAddComponentRelativeLayout.setVisibility(View.GONE);

        }
    }


    private void reeditTaskComponents(){
        TaskMixExecuteActivity.start(this,ActivityType.TYPE_DONE_VIEW,mIndex,mOrderSerialNum,mCurrentStep);
        mCanTouch = true;
    }

    private void startTask(){
        JSONArray array = new JSONArray();

        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,mAccessToken);
        map.put(RequestDataKey.SERIAL_NUM,mOrderSerialNum);
        map.put(RequestDataKey.CURRENT_STEP,mCurrentStep);
        map.put(RequestDataKey.SUB_STATE,"未开始");

        Message msg = mHandler.obtainMessage();
        HashMap<String,Object> respMap = NetOperationHelper.nextTask(map);
        if(respMap!=null){
            OrderDetailModel result = (OrderDetailModel)respMap.get(NetOperationHelper.KEY_RESULT);
            if (result != null){
                mHandler.sendEmptyMessage(MessageType.TYPE_START_TASK_SUCCESS);
            }
            else {
                String errorInfo = (String)respMap.get(NetOperationHelper.KEY_ERROR);

                msg.what = MessageType.TYPE_START_TASK_FAILED;
                msg.obj = errorInfo;
                mHandler.sendMessage(msg);
            }
        }
        else{
            msg.what = MessageType.TYPE_START_TASK_FAILED;
            msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
            mHandler.sendMessage(msg);
        }
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

    private void getNextTask(String step,String subState){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,mAccessToken);
        map.put(RequestDataKey.SERIAL_NUM,mOrderSerialNum);
        map.put(RequestDataKey.CURRENT_STEP,step);
        if(step.equals("0")){
            map.put(RequestDataKey.SUB_STATE,"未开始");
        }
        else{
            map.put(RequestDataKey.SUB_STATE,subState);
        }

        Message msg = mHandler.obtainMessage();
        HashMap<String,Object> respMap = NetOperationHelper.nextTask(map);
        if(respMap!=null){
            OrderDetailModel result = (OrderDetailModel)respMap.get(NetOperationHelper.KEY_RESULT);
            if (result != null){
                msg.obj = result;
                msg.what = MessageType.TYPE_NEXT_TASK_SUCCESS;
                mHandler.sendMessage(msg);
            }
            else {
                String errorInfo = (String)respMap.get(NetOperationHelper.KEY_ERROR);

                msg.what = MessageType.TYPE_NEXT_TASK_FAILED;
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

    private void getPreTask(String step){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,mAccessToken);
        map.put(RequestDataKey.SERIAL_NUM,mOrderSerialNum);
        map.put(RequestDataKey.CURRENT_STEP,step);

        Message msg = mHandler.obtainMessage();
        HashMap<String,Object> respMap = NetOperationHelper.preTask(map);
        if(respMap!=null){
            OrderDetailModel result = (OrderDetailModel)respMap.get(NetOperationHelper.KEY_RESULT);
            if (result != null){
                msg.obj = result;
                msg.what = MessageType.TYPE_PRE_TASK_SUCCESS;
                mHandler.sendMessage(msg);
            }
            else {
                String errorInfo = (String)respMap.get(NetOperationHelper.KEY_ERROR);
                msg.what = MessageType.TYPE_PRE_TASK_FAILED;
                msg.obj = errorInfo;
                mHandler.sendMessage(msg);
            }
        }
        else{
            msg.what = MessageType.TYPE_PRE_TASK_FAILED;
            msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
            mHandler.sendMessage(msg);
        }
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

    private void editComponents(String step){
        JSONArray array = new JSONArray();
        for(int i=0;i<mMixExecuteWidgets.mComponentModelList.size();i++)
        {
            JSONObject compoentJsonObject = new JSONObject();
            try{
                compoentJsonObject.put("title", mMixExecuteWidgets.mComponentModelList.get(i).getmComponentName());
                compoentJsonObject.put("num",mMixExecuteWidgets.mComponentModelList.get(i).getmComponentNum() );
                array.put(compoentJsonObject);
            }catch (Exception e){
                Log.e(mTag,"editComponents:" + e.toString());
            }
        }

        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,mAccessToken);
        map.put(RequestDataKey.SERIAL_NUM,mOrderSerialNum);
        map.put(RequestDataKey.CURRENT_STEP,step);
        map.put(RequestDataKey.COMPONENT_LIST,array);

        Message msg = mHandler.obtainMessage();
        HashMap<String,Object> respMap = NetOperationHelper.editComponents(map);
        if(respMap != null){
            String result = (String)respMap.get(NetOperationHelper.KEY_RESULT);
            if (result != null){
                mHandler.sendEmptyMessage(MessageType.TYPE_EDIT_COMPONENT_SUCCESS);
            }
            else {
                String errorInfo = (String)respMap.get(NetOperationHelper.KEY_ERROR);

                msg.what = MessageType.TYPE_EDIT_COMPONENT_FAILED;
                msg.obj = errorInfo;
                mHandler.sendMessage(msg);
            }
        }
        else{
            msg.what = MessageType.TYPE_EDIT_COMPONENT_FAILED;
            msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onBackEvent() {
        finish();
    }

//    @Override
//    public void finish() {
//        Log.i(mTag," on finish ");
//        Task task = new Task();
//        task.setIndex(mIndex);
//        task.setOrderSerialNum(mOrderSerialNum);
//        task.setOrderType(mOrderType.split(" ")[1]);
//        task.setOrderState(mOrderState);
//        task.setCurrentExecutingMan(mExecuteMan);
//        task.setOrderDate("134354123");
//
//        Intent intent = new Intent();
//        intent.putExtra("result", task);
//        setResult(RESULT_OK, intent);
//
//        super.finish();
//    }

//    public void removeItemFromList(int location){
//        Log.i(mTag, "mComponentModelList size: "+mMixExecuteWidgets.mComponentModelList.size() + "location:" + location);
//        if(mMixExecuteWidgets.mComponentModelList != null)
//        {
//            try{
//                mMixExecuteWidgets.mComponentModelList.get(location);
//            }catch (Exception e){
//                Log.e(mTag,e.toString());
//                return;
//            }
//            Log.i(mTag,"remove from list success");
//            mMixExecuteWidgets.mComponentModelList.remove(location);
//
//        }
//        else {
//            Log.e(mTag,"remove list location :" + location+ "failed");
//        }
//
//    }


    @Override
    protected void onResume() {
        super.onResume();
        startThreadToGetOrderData();
    }
}
