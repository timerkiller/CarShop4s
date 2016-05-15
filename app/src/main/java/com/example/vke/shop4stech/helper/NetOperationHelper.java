package com.example.vke.shop4stech.helper;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.example.vke.shop4stech.adapter.TaskAdapter;
import com.example.vke.shop4stech.constant.URL;
import com.example.vke.shop4stech.model.PersonalInfo;
import com.example.vke.shop4stech.model.Task;

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

    public static void forgetPassword(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.TASK_ORDER,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"modify password failed");
        }
    }

    public static List<Task> getTaskList(HashMap<String,Object> map){
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
                return tasks;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void getUserMessage(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.TASK_ORDER,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"get user message is null");
        }
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
