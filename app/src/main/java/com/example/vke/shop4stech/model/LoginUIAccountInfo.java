package com.example.vke.shop4stech.model;

/**
 * Created by vke on 2016/5/11.
 */
public class LoginUIAccountInfo {
    private String mUserName;
    private String mPassword;
    private boolean mRememberAccountFlag;

    public LoginUIAccountInfo(String userName,String password,boolean rememberAccountFlag){
        mUserName = userName;
        mPassword = password;
        mRememberAccountFlag = rememberAccountFlag;
    }

    public String getUserName(){
        return mUserName;
    }

    public String getPassword(){
        return mPassword;
    }

    public boolean getRememberFlag(){
        return mRememberAccountFlag;
    }
}
