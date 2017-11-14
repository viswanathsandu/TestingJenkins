package com.education.corsalite.gson;

import com.google.gson.GsonBuilder;

/**
 * Created by vissu on 7/28/16.
 */

public class Gson {

    private static com.google.gson.Gson instance;

    public static com.google.gson.Gson get() {
        if (instance == null) {
            instance = new GsonBuilder().create();
        }
        return instance;
    }
}
