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
import java.io.InputStreamReader;
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

    public JSONObject httpPostJsonData()
    {
        httpCilent = HttpClients.createDefault();
        try
        {
            HttpPost httpPost = new HttpPost(mUrl);

            httpPost.addHeader("Content-Type", "application/json");
            //httpPost.setHeader("Content-Type", "application/json");
            //   httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
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

            Log.i(mTag,mUrl+data.toString());

            httpPost.setEntity(new StringEntity(data.toString(),"UTF-8"));
            response = httpCilent.execute(httpPost);
            Log.i(mTag, response.getStatusLine().getStatusCode()+"");

            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                entity = response.getEntity();
                StringBuffer sb = new StringBuffer();

                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                String s = null;
                while((s = reader.readLine()) != null){
                    sb.append(s);
                }

                retObj = new JSONObject(sb.toString());
                return retObj;
            }
        } catch (Exception e) {
                Log.e(mTag, e.toString());
        }

        return null;
    }

    public void httpGetJsonData(){


    }
}
