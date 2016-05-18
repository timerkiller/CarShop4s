package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vke.shop4stech.R;

public class ForgetPasswordActivity extends BaseRegisterActivity {

    public static void start(Activity activity) {
        Intent starter = new Intent(activity, ForgetPasswordActivity.class);
        activity.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_forget_password);
        setToolBarTitle(getResources().getString(R.string.tech_reset_password_step2));
    }

    @Override
    public void goNextPage() {
        this.finish();
        this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
    }

    @Override
    public void goBackPage() {
        this.finish();
        this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
    }
}
