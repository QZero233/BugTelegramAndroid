package com.qzero.telegram.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.qzero.telegram.R;
import com.qzero.telegram.contract.FullUpdateContract;
import com.qzero.telegram.presenter.FullUpdatePresenter;
import com.qzero.telegram.view.BaseActivity;

public class FullUpdateActivity extends BaseActivity implements FullUpdateContract.View {

    private FullUpdateContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_update);

        presenter=new FullUpdatePresenter();
        presenter.attachView(this);

        presenter.executeFullUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void jumpToUserCenter() {
        startActivity(new Intent(this,UserCenterActivity.class));
        finish();
    }
}