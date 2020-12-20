package com.qzero.telegram.module;

import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.http.bean.ActionResult;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface UserInfoModule {


    Observable<ActionResult> updateAccountStatusAndMotto(int accountStatus,String motto);

    Observable<UserInfo> getUserInfo(String userName);

    UserInfo getUserInfoLocally(String userName);

    Observable<UserInfo> getUserInfoFromOnlyRemote(String userName);

    List<UserInfo> getLocalFriendList();

    void deleteLocally(String userName);

}
