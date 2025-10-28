package com.germinare.simbia_mobile.data.api.service;

import com.germinare.simbia_mobile.data.api.model.integration.LawResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IntegrationApiService {

    @GET("/leis")
    Call<List<LawResponse>> findAllLaws();
}
