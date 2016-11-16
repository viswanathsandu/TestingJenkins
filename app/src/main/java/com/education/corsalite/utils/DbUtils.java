package com.education.corsalite.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.education.corsalite.BuildConfig;

import java.io.File;

/**
 * Created by vissu on 8/29/16.
 */

public class DbUtils {

    private static DbUtils instance;
    private Context context;

    public static DbUtils get(Context context) {
        if(instance == null) {
            instance = new DbUtils();
        }
        instance.context = context;
        return  instance;
    }

    public void backupDatabase() {
        // TODO : disabling the db backup temporarily
        /*try {
            String fileName = "corsalite.db";
            String sourceFileName = "/data/data/" + BuildConfig.APPLICATION_ID + "/databases/corsalite.db";
            String destinationDirectory = FileUtils.get(context).getAppRootFolder();
            FileUtils.get(context).copyFile(sourceFileName, destinationDirectory, fileName);
            L.info("Database backup successful");
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            L.info("Database backup failed");
        }*/
    }

    public void loadDatabaseFromBackup() {
        // TODO : disabling the db restore temporarily
        /*try {
            String fileName = "corsalite.db";
            String sourceFileName = FileUtils.get(context).getAppRootFolder() + File.separator + "corsalite.db";
            String destinationFileName = "/data/data/" + BuildConfig.APPLICATION_ID + "/databases";
            FileUtils.get(context).copyFile(sourceFileName, destinationFileName, fileName);
            L.info("successfully loaded Database from backup");
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            L.info("failed to load Database from backup");
        }*/
    }

    public boolean isDatabaseExist() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = "/data/data/"+BuildConfig.APPLICATION_ID+"/databases/corsalite.db";
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            L.error(e.getMessage(), e);
        }
        if (checkDB != null && checkDB.isOpen()) {
            checkDB.close();
        }
        return checkDB != null;
    }

    public boolean isDatabaseFileExist(Context context) {
        try {
            String myPath = "/data/data/"+BuildConfig.APPLICATION_ID+"/databases/corsalite.db";
            File file = new File(myPath);
            return file.exists();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return false;
    }
}
