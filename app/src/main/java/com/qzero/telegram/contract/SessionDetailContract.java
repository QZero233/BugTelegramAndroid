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

        void updateSessionName(String newSessionName);

        void quitSession();

        void deleteSessionRemotely();
        void deleteSessionLocally();

        void registerListener();
        void unregisterListener();
    }

    public interface View extends IBaseView {
        void adjustUserRole(int level);
        void adjustSessionType(String sessionMode);

        void showDeletedMode();

        void loadSessionInfo(ChatSession session);
    }

}
