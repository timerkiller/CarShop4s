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
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.activity.HomeActivity;
import com.example.vke.shop4stech.activity.SignInActivity;
import com.example.vke.shop4stech.adapter.MessageAdapter;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.constant.Prompt;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.customLayout.XListView;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.helper.PreferencesHelper;
import com.example.vke.shop4stech.model.User;
import com.example.vke.shop4stech.model.UserMessage;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

/**
 * Created by vke on 2016/5/8.
 */
public class MessageFragment extends ListFragment
implements XListView.IXListViewListener{
    private static final String mTag = "MessageFragment";
    private List<UserMessage> mTotalMessageList;
    private MessageAdapter mMessageAdapter;
    HomeActivity mParentActivity;
    private static final int FIRST_PAGE =1;
    private static final int PER_PAGE = 40;
    private int mPageId = 2;
    private int mTotalPage;

    private TextView mDeleteTipTextView;
    private ProgressBar mDeletePorcessBar;


    class OPERATION_TYPE{
        public static final int TYPE_UPDATE = 0x100;
        public static final int TYPE_LOAD_MORE = 0x101;
    }

    private Handler mUserMessageHandler;

    public void showDeleteWidgets(boolean flag){
        if(flag){
            mDeleteTipTextView.setVisibility(View.VISIBLE);
            mDeletePorcessBar.setVisibility(View.VISIBLE);
        }
        else {
            mDeleteTipTextView.setVisibility(View.GONE);
            mDeletePorcessBar.setVisibility(View.GONE);
        }
    }

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
        mParentActivity=(HomeActivity)getActivity();

        mMessageAdapter = new MessageAdapter(getActivity(),mTotalMessageList);
        setListAdapter(mMessageAdapter);
        initHandler();
        if(mParentActivity != null){
            mParentActivity.setContentViewVisibility(false);
            mParentActivity.setAnimateFlag(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_message,container,false);
        mDeleteTipTextView = (TextView)view.findViewById(R.id.tech_delete_tip_text_view);
        mDeletePorcessBar = (ProgressBar)view.findViewById(R.id.tech_delete_message_process_bar);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(mTag,">>>>>>>>>>>>>onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        final XListView xListView  = (XListView)getListView();
        xListView.setXListViewListener(this);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        //xListView.setOnItemLongClickListener(this);
        xListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        xListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedCount = xListView.getCheckedItemCount();
                mode.setTitle(checkedCount + " selected");
                mMessageAdapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if(mParentActivity != null) {
                    mParentActivity.setWidgetsClickable(false);
                }
                mMessageAdapter.setNeedAnimate(true);
                mMessageAdapter.setItemMultiCheckable(true);
                mode.getMenuInflater().inflate(R.menu.delete_action_mode, menu);
                xListView.setPullLoadEnable(false);
                xListView.setPullRefreshEnable(false);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_mode:
                        if(!NetOperationHelper.isNetworkConnected(getActivity())){
                            Toast.makeText(getActivity().getApplicationContext(),R.string.tech_network_unuseful,Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        showDeleteWidgets(true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SparseBooleanArray selected = mMessageAdapter.getSelectedIds();
                                String accessToken = PreferencesHelper.getPreferenceAccessToken(getActivity());
                                String result = NetOperationHelper.checkAccessTokenInvalid(accessToken);
                                if(!result.equals("ok")){
                                    Message msg = mUserMessageHandler.obtainMessage();
                                    msg.what = MessageType.TYPE_DELETE_MESSAGE_FAILED;
                                    msg.obj = result;
                                    mUserMessageHandler.sendMessage(msg);
                                    return;
                                }

                                Log.i(mTag,"after check access token size:" +selected.size());
                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                    UserMessage selectedItem = (UserMessage)mMessageAdapter.getItem(selected.keyAt(i)-1);
                                    HashMap<String,Object> map = new HashMap<String, Object>();
                                    map.put(RequestDataKey.ACCESS_TOKEN,accessToken);
                                    map.put(RequestDataKey.INFO,"delete");
                                    map.put(RequestDataKey.INDEX,selectedItem.getIndex());
                                    String ret = NetOperationHelper.removeMessage(map);
                                    if(!ret.equals("ok")){
                                        Message msg = mUserMessageHandler.obtainMessage();
                                        msg.what = MessageType.TYPE_DELETE_MESSAGE_FAILED;
                                        msg.obj = result;
                                        mUserMessageHandler.sendMessage(msg);
                                        return;
                                    }

                                    mMessageAdapter.remove(selected.keyAt(i)-1);
                                }
                                mUserMessageHandler.sendEmptyMessage(MessageType.TYPE_DELETE_MESSAGE_SUCCESS);
                            }
                        }).start();

                        mode.setTag("done");
                        mode.finish();
                        return true;
                    case android.R.id.home:
                        Log.i(mTag,"onBack click");

                    default:
                        return false;
                }
            }

            //退出删除模式处理
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                String tag = (String)mode.getTag();
                if(tag != null && tag.equals("done")){
                    Log.i(mTag,"end with mode finish");
                }
                else{
                    resumeWidgets(xListView);
                }
                mMessageAdapter.setItemMultiCheckable(false);
            }

        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                getMessage(OPERATION_TYPE.TYPE_UPDATE,FIRST_PAGE);
            }
        }).start();

    }

    void resumeWidgets(XListView xListView){
        mMessageAdapter.dataChange();
        if(mParentActivity != null) {
            mParentActivity.setWidgetsClickable(true);
        }
        xListView.setPullRefreshEnable(true);
        if(mTotalPage > 1){
            xListView.setPullLoadEnable(true);
        }
        else {
            xListView.setPullLoadEnable(false);
        }
    }

    /*
    * get messages from server
    * */
    private void getMessage(int operationType,int pageId){
        if(mUserMessageHandler == null){
            Log.e(mTag,"mGetTaskHandler no init yet");
            return;
        }

        if(!NetOperationHelper.isNetworkConnected(getActivity())){
            mUserMessageHandler.sendEmptyMessage(MessageType.TYPE_NETWORK_DISABLE);
            return;
        }

        String accessToken = NetOperationHelper.getValidAccessToken(getActivity());
        if (accessToken == null){//服务器不可用
            Log.w(mTag,"Net server  unavailable");
            Message msg = mUserMessageHandler.obtainMessage();
            msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
            msg.what = MessageType.TYPE_SERVER_NOT_AVAILABLE;
            mUserMessageHandler.sendMessage(msg);
            return ;
        }
        else if(accessToken.equals("failed")){
            Message msg = mUserMessageHandler.obtainMessage();
            msg.obj = Prompt.PROMPT_ACCESS_TOKEN_INVALID;
            msg.what = MessageType.TYPE_ACCESS_TOKEN_INVALID;
            mUserMessageHandler.sendMessage(msg);
            return;
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
                List<UserMessage> messages = (List<UserMessage>)dataMap.get(NetOperationHelper.KEY_MESSAGES);
                if(messages != null && messages.size() !=0 ){
                    mTotalPage = (int)dataMap.get(NetOperationHelper.KEY_TOTAL_PAGE);
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
        initHandler();
    }

    private  void initHandler(){
        if(mUserMessageHandler == null) {
            mUserMessageHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if(mParentActivity !=null){
                        mParentActivity.setContentViewVisibility(true);
                    }

                    XListView xListView = (XListView) getListView();
                    //只有一页数据的时候，不显示下拉框
                    if (mTotalPage == 1) {

                        xListView.setPullLoadEnable(false);
                    } else if (mTotalPage > 1) {
                        xListView.setPullLoadEnable(true);
                    }

                    TextView textView = (TextView) xListView.findViewById(R.id.xlistview_footer_hint_textview);
                    switch (msg.what) {
                        case MessageType.TYPE_LOAD_MORE_SUCCESS:
                            //读取更多时，将数据merge到原来的list里
                            mergeToTotalList((List<UserMessage>) msg.obj);
                            mMessageAdapter.bindData(mTotalMessageList);
                            mMessageAdapter.notifyDataSetChanged();
                            onLoadFinish(true);
                            if (mTotalMessageList != null && mTotalMessageList.size() != 0) {
                                textView.setText(R.string.tech_load_more_data);
                            } else {
                                textView.setText(R.string.tech_no_data);
                            }

                            mPageId++;
                            break;
                        case MessageType.TYPE_UPDATE_SUCCESS:
                            //更新时，直接替换数据
                            mTotalMessageList = (List<UserMessage>) msg.obj;
                            mMessageAdapter.bindData(mTotalMessageList);
                            mMessageAdapter.notifyDataSetChanged();
                            onLoadFinish(true);
                            if (mTotalMessageList != null && mTotalMessageList.size() != 0) {
                                textView.setText(R.string.tech_load_more_data);
                            } else {
                                textView.setText(R.string.tech_no_data);
                            }

                            break;
                        case MessageType.TYPE_DELETE_MESSAGE_CANCEL:
                        case MessageType.TYPE_DELETE_MESSAGE_SUCCESS:
                            showDeleteWidgets(false);
//                            mMessageAdapter.dataChange();
//                            if(mParentActivity != null){
//                                mParentActivity.setWidgetsClickable(true);
//                            }
                            resumeWidgets(xListView);
                            Log.i(mTag,"delete message success");
                            break;

                        case MessageType.TYPE_SERVER_NOT_AVAILABLE:
                            Toast.makeText(getActivity().getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                            onLoadFinish(false);
                            break;
                        case MessageType.TYPE_ACCESS_TOKEN_INVALID:
                            String tip = (String) msg.obj;
                            Toast.makeText(getActivity().getApplicationContext(), tip, Toast.LENGTH_SHORT).show();
                            signOut();
                            break;
                        case MessageType.TYPE_UPDATE_FAILED:
                        case MessageType.TYPE_LOAD_MORE_FAILED:
                            Toast.makeText(getActivity().getApplicationContext(), Prompt.PROMPT_LOAD_FAILED, Toast.LENGTH_SHORT).show();
                            onLoadFinish(false);
                            break;
                        case MessageType.TYPE_NETWORK_DISABLE:
                            Toast.makeText(getActivity(), R.string.tech_network_unuseful, Toast.LENGTH_SHORT).show();
                            onLoadFinish(false);
                            break;
                        case MessageType.TYPE_NO_DATA_FOUND:
                            textView.setText(R.string.tech_no_data);
                            //Toast.makeText(getActivity(),R.string.tech_no_data,Toast.LENGTH_SHORT).show();
                            onLoadFinish(false);
                            break;
                        case MessageType.TYPE_DELETE_MESSAGE_FAILED:
                            showDeleteWidgets(false);
                            resumeWidgets(xListView);
                            Toast.makeText(getActivity().getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Log.e(mTag, "Unknow message type: " + msg.what);
                    }
                }
            };
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPageId = 2;
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
            Toast.makeText(getActivity().getApplicationContext(),Prompt.PROMPT_NO_MORE_DATA,Toast.LENGTH_SHORT).show();
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
