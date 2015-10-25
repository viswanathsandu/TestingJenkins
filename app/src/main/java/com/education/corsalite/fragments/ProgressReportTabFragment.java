package com.education.corsalite.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.utils.AnalyticsHelper;
import com.education.corsalite.utils.L;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.client.Response;

/**
 * Created by Aastha on 27/09/15.
 */
public class ProgressReportTabFragment extends Fragment {

    @Bind(R.id.progress_bar_tab)ProgressBar mProgressBar;
    @Bind(R.id.tv_failure_text)TextView mFailText;
    @Bind(R.id.ll_progress_report)LinearLayout mLinearLayout;
    @Bind(R.id.lc_progress_report)LineChart progressReportChart;
    @Bind(R.id.lc_progress_report_percentile)LineChart progressReportPercentileChart;
    int totalCount =0;

    private HashMap<String,List<CourseAnalysis>> courseDataMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_progress_report, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(AbstractBaseActivity.selectedCourse != null) {
            onEvent(AbstractBaseActivity.selectedCourse);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Course course) {
        drawGraph(course.courseId + "");
    }

    private void drawGraph(String courseId) {
        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "Subject", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysis>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                        mProgressBar.setVisibility(View.GONE);
                        mFailText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if (getActivity() == null) {
                            return;
                        }
                        mLinearLayout.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        buildData(courseAnalysisList);
                        for (Map.Entry<String,List<CourseAnalysis>> entry : courseDataMap.entrySet()) {
                            buildGraphData(entry.getValue(),entry.getKey(),totalCount++);
                        }

                    }
                });

        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "None", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysis>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                        mProgressBar.setVisibility(View.GONE);
                        mFailText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if (getActivity() == null) {
                            return;
                        }

                        mLinearLayout.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        initializeGraph(progressReportChart);
                        buildGraphData(courseAnalysisList, "Total", totalCount++);
                        setData(progressReportChart);

                    }
                });

        //Build Percentile Graph


    }

    private void initializeGraph(LineChart mChart){
        mChart.getLegend().setEnabled(false);
        mChart.setDescription("");
    }

    private void buildData(List<CourseAnalysis> courseAnalysisList) {
        for(CourseAnalysis course:courseAnalysisList){
            if(courseDataMap.get(course.subjectName) == null){
                ArrayList<CourseAnalysis> courseDataList = new ArrayList<>();
                courseDataList.add(course);
                courseDataMap.put(course.subjectName,courseDataList);
            }else{
                List<CourseAnalysis> courseDataList = courseDataMap.get(course.subjectName);
                courseDataList.add(course);
                courseDataMap.put(course.subjectName,courseDataList);
            }
        }

    }

    List<LineDataSet> lineDataSets = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<>();

    private void buildGraphData(List<CourseAnalysis> courseAnalysisList,String subject,int colorIndex){
        for (int i = 0; i < courseAnalysisList.size(); i++) {
            if(courseAnalysisList.get(i).date != null && !courseAnalysisList.get(i).date.isEmpty()) {
                xVals.add(courseAnalysisList.get(i).date);
            }
        }

        ArrayList<Entry> yVals = new ArrayList<>();

        for (int i = 0; i < courseAnalysisList.size(); i++) {
            if(courseAnalysisList.get(i).accuracy != null && !courseAnalysisList.get(i).accuracy.isEmpty()) {
                yVals.add(new Entry(Float.parseFloat(courseAnalysisList.get(i).accuracy), i));
            }
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, subject);
        set1.setColor(AnalyticsHelper.getColors().get(colorIndex));
        set1.setCircleColor(AnalyticsHelper.getColors().get(colorIndex));
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(AnalyticsHelper.getColors().get(colorIndex));

        lineDataSets.add(set1);
    }




    private  void setData(LineChart mChart){
        LineData data = new LineData(AnalyticsHelper.parseDate(xVals,true), lineDataSets);
        mChart.setData(data);
    }
}
