package com.qzero.telegram.view.activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxAdapterView;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.ChatContract;
import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.dao.impl.LocalDataStorageImpl;
import com.qzero.telegram.http.bean.Token;
import com.qzero.telegram.presenter.session.BaseChatPresenter;
import com.qzero.telegram.presenter.session.NormalSessionChatPresenter;
import com.qzero.telegram.presenter.session.PersonalChatPresenter;
import com.qzero.telegram.utils.TimeUtils;
import com.qzero.telegram.view.BaseActivity;
import com.qzero.telegram.view.adapter.ChatMessageNormalAdapter;
import com.qzero.telegram.view.adapter.ChatMessagePersonalAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends BaseActivity implements ChatContract.View {

    private Logger log = LoggerFactory.getLogger(getClass());

    @BindView(R.id.btn_send)
    public Button btn_send;
    @BindView(R.id.et_content)
    public TextInputEditText et_content;
    @BindView(R.id.lv_messages)
    public ListView lv_messages;

    private ChatContract.Presenter presenter;

    private List<ChatMessage> messageList;

    private String sessionId;
    private String sessionType;

    private boolean firstShowMessageList = true;

    private boolean sessionDeleted=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        sessionId=getIntent().getStringExtra("sessionId");
        sessionType=getIntent().getStringExtra("sessionType");

        switch (sessionType){
            case ChatSessionParameter.SESSION_TYPE_NORMAL:
                presenter=new NormalSessionChatPresenter();
                break;
            case ChatSessionParameter.SESSION_TYPE_PERSONAL:
                presenter=new PersonalChatPresenter();
                break;
            default:
                showToast("暂时不支持该类型的会话");
                exit();
                return;
        }

        presenter.attachView(this);

        presenter.initSessionInfo(sessionId);

        presenter.loadMessageList();
        presenter.registerMessageBroadcastListener();

        lv_messages.setClickable(false);

        RxView.clicks(btn_send)
                .subscribe(o -> {
                    String message = et_content.getText().toString();
                    presenter.sendMessage(message.getBytes());
                });

        RxAdapterView.itemLongClicks(lv_messages)
                .subscribe(i -> showMessageEditDialog(i));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterMessageBroadcastListener();
        presenter.detachView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.detachView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.attachView(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.m_session) {
            Intent intent = new Intent(getContext(), SessionDetailActivity.class);
            intent.putExtra("sessionId", sessionId);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMessageList(List<ChatMessage> messageList) {
        int position = lv_messages.getFirstVisiblePosition();
        View view = lv_messages.getChildAt(0);
        int top = view == null ? 0 : view.getTop();

        this.messageList = messageList;

        ListAdapter adapter=null;
        switch (sessionType){
            case ChatSessionParameter.SESSION_TYPE_NORMAL:
                adapter=new ChatMessageNormalAdapter(messageList,getContext());
                break;
            case ChatSessionParameter.SESSION_TYPE_PERSONAL:
                adapter=new ChatMessagePersonalAdapter(messageList,getContext());
                break;
        }

        lv_messages.setAdapter(adapter);

        if (!firstShowMessageList)
            lv_messages.setSelectionFromTop(position, top);
        else {
            lv_messages.setSelection(lv_messages.getCount() - 1);
            firstShowMessageList = false;
        }
    }

    @Override
    public void clearMessageInput() {
        et_content.setText("");
    }

    @Override
    public void showDeletedMode() {
        findViewById(R.id.ll_input).setVisibility(View.GONE);
    }

    @Override
    public void loadSessionInfo(ChatSession session) {
        sessionDeleted=session.isDeleted();
        setTitle(session.getSessionParameter(ChatSessionParameter.NAME_SESSION_NAME));
    }

    private void showDeleteConfirmDialog(String messageId, boolean isPhysical) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if(sessionDeleted){
            isPhysical=true;
        }

        if (isPhysical) {
            builder.setMessage("是否永久删除此消息\n此操作不可逆");
            builder.setPositiveButton("删除", (dialog, which) -> {
                presenter.deleteMessage(messageId, true);
            });
        } else {
            builder.setMessage("是否删除此消息\n本地将仍然保有此消息");
            builder.setPositiveButton("删除", (dialog, which) -> {
                presenter.deleteMessage(messageId, false);
            });
        }

        builder.setNegativeButton("取消", null).setCancelable(false).show();
    }

    private void showEditMessageStatusDialog(String messageId){
        View v=View.inflate(getContext(),R.layout.view_edit_message_status,null);

        String[] statusInEng={"unread","read","urge","useless"};
        String[] statusInChs={"未读","已读","紧急","无用","自定义"};

        Spinner sp_status=v.findViewById(R.id.sp_status);
        TextInputLayout tll_status=v.findViewById(R.id.tll_status);
        TextInputEditText et_status=v.findViewById(R.id.et_status);

        sp_status.setAdapter(new ArrayAdapter(getContext(),R.layout.view_personal_info_sp_tv,statusInChs));

        tll_status.setVisibility(View.GONE);

        sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==statusInChs.length-1){
                    tll_status.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setCancelable(false).setView(v).setNegativeButton("取消",null)
                .setPositiveButton("更改",((dialog, which) -> {
                    String newStatus;
                    if(sp_status.getSelectedItemPosition()==statusInChs.length-1){
                        //Customized
                        newStatus=et_status.getText().toString();
                        if(TextUtils.isEmpty(newStatus) || newStatus.startsWith("#")){
                            showToast("禁止为空或以#开头");
                            return;
                        }
                    }else{
                        //Preset
                        newStatus=statusInEng[sp_status.getSelectedItemPosition()];
                    }
                    presenter.updateMessageStatus(messageId,newStatus);
                })).show();
    }

    private AlertDialog messageEditDialog;
    private void showMessageEditDialog(int position){
        ChatMessage message=messageList.get(position);

        View v=View.inflate(getContext(),R.layout.view_edit_message,null);

        Button btn_delete=v.findViewById(R.id.btn_delete);
        Button btn_copy=v.findViewById(R.id.btn_copy);
        Button btn_edit_status=v.findViewById(R.id.btn_edit_status);

        if(message.getMessageType()!=null){
            switch (message.getMessageType()){
                case ChatMessage.TYPE_SYSTEM_NOTICE:
                    btn_edit_status.setVisibility(View.GONE);
                    break;
            }
        }

        if(message.getMessageStatus()!=null){
            switch (message.getMessageStatus()){
                case ChatMessage.STATUS_DELETED:
                    btn_edit_status.setVisibility(View.GONE);
                    break;
            }
        }

        if(sessionDeleted){
            btn_edit_status.setVisibility(View.GONE);
        }

        RxView.clicks(btn_delete)
                .subscribe(u -> {
                    String status=message.getMessageStatus();
                    boolean physically=false;
                    if(status!=null && status.equals(ChatMessage.STATUS_DELETED))
                        physically=true;

                    if(message.getMessageType().equals(ChatMessage.TYPE_SYSTEM_NOTICE)){
                        physically=true;
                    }

                    showDeleteConfirmDialog(message.getMessageId(),physically);

                    if(messageEditDialog!=null){
                        messageEditDialog.dismiss();
                        messageEditDialog=null;
                    }
                });

        RxView.clicks(btn_copy)
                .subscribe(u -> {
                   String content=new String(message.getContent());
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("messageContent"+message.getMessageId(), content);
                    cm.setPrimaryClip(mClipData);

                    showToast("复制成功");

                    if(messageEditDialog!=null){
                        messageEditDialog.dismiss();
                        messageEditDialog=null;
                    }
                });

        RxView.clicks(btn_edit_status)
                .subscribe(u -> {
                    showEditMessageStatusDialog(message.getMessageId());

                    if(messageEditDialog!=null){
                        messageEditDialog.dismiss();
                        messageEditDialog=null;
                    }
                });


        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        messageEditDialog=builder.setView(v).setNegativeButton("关闭",null).setCancelable(false).show();
    }
}