package com.education.corsalite.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.adapters.CustomLegendAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.models.responsemodels.CourseAnalysisPercentile;
import com.education.corsalite.utils.AnalyticsHelper;
import com.education.corsalite.utils.L;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;

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
    @Bind(R.id.rl_progress_report)RelativeLayout mLayout;
    @Bind(R.id.rl_progress_report_percentile)RelativeLayout mLayoutPercentile;
    @Bind(R.id.lc_progress_report)LineChart progressReportChart;
    @Bind(R.id.lc_progress_report_percentile)LineChart progressReportPercentileChart;
    @Bind(R.id.rv_legend)RecyclerView mLegend;
    @Bind(R.id.rv_legend_percentile)RecyclerView mLegendPercentile;
    int totalCount = 0;
    int failCount = 0;
    RecyclerView.Adapter customLegendAdapter;
    LinearLayoutManager mLayoutManager;
    LinearLayoutManager mLayoutManagerPercentile;
    private HashMap<String,List<CourseAnalysis>> courseDataMap = new HashMap<>();
    private HashMap<String,List<CourseAnalysisPercentile>> courseDataMapPercentile = new HashMap<>();

    private String ACCURACY = "accuracy";
    private String ACCURACY_PERCENTILE = "accuracy_percentile";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_progress_report, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManagerPercentile = new LinearLayoutManager(getActivity());
        mLegend.setLayoutManager(mLayoutManager);
        mLegendPercentile.setLayoutManager(mLayoutManagerPercentile);
        progressReportChart.setTag(ACCURACY);
        progressReportPercentileChart.setTag(ACCURACY_PERCENTILE);

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
        failCount = 0;
        data = null;
        dataPercentile = null;
        mLayout.setVisibility(View.GONE);
        mLayoutPercentile.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mFailText.setVisibility(View.GONE);
        drawGraph(course.courseId + "");
    }

    private synchronized void showFailMessage(){
        failCount++;
        if(failCount == 4){
            mFailText.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }
    private void drawGraph(String courseId) {
        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "Subject", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysis>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                        showFailMessage();

                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if (getActivity() == null) {
                            return;
                        }
                        totalCount = 0;
                        initializeGraph(progressReportChart);
                        buildData(courseAnalysisList);
                        for (Map.Entry<String,List<CourseAnalysis>> entry : courseDataMap.entrySet()) {
                            buildGraphData(entry.getValue(),entry.getKey(),totalCount++);
                        }
                        mLayout.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);


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

                        mLayout.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        initializeGraph(progressReportChart);
                        buildGraphData(courseAnalysisList, "Total", totalCount++);
                    }
                });

        //Build Percentile Graph

        ApiManager.getInstance(getActivity()).getCourseAnalysisAsPercentile(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "Subject", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysisPercentile>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                        showFailMessage();
                    }

                    @Override
                    public void success(List<CourseAnalysisPercentile> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if (getActivity() == null) {
                            return;
                        }
                        totalCount = 0;
                        initializeGraph(progressReportPercentileChart);
                        mLayoutPercentile.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        buildDataPercentile(courseAnalysisList);
                        for (Map.Entry<String,List<CourseAnalysisPercentile>> entry : courseDataMapPercentile.entrySet()) {
                            buildLineGraphDataPercentile(entry.getValue(), entry.getKey(), totalCount++);
                        }

                    }
                });

        ApiManager.getInstance(getActivity()).getCourseAnalysisAsPercentile(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "Test", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysisPercentile>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                        showFailMessage();
                    }

                    @Override
                    public void success(List<CourseAnalysisPercentile> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if (getActivity() == null) {
                            return;
                        }
                        mLayoutPercentile.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        initializeGraph(progressReportPercentileChart);
                        buildLineGraphDataPercentile(courseAnalysisList, "Test", totalCount++);
                    }
                });

    }

    private void initializeGraph(LineChart mChart){
        mChart.setDescription("");

        mChart.getLegend().setEnabled(false);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        mChart.setGridBackgroundColor(getActivity().getResources().getColor(R.color.bg_chart));
        mChart.setMarkerView(new CustomMarkerView(getActivity(),R.layout.marker_view_chart,mChart.getTag().toString()));

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

    private void buildDataPercentile(List<CourseAnalysisPercentile> courseAnalysisList) {
        for(CourseAnalysisPercentile course:courseAnalysisList){
            if(courseDataMapPercentile.get(course.subjectName) == null){
                ArrayList<CourseAnalysisPercentile> courseDataList = new ArrayList<>();
                courseDataList.add(course);
                courseDataMapPercentile.put(course.subjectName,courseDataList);
            }else{
                List<CourseAnalysisPercentile> courseDataList = courseDataMapPercentile.get(course.subjectName);
                courseDataList.add(course);
                courseDataMapPercentile.put(course.subjectName,courseDataList);
            }
        }

    }

    LineData data ;
    LineData dataPercentile;
    ArrayList<String> xValsPercentile;
    ArrayList<String> xVals;
    private void buildGraphData(List<CourseAnalysis> courseAnalysisList,String subject,int colorIndex){

        xVals = new ArrayList<>();
        List<LineDataSet> lineDataSets = new ArrayList<>();
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
        set1.setDrawValues(false);

        lineDataSets.add(set1);
        if(data == null) {
            data = new LineData(AnalyticsHelper.parseDate(xVals, true), lineDataSets);
        }else{
            for (LineDataSet dataset:lineDataSets) {
                data.addDataSet(dataset);
            }
        }
        progressReportChart.setData(data);
        progressReportChart.invalidate();
        Legend legend = progressReportChart.getLegend();
        customLegendAdapter = new CustomLegendAdapter(legend.getColors(),legend.getLabels(),getActivity().getLayoutInflater());
        mLegend.setAdapter(customLegendAdapter);
    }
    private void buildLineGraphDataPercentile(List<CourseAnalysisPercentile> courseAnalysisList,String subject,int colorIndex){
        List<LineDataSet> lineDataSetsPercentile = new ArrayList<>();
        xValsPercentile = new ArrayList<>();
        for (int i = 0; i < courseAnalysisList.size(); i++) {
            if(courseAnalysisList.get(i).startTime != null && !courseAnalysisList.get(i).startTime.isEmpty()) {
                xValsPercentile.add(courseAnalysisList.get(i).startTime);
            }
        }

        ArrayList<Entry> yVals = new ArrayList<>();

        for (int i = 0; i < courseAnalysisList.size(); i++) {
            if(courseAnalysisList.get(i).percentile != null && !courseAnalysisList.get(i).percentile.isEmpty()) {
                yVals.add(new Entry(Float.parseFloat(courseAnalysisList.get(i).percentile), i));
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
        set1.setDrawValues(false);

        lineDataSetsPercentile.add(set1);
        if(dataPercentile == null) {
            dataPercentile = new LineData(xValsPercentile, lineDataSetsPercentile);
        }else{
            for (LineDataSet dataset:lineDataSetsPercentile) {
                dataPercentile.addDataSet(dataset);
            }
        }
        progressReportPercentileChart.setData(dataPercentile);
        progressReportPercentileChart.invalidate();
        Legend legend = progressReportPercentileChart.getLegend();
        customLegendAdapter = new CustomLegendAdapter(legend.getColors(),legend.getLabels(),getActivity().getLayoutInflater());
        mLegendPercentile.setAdapter(customLegendAdapter);
    }


    class CustomMarkerView extends MarkerView {

        private TextView markerViewLine1;
        private TextView markerViewLine2;
        private String chartType;

        public CustomMarkerView (Context context, int layoutResource,String chartType) {
            super(context, layoutResource);
            markerViewLine1 = (TextView) findViewById(R.id.tv_line1);
            markerViewLine2 = (TextView) findViewById(R.id.tv_line2);
            this.chartType = chartType;
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            if(chartType.equalsIgnoreCase(ACCURACY)) {
                markerViewLine1.setVisibility(View.GONE);//TODO
                markerViewLine2.setText("Accuracy: "+e.getVal());
            }else if(chartType.equalsIgnoreCase(ACCURACY_PERCENTILE)){
                markerViewLine1.setVisibility(View.GONE);
                markerViewLine2.setText("Accuracy: "+e.getVal());
            }
        }

        @Override
        public int getXOffset(float xpos) {
            // this will center the marker-view horizontally
            return -(getWidth() / 2);
        }

        @Override
        public int getYOffset(float ypos) {
            // this will cause the marker-view to be above the selected value
            return -getHeight();
        }
    }

}
