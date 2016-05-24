package com.example.vke.shop4stech.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

/**
 * Created by vke on 2016/5/14.
 */
public class Task implements Parcelable{
    private String mIndex;//索引，由服务器提供
    private String mOrderType;
    private String mOrderDate;
    private String mOrderSerialNum;
    private String mTaskContent;
    private String mCurrentExecutingMan;
    private String mOrderState;
    private String mCurrentStep;

    public static final Parcelable.Creator<Task> CREATOR = new Task.Creator<Task>(){

        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public Task(){

    }

    public Task(Parcel parcel){
        mIndex = parcel.readString();
        mOrderType = parcel.readString();
        mOrderDate = parcel.readString();
        mOrderSerialNum = parcel.readString();
        mTaskContent = parcel.readString();
        mCurrentExecutingMan = parcel.readString();
        mOrderState = parcel.readString();
        mCurrentStep = parcel.readString();
    }

    public Task(String index,
                String orderType,
                String orderDate,
                String orderSerialNum,
                String taskContent,
                String currentExecutingMan,
                String orderState,
                String currentStep){
        mIndex = index;
        mOrderType = orderType;
        mOrderDate = orderDate;
        mOrderSerialNum = orderSerialNum;
        mTaskContent = taskContent;
        mCurrentExecutingMan = currentExecutingMan;
        mOrderState = orderState;
        mCurrentStep = currentStep;
    }


    public String getCurrentExecutingMan() {
        return "当前执行人: "+mCurrentExecutingMan;
    }
    public String getIndex() {
        return mIndex;
    }

    public String getOrderDate() {
        return mOrderDate;
    }

    public String getOrderSerialNum() {
        return "订单号: "+mOrderSerialNum;
    }

    public String getOrderState() {
        return mOrderState;
    }

    public String getOrderType() {
        if(mOrderType.equals("mantain"))
            return "工位任务: 维修";
        else if(mOrderType.equals("keepGood")){
            return "工位任务: 保养";
        }
        return "工位任务: "+mOrderType;
    }

    public String getTaskContent() {
        return mTaskContent;
    }

    public String getCurrentStep() {
        return mCurrentStep;
    }

    public void setCurrentExecutingMan(String currentExecutingMan) {
        mCurrentExecutingMan = currentExecutingMan;
    }

    public void setIndex(String index) {
        mIndex = index;
    }

    public void setOrderDate(String orderDate) {
        mOrderDate = orderDate;
    }

    public void setOrderSerialNum(String orderSerialNum) {
        mOrderSerialNum = orderSerialNum;
    }

    public void setOrderState(String orderState) {
        mOrderState = orderState;
    }

    public void setOrderType(String orderType) {
        mOrderType = orderType;
    }

    public void setTaskContent(String taskContent) {
        mTaskContent = taskContent;
    }

    public void setCurrentStep(String currentStep) {
        this.mCurrentStep = currentStep;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mIndex);
        dest.writeString(mOrderType);
        dest.writeString(mOrderDate);
        dest.writeString(mOrderSerialNum);
        dest.writeString(mTaskContent);
        dest.writeString(mCurrentExecutingMan);
        dest.writeString(mOrderState);
        dest.writeString(mCurrentStep);
    }
}
