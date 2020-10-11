package com.qzero.telegram.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.SessionContract;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.notice.bean.NoticeDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SessionPresenter extends BasePresenter<SessionContract.View> implements SessionContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Context context;
    private ChatSessionDao sessionDao;
    private BroadcastModule broadcastModule;
    private List<ChatSession> sessionList;

    @Override
    public void attachView(@NonNull SessionContract.View mView) {
        super.attachView(mView);
        context=mView.getContext();
        sessionDao= SessionManager.getInstance(context).getSession().getChatSessionDao();
        broadcastModule=new BroadcastModuleImpl(context);
    }

    @Override
    public void getSessionList() {
        sessionList=sessionDao.loadAll();
        getView().showSessionList(sessionList);
    }

    @Override
    public void registerSessionBroadcastReceiver() {
        broadcastModule.registerReceiverForCertainData(NoticeDataType.TYPE_SESSION, (dataId, actionType) -> {
            log.debug(String.format("Got session update broadcast with id %s and action %s", dataId,actionType+""));
            getSessionList();
        });
    }

    @Override
    public void unregisterSessionBroadcastReceiver() {
        broadcastModule.unregisterAllReceivers();
    }
}
