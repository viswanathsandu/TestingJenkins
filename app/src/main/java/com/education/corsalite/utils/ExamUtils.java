package com.education.corsalite.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.models.responsemodels.TestQuestionPaperResponse;
import com.google.gson.Gson;

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
        String jsonQuestionPaper = new Gson().toJson(response);
        String savedFileName = new FileUtils(mContext).write(fileName, jsonQuestionPaper, null);
        if(TextUtils.isEmpty(savedFileName)) {
            Toast.makeText(mContext, "Test download failed. Please try again", Toast.LENGTH_SHORT).show();
        } else {
            L.info("Saved test question paper with id " + testQuestionPaperId);
        }
    }

    public void deleteTestQuestionPaper(String testQuestionPaperId) {
        dbManager.deleteOfflineTestModel(testQuestionPaperId);
        String fileName = testQuestionPaperId + "." + Constants.TEST_FILE;
        new FileUtils(mContext).deleteFile(fileName);
        L.info("Deleted test question paper with id " + testQuestionPaperId);
    }
}
