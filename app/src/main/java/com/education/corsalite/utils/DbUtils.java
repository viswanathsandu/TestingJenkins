package com.education.corsalite.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.education.corsalite.BuildConfig;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by vissu on 8/29/16.
 */

public class DbUtils {

    protected void copyDataBase(Context context) throws IOException {

        //Open your local db as the input stream
        InputStream myInput = context.getApplicationContext().getAssets().open("file.db");

        // Path to the just created empty db
        String outFileName = "/data/data/"+ BuildConfig.APPLICATION_ID+"/databases/corsalite.db";

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    protected boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = "/data/data/com.yourpackage/databases/" + "file.db";
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database does't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

}
