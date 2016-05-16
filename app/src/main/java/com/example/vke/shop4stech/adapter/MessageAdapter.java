package com.example.vke.shop4stech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.helper.DateTimeHelper;
import com.example.vke.shop4stech.model.UserMessage;

import java.util.List;

/**
 * Created by vke on 2016/5/10.
 */
public class MessageAdapter extends BaseAdapter {

    List<UserMessage> mMessageList;
    Context mContext;

    public MessageAdapter(Context context, List<UserMessage> messageList){
        this.mContext = context;
        mMessageList = messageList;
    }

    public void bindData(List<UserMessage> list){
        this.mMessageList = list;
    }

    @Override
    public int getCount() {
        if (mMessageList != null) {
            return mMessageList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mMessageList != null) {
            return mMessageList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView  = LayoutInflater.from(mContext).inflate(R.layout.message_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.mAuthor = (TextView)convertView.findViewById(R.id.tech_message_author_text_view);
            viewHolder.mContent = (TextView)convertView.findViewById(R.id.tech_message_content_text_view);
            viewHolder.mTimeStamp = (TextView)convertView.findViewById(R.id.tech_message_date_time_text_view);
            viewHolder.mIndex = (TextView)convertView.findViewById(R.id.tech_message_index_text_view);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        UserMessage message = mMessageList.get(position);
        viewHolder.mAuthor.setText(message.getAuthor());
        viewHolder.mContent.setText(message.getContent().substring(0,18)+"...");
        viewHolder.mIndex.setText(message.getIndex());

        String date = DateTimeHelper.timeStamp2Date(message.getTimeStamp(),"yyyy-MM-dd HH:mm:ss");
        viewHolder.mTimeStamp.setText(date.split(" ")[0]);

        return convertView;
    }

    public static class ViewHolder{
        TextView mContent;
        TextView mAuthor;
        TextView mTimeStamp;
        TextView mIndex;
    }
}
