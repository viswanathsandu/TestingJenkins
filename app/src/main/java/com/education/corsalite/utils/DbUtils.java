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
        String sourceFileName = "/data/data/"+ BuildConfig.APPLICATION_ID+"/databases/corsalite.db";
        String destinationFileName = FileUtils.get(context).getAppRootFolder() + File.separator + "corsalite.db";
        FileUtils.get(context).copyFile(sourceFileName, destinationFileName);
    }

    public void loadDatabaseFromBackup() {
        String sourceFileName = FileUtils.get(context).getAppRootFolder() + File.separator + "corsalite.db";
        String destinationFileName = "/data/data/"+ BuildConfig.APPLICATION_ID+"/databases/corsalite.db";
        FileUtils.get(context).copyFile(sourceFileName, destinationFileName);
    }

    public boolean isDatabaseExist() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = "/data/data/"+BuildConfig.APPLICATION_ID+"/databases/corsalite.db";
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            L.error(e.getMessage(), e);
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

}
