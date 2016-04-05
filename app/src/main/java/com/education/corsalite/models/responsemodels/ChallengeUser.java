package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/2/16.
 */
public class ChallengeUser extends BaseModel {

    @SerializedName("idChallengeTest")
    public String challengeTestId;
    @SerializedName("Role")
    public String role;
    @SerializedName("idTestAnswerPaper")
    public String testAnswerPaperId;
    @SerializedName("isChallengeExam")
    public String challengeExamId;
    @SerializedName("Status")
    public String status;
    @SerializedName("StartedDateTime")
    public String startedDateTime;
    @SerializedName("idChallengeTestParent")
    public String challengeTestParentId;
    @SerializedName("Course")
    public String course;
    @SerializedName("Subject")
    public String subject;
    @SerializedName("Chapter")
    public String chapter;
    @SerializedName("idStudent")
    public String idStudent;
    @SerializedName("VirtualCurrencyWon")
    public String VirtualCurrencyWon;
    @SerializedName("InitiatedDateTime")
    public String initiatedDateTime;
    @SerializedName("QuestionCount")
    public String questionCount;
    @SerializedName("Duration")
    public String duration;
    @SerializedName("idTestQuestionPaper")
    public String idTestQuestionPaper;
    @SerializedName("DisplayName")
    public String displayName;
    @SerializedName("PhotoUrl")
    public String photoUrl;
    @SerializedName("Score")
    public String score;
    @SerializedName("VirtualCurrencyChallenged")
    public String virtualCurrencyChallenged;
}
