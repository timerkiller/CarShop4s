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
        if(mTaskList != null) {
            return mTaskList.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mTaskList!= null){
            return mTaskList.get(position);
        }
        return null;
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

        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.task_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mOrderSerialNumTV = (TextView)convertView.findViewById(R.id.tech_order_serial_num_text_view);
            viewHolder.mTaskContentTextTV = (TextView)convertView.findViewById(R.id.tech_task_content_text_view);
            viewHolder.mCurrentExecutingManTV = (TextView)convertView.findViewById(R.id.tech_task_execute_man_text_view);
            viewHolder.mTaskStateImgV = (ImageView)convertView.findViewById(R.id.tech_task_state_image_view);
            viewHolder.mTaskStateTV = (TextView)convertView.findViewById(R.id.tech_task_state_text_view);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Task task = mTaskList.get(position);
        viewHolder.mOrderSerialNumTV.setText(task.getOrderSerialNum());
        viewHolder.mTaskContentTextTV.setText(task.getOrderType());
        viewHolder.mCurrentExecutingManTV.setText(task.getCurrentExecutingMan());

        switch (task.getOrderState()){
            case "暂停":
                viewHolder.mTaskStateImgV.setBackgroundResource(R.drawable.icon_running);
                break;
            case "已完成":
                viewHolder.mTaskStateImgV.setBackgroundResource(R.drawable.icon_complete);
                break;
            case "执行中":
                viewHolder.mTaskStateImgV.setBackgroundResource(R.drawable.icon_running);
                break;
            case "未开始":
                viewHolder.mTaskStateImgV.setBackgroundResource(R.drawable.icon_prestart);
                break;
        }
        viewHolder.mTaskStateTV.setText(task.getOrderState());

        return convertView;
    }

    public static class ViewHolder{
        TextView mOrderSerialNumTV;
        TextView mTaskContentTextTV;
        TextView mCurrentExecutingManTV;
        ImageView mTaskStateImgV;
        TextView mTaskStateTV;
    }

}
