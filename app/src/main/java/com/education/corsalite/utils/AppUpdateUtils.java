package com.education.corsalite.utils;

import android.content.Context;

import com.education.corsalite.BuildConfig;

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

    public void update() {
        if(!fileUtils.appRootExists()) {
            // delete old folder
            fileUtils.deleteRootLevel(fileUtils.getAppRootFolder(BuildConfig.PARENT_FOLDER.replace(".", "")));
        }
    }
}
