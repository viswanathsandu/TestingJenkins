package com.education.corsalite.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * Created by vissu on 9/17/15.
 */
public class FileUtils {

    private Writer writer;
    private final Context context;

    public FileUtils(Context context) {
        super();
        this.context = context;
    }

    public String write(String fileName, String data, String folderStructure) {
        File root = Environment.getExternalStorageDirectory();
        File outDir = new File(root.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER + File.separator +folderStructure);
        if (!outDir.isDirectory()) {
            outDir.mkdirs();
        }

        File savingDirectory ;
        if(fileName.endsWith(Constants.VIDEO_FILE)) {
            savingDirectory = new File(outDir.getAbsolutePath() + File.separator + Constants.VIDEO_FOLDER);
        } else {
            savingDirectory = new File(outDir.getAbsolutePath() + File.separator + Constants.HTML_FOLDER);
        }
        if (!savingDirectory.isDirectory()) {
            savingDirectory.mkdirs();
        }
        try {
            if (!savingDirectory.isDirectory()) {
                throw new IOException(
                        "Unable to create directory Corsalite. Maybe the SD card is mounted?");
            }
            File outputFile = new File(savingDirectory, fileName);
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(data);
            writer.close();
            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            Log.w("Corsalite", e.getMessage(), e);
            Toast.makeText(context, e.getMessage() + " Unable to write to external storage.",
                    Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private boolean deleteChildren(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteChildren(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }

    private void deleteParent(File path){
        if(path.exists()) {
            File[] files = path.listFiles();
            if (files.length <= 0) {
                File parent = path.getParentFile();
                path.delete();
                deleteParent(parent);

            }
        }

    }
    public void delete(String selectedPath){
        File root = Environment.getExternalStorageDirectory();
        File fileorDir = new File(root.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER + File.separator +selectedPath);

        if(fileorDir.isDirectory()){
            deleteChildren(fileorDir);
        }else if(fileorDir.isFile()){
            File parentFile = fileorDir.getParentFile();
            fileorDir.delete();
            deleteParent(parentFile);
        }
    }

    public String loadJSONFromAsset(AssetManager assets, String fileName) {
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

    public String getUrlFromFile(File file) {
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
