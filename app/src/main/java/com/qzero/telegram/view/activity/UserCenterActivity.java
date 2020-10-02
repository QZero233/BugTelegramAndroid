package com.qzero.telegram.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.qzero.telegram.R;
import com.qzero.telegram.contract.UserCenterContract;
import com.qzero.telegram.notice.NoticeMonitorService;
import com.qzero.telegram.presenter.UserCenterPresenter;
import com.qzero.telegram.view.BaseActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserCenterActivity extends BaseActivity implements UserCenterContract.View {

    private Logger log= LoggerFactory.getLogger(getClass());

    private UserCenterContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        log.debug("Started service");
        startService(new Intent(this, NoticeMonitorService.class));

        presenter=new UserCenterPresenter();
        presenter.attachView(this);
        presenter.checkFullUpdateStatus();
    }
}