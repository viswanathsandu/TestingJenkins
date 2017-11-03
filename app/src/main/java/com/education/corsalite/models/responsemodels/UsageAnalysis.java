package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by Aastha on 19/11/15.
 */
public class UsageAnalysis extends BaseResponseModel {
    @Ignore
    @SerializedName("userAuditArr")
    public List<Float> userAuditList;
    @Ignore
    @SerializedName("allUsersAuditArr")
    public List<Float> allUserAuditList;
    @Ignore
    @SerializedName("pageNamesArr")
    public List<String> pagesName;
    @Ignore
    @SerializedName("pageUsagePercentage")
    public List<Integer> pageUsagePercentageList;

    public UsageAnalysis() {
    }
}
