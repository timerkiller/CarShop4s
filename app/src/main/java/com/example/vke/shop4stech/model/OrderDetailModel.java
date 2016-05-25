package com.example.vke.shop4stech.model;

import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

/**
 * Created by vke on 2016/5/22.
 */
public class OrderDetailModel{
    private String mOrderSerialNum;
    private String mCurrentStep;
    private String mStepAll;
    private String mOrderType;
    private String mOrderStation;
    private String mPrincipal;
    private String mOperation;
    private String mCanNext;

    private String mOrderState;
    private String mOrderSubState;

    private List<Map<String, Object>> mConsumeComponentsList;//针对已经完成的消耗的零件list,不可添加删除
    private List<String> mStepsList;


    private List<ComponentModel> mExecutingComponentList;//针对执行中或者暂停中的添加的零件列表，可以添加删除零件
    //暂停时需要填充
    private String mPauseTitle;
    private int mPauseTime;

    //执行中需要填充
    private String mCurrentStepTitle;
    private int mCurrentStepSpendTime;

    //完成或待评价
    private  int mTotalSpendTime;

    //未开始


    public void setmExecutingComponentList(List<ComponentModel> mExecutingComponentList) {
        this.mExecutingComponentList = mExecutingComponentList;
    }

    public void setmTotalSpendTime(int mTotalSpendTime) {
        this.mTotalSpendTime = mTotalSpendTime;
    }

    public void setmCanNext(String mCanNext) {
        this.mCanNext = mCanNext;
    }

    public void setmComponentsList(List<Map<String, Object>> mComponentsList) {
        this.mConsumeComponentsList = mComponentsList;
    }

    public void setmCurrentStep(String mCurrentStep) {
        this.mCurrentStep = mCurrentStep;
    }

    public void setmOperation(String mOperation) {
        this.mOperation = mOperation;
    }

    public void setmOrderSerialNum(String mOrderSerialNum) {
        this.mOrderSerialNum = mOrderSerialNum;
    }



    public void setmOrderState(String mOrderState) {
        this.mOrderState = mOrderState;
    }

    public void setmOrderStation(String mOrderStation) {
        this.mOrderStation = mOrderStation;
    }

    public void setmOrderSubState(String mOrderSubState) {
        this.mOrderSubState = mOrderSubState;
    }

    public void setmPauseTime(int mPauseTime) {
        this.mPauseTime = mPauseTime;
    }

    public void setmOrderType(String mOrderType) {
        this.mOrderType = mOrderType;
    }

    public void setmPauseTitle(String mPauseTitle) {
        this.mPauseTitle = mPauseTitle;
    }

    public void setmPrincipal(String mPrincipal) {
        this.mPrincipal = mPrincipal;
    }

    public void setmStepAll(String mStepAll) {
        this.mStepAll = mStepAll;
    }

    public void setmStepsList(List<String> mStepsList) {
        this.mStepsList = mStepsList;
    }

    public void setmCurrentStepSpendTime(int mCurrentStepSpendTime) {
        this.mCurrentStepSpendTime = mCurrentStepSpendTime;
    }

    public void setmCurrentStepTitle(String mCurrentStepTitle) {
        this.mCurrentStepTitle = mCurrentStepTitle;
    }

    public String getmOrderType() {
        String respData;
        switch (mOrderType){
            case "keepGood":
                respData = "保养";
                break;
            case "mantain":
                respData = "维修";
                break;
            default:
                respData = "未知";
        }

        return "工位任务: " +respData;
    }

    public String getmOrderState() {
        return mOrderState;
    }

    public String getmOrderSerialNum() {
        return "订单号: " + mOrderSerialNum;
    }

    public List<Map<String, Object>> getmComponentsList() {
        return mConsumeComponentsList;
    }

    public List<String> getmStepsList() {
        return mStepsList;
    }

    public String getmCanNext() {
        return mCanNext;
    }

    public String getmCurrentStep() {
        return mCurrentStep;
    }

    public String getmOperation() {
        return mOperation;
    }

    public String getmOrderStation() {

        return "工位号: " + mOrderStation;
    }

    public String getmOrderSubState() {
        return mOrderSubState;
    }

    public int getmPauseTime() {
        return mPauseTime;
    }

    public String getmPauseTitle() {
        return mPauseTitle;
    }

    public String getmPrincipal() {
        return mPrincipal;
    }

    public String getmStepAll() {
        return mStepAll;
    }

    public int getmCurrentStepSpendTime() {
        return mCurrentStepSpendTime;
    }

    public String getmCurrentStepTitle() {
        return mCurrentStepTitle;
    }

    public int getmTotalSpendTime() {
        return mTotalSpendTime;
    }

    public List<ComponentModel> getmExecutingComponentList() {
        return mExecutingComponentList;
    }
}


