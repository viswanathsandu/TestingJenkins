package com.corsalite.tabletapp.helpers;

import android.content.Context;
import android.text.TextUtils;

import com.corsalite.tabletapp.activities.AbstractBaseActivity;
import com.corsalite.tabletapp.api.ApiCallback;
import com.corsalite.tabletapp.api.ApiManager;
import com.corsalite.tabletapp.cache.LoginUserCache;
import com.corsalite.tabletapp.enums.ExamType;
import com.corsalite.tabletapp.listener.OnExamLoadCallback;
import com.corsalite.tabletapp.models.examengine.BaseTest;
import com.corsalite.tabletapp.models.examengine.PartTest;
import com.corsalite.tabletapp.models.examengine.TakeTest;
import com.corsalite.tabletapp.models.requestmodels.ExamTemplateChapter;
import com.corsalite.tabletapp.models.requestmodels.ExamTemplateConfig;
import com.corsalite.tabletapp.models.requestmodels.PostCustomExamTemplate;
import com.corsalite.tabletapp.models.requestmodels.PostQuestionPaperRequest;
import com.corsalite.tabletapp.models.responsemodels.Chapter;
import com.corsalite.tabletapp.models.responsemodels.Exam;
import com.corsalite.tabletapp.models.responsemodels.ExamModel;
import com.corsalite.tabletapp.models.responsemodels.PartTestGridElement;
import com.corsalite.tabletapp.models.responsemodels.PostExamTemplate;
import com.corsalite.tabletapp.models.responsemodels.PostQuestionPaper;
import com.corsalite.tabletapp.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by vissu on 3/18/16.
 */
public class ExamEngineHelper {

    private Context mActivity;
    private ExamType examType;
    private BaseTest test;
    List<PartTestGridElement> partTestGridelements;

    public ExamEngineHelper(Context activity) {
        this.mActivity = activity;
    }

    public void loadTakeTest(Chapter chapter, String subjectName, String subjectId, String questionsCount, OnExamLoadCallback callback) {
        if (callback == null) {
            L.error("No callback registered");
            return;
        }
        examType = ExamType.TAKE_TEST;
        test = new TakeTest();
        test.subjectId = subjectId;
        test.chapter = chapter;
        test.subjectName = subjectName;
        test.courseId = AbstractBaseActivity.selectedCourse.courseId.toString();
        test.questionsCount = questionsCount;
        getStandardExamByCourse(callback);
    }

    public void loadPartTest(String subjectName, String subjectId, List<PartTestGridElement> elements, OnExamLoadCallback callback) {
        if (callback == null) {
            L.error("No callback registered");
            return;
        }
        this.partTestGridelements = elements;
        examType = ExamType.PART_TEST;
        test = new PartTest();
        test.subjectId = subjectId;
        test.subjectName = subjectName;
        test.courseId = AbstractBaseActivity.selectedCourse.courseId.toString();
        getStandardExamByCourse(callback);
    }

    private void getStandardExamByCourse(final OnExamLoadCallback callback) {
        if (test == null) {
            callback.OnFailure("Illegal test object found");
            return;
        }
        String entityId = LoginUserCache.getInstance().loginResponse.entitiyId;
        String selectedCourseId = test.courseId;
        ApiManager.getInstance(mActivity).getStandardExamsByCourse(selectedCourseId, entityId,
                new ApiCallback<List<Exam>>(mActivity) {
                    @Override
                    public void success(List<Exam> exams, Response response) {
                        super.success(exams, response);
                        if (exams != null && !exams.isEmpty()) {
                            switch (examType) {
                                case TAKE_TEST:
                                    TakeTest takeTest = ((TakeTest) ExamEngineHelper.this.test);
                                    takeTest.exams = exams;
                                    // Load params from respective method
                                    postCustomExamTemplate(null, null, takeTest.questionsCount, exams, callback);
                                    break;
                                case PART_TEST:
                                    PartTest partTest = ((PartTest) test);
                                    partTest.exams = exams;
                                    postCustomExamTemplate(null, null, "", exams, callback);
                                    break;
                            }
                        } else {
                            callback.OnFailure("Sorry, couldn't fetch the test from server");
                        }
                    }
                });
    }

