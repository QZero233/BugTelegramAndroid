package com.qzero.telegram.contract;

import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

public class UserCenterContract {

    public interface Presenter extends IBasePresenter<View> {
        void loadPersonalInfo();
    }

    public interface View extends IBaseView {
        void showPersonalInfo(UserInfo userInfo);
    }

}
