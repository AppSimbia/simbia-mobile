package com.germinare.simbia_mobile.data.api.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.germinare.simbia_mobile.data.api.model.mongo.ChatResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.MessageRequest;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.MongoApiService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import okhttp3.ResponseBody;
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

    public void createMatch(Map<String, Object> request, Consumer<MatchResponse> onSuccessful) {
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
                    try {
                        Log.e(TAG, "createMatch failed: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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

    public void addMessage(String id, MessageRequest request, Consumer<ChatResponse.Message> onSuccessful) {
        Log.d(TAG, "addMessage called with request: " + request);
        Call<ChatResponse.Message> call = apiService.addMessage(id, request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ChatResponse.Message> call, @NonNull Response<ChatResponse.Message> response) {
                Log.d(TAG, "addMessage response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "addMessage success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    try {
                        Log.e(TAG, "addMessage failed: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatResponse.Message> call, @NonNull Throwable t) {
                Log.e(TAG, "createMatch error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void readMessage(String id, Long employeeId, String createAt, Consumer<Boolean> onSuccessful) {
        Log.d(TAG, "readMessage called with id: " + id + " createAt: " + createAt);
        Call<Boolean> call = apiService.readMessage(id, employeeId, createAt);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                Log.d(TAG, "readMessage response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "readMessage success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "readMessage failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.e(TAG, "readMessage error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void changeStatusMatch(String id, String action, Map<String, Object> request, Consumer<String> onSuccessful) {
        Log.d(TAG, "changeStatusMatch called for id: " + id + ", action: " + action);
        Call<ResponseBody> call = apiService.changeStatusMatch(id, action, request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "changeStatusMatch response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "changeStatusMatch success: " + response.body());
                    onSuccessful.accept(response.body().toString());
                } else {
                    try {
                        Log.e(TAG, "changeStatusMatch failed: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "changeStatusMatch error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void cancelMatch(String id) {
        Log.d(TAG, "cancelMatch called for id: " + id);
        Call<Void> call = apiService.cancelMatch(id);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d(TAG, "cancelMatch response code: " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "cancelMatch error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void findMatchByChatId(String id, Consumer<MatchResponse> onSuccessful) {
        Log.d(TAG, "findMatchByChatId called with id: " + id);
        Call<MatchResponse> call = apiService.findMatchByChatId(id);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MatchResponse> call, @NonNull Response<MatchResponse> response) {
                Log.d(TAG, "findMatchByChatId response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "findMatchByChatId success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "findMatchByChatId failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MatchResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "findMatchByChatId error: ", t);
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

    public void findAllChatByEmployeeId(String employeeId, Consumer<List<ChatResponse>> onSuccessful) {
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