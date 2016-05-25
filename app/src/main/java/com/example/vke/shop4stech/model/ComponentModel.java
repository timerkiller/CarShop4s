package com.example.vke.shop4stech.model;

/**
 * Created by vke on 2016/5/24.
 */
public class ComponentModel {
    private String mComponentName;
    private String mComponentNum;

    public ComponentModel(String name, String num){
        mComponentName = name;
        mComponentNum = num;
    }

    public void setmComponentName(String mComponentName) {
        this.mComponentName = mComponentName;
    }

    public void setmComponentNum(String mComponentNum) {
        this.mComponentNum = mComponentNum;
    }

    public String getmComponentName() {
        return mComponentName;
    }

    public String getmComponentNum() {
        return mComponentNum;
    }
}
