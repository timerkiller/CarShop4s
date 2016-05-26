package com.example.vke.shop4stech.fragment;

//import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.activity.HomeActivity;
import com.example.vke.shop4stech.activity.SignInActivity;
import com.example.vke.shop4stech.activity.TaskMixExecuteActivity;
import com.example.vke.shop4stech.adapter.TaskAdapter;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.constant.Prompt;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.constant.URL;
import com.example.vke.shop4stech.customLayout.XListView;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.helper.PreferencesHelper;
import com.example.vke.shop4stech.model.Task;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by vke on 2016/5/8.
 */
public class TaskFragment extends ListFragment
        implements XListView.IXListViewListener {

    private static final int FIRST_PAGE=1;

    private static final String mTag = "TaskFragment";

    private static final String PER_PAGE = "20";
    private TaskAdapter mTaskAdapter;
    private  List<Task> mTotalTaskList;
    private int mPageId = 2;
    private int mTotalPage = 0;
    private Handler mGetTaskHandler ;
    private boolean mIsUpdateOngoing = false;
    HomeActivity mParentActivity;
    private int mListClickItemLocation = 0;

    class OPERATION_TYPE{
        public static final int TYPE_UPDATE = 0x100;
        public static final int TYPE_LOAD_MORE = 0x101;
    }

    public static TaskFragment newInstance() {

        Bundle args = new Bundle();
        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity =(HomeActivity)getActivity();
        mTaskAdapter = new TaskAdapter(getActivity(),mTotalTaskList);
        setListAdapter(mTaskAdapter);
        initHandler();
        mParentActivity.setContentViewVisibility(false);
        mParentActivity.setAnimateFlag(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getListView() != null) {
            XListView xListView = (XListView) getListView();
            xListView.setXListViewListener(this);
            xListView.setPullLoadEnable(true);
            xListView.setPullRefreshEnable(true);
        }
        mIsUpdateOngoing = true;
        if(mGetTaskHandler == null){
            initHandler();

        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                getTask(OPERATION_TYPE.TYPE_UPDATE,FIRST_PAGE);
            }
        }).start();
    }

    @Override
    public void onPause() {
        Log.i(mTag,"onPause Enter");
        mPageId = 2;

        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i(mTag,"onResume Enter");
        super.onResume();
        if(mGetTaskHandler == null){
            initHandler();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                getTask(OPERATION_TYPE.TYPE_UPDATE,FIRST_PAGE);
            }
        }).start();
    }

    private void initHandler(){
        mGetTaskHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.i(mTag,"Thread :ID:"+Thread.currentThread().getId() +" Thread Name: "+ Thread.currentThread().getName());
                mParentActivity.setContentViewVisibility(true);

                try{
                    mIsUpdateOngoing = false;
                    XListView xListView = (XListView)getListView();
                    //只有一页数据的时候，不显示下拉框
                    if(mTotalPage == 1){
                        xListView.setPullLoadEnable(false);
                    }
                    else if(mTotalPage > 1){
                        xListView.setPullLoadEnable(true);
                    }

                    TextView textView = (TextView)xListView.findViewById(R.id.xlistview_footer_hint_textview);
                    switch (msg.what){
                        case MessageType.TYPE_LOAD_MORE_SUCCESS:
                            mergeTasksToTotalList((List<Task>)msg.obj);//loadmore需要将数据合并到list尾部
                            mPageId++;
                            mTaskAdapter.bindData(mTotalTaskList);
                            mTaskAdapter.notifyDataSetChanged();
                            onLoadFinish(true);
                            if(mTotalTaskList != null && mTotalTaskList.size() != 0 ){
                                textView.setText(R.string.tech_load_more_data);
                            }
                            else{
                                textView.setText(R.string.tech_no_data);
                            }

                            break;
                        case MessageType.TYPE_UPDATE_SUCCESS:
                            mTotalTaskList = (List<Task>)msg.obj;//更新的话就直接替换
                            mTaskAdapter.bindData(mTotalTaskList);
                            mTaskAdapter.notifyDataSetChanged();
                            onLoadFinish(true);
                            if(mTotalTaskList != null && mTotalTaskList.size() != 0 ){
                                textView.setText(R.string.tech_load_more_data);
                            }
                            else{
                                textView.setText(R.string.tech_no_data);
                            }

                            break;
                        case MessageType.TYPE_SERVER_NOT_AVAILABLE:
                            Toast.makeText(getActivity().getApplicationContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                            onLoadFinish(false);
                            break;
                        case MessageType.TYPE_ACCESS_TOKEN_INVALID:
                            String tip = (String)msg.obj;
                            Toast.makeText(getActivity().getApplicationContext(),tip,Toast.LENGTH_SHORT).show();
                            signOut();
                            break;
                        case MessageType.TYPE_UPDATE_FAILED:
                        case MessageType.TYPE_LOAD_MORE_FAILED:
                            Toast.makeText(getActivity().getApplicationContext(),Prompt.PROMPT_LOAD_FAILED,Toast.LENGTH_SHORT).show();
                            onLoadFinish(false);
                            break;
                        case MessageType.TYPE_NETWORK_DISABLE:
                            Toast.makeText(getActivity(),R.string.tech_network_unuseful,Toast.LENGTH_SHORT).show();
                            onLoadFinish(false);
                            break;
                        case MessageType.TYPE_NO_DATA_FOUND:
                            textView.setText(R.string.tech_no_data);
                            Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                            onLoadFinish(false);
                            break;
                        default:
                            Log.e(mTag,"Unknow message type: " + msg.what);
                    }
                }
                catch (Exception e){
                    Log.e(mTag,e.toString());
                }
            }
        };
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(mTag,">>>>>>>>>on list item click");
        super.onListItemClick(l, v, position, id);
        TextView indexTextView = (TextView)v.findViewById(R.id.tech_index);
        TextView orderSerialNumTextView = (TextView)v.findViewById(R.id.tech_order_serial_num_text_view);
        TextView currentStateTextView = (TextView)v.findViewById(R.id.tech_task_state_text_view);
        if(indexTextView == null || orderSerialNumTextView == null || currentStateTextView == null){
            Log.w(mTag,"Head list click or footer item clicked");
            return;
        }

        //记录被点击的item，若该task状态发生改变时，需要在taskFragment更新下数据。
        mListClickItemLocation = position;
        String orderSerialNum = orderSerialNumTextView.getText().toString().split(" ")[1];//订单号: WX20150530123455  获取后面的订单号
