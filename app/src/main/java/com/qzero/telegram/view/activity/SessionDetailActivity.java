package com.qzero.telegram.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxAdapterView;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.SessionDetailContract;
import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatSession;
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

    @BindView(R.id.fb_add_member)
    public FloatingActionButton fb_add_member;
    @BindView(R.id.btn_delete)
    public Button btn_delete;
    @BindView(R.id.btn_submit)
    public Button btn_submit;

    private SessionDetailContract.Presenter presenter;

    private boolean isOperator=false;

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

        RxAdapterView.itemClickEvents(lv_members)
                .subscribe(event -> {
                    Intent intent=new Intent(getContext(),UserInfoDetailActivity.class);
                    intent.putExtra("userName",session.getChatMembers().get(event.getPosition()).getUserName());
                    startActivity(intent);
                });

        RxAdapterView.itemLongClickEvents(lv_members)
                .subscribe(event -> {
                    if(isOperator)
                        showMemberOperatorDialog();
                });

        RxView.clicks(fb_add_member)
                .subscribe(u -> {
                    showAddMemberDialog();
                });
    }

    private void showAddMemberDialog(){
        //TODO SELECT FROM LOCAL FRIEND LIST
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

        EditText et_user_name=new EditText(getContext());
        builder.setView(et_user_name);
        builder.setMessage("请输入对方的用户名");
        builder.setPositiveButton("添加",(a,b)->{
            presenter.addMember(et_user_name.getText().toString());
        });

        builder.setNegativeButton("取消",null).setCancelable(false).show();
    }

    private void showMemberOperatorDialog(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void showNormalUserMode() {
        et_session_name.setVisibility(View.GONE);
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
        isOperator=true;
    }

    @Override
    public void loadSessionInfo(ChatSession session) {
        this.session=session;
        tv_session_name.setText(session.getSessionName());
        et_session_name.setText(session.getSessionName());

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
                        case SessionModule.LEVEL_NORMAL:
                            level="普通用户";
                            break;
                        case SessionModule.LEVEL_OPERATOR:
                            level="管理员";
                            break;
                        case SessionModule.LEVEL_OWNER:
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