package com.germinare.simbia_mobile.ui.data.api;

import com.google.firebase.firestore.auth.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface api {

    @GET("/users/{user}")
    Call<User> getUser(@Path("user") String user);
}
