package com.example.vke.shop4stech.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.activity.HomeActivity;
import com.example.vke.shop4stech.fragment.MessageFragment;
import com.example.vke.shop4stech.helper.DateTimeHelper;
import com.example.vke.shop4stech.helper.NetOperationHelper;
import com.example.vke.shop4stech.model.UserMessage;

import java.util.List;

/**
 * Created by vke on 2016/5/10.
 */
public class MessageAdapter extends BaseAdapter {

    private final static String mTag = "MessageAdapter";
    private boolean mShowMultSelectState;
    private List<UserMessage> mMessageList;
    private Context mContext;
    private ViewHolder mViewHolder;
    private SparseBooleanArray mSelectedItemsIds;
    private boolean mNeedAnimate;

    public MessageAdapter(Context context, List<UserMessage> messageList){
        mContext = context;
        mMessageList = messageList;
        mShowMultSelectState = false;
        mSelectedItemsIds = new SparseBooleanArray();
    }







    public void bindData(List<UserMessage> list){
        mMessageList = list;
    }

    public void setItemMultiCheckable(boolean flag) {
        mShowMultSelectState = flag;
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

    public void setNeedAnimate(boolean mNeedAnimate) {
        this.mNeedAnimate = mNeedAnimate;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView  = LayoutInflater.from(mContext).inflate(R.layout.message_list_item,null);
            mViewHolder = new ViewHolder();
            mViewHolder.mAuthor = (TextView)convertView.findViewById(R.id.tech_message_author_text_view);
            mViewHolder.mContent = (TextView)convertView.findViewById(R.id.tech_message_content_text_view);
            mViewHolder.mTimeStamp = (TextView)convertView.findViewById(R.id.tech_message_date_time_text_view);
            mViewHolder.mIndex = (TextView)convertView.findViewById(R.id.tech_message_index_text_view);
            mViewHolder.mSelector =(CheckBox)convertView.findViewById(R.id.tech_message_check_box);

            convertView.setTag(mViewHolder);
        }
        else
        {
            mViewHolder = (ViewHolder)convertView.getTag();
        }

        // 设置checkbox是否可见
        if (mShowMultSelectState) {
            if(mNeedAnimate){
                Log.i(mTag,"on set animate");

                //mNeedAnimate = false;
                TranslateAnimation animation = new TranslateAnimation(60, 0, 0, 0);
                animation.setDuration(500);
                mViewHolder.mTimeStamp.setAnimation(animation);
                mViewHolder.mContent.setAnimation(animation);

                AnimationSet animationSet = new AnimationSet(true);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                alphaAnimation.setDuration(600);
                animationSet.addAnimation(alphaAnimation);
                mViewHolder.mSelector.startAnimation(animationSet);

            }

            mViewHolder.mSelector.setVisibility(View.VISIBLE);

            // 如果checkbox可见，证明当前处于可多选操作情况下,则根据用户选择情况设置checkbox被选中状态
            if (mSelectedItemsIds.get(position+1)) {
                mViewHolder.mSelector.setChecked(true);
            } else {
                mViewHolder.mSelector.setChecked(false);
            }

        } else {
            if(mNeedAnimate) {
                //mNeedAnimate =false;
                TranslateAnimation animation = new TranslateAnimation(-60, 0, 0, 0);
                animation.setDuration(600);
                mViewHolder.mTimeStamp.setAnimation(animation);
                mViewHolder.mContent.setAnimation(animation);
            }

            mViewHolder.mSelector.setVisibility(View.GONE);
        }


        UserMessage message = mMessageList.get(position);
        mViewHolder.mAuthor.setText(message.getAuthor());
        mViewHolder.mContent.setText(message.getContent());
        mViewHolder.mIndex.setText(message.getIndex());

        String date = DateTimeHelper.timeStamp2Date(message.getTimeStamp(),"yyyy-MM-dd HH:mm:ss");
        mViewHolder.mTimeStamp.setText(date.split(" ")[0]);

        return convertView;
    }

    public static class ViewHolder{
        TextView mContent;
        TextView mAuthor;
        TextView mTimeStamp;
        TextView mIndex;

        CheckBox mSelector;
    }

    public void remove(int position) {
        // TODO Auto-generated method stub
        Log.i(mTag,">>>>>>>>>>>>>> on remove item position:"+position);
        mMessageList.remove(position);
        //NetOperationHelper.removeMessage();
    }


    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
        notifyDataSetChanged();
    }

    public void dataChange() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void selectView(int position, boolean value) {
        if(value){
            mSelectedItemsIds.put(position, true);

        }
        else{
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
