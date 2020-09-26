package com.qzero.telegram.module.impl;

import android.content.Context;

import com.qzero.telegram.dao.bean.DataNotice;
import com.qzero.telegram.module.bean.NoticeConnectInfo;
import com.qzero.telegram.http.RetrofitHelper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.service.DefaultTransformer;
import com.qzero.telegram.http.service.NoticeService;
import com.qzero.telegram.module.NoticeModule;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class NoticeModuleImpl implements NoticeModule {

    private NoticeService noticeService;
    private Context context;

    public NoticeModuleImpl(Context context) {
        this.context = context;
        noticeService= RetrofitHelper.getInstance(context).getService(NoticeService.class);
    }

    @Override
    public Observable<NoticeConnectInfo> requestConnection() {
        return noticeService.requestNoticeMonitorConnection()
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(NoticeConnectInfo.class)));
    }

    @Override
    public Observable<List<DataNotice>> getAllNotices() {
        return noticeService.getAllNotices()
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just((List<DataNotice>) packedObject.parseCollectionObject("DataNoticeList",List.class,DataNotice.class)));
    }

    @Override
    public Observable<ActionResult> deleteNotice(String noticeId) {
        return noticeService.deleteNotice(noticeId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ActionResult.class)));
    }
}
