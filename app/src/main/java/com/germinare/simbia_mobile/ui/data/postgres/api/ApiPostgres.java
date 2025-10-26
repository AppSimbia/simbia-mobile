package com.germinare.simbia_mobile.ui.data.postgres.api;

import com.germinare.simbia_mobile.ui.data.postgres.model.PostRequestDto;


import com.germinare.simbia_mobile.ui.data.postgres.model.PostResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPostgres {
    @Headers("Content-Type: application/json")
    @POST("/")
    Call<PostResponseDto> createPost(@Body PostRequestDto post);


    @Headers("Content-Type: application/json")
    @GET("/category/list")
    Call<List<PostCategoryResponseDto>> listCategories();
}