//        Intent starter = new Intent(getActivity(), TaskMixExecuteActivity.class);
//        starter.putExtra(TaskMixExecuteActivity.KEY_INDEX,indexTextView.getText().toString());
//        starter.putExtra(TaskMixExecuteActivity.KEY_ORDER_SERIAL_NUM,orderSerialNum);
        switch (currentStateTextView.getText().toString()){
            case "暂停":

//                starter.putExtra(TaskMixExecuteActivity.KEY_ACTIVITY_TYPE,TaskMixExecuteActivity.ActivityType.TYPE_PAUSE);
//                startActivityForResult(starter,0);
                TaskMixExecuteActivity.start(getActivity(),TaskMixExecuteActivity.ActivityType.TYPE_PAUSE,indexTextView.getText().toString(),orderSerialNum);
                break;
            case "未开始":
//                starter.putExtra(TaskMixExecuteActivity.KEY_ACTIVITY_TYPE,TaskMixExecuteActivity.ActivityType.TYPE_UNSTART);
//                startActivityForResult(starter,0);
                TaskMixExecuteActivity.start(getActivity(),TaskMixExecuteActivity.ActivityType.TYPE_UNSTART,indexTextView.getText().toString(),orderSerialNum);
                break;
            case "执行中":
//                starter.putExtra(TaskMixExecuteActivity.KEY_ACTIVITY_TYPE,TaskMixExecuteActivity.ActivityType.TYPE_EXECUTING);
//                startActivityForResult(starter,0);
                TaskMixExecuteActivity.start(getActivity(),TaskMixExecuteActivity.ActivityType.TYPE_EXECUTING,indexTextView.getText().toString(),orderSerialNum);
                break;
            case "待评价":
            case "完成":
//                starter.putExtra(TaskMixExecuteActivity.KEY_ACTIVITY_TYPE,TaskMixExecuteActivity.ActivityType.TYPE_DONE);
//                startActivityForResult(starter,0);
                TaskMixExecuteActivity.start(getActivity(),TaskMixExecuteActivity.ActivityType.TYPE_DONE,indexTextView.getText().toString(),orderSerialNum);
                break;
        }
    }

    private void onLoadFinish(boolean updateTime) {
        XListView view = (XListView)getListView();
        if(view == null){
            return;
        }
        view.stopRefresh();
        view.stopLoadMore();
        if (updateTime){
            SimpleDateFormat sDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String date = sDateFormat.format(new java.util.Date());
            view.setRefreshTime(date);
        }
    }

    void getTask(int type,int pageId){
        if(mGetTaskHandler == null){
            Log.e(mTag,"mGetTaskHandler no init yet");
            return;
        }

        if(!NetOperationHelper.isNetworkConnected(getActivity())){
            mGetTaskHandler.sendEmptyMessage(MessageType.TYPE_NETWORK_DISABLE);
            return;
        }


        String accessToken = NetOperationHelper.getValidAccessToken(getActivity());
        if (accessToken == null){
            Log.i(mTag,"net work unAvailable");
            Message msg = mGetTaskHandler.obtainMessage();
            msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
            msg.what = MessageType.TYPE_SERVER_NOT_AVAILABLE;
            mGetTaskHandler.sendMessage(msg);
            return ;
        }
        else if(accessToken.equals("failed")){
            Message msg = mGetTaskHandler.obtainMessage();
            msg.obj = Prompt.PROMPT_ACCESS_TOKEN_INVALID;
            msg.what = MessageType.TYPE_ACCESS_TOKEN_INVALID;
            mGetTaskHandler.sendMessage(msg);
            return;
        }

        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,accessToken);
        map.put(RequestDataKey.INFO, "list");
        map.put(RequestDataKey.TYPE, "all");
        map.put(RequestDataKey.PAGE, Integer.toString(pageId));
        map.put(RequestDataKey.PER_PAGE, PER_PAGE);

        //从服务器获取任务数据
        HashMap<String,Object> dataMap = NetOperationHelper.getTaskList(map);
        try{
            if(dataMap != null){
                List<Task> tasks = (List<Task>)dataMap.get(NetOperationHelper.KEY_TASKS);
                if(tasks != null && tasks.size() !=0 ){
                    mTotalPage = (int)dataMap.get(NetOperationHelper.KEY_TOTAL_PAGE);
                    Message msg = mGetTaskHandler.obtainMessage();
                    if(type == OPERATION_TYPE.TYPE_UPDATE){
                        //mTotalTaskList = tasks;
                        msg.obj = tasks;
                        msg.what = MessageType.TYPE_UPDATE_SUCCESS;
                    }
                    else {
                        msg.obj = tasks;//mergeTasksToTotalList(tasks);
                        msg.what = MessageType.TYPE_LOAD_MORE_SUCCESS;
                    }
                    mGetTaskHandler.sendMessage(msg);
                }
                else{
                    if(tasks != null && tasks.size() == 0){
                        Message msg = mGetTaskHandler.obtainMessage();
                        msg.what = MessageType.TYPE_NO_DATA_FOUND;
                        msg.obj = Prompt.PROMPT_ORDER_NOT_EXIST;
                        mGetTaskHandler.sendMessage(msg);
                        return;
                    }
                    else{
                        String errorInfo = (String)dataMap.get(NetOperationHelper.KEY_ERROR);
                        if(errorInfo != null){
                            Message msg = mGetTaskHandler.obtainMessage();
                            msg.what = MessageType.TYPE_NO_DATA_FOUND;
                            msg.obj = errorInfo;
                            mGetTaskHandler.sendMessage(msg);
                            return;
                        }
                    }


                    //这里应该不会再跳到了，是否考虑删除
                    if(type == OPERATION_TYPE.TYPE_UPDATE){
                        mGetTaskHandler.sendEmptyMessage(MessageType.TYPE_UPDATE_FAILED);
                    }
                    else {
                        mGetTaskHandler.sendEmptyMessage(MessageType.TYPE_LOAD_MORE_FAILED);
                    }
                }
            }
            else{
                mGetTaskHandler.sendEmptyMessage(MessageType.TYPE_NO_DATA_FOUND);
            }
        }catch (Exception e){
            Log.w(mTag,e.toString());
        }
    }

    private void mergeTasksToTotalList(List<Task> tasksList){

        for(int i = 0; i<tasksList.size();i++) {
            mTotalTaskList.add(tasksList.get(i));
        }
    }

    private void signOut(){
        Log.i(mTag,"signOut");

        if(mTotalTaskList != null){
            mTotalTaskList.clear();
        }

        PreferencesHelper.signOut(getActivity());
        SignInActivity.startWithNoAnimate(getActivity());
        ActivityCompat.finishAfterTransition(getActivity());
    }


    @Override
    public void onStart() {
        Log.i(mTag,"onStart enter");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i(mTag,"onStop enter");
        super.onStop();
    }

    //更新数据
    @Override
    public void onRefresh() {
        if(mIsUpdateOngoing){
            return;
        }
        mIsUpdateOngoing = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getTask(OPERATION_TYPE.TYPE_UPDATE,FIRST_PAGE);
                mPageId = 2;//刷新后需要重置mPageID
            }
        }).start();

    }
    //加载数据
    @Override
    public void onLoadMore() {
        //判断当前加载的页数是否和服务器上的总页数相等，若相等了就不进行加载了，提示没有数据了
        if(mPageId > mTotalPage)
        {
            Toast.makeText(getActivity().getApplicationContext(),Prompt.PROMPT_NO_MORE_DATA,Toast.LENGTH_SHORT).show();
            onLoadFinish(false);
            return;
        }

        if(mIsUpdateOngoing){
            return;
        }

        mIsUpdateOngoing = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getTask(OPERATION_TYPE.TYPE_LOAD_MORE,mPageId);
            }
        }).start();
    }

    //这里需要
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(mTag,"onDestroyView invoked");
        mGetTaskHandler = null;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.i(mTag,"on ActivityResult requestCode:" + requestCode + "resultCode" + resultCode);
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 0){
//            Task task = data.getParcelableExtra("result");
//            mTotalTaskList.remove(mListClickItemLocation-1);
//            mTotalTaskList.set(mListClickItemLocation-1,task);
//            mTaskAdapter.bindData(mTotalTaskList);
//            mTaskAdapter.notifyDataSetChanged();
//
//        }
//
//    }


}
