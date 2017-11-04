package com.education.corsalite.models.examengine;

import com.education.corsalite.models.responsemodels.Exam;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 3/18/16.
 */
public class PartTest extends BaseTest {

    public String examTemplateId;
    public String chapterId;
    public String topicId;
    @Ignore
    public List<Exam> exams;
}
