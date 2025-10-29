package com.germinare.simbia_mobile.data.api.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.germinare.simbia_mobile.data.api.model.mongo.ChatResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchRequest;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchResponse;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.MongoApiService;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MongoRepository {

    private static final String TAG = "mongo";
    private final MongoApiService apiService;
    private final Consumer<String> onFailure;
    private static final String MESSAGE_ERROR = "Ocorreu um erro inesperado. Tente novamente mais tarde.";

    public MongoRepository(Consumer<String> onFailure) {
        this.onFailure = onFailure;
        this.apiService = ApiServiceFactory.getMongoApi();
        Log.d(TAG, "MongoRepository initialized");
    }

    public void createMatch(MatchRequest request, Consumer<MatchResponse> onSuccessful) {
        Log.d(TAG, "createMatch called with request: " + request);
        Call<MatchResponse> call = apiService.createMatch(request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MatchResponse> call, @NonNull Response<MatchResponse> response) {
                Log.d(TAG, "createMatch response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "createMatch success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "createMatch failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MatchResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "createMatch error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void changeStatusMatch(String id, String action, MatchRequest request, Consumer<String> onSuccessful) {
        Log.d(TAG, "changeStatusMatch called for id: " + id + ", action: " + action);
        Call<String> call = apiService.changeStatusMatch(id, action, request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.d(TAG, "changeStatusMatch response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "changeStatusMatch success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "changeStatusMatch failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "changeStatusMatch error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void findChatById(String id, Consumer<ChatResponse> onSuccessful) {
        Log.d(TAG, "findChatById called with id: " + id);
        Call<ChatResponse> call = apiService.findChatById(id);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ChatResponse> call, @NonNull Response<ChatResponse> response) {
                Log.d(TAG, "findChatById response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "findChatById success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "findChatById failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "findChatById error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void findAllChatByEmployeeId(Long employeeId, Consumer<List<ChatResponse>> onSuccessful) {
        Log.d(TAG, "findAllChatByEmployeeId called with id: " + employeeId);
        Call<List<ChatResponse>> call = apiService.findAllChatByEmployeeId(employeeId);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatResponse>> call, @NonNull Response<List<ChatResponse>> response) {
                Log.d(TAG, "findAllChatByEmployeeId response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "findAllChatByEmployeeId success: " + response.body().size() + " chats received");
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "findAllChatByEmployeeId failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "findAllChatByEmployeeId error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }
}