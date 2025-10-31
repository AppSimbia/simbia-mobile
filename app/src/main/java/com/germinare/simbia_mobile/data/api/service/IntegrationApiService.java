package com.germinare.simbia_mobile.data.api.service;

import com.germinare.simbia_mobile.data.api.model.integration.PostSuggestRequest;
import com.germinare.simbia_mobile.data.api.model.integration.PostSuggestResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IntegrationApiService {

    @POST("/chat/post")
    Call<PostSuggestResponse> suggestPost(@Body PostSuggestRequest request);


}
