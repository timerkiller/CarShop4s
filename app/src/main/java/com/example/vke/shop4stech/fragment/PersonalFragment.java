package com.example.vke.shop4stech.fragment;

//import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.activity.AboutUsActivity;
import com.example.vke.shop4stech.activity.SignInActivity;
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

    static class MessageType{
        public static final int TYPE_GET_PERSONAL_INFO_OK = 0x1001;
        public static final int TYPE_GET_PERSONAL_INFO_ERROR = 0x1002;
        public static final int TYPE_SIGN_OUT_OK = 0x1003;
        public static final int TYPE_SIGN_OUT_ERROR = 0x1004;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    default:

                }
            }
        };
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
                String accessToken = PreferencesHelper.getPreferenceAccessToken(getActivity());
                if (accessToken == null){
                    Message msg = mUpdatePersonalInfoHandler.obtainMessage();
                    msg.obj = "AccessToken失效，请重新登陆";
                    msg.what = MessageType.TYPE_GET_PERSONAL_INFO_ERROR;
                    mUpdatePersonalInfoHandler.sendMessage(msg);;
                }

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(RequestDataKey.LOGIN_MODE, "userInfo");
                map.put(RequestDataKey.ACCESS_TOKEN,accessToken);

                PersonalInfo personalInfo = NetOperationHelper.getPersnoalInfo(map);
                if (personalInfo != null) {
                    Message msg = mUpdatePersonalInfoHandler.obtainMessage();
                    msg.obj = personalInfo;
                    msg.what = MessageType.TYPE_GET_PERSONAL_INFO_OK;
                    mUpdatePersonalInfoHandler.sendMessage(msg);
                }
                else {
                    Message msg = mUpdatePersonalInfoHandler.obtainMessage();
                    msg.obj = "AccessToken失效，请重新登陆";
                    msg.what = MessageType.TYPE_GET_PERSONAL_INFO_ERROR;
                    mUpdatePersonalInfoHandler.sendMessage(msg);
                }
            }
        }).start();

        super.onViewCreated(view, savedInstanceState);
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
                break;
            case R.id.tech_help_relative_layout:
                break;
            case R.id.tech_about_us_relative_layout:
                AboutUsActivity.start(getActivity());
                break;
            case R.id.tech_sign_out_relative_layout:
                signOut();
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

}
