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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding4.view.RxView;
import com.qzero.telegram.InputSessionInfoActivity;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.SessionContract;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
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

        String[] sessionTypesToShow=new String[]{"普通会话","加密通话","P2P会话"};
        String[] sessionTypes=new String[]{ChatSessionParameter.SESSION_TYPE_NORMAL,ChatSessionParameter.SESSION_TYPE_SECRET,ChatSessionParameter.SESSION_TYPE_PERSONAL};

        Spinner sp_type=new Spinner(getContext());
        sp_type.setAdapter(new ArrayAdapter(getContext(),R.layout.view_personal_info_sp_tv,sessionTypesToShow));
        builder.setView(sp_type);

        builder.setMessage("请选择会话类型");
        builder.setNegativeButton("取消",null)
                .setPositiveButton("GO",(a,b) ->
                        {
                            String type=sessionTypes[sp_type.getSelectedItemPosition()];
                            Intent intent=new Intent(getContext(), InputSessionInfoActivity.class);
                            intent.putExtra("sessionType",type);
                            startActivity(intent);
                        });

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
                tv_name.setText(session.getSessionParameter(ChatSessionParameter.NAME_SESSION_NAME));
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
        intent.putExtra("sessionType",session.getSessionParameter(ChatSessionParameter.NAME_SESSION_TYPE));
        startActivity(intent);
    }
}
