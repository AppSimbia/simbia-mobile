package com.germinare.simbia_mobile.data.api.repository;

import com.germinare.simbia_mobile.data.api.model.postgres.PostRequestDto;
import com.germinare.simbia_mobile.data.api.model.postgres.PostResponseDto;
import com.germinare.simbia_mobile.data.api.retrofit.ApiServiceFactory;
import com.germinare.simbia_mobile.data.api.service.PostgresApiService;

import retrofit2.Call;
import retrofit2.Response;

public class PostgresRepository {

    private final PostgresApiService apiService;

    public PostgresRepository(){
        this.apiService = ApiServiceFactory.getPostgresApi();
    }

    public PostResponseDto createPost(PostRequestDto request) throws Exception {
        Call<PostResponseDto> call = apiService.createPost(request);
        Response<PostResponseDto> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new Exception("Error fetching user: " + response.code());
        }
    }

}
