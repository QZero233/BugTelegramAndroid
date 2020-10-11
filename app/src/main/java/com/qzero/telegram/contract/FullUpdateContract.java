package com.qzero.telegram.contract;

import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

public class FullUpdateContract {

    public interface Presenter extends IBasePresenter<View>{
        void executeFullUpdate();
    }

    public interface View extends IBaseView{
        void jumpToUserCenter();
    }

}
