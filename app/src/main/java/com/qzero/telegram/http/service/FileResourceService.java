package com.qzero.telegram.http.service;

import com.qzero.telegram.http.exchange.PackedObject;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FileResourceService {

    @POST("/storage/resource/")
    Observable<PackedObject> newFileResource(@Body PackedObject parameter);

    @DELETE("/storage/resource/{resource_id}")
    Observable<PackedObject> deleteFileResource(@Path("resource_id") String resourceId);

    @GET("/storage/resource/{resource_id}")
    Observable<PackedObject> getFileResourceInfo(@Path("resource_id") String resourceId);

    @GET("/storage/resource/")
    Observable<PackedObject> getAllFileResources();

}
