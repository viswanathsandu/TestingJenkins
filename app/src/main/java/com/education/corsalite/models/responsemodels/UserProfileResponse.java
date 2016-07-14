package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 9/12/15.
 */
public class UserProfileResponse extends BaseResponseModel{
    @SerializedName("BasicProfile")
    public BasicProfile basicProfile;
    @Ignore
    @SerializedName("ExamDetails")
    public List<ExamDetail> examDetails;

    public UserProfileResponse() {
    }
}
