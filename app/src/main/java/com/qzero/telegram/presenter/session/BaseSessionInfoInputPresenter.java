package com.qzero.telegram.presenter.session;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.InputSessionInfoContract;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.UserInfoModule;
import com.qzero.telegram.module.impl.SessionModuleImpl;
import com.qzero.telegram.module.impl.UserInfoModuleImpl;
import com.qzero.telegram.presenter.BasePresenter;
import com.qzero.telegram.utils.LocalStorageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BaseSessionInfoInputPresenter extends BasePresenter<InputSessionInfoContract.View> implements InputSessionInfoContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    protected SessionModule sessionModule;
    private UserInfoModule userInfoModule;

    @Override
    public void attachView(@NonNull InputSessionInfoContract.View mView) {
        super.attachView(mView);

        sessionModule=new SessionModuleImpl(getView().getContext());
        userInfoModule=new UserInfoModuleImpl(getView().getContext());
    }

    @Override
    public void submit(List<ChatSessionParameter> parameterList) {

    }

    @Override
    public String[] getFriendNames() {
        List<UserInfo> userInfoList=userInfoModule.getLocalFriendList();
        if(userInfoList==null)
            return new String[0];

        String myName= LocalStorageUtils.getLocalTokenUserName(getView().getContext());
        List<String> nameList=new ArrayList<>();
        for(UserInfo userInfo:userInfoList){
            if(userInfo.getUserName().equals(myName))
                continue;

            nameList.add(userInfo.getUserName());
        }

        return nameList.toArray(new String[]{});
    }
}
