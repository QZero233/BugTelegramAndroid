package com.qzero.telegram.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxAdapterView;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.SessionDetailContract;
import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.presenter.SessionDetailPresenter;
import com.qzero.telegram.view.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SessionDetailActivity extends BaseActivity implements SessionDetailContract.View {

    @BindView(R.id.lv_members)
    public ListView lv_members;

    @BindView(R.id.tv_session_name)
    public TextView tv_session_name;
    @BindView(R.id.et_session_name)
    public TextInputEditText et_session_name;
    @BindView(R.id.til_session_name)
    public TextInputLayout til_session_name;

    @BindView(R.id.fb_add_member)
    public FloatingActionButton fb_add_member;
    @BindView(R.id.btn_delete)
    public Button btn_delete;
    @BindView(R.id.btn_submit)
    public Button btn_submit;
    @BindView(R.id.btn_quit)
    public Button btn_quit;

    private SessionDetailContract.Presenter presenter;

    private boolean isOperator=false;
    private boolean isDeleted=false;

    private ChatSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        ButterKnife.bind(this);

        presenter=new SessionDetailPresenter();
        presenter.attachView(this);

        String sessionId=getIntent().getStringExtra("sessionId");
        presenter.initView(sessionId);

        presenter.registerListener();

        RxAdapterView.itemClickEvents(lv_members)
                .subscribe(event -> {
                    Intent intent=new Intent(getContext(),UserInfoDetailActivity.class);
                    intent.putExtra("userName",session.getChatMembers().get(event.getPosition()).getUserName());
                    startActivity(intent);
                });

        RxAdapterView.itemLongClickEvents(lv_members)
                .subscribe(event -> {
                    if(isDeleted)
                        return;

                    if(isOperator)
                        showMemberOperatorDialog(event.getPosition(),session.getChatMembers().get(event.getPosition()));
                });

        RxView.clicks(fb_add_member)
                .subscribe(u -> {
                    showAddMemberDialog();
                });

        RxView.clicks(btn_quit)
                .subscribe(u -> {
                    quitSession();
                });

        RxView.clicks(btn_submit)
                .subscribe(u-> {
                    submitUpdates();
                });

        RxView.clicks(btn_delete)
                .subscribe(u ->{
                    deleteSession();
                });
    }

    private void deleteSession(){
        if(!isDeleted){
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            builder.setMessage("确认删除此会话？\n此操作不可逆")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("删除",(a,b) -> {
                        presenter.deleteSessionRemotely();
                    })
                    .setCancelable(false)
                    .show();
        }else{
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            builder.setMessage("确认永久删除此会话？\n这将顺带删除此会话的所有聊天记录且此操作不可逆！");
            builder.setNegativeButton("取消",null);
            builder.setPositiveButton("删除",(a,b) -> {
                presenter.deleteSessionLocally();
            });
            builder.setCancelable(false).show();
        }

    }

    private void submitUpdates(){
        String sessionName=et_session_name.getText().toString();
        presenter.updateSessionName(sessionName);
    }

    private void quitSession(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setMessage("是否退出会话？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确认",(a,b) -> {presenter.quitSession();})
                .setCancelable(false)
                .show();
    }

    private void showAddMemberDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

        String[] friendNameArray=presenter.getFriendNames();
        ListView listView=new ListView(getContext());
        listView.setAdapter(new ArrayAdapter<>(getContext(),R.layout.view_personal_info_sp_tv,friendNameArray));

        RxAdapterView.itemClickEvents(listView)
                .subscribe(event-> {
                    String name=friendNameArray[event.getPosition()];
                    presenter.addMember(name);
                });

        builder.setView(listView);
        builder.setMessage("请选择要添加的用户");
        builder.setNegativeButton("取消",null).setCancelable(false).show();
    }

    private AlertDialog operationDialog=null;

    private void showMemberOperatorDialog(int position,ChatMember member){
        View v=View.inflate(getContext(),R.layout.view_manage_member,null);

        TextView tv_user_name=v.findViewById(R.id.tv_user_name);
        Spinner sp_level=v.findViewById(R.id.sp_level);
        Button btn_submit=v.findViewById(R.id.btn_submit);
        Button btn_remove=v.findViewById(R.id.btn_remove);

        tv_user_name.setText(member.getUserName());
        if(member.getLevel()<=getResources().getStringArray(R.array.array_session_member_level).length){
            sp_level.setSelection(member.getLevel());
        }

        RxView.clicks(btn_submit)
                .subscribe(u -> {
                    member.setLevel(sp_level.getSelectedItemPosition());
                    presenter.updateMember(member);
                    if(operationDialog!=null){
                        operationDialog.dismiss();
                        operationDialog=null;
                    }
                });
        RxView.clicks(btn_remove)
                .subscribe(u -> {
                    presenter.deleteMember(member.getUserName());
                    if(operationDialog!=null){
                        operationDialog.dismiss();
                        operationDialog=null;
                    }
                });


        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        operationDialog=builder.setView(v)
                .setNegativeButton("取消",null)
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterListener();
        presenter.detachView();
    }

    @Override
    public void showNormalUserMode() {
        til_session_name.setVisibility(View.GONE);
        btn_delete.setVisibility(View.GONE);
        btn_submit.setVisibility(View.GONE);
        fb_add_member.setVisibility(View.GONE);
    }

    @Override
    public void showOperatorMode() {
        tv_session_name.setVisibility(View.GONE);
        btn_delete.setVisibility(View.GONE);
        isOperator=true;
    }

    @Override
    public void showOwnerMode() {
        tv_session_name.setVisibility(View.GONE);
        btn_quit.setVisibility(View.GONE);
        isOperator=true;
    }

    @Override
    public void showDeletedMode() {
        til_session_name.setVisibility(View.GONE);
        btn_delete.setVisibility(View.VISIBLE);
        btn_submit.setVisibility(View.GONE);
        btn_quit.setVisibility(View.GONE);
        fb_add_member.setVisibility(View.GONE);
        isDeleted=true;
    }

    @Override
    public void loadSessionInfo(ChatSession session) {
        this.session=session;
        String sessionName=session.getSessionParameter(ChatSessionParameter.NAME_SESSION_NAME);
        tv_session_name.setText(sessionName);
        et_session_name.setText(sessionName);

        List<ChatMember> memberList=session.getChatMembers();
        if(memberList!=null){
            lv_members.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return memberList.size();
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
                    ChatMember member=memberList.get(position);

                    View view=View.inflate(getContext(),R.layout.view_session_detail_member,null);

                    TextView tv_user_name=view.findViewById(R.id.tv_user_name);
                    TextView tv_level=view.findViewById(R.id.tv_level);

                    tv_user_name.setText(member.getUserName());

                    String level="普通用户";
                    switch (member.getLevel()){
                        case ChatMember.LEVEL_NORMAL:
                            level="普通用户";
                            break;
                        case ChatMember.LEVEL_OPERATOR:
                            level="管理员";
                            break;
                        case ChatMember.LEVEL_OWNER:
                            level="会话拥有者";
                            break;
                    }
                    tv_level.setText(level);

                    return view;
                }
            });
        }
    }
}