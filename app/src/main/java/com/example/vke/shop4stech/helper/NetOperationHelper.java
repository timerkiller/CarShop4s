package com.example.vke.shop4stech.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.example.vke.shop4stech.adapter.TaskAdapter;
import com.example.vke.shop4stech.constant.RequestDataKey;
import com.example.vke.shop4stech.constant.URL;
import com.example.vke.shop4stech.model.PersonalInfo;
import com.example.vke.shop4stech.model.Task;
import com.example.vke.shop4stech.model.UserMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

    public static void register(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.MAINTAIN_USER,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"register failed");
        }

    }

    public static HashMap<String,Object> getShopList(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.SHOP_4S,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"getShopList failed");
            return null;
        }

        try {
            String result = respData.getString("result");
            if (result.equals("ok")) {
                //return shop list;

            }
            else if(result.equals("error")){
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }



    public static void forgetPassword(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.MAINTAIN_USER,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"modify password failed");
        }
    }


    public static boolean checkAccessTokenInvalid(String accessToken){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,accessToken);
        map.put(RequestDataKey.LOGIN_MODE, "userInfo");


        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.MAINTAIN_USER,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            return false;
        }

        try {
            String result = respData.getString("result");
            if (result.equals("ok")) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 检测网络是否可用
     * @return true:可用，false:不可用
     */
    public static boolean  isNetworkConnected(Activity acitvity) {
        ConnectivityManager cm = (ConnectivityManager) acitvity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    public static HashMap<String,Object> getTaskList(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.TASK_ORDER,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"get task list is null");
            return null;
        }

        try {
            String result = respData.getString("result");
            if (result.equals("ok")) {
                List<Task> tasks = new ArrayList<Task>();
                JSONArray taskList = respData.getJSONArray("listItems");
                String pageAll = respData.getString("pageAll");

                for(int i =0; i<taskList.length();i++){
                    JSONObject taskObject = taskList.getJSONObject(i);
                    Task task = new Task();
                    task.setCurrentExecutingMan(taskObject.getString("userName"));
                    task.setCurrentStep(taskObject.getString("currentStep"));
                    task.setIndex(taskObject.getString("index"));
                    task.setOrderSerialNum(taskObject.getString("serialNum"));
                    task.setOrderDate(taskObject.getString("orderTime"));
                    task.setOrderState(taskObject.getString("state"));
                    task.setOrderType(taskObject.getString("type"));
                    task.setTaskContent(taskObject.getString("type"));

                    tasks.add(task);
                }
                HashMap<String,Object> dataMap = new HashMap<String,Object>();
                dataMap.put("tasks",tasks);
                dataMap.put("pageAll",Integer.parseInt(pageAll));
                return dataMap;
            }
            else if(result.equals("error")){
                //这里还需要解析错误的信息
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static HashMap<String,Object> getUserMessage(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.USER_MESSAGE,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"get user message is null");
            return null;
        }

        try {
            String result = respData.getString("result");
            if (result.equals("ok")) {
                List<UserMessage> messages = new ArrayList<>();
                HashMap<String,Object> respDataMap = new HashMap<String,Object>();
                String pageAll = respData.getString("pageAll");
                JSONArray messageList = respData.getJSONArray("listItems");

                for(int i=0; i<messageList.length(); i++){
                    JSONObject messageObj = messageList.getJSONObject(i);
                    UserMessage message = new UserMessage();
                    message.setAuthor(messageObj.getString("author"));
                    message.setContent(messageObj.getString("content"));
                    message.setIndex(messageObj.getString("index"));
                    message.setTimeStamp(messageObj.getString("timestamp"));
                    messages.add(message);
                }
                respDataMap.put("messages",messages);
                respDataMap.put("pageAll",Integer.parseInt(pageAll));
                return respDataMap;
            }
            else if(result.equals("error")){
                //need handle errors operation
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PersonalInfo getPersnoalInfo(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.MAINTAIN_USER,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if (respData == null)
        {
            Log.e(mTag,"get personal info null");
            return null;
        }

        try {
            String result = respData.getString("result");
            if (result.equals("ok")) {
                String username=respData.getString("userName");
                String staffID=respData.getString("staffID");
                String jobType=respData.getString("jobType");
                String station=respData.getString("station");
                String team=respData.getString("team");

                return new PersonalInfo(username,staffID,jobType,station,team);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
