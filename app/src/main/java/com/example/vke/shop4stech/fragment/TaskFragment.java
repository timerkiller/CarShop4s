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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by vke on 2016/5/8.
 */
public class TaskFragment extends ListFragment
        implements XListView.IXListViewListener {

    private static final String mTag = "TaskFragment";

    private TaskAdapter mTaskAdapter;
    private static List<Task> mTotalTaskList;
    private int mPageId = 1;
    private Handler mGetTaskHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MessageType.TYPE_GET_TASK_SUCCESS:
                    mTaskAdapter.bindData(mTotalTaskList);
                    mTaskAdapter.notifyDataSetChanged();
                    mPageId++;
                    onLoad();

                    break;
                case MessageType.TYPE_ACCESS_TOKEN_INVALID:
                    String tip = (String)msg.obj;
                    Toast.makeText(getActivity().getApplicationContext(),tip,Toast.LENGTH_SHORT).show();
                    signOut();

                    break;
                case MessageType.TYPE_GET_TASK_FAILED:
                    break;
                default:
                    Log.e(mTag,"Unknow message type: " + msg.what);
            }
        }
    };


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
                getTask();
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
//        getListView().setOnScrollListener(this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    private void onLoad() {
        XListView view = (XListView)getListView();
        view.stopRefresh();
        view.stopLoadMore();
        view.setRefreshTime("刚刚");
    }


    void getTask(){
        String accessToken = PreferencesHelper.getPreferenceAccessToken(getActivity());
        if (accessToken == null){
            Message msg = mGetTaskHandler.obtainMessage();
            msg.obj = "AccessToken失效，请重新登陆";
            msg.what = MessageType.TYPE_ACCESS_TOKEN_INVALID;
            mGetTaskHandler.sendMessage(msg);;
        }

        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,accessToken);
        map.put("info", "list");
        map.put("type", "all");
        map.put("page", Integer.toString(mPageId));
        map.put("perPage", "2");


        List<Task> tasks = NetOperationHelper.getTaskList(map);
        if(tasks != null){
            mTotalTaskList = tasks;
            Message msg = mGetTaskHandler.obtainMessage();
            //msg.obj = tasks;
            msg.what = MessageType.TYPE_GET_TASK_SUCCESS;
            mGetTaskHandler.sendMessage(msg);
        }
        else{
            //这里还需要考虑，有可能是ACCESSToken失效，还有其他failed的情况
            mGetTaskHandler.sendEmptyMessage(MessageType.TYPE_GET_TASK_FAILED);
        }
    }

    private void signOut(){
        Log.i(mTag,"signOut");
        PreferencesHelper.signOut(getActivity());
        SignInActivity.startWithNoAnimate(getActivity());
        ActivityCompat.finishAfterTransition(getActivity());
    }

//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        if (is_scrolling && AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    getTask();
//                }
//            }).start();
//        }
//    }
//
//    private boolean is_scrolling;
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        // TODO Auto-generated method stub
//        if (firstVisibleItem + visibleItemCount == totalItemCount
//                && totalItemCount != 0) {
//            is_scrolling = true;
//        } else {
//            is_scrolling = false;
//        }
//    }


    //更新数据
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getTask();
            }
        }).start();

        //onLoad();
    }
    //加载数据
    @Override
    public void onLoadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getTask();
            }
        }).start();
    }
}
