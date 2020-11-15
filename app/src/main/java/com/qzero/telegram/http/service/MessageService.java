package com.qzero.telegram.http.service;

import com.qzero.telegram.http.exchange.PackedObject;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageService {

    @GET("/message/{message_id}")
    Observable<PackedObject> getMessage(@Path("message_id") String messageId);

    @POST("/message/")
    Observable<PackedObject> saveMessage(@Body PackedObject parameter);

    @DELETE("/message/{message_id}")
    Observable<PackedObject> deleteMessage(@Path("message_id") String messageId);

    @PUT("/message/{message_id}/status")
    Observable<PackedObject> updateMessageStatus(@Path("message_id") String messageId,@Query("status") String newStatus);

    @GET("/message/")
    Observable<PackedObject> getAllMessagesBySessionId(@Query("session_id") String sessionId);

}
