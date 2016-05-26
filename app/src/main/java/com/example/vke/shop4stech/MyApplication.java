package com.example.vke.shop4stech;

import android.app.Application;

import com.example.vke.shop4stech.logcollector.LogCollector;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by vke on 2016/5/17.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LogCollector.setDebugMode(true);
        LogCollector.init(getApplicationContext(), "123", null);
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志

        JPushInterface.init(getApplicationContext());
        LogCollector.upload(false);
    }

}
