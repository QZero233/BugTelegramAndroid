package com.qzero.telegram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.qzero.telegram.dao.bean.DataNotice;
import com.qzero.telegram.http.bean.Token;
import com.qzero.telegram.module.AuthorizeModule;
import com.qzero.telegram.module.NoticeModule;
import com.qzero.telegram.module.impl.AuthorizeModuleImpl;
import com.qzero.telegram.module.bean.LoginForm;
import com.qzero.telegram.module.impl.NoticeModuleImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.tv);

        log.debug("Started");

        AuthorizeModule authorizeModule = new AuthorizeModuleImpl(this);
        NoticeModule noticeModule = new NoticeModuleImpl(this);

        Token tokenPreset = new Token();
        tokenPreset.setApplicationId(Token.APP_ID_BT);
        tokenPreset.setTokenDescription("BugTelegram Application");

        LoginForm loginForm = new LoginForm();
        loginForm.setUserName("QZero");
        loginForm.setPasswordHash("8d:96:9e:ef:6e:ca:d3:c2:9a:3a:62:92:80:e6:86:cf:0c:3f:5d:5a:86:af:f3:ca:12:02:0c:92:3a:dc:6c:92");

        authorizeModule.login(loginForm, tokenPreset)
                .flatMap(actionResult -> noticeModule.getAllNotices())
                .subscribe(new Observer<List<DataNotice>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<DataNotice> dataNotices) {
                        log.info(dataNotices+"");
                        tv.setText(dataNotices+"");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        log.error("ERROR",e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}