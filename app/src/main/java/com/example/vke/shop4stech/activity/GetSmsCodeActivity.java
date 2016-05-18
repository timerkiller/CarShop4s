package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vke.shop4stech.R;

public class GetSmsCodeActivity extends BaseRegisterActivity {

    public static void start(Activity context) {
        Intent starter = new Intent(context, GetSmsCodeActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_get_sms_code);
        setToolBarTitle(getResources().getString(R.string.tech_reset_password_step1));
    }

    @Override
    public void goNextPage() {
        this.finish();
        //this.overridePendingTransition(R.anim.animate_enter_alpha,R.anim.animate_out_alpha);
        ForgetPasswordActivity.start(this);
    }

    @Override
    public void goBackPage() {
        this.finish();
        this.overridePendingTransition(R.anim.animate_enter_alpha,R.anim.animate_out_alpha);
    }
}
