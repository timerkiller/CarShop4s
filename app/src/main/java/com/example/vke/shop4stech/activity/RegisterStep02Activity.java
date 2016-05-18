package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.vke.shop4stech.R;

public class RegisterStep02Activity extends BaseRegisterActivity {

    private static final String mTag = "RegisterStep02Activity";

    public static void start(Activity activity) {
        Intent starter = new Intent(activity, RegisterStep02Activity.class);
        activity.startActivity(starter);
        activity.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_register_step02);
        setToolBarTitle(getResources().getString(R.string.tech_register_step_02));
    }

    @Override
    public void goNextPage() {
        RegisterStep03Activity.start(this);
    }

    @Override
    public void goBackPage() {
        RegisterStep01Activity.start(this);
        this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
        this.finish();
    }


}
