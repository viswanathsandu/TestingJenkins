package com.education.corsalite.services;

import com.education.corsalite.api.ICorsaliteApi;
import com.education.corsalite.deserializer.ExerciseModelResponseDeserializer;
import com.education.corsalite.interceptors.SessionRequestInterceptor;
import com.education.corsalite.models.responsemodels.ExerciseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by vissu on 9/11/15.
 */
public class ApiClientService {

    private static ICorsaliteApi client;
    private static final String PROD = "https://app.corsalite.com/v1/webservices/";
    private static final String STAGING = "https://staging.corsalite.com/v1/webservices/";

    private static String ROOT = STAGING;

    private static String setCookie;

    public static String getBaseUrl() {
        return ROOT.replace("webservices/", "");
    }

    public static void setSetCookie(String cookie) {
        setCookie = cookie;
    }

    public static String getSetCookie() {
        return setCookie;
    }

    static {
        setupRestClient();
    }

    public static ICorsaliteApi get() {
        return client;
    }

    private static void setupRestClient() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ExerciseModel.class, new ExerciseModelResponseDeserializer()) // This is the important line ;)
                .create();


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .setRequestInterceptor(new SessionRequestInterceptor())
                .setConverter(new GsonConverter(gson))
                .build();
        client = restAdapter.create(ICorsaliteApi.class);
    }
}
