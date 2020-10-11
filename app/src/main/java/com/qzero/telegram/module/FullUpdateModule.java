package com.qzero.telegram.module;

import io.reactivex.rxjava3.core.Observable;

public interface FullUpdateModule {

    boolean checkIfNeedFullUpdate();

    void resetFullUpdateStatus();

    void setUpdated();

    Observable<Boolean> executeFullUpdate();
}
