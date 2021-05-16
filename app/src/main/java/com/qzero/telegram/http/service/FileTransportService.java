package com.qzero.telegram.http.service;


import com.qzero.telegram.http.exchange.PackedObject;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FileTransportService {

    @Multipart
    @POST("/storage/transport/{resource_id}/{block_index}")
    Observable<PackedObject> uploadFile(@Part RequestBody requestBody,
                                        @Query("offset") long offset,
                                        @Query("length") int length);


    @GET("/storage/transport/{resource_id}")
    Observable<ResponseBody> downloadFile(@Path("resource_id") String resourceId,
                                               @Query("offset") long offset,
                                               @Query("length") int length);

    @PUT("/storage/transport/{resource_id}/finished")
    Observable<PackedObject> markTaskFinished(@Path("resource_id") String resourceId);
}
