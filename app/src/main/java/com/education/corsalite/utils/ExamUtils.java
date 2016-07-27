package com.education.corsalite.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.models.responsemodels.TestQuestionPaperResponse;
import com.education.corsalite.security.Encrypter;


/**
 * Created by vissu on 7/28/16.
 */

public class ExamUtils {

    private Context mContext;
    private SugarDbManager dbManager;

    public ExamUtils(Context context) {
        this.mContext = context;
        dbManager = SugarDbManager.get(mContext);
    }

    public void saveTestQuestionPaper(String testQuestionPaperId, TestQuestionPaperResponse response) {
        String fileName = testQuestionPaperId + "." + Constants.TEST_FILE;
        String testPaper = Gson.get().toJson(response);
        try {
            testPaper = Gzip.compress(testPaper);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        try {
            testPaper = Encrypter.encrypt(LoginUserCache.getInstance().getUserId(), testPaper);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        String savedFileName = new FileUtils(mContext).write(fileName, testPaper, null);
        if(TextUtils.isEmpty(savedFileName)) {
            Toast.makeText(mContext, "Test download failed. Please try again", Toast.LENGTH_SHORT).show();
        } else {
            L.info("Saved test question paper with id " + testQuestionPaperId);
        }
    }

    public TestQuestionPaperResponse getTestQuestionPaper(String testQuestionPaperId) {
        try {

            String fileName = testQuestionPaperId + "." + Constants.TEST_FILE;
            String testPaper = new FileUtils(mContext).readFromFile(fileName, null);
            try {
                testPaper = Encrypter.decrypt(LoginUserCache.getInstance().getUserId(), testPaper);
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
            try {
                testPaper = Gzip.decompress(testPaper);
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
            TestQuestionPaperResponse response = Gson.get().fromJson(testPaper, TestQuestionPaperResponse.class);
            return response;
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return null;
        }
    }

    public void deleteTestQuestionPaper(String testQuestionPaperId) {
        dbManager.deleteOfflineTestModel(testQuestionPaperId);
        String fileName = testQuestionPaperId + "." + Constants.TEST_FILE;
        new FileUtils(mContext).deleteFile(fileName);
        L.info("Deleted test question paper with id " + testQuestionPaperId);
    }
}
