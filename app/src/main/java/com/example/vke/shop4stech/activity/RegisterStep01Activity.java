package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.constant.MessageType;
import com.example.vke.shop4stech.helper.NetOperationHelper;

import java.util.HashMap;
import java.util.List;

public class RegisterStep01Activity extends BaseRegisterActivity{

    private static final String mTag = "RegisterStep01Activity";
    private List<String> mShopList;
    private static Handler mShopHandler;
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

        setToolBarTitle(getResources().getString(R.string.tech_register_step_01));
        Log.i(mTag,"after parent base register");
        mShopHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MessageType.TYPE_GET_SHOP_LIST_SUCCESS:
                        break;
                    case MessageType.TYPE_GET_SHOP_LIST_FAILED:
                        break;
                    default:
                        Log.e(mTag,"Unknow message type:" + msg.what);
                }
                mShopList = (List<String>)msg.obj;
            }
        };



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

        RegisterStep02Activity.start(this);
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
            if(result != null){
                //send to main thread to update shopList;
                List<String> shops = (List<String>) result.get("shop");
                Message msg = mShopHandler.obtainMessage();
                msg.what = MessageType.TYPE_GET_SHOP_LIST_SUCCESS;
                msg.obj = shops;
                mShopHandler.sendMessage(msg);
            }
            else
            {
                mShopHandler.sendEmptyMessage(MessageType.TYPE_GET_SHOP_LIST_FAILED);
            }
        }
        catch (Exception e){
            Log.e(mTag,e.toString());
        }
    }


}
