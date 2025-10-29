package com.germinare.simbia_mobile.data.api.service;

import com.germinare.simbia_mobile.data.api.model.postgres.IndustryResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.PostRequest;
import com.germinare.simbia_mobile.data.api.model.postgres.PostResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.ProductCategoryResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PostgresApiService {

    // ENDPOINTS

    // EMPLOYEE
    @GET("/industries/id/{id}")
    Call<IndustryResponse> findIndustryById(@Path("id") Long id);

    // POSTS
    @POST("/posts")
    Call<PostResponse> createPost(@Body PostRequest post);

    @PUT("/posts/{id}")
    Call<PostResponse> updatePost(@Path("id") Long id, @Body Map<String, Object> map);

    @GET("/posts/list/{cnpj}")
    Call<List<PostResponse>> findAllPostsExceptCnpj(@Path("cnpj") String cnpj);

    @GET("/posts/category/list")
    Call<List<ProductCategoryResponse>> findAllProductsCategories();

    @GET("/posts/list/{id}/employee")
    Call<List<PostResponse>> findAllPostsByEmployee(@Path("id") Long id);

}
