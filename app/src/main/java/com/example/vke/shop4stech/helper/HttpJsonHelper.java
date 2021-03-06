package com.example.vke.shop4stech.helper;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by vke on 2016/5/11.
 */
public class HttpJsonHelper {
    private final static String mTag = "HttpJsonHelper";
    public static CloseableHttpClient httpCilent = null;
    public static HttpResponse response = null;
    public static HttpEntity entity = null;

    //public static String retStr = null;
    public JSONObject retObj = null;

    private Map<String,Object> mDataMap;
    private String mUrl;


    public HttpJsonHelper(String url,HashMap<String, Object> map){
        mUrl = url;
        mDataMap = map;
    }

    public JSONObject httpPostJsonData(){
        JSONObject data = new JSONObject();
        try
        {
            Iterator<?> iter = mDataMap.entrySet().iterator();
            while (iter.hasNext()) {
                @SuppressWarnings("rawtypes")
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                data.put((String) key, val);
                //	Log.i(mTag, data.toString());
            }
        } catch (JSONException e) {
            Log.e(mTag, e.toString());
        }

        String response = SendRequest(mUrl,data.toString());
        Log.d(mTag,"resp:"+response);
        try {
            retObj =new JSONObject(response);
            return retObj;
        }catch (JSONException e){
            Log.d(mTag,e.toString());
        }

        return null;
    }

    public static String SendRequest(String adress_Http, String strJson) {

        String returnLine = "";
        try {

            Log.i(mTag,"**************开始http通讯**************");
            Log.i(mTag,"**************调用的接口地址为**************" + adress_Http);
            Log.i(mTag,"**************请求发送的数据为**************" + strJson);
            URL my_url = new URL(adress_Http);
            HttpURLConnection connection = (HttpURLConnection) my_url.openConnection();
            connection.setDoOutput(true);

            connection.setDoInput(true);

            connection.setRequestMethod("POST");

            connection.setUseCaches(false);

            connection.setInstanceFollowRedirects(true);

            connection.setRequestProperty("Content-Type", "application/json");

            connection.connect();
            DataOutputStream out = new DataOutputStream(connection
                    .getOutputStream());

            byte[] content = strJson.getBytes("utf-8");

            out.write(content, 0, content.length);
            out.flush();
            out.close(); // flush and close

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

            //StringBuilder builder = new StringBuilder();

            String line = "";

            Log.i(mTag,"Contents of post request start");

            while ((line = reader.readLine()) != null) {
                // line = new String(line.getBytes(), "utf-8");
                returnLine += line;

                Log.d(mTag,line);

            }

            Log.i(mTag,"Contents of post request ends");

            reader.close();
            connection.disconnect();
            Log.i(mTag,"========返回的结果的为========" + returnLine);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnLine;
    }

    public void httpGetJsonData(){


    }
}
