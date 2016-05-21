package com.example.vke.shop4stech.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.helper.DateTimeHelper;
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
            viewHolder.mDateTV = (TextView)convertView.findViewById(R.id.tech_task_date_text_view);
            viewHolder.mIndex=(TextView)convertView.findViewById(R.id.tech_index);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Task task = mTaskList.get(position);
        viewHolder.mOrderSerialNumTV.setText(task.getOrderSerialNum());
        viewHolder.mTaskContentTextTV.setText(task.getOrderType());
        viewHolder.mCurrentExecutingManTV.setText(task.getCurrentExecutingMan());
        viewHolder.mIndex.setText(task.getIndex());

        String DateTime = DateTimeHelper.timeStamp2Date(task.getOrderDate(),null);
        String Date = DateTime.split(" ")[0];
        String month = Date.split("-")[1];
        String day = Date.split("-")[2];

        String displayStr = day+" "+month+"月";
        final SpannableStringBuilder sb = new SpannableStringBuilder(displayStr);
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(37, 37, 37)); // Span to set text color to some RGB value
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sb.setSpan(fcs, 0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Set the text color for first 4 characters
        sb.setSpan(bss, 0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make them also bold
//        Spannable WordtoSpan = new SpannableString("大字小字");
//        WordtoSpan.setSpan(new AbsoluteSizeSpan(20), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        WordtoSpan.setSpan(new AbsoluteSizeSpan(14), 2, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.mDateTV.setText(sb);

        switch (task.getOrderState()){
            case "暂停":
                viewHolder.mTaskStateImgV.setBackgroundResource(R.drawable.icon_running);
                break;
            case "待评价":
            case "已完成":
                viewHolder.mTaskStateImgV.setBackgroundResource(R.drawable.icon_complete);
                break;
            case "执行中":
                viewHolder.mTaskStateImgV.setBackgroundResource(R.drawable.icon_running);
                break;
            case "未开始":
                viewHolder.mTaskStateImgV.setBackgroundResource(R.drawable.icon_prestart);
                //viewHolder.mTaskStateTV.setTextColor(mContext.getResources().getColor(R.color.colorLightGray));
                break;
        }
        viewHolder.mTaskStateTV.setText(task.getOrderState());

        return convertView;
    }

    public static class ViewHolder{
        TextView mIndex;
        TextView mOrderSerialNumTV;
        TextView mTaskContentTextTV;
        TextView mCurrentExecutingManTV;
        ImageView mTaskStateImgV;
        TextView mTaskStateTV;
        TextView mDateTV;
    }

}
