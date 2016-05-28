package com.corsalite.tabletapp.utils;

import com.corsalite.tabletapp.models.responsemodels.CorsaliteError;

import java.util.ArrayList;

import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by vissu on 11/27/15.
 */
public class MockUtils {

    // Dummy retrofit response object
    public static Response getRetrofitResponse() {
        Response response = new Response("http://corsalite.com", 200, "Success", new ArrayList<Header>(), null);
        return response;
    }

    // Dummy corsalite error object
    public static CorsaliteError getCorsaliteError(String status, String message) {
        CorsaliteError error = new CorsaliteError();
        error.status = status;
        error.message = message;
        return error;
    }
}
