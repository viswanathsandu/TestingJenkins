package com.education.corsalite.utils;

import android.content.Context;

import com.education.corsalite.BuildConfig;
import com.orm.SugarContext;

import java.io.File;

/**
 * Created by vissu on 7/29/16.
 */

public class AppUpdateUtils {

    private Context mContext;
    private FileUtils fileUtils;

    public AppUpdateUtils(Context context) {
        this.mContext = context;
        fileUtils = FileUtils.get(context);
    }

    public void update(String versionName) {
        String versionsFolderPath = fileUtils.getAppRootFolder() + File.separator + "versions";
        String filename = versionName + ".meta";
        File file = new File(versionsFolderPath, filename);
        if(!file.exists()) {
            // App opened for the first time with this version
            if(BuildConfig.VERSION_CODE >= 102000 && BuildConfig.VERSION_CODE <= 102099) {
                // delete all content and database
                fileUtils.deleteRootLevel(fileUtils.getAppRootFolder());
                fileUtils.deleteRootLevel("/data/data/"+BuildConfig.APPLICATION_ID+"/databases/corsalite.db");
                SugarContext.init(mContext.getApplicationContext());
            }
            // create directories
            File dir = new File(versionsFolderPath);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
        }
    }
}
