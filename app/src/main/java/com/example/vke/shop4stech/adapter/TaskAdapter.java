package com.example.vke.shop4stech.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.model.Task;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by vke on 2016/5/10.
 */
public class TaskAdapter extends BaseAdapter{
    private Context mContext;
    private List<Task> mTaskList;

    public TaskAdapter(Context context, List<Task> list){
        mContext = context;
        this.mTaskList = list;
    }

    @Override
    public int getCount() {
        return mTaskList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTaskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void bindData(List<Task> taskList){
        this.mTaskList = taskList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        if(convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.task_list_item, null);
        }
        else{
            view = convertView;
        }

        TextView orderSerialNumTV = (TextView)view.findViewById(R.id.tech_order_serial_num_text_view);
        TextView taskContentTextTV = (TextView)view.findViewById(R.id.tech_task_content_text_view);
        TextView currentExecutingManTV = (TextView)view.findViewById(R.id.tech_task_execute_man_text_view);
        ImageView taskStateImgV = (ImageView)view.findViewById(R.id.tech_task_state_image_view);
        TextView taskStateTV = (TextView)view.findViewById(R.id.tech_task_state_text_view);

        Task task = mTaskList.get(position);
        orderSerialNumTV.setText(task.getOrderSerialNum());
        taskContentTextTV.setText(task.getOrderType());
        currentExecutingManTV.setText(task.getCurrentExecutingMan());

        switch (task.getOrderState()){
            case "暂停":
                taskStateImgV.setBackgroundResource(R.drawable.icon_running);
                break;
            case "已完成":
                taskStateImgV.setBackgroundResource(R.drawable.icon_complete);
                break;
            case "执行中":
                taskStateImgV.setBackgroundResource(R.drawable.icon_running);
                break;
            case "未开始":
                taskStateImgV.setBackgroundResource(R.drawable.icon_prestart);
                break;
        }
        taskStateTV.setText(task.getOrderState());

        return view;
    }


}
