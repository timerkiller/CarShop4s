package com.example.vke.shop4stech.model;

/**
 * Created by vke on 2016/5/13.
 */
public class PersonalInfo {

    private String mUserName;
    private String mStaffId;
    private String mJobType;
    private String mStation;
    private String mTeam;

    public PersonalInfo(){

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

    public void setJobType(String jobType) {
        this.mJobType = jobType;
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
}
