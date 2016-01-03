package com.education.corsalite.utils;

import android.content.Context;

import com.education.corsalite.models.FeatureModel;
import com.google.gson.Gson;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class AppConfig {

    public FeatureModel readFeatures(Context context){

        String jsonResponse = FileUtils.loadJSONFromAsset(context.getAssets(), "api/features.json");
        return new Gson().fromJson(jsonResponse,FeatureModel.class);

    }
}
