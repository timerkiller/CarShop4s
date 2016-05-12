package com.example.vke.shop4stech.helper;

import android.util.Log;

import com.example.vke.shop4stech.constant.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vke on 2016/5/8.
 */
public class NetOperationHelper {
    public static final String mTag="NetOperationHelper";

    public static  String login(HashMap<String,Object> map){

        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.AUTH,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();

        if (respData == null) {
            Log.i(mTag, "login failed");
            return null;
        }
        Log.i(mTag, respData.toString());

        String result = null;
        try {
            result = respData.getString("result");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        if (result.equals("ok")) {

            try {
                return "ok"+" " +respData.getString("accessToken");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }else if (result.equals("error")) {
            try {
                JSONArray errorList = respData.getJSONArray("errors");
                String erroInfo = errorList.getJSONObject(0).getString("desc");
                Log.i(mTag,erroInfo);
                return "failed"+" " +erroInfo;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    public static void register(){

    }

    public static void forgetPassword(){

    }

    public static void getTaskList(){

    }

    public static void getUserMessage(){

    }

    public static void getPersnoalInfo(){

    }
}
