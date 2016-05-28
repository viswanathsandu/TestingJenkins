package com.corsalite.tabletapp.listener;

import com.corsalite.tabletapp.models.examengine.BaseTest;

/**
 * Created by vissu on 18/03/2016
 */

public interface OnExamLoadCallback {
    void onSuccess(BaseTest test);
    void OnFailure(String message);
}