package com.qzero.telegram.module;

import com.qzero.telegram.notice.bean.DataNotice;
import com.qzero.telegram.module.bean.NoticeConnectInfo;
import com.qzero.telegram.http.bean.ActionResult;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface NoticeModule {

    Observable<NoticeConnectInfo> requestConnection();

    Observable<List<DataNotice>> getAllNotices();

    Observable<ActionResult> deleteNotice(String noticeId);

}
