package com.corsalite.tabletapp.models.responsemodels;

import android.text.TextUtils;

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
    public String isCchallengeExam;
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
    public String virtualCurrencyWon;
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

    public String getChallengeStatus() {
        if(TextUtils.isEmpty(virtualCurrencyWon) || virtualCurrencyWon.equals("0")) {
            return "TIE";
        } else if(virtualCurrencyWon.contains("-")) {
            return "LOST";
        } else {
            return "WON";
        }
    }
}
