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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Created by vissu on 9/17/15.
 */
public class FileUtils {

    private Writer writer;
    private Context context;

    public static FileUtils instance;

    public static FileUtils get() {
        return instance != null ? instance : null;
    }

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

    public String getAppRootFolder() {
        File root = Environment.getExternalStorageDirectory();
        File parent = new File(root.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER);
        return parent.getAbsolutePath();
    }

    public String getAppRootFolder(String fileName) {
        File root = Environment.getExternalStorageDirectory();
        File parent = new File(root.getAbsolutePath() + File.separator + fileName);
        return parent.getAbsolutePath();
    }

    public boolean appRootExists() {
        try {
            File dir = new File(getAppRootFolder());
            return dir.exists() && dir.isDirectory();
        } catch (Exception e) {
            return false;
        }
    }

    public String getParentFolder() {
        File root = Environment.getExternalStorageDirectory();
        File parent = new File(root.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER + File.separator + AppPref.get(context).getUserId());
        return parent.getAbsolutePath();
    }

    public String getVideoDownloadPath(String videoId) {
        String fileName = "v." + Constants.VIDEO_FILE;
        String folderPath = getParentFolder() + File.separator + Constants.VIDEO_FOLDER + File.separator + videoId;
        File folder = new File(folderPath);
        if(!folder.isDirectory() && !folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, fileName);
        if(!file.exists()) {
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write("");
                writer.close();
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
        }
        return file.getAbsolutePath();
    }

    public String  getContentFileName(String contentId) {
        return contentId + "." + Constants.HTML_FILE;
    }

    public String  getContentFilePath() {
        String filePath = File.separator + Constants.CONTENT_FOLDER;
        return filePath;
    }

    public String getTestsFolderPath(String testQuestionPaperId) {
        String filePath = File.separator + Constants.TESTS_FOLDER + File.separator + testQuestionPaperId;
        return filePath;
    }

    public String getLogFolderPath() {
        String filePath = getAppRootFolder() + File.separator + Constants.LOGS_FOLDER;
        return filePath;
    }

    public File getLogFilePath(String fileName) {
        String filePath = getAppRootFolder() + File.separator + Constants.LOGS_FOLDER;
        File file = new File(filePath);
        if(!file.exists()) {
            file.mkdirs();
        }
        file = new File(filePath, fileName);
        return file;
    }

    public String getApkFolderPath() {
        String filePath = getAppRootFolder() + File.separator + Constants.APK_FOLDER;
        String fileName = Constants.APK_FILE;
        File file = new File(filePath, fileName);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public String  getExerciseFileName() {
        return "e." + Constants.TEST_FILE;
    }

    public String  getTestQuestionPaperFileName() {
        return "q." + Constants.TEST_FILE;
    }

    public String  getTestPaperIndexFileName() {
        return "i." + Constants.TEST_FILE;
    }

    public String  getTestAnswerPaperFileName() {
        return "a." + Constants.TEST_FILE;
    }

    public String write(String fileName, String data, String folderStructure) {
        File dir = null;
        if (TextUtils.isEmpty(folderStructure)) {
            dir = new File(getParentFolder());
        } else if(folderStructure.startsWith("/")){
            dir = new File(getParentFolder() + folderStructure);
        } else {
            dir = new File(getParentFolder() + File.separator + folderStructure);
        }
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        try {
            if (!dir.isDirectory()) {
                throw new IOException(
                        "Unable to create directory Corsalite. Maybe the SD card is mounted?");
            }
            File outputFile = new File(dir, fileName);
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(data);
            writer.close();
            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            Log.w("Corsalite", e.getMessage(), e);
            Toast.makeText(context, e.getMessage() + " Unable to write to external storage.", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public String readFromFile(String fileName, String folderStructure) {
        File file;
        if (TextUtils.isEmpty(folderStructure)) {
            file = new File(getParentFolder() + File.separator + fileName);
        } else {
            file = new File(getParentFolder() + File.separator + folderStructure + File.separator + fileName);
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

    public void deleteRootLevel(String selectedPath) {
        File fileorDir = new File(selectedPath);
        if (fileorDir.isDirectory()) {
            deleteChildren(fileorDir);
            L.info("Deleted path "+fileorDir);
        } else if (fileorDir.isFile()) {
            File parentFile = fileorDir.getParentFile();
            fileorDir.delete();
            deleteParent(parentFile);
            L.info("Deleted path "+fileorDir);
        }
    }

    public void delete(String selectedPath) {
        File fileorDir;
        if(TextUtils.isEmpty(selectedPath)) {
            return;
        }
        if(selectedPath.startsWith("/")) {
            fileorDir = new File(getParentFolder() + selectedPath);
        } else {
            fileorDir = new File(getParentFolder() + File.separator + selectedPath);
        }
        if (fileorDir.isDirectory()) {
            deleteChildren(fileorDir);
            L.info("Deleted path "+fileorDir);
        } else if (fileorDir.isFile()) {
            File parentFile = fileorDir.getParentFile();
            fileorDir.delete();
            deleteParent(parentFile);
            L.info("Deleted path "+fileorDir);
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

    public void copyFile(String sourceFile, String destinationDir, String fileName) {
        try {
            File dir = new File(destinationDir);
            dir.mkdirs();
            File destination = new File(destinationDir, fileName);
            if (!destination.exists()) {
                destination.createNewFile();
            }
            copyFile(sourceFile, destination.getAbsolutePath());
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void copyFile(String sourceFile, String destinationFile) {
        InputStream is = null;
        OutputStream os = null;
        try {

            is = new FileInputStream(new File(sourceFile));
            os = new FileOutputStream(new File(destinationFile));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (Exception e) {
          L.error(e.getMessage(), e);
        } finally {
            try {
                is.close();
                os.close();
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
        }
    }
}
