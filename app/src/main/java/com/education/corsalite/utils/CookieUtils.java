package com.education.corsalite.utils;

import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by vissu on 9/12/15.
 */
public class CookieUtils {

    public static String getCookieString(Response response) {
        for (Header header : response.getHeaders()) {
            if (null!= header.getName() && header.getName().equals("Set-Cookie")) {
                return header.getValue();
            }
        }
        return null;
    }
}
