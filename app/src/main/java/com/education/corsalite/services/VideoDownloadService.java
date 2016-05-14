package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Madhuri on 14-02-2016.
 */
public class VideoDownloadService extends IntentService {

    public VideoDownloadService() {
        super("VideoDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String download_file_path = intent.getStringExtra("download_file_path");
        String fileName = intent.getStringExtra("fileName");
        String folderStructure = intent.getStringExtra("folderStructure");
        L.debug("Downloading video");
        L.debug("file name : "+fileName);
        L.debug("from : "+download_file_path);
        L.debug("to : "+folderStructure);
        downloadFile(fileName,download_file_path,folderStructure);
    }

    private void downloadFile(String fileName,String download_file_path,String folderStructure){
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
}
