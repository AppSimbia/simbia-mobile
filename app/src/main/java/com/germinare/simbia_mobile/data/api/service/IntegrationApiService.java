package com.germinare.simbia_mobile.data.api.service;

import com.germinare.simbia_mobile.data.api.model.integration.EvaResquest;
import com.germinare.simbia_mobile.data.api.model.integration.EvaResponse;
import com.germinare.simbia_mobile.data.api.model.integration.LawResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IntegrationApiService {

    @GET("/leis")
    Call<List<LawResponse>> findAllLaws();

    @POST("/chat/questions")
    Call<EvaResponse> askQuestion(@Body EvaResquest request);
}
