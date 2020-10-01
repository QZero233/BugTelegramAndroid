package com.qzero.telegram.contract;

import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

public class LoginContract {

    public interface Presenter extends IBasePresenter<View> {
        void login(String userName,String password);
    }

    public interface View extends IBaseView {
        void jumpToUserCenter();
    }

}
