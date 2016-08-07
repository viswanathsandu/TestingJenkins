package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.enums.OfflineContentStatus;
import com.education.corsalite.event.OfflineActivityRefreshEvent;
import com.education.corsalite.event.RefreshOfflineUiEvent;
import com.education.corsalite.models.db.ExerciseOfflineModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.FileUtils;
import com.education.corsalite.utils.L;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Viswanath on 2/5/2016.
 */
public class ContentDownloadService extends IntentService {

    public static boolean isIntentServiceRunning = false;
    public static int downloandInProgress = 0;
    private List<OfflineContent> offlineContents = new ArrayList<>();
    private List<ExerciseOfflineModel> offlineExercises = new ArrayList<>();
    private com.thin.downloadmanager.DownloadManager downloadManager = new ThinDownloadManager();
    private SugarDbManager dbManager;

    public ContentDownloadService() {
        super("ContentDownloadService");
    }

    public static boolean isDownloadInProgress() {
        return downloandInProgress > 0;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        L.info("onHandleIntent called");
        isIntentServiceRunning = true;
        if(dbManager == null) {
            dbManager = SugarDbManager.get(getApplicationContext());
        }
        fetchOfflineContents();
    }

    private void fetchOfflineContents() {
        offlineContents = dbManager.getOfflineContents(null);
        offlineExercises = dbManager.getOfflineExerciseModels(null);
        startDownload();
    }

    private void startDownload() {
        List<OfflineContent> offlineContentsList = new ArrayList<>(offlineContents);
        for (OfflineContent content : offlineContentsList) {
            switch (content.status) {
                case STARTED:
                case IN_PROGRESS:
                    int status = downloadManager.query(content.downloadId);
                    if(status != DownloadManager.STATUS_SUCCESSFUL && status != DownloadManager.STATUS_RUNNING) {
                        downloadSync(content);
                    }
                    break;
                case WAITING:
                case FAILED:
                    downloadSync(content);
                    break;
            }
        }
    }

    private void downloadSync(OfflineContent content) {
        if (!TextUtils.isEmpty(content.contentId)) {
            downloandInProgress++;
            List<Content> contents = ApiManager.getInstance(this).getContent(content.contentId, "");
            if(contents != null && !contents.isEmpty()) {
                if(content.fileName.endsWith("html")) {
                    updateOfflineContent(content, OfflineContentStatus.COMPLETED, contents.get(0), 100);
                }
                saveFileToDisk(content, getHtmlText(contents.get(0)), contents.get(0));
            } else {
                updateOfflineContent(content, OfflineContentStatus.FAILED, null, 0);
            }
            downloandInProgress--;
        }
        downloadExercises();
    }

    private void saveFileToDisk(OfflineContent offlineContent, String htmlText, Content content) {
        FileUtils fileUtils = FileUtils.get(this);
        if (content.type.equalsIgnoreCase(Constants.VIDEO_FILE)) {
            downloadVideo(offlineContent, content, ApiClientService.getBaseUrl() + htmlText.replaceFirst("./", ""),
                    FileUtils.get(getApplicationContext()).getVideoDownloadPath(content.idContent));
        } else if (TextUtils.isEmpty(htmlText) || htmlText.endsWith(Constants.HTML_FILE)) {
            Toast.makeText(this, getString(R.string.file_exists), Toast.LENGTH_SHORT).show();
        } else {
            String htmlUrl = fileUtils.write(fileUtils.getContentFileName(content.idContent), htmlText, fileUtils.getContentFilePath());
            if (htmlUrl != null) {
                EventBus.getDefault().post(new OfflineActivityRefreshEvent(content.idContent));
            }
        }
    }

    private void downloadVideo(final OfflineContent offlineContent, final Content content, String videoUrl, String downloadLocation) {
        try {
            Uri downloadUri = Uri.parse(videoUrl);
            Uri destinationUri = Uri.parse(downloadLocation);
            if(TextUtils.isEmpty(videoUrl) || TextUtils.isEmpty(downloadLocation)
                    || downloadUri == null || destinationUri == null
                    || TextUtils.isEmpty(downloadUri.getPath())
                    || TextUtils.isEmpty(destinationUri.getPath())) {
                return;
            }
            downloandInProgress++;
            DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                    .addCustomHeader("cookie", ApiClientService.getSetCookie())
                    .setRetryPolicy(new DefaultRetryPolicy())
                    .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.NORMAL)
                    .setDownloadContext(getApplicationContext()) //Optional
                    .setStatusListener(new DownloadStatusListenerV1() {
                        int preProgress = 0;
                        @Override
                        public void onDownloadComplete(DownloadRequest downloadRequest) {
                            updateOfflineContent(offlineContent, OfflineContentStatus.COMPLETED, content, 100);
                            L.info("Downloader : completed");
                            downloandInProgress--;
                        }

                        @Override
                        public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                            updateOfflineContent(offlineContent, OfflineContentStatus.FAILED, content, 0);
                            L.info("Downloader : failed");
                            downloandInProgress--;
                        }

                        public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                            if (progress != 0 && progress % 10 == 0  && preProgress != progress) {
                                preProgress = progress;
                                updateOfflineContent(offlineContent, OfflineContentStatus.IN_PROGRESS, content, progress);
                                L.info("Downloader : In progress - " + progress + "Update DB");
                            }
                        }
                    });
            offlineContent.downloadId = downloadManager.add(downloadRequest);
            dbManager.save(offlineContent);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            updateOfflineContent(offlineContent, OfflineContentStatus.FAILED, content, 0);
        }
    }

    private void updateOfflineContent(OfflineContent offlineContent, OfflineContentStatus status, Content content, int progress) {
        if(content == null) return;
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
                offlineContent.progress = progress;
            }
            offlineContent.status = status;
            dbManager.save(offlineContent);
            EventBus.getDefault().post(new RefreshOfflineUiEvent());
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

    private void downloadExercises() {
        try {
            List<ExerciseOfflineModel> resultsToSave = new ArrayList<>();
            List<ExerciseOfflineModel> resultsToDelete = new ArrayList<>();
            List<ExerciseOfflineModel> offlineExerciseList = new ArrayList<>(offlineExercises);
            downloandInProgress += offlineExerciseList.size();
            for (ExerciseOfflineModel model : offlineExerciseList) {
                if(model.progress != 100) {
                    List<ExamModel> examModels = ApiManager.getInstance(this).getExercise(model.topicId, model.courseId, LoginUserCache.getInstance().getStudentId(), null);
                    if (examModels != null && !examModels.isEmpty()) {
                        model.progress = 100;
                        model.questions = examModels;
                        resultsToSave.add(model);
                    } else {
                        resultsToDelete.add(model);
                    }
                }
            }
            dbManager.saveOfflineExerciseTests(resultsToSave);
            dbManager.delete(resultsToDelete);
            downloandInProgress -= offlineExerciseList.size();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }
}
