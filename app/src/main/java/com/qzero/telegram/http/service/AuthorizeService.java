package com.qzero.telegram.http.service;


import com.qzero.telegram.http.exchange.PackedObject;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthorizeService {

    @POST("/authorize/login")
    Observable<PackedObject> login(@Body PackedObject parameter);

    @DELETE("/authorize/logout/{token_id}")
    Observable<PackedObject> logout(@Path("token_id") String tokenId);
}
