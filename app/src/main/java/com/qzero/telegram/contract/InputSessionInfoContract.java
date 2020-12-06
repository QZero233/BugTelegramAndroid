package com.qzero.telegram.contract;

import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

import java.util.List;

public class InputSessionInfoContract {

    public interface Presenter extends IBasePresenter<View> {
        void submit(List<ChatSessionParameter> parameterList);
        String[] getFriendNames();
    }

    public interface View extends IBaseView{

    }

}
