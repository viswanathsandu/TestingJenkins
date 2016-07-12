package com.education.corsalite.services;

import com.education.corsalite.BuildConfig;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ICorsaliteApi;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.deserializer.ExerciseModelResponseDeserializer;
import com.education.corsalite.interceptors.SessionRequestInterceptor;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.CookieUtils;
import com.education.corsalite.utils.L;
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
    private static String ROOT = BuildConfig.BASE_URL;
    private static String ROOT_SOCKET = BuildConfig.SOCKET_URL;

    private static String setCookie;

    public static String getBaseUrl() {
        return BuildConfig.BASE_URL;
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

    private static void setupRestClient() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ExamModel.class, new ExerciseModelResponseDeserializer()) // This is the important line ;)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BuildConfig.BASE_API_URL)
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
                        if (url.contains("AuthToken")
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