    private void postCustomExamTemplate(String chapterId, String topicIds, String questionsCount, List<Exam> examsList, final OnExamLoadCallback callback) {
        final PostCustomExamTemplate postCustomExamTemplate = new PostCustomExamTemplate();
        postCustomExamTemplate.examId = examsList.get(0).examId;
        postCustomExamTemplate.examName = examsList.get(0).examName;
        postCustomExamTemplate.examTemplateConfig = new ArrayList<>();
        ExamTemplateConfig examTemplateConfig = new ExamTemplateConfig();
        examTemplateConfig.subjectId = test.subjectId;
        examTemplateConfig.questionCount = questionsCount == null ? "" : questionsCount;
        examTemplateConfig.examTemplateChapter = new ArrayList<>();
        if(partTestGridelements != null && !partTestGridelements.isEmpty()) {
            for(PartTestGridElement element : partTestGridelements) {
                examTemplateConfig.examTemplateChapter.add(new ExamTemplateChapter(element.idCourseSubjectChapter, element.recommendedQuestionCount));
            }
        } else {
            ExamTemplateChapter examTemplateChapter = new ExamTemplateChapter();
            examTemplateChapter.chapterID = chapterId;
            examTemplateChapter.topicIDs = topicIds;
            examTemplateChapter.questionCount = questionsCount;
            examTemplateConfig.examTemplateChapter.add(examTemplateChapter);
        }
        postCustomExamTemplate.examTemplateConfig.add(examTemplateConfig);
        ApiManager.getInstance(mActivity).postCustomExamTemplate(new Gson().toJson(postCustomExamTemplate),
                new ApiCallback<PostExamTemplate>(mActivity) {
                    @Override
                    public void success(PostExamTemplate postExamTemplate, Response response) {
                        super.success(postExamTemplate, response);
                        if (postExamTemplate != null && !TextUtils.isEmpty(postExamTemplate.idExamTemplate)) {
                            switch (examType) {
                                case TAKE_TEST:
                                    TakeTest takeTest = ((TakeTest) ExamEngineHelper.this.test);
                                    takeTest.examTemplateId = postExamTemplate.idExamTemplate;
                                    break;
                                case PART_TEST:
                                    PartTest partTest = ((PartTest) test);
                                    partTest.examTemplateId = postExamTemplate.idExamTemplate;
                                    break;
                            }
                            postQuestionPaper(postExamTemplate.idExamTemplate, callback);
                        } else {
                            callback.OnFailure("Sorry, couldn't fetch the test from server");
                        }
                    }
                });
    }

    private void postQuestionPaper(String examTemplateId, final OnExamLoadCallback callback) {
        PostQuestionPaperRequest postQuestionPaper = new PostQuestionPaperRequest();
        postQuestionPaper.idCollegeBatch = "";
        postQuestionPaper.idEntity = LoginUserCache.getInstance().loginResponse.entitiyId;
        postQuestionPaper.idExamTemplate = examTemplateId;
        postQuestionPaper.idSubject = "";
        postQuestionPaper.idStudent = LoginUserCache.getInstance().loginResponse.studentId;

        ApiManager.getInstance(mActivity).postQuestionPaper(new Gson().toJson(postQuestionPaper),
                new ApiCallback<PostQuestionPaper>(mActivity) {
                    @Override
                    public void success(PostQuestionPaper postQuestionPaper, Response response) {
                        super.success(postQuestionPaper, response);
                        if (postQuestionPaper != null && !TextUtils.isEmpty(postQuestionPaper.idTestQuestionPaper)) {
                            switch (examType) {
                                case TAKE_TEST:
                                    TakeTest takeTest = ((TakeTest) ExamEngineHelper.this.test);
                                    takeTest.testQuestionPaperId = postQuestionPaper.idTestQuestionPaper;
                                    break;
                                case PART_TEST:
                                    PartTest partTest = ((PartTest) test);
                                    partTest.testQuestionPaperId = postQuestionPaper.idTestQuestionPaper;
                                    break;
                            }
                            getTestQuestionPaper(postQuestionPaper.idTestQuestionPaper, null, callback);
                        } else {
                            callback.OnFailure("Sorry, couldn't fetch the test from server");
                        }
                    }
                });
    }

    private void getTestQuestionPaper(String testQuestionPaperId, String testAnswerPaperId, final OnExamLoadCallback callback) {
        ApiManager.getInstance(mActivity).getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId,
                new ApiCallback<List<ExamModel>>(mActivity) {
                    @Override
                    public void success(List<ExamModel> examModels, Response response) {
                        super.success(examModels, response);
                        if (examModels != null) {
                            test.questions = examModels;
                            callback.onSuccess(test);
                        } else {
                            callback.OnFailure("Sorry, couldn't fetch the test from server");
                        }
                    }
                });
    }

}
