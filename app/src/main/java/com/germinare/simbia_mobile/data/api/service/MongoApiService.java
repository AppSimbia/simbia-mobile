package com.germinare.simbia_mobile.data.api.service;

import com.germinare.simbia_mobile.data.api.model.mongo.ChallengeRequest;
import com.germinare.simbia_mobile.data.api.model.mongo.ChallengeResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.ChatResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.MessageRequest;
import com.germinare.simbia_mobile.data.api.model.mongo.SolutionRequest;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MongoApiService {

    @POST("/desafios")
    Call<ChallengeResponse> createChallenge(@Body ChallengeRequest request);

    @GET("/desafios/list")
    Call<List<ChallengeResponse>> listChallenges();

    @GET("/desafios/{id}")
    Call<ChallengeResponse> getChallengeById(@Path("id") String challengeId);

    @POST("/match")
    Call<MatchResponse> createMatch(@Body Map<String, Object> request);

    @GET("/match/chat/{id}")
    Call<MatchResponse> findMatchByChatId(@Path("id") String id);

    @GET("/match/completed/{id}")
    Call<List<MatchResponse>> findMatchByEmployeeId(@Path("id") String id);

    @POST("/match/status/{id}")
    Call<ResponseBody> changeStatusMatch(@Path("id") String id, @Query("action") String action, @Body Map<String, Object> request);

    @DELETE("/match/status/{id}")
    Call<Void> cancelMatch(@Path("id") String id);

    @GET("/chats/{id}")
    Call<ChatResponse> findChatById(@Path("id") String id);

    @POST("/chats/{id}/messages")
    Call<ChatResponse.Message> addMessage(@Path("id") String id, @Body MessageRequest request);

    @PUT("/chats/{id}/messages")
    Call<Boolean> readMessage(@Path("id") String id, @Query("employee") Long employeeId, @Query("createAt") String createAt);

    @GET("/chats/list/{id}")
    Call<List<ChatResponse>> findAllChatByEmployeeId(@Path("id") String id);

    @POST("/desafios/create/solucao")
    Call<ChallengeResponse> createSolution(@Query("idDesafio") String id, @Body SolutionRequest request);
}
