package com.qzero.telegram.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.SessionContract;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.gen.ChatSessionDao;

import java.util.List;

public class SessionPresenter extends BasePresenter<SessionContract.View> implements SessionContract.Presenter {

    private Context context;
    private ChatSessionDao sessionDao;

    @Override
    public void attachView(@NonNull SessionContract.View mView) {
        super.attachView(mView);
        context=mView.getContext();
        sessionDao= SessionManager.getInstance(context).getSession().getChatSessionDao();
    }

    @Override
    public void getSessionList() {
        List<ChatSession> sessionList=sessionDao.loadAll();
        getView().showSessionList(sessionList);
    }
}
