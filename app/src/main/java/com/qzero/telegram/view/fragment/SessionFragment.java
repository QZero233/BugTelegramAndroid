package com.qzero.telegram.view.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qzero.telegram.R;
import com.qzero.telegram.contract.SessionContract;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.presenter.SessionPresenter;
import com.qzero.telegram.view.BaseFragment;

import java.util.List;

public class SessionFragment extends BaseFragment implements SessionContract.View {

    public ListView lv_sessions;

    private SessionContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(getContext(),R.layout.fragment_session,null);
        lv_sessions=view.findViewById(R.id.lv_sessions);
        presenter=new SessionPresenter();

        return view;
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

                if(session.isDeleted()){
                    tv_name.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
                    tv_name.setTextColor(Color.GRAY);
                }

                return tv_name;
            }
        });
    }
}
