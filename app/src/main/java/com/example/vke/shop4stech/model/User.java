package com.example.vke.shop4stech.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vke on 2016/4/20.
 */
public class User implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private final String mUserName;
    private final String mPassword;


    public User(String userName, String password) {
        mUserName = userName;
        mPassword = password;
    }

    protected User(Parcel in) {
        mUserName = in.readString();
        mPassword = in.readString();
    }

    public String getUserName() {
        return mUserName;
    }

    public String getPassword() {
        return mPassword;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUserName);
        dest.writeString(mPassword);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User userTech = (User) o;

        if (!mUserName.equals(userTech.mUserName)) {
            return false;
        }
        if (!mPassword.equals(userTech.mPassword)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mUserName.hashCode();
        result = 31 * result + mPassword.hashCode();
        return result;
    }
}

