package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.enums.OfflineContentStatus;
import com.education.corsalite.enums.Tests;
import com.education.corsalite.event.OfflineActivityRefreshEvent;
import com.education.corsalite.helpers.ExamEngineHelper;
import com.education.corsalite.listener.OnExamLoadCallback;
import com.education.corsalite.models.ExerciseOfflineModel;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.OfflineTestModel;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.examengine.BaseTest;
import com.education.corsalite.models.responsemodels.Chapter;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.PartTestGridElement;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.FileUtilities;
import com.education.corsalite.utils.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;

/**
 * Created by Viswanath on 2/5/2016.
 */
public class ContentDownloadService extends IntentService {

    private List<OfflineContent> offlineContents = new ArrayList<>();

    public ContentDownloadService() {
        super("ContentDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        L.info("onHandleIntent called");
        fetchOfflineContents();
    }

    private void fetchOfflineContents() {
        offlineContents = SugarDbManager.get(getApplicationContext()).getOfflineContents(null);
        startDownload();
    }

    private void startDownload() {
        for (OfflineContent content : offlineContents) {
            switch (content.status) {
                case WAITING:
                case FAILED:
                    downloadSync(content);
                    break;
            }
        }
    }

    private void downloadSync(final OfflineContent content) {
        if (!TextUtils.isEmpty(content.contentId)) {
            List<Content> contents = ApiManager.getInstance(this).getContent(content.contentId, "");
            if(contents != null && !contents.isEmpty()) {
                updateOfflineContent(content, OfflineContentStatus.COMPLETED, contents.get(0));
                saveFileToDisk(content, getHtmlText(contents.get(0)), contents.get(0));
            } else {
                updateOfflineContent(content, OfflineContentStatus.FAILED, null);
            }
        } else {
            // download exercise
        }
    }

    private void saveFileToDisk(OfflineContent offlineContent, final String htmlText, final Content content) {
        FileUtilities fileUtilities = new FileUtilities(this);
        final String folderStructure = offlineContent.courseName + File.separator + offlineContent.subjectName + File.separator + offlineContent.chapterName + File.separator +
                offlineContent.contentName + File.separator + offlineContent.topicName;
        L.info("File Path : " + folderStructure);
        if (content.type.equalsIgnoreCase(Constants.VIDEO_FILE)) {
            downloadVideoFile(content.name+"."+content.type,
                            ApiClientService.getBaseUrl() + htmlText.replaceFirst("./", ""),
                            folderStructure);
        } else if (TextUtils.isEmpty(htmlText) || htmlText.endsWith(Constants.HTML_FILE)) {
            Toast.makeText(this, getString(R.string.file_exists), Toast.LENGTH_SHORT).show();
        } else {
            String htmlUrl = fileUtilities.write(content.name + "." + Constants.HTML_FILE, htmlText, folderStructure);
            if (htmlUrl != null) {
                EventBus.getDefault().post(new OfflineActivityRefreshEvent(content.idContent));
                Toast.makeText(this, getString(R.string.file_saved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.file_save_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadVideoFile(String fileName,String download_file_path,String folderStructure){
        try {
            URL url = new URL(download_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            File SDCardRoot = Environment.getExternalStorageDirectory();
            File outDir = new File(SDCardRoot.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER + File.separator +folderStructure);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            File file = new File(outDir.getAbsolutePath() + File.separator + Constants.VIDEO_FOLDER);
            if(!file.exists())
                file.mkdir();
            File newFile = new File(file,fileName);
            newFile.createNewFile();
            FileOutputStream fileOutput = new FileOutputStream(newFile);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength ;
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();

        } catch (final MalformedURLException e) {
            L.error(e.getMessage(), e);
        } catch (final IOException e) {
            L.error(e.getMessage(), e);
        } catch (final Exception e) {
            L.error(e.getMessage(), e);
        }

    }

    private void updateOfflineContent(OfflineContent offlineContent, OfflineContentStatus status, Content content) {
        if(offlineContent != null && content != null) {
            if (!TextUtils.isEmpty(content.idContent) && !TextUtils.isEmpty(offlineContent.contentId) && offlineContent.contentId.equalsIgnoreCase(content.idContent)) {
                String fileName = "";
                if (TextUtils.isEmpty(content.type)) {
                    fileName = content.name + ".html";
                } else if (content.type.equalsIgnoreCase("html")) {
                    fileName = content.name + "." + content.type;
                } else if (content.type.equalsIgnoreCase("mpg")) {
                    fileName = content.name.replace("./", ApiClientService.getBaseUrl()) + "." + content.type;
                }
                if (content != null) {
                    offlineContent.fileName = fileName;
                    offlineContent.contentName = content.name;
                    offlineContent.contentId = content.idContent;
                }
                offlineContent.status = status;
                SugarDbManager.get(getApplicationContext()).saveOfflineContent(offlineContent);
            }
        }
    }

    private void updateOfflineContent(final OfflineContentStatus status, final Content content) {
        List<OfflineContent> offlineContents = SugarDbManager.get(this).getOfflineContents(null);
        for (OfflineContent offlineContent : offlineContents) {
            if (!TextUtils.isEmpty(content.idContent) && !TextUtils.isEmpty(offlineContent.contentId) && offlineContent.contentId.equalsIgnoreCase(content.idContent)) {
                String fileName = "";
                if (TextUtils.isEmpty(content.type)) {
                    fileName = content.name + ".html";
                } else if (content.type.equalsIgnoreCase("html")) {
                    fileName = content.name + "." + content.type;
                } else if (content.type.equalsIgnoreCase("mpg")) {
                    fileName = content.name.replace("./", ApiClientService.getBaseUrl()) + "." + content.type;
                }
                if (content != null) {
                    offlineContent.fileName = fileName;
                    offlineContent.contentName = content.name;
                    offlineContent.contentId = content.idContent;
                }
                offlineContent.status = status;
                List<OfflineContent> offlineUpdatedContents = new ArrayList<OfflineContent>();
                offlineUpdatedContents.add(offlineContent);
                SugarDbManager.get(ContentDownloadService.this).saveOfflineContents(offlineUpdatedContents);
            }
        }
    }

    private String getHtmlText(Content content) {
        String text = content.type.equalsIgnoreCase(Constants.VIDEO_FILE) ?
                content.url :
                "<script type='text/javascript'>" +
                        "function copy() {" +
                        "    var t = (document.all) ? document.selection.createRange().text : document.getSelection();" +
                        "    return t;" +
                        "}" +
                        "</script>" + content.contentHtml;
        return text;
    }

    private void downloadExercises(List<ExerciseOfflineModel> models) {
        for (final ExerciseOfflineModel model : models) {
            ApiManager.getInstance(this).getExercise(model.topicId, model.courseId, LoginUserCache.getInstance().loginResponse.studentId, null,
                    new ApiCallback<List<ExamModel>>(this) {
                        @Override
                        public void failure(CorsaliteError error) {
                            super.failure(error);
                            L.error("Failed to save exercise for topic model" + model.topicId);
                        }

                        @Override
                        public void success(List<ExamModel> examModels, Response response) {
                            super.success(examModels, response);
                            if (examModels != null && !examModels.isEmpty()) {
                                model.questions = examModels;
                                SugarDbManager.get(getApplicationContext()).saveOfflineExerciseTest(model);
                            }
                        }
                    });
        }
    }

    private void getTestQuestionPaper(final String testQuestionPaperId, final String testAnswerPaperId, final MockTest mockTest, final TestPaperIndex testPAperIndecies, final ScheduledTestList.ScheduledTestsArray scheduledTestsArray) {
        try {
            ApiManager.getInstance(this).getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId,
                    new ApiCallback<List<ExamModel>>(this) {
                        @Override
                        public void success(List<ExamModel> examModels, Response response) {
                            super.success(examModels, response);
                            OfflineTestModel model = new OfflineTestModel();
                            model.examModels = examModels;
                            if (mockTest != null) {
                                model.testType = Tests.MOCK;
                                model.mockTest = mockTest;
                            } else {
                                model.testType = Tests.SCHEDULED;
                                model.scheduledTest = scheduledTestsArray;
                            }
                            model.baseTest = new BaseTest();
                            model.baseTest.courseId = AbstractBaseActivity.selectedCourse.courseId + "";
                            model.testPaperIndecies = testPAperIndecies;
                            model.testQuestionPaperId = testQuestionPaperId;
                            model.testAnswerPaperId = testAnswerPaperId;
                            model.dateTime = System.currentTimeMillis();
                            SugarDbManager.get(getApplicationContext()).saveOfflineTest(model);
                        }
                    });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void loadTakeTest(Chapter chapter, String subjectName, String questionsCount, String subjectId) {
        ExamEngineHelper helper = new ExamEngineHelper(this);
        helper.loadTakeTest(chapter, subjectName, subjectId, questionsCount, new OnExamLoadCallback() {
            @Override
            public void onSuccess(BaseTest test) {
                OfflineTestModel model = new OfflineTestModel();
                model.testType = Tests.CHAPTER;
                model.baseTest = test;
                model.dateTime = System.currentTimeMillis();
                SugarDbManager.get(ContentDownloadService.this).saveOfflineTest(model);
            }

            @Override
            public void OnFailure(String message) {
            }
        });
    }

    private void loadPartTest(final String subjectName, final String subjectId, List<PartTestGridElement> elements) {
        ExamEngineHelper helper = new ExamEngineHelper(this);
        helper.loadPartTest(subjectName, subjectId, elements, new OnExamLoadCallback() {
            @Override
            public void onSuccess(BaseTest test) {
                OfflineTestModel model = new OfflineTestModel();
                model.testType = Tests.PART;
                model.baseTest = test;
                if (test != null) {
                    model.baseTest.subjectId = subjectId;
                    model.baseTest.subjectName = subjectName;
                }
                model.dateTime = System.currentTimeMillis();
                SugarDbManager.get(ContentDownloadService.this).saveOfflineTest(model);
                L.info("Test Saved : " + model.getClass());
            }

            @Override
            public void OnFailure(String message) {
            }
        });
    }
}
