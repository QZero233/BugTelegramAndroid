package com.qzero.telegram.view.activity;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.qzero.telegram.R;
import com.qzero.telegram.UserCenterActivity;
import com.qzero.telegram.contract.LoginContract;
import com.qzero.telegram.presenter.LoginPresenter;
import com.qzero.telegram.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.core.Observable;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    private LoginContract.Presenter presenter;

    @BindView(R.id.et_username)
    public EditText et_username;
    @BindView(R.id.et_password)
    public EditText et_password;
    @BindView(R.id.pb_loading)
    public ProgressBar pb_loading;
    @BindView(R.id.btn_login)
    public Button btn_login;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        presenter = new LoginPresenter();
        presenter.attachView(this);
        presenter.onCreate();

        Observable<CharSequence> usernameObservable= RxTextView.textChanges(et_username);
        Observable<CharSequence> codeObservable= RxTextView.textChanges(et_password);

        RxView.clicks(btn_login)
                .subscribe(unit -> login());

        Observable.combineLatest(usernameObservable, codeObservable, (CharSequence charSequence, CharSequence charSequence2) ->{
            if(!TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(charSequence2))
                return true;
            return false;
        }).subscribe(aBoolean -> {
            btn_login.setEnabled(aBoolean);
        });
    }

    @Override
    public void showProgress() {
        pb_loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pb_loading.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    public void login() {
        String userName=et_username.getText().toString();
        String password=et_password.getText().toString();
        presenter.login(userName,password);
    }

    @Override
    public void jumpToUserCenter() {
        startActivity(new Intent(this, UserCenterActivity.class));
    }
}