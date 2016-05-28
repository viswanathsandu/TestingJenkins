package com.education.corsalite.utils;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vissu on 9/17/15.
 */
public class FileUtils {

    public static String loadJSONFromAsset(AssetManager assets, String fileName) {
        String json = null;
        try {
            InputStream is = assets.open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String getUrlFromFile(File file) {
        StringBuilder videoUrl = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                videoUrl.append(line);
            }
            br.close();
        } catch (Exception ignore) {
        }
        return videoUrl.toString();
    }
}
