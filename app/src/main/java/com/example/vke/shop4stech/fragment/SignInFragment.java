package com.example.vke.shop4stech.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.activity.HomeActivity;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.helper.PreferencesHelper;
import com.example.vke.shop4stech.helper.TransitionHelper;
import com.example.vke.shop4stech.model.LoginUIAccountInfo;
import com.example.vke.shop4stech.model.User;

import org.apache.commons.codec.binary.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Enable selection of user name.
 */
public class SignInFragment extends Fragment {

    private static final String ARG_EDIT = "EDIT";
    private static final String mTag = "SignInFragment";
    private User mUser;
    private LoginUIAccountInfo mUIAccountInfo;
    private EditText mUserName;
    private EditText mPassword;
    private Button mLoginButton;
    private LinearLayout mContentLinear;
    private ToggleButton mShowPasswordToggleButton;
    private ToggleButton mRememberPasswordToggleButton;
    private String mAccessToken;
    private static SignInFragment ourInstance = null;
    private OnFragmentInteractionListener mListener;
    private static final int LOGIN_SERVICE_OK = 0x200;
    private static final int LOGIN_SERVICE_ERR = 0x201;
    private static final int TOKEN_INVALID = 0x202;
    private static final int TOKEN_OK = 0x203;

    private Handler mLoginHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SERVICE_OK:
                    saveUserData(getActivity(),mAccessToken);
                    if (mRememberPasswordToggleButton.isChecked()){
                        saveUiAccountInfoData(getActivity(),true);
                    }
                    else{
                        saveUiAccountInfoData(getActivity(),false);
                    }

                case TOKEN_OK:
                    //登陆成功后，才能设置button重新可以登陆
                    mLoginButton.setClickable(true);
                    Log.i(mTag,"User already login,so start home activity directly");
                    final Activity activity = getActivity();
                    HomeActivity.start(activity, mUser);
                    activity.finish();
                    break;
                case LOGIN_SERVICE_ERR:
                    Toast.makeText(getActivity(), R.string.tech_user_login_error , Toast.LENGTH_SHORT).show();
                    break;

                case TOKEN_INVALID:
                    Log.i(mTag,">>>>>>>>>>>>> Token INVALID");
                    mContentLinear.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment getInstance(){
        if (ourInstance == null) {
            ourInstance = new SignInFragment();
        }
        return ourInstance;
    }

    public static SignInFragment newInstance(boolean edit) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_EDIT, edit);
        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 检测网络是否可用
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

