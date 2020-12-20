package com.qzero.telegram.http.service;

import com.qzero.telegram.http.exchange.PackedObject;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserInfoService {

    @PUT("/account/user_info/account_status_and_motto")
    Observable<PackedObject> updateAccountStatusAndMotto(@Query("account_status") int accountStatus,@Query("motto") String motto);

    @GET("/account/user_info/{user_name}")
    Observable<PackedObject> getOtherUserInfo(@Path("user_name")String userName);

}
