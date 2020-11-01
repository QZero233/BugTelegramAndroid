package com.qzero.telegram.contract;

import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

public class UserInfoDetailContract {

    public interface Presenter extends IBasePresenter<View> {
        void loadUserInfoDetail(String userName);
        void delete(String userName);
    }

    public interface View extends IBaseView {
        void loadUserInfoDetail(UserInfo userInfo);
    }

}
