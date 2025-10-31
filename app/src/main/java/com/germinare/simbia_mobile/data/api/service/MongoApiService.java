package com.germinare.simbia_mobile.data.api.service;

import com.germinare.simbia_mobile.data.api.model.mongo.ChalengeRequest;
import com.germinare.simbia_mobile.data.api.model.mongo.ChalengeResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.SolutionRequest;
import com.germinare.simbia_mobile.ui.features.home.fragments.challenges.EmptyResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MongoApiService {

    @POST("/desafios")
    Call<ChalengeResponse> createChallenge(@Body ChalengeRequest request);

    @GET("/desafios/list")
    Call<List<ChalengeResponse>> listChallenges();

    @GET("/desafios/{id}")
    Call<ChalengeResponse> getChallengeById(@Path("id") String challengeId);

    @POST("/desafios/create/solucao")
    Call<ChalengeResponse> createSolution(@Query("idDesafio") String id, @Body SolutionRequest request);
}