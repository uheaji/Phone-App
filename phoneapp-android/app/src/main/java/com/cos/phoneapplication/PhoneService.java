package com.cos.phoneapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PhoneService {

    @GET("phone/{id}")
    Call<CMRespDto<Phone>> findById(@Path("id") Long id);

    @GET("phone")
    Call<CMRespDto<List<Phone>>> findAll();

    @DELETE("phone/{id}")
    Call<CMRespDto> delete(@Path("id") long id);

    @PUT("phone/{id}")
    Call<PhoneSaveReqDto> update(@Path("id") Long id, @Body PhoneSaveReqDto phoneSaveReqDto);

    @POST("phone")
    Call<CMRespDto<Phone>> save(@Body Phone phone);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.0.3:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
