package com.qzero.telegram.module;

import com.qzero.telegram.notice.bean.NoticeDataType;

public interface BroadcastModule {

    enum ActionType{
        ACTION_TYPE_DELETE,
        ACTION_TYPE_UPDATE,
        ACTION_TYPE_INSERT,
        ACTION_TYPE_UNKNOWN
    }

    interface Receiver{
        void onReceived(String dataId,ActionType actionType);
    }

    void registerReceiverForCertainData(NoticeDataType dataType,Receiver receiver);

    /**
     * This will unregister all receivers registered <b>in this module instance<b/>
     */
    void unregisterAllReceivers();

    void sendBroadcast(NoticeDataType dataType,String dataId,ActionType actionType);

}
