package com.corsalite.tabletapp.services;

import android.text.TextUtils;

import com.corsalite.tabletapp.activities.AbstractBaseActivity;
import com.corsalite.tabletapp.api.ICorsaliteApi;
import com.corsalite.tabletapp.cache.LoginUserCache;
import com.corsalite.tabletapp.deserializer.ExerciseModelResponseDeserializer;
import com.corsalite.tabletapp.interceptors.SessionRequestInterceptor;
import com.corsalite.tabletapp.models.responsemodels.ExamModel;
import com.corsalite.tabletapp.utils.CookieUtils;
import com.corsalite.tabletapp.utils.L;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by vissu on 9/11/15.
 */
public class ApiClientService {

    private static ICorsaliteApi client;
    private static String PROD = "http://app.corsalite.com/ws/webservices/";
    private static String STAGING = "http://staging.corsalite.com/v1/webservices/";
    private static String PROD_SOCKET = "ws://app.corsalite.com:9300";
    private static String STAGING_SOCKET = "ws://staging.corsalite.com:9300";

    private static String ROOT = STAGING;
    private static String ROOT_SOCKET = STAGING_SOCKET;

    private static String setCookie;

    public static String getBaseUrl() {
        return ROOT == null ? "" : ROOT.replace("webservices/", "");
    }

    public static void setSocketUrl(String url) {
        if(!TextUtils.isEmpty(url)) {
            ROOT_SOCKET = url;
        }
    }

    public static String getSocketUrl() {
        return ROOT_SOCKET;
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

    public static void setBaseUrl(String url){
        if(!TextUtils.isEmpty(url)) {
            ROOT = url;
            setupRestClient();
        }
    }

    private static void setupRestClient() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ExamModel.class, new ExerciseModelResponseDeserializer()) // This is the important line ;)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ROOT)
                .setClient(new OkClient(getOkHttpClient()))
                .setRequestInterceptor(new SessionRequestInterceptor())
                .setConverter(new GsonConverter(gson))
                .build();
        client = restAdapter.create(ICorsaliteApi.class);
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            int tryCount = 0;
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                // try the request
                Response response = chain.proceed(request);
                if(response != null ) {
                    if (response.isSuccessful()) {
                        String url = request.httpUrl().toString();
                        L.info("Intercept : URL - "+url);
                        if (url.contains("webservices/AuthToken")
                                && url.contains("LoginID") && url.contains("PasswordHash")) {
                            // save login request
                            L.info("Intercept : Saving login request");
                            LoginUserCache.getInstance().loginRequest = request;
                        }
                    } else if (!response.isSuccessful()) {
                        L.info("Intercept : try again - "+tryCount);
                        while (!response.isSuccessful() && tryCount < 3) {
                            L.info("Intercept : Request is not successful - " + tryCount);
                            tryCount++;
                            // retry the request with latest cookie
                            String setcookie = makeAuthCallAndGetcookie(chain);
                            if(setcookie != null && !setcookie.isEmpty()) {
                                request = request.newBuilder().removeHeader("cookie").build();
                                request = request.newBuilder().addHeader("cookie", setcookie).build();
                                L.info("Intercept : Retrying api call");
                                L.info("Intercept : Request : " + new Gson().toJson(request));
                                response = chain.proceed(request);
                                L.info("Intercept : Response : " + response.code()+ " -- " + new Gson().toJson(response));
                            }
                        }
                        tryCount = 0;
                    }
                }
                // otherwise just pass the original response on
                return response;
            }

            public String getSessionCookie(com.squareup.okhttp.Response response) {
                String cookie = CookieUtils.getCookieString(response);
                if (response.code() != 401 && cookie != null) {
                    L.info("Intercept : save session cookie : "+cookie);
                    ApiClientService.setSetCookie(cookie);
                    return cookie;
                }
                return "";
            }

            private String makeAuthCallAndGetcookie(Chain chain) throws IOException {
                Request loginRequest = LoginUserCache.getInstance().loginRequest;
                if(loginRequest != null) {
                    L.info("Intercept : Making auth call");
                    Response loginResponse = chain.proceed(loginRequest);
                    L.info("Intercept : Auth call response : " + loginResponse.code()+ " -- " +new Gson().toJson(loginResponse));
                    if(loginResponse != null && loginResponse.isSuccessful()) {
                        L.info("Intercept : Auth call saving session cookie");
                        AbstractBaseActivity.saveSessionCookie(loginResponse);
                        return getSessionCookie(loginResponse);
                    }
                }
                return null;
            }
        });
        return client;
    }
}
