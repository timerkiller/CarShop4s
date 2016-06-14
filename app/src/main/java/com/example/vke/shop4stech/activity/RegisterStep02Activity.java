package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.constant.Prompt;
import com.example.vke.shop4stech.customLayout.WheelView;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.helper.StringHelper;
import com.example.vke.shop4stech.model.PersonalInfo;

import java.util.HashMap;
import java.util.List;

public class RegisterStep02Activity extends BaseRegisterActivity {

    private static final String mTag = "RegisterStep02Activity";

    private EditText mUserNameEditText,mStaffIdEditText,mJobTypeEditText,mStationEditText,mTeamEditText,mPhoneEditText;

    private Handler mShopInfoHandler;

    private List<String> jobTypeLists;

    private  List<String> teamLists;

    private  List<String> stationLists;

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
        PersonalInfo personalInfo = null;
        if(null!=intent){
            personalInfo = intent.getParcelableExtra(RegisterStep03Activity.PERSONAL_INFO);
            mUserNameEditText.setText(personalInfo.getUserName());
            mPhoneEditText.setText(personalInfo.getPhone());
            mStaffIdEditText.setText(personalInfo.getStaffId());
            mJobTypeEditText.setText(personalInfo.getJobType());
            mStationEditText.setText(personalInfo.getStation());
            mTeamEditText.setText(personalInfo.getTeam());
        }
        setToolBarTitle(getResources().getString(R.string.tech_register_step_02));

        mShopInfoHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MessageType.TYPE_GET_SHOP_INFO_SUCCESS:

                        if(msg.obj instanceof String){
                            Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                        }else if(msg.obj instanceof HashMap){
                            HashMap<String,Object> result = (HashMap<String,Object>)msg.obj;
                            //TODO
                            jobTypeLists= (List<String>) result.get(NetOperationHelper.KEY_JOB);
                            teamLists= (List<String>) result.get(NetOperationHelper.KEY_TEAM);
                            stationLists= (List<String>) result.get(NetOperationHelper.KEY_STATION);
                        }
                        break;

                    default:
                        Log.e(mTag,"Unknow message type:" + msg.what);
                }
                //super.handleMessage(msg);
            }
        };

        final PersonalInfo finalPersonalInfo = personalInfo;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getShopInfo(finalPersonalInfo);
            }
        }).start();

		//TODO 写法需要封装
        mJobTypeEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tech_job_type_edit_text:
                        /*隐藏键盘
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        */

                        View view = LayoutInflater.from(RegisterStep02Activity.this).inflate(R.layout.wheelview,null);
                        final WheelView wheelView = (WheelView) view.findViewById(R.id.wheel_view);
                        wheelView.setOffset(2);
                        wheelView.setItems(jobTypeLists);
                        wheelView.setSeletion(3);
                        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
                            public void onSelected(int selectedIndex, String item) {
                                Log.i(mTag, "selectedIndex: " + selectedIndex + ", item: " + item);
                                //selectItem = item;
                            }
                        });

                        new AlertDialog.Builder(RegisterStep02Activity.this)
                                .setTitle("请选择")
                                .setView(view)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mJobTypeEditText.setText(wheelView.getSeletedItem());

                                    }
                                })
                                .setNegativeButton("取消",null)
                                .show();
                        break;
                }


            }
        });


        mStationEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tech_job_station_edit_text:
                        /*隐藏键盘
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        */

                        View view = LayoutInflater.from(RegisterStep02Activity.this).inflate(R.layout.wheelview,null);
                        final WheelView wheelView = (WheelView) view.findViewById(R.id.wheel_view);
                        wheelView.setOffset(2);
                        wheelView.setItems(stationLists);
                        wheelView.setSeletion(3);
                        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
                            public void onSelected(int selectedIndex, String item) {
                                Log.i(mTag, "selectedIndex: " + selectedIndex + ", item: " + item);
                                //selectItem = item;
                            }
                        });

                        new AlertDialog.Builder(RegisterStep02Activity.this)
                                .setTitle("请选择")
                                .setView(view)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mStationEditText.setText(wheelView.getSeletedItem());

                                    }
                                })
                                .setNegativeButton("取消",null)
                                .show();
                        break;
                }


            }
        });


        mTeamEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tech_job_team_edit_text:
                        /*隐藏键盘
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        */

                        View view = LayoutInflater.from(RegisterStep02Activity.this).inflate(R.layout.wheelview,null);
                        final WheelView wheelView = (WheelView) view.findViewById(R.id.wheel_view);
                        wheelView.setOffset(2);
                        wheelView.setItems(teamLists);
                        wheelView.setSeletion(3);
                        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
                            public void onSelected(int selectedIndex, String item) {
                                Log.i(mTag, "selectedIndex: " + selectedIndex + ", item: " + item);
                                //selectItem = item;
                            }
                        });

                        new AlertDialog.Builder(RegisterStep02Activity.this)
                                .setTitle("请选择")
                                .setView(view)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mTeamEditText.setText(wheelView.getSeletedItem());

                                    }
                                })
                                .setNegativeButton("取消",null)
                                .show();
                        break;
                }


            }
        });

    }
    private void initEditText() {
        mUserNameEditText = (EditText) this.findViewById(R.id.tech_user_name_edit_text);
        mPhoneEditText = (EditText)this.findViewById(R.id.tech_phone_edit_text);
        mStaffIdEditText = (EditText)this.findViewById(R.id.tech_job_id_edit_text);
        mJobTypeEditText =(EditText) this.findViewById(R.id.tech_job_type_edit_text);
        mStationEditText = (EditText)this.findViewById(R.id.tech_job_station_edit_text);
        mTeamEditText = (EditText)this.findViewById(R.id.tech_job_team_edit_text);
    }

    private void getShopInfo(PersonalInfo personalInfo){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("mode", "info");
        map.put("4SShop",personalInfo.getmCarShop());
        map.put("registerCode",personalInfo.getmRegisterCode());
        HashMap<String,Object> result = NetOperationHelper.getShopInfoAbout(map);
        Message msg = mShopInfoHandler.obtainMessage();
        if(null!=result){
            msg.what = MessageType.TYPE_GET_SHOP_INFO_SUCCESS;
            msg.obj = result;
            mShopInfoHandler.sendMessage(msg);
        }else {
            msg.what = MessageType.TYPE_GET_SHOP_LIST_FAILED;
            msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
            mShopInfoHandler.sendMessage(msg);
        }



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
        Intent intent = new Intent(this, RegisterStep01Activity.class);
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
