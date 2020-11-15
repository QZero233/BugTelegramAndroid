package com.qzero.telegram.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxAdapterView;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.ChatContract;
import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.gen.ChatSessionDao;
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

public class ChatActivity extends BaseActivity implements ChatContract.View{

    private Logger log= LoggerFactory.getLogger(getClass());

    @BindView(R.id.btn_send)
    public Button btn_send;
    @BindView(R.id.et_content)
    public TextInputEditText et_content;
    @BindView(R.id.lv_messages)
    public ListView lv_messages;

    private ChatContract.Presenter presenter;
    private Token token;

    private List<ChatMessage> messageList;

    private String sessionId;

    private boolean firstShowMessageList=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        sessionId=getIntent().getStringExtra("sessionId");
        ChatSessionDao chatSessionDao= SessionManager.getInstance(getContext()).getSession().getChatSessionDao();
        ChatSession session=chatSessionDao.load(sessionId);
        if(session!=null && session.getDeleted()){
            findViewById(R.id.ll_input).setVisibility(View.GONE);
        }
        if(session!=null){
            setTitle(session.getSessionName());
        }

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

        RxAdapterView.itemLongClicks(lv_messages)
                .subscribe(i -> {
                    ChatMessage message=messageList.get(i);
                    if(message.getMessageType()==null){
                        //Normal message
                        showDeleteConfirmDialog(message.getMessageId(),message.getMessageStatus().equals("deleted"));
                    }

                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.m_session){
            Intent intent=new Intent(getContext(),SessionDetailActivity.class);
            intent.putExtra("sessionId",sessionId);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMessageList(List<ChatMessage> messageList) {

        int position = lv_messages.getFirstVisiblePosition();
        View view = lv_messages.getChildAt(0);
        int top = view==null ? 0:view.getTop();

        this.messageList=messageList;
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

                if(message.getMessageType()!=null && message.getMessageType().equals(ChatMessage.TYPE_SYSTEM_NOTICE)){
                    //System notice, treat it specially
                    View v=View.inflate(getContext(),R.layout.view_chat_message_system,null);

                    TextView tv_system_notice=v.findViewById(R.id.tv_system_notice);
                    tv_system_notice.setText(new String(message.getContent()));

                    return v;
                }

                if(!message.getSenderUserName().equals(token.getOwnerUserName()) && message.getMessageStatus().equals("unread")){
                    presenter.markRead(message.getMessageId());
                }

                View v=View.inflate(getContext(),R.layout.view_chat_message,null);

                TextView tv_msg=v.findViewById(R.id.tv_msg);
                TextView tv_time=v.findViewById(R.id.tv_time);
                TextView tv_status=v.findViewById(R.id.tv_status);
                TextView tv_sender=v.findViewById(R.id.tv_sender);

                tv_time.setText(TimeUtils.toStandardTime(message.getSendTime()));
                tv_status.setText(message.getMessageStatus());
                tv_sender.setText(message.getSenderUserName()+" :");
                tv_status.setVisibility(View.GONE);


                switch (message.getMessageStatus().toLowerCase()){
                    case "read":
                        tv_status.setText("已读");
                        tv_status.setTextColor(Color.GREEN);
                        break;
                    case "unread":
                        tv_status.setText("未读");
                        tv_status.setTextColor(Color.RED);
                        break;
                    case "deleted":
                        tv_status.setText("已删除");

                        tv_status.setTextColor(Color.GRAY);
                        tv_status.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
                        tv_status.setVisibility(View.VISIBLE);

                        tv_msg.setTextColor(Color.GRAY);
                        tv_msg.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);

                        tv_time.setTextColor(Color.GRAY);
                        tv_time.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);

                        tv_sender.setTextColor(Color.GRAY);
                        tv_sender.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);

                        break;
                    default:
                        tv_status.setTextColor(Color.BLUE);
                        tv_status.setVisibility(View.VISIBLE);
                        break;
                }


                if(token!=null){
                    if(token.getOwnerUserName().equals(message.getSenderUserName())){
                        //Send by me
                        tv_msg.setGravity(Gravity.RIGHT);
                        tv_status.setGravity(Gravity.RIGHT);
                        tv_time.setGravity(Gravity.RIGHT);

                        tv_status.setVisibility(View.VISIBLE);
                        tv_sender.setVisibility(View.GONE);
                    }
                }

                if(message!=null && message.getContent()!=null){
                    tv_msg.setText(new String(message.getContent()));
                }


                return v;
            }
        });

        if(!firstShowMessageList)
            lv_messages.setSelectionFromTop(position , top );
        else{
            lv_messages.setSelection(lv_messages.getCount()-1);
            firstShowMessageList=false;
        }
    }

    @Override
    public void clearMessageInput() {
        et_content.setText("");
    }

    private void showDeleteConfirmDialog(String messageId,boolean isPhysical) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

        if(isPhysical){
            builder.setMessage("是否永久删除此消息\n此操作不可逆");
            builder.setPositiveButton("删除", (dialog, which) -> {
                presenter.deleteMessage(messageId,true);
            });
        }else{
            builder.setMessage("是否删除此消息\n本地将仍然保有此消息");
            builder.setPositiveButton("删除",(dialog,which) -> {
                presenter.deleteMessage(messageId,false);
            });
        }

        builder.setNegativeButton("取消",null).setCancelable(false).show();
    }
}