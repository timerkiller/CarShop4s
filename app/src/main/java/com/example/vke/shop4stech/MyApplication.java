package com.example.vke.shop4stech;

import android.app.Application;

import com.example.vke.shop4stech.logcollector.LogCollector;

/**
 * Created by vke on 2016/5/17.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LogCollector.setDebugMode(true);
        LogCollector.init(getApplicationContext(), "", null);
    }
}
