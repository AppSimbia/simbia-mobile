package com.germinare.simbia_mobile.data.api.service;

import com.germinare.simbia_mobile.data.api.model.mongo.ChatResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchRequest;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MongoApiService {

    @POST("/match")
    Call<MatchResponse> createMatch(@Body MatchRequest request);

    @POST("/match/status/{id}")
    Call<String> changeStatusMatch(@Path("id") String id, @Query("action") String action, @Body MatchRequest request);

    @GET("/chats/{id}")
    Call<ChatResponse> findChatById(@Path("id") String id);

    @GET("/chats/list/{id}")
    Call<List<ChatResponse>> findAllChatByEmployeeId(@Path("id") Long id);

}
