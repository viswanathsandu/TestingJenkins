package com.education.corsalite.services;

import com.education.corsalite.api.ICorsaliteApi;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by vissu on 9/11/15.
 */
public class ApiClientService {

    private static ICorsaliteApi client;
    private static final String PROD = "https://app.corsalite.com/v1/webservices/";
    private static final String STAGING = "https://staging.corsalite.com/v1/webservices/";

    private static String ROOT = STAGING;

    static {
        setupRestClient();
    }

    public static ICorsaliteApi get() {
        return client;
    }

    private static void setupRestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .setRequestInterceptor(new SessionRequestInterceptor())
                .build();
        client = restAdapter.create(ICorsaliteApi.class);
    }
}
