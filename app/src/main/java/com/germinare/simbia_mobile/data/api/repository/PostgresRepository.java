package com.germinare.simbia_mobile.data.api.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.germinare.simbia_mobile.data.api.model.postgres.PostRequest;
import com.germinare.simbia_mobile.data.api.model.postgres.PostResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.ProductCategoryResponse;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.PostgresApiService;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostgresRepository {

    private final PostgresApiService apiService;
    private final Consumer<String> onFailure;
    private static final String MESSAGE_ERROR = "Ocorreu um erro inesperado. Tente novamente mais tarde.";

    public PostgresRepository(Consumer<String> onFailure){
        this.onFailure = onFailure;
        this.apiService = ApiServiceFactory.getPostgresApi();
    }

    public void createPost(PostRequest request, Consumer<PostResponse> onSuccessful) {
        Call<PostResponse> call = apiService.createPost(request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("teste", "foi criado");
                    onSuccessful.accept(response.body());
                } else {
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void updatePost(Long id, Map<String, Object> map, Consumer<PostResponse> onSuccessful) {
        Call<PostResponse> call = apiService.updatePost(id, map);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onSuccessful.accept(response.body());
                } else {
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }


    public void listProductCategories(Consumer<List<ProductCategoryResponse>> onSuccessful) {
        Call<List<ProductCategoryResponse>> call = apiService.findAllProductsCategories();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductCategoryResponse>> call, @NonNull Response<List<ProductCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onSuccessful.accept(response.body());
                } else {
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductCategoryResponse>> call, @NonNull Throwable t) {
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }
}
