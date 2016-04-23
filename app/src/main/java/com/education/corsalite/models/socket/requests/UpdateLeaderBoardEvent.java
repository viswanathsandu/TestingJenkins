package com.education.corsalite.models.socket.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 3/30/16.
 */
public class UpdateLeaderBoardEvent {

    public final String event = "UpdateLeaderBoard";
    @SerializedName("TestQuestionPaperId")
    public String testQuestionPaperId;

    public UpdateLeaderBoardEvent(String testQuestionPaperId) {
        this.testQuestionPaperId = testQuestionPaperId;
    }
}
