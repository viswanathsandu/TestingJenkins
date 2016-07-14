package com.education.corsalite.utils;

import android.text.TextUtils;

import java.util.List;

import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by vissu on 9/12/15.
 */
public class CookieUtils {

    public static String getCookieString(Response response) {
        if(response.getStatus() == 200) {
            for (Header header : response.getHeaders()) {
                if (!TextUtils.isEmpty(header.getName())
                        && header.getName().equals("Set-Cookie")
                        && !header.getValue().contains("expires")) {
                    return header.getValue();
                }
            }
        }
        return null;
    }

    public static String getCookieString(com.squareup.okhttp.Response response) {
        if(response.isSuccessful()) {
            List<String> headers = response.headers("Set-Cookie");
            if (headers != null && !headers.isEmpty()) {
                for (String header : headers) {
                    if (!header.contains("expires")) {
                        return header;
                    }
                }
            }
        }
        return null;
    }
}
