package com.germinare.simbia_mobile.data.api.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.germinare.simbia_mobile.data.api.model.postgres.IndustryResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.PostRequest;
import com.germinare.simbia_mobile.data.api.model.postgres.PostResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.ProductCategoryResponse;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.PostgresApiService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostgresRepository {

    private static final String TAG = "postgres";
    private final PostgresApiService apiService;
    private final Consumer<String> onFailure;
    private static final String MESSAGE_ERROR = "Ocorreu um erro inesperado. Tente novamente mais tarde.";

    public PostgresRepository(Consumer<String> onFailure) {
        this.onFailure = onFailure;
        this.apiService = ApiServiceFactory.getPostgresApi();
        Log.d(TAG, "PostgresRepository initialized");
    }

    public void findIndustryById(Long id, Consumer<IndustryResponse> onSuccessful) {
        Log.d(TAG, "findIndustryById called with id: " + id);
        Call<IndustryResponse> call = apiService.findIndustryById(id);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<IndustryResponse> call, @NonNull Response<IndustryResponse> response) {
                Log.d(TAG, "findIndustryById response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "findIndustryById success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "findIndustryById failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<IndustryResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "findIndustryById error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void findIndustryByCnpj(String cnpj, Consumer<IndustryResponse> onSuccessful) {
        Log.d(TAG, "findIndustryById called with cnpj: " + cnpj);
        Call<IndustryResponse> call = apiService.findIndustryByCnpj(cnpj);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<IndustryResponse> call, @NonNull Response<IndustryResponse> response) {
                Log.d(TAG, "findIndustryById response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "findIndustryById success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "findIndustryById failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<IndustryResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "findIndustryById error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void createPost(PostRequest request, Consumer<PostResponse> onSuccessful) {
        Log.d(TAG, "createPost called with request: " + request);
        Call<PostResponse> call = apiService.createPost(request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                Log.d(TAG, "createPost response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "createPost success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    try {
                        Log.e(TAG, "createPost failed: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "createPost error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void updatePost(Long id, Map<String, Object> map, Consumer<PostResponse> onSuccessful) {
        Log.d(TAG, "updatePost called with id: " + id + " and map: " + map);
        Call<PostResponse> call = apiService.updatePost(id, map);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                Log.d(TAG, "updatePost response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "updatePost success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "updatePost failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "updatePost error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void findPostById(Long id, Consumer<PostResponse> onSuccessful) {
        Log.d(TAG, "findPostById called with id: " + id);
        Call<PostResponse> call = apiService.findPostById(id);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                Log.d(TAG, "findPostById response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "findPostById success: " + response.body());
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "findPostById failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "findPostById error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void listPostsByCnpj(String cnpj, Consumer<List<PostResponse>> onSuccessful) {
        Log.d(TAG, "listPostsByCnpj called with cnpj: " + cnpj);
        Call<List<PostResponse>> call = apiService.findAllPostsExceptCnpj(cnpj);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<PostResponse>> call, @NonNull Response<List<PostResponse>> response) {
                Log.d(TAG, "listPostsByCnpj response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "listPostsByCnpj success: " + response.body().size() + " posts received");
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "listPostsByCnpj failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PostResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "listPostsByCnpj error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }

    public void listProductCategories(Consumer<List<ProductCategoryResponse>> onSuccessful) {
        Log.d(TAG, "listProductCategories called");
        Call<List<ProductCategoryResponse>> call = apiService.findAllProductsCategories();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductCategoryResponse>> call, @NonNull Response<List<ProductCategoryResponse>> response) {
                Log.d(TAG, "listProductCategories response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "listProductCategories success: " + response.body().size() + " categories received");
                    onSuccessful.accept(response.body());
                } else {
                    Log.e(TAG, "listProductCategories failed: " + response.message());
                    onFailure.accept(MESSAGE_ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductCategoryResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "listProductCategories error: ", t);
                onFailure.accept(MESSAGE_ERROR);
            }
        });
    }
}
