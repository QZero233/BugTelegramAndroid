package com.qzero.telegram.presenter.session;

import android.util.Log;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.InputSessionInfoContract;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.http.bean.ActionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class NormalSessionInfoInputPresenter extends BaseSessionInfoInputPresenter implements InputSessionInfoContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public void submit(List<ChatSessionParameter> parameterList) {
        parameterList.add(new ChatSessionParameter(null,null,ChatSessionParameter.NAME_SESSION_TYPE,ChatSessionParameter.SESSION_TYPE_NORMAL));
        super.submit(parameterList);
    }

}
