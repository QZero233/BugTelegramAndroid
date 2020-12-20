package com.qzero.telegram.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.SessionContract;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.MessageModule;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.module.impl.MessageModuleImpl;
import com.qzero.telegram.module.impl.SessionModuleImpl;
import com.qzero.telegram.notice.bean.NoticeDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class SessionPresenter extends BasePresenter<SessionContract.View> implements SessionContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Context context;
    private ChatSessionDao sessionDao;

    private BroadcastModule broadcastModule;
    private MessageModule messageModule;

    @Override
    public void attachView(@NonNull SessionContract.View mView) {
        super.attachView(mView);
        context=mView.getContext();
        sessionDao= SessionManager.getInstance(context).getSession().getChatSessionDao();
        broadcastModule=new BroadcastModuleImpl(context);
        messageModule=new MessageModuleImpl(context);
    }

    @Override
    public void getSessionList() {
        List<ChatSession> sessionList=sessionDao.loadAll();
        if(sessionList==null || sessionList.isEmpty())
            return;

        Map<String,Integer> freshMessageCountMap=new HashMap<>();
        for(ChatSession session:sessionList){
            freshMessageCountMap.put(session.getSessionId(),messageModule.getFreshMessageCount(session.getSessionId()));
        }

        getView().showSessionList(sessionList,freshMessageCountMap);
    }

    @Override
    public void registerSessionBroadcastReceiver() {
        if(broadcastModule!=null){
            broadcastModule.registerReceiverForCertainData(NoticeDataType.TYPE_SESSION, (dataId, actionType) -> {
                log.debug(String.format("Got session update broadcast with id %s and action %s", dataId,actionType+""));
                if(isViewAttached())
                    getSessionList();
            });

            broadcastModule.registerReceiverForCertainData(NoticeDataType.TYPE_MESSAGE,(dataId, actionType) -> {
                if(actionType== BroadcastModule.ActionType.ACTION_TYPE_INSERT){
                    if(isViewAttached())
                        getSessionList();
                }
            });
        }
    }

    @Override
    public void unregisterSessionBroadcastReceiver() {
        if(broadcastModule!=null)
            broadcastModule.unregisterAllReceivers();
    }

}
