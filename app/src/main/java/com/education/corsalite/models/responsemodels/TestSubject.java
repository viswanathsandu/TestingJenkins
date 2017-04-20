package com.education.corsalite.models.responsemodels;

import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 3/27/17.
 */

public class TestSubject extends BaseModel {
    public Double TimeTaken;

    public String subjectName;

    public String idCourseSubject;

    public Double TotalTestedMarks;

    @Ignore
    public List<TestChapter> SubjectChapters;

    public Integer TestCountTaken;

    public Double EarnedMarks;

    public String ScoreAmber;

    public String btncolor;

    public String ScoreRed;

}
