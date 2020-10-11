package com.qzero.telegram.contract;

import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

import java.util.List;

public class SessionContract {

    public interface Presenter extends IBasePresenter<View> {
        void getSessionList();
        void registerSessionBroadcastReceiver();
        void unregisterSessionBroadcastReceiver();
    }

    public interface View extends IBaseView {
        void showSessionList(List<ChatSession> sessionList);
    }

}
