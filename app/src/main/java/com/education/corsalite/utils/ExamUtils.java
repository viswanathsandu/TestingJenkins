package com.education.corsalite.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.ExerciseOfflineModel;
import com.education.corsalite.models.responsemodels.TestAnswerPaper;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
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

    public void saveExerciseQuestionPaper(String topicId, ExerciseOfflineModel response) {
        if (response == null) {
            return;
        }
        String testPaper = Gson.get().toJson(response);
        try {
            testPaper = Gzip.compress(testPaper);
            testPaper = Encrypter.encrypt(AppPref.get(mContext).getUserId(), testPaper);
            FileUtils fileUtils = FileUtils.get(mContext);
            String savedFileName = fileUtils.write(fileUtils.getExerciseFileName(),
                    testPaper, fileUtils.getTestsFolderPath(topicId));
            if (TextUtils.isEmpty(savedFileName)) {
                Toast.makeText(mContext, "Exercise download failed. Please try again", Toast.LENGTH_SHORT).show();
                L.info("Failed to Save exercise with id " + topicId);
            } else {
                L.info("Saved exercise with id " + topicId);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void saveTestQuestionPaper(String testQuestionPaperId, TestQuestionPaperResponse response) {
        if (response == null) {
            return;
        }
        String testPaper = Gson.get().toJson(response);
        try {
            testPaper = Gzip.compress(testPaper);
            testPaper = Encrypter.encrypt(AppPref.get(mContext).getUserId(), testPaper);
            FileUtils fileUtils = FileUtils.get(mContext);
            String savedFileName = fileUtils.write(fileUtils.getTestQuestionPaperFileName(),
                    testPaper, fileUtils.getTestsFolderPath(testQuestionPaperId));
            if (TextUtils.isEmpty(savedFileName)) {
                Toast.makeText(mContext, "Test download failed. Please try again", Toast.LENGTH_SHORT).show();
                L.info("Failed to Save test paper  with id " + testQuestionPaperId);
            } else {
                L.info("Saved test paper with id " + testQuestionPaperId);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void saveTestPaperIndex(String testQuestionPaperId, TestPaperIndex response) {
        if (response == null) {
            return;
        }
        String testPaper = Gson.get().toJson(response);
        try {
            testPaper = Gzip.compress(testPaper);
            testPaper = Encrypter.encrypt(AppPref.get(mContext).getUserId(), testPaper);
            FileUtils fileUtils = FileUtils.get(mContext);
            String savedFileName = fileUtils.write(fileUtils.getTestPaperIndexFileName(),
                    testPaper, fileUtils.getTestsFolderPath(testQuestionPaperId));
            if (TextUtils.isEmpty(savedFileName)) {
                Toast.makeText(mContext, "Test download failed. Please try again", Toast.LENGTH_SHORT).show();
                L.info("Failed to Save test paper index with id " + testQuestionPaperId);
            } else {
                L.info("Saved test paper index with id " + testQuestionPaperId);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void saveTestAnswerPaper(String testQuestionPaperId, TestAnswerPaper response) {
        if (response == null) {
            return;
        }
        String testPaper = Gson.get().toJson(response);
        try {
            testPaper = Gzip.compress(testPaper);
            testPaper = Encrypter.encrypt(AppPref.get(mContext).getUserId(), testPaper);
            FileUtils fileUtils = FileUtils.get(mContext);
            String savedFileName = fileUtils.write(fileUtils.getTestAnswerPaperFileName(), testPaper, fileUtils.getTestsFolderPath(testQuestionPaperId));
            if (TextUtils.isEmpty(savedFileName)) {
                L.info("Test Could not be saved on device. Please try again");
            } else {
                L.info("Saved test answer paper with id " + testQuestionPaperId);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public TestPaperIndex getTestPaperIndex(String testQuestionPaperId) {
        try {
            FileUtils fileUtils = FileUtils.get(mContext);
            String testPaper = fileUtils.readFromFile(fileUtils.getTestPaperIndexFileName(), fileUtils.getTestsFolderPath(testQuestionPaperId));
            if (!TextUtils.isEmpty(testPaper)) {
                try {
                    testPaper = Encrypter.decrypt(AppPref.get(mContext).getUserId(), testPaper);
                    testPaper = Gzip.decompress(testPaper);
                    TestPaperIndex response = Gson.get().fromJson(testPaper, TestPaperIndex.class);
                    return response;
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public ExerciseOfflineModel getExerciseModel(String topicId) {
        try {
            FileUtils fileUtils = FileUtils.get(mContext);
            L.info("Reading offline Test question paper from file");
            String testPaper = fileUtils.readFromFile(fileUtils.getExerciseFileName(), fileUtils.getTestsFolderPath(topicId));
            L.info("Completed reading offline Test question paper from file");
            if (!TextUtils.isEmpty(testPaper)) {
                try {
                    L.info("Decrypting offline Test question paper from file");
                    testPaper = Encrypter.decrypt(AppPref.get(mContext).getUserId(), testPaper);
                    L.info("Decrypted offline Test question paper from file");
                    L.info("Decompressing offline Test question paper from file");
                    testPaper = Gzip.decompress(testPaper);
                    L.info("Decompressed offline Test question paper from file");
                    ExerciseOfflineModel response = Gson.get().fromJson(testPaper, ExerciseOfflineModel.class);
                    return response;
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public TestQuestionPaperResponse getTestQuestionPaper(String testQuestionPaperId) {
        try {
            FileUtils fileUtils = FileUtils.get(mContext);
            L.info("Reading offline Test question paper from file");
            String testPaper = fileUtils.readFromFile(fileUtils.getTestQuestionPaperFileName(), fileUtils.getTestsFolderPath(testQuestionPaperId));
            L.info("Completed reading offline Test question paper from file");
            if (!TextUtils.isEmpty(testPaper)) {
                try {
                    L.info("Decrypting offline Test question paper from file");
                    testPaper = Encrypter.decrypt(AppPref.get(mContext).getUserId(), testPaper);
                    L.info("Decrypted offline Test question paper from file");
                    L.info("Decompressing offline Test question paper from file");
                    testPaper = Gzip.decompress(testPaper);
                    L.info("Decompressed offline Test question paper from file");
                    TestQuestionPaperResponse response = Gson.get().fromJson(testPaper, TestQuestionPaperResponse.class);
                    return response;
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public TestAnswerPaper getTestAnswerPaper(String testQuestionPaperId) {
        try {
            FileUtils fileUtils = FileUtils.get(mContext);
            String testPaper = fileUtils.readFromFile(fileUtils.getTestAnswerPaperFileName(), fileUtils.getTestsFolderPath(testQuestionPaperId));
            if (!TextUtils.isEmpty(testPaper)) {
                try {
                    testPaper = Encrypter.decrypt(AppPref.get(mContext).getUserId(), testPaper);
                    testPaper = Gzip.decompress(testPaper);
                    TestAnswerPaper response = Gson.get().fromJson(testPaper, TestAnswerPaper.class);
                    return response;
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public void deleteTestQuestionPaper(String testQuestionPaperId) {
        dbManager.deleteOfflineTestModel(testQuestionPaperId);
        FileUtils fileUtils = FileUtils.get(mContext);
        fileUtils.deleteFile(fileUtils.getTestsFolderPath(testQuestionPaperId));
        L.info("Deleted test question paper with id " + testQuestionPaperId);
    }
}
