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

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.model.PersonalInfo;

public class RegisterStep03Activity extends BaseRegisterActivity {

    private static final String mTag = "RegisterStep03Activity";
    private Button mNextButton,mBackButton;
    private PersonalInfo mPersonalInfo;
    private static final String PERSONAL_INFO = "PersonalInfo";

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
        SignInActivity.startWithNoAnimate(this);
        this.overridePendingTransition(R.anim.animate_out_alpha,R.anim.animate_enter_alpha);
        this.finish();
    }

    @Override
    public void goBackPage() {
        RegisterStep02Activity.start(this);
        this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
        this.finish();
    }
}
