package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 11-08-2017.
 */

class ExamResultSubjectSection extends BaseModel {

    @SerializedName("SectionName")
    public String name;
    @SerializedName("QuestionTotal")
    public String questionTotal;
    @SerializedName("QuestionCorrect")
    public String questionCorrect;
    @SerializedName("QuestionWrong")
    public Integer questionWrong;
    @SerializedName("MarkMax")
    public Integer marksMax;
    @SerializedName("ScorePositive")
    public Integer scorePositive;
    @SerializedName("ScoreNegative")
    public Integer scoreNegative;
    @SerializedName("PeerAvgScore")
    public String peerAvgScore;
    @SerializedName("Percentile")
    public String percentile;
    @SerializedName("TimeRecommended")
    public String timeRecommended;
    @SerializedName("TimeTaken")
    public String timeTaken;
    @SerializedName("PeerAvgTimeTaken")
    public String peerAvgTimeTaken;
}
