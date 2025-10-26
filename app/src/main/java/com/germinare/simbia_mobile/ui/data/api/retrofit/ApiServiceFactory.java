package com.germinare.simbia_mobile.ui.data.api.retrofit;

import com.germinare.simbia_mobile.ui.data.api.service.IntegrationApiService;
import com.germinare.simbia_mobile.ui.data.api.service.MongoApiService;
import com.germinare.simbia_mobile.ui.data.api.service.PostgresApiService;

public class ApiServiceFactory {

    private static final String BASE_URL_POSTGRES = "https://simbia-api.onrender.com";
    private static final String BASE_URL_MONGO = "https://simbia-api-nosql.onrender.com";
    private static final String BASE_URL_INTEGRATION = "https://simbia-integration-service-2.onrender.com";

    public static PostgresApiService getPostgresApi() {
        return RetrofitClient.create(BASE_URL_POSTGRES).create(PostgresApiService.class);
    }

    public static MongoApiService getMongoApi() {
        return RetrofitClient.create(BASE_URL_MONGO).create(MongoApiService.class);
    }

    public static IntegrationApiService getIntegrationApi() {
        return RetrofitClient.create(BASE_URL_INTEGRATION).create(IntegrationApiService.class);
    }
}
