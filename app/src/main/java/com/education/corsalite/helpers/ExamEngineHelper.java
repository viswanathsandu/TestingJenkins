package com.education.corsalite.helpers;

import android.content.Context;
import android.text.TextUtils;

import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.enums.ExamType;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.listener.OnExamLoadCallback;
import com.education.corsalite.models.examengine.BaseTest;
import com.education.corsalite.models.examengine.PartTest;
import com.education.corsalite.models.examengine.TakeTest;
import com.education.corsalite.models.requestmodels.ExamTemplateChapter;
import com.education.corsalite.models.requestmodels.ExamTemplateConfig;
import com.education.corsalite.models.requestmodels.PostCustomExamTemplate;
import com.education.corsalite.models.requestmodels.PostQuestionPaperRequest;
import com.education.corsalite.models.responsemodels.Chapter;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Exam;
import com.education.corsalite.models.responsemodels.PartTestGridElement;
import com.education.corsalite.models.responsemodels.PostExamTemplate;
import com.education.corsalite.models.responsemodels.PostQuestionPaper;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.models.responsemodels.TestQuestionPaperResponse;
import com.education.corsalite.utils.ExamUtils;
import com.education.corsalite.utils.L;

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
        test.courseId = AbstractBaseActivity.getSelectedCourseId();
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
        test.courseId = AbstractBaseActivity.getSelectedCourseId();
        getStandardExamByCourse(callback);
    }

    private void getStandardExamByCourse(final OnExamLoadCallback callback) {
        if (test == null) {
            callback.OnFailure("Illegal test object found");
            return;
        }
        String entityId = LoginUserCache.getInstance().getEntityId();
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
                                    String chapterId = null;
                                    if(test != null && test.chapter != null) {
                                        chapterId = test.chapter.idCourseSubjectChapter;
                                    }
                                    postCustomExamTemplate(chapterId, null, takeTest.questionsCount, exams, callback);
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

        // TODO : Handling exam name for take test and part test
        if (examType == ExamType.TAKE_TEST) {
            postCustomExamTemplate.examName = "Chapter Practice Test - " + test.chapter.chapterName;
        } else if (examType == ExamType.PART_TEST) {
            postCustomExamTemplate.examName = "Part Test - " + test.subjectName;
        } else {
            postCustomExamTemplate.examName = examsList.get(0).examName;
        }

        postCustomExamTemplate.examTemplateConfig = new ArrayList<>();
        ExamTemplateConfig examTemplateConfig = new ExamTemplateConfig();
        examTemplateConfig.subjectId = test.subjectId;
        examTemplateConfig.questionCount = questionsCount == null ? "" : questionsCount;
        examTemplateConfig.examTemplateChapter = new ArrayList<>();
        if (partTestGridelements != null && !partTestGridelements.isEmpty()) {
            for (PartTestGridElement element : partTestGridelements) {
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
        ApiManager.getInstance(mActivity).postCustomExamTemplate(Gson.get().toJson(postCustomExamTemplate),
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
        postQuestionPaper.idEntity = LoginUserCache.getInstance().getEntityId();
        postQuestionPaper.idExamTemplate = examTemplateId;
        postQuestionPaper.idSubject = "";
        postQuestionPaper.idStudent = LoginUserCache.getInstance().getStudentId();

        ApiManager.getInstance(mActivity).postQuestionPaper(Gson.get().toJson(postQuestionPaper),
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

    private void getTestQuestionPaper(final String testQuestionPaperId, String testAnswerPaperId, final OnExamLoadCallback callback) {
        ApiManager.getInstance(mActivity).getTestPaperIndex(testQuestionPaperId, testAnswerPaperId, "N",
                new ApiCallback<TestPaperIndex>(mActivity) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                    }

                    @Override
                    public void success(TestPaperIndex testPaperIndex, Response response) {
                        super.success(testPaperIndex, response);
                        if (testPaperIndex != null) {
                            new ExamUtils(mActivity).saveTestPaperIndex(testQuestionPaperId, testPaperIndex);
                        }
                    }
                });
        ApiManager.getInstance(mActivity).getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, LoginUserCache.getInstance().getStudentId(),
                new ApiCallback<TestQuestionPaperResponse>(mActivity) {
                    @Override
                    public void success(TestQuestionPaperResponse questionPaperResponse, Response response) {
                        super.success(questionPaperResponse, response);
                        if (questionPaperResponse != null && questionPaperResponse.questions != null) {
                            test.testQuestionPaperResponse = questionPaperResponse;
                            callback.onSuccess(test);
                        } else {
                            callback.OnFailure("Sorry, couldn't fetch the test from server");
                        }
                    }
                });


    }

}
