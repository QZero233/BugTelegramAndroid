package com.qzero.telegram.view.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.ChatContract;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.presenter.ChatPresenter;
import com.qzero.telegram.view.BaseActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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



                TextView tv=new TextView(getContext());
                if(message!=null && message.getContent()!=null){
                    tv.setText(new String(message.getContent()));
                }

                return tv;
            }
        });
    }
}