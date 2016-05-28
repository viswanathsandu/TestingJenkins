package com.corsalite.tabletapp.models.responsemodels;

import android.text.TextUtils;

import com.corsalite.tabletapp.utils.Data;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

public class StudyCenter {
    public String SubjectName;
    public Integer idCourseSubject;
    @SerializedName("Chapters")
    @Ignore
    public List<Chapter> chapters;
    @Ignore
    public List<Chapter> redListChapters = new ArrayList<>();
    @Ignore
    public List<Chapter> amberListChapters = new ArrayList<>();
    @Ignore
    public List<Chapter> greenListChapters = new ArrayList<>();
    @Ignore
    public List<Chapter> blueListChapters = new ArrayList<>();

    public void resetColoredLists() {
        redListChapters.clear();
        greenListChapters.clear();
        amberListChapters.clear();
        blueListChapters.clear();
    }

    public double getCompletion(){
        int totalTopics = 0;
        int completedTopics = 0;
        for (Chapter chapter : this.chapters) {
            totalTopics = totalTopics + Integer.parseInt(TextUtils.isEmpty(chapter.totalTopics) ? "0" : chapter.totalTopics);
            completedTopics = completedTopics + Integer.parseInt(TextUtils.isEmpty(chapter.completedTopics) ? "0" : chapter.completedTopics);
        }
        if(totalTopics != 0){
            double completedPercentage = (double) completedTopics / (double) totalTopics * 100;
            return Math.round(completedPercentage * 100.0) / 100.0;
        }
        return 0;
    }

    public String getScore(){
        int earnedScore = 0;
        int totalTestScore = 0;
        for (Chapter chapter : this.chapters) {
            earnedScore = earnedScore + Data.getDoubleInInt(chapter.earnedMarks);
            totalTestScore = totalTestScore + Data.getDoubleInInt(chapter.totalTestedMarks);
        }
        return earnedScore+"/"+totalTestScore;
    }

    public String getNotes(){
        int notesCount = 0;
        for (Chapter chapter : this.chapters) {
            notesCount = notesCount+ Integer.parseInt(TextUtils.isEmpty(chapter.notesCount) ? "0" : chapter.notesCount);
        }
        return notesCount+"";
    }
}