package com.example.vke.shop4stech.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.activity.TaskMixExecuteActivity;
import com.example.vke.shop4stech.model.ComponentModel;

import java.util.List;

/**
 * Created by vke on 2016/5/21.
 */
public class ComponentAdapter extends BaseAdapter {
    Activity mContext;
    public List<ComponentModel> mComponentList;

    public static class ViewHolder {

        TextView componentName, componentNum;
        Button addBtn,reduceBtn;
    }

    public ComponentAdapter(Activity context, List<ComponentModel> list){
        mContext = context;
        mComponentList =list;
    }

    @Override
    public int getCount() {
        if(mComponentList != null){
            return mComponentList.size();
        }
        return 0;
    }

    public void bindData(List<ComponentModel> list) {
        mComponentList = list;
    }
    @Override
    public Object getItem(int position) {
        if(mComponentList != null){
            return mComponentList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView= LayoutInflater.from(mContext).inflate(R.layout.task_new_component_item, null);
            viewHolder.componentName = (TextView) convertView
                    .findViewById(R.id.task_new_component_item_name_text_view);
            viewHolder.componentNum = (TextView) convertView
                    .findViewById(R.id.task_new_component_item_num_text_view);
            viewHolder.addBtn = (Button) convertView
                    .findViewById(R.id.task_new_component_item_add_button);
            viewHolder.reduceBtn = (Button) convertView
                    .findViewById(R.id.task_new_component_item_reduce_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }

        final TaskMixExecuteActivity activity = (TaskMixExecuteActivity)mContext;
        viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.mActivityType == TaskMixExecuteActivity.ActivityType.TYPE_DONE_VIEW){
                    return;
                }

                int currentNum = Integer.parseInt(viewHolder.componentNum.getText().toString());
                currentNum = currentNum+1;
                viewHolder.componentNum.setText(String.valueOf(currentNum));
            }
        });


        final int needRemovePosition = position;
        viewHolder.reduceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.mActivityType == TaskMixExecuteActivity.ActivityType.TYPE_DONE_VIEW){
                    return;
                }

                int currentNUm = Integer.parseInt(viewHolder.componentNum.getText().toString());
                if(currentNUm == 1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("确认删除该零件吗?");

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mComponentList.remove(needRemovePosition);
                            TaskMixExecuteActivity activity = (TaskMixExecuteActivity)mContext;
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

                    return;
                }
                currentNUm--;
                viewHolder.componentNum.setText(Integer.toString(currentNUm));
            }
        });

        viewHolder.componentName.setText(mComponentList.get(position).getmComponentName());
        viewHolder.componentNum.setText(mComponentList.get(position).getmComponentNum());

        return convertView;
    }
}
