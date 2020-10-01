package com.qzero.telegram.view;

import android.content.Context;
import android.content.Intent;

/**
 * Base view with some frequently used methods
 * @author QZero
 */
public interface IBaseView {

    /**
     * Show progress dialog
     */
    void showProgress();

    /**
     * Hide progress dialog
     */
    void hideProgress();

    /**
     * Show a toast with length SHORT
     * @param text The text you want to show
     */
    void showToast(String text);

    /**
     * Show error message caused by local factors
     * Such as internet error or permission denied
     * @param localErrorMessage
     */
    void showLocalErrorMessage(String localErrorMessage);

    /**
     * Get the context of the view
     * @return The context
     */
    Context getContext();

    /**
     * Close the view
     */
    void exit();


}