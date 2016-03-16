package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 3/16/16.
 */
public class CreateChallengeRequest {

    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("QuestionCount")
    public String questionCount;
    @SerializedName("Duration")
    public String durationInMins;
    @SerializedName("VCChallenged")
    public String virtualCurrencyChallenged;
    @SerializedName("idSubject")
    public String subjectId;
    @SerializedName("idChapter")
    public String chapterId;
    @SerializedName("idCourse")
    public String courseId;
    @SerializedName("idExam")
    public String examId;
    @SerializedName("ChallengedFriendsList")
    public ChallengeFriend challengedFriendsId;

}
