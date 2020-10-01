package com.qzero.telegram.presenter;

import androidx.annotation.NonNull;

import com.qzero.telegram.view.IBaseView;


public class BasePresenter<V extends IBaseView> implements IBasePresenter<V> {

    /**
     * The view attached to the presenter
     */
    private V mView;

    /**
     * Attach view to the presenter
     * Call when view is created
     * @param mView The view that is to be attached
     */
    @Override
    public void attachView(@NonNull V mView){
        this.mView=mView;
    }

    /**
     * Detach view from the presenter
     * Call when view is destroyed
     */
    @Override
    public void detachView(){
        mView=null;
    }

    /**
     * Check if the view is attached
     * @return Whether the view is attached
     */
    @Override
    public boolean isViewAttached(){
        return mView!=null;
    }

    /**
     * Get the view attached
     * @return The view that attached,may be null
     */
    @Override
    public V getView(){
        return mView;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStop() {

    }

}
