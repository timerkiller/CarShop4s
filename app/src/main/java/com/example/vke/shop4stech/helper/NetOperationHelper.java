package com.example.vke.shop4stech.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.BoolRes;
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
import java.util.Objects;

/**
 * Created by vke on 2016/5/8.
 */
public class NetOperationHelper {
    public static final String KEY_TASKS="tasks";
    public static final String KEY_MESSAGES="messages";
    public static final String KEY_SHOP="shop";
    public static final String KEY_ERROR="error";
    public static final String KEY_TOTAL_PAGE="pageAll";

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
                List<String> shopList = new ArrayList<String>();
                JSONArray shopListArray = respData.getJSONArray("listItems");
                for(int i=0; i< shopListArray.length(); i++){
                    shopList.add(shopListArray.getString(i));
                }

                HashMap<String,Object> respDataMap = new HashMap<String,Object>();
                respDataMap.put(KEY_SHOP,shopList);
                return respDataMap;
            }
            else if(result.equals("error")){
                return parseErrorInfo(respData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }



    public static HashMap<String,Object> getOrderDetail(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.TASK_ORDER,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"getOrderDetail failed");
            return null;
        }

        try{
            String result = respData.getString("ok");
            if(result.equals("ok")){
                return  null;
            }else if( result.equals("error")){
                return parseErrorInfo(respData);
            }
            else {
                throw new UnsupportedOperationException("get unknow result from server");
            }
        }catch (Exception e){
            Log.e(mTag,e.toString());
        }

        return null;
    }

    public static HashMap<String,Object> nextTask(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.TASK_NEXT,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"nextTask failed");
            return null;
        }

        try{
            String result = respData.getString("ok");
            if(result.equals("ok")){
                return  null;
            }else if( result.equals("error")){
                return parseErrorInfo(respData);
            }
            else {
                throw new UnsupportedOperationException("get unknow result from server");
            }
        }catch (Exception e){
            Log.e(mTag,e.toString());
        }

        return null;
    }

    public static HashMap<String,Object> preTask(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.TASK_PRE,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"preTask failed");
            return null;
        }

        try{
            String result = respData.getString("ok");
            if(result.equals("ok")){
                return  null;
            }else if( result.equals("error")){
                return parseErrorInfo(respData);
            }
            else {
                throw new UnsupportedOperationException("get unknow result from server");
            }
        }catch (Exception e){
            Log.e(mTag,e.toString());
        }

        return null;
    }


    public static HashMap<String,Object> pauseTask(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.TASK_PAUSE,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"pauseTask failed");
            return null;
        }

        try{
            String result = respData.getString("ok");
            if(result.equals("ok")){
                return  null;
            }else if( result.equals("error")){
                return parseErrorInfo(respData);
            }
            else {
                throw new UnsupportedOperationException("get unknow result from server");
            }
        }catch (Exception e){
            Log.e(mTag,e.toString());
        }

        return null;
    }

    public static HashMap<String,Object> resumeTask(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.TASK_RESUME,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            Log.e(mTag,"resumeTask failed");
            return null;
        }

        try{
            String result = respData.getString("ok");
            if(result.equals("ok")){
                return  null;
            }else if( result.equals("error")){
                return parseErrorInfo(respData);
            }
            else {
                throw new UnsupportedOperationException("get unknow result from server");
            }
        }catch (Exception e){
            Log.e(mTag,e.toString());
        }

        return null;
    }

    /*
    * 获取有效的accessToken
    * @Param 1.activity
    * @return 1.成功:"ok" 2.失败:"failed"  3.服务器未开: null
    */
    public static String getValidAccessToken(Activity activity){
        String accessToken = PreferencesHelper.getPreferenceAccessToken(activity);
        String result = checkAccessTokenInvalid(accessToken);
        if(result != null){
            if(result.equals("ok")) {
                return accessToken;
            }
            else {
                return "failed";
            }
        }

        return null;
    }

    public static String checkAccessTokenInvalid(String accessToken){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(RequestDataKey.ACCESS_TOKEN,accessToken);
        map.put(RequestDataKey.LOGIN_MODE, "userInfo");

        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.MAINTAIN_USER,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if(respData == null){
            return null;
        }

        try {
            String result = respData.getString("result");
            if (result.equals("ok")) {
                return "ok";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "failed";
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
                dataMap.put(KEY_TASKS,tasks);
                dataMap.put(KEY_TOTAL_PAGE,Integer.parseInt(pageAll));
                return dataMap;
            }
            else if(result.equals("error")){
                return parseErrorInfo(respData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HashMap<String,Object>  parseErrorInfo(JSONObject object){
        try{
            JSONArray errorList = object.getJSONArray("errors");
            String erroInfo = errorList.getJSONObject(0).getString("desc");
            HashMap<String,Object> respDataMap = new HashMap<String,Object>();
            respDataMap.put(KEY_ERROR,erroInfo);
            Log.i(mTag,"Error get from server:" + erroInfo);
            return respDataMap;
        }catch (Exception e){
            Log.e(mTag,e.toString());
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
                respDataMap.put(KEY_MESSAGES,messages);
                respDataMap.put(KEY_TOTAL_PAGE,Integer.parseInt(pageAll));
                return respDataMap;
            }
            else if(result.equals("error")){
                //need handle errors operation
                return parseErrorInfo(respData);
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
            else if(result.equals("error")){
                JSONArray errorList = respData.getJSONArray("errors");
                String erroInfo = errorList.getJSONObject(0).getString("desc");
                Log.e(mTag,"get Personal info failed ,error :" +erroInfo );
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getSmsCode(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.SMS_CODE,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if (respData == null)
        {
            Log.e(mTag,"get SMS info null");
            return null;
        }

        try {
            String result = respData.getString("result");
            if (result.equals("ok")) {
                return respData.getString("encrySMScode");
            }
            else if(result.equals("error")){
                JSONArray errors = respData.getJSONArray("errors");
                String errorInfo = errors.getJSONObject(0).getString("desc");

                return "error"+ " " + errorInfo;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String modifyPassword(HashMap<String,Object> map){
        HttpJsonHelper httpJsonHelper = new HttpJsonHelper(URL.MAINTAIN_USER,map);
        JSONObject respData = httpJsonHelper.httpPostJsonData();
        if (respData == null)
        {
            Log.e(mTag,"get modifyPassword failed");
            return null;
        }

        try {
            String result = respData.getString("result");
            if (result.equals("ok")) {
                return "ok";
            }
            else if(result.equals("error")){
                JSONArray errors = respData.getJSONArray("errors");
                return errors.getJSONObject(0).getString("desc");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }
}
