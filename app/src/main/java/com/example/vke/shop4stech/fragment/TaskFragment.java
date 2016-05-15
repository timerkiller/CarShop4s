package com.example.vke.shop4stech.fragment;

//import android.app.ListFragment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.activity.SignInActivity;
import com.example.vke.shop4stech.adapter.TaskAdapter;
import com.example.vke.shop4stech.constant.MessageType;
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

    private TaskAdapter mTaskAdapter;
    private  List<Task> mTotalTaskList;
    private int mPageId = 1;
    private int mTotalPage = 0;
    private Handler mGetTaskHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            TextView textView = (TextView)getListView().findViewById(R.id.xlistview_footer_hint_textview);
            switch (msg.what){
                case MessageType.TYPE_GET_TASK_LOAD_MORE_SUCCESS:
                    mPageId++;

                case MessageType.TYPE_GET_TASK_UPDATE_SUCCESS:
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
                case MessageType.TYPE_ACCESS_TOKEN_INVALID:
                    String tip = (String)msg.obj;
                    Toast.makeText(getActivity().getApplicationContext(),tip,Toast.LENGTH_SHORT).show();
                    signOut();

                    break;
                case MessageType.TYPE_GET_TASK_UPDATE_FAILED:
                case MessageType.TYPE_GET_TASK_LOAD_MORE_FAILED:
                    Toast.makeText(getActivity().getApplicationContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    onLoadFinish(false);
                    break;
                case MessageType.TYPE_NETWORK_DISABLE:
                    Toast.makeText(getActivity(),R.string.tech_network_unuseful,Toast.LENGTH_SHORT).show();
                    onLoadFinish(false);
                    break;
                case MessageType.TYPE_NO_DATA_FOUND:
                    textView.setText(R.string.tech_no_data);
                    //Toast.makeText(getActivity(),R.string.tech_no_data,Toast.LENGTH_SHORT).show();
                    onLoadFinish(false);
                    break;
                default:
                    Log.e(mTag,"Unknow message type: " + msg.what);
            }
        }
    };

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
        mTaskAdapter = new TaskAdapter(getActivity(),mTotalTaskList);
        setListAdapter(mTaskAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_list,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        XListView xListView  = (XListView)getListView();
        xListView.setXListViewListener(this);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);

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
        mPageId = 1;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    private void onLoadFinish(boolean updateTime) {
        XListView view = (XListView)getListView();
        view.stopRefresh();
        view.stopLoadMore();
        if (updateTime){
            SimpleDateFormat sDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String date = sDateFormat.format(new java.util.Date());
            view.setRefreshTime(date);
        }
    }

    void getTask(int type,int pageId){
        if(!NetOperationHelper.isNetworkConnected(getActivity())){
            mGetTaskHandler.sendEmptyMessage(MessageType.TYPE_NETWORK_DISABLE);
            return;
        }

        String accessToken = getValidAccessToken();
        if (accessToken == null){
            Message msg = mGetTaskHandler.obtainMessage();
            msg.obj = "AccessToken失效，请重新登陆";
            msg.what = MessageType.TYPE_ACCESS_TOKEN_INVALID;
            mGetTaskHandler.sendMessage(msg);;
            return ;
        }

        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,accessToken);
        map.put("info", "list");
        map.put("type", "all");
        map.put("page", Integer.toString(pageId));
        map.put("perPage", "4");


        //从服务器获取任务数据
        HashMap<String,Object> dataMap = NetOperationHelper.getTaskList(map);
        try{
            if(dataMap != null){
                List<Task> tasks = (List<Task>)dataMap.get("tasks");
                mTotalPage = (int)dataMap.get("pageAll");
                if(tasks != null && tasks.size() !=0 ){
                    Message msg = mGetTaskHandler.obtainMessage();
                    if(type == OPERATION_TYPE.TYPE_UPDATE){
                        mTotalTaskList = tasks;
                        msg.what = MessageType.TYPE_GET_TASK_UPDATE_SUCCESS;
                    }
                    else {
                        mergeTasksToTotalList(tasks);
                        msg.what = MessageType.TYPE_GET_TASK_LOAD_MORE_SUCCESS;
                    }

                    mGetTaskHandler.sendMessage(msg);
                }
                else{
                    if(tasks != null && tasks.size() == 0){
                        mGetTaskHandler.sendEmptyMessage(MessageType.TYPE_NO_DATA_FOUND);
                        return;
                    }

                    //这里还需要考虑，有可能是ACCESSToken失效，还有其他failed的情况
                    if(type == OPERATION_TYPE.TYPE_UPDATE){
                        mGetTaskHandler.sendEmptyMessage(MessageType.TYPE_GET_TASK_UPDATE_FAILED);
                    }
                    else {
                        mGetTaskHandler.sendEmptyMessage(MessageType.TYPE_GET_TASK_LOAD_MORE_FAILED);
                    }
                }
            }
            else{
                mGetTaskHandler.sendEmptyMessage(MessageType.TYPE_NO_DATA_FOUND);
            }
        }catch (Exception e){
            Log.e(mTag,e.toString());
        }
    }

    //获取有效的accessToken，这里会进行一个网络操作判断
    private String getValidAccessToken(){
        String accessToken = PreferencesHelper.getPreferenceAccessToken(getActivity());
        if(NetOperationHelper.checkAccessTokenInvalid(accessToken)){
            return accessToken;
        }
        return null;
    }

    private void mergeTasksToTotalList(List<Task> tasksList){
        for(int i = 0; i<tasksList.size();i++) {
            mTotalTaskList.add(tasksList.get(i));
        }
    }

    private void signOut(){
        Log.i(mTag,"signOut");

        mTotalTaskList.clear();
        PreferencesHelper.signOut(getActivity());
        SignInActivity.startWithNoAnimate(getActivity());
        ActivityCompat.finishAfterTransition(getActivity());
    }

    //更新数据
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getTask(OPERATION_TYPE.TYPE_UPDATE,FIRST_PAGE);
                mPageId = 1;//刷新后需要重置mPageID
            }
        }).start();

    }
    //加载数据
    @Override
    public void onLoadMore() {
        //判断当前加载的页数是否和服务器上的总页数相等，若相等了就不进行加载了，提示没有数据了
        if(mPageId == mTotalPage)
        {
            Toast.makeText(getActivity().getApplicationContext(),"没有更多了",Toast.LENGTH_SHORT).show();
            onLoadFinish(false);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                getTask(OPERATION_TYPE.TYPE_LOAD_MORE,mPageId);
            }
        }).start();
    }
}
