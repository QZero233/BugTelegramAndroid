package com.qzero.telegram.contract;

import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

import java.util.List;

public class FriendListContract {

    public interface Presenter extends IBasePresenter<View> {
        void findUser(String userName);
        void loadLocalFriendList();
    }

    public interface View extends IBaseView {
        void loadLocalFriendList(List<UserInfo> friendList);
    }

}
