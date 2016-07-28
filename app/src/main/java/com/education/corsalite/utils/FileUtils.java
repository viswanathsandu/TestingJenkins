package com.education.corsalite.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
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
    private Context context;

    public static FileUtils instance;

    public static FileUtils get(Context context) {
        if(instance == null) {
            instance = new FileUtils();
        }
        instance.context = context;
        return instance;
    }

    private FileUtils() {
        super();
    }

    public String getParentFolder() {
        File root = Environment.getExternalStorageDirectory();
        File parent = new File(root.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER + File.separator + AppPref.get(context).getUserId());
        return parent.getAbsolutePath();
    }

    public String write(String fileName, String data, String folderStructure) {
        File root = Environment.getExternalStorageDirectory();
        File outDir = null;
        if (TextUtils.isEmpty(folderStructure)) {
            outDir = new File(getParentFolder());
        } else {
            outDir = new File(getParentFolder() + File.separator + folderStructure);
        }
        if (!outDir.isDirectory()) {
            outDir.mkdirs();
        }

        File savingDirectory;
        if (fileName.endsWith(Constants.VIDEO_FILE)) {
            savingDirectory = new File(outDir.getAbsolutePath() + File.separator + Constants.VIDEO_FOLDER);
        } else if (fileName.endsWith(Constants.TEST_FILE)) {
            savingDirectory = new File(outDir.getAbsolutePath() + File.separator + Constants.TESTS_FOLDER);
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

    public String readFromFile(String fileName, String folderStructure) {
        File root = Environment.getExternalStorageDirectory();
        File file = null;
        if (TextUtils.isEmpty(folderStructure)) {
            file = new File(getParentFolder());
        } else {
            file = new File(getParentFolder() + File.separator + folderStructure);
        }
        if (fileName.endsWith(Constants.VIDEO_FILE)) {
            file = new File(file.getAbsolutePath() + File.separator + Constants.VIDEO_FOLDER + File.separator + fileName);
        } else if (fileName.endsWith(Constants.TEST_FILE)) {
            file = new File(file.getAbsolutePath() + File.separator + Constants.TESTS_FOLDER + File.separator + fileName);
        } else {
            file = new File(file.getAbsolutePath() + File.separator + Constants.HTML_FOLDER + File.separator + fileName);
        }
        if (file.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                return text.toString();
            } catch (IOException e) {
                L.error(e.getMessage(), e);
            }
        }
        return null;
    }

    private boolean deleteChildren(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteChildren(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    private void deleteParent(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files.length <= 0) {
                File parent = path.getParentFile();
                path.delete();
                deleteParent(parent);

            }
        }

    }

    public void deleteFile(String fileName) {
        File root = Environment.getExternalStorageDirectory();
        File fileorDir;
        if(fileName.endsWith("." + Constants.TEST_FILE)) {
            fileorDir = new File(getParentFolder() + File.separator + Constants.TESTS_FOLDER + File.separator + fileName);
        } else {
            fileorDir = new File(getParentFolder() + File.separator + fileName);
        }

        if (fileorDir.isDirectory()) {
            deleteChildren(fileorDir);
        } else if (fileorDir.isFile()) {
            File parentFile = fileorDir.getParentFile();
            fileorDir.delete();
            deleteParent(parentFile);
        }
    }

    public void delete(String selectedPath) {
        File root = Environment.getExternalStorageDirectory();
        File fileorDir = new File(getParentFolder() + File.separator + selectedPath);

        if (fileorDir.isDirectory()) {
            deleteChildren(fileorDir);
        } else if (fileorDir.isFile()) {
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
