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
import android.widget.EditText;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.helper.StringHelper;
import com.example.vke.shop4stech.model.PersonalInfo;

public class RegisterStep02Activity extends BaseRegisterActivity {

    private static final String mTag = "RegisterStep02Activity";

    private EditText mUserNameEditText,mStaffIdEditText,mJobTypeEditText,mStationEditText,mTeamEditText,mPhoneEditText;

    public static void start(Activity activity) {
        Intent starter = new Intent(activity, RegisterStep02Activity.class);
        activity.startActivity(starter);
        activity.overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_register_step02);
        initEditText();
        //获取意图进行数据返现
        Intent intent = getIntent();
        if(null!=intent){
            PersonalInfo personalInfo = intent.getParcelableExtra(RegisterStep03Activity.PERSONAL_INFO);
            mUserNameEditText.setText(personalInfo.getUserName());
            mPhoneEditText.setText(personalInfo.getPhone());
            mStaffIdEditText.setText(personalInfo.getStaffId());
            mJobTypeEditText.setText(personalInfo.getJobType());
            mStationEditText.setText(personalInfo.getStation());
            mTeamEditText.setText(personalInfo.getTeam());
        }
        setToolBarTitle(getResources().getString(R.string.tech_register_step_02));
    }
    private void initEditText() {
        mUserNameEditText = (EditText) this.findViewById(R.id.tech_user_name_edit_text);
        mPhoneEditText = (EditText)this.findViewById(R.id.tech_phone_edit_text);
        mStaffIdEditText = (EditText)this.findViewById(R.id.tech_job_id_edit_text);
        mJobTypeEditText =(EditText) this.findViewById(R.id.tech_job_type_edit_text);
        mStationEditText = (EditText)this.findViewById(R.id.tech_job_station_edit_text);
        mTeamEditText = (EditText)this.findViewById(R.id.tech_job_team_edit_text);
    }




    @Override
    public void goNextPage() {
        checkSimpleData();//非空校验
        PersonalInfo personalInfo = getIntent().getParcelableExtra(RegisterStep03Activity.PERSONAL_INFO);
        personalInfo.setUserName(mUserNameEditText.getText().toString());
        personalInfo.setPhone(mPhoneEditText.getText().toString());
        personalInfo.setStaffId(mStaffIdEditText.getText().toString());
        personalInfo.setJobType(mJobTypeEditText.getText().toString());
        personalInfo.setStation(mStationEditText.getText().toString());
        personalInfo.setTeam(mTeamEditText.getText().toString());
        RegisterStep03Activity.start(this, personalInfo);
        this.finish();
    }



    @Override
    public void goBackPage() {
        PersonalInfo mPersonalInfo = (PersonalInfo) getIntent().getParcelableExtra(RegisterStep03Activity.PERSONAL_INFO);
        Intent intent = new Intent(this, RegisterStep03Activity.class);
        intent.putExtra(RegisterStep03Activity.PERSONAL_INFO,mPersonalInfo);
        startActivity(intent);
        //RegisterStep01Activity.start(this);
        this.overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_right_out);
        this.finish();
    }

    private void checkSimpleData(){
        if(StringHelper.isEmpty(mUserNameEditText.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.tech_input_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if(StringHelper.isEmpty(mPhoneEditText.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.tech_input_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        if(StringHelper.isEmpty(mStaffIdEditText.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.tech_input_job_id, Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringHelper.isEmpty(mJobTypeEditText.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.tech_input_job_type, Toast.LENGTH_SHORT).show();
            return;
        }

        if(StringHelper.isEmpty(mStationEditText.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.tech_input_job_station, Toast.LENGTH_SHORT).show();
            return;
        }

        if(StringHelper.isEmpty(mTeamEditText.getText().toString())){
            Toast.makeText(getApplicationContext(),R.string.tech_input_job_team, Toast.LENGTH_SHORT).show();
            return;
        }


    }

}
