package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.ActionMenuView.OnMenuItemClickListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.helper.StringHelper;
import com.example.vke.shop4stech.model.PersonalInfo;

public class RegisterStep03Activity extends BaseRegisterActivity {

    private static final String mTag = "RegisterStep03Activity";
    private Button mNextButton,mBackButton;
    private PersonalInfo mPersonalInfo;
    public static final String PERSONAL_INFO = "PersonalInfo";
    private EditText mPwdEditText ,mConfirmPwdEditText;

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

        mPersonalInfo = (PersonalInfo) getIntent().getParcelableExtra(PERSONAL_INFO);
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
        PersonalInfo personalInfo = getIntent().getParcelableExtra(RegisterStep03Activity.PERSONAL_INFO);
        //TODO 密码需要加密
        personalInfo.setPassword(mPwdEditText.getText().toString());
        //TODO 保存信息
        SignInActivity.startWithNoAnimate(this);
        this.overridePendingTransition(R.anim.animate_out_alpha,R.anim.animate_enter_alpha);
        this.finish();
    }

    @Override
    public void goBackPage() {
        //TODO REG2页面需要回显数据
        RegisterStep02Activity.start(this);
        this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
        this.finish();
    }
}
