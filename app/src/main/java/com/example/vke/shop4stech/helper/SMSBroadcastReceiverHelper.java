package com.example.vke.shop4stech.helper;

/**
 * Created by vke on 2016/5/19.
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;


/**
 * 短信监听
 */
public class SMSBroadcastReceiverHelper extends BroadcastReceiver {

    private static final String mTag = "SMSBroadcastReceiver";
    private static MessageListener mMessageListener;
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    public SMSBroadcastReceiverHelper() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(mTag,"new message received");
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if(pdus !=null){
                for(Object pdu:pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte [])pdu);
                    String sender = smsMessage.getDisplayOriginatingAddress();
                    //短信内容
                    String content = smsMessage.getDisplayMessageBody();
                    long date = smsMessage.getTimestampMillis();
                    Date tiemDate = new Date(date);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    String time = simpleDateFormat.format(tiemDate);
                    Log.i(mTag,"sender: " +sender);
                    analysisVerify(content,null);
                }
            }
        }

    }

    private void analysisVerify(String message,String sender) {
        char[] msgs = message.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < msgs.length; i++) {
            if ('0' <= msgs[i] && msgs[i] <= '9') {
                sb.append(msgs[i]);
            }
        }

        //过滤不需要读取的短信的发送号码
        if(sender !=null){
            if ("+8613450214963".equals(sender)) {
                mMessageListener.onReceived(sb.toString());
                abortBroadcast();
            }
        }
        else {
            mMessageListener.onReceived(sb.toString());
            abortBroadcast();
        }
    }

    //回调接口
    public interface MessageListener {
        public void onReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        this.mMessageListener = messageListener;
    }
}

