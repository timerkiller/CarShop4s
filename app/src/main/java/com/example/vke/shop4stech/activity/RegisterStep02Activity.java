package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.vke.shop4stech.R;

public class RegisterStep02Activity extends BaseSwipeBackActivity implements View.OnClickListener{

    private static final String mTag = "RegisterStep02Activity";
    private Button mNextButton,mBackButton;

    public static void start(Activity activity) {
        Intent starter = new Intent(activity, RegisterStep02Activity.class);
        activity.startActivity(starter);
        activity.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step02);
        Toolbar toolbar = (Toolbar)this.findViewById(R.id.tech_register_step_tool_bar);
        this.setSupportActionBar(toolbar);
        mBackButton = (Button)this.findViewById(R.id.tech_register_step_back_button);
        mNextButton = (Button)this.findViewById(R.id.tech_register_step_next_button);
        initClickListener();
    }

    private void initClickListener(){
        mBackButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tech_register_step_next_button:
                RegisterStep03Activity.start(this);
                break;
            case R.id.tech_register_step_back_button:
                RegisterStep01Activity.start(this);
                this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
                this.finish();
                break;
            default:
                Log.i(mTag,"Unkonw id click");
        }
    }
}
