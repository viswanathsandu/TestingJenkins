package com.education.corsalite.models.socket.response;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vissu on 4/1/16.
 */
public class UpdateLeaderBoardEvent {
    public String event;
    @SerializedName("TestQuestionPaperId")
    public String testQuestionPaperId;
    @SerializedName("LeaderBoardTxt")
    public String leaderBoardTxt;
    @Ignore
    private List<LeaderBoardStudent> students;

    public UpdateLeaderBoardEvent(String event, String testQuestionPaperId, String leaderBoardText) {
        this.event = event;
        this.testQuestionPaperId = testQuestionPaperId;
        students = getStudents();
    }

    public List<LeaderBoardStudent> getStudents() {
        List<LeaderBoardStudent> students = new ArrayList<>();
        if(!TextUtils.isEmpty(leaderBoardTxt)) {
            String[] studentsArray = leaderBoardTxt.split(",");
            for (int i = 0; i < studentsArray.length; i++) {
                if (!TextUtils.isEmpty(studentsArray[i].trim())) {
                    String[] studentData = studentsArray[i].trim().split(":");
                    if (studentData.length >= 3) {
                        students.add(new LeaderBoardStudent(studentData[0], studentData[1], studentData[2]));
                    }
                }
            }
        }
        return students;
    }
}
