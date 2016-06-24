package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.constant.Prompt;
import com.example.vke.shop4stech.customLayout.CustomerDialog;
import com.example.vke.shop4stech.customLayout.WheelView;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.model.PersonalInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class RegisterStep01Activity extends BaseRegisterActivity{

    private static final String mTag = "RegisterStep01Activity";
    private List<String> mShopList;
    private Handler mShopHandler;
    private EditText mShopEditText,mRegisterCodeEditText;

    public static void start(Activity activity) {
        Intent starter = new Intent(activity, RegisterStep01Activity.class);
        activity.startActivity(starter);
        activity.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(R.layout.activity_register_step01);

        mShopEditText = (EditText)this.findViewById(R.id.tech_4s_shop_edit_text);
        mRegisterCodeEditText = (EditText)this.findViewById(R.id.tech_register_code_edit_text);

        //获取意图进行数据返现
        Intent intent = getIntent();
        if(null!=intent){
            PersonalInfo personalInfo = intent.getParcelableExtra(RegisterStep03Activity.PERSONAL_INFO);
            if(null!=personalInfo){
                mShopEditText.setText(personalInfo.getmCarShop());
                mRegisterCodeEditText.setText(personalInfo.getmRegisterCode());
            }
        }

        setToolBarTitle(getResources().getString(R.string.tech_register_step_01));
        Log.i(mTag,"after parent base register");
        final Activity activity = this;
        mShopHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MessageType.TYPE_GET_SHOP_LIST_SUCCESS:
                        mShopList = (List<String>)msg.obj;
                        break;
                    case MessageType.TYPE_GET_SHOP_LIST_FAILED:
                        Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                        break;
                    case MessageType.TYPE_GET_SHOP_INFO:
                        HashMap<String,Object> result = (HashMap<String, Object>) msg.obj;
                        if("ok".equals(result.get("ok"))) {
							PersonalInfo personalInfo = new PersonalInfo();
                            personalInfo.setmRegisterCode(mRegisterCodeEditText.getText().toString());
                            personalInfo.setmCarShop(mShopEditText.getText().toString());

                            Intent intent = new Intent(activity,RegisterStep02Activity.class);
                            intent.putExtra(RegisterStep03Activity.PERSONAL_INFO,personalInfo);
                            startActivity(intent);
                            //RegisterStep02Activity.start(this);
                            activity.finish();
                            
                        }else{
                            Toast.makeText(getApplicationContext(), result.get("error").toString(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Log.e(mTag,"Unknow message type:" + msg.what);
                }

            }
        };



        mShopEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tech_4s_shop_edit_text:
                        /*隐藏键盘*/
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						if(null==mShopList){
							Toast.makeText(getApplicationContext(), Prompt.PROMPT_NETWORK_NOT_AVAILABLE, Toast.LENGTH_SHORT).show();
							return ;
						}
                        //自定义对话框
                        final CustomerDialog customerDialog = new CustomerDialog(RegisterStep01Activity.this);
                        //组装wheelview
                        final View view = BaseRegisterActivity.packWheelView(RegisterStep01Activity.this,mShopList, 0, 1,  new Callback() {
                            @Override
                            public void selectCallback(int selectedIndex, String item) {
                                mShopEditText.setText(item);
                                customerDialog.dismiss();
                            }
                        });
                        customerDialog.createDialog("请选择",  "", "",view, null);
                    break;
                }


            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getShopList();
            }
        }).start();


    }

    @Override
    public void goNextPage() {
        if (mShopEditText.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),R.string.tech_shop_should_not_null,Toast.LENGTH_SHORT).show();
            return;
        }

        if(mRegisterCodeEditText.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),R.string.tech_regsiter_should_not_null,Toast.LENGTH_SHORT).show();
            return;
        }

        //校验4s点和注册码是否存在

        //HashMap<String,Object> result = NetOperationHelper.getShopInfoAbout(map);
       /* if(!"ok".equals(result.get("ok"))){
            Toast.makeText(getApplicationContext(),result.get("error").toString(),Toast.LENGTH_SHORT).show();
            return;
        }*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("mode", "info");
                map.put("4SShop",mShopEditText.getText().toString());
                map.put("registerCode",mRegisterCodeEditText.getText().toString());
                HashMap<String,Object> result = NetOperationHelper.getShopInfoAbout(map);
                Message msg = mShopHandler.obtainMessage();
                if(null!=result){
                    msg.what = MessageType.TYPE_GET_SHOP_INFO;
                    msg.obj = result;
                    mShopHandler.sendMessage(msg);
                }
            }
        }).start();

        //Bundle bundle = new Bundle();
        /*PersonalInfo personalInfo = new PersonalInfo();
        personalInfo.setmRegisterCode(mRegisterCodeEditText.getText().toString());
        personalInfo.setmCarShop(mShopEditText.getText().toString());

        Intent intent = new Intent(this,RegisterStep02Activity.class);
        intent.putExtra(RegisterStep03Activity.PERSONAL_INFO,personalInfo);
        startActivity(intent);
        //RegisterStep02Activity.start(this);
        this.finish();*/
    }

    @Override
    public void goBackPage() {
        SignInActivity.startWithNoAnimate(this);
        this.overridePendingTransition(R.anim.base_slide_right_in,R.anim.base_slide_right_out);
        this.finish();
    }

    private void getShopList(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("mode", "title");
        HashMap<String,Object> result= NetOperationHelper.getShopList(map);

        try {
            Message msg = mShopHandler.obtainMessage();
            if(result != null){
                //send to main thread to update shopList;
                List<String> shops = (List<String>) result.get(NetOperationHelper.KEY_SHOP);
                if (shops != null){
                    msg.what = MessageType.TYPE_GET_SHOP_LIST_SUCCESS;
                    msg.obj = shops;
                    mShopHandler.sendMessage(msg);
                }
                else {
                    String errorinfo = (String)result.get(NetOperationHelper.KEY_ERROR);
                    msg.what = MessageType.TYPE_GET_SHOP_LIST_FAILED;
                    msg.obj = errorinfo;
                    mShopHandler.sendMessage(msg);
                }
            }
            else
            {
                msg.what = MessageType.TYPE_GET_SHOP_LIST_FAILED;
                msg.obj = Prompt.PROMPT_SERVER_NOT_AVAILABLE;
                mShopHandler.sendMessage(msg);
            }
        }
        catch (Exception e){
            Log.e(mTag,e.toString());
        }
    }

}
