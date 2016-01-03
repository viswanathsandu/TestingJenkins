package com.education.corsalite.activities;

import android.app.Activity;
import android.os.Bundle;

import com.education.corsalite.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class AppConfig extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readFeatures();
    }

    public void readFeatures(){

        String jsonResponse = FileUtils.loadJSONFromAsset(this.getAssets(), "api/features.json");
        try {

            JSONObject obj = new JSONObject(jsonResponse);
            boolean isFeature1 = obj.getBoolean("feature1");
            boolean isFeature2 = obj.getBoolean("fetuare2");
            boolean isFeature3 = obj.getBoolean("feature3");
            boolean isFeature4 = obj.getBoolean("fetuare4");
            boolean isFeature5 = obj.getBoolean("feature5");
            boolean isFeature6 = obj.getBoolean("fetuare6");
            boolean isFeature7 = obj.getBoolean("feature7");
            boolean isFeature8 = obj.getBoolean("fetuare8");
            boolean isFeature9 = obj.getBoolean("feature9");
            boolean isFeature10 = obj.getBoolean("fetuare10");
        }catch (JSONException e){

        }

    }
}
