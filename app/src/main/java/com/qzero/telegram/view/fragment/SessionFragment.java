package com.qzero.telegram.view.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding4.view.RxView;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.SessionContract;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.presenter.SessionPresenter;
import com.qzero.telegram.view.BaseFragment;
import com.qzero.telegram.view.activity.ChatActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SessionFragment extends BaseFragment implements SessionContract.View, AdapterView.OnItemClickListener {

    @BindView(R.id.fb_add)
    public FloatingActionButton fb_add;
    @BindView(R.id.lv_sessions)
    public ListView lv_sessions;

    private SessionContract.Presenter presenter;

    private List<ChatSession> sessionList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(getContext(),R.layout.fragment_session,null);

        ButterKnife.bind(this,view);

        lv_sessions.setOnItemClickListener(this);
        presenter=new SessionPresenter();

        RxView.clicks(fb_add).subscribe(u->{showAddDialog();});

        return view;
    }

    private void showAddDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

        EditText et_session_name=new EditText(getContext());
        builder.setView(et_session_name);

        builder.setMessage("请输入新会话的名称：");
        builder.setNegativeButton("取消",null)
                .setPositiveButton("新建",(a,b) ->
                        {presenter.createNewSession(et_session_name.getText().toString());});

        builder.setCancelable(false).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        presenter.attachView(this);
        presenter.onCreate();

        presenter.getSessionList();
        presenter.registerSessionBroadcastReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unregisterSessionBroadcastReceiver();
        presenter.detachView();
    }

    @Override
    public void showSessionList(List<ChatSession> sessionList) {
        this.sessionList=sessionList;
        lv_sessions.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                if(sessionList==null)
                    return 0;
                return sessionList.size();
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
                ChatSession session=sessionList.get(position);
                TextView tv_name=new TextView(getContext());
                tv_name.setText(session.getSessionName());
                tv_name.setTextSize(20);
                tv_name.setTextColor(Color.BLUE);

                if(session.getDeleted()){
                    tv_name.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
                    tv_name.setTextColor(Color.GRAY);
                }

                return tv_name;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChatSession session=sessionList.get(position);
        Intent intent=new Intent(getContext(), ChatActivity.class);
        intent.putExtra("sessionId",session.getSessionId());
        startActivity(intent);
    }
}
