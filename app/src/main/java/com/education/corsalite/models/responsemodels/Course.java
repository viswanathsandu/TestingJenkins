package com.education.corsalite.models.responsemodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.io.Serializable;

/**
 * Created by vissu on 9/12/15.
 */
public class Course extends BaseModel  implements Serializable {

    @SerializedName("idCourse")
    public Integer courseId;
    @SerializedName("Name")
    public String name;
    @SerializedName("DefaultYN")
    public String isDefault;
    @SerializedName("EndDate")
    public String endDate;
    @SerializedName("IsCourseEnded")
    public String isCourseEnded;
    @Ignore
    @SerializedName("isTestSeries")
    public String testSeriesId;
    @Ignore
    @SerializedName("idCourseInstance")
    public String courseInstanceId;

    @Ignore
    public Integer isContentReading;
    @Ignore
    public Integer isVideos;
    @Ignore
    public Integer isForums;
    @Ignore
    public Integer isSmartClass;
    @Ignore
    public Integer isPracticeTest;
    @Ignore
    public Integer isReward;
    @Ignore
    public Integer isMockTest;
    @Ignore
    public Integer isWelcome;
    @Ignore
    public Integer isProfile;
    @Ignore
    public Integer isOffline;
    @Ignore
    public Integer isScheduledTests;
    @Ignore
    public Integer isCurriculum;
    @Ignore
    public Integer isStudyCenter;
    @Ignore
    public Integer isChallengeTest;
    @Ignore
    public Integer isAnalytics;
    @Ignore
    public Integer isRecommendedReading;
    @Ignore
    public Integer isProgressReport;
    @Ignore
    public Integer isTimeManagement;
    
    public boolean isDefault() {
        return (!TextUtils.isEmpty(isDefault) && isDefault.equals("Y"));
    }

    public boolean isEnded() {
        return (!TextUtils.isEmpty(isCourseEnded) && isCourseEnded.equalsIgnoreCase("Y"));
    }

    public boolean isTestSeries() {
        return !TextUtils.isEmpty(testSeriesId) && !testSeriesId.equalsIgnoreCase("0");
    }

    @Override
    public String toString() {
        return name;
    }
}
