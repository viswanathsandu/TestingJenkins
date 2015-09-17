package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vissu on 9/12/15.
 */
public class UserProfileResponse extends BaseResponseModel implements Serializable {
    @SerializedName("BasicProfile")
    public BasicProfile basicProfile;
    @SerializedName("ExamDetails")
    public List<ExamDetail> examDetails;
}
