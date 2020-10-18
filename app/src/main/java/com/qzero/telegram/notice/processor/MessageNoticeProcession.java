package com.qzero.telegram.notice.processor;

import com.qzero.telegram.notice.bean.DataNotice;
import com.qzero.telegram.notice.bean.NoticeDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class MessageNoticeProcession implements NoticeProcessor {

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public NoticeDataType getDataType() {
        return NoticeDataType.TYPE_MESSAGE;
    }

    @Override
    public boolean processNotice(DataNotice notice) {
        URI uri=URI.create(notice.getDataUri());
        String dataId=uri.getAuthority();
        String detail=uri.getFragment();

        log.debug(String.format("Processing message update with id %s and detail %s", dataId,detail));



        //TODO PROCESS
        return true;
    }
}
