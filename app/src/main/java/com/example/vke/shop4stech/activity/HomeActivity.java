package com.example.vke.shop4stech.activity;


//import android.app.FragmentManager;
import android.support.v4.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.fragment.MessageFragment;
import com.example.vke.shop4stech.fragment.PersonalFragment;
import com.example.vke.shop4stech.fragment.TaskFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class HomeActivity extends AppCompatActivity
implements View.OnClickListener{

    private final static String m_Tag = "HomeActivity";
    private Button mTaskButton;
    private Button mMessageButton;
    private Button mPersonalInfoButton;
    private LinearLayout mTaskLinearLayOut;
    private LinearLayout mMessageLinearLayOut;
    private LinearLayout mPersonalInfoLinearLayOut;
    private TextView mTaskTextView;
    private TextView mMessageTextView;
    private TextView mPersonalInfoTextView;

    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;
    private FragmentAdapter mFragmentAdapter;

    private final static int FRAGMENT_ID_TASK_LIST = 0;
    private final static int FRAGMENT_ID_MESSAGE = 1;
    private final static int FRAGMENT_ID_PERSONAL_INFO =2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initContentViews();
        Log.d(m_Tag,"view create done !!!!");
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

        mViewPager = (ViewPager)this.findViewById(R.id.tech_view_pager);
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),mFragmentList);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.addOnPageChangeListener(new TabOnPageChangeListener());

        initClickListener();
    }

    private void initClickListener(){
        mTaskLinearLayOut.setOnClickListener(this);
        mMessageLinearLayOut.setOnClickListener(this);
        mPersonalInfoLinearLayOut.setOnClickListener(this);

        Log.i(m_Tag,"Set on click listener done!");
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

    class TabOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

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
        Log.i(m_Tag,"reset widget");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //openSearch();
                return true;
            case R.id.action_settings:
                //openSettings();
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
