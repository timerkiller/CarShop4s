package com.example.vke.shop4stech.activity;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getFragmentManager().beginTransaction().replace(R.id.home_container, TaskFragment.newInstance()).commit();

        initContentViews();
        Log.d(m_Tag,"view create done !!!!");
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

        initClickListener();
    }

    private void initClickListener(){
        mTaskLinearLayOut.setOnClickListener(this);
        mMessageLinearLayOut.setOnClickListener(this);
        mPersonalInfoLinearLayOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        //启动相应界面的Fragment,
        FragmentManager fragmentManager = getFragmentManager();
        switch (v.getId()){
            case R.id.tech_line_container_task:
                mTaskButton.setBackgroundResource(R.drawable.icon_bottom_mission_green);
                mMessageButton.setBackgroundResource(R.drawable.icon_bottom_msg_gray);
                mPersonalInfoButton.setBackgroundResource(R.drawable.icon_bottom_mine_gray);

                mTaskTextView.setTextColor(Color.parseColor("#05C0AB"));
                mMessageTextView.setTextColor(Color.parseColor("#808080"));
                mPersonalInfoTextView.setTextColor(Color.parseColor("#808080"));

                fragmentManager.beginTransaction().replace(R.id.home_container,TaskFragment.newInstance()).commit();
                break;
            case R.id.tech_line_container_message:
                mMessageButton.setBackgroundResource(R.drawable.icon_bottom_msg_green);
                mTaskButton.setBackgroundResource(R.drawable.icon_bottom_mission_gray);
                mPersonalInfoButton.setBackgroundResource(R.drawable.icon_bottom_mine_gray);

                mTaskTextView.setTextColor(Color.parseColor("#808080"));
                mMessageTextView.setTextColor(Color.parseColor("#05C0AB"));
                mPersonalInfoTextView.setTextColor(Color.parseColor("#808080"));

                fragmentManager.beginTransaction().replace(R.id.home_container, MessageFragment.newInstance()).commit();
                break;
            case R.id.tech_line_container_personal_info:
                mTaskButton.setBackgroundResource(R.drawable.icon_bottom_mission_gray);
                mMessageButton.setBackgroundResource(R.drawable.icon_bottom_msg_gray);
                mPersonalInfoButton.setBackgroundResource(R.drawable.icon_bottom_mine_green);

                mTaskTextView.setTextColor(Color.parseColor("#808080"));
                mMessageTextView.setTextColor(Color.parseColor("#808080"));
                mPersonalInfoTextView.setTextColor(Color.parseColor("#05C0AB"));

                fragmentManager.beginTransaction().replace(R.id.home_container, PersonalFragment.newInstance()).commit();
                break;
            default:
                throw new UnsupportedOperationException(
                        "The onClick method has not been implemented for " + getResources()
                                .getResourceEntryName(v.getId()));
        }
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
