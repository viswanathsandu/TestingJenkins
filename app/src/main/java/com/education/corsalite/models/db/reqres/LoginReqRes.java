package com.education.corsalite.models.db.reqres;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.LoginResponse;

/**
 * Created by vissu on 11/27/15.
 */
public class LoginReqRes extends BaseModel{
    public String loginId;
    public String passwordHash;
    public LoginResponse response;
}
