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

public interface SessionService {

    @GET("/chat_session/")
    Observable<PackedObject> getAllSessions();

    @GET("/chat_session/{session_id}")
    Observable<PackedObject> getSession(@Path("session_id") String sessionId);

    @POST("/chat_session/")
    Observable<PackedObject> createSession(@Body PackedObject parameter);

    @POST("/chat_session/{session_id}/members")
    Observable<PackedObject> addChatMember(@Path("session_id") String sessionId, @Query("member_user_name")String newMemberUserName);

    @DELETE("/chat_session/{session_id}/members")
    Observable<PackedObject> removeChatMember(@Path("session_id") String sessionId,@Query("member_user_name") String memberUserName);

    @DELETE("/chat_session/{session_id}")
    Observable<PackedObject> deleteSession(@Path("session_id") String sessionId);

    @PUT("/chat_session/{session_id}/{parameter_name}")
    Observable<PackedObject> updateSessionParameter(@Path("session_id") String sessionId,
                                                    @Path("parameter_name")String parameterName,
                                                    @Query("parameter_value")String parameterValue);

    @PUT("/chat_session/{session_id}/members/{member_user_name}/level")
    Observable<PackedObject> updateChatMemberLevel(@Path("member_user_name") String memberUserName,@Path("session_id") String sessionId,
                                                   @Query("level") int newLevel);

    @DELETE("/chat_session/{session_id}/{parameter_name}")
    Observable<PackedObject> deleteSessionParameter(@Path("session_id") String sessionId,
                                                    @Path("parameter_name")String parameterName);

    @POST("/chat_session/{session_id}/{parameter_name}")
    Observable<PackedObject> addSessionParameter(@Path("session_id") String sessionId,
                                                 @Path("parameter_name")String parameterName,
                                                 @Query("parameter_value")String parameterValue);

}
