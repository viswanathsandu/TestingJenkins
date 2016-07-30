package com.education.corsalite.models.responsemodels;

import android.content.Context;
import android.text.TextUtils;

import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.L;
import com.orm.SugarRecord;

/**
 * Created by vissu on 9/11/15.
 */
public class BaseModel extends SugarRecord {
    /*
    This is used to determine which user has saved the data
     */
    private String baseUserId;

    /**
     * This is specifically used to support lists in Sugar Db
     */
    public String reflectionJsonString = null;

    public BaseModel() {
    }

    public void setBaseUserId(Context context) {
        try {
            if (!TextUtils.isEmpty(AppPref.get(context).getUserId())) {
                baseUserId = AppPref.get(context).getUserId();
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public boolean isCurrentUser(Context context) {
        try {
            if (!TextUtils.isEmpty(baseUserId) && !TextUtils.isEmpty(AppPref.get(context).getUserId())) {
                return baseUserId.equalsIgnoreCase(AppPref.get(context).getUserId());
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return false;
    }
}
