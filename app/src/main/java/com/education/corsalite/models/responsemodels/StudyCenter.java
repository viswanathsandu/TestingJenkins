package com.education.corsalite.models.responsemodels;

import com.education.corsalite.utils.Data;

import java.util.ArrayList;
import java.util.List;

public class StudyCenter {
    public String SubjectName;
    public Integer idCourseSubject;
    public List<Chapters> Chapters;
    public List<Chapters> redListChapters = new ArrayList<>();
    public List<Chapters> amberListChapters = new ArrayList<>();
    public List<Chapters> greenListChapters = new ArrayList<>();
    public List<Chapters> blueListChapters = new ArrayList<>();

    public void resetColoredLists() {
        redListChapters.clear();
        greenListChapters.clear();
        amberListChapters.clear();
        blueListChapters.clear();
    }

    public double getCompletion(){
        int totalTopics = 0;
        int completedTopics = 0;
        for (Chapters chapters:Chapters
             ) {
            totalTopics =+ Integer.parseInt(chapters.totalTopics);
            completedTopics =+Integer.parseInt(chapters.completedTopics);
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
        for (Chapters chapters:Chapters
                ) {

            earnedScore =+ Data.getDoubleInInt(chapters.earnedMarks);
            totalTestScore =+ Data.getDoubleInInt(chapters.totalTestedMarks);
        }

        return earnedScore+"/"+totalTestScore;
    }

    public int getNotes(){
        int notesCount = 0;
        for (Chapters chapters:Chapters
                ) {

            notesCount =+ Integer.parseInt(chapters.notesCount);
        }
            return notesCount;
    }

}