package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Aastha on 19/11/15.
 */
public class UsageAnalysis extends BaseResponseModel{
    @SerializedName("userAuditArr")
    public List<Float> userAuditList;
    @SerializedName("allUsersAuditArr")
    public List<Float> allUserAuditList;

    @SerializedName("pageNamesArr")
    public List<String> pagesName;
    @SerializedName("pageUsagePercentage")
    public List<Integer> pageUsagePercentageList;

    public UsageAnalysis() {
    }
}
