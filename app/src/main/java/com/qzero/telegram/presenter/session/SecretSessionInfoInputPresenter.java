package com.qzero.telegram.presenter.session;

import com.qzero.telegram.dao.entity.ChatSessionParameter;

import java.util.List;

public class SecretSessionInfoInputPresenter extends BaseSessionInfoInputPresenter {

    @Override
    public void submit(List<ChatSessionParameter> parameterList) {
        parameterList.add(new ChatSessionParameter(null,null,ChatSessionParameter.NAME_SESSION_TYPE,ChatSessionParameter.SESSION_TYPE_SECRET));
        super.submit(parameterList);
    }
}
