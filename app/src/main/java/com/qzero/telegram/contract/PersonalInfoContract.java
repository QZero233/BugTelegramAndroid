package com.qzero.telegram.contract;

import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

public class PersonalInfoContract {

    public interface Presenter extends IBasePresenter<View> {
        void loadPersonalInfo();
        void updatePersonalInfo(UserInfo userInfo);
    }

    public interface View extends IBaseView {
        void loadPersonalInfo(UserInfo userInfo);
        void disableSubmitButton();
        void gotoMainFragmentAndReloadInfo(UserInfo userInfo);
    }

}
