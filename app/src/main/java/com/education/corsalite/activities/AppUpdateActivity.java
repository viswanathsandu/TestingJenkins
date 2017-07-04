package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.FileUtils;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppUpdateActivity extends AbstractBaseActivity {

    @Bind(R.id.download_status_txt) TextView downloadStatusTxt;
    @Bind(R.id.download_progress) ProgressBar downloadProgress;

    private com.thin.downloadmanager.DownloadManager downloadManager = new ThinDownloadManager();
    private int downloadId = 0;
    private String apkUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_app_update, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this, myView);
        apkUrl = getIntent().getExtras().getString("apk_url");
        if(!TextUtils.isEmpty(apkUrl)) {
            downloadApk();
        } else {
            showToast("Something went wrong. Please launch the app again");
            finish();
        }
    }

    @Override
    public void checkForceUpgrade() {
        // do nothing
    }

    private void downloadApk(){
        Uri downloadUri = Uri.parse(apkUrl);
        Uri destinationUri = Uri.parse(FileUtils.get(this).getApkFolderPath());
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .addCustomHeader("cookie", ApiClientService.getSetCookie())
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.IMMEDIATE)
                .setDownloadContext(getApplicationContext()) //Optional
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        showToast("Download Completed");
                        updateStatus("Download Completed");
                        installApk(downloadRequest.getDestinationURI().getPath());
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        showToast("Download Failed. Please close the app and relaunch it again");
                        showProgress(0);
                        updateStatus("Download failed. Please relaunch the app to try again");
                    }

                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        showProgress(progress);
                    }
                });
        downloadId = downloadManager.add(downloadRequest);
    }

    private void showProgress(int progress) {
        downloadProgress.setProgress(progress);
    }

    private void updateStatus(String message) {
        downloadStatusTxt.setText(message);
    }

    private void installApk(String filePath){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
