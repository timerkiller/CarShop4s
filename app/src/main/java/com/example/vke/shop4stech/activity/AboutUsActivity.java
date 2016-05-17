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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.customLayout.SildingFinishLayout;

public class AboutUsActivity extends BaseSwipeBackActivity {
    ActionBar mActionBar;
//    SildingFinishLayout mSildingFinishLayout;
    public static void start(Activity activty) {
        Intent starter = new Intent(activty, AboutUsActivity.class);
        activty.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Toolbar mToolbar = (Toolbar)this.findViewById(R.id.tech_tool_bar);
        this.setSupportActionBar(mToolbar);

        mActionBar = getSupportActionBar();
//        mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout);
//        mSildingFinishLayout.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {
//                    @Override
//                    public void onSildingFinish() {
//                        AboutUsActivity.this.finish();
//                    }
//                });
//        mSildingFinishLayout.setTouchView(mSildingFinishLayout);
        initActionBarView();

    }

    private void initActionBarView(){
        mActionBar.setTitle("关于我们");
        mActionBar.setDisplayShowHomeEnabled(false);

        mActionBar.setDisplayHomeAsUpEnabled(true);

        Resources resources = getResources();
        //Drawable drawable = resources.getDrawable(R.drawable.actionbar_background,null);
        //mActionBar.setBackgroundDrawable(drawable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_back:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_about_us,menu);
//        return super.onCreateOptionsMenu(menu);
//    }


    // Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }
}