//    /**
//     * 获取当前网络类型
//     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
//     */
//
//    public static final int NETTYPE_WIFI = 0x01;
//    public static final int NETTYPE_CMWAP = 0x02;
//    public static final int NETTYPE_CMNET = 0x03;
//    public int getNetworkType() {
//        int netType = 0;
//        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        if (networkInfo == null) {
//            return netType;
//        }
//        int nType = networkInfo.getType();
//        if (nType == ConnectivityManager.TYPE_MOBILE) {
//            String extraInfo = networkInfo.getExtraInfo();
//            if(!StringUtils.isEmpty(extraInfo)){
//                if (extraInfo.toLowerCase().equals("cmnet")) {
//                    netType = NETTYPE_CMNET;
//                } else {
//                    netType = NETTYPE_CMWAP;
//                }
//            }
//        } else if (nType == ConnectivityManager.TYPE_WIFI) {
//            netType = NETTYPE_WIFI;
//        }
//        return netType;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            final int savedAvatarIndex = savedInstanceState.getInt(KEY_SELECTED_AVATAR_INDEX);
//            if (savedAvatarIndex != GridView.INVALID_POSITION) {
//                mSelectedAvatar = Avatar.values()[savedAvatarIndex];
//            }
//        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mContentLinear = (LinearLayout) contentView.findViewById(R.id.tech_sign_in_content);
        return  contentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        if (mAvatarGrid != null) {
//            outState.putInt(KEY_SELECTED_AVATAR_INDEX, mAvatarGrid.getCheckedItemPosition());
//        } else {
//            outState.putInt(KEY_SELECTED_AVATAR_INDEX, GridView.INVALID_POSITION);
//        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        assureUserInit();
        initContentViews(view);
        initContents();
        if (mUser == null) {
            Log.i(mTag,"User is null ,so need start login UI");
            view.findViewById(R.id.empty).setVisibility(View.GONE);
            view.findViewById(R.id.tech_sign_in_content).setVisibility(View.VISIBLE);
        } else {
            Log.i(mTag,"User is not null, so need to verify the token,username:"+mUser.getUserName()+" password:"+mUser.getPassword()+" accessToken:"+mUser.getAccessToken());
            view.findViewById(R.id.empty).setVisibility(View.GONE);
            String accessToken = mUser.getAccessToken();
            if (!accessToken.equals(""))//如果accessToken不为空，需要登陆到服务器去判断token是否可用
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        checkTokenValid();
                    }

                }).start();
            }
            else {

                view.findViewById(R.id.empty).setVisibility(View.GONE);
                view.findViewById(R.id.tech_sign_in_content).setVisibility(View.VISIBLE);
            }
        }
        super.onViewCreated(view, savedInstanceState);
    }

    private void checkTokenValid(){
        mLoginHandler.sendEmptyMessage(TOKEN_OK);
    }

    private void initContentViews(View view) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* no-op */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // hiding the floating action button if text is empty
                if (s.length() == 0) {
                   Log.i(mTag,"onTextChanged");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // showing the floating action button if avatar is selected and input data is valid
                Log.i(mTag,"afterTextChanged");
            }
        };

        mUserName = (EditText) view.findViewById(R.id.edit_text_username);
        mUserName.addTextChangedListener(textWatcher);
        mPassword = (EditText) view.findViewById(R.id.edit_text_password);
        mPassword.addTextChangedListener(textWatcher);
        mRememberPasswordToggleButton = (ToggleButton)view.findViewById(R.id.tech_toggle_button_remember_password);
        mRememberPasswordToggleButton.setOnCheckedChangeListener(new ToggleButtonListeners());
        mShowPasswordToggleButton = (ToggleButton)view.findViewById(R.id.tech_toggle_show_password);
        mShowPasswordToggleButton.setOnCheckedChangeListener(new ToggleButtonListeners());

        mLoginButton = (Button) view.findViewById(R.id.tech_button_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tech_button_login:
                        Log.d(mTag,"mUserName:"+mUserName.getText().toString().equals("") + " mPassword:"+mPassword.getText().toString().equals(""));
                        if (mUserName.getText().toString().equals("") || mPassword.getText().toString().equals("")){
                            Toast.makeText(getActivity().getApplicationContext(),R.string.tech_account_info_not_null,Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (!isNetworkConnected()){
                            Toast.makeText(getActivity(),R.string.tech_network_unuseful,Toast.LENGTH_SHORT).show();
                            break;
                        }

                        mLoginButton.setClickable(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HashMap<String,Object> loginDataMap = new HashMap<String, Object>();
                                loginDataMap.put(RequestDataKey.PHONE,mUserName.getText().toString().replace(" ",""));
                                loginDataMap.put(RequestDataKey.LOGIN_MODE,"login");
                                loginDataMap.put(RequestDataKey.PASSWORD, Base64.encodeToString(mPassword.getText().toString().getBytes(),Base64.DEFAULT));
                                loginDataMap.put(RequestDataKey.JPUSH_ID,"fjdsklfjkdj3fejkfjkdf");
                                loginDataMap.put(RequestDataKey.TYPE,"mantain");

                                String result = NetOperationHelper.login(loginDataMap);
                                if(result != null){
                                    String [] data = result.split(" ");
                                    if (data[0].equals("ok")){
                                        mAccessToken = data[1];

                                        mLoginHandler.sendEmptyMessage(LOGIN_SERVICE_OK);
                                    }
                                    else {
                                        //Toast.makeText(getActivity().getApplicationContext(),data[1],Toast.LENGTH_SHORT).show();
                                        mLoginHandler.sendEmptyMessage(LOGIN_SERVICE_ERR);
                                    }
                                }

                            }
                        }).start();

                        break;
                    default:
                        throw new UnsupportedOperationException(
                                "The onClick method has not been implemented for " + getResources()
                                        .getResourceEntryName(v.getId()));
                }
            }
        });
    }


    private void initContents() {
        assureUserInit();
        if (mUIAccountInfo != null) {
            Log.i(mTag,"username :" +mUIAccountInfo.getUserName() + "password:" + mUIAccountInfo.getPassword() + " Flag:" + mUIAccountInfo.getRememberFlag());
            mUserName.setText(mUIAccountInfo.getUserName());
            mPassword.setText(mUIAccountInfo.getPassword());
            if (mUIAccountInfo.getRememberFlag()) {
                mRememberPasswordToggleButton.setChecked(true);
            }
            else{
                mRememberPasswordToggleButton.setChecked(false);
            }
        }
        else {
            Log.i(mTag,"mUIAccountInfo is null");
        }
    }

    private void assureUserInit() {
        if (mUser == null) {
            mUser = PreferencesHelper.getUser(getActivity());
        }

        if (mUIAccountInfo == null){
            mUIAccountInfo = PreferencesHelper.getUIAccountInfo(getActivity());
        }
    }

    private void saveUserData(Activity activity,String accessToken) {
        mUser = new User(mUserName.getText().toString(), mPassword.getText().toString(),accessToken);
        PreferencesHelper.writeToPreferences(activity, mUser);
    }

    private void saveUiAccountInfoData(Activity activity,boolean remember){
        PreferencesHelper.writeUiInfoToPreferences(getActivity(),new LoginUIAccountInfo( mUserName.getText().toString(),mPassword.getText().toString(),remember));
    }

    private boolean isInputDataValid() {
        return PreferencesHelper.isInputDataValid(mUserName.getText(), mPassword.getText());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        Log.i(mTag,">>>>>..Fragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {

        Log.i(mTag,">>>>>..Fragment onPause");
        if (mRememberPasswordToggleButton.isChecked()) {
            PreferencesHelper.writeUiInfoToPreferences(getActivity(),new LoginUIAccountInfo( mUserName.getText().toString(),mPassword.getText().toString(),true));
        }
        else {
            PreferencesHelper.writeUiInfoToPreferences(getActivity(),new LoginUIAccountInfo("","",false));
        }
        super.onPause();
    }

    class ToggleButtonListeners implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.tech_toggle_show_password:
                    if (isChecked) {
                        mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        mPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    break;

                case R.id.tech_toggle_button_remember_password:
                    if (isChecked) {
                        String username = mUserName.getText().toString();
                        String password = mPassword.getText().toString();
                        if ( password.equals("")||username.equals("")) {
                            Toast.makeText(getActivity().getApplicationContext(),R.string.tech_pls_input_account_info,Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                default:
                    break;
            }
        }

    }
}

