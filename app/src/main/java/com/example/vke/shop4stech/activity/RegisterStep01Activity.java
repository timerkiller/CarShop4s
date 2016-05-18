package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.vke.shop4stech.R;

import java.util.List;

public class RegisterStep01Activity extends BaseRegisterActivity{

    private static final String mTag = "RegisterStep01Activity";

    public static void start(Activity activity) {
        Intent starter = new Intent(activity, RegisterStep01Activity.class);
        activity.startActivity(starter);
        activity.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_register_step01);
        setToolBarTitle(getResources().getString(R.string.tech_register_step_01));
        Log.i(mTag,"after parent base register");
    }

    @Override
    public void goNextPage() {
        RegisterStep02Activity.start(this);
    }

    @Override
    public void goBackPage() {
        SignInActivity.startWithNoAnimate(this);
        this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
        this.finish();
    }
}
