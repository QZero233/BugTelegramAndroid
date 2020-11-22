package com.qzero.telegram.presenter;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.FullUpdateContract;
import com.qzero.telegram.module.FullUpdateModule;
import com.qzero.telegram.module.impl.FullUpdateModuleImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class FullUpdatePresenter extends BasePresenter<FullUpdateContract.View> implements FullUpdateContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private FullUpdateModule fullUpdateModule;

    @Override
    public void attachView(@NonNull FullUpdateContract.View mView) {
        super.attachView(mView);
        fullUpdateModule=new FullUpdateModuleImpl(mView.getContext());
    }

    @Override
    public void executeFullUpdate() {
        getView().showToast("Executing full update,please wait......");
        getView().showProgress();
        fullUpdateModule.executeFullUpdate()
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Boolean aBoolean) {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to execute full update",e);
                        fullUpdateModule.setUpdated();
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("Full update failed,but you can do it later manually in settings");
                            getView().jumpToUserCenter();
                        }
                    }

                    @Override
                    public void onComplete() {
                        log.debug("Full update successfully");
                        if(isViewAttached()){
                            getView().showToast("Full update successfully,jumping to user center");
                            getView().hideProgress();
                            getView().jumpToUserCenter();
                            fullUpdateModule.setUpdated();
                        }
                    }
                });
    }
}
