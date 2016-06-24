package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.ActionMenuView.OnMenuItemClickListener;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.constant.Prompt;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.helper.StringHelper;
import com.example.vke.shop4stech.model.PersonalInfo;

import java.util.HashMap;

public class RegisterStep03Activity extends BaseRegisterActivity {

    private static final String mTag = "RegisterStep03Activity";
    private Button mNextButton,mBackButton;
    private PersonalInfo mPersonalInfo;
    public static final String PERSONAL_INFO = "PersonalInfo";
    private EditText mPwdEditText ,mConfirmPwdEditText;
    private Handler mRegisterHandler;

    public static void start(Activity activity, PersonalInfo personalInfo) {
        Intent starter = new Intent(activity, RegisterStep03Activity.class);
        starter.putExtra(PERSONAL_INFO,personalInfo);
        activity.startActivity(starter);
        activity.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_register_step03);
        setToolBarTitle(getResources().getString(R.string.tech_register_step_03));

        final Activity activity = this;
        mPersonalInfo = (PersonalInfo) getIntent().getParcelableExtra(PERSONAL_INFO);
        mRegisterHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String result = (String) msg.obj;
                if(!"ok".equals(result)){
                    Toast.makeText(getApplicationContext(),result, Toast.LENGTH_SHORT).show();
                    return ;
                }else{
                    Toast.makeText(getApplicationContext(), Prompt.PROMPT_REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();//注册成功
                }

                SignInActivity.startWithNoAnimate(RegisterStep03Activity.this);
                activity.overridePendingTransition(R.anim.animate_out_alpha,R.anim.animate_enter_alpha);
                activity.finish();
            }
        };
    }

    @Override
    public void goNextPage() {
        mPwdEditText = (EditText) this.findViewById(R.id.tech_set_password_edit_text);
        mConfirmPwdEditText = (EditText) this.findViewById(R.id.tech_confirm_password_edit_text);
        if(StringHelper.isEmpty(mPwdEditText.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.tech_input_password, Toast.LENGTH_SHORT).show();
            return ;
        }
        if(StringHelper.isEmpty(mConfirmPwdEditText.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.tech_input_password_again, Toast.LENGTH_SHORT).show();
            return ;
        }
        if(!mConfirmPwdEditText.getText().toString().equals(mPwdEditText.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.tech_password_check_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                PersonalInfo personalInfo = getIntent().getParcelableExtra(RegisterStep03Activity.PERSONAL_INFO);

                personalInfo.setPassword(Base64.encodeToString(mPwdEditText.getText().toString().getBytes(),Base64.DEFAULT));

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("mode", "register");
                map.put("userName",personalInfo.getUserName());
                map.put("phone",personalInfo.getPhone());
                map.put("password",personalInfo.getmPassword());
                map.put("registerCode",personalInfo.getmRegisterCode());
                map.put("staffID",personalInfo.getStaffId());
                map.put("jobType",personalInfo.getJobType());
                map.put("station",personalInfo.getStation());
                map.put("team",personalInfo.getTeam());
                map.put("carShop",personalInfo.getmCarShop());
                String result = NetOperationHelper.register(map);
                Message msg = mRegisterHandler.obtainMessage();
                //msg.what = "";
                msg.obj = result;
                mRegisterHandler.sendMessage(msg);

            }
        }).start();

        /*SignInActivity.startWithNoAnimate(this);
        this.overridePendingTransition(R.anim.animate_out_alpha,R.anim.animate_enter_alpha);
        this.finish();*/
    }

    @Override
    public void goBackPage() {
        PersonalInfo mPersonalInfo = (PersonalInfo) getIntent().getParcelableExtra(PERSONAL_INFO);
        Intent intent = new Intent(this, RegisterStep02Activity.class);
        intent.putExtra(PERSONAL_INFO,mPersonalInfo);
        startActivity(intent);
        //RegisterStep02Activity.start(this);
        this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
        this.finish();
    }
}
