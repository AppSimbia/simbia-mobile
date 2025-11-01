package com.germinare.simbia_mobile.data.api.service;

import com.germinare.simbia_mobile.data.api.model.mongo.ChalengeRequest;
import com.germinare.simbia_mobile.data.api.model.mongo.ChalengeResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.SolutionRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.germinare.simbia_mobile.data.api.model.mongo.ChatResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchResponse;

import java.util.Map;

public interface MongoApiService {

    @POST("/desafios")
    Call<ChalengeResponse> createChallenge(@Body ChalengeRequest request);

    @GET("/desafios/list")
    Call<List<ChalengeResponse>> listChallenges();

    @GET("/desafios/{id}")
    Call<ChalengeResponse> getChallengeById(@Path("id") String challengeId);

    @POST("/match")
    Call<MatchResponse> createMatch(@Body Map<String, Object> request);

    @POST("/match/status/{id}")
    Call<String> changeStatusMatch(@Path("id") String id, @Query("action") String action, @Body Map<String, Object> request);

    @GET("/chats/{id}")
    Call<ChatResponse> findChatById(@Path("id") String id);

    @GET("/chats/list/{id}")
    Call<List<ChatResponse>> findAllChatByEmployeeId(@Path("id") Long id);

    @POST("/desafios/create/solucao")
    Call<ChalengeResponse> createSolution(@Query("idDesafio") String id, @Body SolutionRequest request);
}