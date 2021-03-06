package com.example.vke.shop4stech.activity;


//import android.app.FragmentManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.customLayout.CustomViewPager;
import com.example.vke.shop4stech.fragment.MessageFragment;
import com.example.vke.shop4stech.fragment.PersonalFragment;
import com.example.vke.shop4stech.fragment.TaskFragment;
import com.example.vke.shop4stech.helper.PreferencesHelper;
import com.example.vke.shop4stech.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import cn.jpush.android.api.JPushInterface;

public class HomeActivity extends AppCompatActivity
implements View.OnClickListener,View.OnLongClickListener{

    private final static String m_Tag = "HomeActivity";
    private final static String EXTRA_USER_INFO = "UserInfomation";
    private ActionBar mActionBar;
    private Button mTaskButton;
    private Button mMessageButton;
    private Button mPersonalInfoButton;
    private LinearLayout mTaskLinearLayOut;
    private LinearLayout mMessageLinearLayOut;
    private LinearLayout mPersonalInfoLinearLayOut;
    private TextView mTaskTextView;
    private TextView mMessageTextView;
    private TextView mPersonalInfoTextView;
    private ProgressBar mProgressBar;

    private CustomViewPager mViewPager;
    private List<Fragment> mFragmentList;
    private FragmentAdapter mFragmentAdapter;

    private final static int FRAGMENT_ID_TASK_LIST = 0;
    private final static int FRAGMENT_ID_MESSAGE = 1;
    private final static int FRAGMENT_ID_PERSONAL_INFO =2;
    private static Boolean isExit = false;

    private static boolean mAnimateFlag = false;
    public static void start(Activity activity, User user, ActivityOptionsCompat options) {
        Intent starter = getStartIntent(activity, user);
        ActivityCompat.startActivity(activity, starter, options.toBundle());
    }

    public static void start(Context context, User user) {
        Intent starter = getStartIntent(context, user);
        context.startActivity(starter);

    }

    @NonNull
    static Intent getStartIntent(Context context, User user) {
        Intent starter = new Intent(context, HomeActivity.class);
        starter.putExtra(EXTRA_USER_INFO, user);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mActionBar = getSupportActionBar();
        initContentViews();
        Log.d(m_Tag,"view create done !!!!");

    }

    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitBy2Click(); //调用双击退出函数
        }
        return false;
    }

    private void exitBy2Click() {
        Timer exitTimer= null;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this,R.string.tech_touch_again_to_exit, Toast.LENGTH_SHORT).show();
            exitTimer = new Timer();
            exitTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    @Override
    public boolean onLongClick(View v) {

        Log.i(m_Tag,"on fragement long click ");
        return true;
    }

    public static class FragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;
        public FragmentAdapter(FragmentManager fm,List<Fragment> fragments) {
            super(fm);
            this.mFragments=fragments;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    private void initContentViews(){
        mTaskButton = (Button)this.findViewById(R.id.tech_task_list_btn);
        mMessageButton = (Button)this.findViewById(R.id.tech_message_btn);
        mPersonalInfoButton=(Button)this.findViewById(R.id.tech_personal_info_btn);
        mTaskLinearLayOut = (LinearLayout)this.findViewById(R.id.tech_line_container_task);
        mMessageLinearLayOut = (LinearLayout)this.findViewById(R.id.tech_line_container_message);
        mPersonalInfoLinearLayOut  = (LinearLayout)this.findViewById(R.id.tech_line_container_personal_info);
        mTaskTextView = (TextView)this.findViewById(R.id.tech_task_list_text);
        mMessageTextView = (TextView)this.findViewById(R.id.tech_message_text);
        mPersonalInfoTextView = (TextView)this.findViewById(R.id.tech_personal_info_text);

        mTaskButton.setBackgroundResource(R.drawable.icon_bottom_mission_green);
        mTaskTextView.setTextColor(Color.parseColor("#05C0AB"));

        mFragmentList = new ArrayList<>();
        mFragmentList.add(TaskFragment.newInstance());
        mFragmentList.add(MessageFragment.newInstance());
        mFragmentList.add(PersonalFragment.newInstance());

        mViewPager = (CustomViewPager) this.findViewById(R.id.tech_view_pager);
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),mFragmentList);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.addOnPageChangeListener(new TabOnPageChangeListener());

        mProgressBar = (ProgressBar)this.findViewById(R.id.tech_activity_home_progress_bar);

        mActionBar.setTitle("4S技师端");
        mActionBar.setDisplayShowHomeEnabled(false);
        Resources resources = getResources();
        //Drawable  drawable = resources.getDrawable(R.drawable.actionbar_background,null);
       // mActionBar.setBackgroundDrawable(drawable);
        initClickListener();
    }

    private void initClickListener(){
        mTaskLinearLayOut.setOnClickListener(this);
        mMessageLinearLayOut.setOnClickListener(this);
        mPersonalInfoLinearLayOut.setOnClickListener(this);

        Log.i(m_Tag,"Set on click listener done!");
    }

    public void setWidgetsClickable(boolean flag){
        if(flag){
            mViewPager.setNoScroll(false);
            mTaskLinearLayOut.setClickable(true);
            mMessageLinearLayOut.setClickable(true);
            mPersonalInfoLinearLayOut.setClickable(true);
        }
        else {
            mViewPager.setNoScroll(true);
            mTaskLinearLayOut.setClickable(false);
            mMessageLinearLayOut.setClickable(false);
            mPersonalInfoLinearLayOut.setClickable(false);
        }
    }

    @Override
    public void onClick(View v){
        //启动相应界面的Fragment,
        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.d(m_Tag,"In Click function");
        resetWidget();
        switch (v.getId()){
            case R.id.tech_line_container_task:
                mTaskButton.setBackgroundResource(R.drawable.icon_bottom_mission_green);
                mTaskTextView.setTextColor(Color.parseColor("#05C0AB"));

                Log.d(m_Tag,"In task tab click");
                mViewPager.setCurrentItem(FRAGMENT_ID_TASK_LIST);

                break;
            case R.id.tech_line_container_message:
                mMessageButton.setBackgroundResource(R.drawable.icon_bottom_msg_green);
                mMessageTextView.setTextColor(Color.parseColor("#05C0AB"));

                Log.d(m_Tag,"In message tab click");
                mViewPager.setCurrentItem(FRAGMENT_ID_MESSAGE);
                break;
            case R.id.tech_line_container_personal_info:
                mPersonalInfoButton.setBackgroundResource(R.drawable.icon_bottom_mine_green);
                mPersonalInfoTextView.setTextColor(Color.parseColor("#05C0AB"));

                Log.d(m_Tag,"In personal info tab click");
                mViewPager.setCurrentItem(FRAGMENT_ID_PERSONAL_INFO);
                break;
            default:
                throw new UnsupportedOperationException(
                        "The onClick method has not been implemented for " + getResources()
                                .getResourceEntryName(v.getId()));
        }
    }

    public void setContentViewVisibility(boolean flag) {
        if(flag){
            mProgressBar.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            if(mAnimateFlag){
                mAnimateFlag = false;
                AnimationSet animationSet = new AnimationSet(true);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                alphaAnimation.setDuration(600);
                animationSet.addAnimation(alphaAnimation);
                mViewPager.startAnimation(animationSet);
            }
        }else {
            if(mProgressBar != null && mViewPager != null){
                mProgressBar.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setAnimateFlag(boolean flag){
        mAnimateFlag = flag;
    }

    class TabOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.i(m_Tag,"position:" + position+" positionOffset:"+positionOffset + " positionOffsetPixels" + positionOffsetPixels);
//            if (position == FRAGMENT_ID_TASK_LIST) {
//                mMessageButton.setBackgroundResource(R.drawable.icon_bottom_msg_green);
//                mMessageTextView.setTextColor(Color.parseColor("#05C0AB"));
//
//                mTaskButton.setBackgroundResource(R.drawable.icon_bottom_mission_green);
//                mTaskTextView.setTextColor(Color.parseColor("#05C0AB"));
//
//                mMessageButton.setAlpha(positionOffset);
//                mMessageTextView.setAlpha(positionOffset);
//
//
//                mTaskButton.setAlpha(1-positionOffset);
//                mTaskTextView.setAlpha(1-positionOffset);
//
//            }
//            else if(position == FRAGMENT_ID_MESSAGE) {
//                mMessageButton.setAlpha(1-positionOffset);
//                mMessageTextView.setAlpha(1-positionOffset);
//            }
//            else if (position == FRAGMENT_ID_PERSONAL_INFO){
//                mPersonalInfoButton.setAlpha(1-positionOffset);
//                mPersonalInfoTextView.setAlpha(1-positionOffset);
//            }
        }

        @Override
        public void onPageSelected(int position) {
            //重置所有TextView和Button的字体颜色
            resetWidget();
            switch (position) {
                case FRAGMENT_ID_TASK_LIST:
                    mTaskButton.setBackgroundResource(R.drawable.icon_bottom_mission_green);
                    mTaskTextView.setTextColor(Color.parseColor("#05C0AB"));
                    break;
                case FRAGMENT_ID_MESSAGE:
                    mMessageButton.setBackgroundResource(R.drawable.icon_bottom_msg_green);
                    mMessageTextView.setTextColor(Color.parseColor("#05C0AB"));
                    break;
                case FRAGMENT_ID_PERSONAL_INFO:
                    mPersonalInfoButton.setBackgroundResource(R.drawable.icon_bottom_mine_green);
                    mPersonalInfoTextView.setTextColor(Color.parseColor("#05C0AB"));
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public void resetWidget(){
        mTaskButton.setBackgroundResource(R.drawable.icon_bottom_mission_gray);
        mMessageButton.setBackgroundResource(R.drawable.icon_bottom_msg_gray);
        mPersonalInfoButton.setBackgroundResource(R.drawable.icon_bottom_mine_gray);

        mTaskTextView.setTextColor(Color.parseColor("#808080"));
        mMessageTextView.setTextColor(Color.parseColor("#808080"));
        mPersonalInfoTextView.setTextColor(Color.parseColor("#808080"));

        mTaskButton.setAlpha(1.0f);
        mMessageButton.setAlpha(1.0f);
        mPersonalInfoButton.setAlpha(1.0f);

        mTaskTextView.setAlpha(1.0f);
        mMessageTextView.setAlpha(1.0f);
        mPersonalInfoTextView.setAlpha(1.0f);
        Log.i(m_Tag,"reset widget");
    }

    private void signOut(){
        Log.i(m_Tag,"signOut");
        PreferencesHelper.signOut(this);
        //this.overridePendingTransition(R.anim.animate_out_alpha,R.anim.animate_enter_alpha);
        //SignInActivity.start(this);
        SignInActivity.startWithNoAnimate(this);
        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_home,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
