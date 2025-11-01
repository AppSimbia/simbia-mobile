package com.germinare.simbia_mobile.data.api.service;

import com.germinare.simbia_mobile.data.api.model.integration.PostSuggestRequest;
import com.germinare.simbia_mobile.data.api.model.integration.PostSuggestResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import com.germinare.simbia_mobile.data.api.model.integration.EvaResquest;
import com.germinare.simbia_mobile.data.api.model.integration.EvaResponse;
import com.germinare.simbia_mobile.data.api.model.integration.LawResponse;

import java.util.List;
import retrofit2.http.GET;

public interface IntegrationApiService {

    @GET("/leis")
    Call<List<LawResponse>> findAllLaws();

    @POST("/chat/questions")
    Call<EvaResponse> askQuestion(@Body EvaResquest request);

    @POST("/chat/post")
    Call<PostSuggestResponse> suggestPost(@Body PostSuggestRequest request);
}
