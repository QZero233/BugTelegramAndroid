package com.qzero.telegram.module;

import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.bean.Token;
import com.qzero.telegram.module.bean.LoginForm;

import java.io.IOException;

import io.reactivex.rxjava3.core.Observable;

public interface AuthorizeModule {

    Observable<ActionResult> login(LoginForm loginForm, Token tokenPreset);

    Observable<ActionResult> logout() throws IOException;

}
