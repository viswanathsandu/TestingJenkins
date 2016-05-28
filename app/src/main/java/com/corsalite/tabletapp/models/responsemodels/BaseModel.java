package com.corsalite.tabletapp.models.responsemodels;

import com.corsalite.tabletapp.cache.LoginUserCache;
import com.corsalite.tabletapp.db.IDomainEntity;
import com.corsalite.tabletapp.utils.L;
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

    public BaseModel() {}

    public void setUserId() {
        try {
            userId = LoginUserCache.getInstance().loginResponse.userId;
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
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
