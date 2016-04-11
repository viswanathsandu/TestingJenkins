package com.education.corsalite.models.responsemodels;

import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.IDomainEntity;
import com.education.corsalite.utils.L;

/**
 * Created by vissu on 9/11/15.
 */
public abstract class BaseModel implements IDomainEntity<BaseModel> {
    /*
    This is used to determine which user has saved the data
     */
    private String userId = "";

    public void setUserId() {
        try {
            userId = LoginUserCache.getInstance().loginResponse.userId;
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public String getUserID() {
        return userId;
    }

    public boolean isCurrentUser() {
        try {
            return userId.equalsIgnoreCase(LoginUserCache.getInstance().loginResponse.userId);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return false;
        }
    }
}
