package com.germinare.simbia_mobile.data.api.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.germinare.simbia_mobile.data.api.model.integration.PostSuggestRequest;
import com.germinare.simbia_mobile.data.api.model.integration.PostSuggestResponse;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchResponse;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.IntegrationApiService;

import java.io.IOException;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntegrationRepository {

    private static final String TAG = "integration";
    private final IntegrationApiService apiService;
    private final Consumer<String> onFailure;
    private static final String MESSAGE_ERROR = "Ocorreu um erro inesperado. Tente novamente mais tarde.";

    public IntegrationRepository(Consumer<String> onFailure) {
        this.onFailure = onFailure;
        this.apiService = ApiServiceFactory.getIntegrationApi();
        Log.d(TAG, "MongoRepository initialized");
    }

    public void suggestPost(PostSuggestRequest request, Consumer<PostSuggestResponse> onSuccessful){
        Log.d(TAG, "suggestPost called with request: " + request.toString());
        Call<PostSuggestResponse> call = apiService.suggestPost(request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PostSuggestResponse> call, @NonNull Response<PostSuggestResponse> response) {
                Log.d(TAG, "suggestPost response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "suggestPost success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    try {
                        Log.e(TAG, "suggestPost failed: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostSuggestResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "suggestPost error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }
}
