package com.education.corsalite.models.responsemodels;

import android.text.TextUtils;

import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.utils.L;
import com.orm.SugarRecord;

/**
 * Created by vissu on 9/11/15.
 */
public class BaseModel extends SugarRecord {
    /*
    This is used to determine which user has saved the data
     */
    public String userId = "";

    /**
     * This is specifically used to support lists in Sugar Db
     */
    public byte[] reflectionJsonString = null;

    public BaseModel() {
    }

    public void setUserId() {
        try {
            if (!TextUtils.isEmpty(LoginUserCache.getInstance().getUserId())) {
                userId = LoginUserCache.getInstance().getUserId();
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public boolean isCurrentUser() {
        try {
            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(LoginUserCache.getInstance().getUserId())) {
                return userId.equalsIgnoreCase(LoginUserCache.getInstance().getUserId());
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return false;
    }
}
