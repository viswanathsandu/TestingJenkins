package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.enums.OfflineContentStatus;
import com.education.corsalite.event.OfflineActivityRefreshEvent;
import com.education.corsalite.models.db.ExerciseOfflineModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.FileUtilities;
import com.education.corsalite.utils.L;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Viswanath on 2/5/2016.
 */
public class ContentDownloadService extends IntentService {

    public static boolean isIntentServiceRunning = false;
    private List<OfflineContent> offlineContents = new ArrayList<>();
    private List<ExerciseOfflineModel> offlineExercises = new ArrayList<>();
    private DownloadManager downloadManager = new ThinDownloadManager();
    private SugarDbManager dbManager;

    public ContentDownloadService() {
        super("ContentDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        L.info("onHandleIntent called");
        isIntentServiceRunning = true;
        if(dbManager == null) {
            dbManager = SugarDbManager.get(getApplicationContext());
        }
        fetchOfflineContents();
        // TODO : remove it after implementing the content download
//        downloadContent("https://staging.corsalite.com/v1/webservices/ContentIndex?idStudent=1599&idCourse=10",
//                "/storage/emulated/0/Corsalite/test_content_download.html");
    }

    private void fetchOfflineContents() {
        L.info("SUGAR : Fetching offline contents");
        offlineContents = dbManager.getOfflineContents(null);
        offlineExercises = dbManager.getOfflineExerciseModels(null);
        startDownload();
    }

    private void startDownload() {
        for (OfflineContent content : offlineContents) {
            switch (content.status) {
                case STARTED:
                case IN_PROGRESS:
                    if(downloadManager.query(content.downloadId) != DownloadManager.STATUS_SUCCESSFUL
                            && downloadManager.query(content.downloadId) != DownloadManager.STATUS_RUNNING) {
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

    private void downloadSync(final OfflineContent content) {
        if (!TextUtils.isEmpty(content.contentId)) {
            List<Content> contents = ApiManager.getInstance(this).getContent(content.contentId, "");
            if(contents != null && !contents.isEmpty()) {
                if(content.fileName.endsWith("html")) {
                    updateOfflineContent(OfflineContentStatus.COMPLETED, contents.get(0), 100);
                }
                saveFileToDisk(content, getHtmlText(contents.get(0)), contents.get(0));
            } else {
                updateOfflineContent(OfflineContentStatus.FAILED, null, 0);
            }
        }
        downloadExercises();
    }

    private void saveFileToDisk(OfflineContent offlineContent, final String htmlText, final Content content) {
        FileUtilities fileUtilities = new FileUtilities(this);
        final String folderStructure = offlineContent.courseName + File.separator
                                        + offlineContent.subjectName + File.separator
                                        + offlineContent.chapterName + File.separator
                                        + offlineContent.topicName;
        L.info("File Path : " + folderStructure);
        if (content.type.equalsIgnoreCase(Constants.VIDEO_FILE)) {
            String downloadPath = getDestinationPath(content, folderStructure);
            downloadVideo(offlineContent, content, ApiClientService.getBaseUrl() + htmlText.replaceFirst("./", ""), downloadPath);
        } else if (TextUtils.isEmpty(htmlText) || htmlText.endsWith(Constants.HTML_FILE)) {
            Toast.makeText(this, getString(R.string.file_exists), Toast.LENGTH_SHORT).show();
        } else {
            String htmlUrl = fileUtilities.write(content.name + "." + Constants.HTML_FILE, htmlText, folderStructure);
            if (htmlUrl != null) {
                EventBus.getDefault().post(new OfflineActivityRefreshEvent(content.idContent));
//                Toast.makeText(this, getString(R.string.file_saved), Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, getString(R.string.file_save_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDestinationPath(Content content, String folderStructure) {
        String destinationPath = "";
        try {
            File SDCardRoot = Environment.getExternalStorageDirectory();
            File outDir = new File(SDCardRoot.getAbsolutePath() + File.separator + Constants.PARENT_FOLDER + File.separator + folderStructure);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            File file = new File(outDir.getAbsolutePath() + File.separator + Constants.VIDEO_FOLDER);
            if (!file.exists())
                file.mkdir();
            File newFile = new File(file, content.name + "." + content.type);
            newFile.createNewFile();
            destinationPath = newFile.getAbsolutePath();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        L.info("Video Downloaing Url : "+destinationPath);
        return destinationPath;
    }

    // TODO : need to implement it further
    private void downloadContent(String url, String downloadLocation) {
        Uri downloadUri = Uri.parse(url);
        Uri destinationUri = Uri.parse(downloadLocation);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .addCustomHeader("cookie", ApiClientService.getSetCookie())
                .addCustomHeader("Accept-Encoding", "gzip")
                .addCustomHeader("Accept", "application/json")
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadContext(getApplicationContext())//Optional
                .setStatusListener(new DownloadStatusListenerV1() {
                    int preProgress = -1;
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        L.info("Downloader : completed");
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        L.info("Downloader : failed with error code : "+errorCode+"\t message : "+errorMessage);
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        if(progress - preProgress >= 10 || progress == 100) {
                            L.info("Downloader : In progress - "+progress);
                            preProgress = progress;
                        }
                    }
                });
        downloadManager.add(downloadRequest);
    }

    private void downloadVideo(OfflineContent offlineContent, final Content content, String videoUrl, String downloadLocation) {
        try {
            Uri downloadUri = Uri.parse(videoUrl);
            Uri destinationUri = Uri.parse(downloadLocation);
            DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                    .addCustomHeader("cookie", ApiClientService.getSetCookie())
                    .setRetryPolicy(new DefaultRetryPolicy())
                    .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                    .setDownloadContext(getApplicationContext()) //Optional
                    .setStatusListener(new DownloadStatusListenerV1() {
                        int preProgress = 0;
                        @Override
                        public void onDownloadComplete(DownloadRequest downloadRequest) {
                            updateOfflineContent(OfflineContentStatus.COMPLETED, content, 100);
                            L.info("Downloader : completed");
                        }

                        @Override
                        public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                            updateOfflineContent(OfflineContentStatus.FAILED, content, 0);
                            L.info("Downloader : failed");
                        }

                        public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                            if (progress != 0 && progress % 50 == 0) {
                                // TODO : this needs to be handled properly
                                updateOfflineContent(OfflineContentStatus.IN_PROGRESS, content, progress);
                                L.info("Downloader : In progress - " + progress + "Update DB");
                            } else if(progress % 10 == 0 && preProgress != progress){
                                preProgress = progress;
                                L.info("Downloader : In progress - " + progress);
                            }
                        }
                    });
            updateDownloadIdForOfflinecontent(offlineContent, downloadManager.add(downloadRequest));
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            updateOfflineContent(OfflineContentStatus.FAILED, content, 0);
        }
    }

    private void updateDownloadIdForOfflinecontent(OfflineContent content, int downloadId) {
        List<OfflineContent> offlineContents = dbManager.getOfflineContents(null);
        List<OfflineContent> results = new ArrayList<>();
        for (OfflineContent offlineContent : offlineContents) {
            if(offlineContent.getId() == content.getId()) {
                offlineContent.downloadId = downloadId;
                results.add(offlineContent);
            }
        }
        if(results != null && !results.isEmpty()) {
            dbManager.saveOfflineContents(offlineContents);
        }
    }

    private void updateOfflineContent(final OfflineContentStatus status, final Content content, final int progress) {
        if(content == null) return;
        List<OfflineContent> results = new ArrayList<>();
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
                    offlineContent.progress = progress;
                }
                offlineContent.status = status;
                results.add(offlineContent);
            }
        }
        if(results != null && !results.isEmpty()) {
            dbManager.saveOfflineContents(results);
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
            for (final ExerciseOfflineModel model : offlineExercises) {
                List<ExamModel> examModels = ApiManager.getInstance(this).getExercise(model.topicId, model.courseId, LoginUserCache.getInstance().loginResponse.studentId, null);
                if (examModels != null && !examModels.isEmpty()) {
                    model.progress = 100;
                    model.questions = examModels;
                    resultsToSave.add(model);
                } else {
                    resultsToDelete.add(model);
                }
            }
            dbManager.saveOfflineExerciseTests(resultsToSave);

        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }
}
