package com.education.corsalite.db;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;

import java.io.File;

/**
 * Created by Vissu on 22/07/2014.
 */
public class Db4oHelper {
    public static final String DATABASE_NAME = "corsalite.db4o";
    public static final String DATABASE_Intrenal_DIR = "corsalite.db4o";
    public static final String DATABASE_SD_DIR = "corsalite";
    protected static ObjectContainer db = null;
    protected Context context = null;
    // SDcard
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    String state = Environment.getExternalStorageState();
    String root;
    File path;

    public Db4oHelper(Context context) {
        this.context = context;
    }


    public ObjectContainer db() {
        Log.i(Db4oHelper.class.getName(), "Accessing Database...");
        try {
            if ((Db4oHelper.db == null) || Db4oHelper.db.ext().isClosed()) {
                ///Toast.makeText(this.context, "Loading DB", Toast.LENGTH_SHORT).show();
                EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
                configuration.common().activationDepth(40);
//
//                configuration.common().objectClass(BaseModel.class).updateDepth(20);
//                configuration.common().objectClass(BaseModel.class).cascadeOnActivate(true);
//                configuration.common().objectClass(BaseModel.class).cascadeOnUpdate(true);

                CheckSDCard();
                writeDBtoStorage(configuration);
            }
            return Db4oHelper.db;
        } catch (Exception e) {
            Log.e(Db4oHelper.class.getName(), e.toString());
            return null;
        }
    }

    private void CheckSDCard() {
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    public void writeDBtoStorage(EmbeddedConfiguration configuration) {
        // if there is a SDcard store the file there.
        if (mExternalStorageWriteable == true) {
            // Checks to see whether the device is a Samsung or not.
            // Which requires a manual path to external sdcard or you get the emulated
            // one.
            if (android.os.Build.DEVICE.contains("Samsung")
                    || android.os.Build.MANUFACTURER.contains("samsung")
                    || android.os.Build.MANUFACTURER.contains("Samsung")) {
                //root = "/storage/extSdCard";
                root = Environment.getExternalStorageDirectory().toString();
            }
            // for any other type of device to save to SD
            else {
                root = Environment.getExternalStorageDirectory().toString();
            }
            path = new File(root + "/" + DATABASE_SD_DIR);
            Db4oHelper.db = Db4oEmbedded.openFile(configuration,
                    db4oDBFullPathSdCard(this.context));

        }
        // If no SDcard
        else {
            Db4oHelper.db = Db4oEmbedded.openFile(configuration,
                    db4oDBFullPath(this.context));
        }
    }

    private String db4oDBFullPath(Context context) {
        return context.getDir(DATABASE_Intrenal_DIR, 0) + "/" + DATABASE_NAME;
    }

    private String db4oDBFullPathSdCard(Context ctx) {
        if (!path.exists()) {
            path.mkdirs();
        }
        Log.i(Db4oHelper.class.getName(), path.toString());
        return path + "/" + DATABASE_NAME;
    }

    public void close() {
        if (db() != null) {
            db().close();
            Db4oHelper.db = null;
        }
    }

    public void deleteDatabase() {
        // We dont actually delete the database we just wipe all objects in it
        ObjectSet<Object> results = db().query(Object.class);
        if ((results != null) && (results.size() > 0)) {
            for (Object o : results) {
                db().delete(o);
            }
            close();
            Toast.makeText(this.context, "Finished Deleting Database", Toast.LENGTH_SHORT).show();
        }
    }

    protected Context getContext() {
        return this.context;
    }
}
