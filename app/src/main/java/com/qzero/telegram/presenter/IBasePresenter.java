package com.qzero.telegram.presenter;

import androidx.annotation.NonNull;

import com.qzero.telegram.view.IBaseView;


public interface IBasePresenter<V extends IBaseView> {

    void attachView(@NonNull V mView);

    void detachView();

    boolean isViewAttached();

    V getView();

    void onCreate();

    void onStop();

}
