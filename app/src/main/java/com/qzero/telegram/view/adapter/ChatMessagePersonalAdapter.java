package com.qzero.telegram.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qzero.telegram.R;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.utils.LocalStorageUtils;
import com.qzero.telegram.utils.TimeUtils;

import java.util.List;

public class ChatMessagePersonalAdapter extends BaseAdapter {

    private List<ChatMessage> messageList;
    private Context context;

    private String myName;

    public ChatMessagePersonalAdapter(List<ChatMessage> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
        myName= LocalStorageUtils.getLocalTokenUserName(context);
    }

    @Override
    public int getCount() {
        if (messageList == null)
            return 0;
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = messageList.get(position);

        if (message.getMessageType() != null && message.getMessageType().equals(ChatMessage.TYPE_SYSTEM_NOTICE)) {
            //System notice, treat it specially
            View v = View.inflate(context, R.layout.view_chat_message_system, null);

            TextView tv_system_notice = v.findViewById(R.id.tv_system_notice);
            tv_system_notice.setText(new String(message.getContent()));

            return v;
        }

        View v = View.inflate(context, R.layout.view_chat_message, null);

        TextView tv_msg = v.findViewById(R.id.tv_msg);
        TextView tv_time = v.findViewById(R.id.tv_time);
        TextView tv_status = v.findViewById(R.id.tv_status);
        TextView tv_sender = v.findViewById(R.id.tv_sender);

        tv_time.setText(TimeUtils.toStandardTime(message.getSendTime()));
        tv_status.setText(message.getMessageStatus());
        tv_sender.setText(message.getSenderUserName() + " :");

        if(message.getMessageStatus()==null){
            tv_status.setVisibility(View.GONE);
        }else{
            switch (message.getMessageStatus().toLowerCase()) {
                case "read":
                    tv_status.setText("已读");
                    tv_status.setTextColor(Color.GREEN);
                    break;
                case "unread":
                    tv_status.setText("未读");
                    tv_status.setTextColor(Color.CYAN);
                    break;
                case "urge":
                    tv_status.setText("紧急");
                    tv_status.setTextColor(Color.RED);
                    break;
                case "useless":
                    tv_status.setText("无用");
                    tv_status.setTextColor(Color.DKGRAY);
                    break;
                case ChatMessage.STATUS_DELETED:
                    tv_status.setText("已删除");

                    tv_status.setTextColor(Color.GRAY);
                    tv_status.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    tv_status.setVisibility(View.VISIBLE);

                    tv_msg.setTextColor(Color.GRAY);
                    tv_msg.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

                    tv_time.setTextColor(Color.GRAY);
                    tv_time.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

                    tv_sender.setTextColor(Color.GRAY);
                    tv_sender.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

                    break;
                case "empty":
                    tv_status.setVisibility(View.GONE);
                    break;
                default:
                    tv_status.setTextColor(Color.BLUE);
                    tv_status.setText(message.getMessageStatus());
                    break;
            }
        }


        if (myName.equals(message.getSenderUserName())) {
            //Send by me
            tv_msg.setGravity(Gravity.RIGHT);
            tv_status.setGravity(Gravity.RIGHT);
            tv_time.setGravity(Gravity.RIGHT);

            tv_sender.setVisibility(View.GONE);
        }else{
            //If sent by the other, and status is read or unread, status shall be hidden
            if(message.getMessageStatus().equals("read") ||
            message.getMessageStatus().equals("unread"))
                tv_status.setVisibility(View.GONE);
        }

        if (message != null && message.getContent() != null) {
            tv_msg.setText(new String(message.getContent()));
        }

        return v;
    }

}
