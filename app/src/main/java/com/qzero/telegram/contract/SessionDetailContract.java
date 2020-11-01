package com.qzero.telegram.contract;

import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

public class SessionDetailContract {

    public interface Presenter extends IBasePresenter<View> {
        void initView(String sessionId);

        void addMember(String memberUserName);
    }

    public interface View extends IBaseView {
        void showNormalUserMode();
        void showOperatorMode();
        void showOwnerMode();

        void loadSessionInfo(ChatSession session);
    }

}
