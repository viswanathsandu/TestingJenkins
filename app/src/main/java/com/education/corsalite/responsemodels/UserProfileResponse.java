package com.education.corsalite.responsemodels;

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

    @Override
    public String toString() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
