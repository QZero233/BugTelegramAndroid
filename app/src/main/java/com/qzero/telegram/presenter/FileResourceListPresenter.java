package com.qzero.telegram.presenter;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.FileResourceListContract;
import com.qzero.telegram.dao.entity.FileResource;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.FileResourceModule;
import com.qzero.telegram.module.impl.FileResourceModuleImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class FileResourceListPresenter extends BasePresenter<FileResourceListContract.View> implements FileResourceListContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private FileResourceModule resourceModule;

    @Override
    public void attachView(@NonNull FileResourceListContract.View mView) {
        super.attachView(mView);
        resourceModule=new FileResourceModuleImpl(mView.getContext());
    }

    @Override
    public void loadAllFileResource() {
        getView().showProgress();

        resourceModule.getAllFileResources()
                .subscribe(new Observer<List<FileResource>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<FileResource> fileResourceList) {
                        if(isViewAttached()){
                            getView().loadAllFileResource(fileResourceList);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to get file resource list",e);
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("Failed to get resource list");
                            getView().exit();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(isViewAttached()){
                            getView().hideProgress();
                        }
                    }
                });
    }

    @Override
    public void newFileResource(File fileResource) {
        getView().showProgress();
        resourceModule.newFileResource(fileResource)
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {
                        if(isViewAttached()){
                            getView().showToast("Create file resource successfully");
                            loadAllFileResource();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to create new file resource for file "+fileResource.getAbsolutePath(),e);
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("Failed to create new file resource");
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(isViewAttached()){
                            getView().hideProgress();
                        }
                    }
                });
    }

    @Override
    public void deleteFileResource(String resourceId) {
        getView().showProgress();

        resourceModule.deleteFileResource(resourceId)
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {
                        if(isViewAttached()){
                            getView().showToast("Resource was deleted successfully");
                            loadAllFileResource();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to delete file resource with id "+resourceId,e);
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("Failed to delete the resource");
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(isViewAttached()){
                            getView().hideProgress();
                        }
                    }
                });
    }
}
