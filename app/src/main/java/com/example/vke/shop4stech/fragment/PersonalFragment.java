package com.example.vke.shop4stech.fragment;

//import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.activity.AboutUsActivity;
import com.example.vke.shop4stech.activity.GetSmsCodeActivity;
import com.example.vke.shop4stech.activity.SignInActivity;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.constant.Prompt;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.helper.PreferencesHelper;
import com.example.vke.shop4stech.model.PersonalInfo;


import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.logging.LogRecord;

/**
 * Created by vke on 2016/5/8.
 */
public class PersonalFragment extends Fragment implements View.OnClickListener{

    private static final String mTag = "PersonalFragment";

    private PersonalInfo mPersonInfo;

    private TextView mUserNameTextView;
    private TextView mJobIdTextView;
    private TextView mJobTypeTextView;
    private TextView mStationTextView;
    private TextView mTeamTextView;

    private RelativeLayout mModifyPasswordRelativeLayOut;
    private RelativeLayout mUserManualRelativeLayOut;
    private RelativeLayout mAboutUsRelativeLayOut;
    private RelativeLayout mSignoutLayOut;

    private Handler mUpdatePersonalInfoHandler;
    public static PersonalFragment newInstance() {

        Bundle args = new Bundle();

        PersonalFragment fragment = new PersonalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(mTag,"on Create in personal fragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(mTag,"in function onCreateView");
        View view =  inflater.inflate(R.layout.fragment_personal_info,container,false);

        initContentView(view);
        setOnClickListeners();
        return view;
    }

    private void  initContentView(View view){
        mUserNameTextView = (TextView)view.findViewById(R.id.tech_name_text_view);
        mJobIdTextView = (TextView)view.findViewById(R.id.tech_job_id_text_view);
        mJobTypeTextView = (TextView)view.findViewById(R.id.tech_job_type_text_view);
        mStationTextView = (TextView)view.findViewById(R.id.tech_station_text_view);
        mTeamTextView = (TextView)view.findViewById(R.id.tech_team_text_view);

        mModifyPasswordRelativeLayOut = (RelativeLayout)view.findViewById(R.id.tech_modify_password_relative_layout);
        mUserManualRelativeLayOut= (RelativeLayout)view.findViewById(R.id.tech_help_relative_layout);
        mAboutUsRelativeLayOut = (RelativeLayout)view.findViewById(R.id.tech_about_us_relative_layout);
        mSignoutLayOut = (RelativeLayout)view.findViewById(R.id.tech_sign_out_relative_layout);
    }

    private void setOnClickListeners(){
        mModifyPasswordRelativeLayOut.setOnClickListener(this);
        mUserManualRelativeLayOut.setOnClickListener(this);
        mAboutUsRelativeLayOut.setOnClickListener(this);
        mSignoutLayOut.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (!NetOperationHelper.isNetworkConnected(getActivity())) {
                        mUpdatePersonalInfoHandler.sendEmptyMessage(MessageType.TYPE_NETWORK_DISABLE);
                        return;
                    }

                    String accessToken = getValidAccessToken();
                    if (accessToken == null) {
                        Message msg = mUpdatePersonalInfoHandler.obtainMessage();
                        msg.obj = Prompt.PROMPT_ACCESS_TOKEN_INVALID;
                        msg.what = MessageType.TYPE_GET_PERSONAL_INFO_ERROR;
                        mUpdatePersonalInfoHandler.sendMessage(msg);
                        ;
                    }

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put(RequestDataKey.LOGIN_MODE, "userInfo");
                    map.put(RequestDataKey.ACCESS_TOKEN, accessToken);

                    PersonalInfo personalInfo = NetOperationHelper.getPersnoalInfo(map);
                    if (personalInfo != null) {
                        Message msg = mUpdatePersonalInfoHandler.obtainMessage();
                        msg.obj = personalInfo;
                        msg.what = MessageType.TYPE_GET_PERSONAL_INFO_OK;
                        mUpdatePersonalInfoHandler.sendMessage(msg);
                    } else {
                        Message msg = mUpdatePersonalInfoHandler.obtainMessage();
                        msg.obj = Prompt.PROMPT_ACCESS_TOKEN_INVALID;
                        msg.what = MessageType.TYPE_GET_PERSONAL_INFO_ERROR;
                        mUpdatePersonalInfoHandler.sendMessage(msg);
                    }
                }
                catch(Exception e){
                    Log.w(mTag,e.toString());
                }
            }
        }).start();


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.i(mTag,"onResume");
        super.onResume();
        mUpdatePersonalInfoHandler= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MessageType.TYPE_GET_PERSONAL_INFO_OK:
                        PersonalInfo personalInfo =(PersonalInfo) msg.obj;
                        updateContentView(personalInfo);
                        break;
                    case MessageType.TYPE_GET_PERSONAL_INFO_ERROR:
                        String tip = (String)msg.obj;
                        Toast.makeText(getActivity().getApplicationContext(),tip,Toast.LENGTH_SHORT).show();
                        signOut();
                        break;
                    case MessageType.TYPE_SIGN_OUT_OK:
                        break;
                    case MessageType.TYPE_SIGN_OUT_ERROR:
                        break;

                    case MessageType.TYPE_NETWORK_DISABLE:
                        Toast.makeText(getActivity(),R.string.tech_network_unuseful,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Log.e(mTag,"Unknow message type: " + msg.what);
                }
            }
        };
    }

    //获取有效的accessToken，这里会进行一个网络操作判断
    private String getValidAccessToken(){
        String accessToken = PreferencesHelper.getPreferenceAccessToken(getActivity());
        if(NetOperationHelper.checkAccessTokenInvalid(accessToken)){
            return accessToken;
        }

        return null;
    }

    private void updateContentView(PersonalInfo personalInfo){
        mUserNameTextView.setText(personalInfo.getUserName());
        mJobIdTextView.setText(personalInfo.getStaffId());
        mJobTypeTextView.setText(personalInfo.getJobType());
        mStationTextView.setText(personalInfo.getStation());
        mTeamTextView.setText(personalInfo.getTeam());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tech_modify_password_relative_layout:
                GetSmsCodeActivity.start(getActivity());

                break;
            case R.id.tech_help_relative_layout:
                break;
            case R.id.tech_about_us_relative_layout:
                AboutUsActivity.start(getActivity());
                break;
            case R.id.tech_sign_out_relative_layout:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("确认退出吗?");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        signOut();
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

                break;
            default:
                Log.i(mTag,"Unknow button on click");
        }
    }

    private void signOut(){
        Log.i(mTag,"signOut");
        PreferencesHelper.signOut(getActivity());
        SignInActivity.startWithNoAnimate(getActivity());
        ActivityCompat.finishAfterTransition(getActivity());
    }

    @Override
    public void onDestroyView() {
        Log.i(mTag,"onDestroyView");
        super.onDestroyView();
        mUpdatePersonalInfoHandler = null;
    }

}
