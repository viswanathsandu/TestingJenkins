package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by Girish on 02/10/15.
 */
public class SubjectModel extends BaseModel {

    public String idSubject;
    public String subjectName;
    public String subjectStatus;
    public String scoreRed;
    public String scoreAmber;
    public String scoreLevelPassing;
    public HashMap<String, SortedSet<ChapterModel>> chapterMap;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubjectModel that = (SubjectModel) o;

        if (idSubject != null ? !idSubject.equals(that.idSubject) : that.idSubject != null)
            return false;
        if (subjectName != null ? !subjectName.equals(that.subjectName) : that.subjectName != null)
            return false;
        if (subjectStatus != null ? !subjectStatus.equals(that.subjectStatus) : that.subjectStatus != null)
            return false;
        if (scoreRed != null ? !scoreRed.equals(that.scoreRed) : that.scoreRed != null)
            return false;
        if (scoreAmber != null ? !scoreAmber.equals(that.scoreAmber) : that.scoreAmber != null)
            return false;
        if (scoreLevelPassing != null ? !scoreLevelPassing.equals(that.scoreLevelPassing) : that.scoreLevelPassing != null)
            return false;
        return !(chapterMap != null ? !chapterMap.equals(that.chapterMap) : that.chapterMap != null);

    }

    @Override
    public int hashCode() {
        int result = idSubject != null ? idSubject.hashCode() : 0;
        result = 31 * result + (subjectName != null ? subjectName.hashCode() : 0);
        result = 31 * result + (subjectStatus != null ? subjectStatus.hashCode() : 0);
        result = 31 * result + (scoreRed != null ? scoreRed.hashCode() : 0);
        result = 31 * result + (scoreAmber != null ? scoreAmber.hashCode() : 0);
        result = 31 * result + (scoreLevelPassing != null ? scoreLevelPassing.hashCode() : 0);
        result = 31 * result + (chapterMap != null ? chapterMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SubjectModel{" +
                "idSubject='" + idSubject + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", subjectStatus='" + subjectStatus + '\'' +
                ", scoreRed='" + scoreRed + '\'' +
                ", scoreAmber='" + scoreAmber + '\'' +
                ", scoreLevelPassing='" + scoreLevelPassing + '\'' +
                ", chapterMap=" + chapterMap +
                '}';
    }
}
