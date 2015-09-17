package com.education.corsalite.api;

import android.content.Context;
import android.content.res.AssetManager;

import com.education.corsalite.config.AppConfig;
import com.education.corsalite.enums.NetworkMode;
import com.education.corsalite.responsemodels.LoginResponse;
import com.education.corsalite.responsemodels.LogoutResponse;
import com.education.corsalite.responsemodels.MessageResponse;
import com.education.corsalite.responsemodels.UserProfileResponse;
import com.education.corsalite.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.responsemodels.VirtualCurrencySummaryResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.FileUtils;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by vissu on 9/17/15.
 */
public class ApiManager {

    private static ApiManager instance;
    private AssetManager assets;

    public static ApiManager getInstance(Context context) {
        if(instance == null) {
            instance = new ApiManager();
        }
        instance.assets = context.getAssets();
        return instance;
    }

    public boolean isApiOnline() {
        L.info("Network Mode is "+AppConfig.NETWORK_MODE.getValue());
        return AppConfig.NETWORK_MODE == NetworkMode.ONLINE;
    }

    // Dummy response object
    private Response getRetrofitResponse() {
        Response response = new Response("http://corsalite.com", 200, "Success", new ArrayList<Header>(), null);
        return response;
    }

    public void login(String loginId, String passwordHash, ApiCallback<LoginResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().login(loginId, passwordHash, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/login.json");
            L.info("Response for 'api/login.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, LoginResponse.class), getRetrofitResponse());
        }
    }

    public void logout(String update, ApiCallback<LogoutResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().logout(update, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/logout.json");
            L.info("Response for 'api/logout.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, LogoutResponse.class), getRetrofitResponse());
        }
    }

    public void getUserProfile(String studentId, ApiCallback<UserProfileResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().getUserProfile(studentId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/user_profile.json");
            L.info("Response for 'api/user_profile.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, UserProfileResponse.class), getRetrofitResponse());
        }
    }

    public void getVirtualCurrencyBalance(String studentId, ApiCallback<VirtualCurrencyBalanceResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().getVirtualCurrencyBalance(studentId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/virtual_currency_balance.json");
            L.info("Response for 'api/virtual_currency_balance.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, VirtualCurrencyBalanceResponse.class), getRetrofitResponse());
        }
    }

    public void getVirtualCurrencyTransactions(String studentId, ApiCallback<VirtualCurrencySummaryResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().getVirtualCurrencyTransactions(studentId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/virtual_currency_summary.json");
            L.info("Response for 'api/virtual_currency_summary.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, VirtualCurrencySummaryResponse.class), getRetrofitResponse());
        }
    }

    public void getMessages(String studentId, ApiCallback<MessageResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().getMessages(studentId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/messages.json");
            L.info("Response for 'api/messages.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, MessageResponse.class), getRetrofitResponse());
        }
    }
}
