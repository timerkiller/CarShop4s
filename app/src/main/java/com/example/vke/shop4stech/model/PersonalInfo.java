package com.example.vke.shop4stech.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vke on 2016/5/13.
 */
public class PersonalInfo implements Parcelable{

    public static final Parcelable.Creator<PersonalInfo> CREATOR = new PersonalInfo.Creator<PersonalInfo>(){

        @Override
        public PersonalInfo createFromParcel(Parcel source) {
            return new PersonalInfo(source);
        }

        @Override
        public PersonalInfo[] newArray(int size) {
            return new PersonalInfo[size];
        }
    };
    private String mUserName;
    private String mStaffId;
    private String mJobType;
    private String mStation;
    private String mTeam;
    private String mPhone;
    private String mPassword;
    private String mRegisterCode;
    private String mCarShop;

    public PersonalInfo(){

    }

    public PersonalInfo(Parcel parcel){
        mUserName = parcel.readString();
        mStaffId = parcel.readString();
        mJobType = parcel.readString();
        mStation = parcel.readString();
        mTeam = parcel.readString();
        mPhone = parcel.readString();
        mPassword = parcel.readString();
        mRegisterCode = parcel.readString();
        mCarShop = parcel.readString();
    }

    public PersonalInfo(String userName,String staffId, String jobType,String station, String team){
        mUserName = userName;
        mJobType = jobType;
        mStaffId = staffId;
        mStation = station;
        mTeam = team;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getJobType() {
        return mJobType;
    }

    public String getStaffId() {
        return "工作证号:"+mStaffId;
    }

    public String getTeam() {
        return mTeam;
    }

    public String getStation() {
        return mStation;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setJobType(String jobType) {
        this.mJobType = jobType;
    }

    public void setPhone(String phone) {
        this.mPhone = phone;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public void setStaffId(String staffId) {
        this.mStaffId = staffId;
    }

    public void setStation(String station) {
        this.mStation = station;
    }

    public void setTeam(String team) {
        this.mTeam = team;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmRegisterCode() {
        return mRegisterCode;
    }

    public void setmRegisterCode(String mRegisterCode) {
        this.mRegisterCode = mRegisterCode;
    }

    public String getmCarShop() {
        return mCarShop;
    }

    public void setmCarShop(String mCarShop) {
        this.mCarShop = mCarShop;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mJobType);
        dest.writeString(mPassword);
        dest.writeString(mPhone);
        dest.writeString(mStaffId);
        dest.writeString(mTeam);
        dest.writeString(mStation);
        dest.writeString(mUserName);
        dest.writeString(mRegisterCode);
        dest.writeString(mCarShop);
    }
}
