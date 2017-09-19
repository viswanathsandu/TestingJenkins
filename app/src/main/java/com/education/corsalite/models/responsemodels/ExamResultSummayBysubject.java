package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 05-09-2017.
 */

public class ExamResultSummayBysubject extends BaseModel {
    @SerializedName("QuestionsJson")
    public List<Integer> questions;
    @SerializedName("MarkTotalJson")
    public List<Integer> totalMarks;
    @SerializedName("MarkMaxJson")
    public List<Integer> maxMarks;
    @SerializedName("PeerAvgScoreJson")
    public List<Integer> peerAvgScore;
    @SerializedName("TimeTakenJson")
    public List<Integer> timeTaken;
    @SerializedName("LevelJson")
    public List<Integer> levels;
    @SerializedName("TimeRecommendedJson")
    public List<Integer> timeRecommended;
    @SerializedName("PeerAvgTimeTakenJson")
    public List<Integer> peerAvgTimeTaken;
}
