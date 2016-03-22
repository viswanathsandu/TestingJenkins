package com.education.corsalite.services;

import android.text.TextUtils;

import com.education.corsalite.api.ICorsaliteApi;
import com.education.corsalite.deserializer.ExerciseModelResponseDeserializer;
import com.education.corsalite.interceptors.SessionRequestInterceptor;
import com.education.corsalite.models.responsemodels.ExamModel;
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
    private static final String PROD = "http://app.corsalite.com/ws/webservices/";
    private static  final String STAGING = "http://staging.corsalite.com/v1/webservices/";

    private static String ROOT = STAGING;

    private static String setCookie;

    public static String getBaseUrl() {
        return ROOT == null ? "" : ROOT.replace("webservices/", "");
    }

    public static void enableStaging() {
        ROOT = STAGING;
        setupRestClient();
    }

    public static void enableProduction() {
        ROOT = PROD;
        setupRestClient();
    }

    public static void setSetCookie(String cookie) {
        setCookie = cookie;
    }

    public static String getSetCookie() {
        return setCookie;
    }

    static {
        ROOT = STAGING;
        setupRestClient();
    }

    public static ICorsaliteApi get() {
        return client;
    }

    public static void setBaseUrl(String url){
        if(!TextUtils.isEmpty(url)) {
            ROOT = url;
        }
    }

    private static void setupRestClient() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ExamModel.class, new ExerciseModelResponseDeserializer()) // This is the important line ;)
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
