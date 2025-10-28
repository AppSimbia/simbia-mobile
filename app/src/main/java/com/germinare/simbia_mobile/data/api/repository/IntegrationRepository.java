package com.germinare.simbia_mobile.data.api.repository;


import androidx.annotation.NonNull;

import com.germinare.simbia_mobile.data.api.model.integration.LawResponse;
import com.germinare.simbia_mobile.data.api.service.IntegrationApiService;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntegrationRepository {

    private final IntegrationApiService integrationApiService;

    private final Consumer<String> onFailure;
    private static final String MESSAGE_ERROR = "Ocorreu um erro inesperado. Tente novamente mais tarde.";


    public IntegrationRepository(IntegrationApiService integrationApiService ,Consumer<String> onFailure) {
        this.integrationApiService = integrationApiService;
        this.onFailure = onFailure;
    }

    public void listLaws(final Consumer<List<LawResponse>> onSuccessful) {

        Call<List<LawResponse>> call = integrationApiService.findAllLaws();

        call.enqueue(new Callback<List<LawResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<LawResponse>> call, @NonNull Response<List<LawResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onSuccessful.accept(response.body());
                } else {
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LawResponse>> call, @NonNull Throwable t) {
                onFailure.accept(MESSAGE_ERROR);
            }
        });

    }
}