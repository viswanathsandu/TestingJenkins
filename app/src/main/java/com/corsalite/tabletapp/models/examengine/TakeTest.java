package com.corsalite.tabletapp.models.examengine;

import com.corsalite.tabletapp.models.responsemodels.Exam;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 3/18/16.
 */
public class TakeTest extends BaseTest{

    public String examTemplateId;
    public String chapterId;
    public String topicId;
    @Ignore
    public List<Exam> exams;
}
