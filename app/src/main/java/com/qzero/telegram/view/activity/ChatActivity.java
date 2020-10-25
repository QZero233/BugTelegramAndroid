package com.qzero.telegram.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding4.view.RxView;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.ChatContract;
import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.impl.LocalDataStorageImpl;
import com.qzero.telegram.http.bean.Token;
import com.qzero.telegram.presenter.ChatPresenter;
import com.qzero.telegram.utils.TimeUtils;
import com.qzero.telegram.view.BaseActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends BaseActivity implements ChatContract.View {

    private Logger log= LoggerFactory.getLogger(getClass());

    @BindView(R.id.btn_send)
    public Button btn_send;
    @BindView(R.id.et_content)
    public TextInputEditText et_content;
    @BindView(R.id.lv_messages)
    public ListView lv_messages;

    private ChatContract.Presenter presenter;

    private Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String sessionId=getIntent().getStringExtra("sessionId");

        ButterKnife.bind(this);

        presenter=new ChatPresenter(sessionId);
        presenter.attachView(this);
        presenter.loadMessageList(sessionId);
        presenter.registerMessageBroadcastListener();

        lv_messages.setClickable(false);

        try {
            token=new LocalDataStorageImpl(getContext()).getObject(LocalDataStorage.NAME_LOCAL_TOKEN,Token.class);
        } catch (IOException e) {
            log.error("Failed to get token",e);
        }

        RxView.clicks(btn_send)
                .subscribe(o -> {
                    String message=et_content.getText().toString();
                    presenter.sendMessage(token.getOwnerUserName(),message.getBytes());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void showMessageList(List<ChatMessage> messageList) {
        lv_messages.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                if(messageList==null)
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
                ChatMessage message=messageList.get(position);
                log.debug(message+"");

                if(!message.getSenderUserName().equals(token.getOwnerUserName()) && message.getMessageStatus().equals("unread")){
                    presenter.markRead(message.getMessageId());
                }

                View v=View.inflate(getContext(),R.layout.view_chat_message,null);

                TextView tv_msg=v.findViewById(R.id.tv_msg);
                TextView tv_time=v.findViewById(R.id.tv_time);
                TextView tv_status=v.findViewById(R.id.tv_status);

                tv_time.setText(TimeUtils.toStandardTime(message.getSendTime()));
                tv_status.setText(message.getMessageStatus());
                tv_status.setVisibility(View.GONE);

                boolean isStatusNormal=true;
                switch (message.getMessageStatus().toLowerCase()){
                    case "read":
                        tv_status.setText("已读");
                        tv_status.setTextColor(Color.GREEN);
                        break;
                    case "unread":
                        tv_status.setText("未读");
                        tv_status.setTextColor(Color.RED);
                        break;
                    default:
                        isStatusNormal=false;
                        tv_status.setTextColor(Color.BLUE);
                        break;
                }

                if(!isStatusNormal){
                    tv_status.setVisibility(View.VISIBLE);
                }

                if(token!=null){
                    if(token.getOwnerUserName().equals(message.getSenderUserName())){
                        //Send by me
                        tv_msg.setGravity(Gravity.RIGHT);
                        tv_status.setGravity(Gravity.RIGHT);
                        tv_time.setGravity(Gravity.RIGHT);

                        tv_status.setVisibility(View.VISIBLE);
                    }
                }

                if(message!=null && message.getContent()!=null){
                    tv_msg.setText(new String(message.getContent()));
                }


                return v;
            }
        });
    }
}