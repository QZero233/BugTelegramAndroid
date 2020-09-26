package com.qzero.telegram.http.service;

import com.qzero.telegram.http.exchange.PackedObject;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NoticeService {

    @GET("/notice/request_connection")
    Observable<PackedObject> requestNoticeMonitorConnection();

    @GET("/notice/")
    Observable<PackedObject> getAllNotices();

    @DELETE("/notice/{notice_id}")
    Observable<PackedObject> deleteNotice(@Path("notice_id") String noticeId);

}
