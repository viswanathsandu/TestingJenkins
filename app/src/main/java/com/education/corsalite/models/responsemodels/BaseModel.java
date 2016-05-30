package com.education.corsalite.models.responsemodels;

import android.text.TextUtils;

import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.IDomainEntity;
import com.education.corsalite.utils.L;
import com.orm.SugarRecord;

/**
 * Created by vissu on 9/11/15.
 */
public class BaseModel extends SugarRecord implements IDomainEntity<BaseModel> {
    /*
    This is used to determine which user has saved the data
     */
    public String userId = "";

    /**
     * This is specifically used to support lists in Sugar Db
     */
    public String reflectionJsonString = null;

    public BaseModel() {
    }

    public void setUserId() {
        try {
            if (LoginUserCache.getInstance().loginResponse != null && !TextUtils.isEmpty(LoginUserCache.getInstance().loginResponse.userId)) {
                userId = LoginUserCache.getInstance().loginResponse.userId;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public boolean isCurrentUser() {
        try {
            if (!TextUtils.isEmpty(userId) && LoginUserCache.getInstance().loginResponse != null && TextUtils.isEmpty(LoginUserCache.getInstance().loginResponse.userId)) {
                return userId.equalsIgnoreCase(LoginUserCache.getInstance().loginResponse.userId);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return false;
    }
}
