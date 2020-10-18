package com.qzero.telegram.module;

import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.http.bean.ActionResult;

import io.reactivex.rxjava3.core.Observable;

public interface UserInfoModule {

    Observable<UserInfo> getPersonalInfo();

    Observable<ActionResult> updatePersonalInfo(UserInfo newUserInfo);

}
