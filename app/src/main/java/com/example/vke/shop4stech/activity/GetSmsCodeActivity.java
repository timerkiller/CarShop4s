package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.helper.DateTimeHelper;
import com.example.vke.shop4stech.helper.NetOperationHelper;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;

public class GetSmsCodeActivity extends BaseRegisterActivity {
    private static final String mTag = "GetSmsCodeActivity";
    private static final int VALID_PHONE_LENGTH = 11;

    private EditText mPhoneEditView, mSmsCodeEditView;
    private Button mGetSmsCodeButton;
    private TimeCount mCounter;
    String mEncrySmsCode;

    private  Handler mSmsCodeHander;

    public static void start(Activity context) {
        Intent starter = new Intent(context, GetSmsCodeActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_get_sms_code);
        setToolBarTitle(getResources().getString(R.string.tech_reset_password_step1));

        initView();
    }

    private void initView(){
        mSmsCodeHander = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MessageType.TYPE_GET_SMS_SUCCESS:
                        mEncrySmsCode = (String)msg.obj;
                        mCounter.start();
                        break;
                    case MessageType.TYPE_GET_SMS_FAILED:
                        Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

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
                Log.i(mTag,"afterTextChanged" + " length" +s.toString().length());
                if(s.toString().length() == VALID_PHONE_LENGTH && !mCounter.getTimerStatus()){
                    mGetSmsCodeButton.setEnabled(true);
                    mSmsCodeEditView.requestFocus();
                }
                else{
                    mGetSmsCodeButton.setEnabled(false);
                }
            }
        };

        mCounter = new TimeCount(60000,1000);
        mPhoneEditView = (EditText)this.findViewById(R.id.tech_input_phone_edit_text);
        mSmsCodeEditView = (EditText)this.findViewById(R.id.tech_input_sms_code_edit_text);
        mGetSmsCodeButton = (Button)this.findViewById(R.id.tech_get_sms_code_button);

        mPhoneEditView.addTextChangedListener(textWatcher);
        mGetSmsCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetOperationHelper.isNetworkConnected(GetSmsCodeActivity.this)){
                    Toast.makeText(getApplicationContext(),R.string.tech_get_sms_code_failed,Toast.LENGTH_SHORT).show();
                    return;
                }

                mGetSmsCodeButton.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getSmsCode();
                    }
                }).start();

            }
        });
        mGetSmsCodeButton.setEnabled(false);
    }

    private void getSmsCode(){
        HashMap<String,Object> map = new HashMap<String, Object>();
        //reqMap.put("phone",);
        String phone = mPhoneEditView.getText().toString();
        String timestamp = DateTimeHelper.timeStamp();
        String check = new String(Hex.encodeHex(DigestUtils.md5( phone + timestamp)));
        map.put(RequestDataKey.OPERATE, "send");
        map.put(RequestDataKey.PHONE, phone);
        map.put(RequestDataKey.TYPE, "mantain");
        map.put(RequestDataKey.TIMESTAMP, timestamp);
        map.put(RequestDataKey.CHECK, check);
        map.put(RequestDataKey.LOGIN_MODE, "forget");

        String encrySmsCode = NetOperationHelper.getSmsCode(map);
        Log.i(mTag,"get server code is :" + mEncrySmsCode);
        Message msg = mSmsCodeHander.obtainMessage();
        if(encrySmsCode !=null){
            if (encrySmsCode.contains("error")){
                String errofInfo = encrySmsCode.split(" ")[1];
                msg.what = MessageType.TYPE_GET_SMS_FAILED;
                msg.obj = errofInfo;
                mSmsCodeHander.sendMessage(msg);
            }
            else{
                msg.what = MessageType.TYPE_GET_SMS_SUCCESS;
                msg.obj = encrySmsCode;
                mSmsCodeHander.sendMessage(msg);
            }
        }
        else{
            msg.what = MessageType.TYPE_GET_SMS_FAILED;
            msg.obj = "Oh,服务器出了点状态，请稍后再试!";
            mSmsCodeHander.sendMessage(msg);
        }
    }

    @Override
    public void goNextPage() {
        String phone = mPhoneEditView.getText().toString();
        if(phone.equals("") || phone.length() != VALID_PHONE_LENGTH){
            Toast.makeText(getApplicationContext(),R.string.tech_input_valid_phone,Toast.LENGTH_SHORT).show();
            return;
        }

        String smsCode = mSmsCodeEditView.getText().toString();
        String encrySmsCode = new String(Hex.encodeHex(DigestUtils.md5(smsCode)));
        Log.i(mTag,"local encySmsCode:" + encrySmsCode + "service encrySmsCode:" + mEncrySmsCode);
        if (!encrySmsCode.equals(mEncrySmsCode) || smsCode.equals(" ")){
            Toast.makeText(getApplicationContext(),R.string.tech_sms_code_error,Toast.LENGTH_SHORT).show();
            return;
        }

        this.finish();
        //this.overridePendingTransition(R.anim.animate_enter_alpha,R.anim.animate_out_alpha);
        ForgetPasswordActivity.start(this,phone,mEncrySmsCode);
    }

    @Override
    public void goBackPage() {
        this.finish();
        this.overridePendingTransition(R.anim.animate_enter_alpha,R.anim.animate_out_alpha);
    }

    class TimeCount extends CountDownTimer {
        private boolean mTimerOngoing = false;

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            mGetSmsCodeButton.setText("获取验证码");
            mGetSmsCodeButton.setEnabled(true);
            mTimerOngoing = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            mTimerOngoing = true;
            mGetSmsCodeButton.setEnabled(false);//防止重复点击
            String currentSec = millisUntilFinished / 1000 + "s";
            mGetSmsCodeButton.setText(currentSec);
        }

        public boolean getTimerStatus(){
            return mTimerOngoing;
        }
    }


}
