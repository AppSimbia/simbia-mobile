package com.germinare.simbia_mobile.ui.data.api.service;

import com.germinare.simbia_mobile.ui.data.api.model.postgres.PostRequestDto;
import com.germinare.simbia_mobile.ui.data.api.model.postgres.PostResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PostgresApiService {

    // ENDPOINTS

    // POSTS
    @POST("/post")
    Call<PostResponseDto> createPost(@Body PostRequestDto post);


}
