package com.education.corsalite.models.socket.response;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vissu on 4/1/16.
 */
public class UpdateLeaderBoardEvent {
    public String event;
    public String testQuestionPaperId;
    public List<LeaderBoardStudent> students;

    public UpdateLeaderBoardEvent(String event, String testQuestionPaperId, String leaderBoardText) {
        this.event = event;
        this.testQuestionPaperId = testQuestionPaperId;
        students = getStudents(leaderBoardText);
    }

    private List<LeaderBoardStudent> getStudents(String leaderBoardText) {
        List<LeaderBoardStudent> students = new ArrayList<>();
        if(!TextUtils.isEmpty(leaderBoardText)) {
            String[] studentsArray = leaderBoardText.split(",");
            for (int i = 0; i < studentsArray.length; i++) {
                if (!TextUtils.isEmpty(studentsArray[i].trim())) {
                    String[] studentData = studentsArray[i].trim().split(":");
                    if (studentData.length >= 2) {
                        students.add(new LeaderBoardStudent(studentData[0], studentData[1]));
                    }
                }
            }
        }
        return students;
    }
}
