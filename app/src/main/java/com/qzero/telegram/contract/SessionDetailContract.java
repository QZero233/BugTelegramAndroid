package com.qzero.telegram.contract;

import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

public class SessionDetailContract {

    public interface Presenter extends IBasePresenter<View> {
        void initView(String sessionId);

        void addMember(String memberUserName);
        void deleteMember(String memberUserName);
        void updateMember(ChatMember chatMember);

        String[] getFriendNames();

        void quitSession();

        void deleteSessionRemotely();
        void deleteSessionLocally();

        void updateSessionName(String newSessionName);

        void registerListener();
        void unregisterListener();
    }

    public interface View extends IBaseView {
        void showNormalUserMode();
        void showOperatorMode();
        void showOwnerMode();
        void showDeletedMode();

        void loadSessionInfo(ChatSession session);
    }

}
