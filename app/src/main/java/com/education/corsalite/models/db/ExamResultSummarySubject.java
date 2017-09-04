package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 11-08-2017.
 */

public class ExamResultSummarySubject extends BaseModel {


    @SerializedName("idSubject")
    public String subjectId;
    @SerializedName("SubjectName")
    public String subjectName;
    @SerializedName("QuestionTotal")
    public String questionTotal;
    @SerializedName("QuestionCorrect")
    public String questionCorrect;
    @SerializedName("QuestionWrong")
    public String questionWrong;
    @SerializedName("MarkMax")
    public String markMax;
    @SerializedName("ScorePositive")
    public String scorePositive;
    @SerializedName("ScoreNegative")
    public String scoreNegative;
    @SerializedName("ScoreTotal")
    public String scoreTotal;
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
    @SerializedName("sectionData")
    public List<ExamResultSubjectSection> sections;
}
