package com.example.vke.shop4stech.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.fragment.TaskFragment;

import java.util.zip.Inflater;

public class HomeActivity extends AppCompatActivity
implements View.OnClickListener{

    private final static String m_Tag = "HomeActivity";
    private Button mTaskButton;
    private Button mMessageButton;
    private Button mPersonalInfoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getFragmentManager().beginTransaction().replace(R.id.home_container, TaskFragment.newInstance()).commit();
        Log.d(m_Tag,"view create done !!!!");
    }

    @Override
    public void onClick(View v){
        //启动相应界面的Fragment,
        switch (v.getId()){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
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
