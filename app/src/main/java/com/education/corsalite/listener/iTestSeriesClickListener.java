package com.education.corsalite.listener;

import com.education.corsalite.models.responsemodels.TestChapter;
import com.education.corsalite.models.responsemodels.TestSeriesMockData;

/**
 * Created by vissu on 3/29/17.
 */

public interface iTestSeriesClickListener {

    void onTakeTest(TestChapter chapter);

    void onMockTest(TestChapter chapter);

    void onMockTest(TestSeriesMockData mockTest);

    void onRecommendedReading(TestChapter chapter);
}
