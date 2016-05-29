package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.customLayout.SildingFinishLayout;

public class AboutUsActivity extends BaseSwipeBackActivity implements View.OnClickListener{
    ActionBar mActionBar;
    private static final String mTag ="AboutUsActivity";
//    SildingFinishLayout mSildingFinishLayout;
    public static void start(Activity activty) {
        Intent starter = new Intent(activty, AboutUsActivity.class);
        activty.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        setUpToolbar();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tech_tool_bar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(toolbar!=null) {
            Button backButton = (Button) toolbar.findViewById(R.id.tech_abous_back_button);
            if (backButton != null) {
                backButton.setOnClickListener(this);
            }
        }
        getSupportActionBar().setTitle("关于我们");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tech_abous_back_button:
                finish();
                break;
            default:
                Log.e(mTag,"not support action");
        }
    }
}
