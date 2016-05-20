package com.example.vke.shop4stech.fragment;

//import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.activity.SignInActivity;
import com.example.vke.shop4stech.adapter.MessageAdapter;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.customLayout.XListView;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.helper.PreferencesHelper;
import com.example.vke.shop4stech.model.User;
import com.example.vke.shop4stech.model.UserMessage;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

/**
 * Created by vke on 2016/5/8.
 */
public class MessageFragment extends ListFragment
implements XListView.IXListViewListener,View.OnLongClickListener{
    private static final String mTag = "MessageFragment";
    private List<UserMessage> mTotalMessageList;
    private MessageAdapter mMessageAdapter;
    private static final int FIRST_PAGE =1;
    private static final int PER_PAGE = 10;
    private int mPageId = 2;
    private int mTotalPage;

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    class OPERATION_TYPE{
        public static final int TYPE_UPDATE = 0x100;
        public static final int TYPE_LOAD_MORE = 0x101;
    }

    private Handler mUserMessageHandler;

    public static MessageFragment newInstance() {
        Bundle args = new Bundle();

        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void signOut(){
        Log.i(mTag,"signOut");

        if(mTotalMessageList != null){
            mTotalMessageList.clear();
        }

        PreferencesHelper.signOut(getActivity());
        SignInActivity.startWithNoAnimate(getActivity());
        ActivityCompat.finishAfterTransition(getActivity());
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageAdapter = new MessageAdapter(getActivity(),mTotalMessageList);
        setListAdapter(mMessageAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message,container,false);
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
                getMessage(OPERATION_TYPE.TYPE_UPDATE,FIRST_PAGE);
            }
        }).start();

    }

    /*
    * get messages from server
    * */
    private void getMessage(int operationType,int pageId){
        if(!NetOperationHelper.isNetworkConnected(getActivity())){
            mUserMessageHandler.sendEmptyMessage(MessageType.TYPE_NETWORK_DISABLE);
            return;
        }

        String accessToken = getValidAccessToken();
        if (accessToken == null){
            Message msg = mUserMessageHandler.obtainMessage();
            msg.obj = "AccessToken失效，请重新登陆";
            msg.what = MessageType.TYPE_ACCESS_TOKEN_INVALID;
            mUserMessageHandler.sendMessage(msg);;
            return ;
        }

        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,accessToken);
        map.put(RequestDataKey.INFO, "list");
        map.put(RequestDataKey.PAGE, Integer.toString(pageId));
        map.put(RequestDataKey.PER_PAGE, Integer.toString(PER_PAGE));


        //从服务器获取任务数据
        HashMap<String,Object> dataMap = NetOperationHelper.getUserMessage(map);
        try{
            if(dataMap != null){
                List<UserMessage> messages = (List<UserMessage>)dataMap.get("messages");
                if(messages != null && messages.size() !=0 ){
                    mTotalPage = (int)dataMap.get("pageAll");
                    Message msg = mUserMessageHandler.obtainMessage();
                    if(operationType == OPERATION_TYPE.TYPE_UPDATE){
                        msg.obj = messages;
                        msg.what = MessageType.TYPE_UPDATE_SUCCESS;
                    }
                    else {
                        msg.obj = messages;//mergeToTotalList(messages);消息体更新需要放到主线程里，否则会引起多次loadmore的时候线程不同步，导致程序奔溃
                        msg.what = MessageType.TYPE_LOAD_MORE_SUCCESS;
                    }

                    mUserMessageHandler.sendMessage(msg);
                }
                else{
                    if(messages != null && messages.size() == 0){
                        mUserMessageHandler.sendEmptyMessage(MessageType.TYPE_NO_DATA_FOUND);
                        return;
                    }

                    //这里还需要考虑，有可能是ACCESSToken失效，还有其他failed的情况
                    if(operationType == OPERATION_TYPE.TYPE_UPDATE){
                        mUserMessageHandler.sendEmptyMessage(MessageType.TYPE_UPDATE_FAILED);
                    }
                    else {
                        mUserMessageHandler.sendEmptyMessage(MessageType.TYPE_LOAD_MORE_FAILED);
                    }
                }
            }
            else{
                mUserMessageHandler.sendEmptyMessage(MessageType.TYPE_NO_DATA_FOUND);
            }
        }catch (Exception e){
            Log.e(mTag,e.toString());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mUserMessageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

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
                        //读取更多时，将数据merge到原来的list里
                        mergeToTotalList((List<UserMessage>)msg.obj);
                        mMessageAdapter.bindData(mTotalMessageList);
                        mMessageAdapter.notifyDataSetChanged();
                        onLoadFinish(true);
                        if(mTotalMessageList != null && mTotalMessageList.size() != 0 ){
                            textView.setText(R.string.tech_load_more_data);
                        }
                        else{
                            textView.setText(R.string.tech_no_data);
                        }

                        mPageId++;
                        break;
                    case MessageType.TYPE_UPDATE_SUCCESS:
                        //更新时，直接替换数据
                        mTotalMessageList = (List<UserMessage>)msg.obj;
                        mMessageAdapter.bindData(mTotalMessageList);
                        mMessageAdapter.notifyDataSetChanged();
                        onLoadFinish(true);
                        if(mTotalMessageList != null && mTotalMessageList.size() != 0 ){
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
                    case MessageType.TYPE_UPDATE_FAILED:
                    case MessageType.TYPE_LOAD_MORE_FAILED:
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mPageId = 2;
    }

    //获取有效的accessToken，这里会进行一个网络操作判断
    private String getValidAccessToken(){
        String accessToken = PreferencesHelper.getPreferenceAccessToken(getActivity());
        if(NetOperationHelper.checkAccessTokenInvalid(accessToken)){
            return accessToken;
        }
        return null;
    }

    private void mergeToTotalList(List<UserMessage> messageList){
        for(int i = 0; i<messageList.size();i++) {
            mTotalMessageList.add(messageList.get(i));
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(mTag,">>>>>>onListItemClick");
        super.onListItemClick(l, v, position, id);

        Log.i(mTag,"after super invoked");
        TextView contentView = (TextView)v.findViewById(R.id.tech_message_content_text_view);
        if(contentView == null){
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("消息");
        builder.setMessage(contentView.getText().toString());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        }).show();

    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                getMessage(OPERATION_TYPE.TYPE_UPDATE,FIRST_PAGE);
                mPageId = 2;
            }
        }).start();
    }

    @Override
    public void onLoadMore() {

        //判断当前加载的页数是否和服务器上的总页数相等，若相等了就不进行加载了，提示没有数据了
        if(mPageId > mTotalPage)
        {
            Toast.makeText(getActivity().getApplicationContext(),"没有更多了",Toast.LENGTH_SHORT).show();
            onLoadFinish(false);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                getMessage(OPERATION_TYPE.TYPE_LOAD_MORE,mPageId);
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserMessageHandler = null;
    }

}
