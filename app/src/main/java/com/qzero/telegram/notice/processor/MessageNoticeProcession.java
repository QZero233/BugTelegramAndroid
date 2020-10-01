package com.qzero.telegram.notice.processor;

import com.qzero.telegram.notice.bean.DataNotice;
import com.qzero.telegram.notice.bean.NoticeDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageNoticeProcession implements NoticeProcessor {

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public NoticeDataType getDataType() {
        return NoticeDataType.TYPE_MESSAGE;
    }

    @Override
    public boolean processNotice(DataNotice notice) {
        log.debug("Processing notice with uri "+notice.getDataUri());
        //TODO PROCESS
        return true;
    }
}
