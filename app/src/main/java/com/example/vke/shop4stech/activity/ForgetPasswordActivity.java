package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.constant.Prompt;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.helper.NetOperationHelper;

import java.util.HashMap;

public class ForgetPasswordActivity extends BaseRegisterActivity {

    private static final String mTag = "ForgetPasswordActivity";

    private String mPhone;
    private String mEncrySmsCode;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private Handler mModifyPasswordHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MessageType.TYPE_MODIFY_SUCCESS:
                    Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    ForgetPasswordActivity.this.finish();
                    ForgetPasswordActivity.this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
                    break;
                case MessageType.TYPE_MODIFY_FAILED:
                    Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Log.e(mTag,"Unknow message type :" + msg.what);
            }
        }
    };

    public static void start(Activity activity,String phone,String encrySmsCode) {
        Intent starter = new Intent(activity, ForgetPasswordActivity.class);
        starter.putExtra("phone",phone);
        starter.putExtra("encrySmsCode",encrySmsCode);
        activity.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_forget_password);
        setToolBarTitle(getResources().getString(R.string.tech_reset_password_step2));

        mPhone = getIntent().getStringExtra("phone");
        mEncrySmsCode = getIntent().getStringExtra("encrySmsCode");
        initView();
    }

    private void initView(){
        mPasswordEditText = (EditText)this.findViewById(R.id.tech_input_password_edit_text);
        mPasswordConfirmEditText = (EditText)this.findViewById(R.id.tech_input_password_again_edit_text);
    }

    @Override
    public void goNextPage() {

        if(!NetOperationHelper.isNetworkConnected(ForgetPasswordActivity.this)){
            Toast.makeText(getApplicationContext(),R.string.tech_network_unuseful,Toast.LENGTH_SHORT).show();
            return;
        }

        if(!mPasswordEditText.getText().toString().equals(mPasswordConfirmEditText.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.tech_password_check_failed,Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                modifyPassword();
            }
        }).start();


    }

    @Override
    public void goBackPage() {
        this.finish();
        this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
    }

    private void modifyPassword(){
        HashMap<String,Object> map = new HashMap<String,Object>();
        String newPassword = Base64.encodeToString(mPasswordEditText.getText().toString().getBytes(),Base64.DEFAULT);
        map.put(RequestDataKey.PHONE,mPhone);
        map.put(RequestDataKey.NEW_PASSWORD,newPassword);
        map.put(RequestDataKey.LOGIN_MODE,"modify");
        map.put(RequestDataKey.SMS_TOKEN,mEncrySmsCode);

        String result = NetOperationHelper.modifyPassword(map);
        Message msg = mModifyPasswordHandler.obtainMessage();
        if(result != null){
            if (result.equals("ok")){
                msg.what = MessageType.TYPE_MODIFY_SUCCESS;
                msg.obj = Prompt.PROMPT_MODIFY_SUCCESS;
                mModifyPasswordHandler.sendMessage(msg);
            }
            else{
                msg.what = MessageType.TYPE_MODIFY_FAILED;
                msg.obj = result;
                mModifyPasswordHandler.sendMessage(msg);
            }
        }
        else{
            msg.what = MessageType.TYPE_MODIFY_FAILED;
            msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
            mModifyPasswordHandler.sendMessage(msg);
        }
    }
}
