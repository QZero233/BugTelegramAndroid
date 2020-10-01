package com.qzero.telegram.notice.processor;

import com.qzero.telegram.notice.bean.DataNotice;
import com.qzero.telegram.notice.bean.NoticeDataType;

public interface NoticeProcessor {

    NoticeDataType getDataType();

    /**
     * Process a data notice
     * @return if the value is true,the notice will be deleted from server
     */
    boolean processNotice(DataNotice notice);

}
