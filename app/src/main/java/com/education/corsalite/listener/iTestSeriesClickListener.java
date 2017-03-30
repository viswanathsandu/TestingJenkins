package com.education.corsalite.listener;

import com.education.corsalite.models.responsemodels.TestChapter;

/**
 * Created by vissu on 3/29/17.
 */

public interface iTestSeriesClickListener {

    void onTakeTest(TestChapter chapter);
    void onMockTest(TestChapter chapter);
}
