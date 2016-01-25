package com.education.corsalite.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.education.corsalite.utils.AnalyticsHelper;
import com.education.corsalite.utils.L;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.ArrayList;
import java.util.Arrays;
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
public class TimeManagementTabFragment extends Fragment {

    @Bind(R.id.pc_subject)PieChart graphBySubject;
    @Bind(R.id.ll_subject)LinearLayout mLinearLayoutSubject;
    @Bind(R.id.ll_chapter)LinearLayout mLinearLayoutChapter;
    @Bind(R.id.rv_legend)RecyclerView mRecyclerView;
    @Bind(R.id.tv_failure_text)TextView mFailText;
    @Bind(R.id.progress_bar_tab)ProgressBar progressBar;

    int failCount =0;
    private HashMap<String,List<CourseAnalysis>> courseDataMap = new HashMap<>();
    final String SUBJECT = "Subject";
    final String CHAPTER = "Chapter";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time_management_tab,container,false);
        ButterKnife.bind(this, v);
        EventBus.getDefault().register(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
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
        courseDataMap.clear();
        progressBar.setVisibility(View.VISIBLE);
        mFailText.setVisibility(View.GONE);
        mLinearLayoutSubject.setVisibility(View.GONE);
        mLinearLayoutChapter.removeAllViews();
        drawGraph(course.courseId + "");
    }

    private void drawGraph(String courseId) {
        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "Subject", "None", "30", "true",
                new ApiCallback<List<CourseAnalysis>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if(getActivity() == null) {
                            return;
                        }
                        L.error(error.message);
                        showFailMessage();
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if(getActivity() == null) {
                            return;
                        }
                        initializeGraph(graphBySubject);
                        if(buildGraphData(courseAnalysisList, SUBJECT, graphBySubject)){
                            //Custom Legend
                            Legend mLegend = graphBySubject.getLegend();
                            CustomLegendAdapter customLegendAdapter = new CustomLegendAdapter(Arrays.copyOf(mLegend.getColors(),mLegend.getColors().length-1),
                                    Arrays.copyOf(mLegend.getLabels(),mLegend.getLabels().length -1),getActivity().getLayoutInflater());
                            mRecyclerView.setAdapter(customLegendAdapter);
                            progressBar.setVisibility(View.GONE);
                            mLinearLayoutSubject.setVisibility(View.VISIBLE);

                        }else{
                            showFailMessage();
                        }

                    }
                });


        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "Chapter", "None", "30", "true",
                new ApiCallback<List<CourseAnalysis>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if(getActivity() == null) {
                            return;
                        }
                        L.error(error.message);
                        showFailMessage();
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if(getActivity() == null) {
                            return;
                        }

                        buildChapterData(courseAnalysisList);

                        TextView mTitleText = new TextView(getActivity());
                        mTitleText.setText("Time Management by Chapter - based on recent 365 days of performance");
                        mTitleText.setGravity(Gravity.LEFT);
                        mTitleText.setTextAppearance(getActivity(), R.style.analytics_title_style);
                        mLinearLayoutChapter.addView(mTitleText);

                        for (Map.Entry<String,List<CourseAnalysis>> entry : courseDataMap.entrySet()) {

                            PieChart chart = new PieChart(getActivity());
                            RecyclerView recyclerView = new RecyclerView(getActivity());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            LinearLayout chartLayout = new LinearLayout(getActivity());
                            LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,converDPToPX(360) );
                            chartParams.setMargins(15,15,15,15);

                            initializeGraph(chart);

                            if(buildGraphData(entry.getValue(), CHAPTER, chart)){

                                TextView mDescText = new TextView(getActivity());
                                mDescText.setText(entry.getKey());
                                mDescText.setGravity(Gravity.CENTER);
                                mDescText.setTextAppearance(getActivity(), R.style.analytics_text_style);
                                mDescText.setPadding(0, 15, 0, 15);
                                mLinearLayoutChapter.addView(mDescText);

                                //Custom Legend
                                Legend mLegend = chart.getLegend();
                                CustomLegendAdapter customLegendAdapter = new CustomLegendAdapter(Arrays.copyOf(mLegend.getColors(),mLegend.getColors().length-1),
                                        Arrays.copyOf(mLegend.getLabels(), mLegend.getLabels().length - 1),getActivity().getLayoutInflater());
                                recyclerView.setAdapter(customLegendAdapter);


                                chartLayout.addView(chart, new LinearLayout.LayoutParams(0,
                                        LinearLayout.LayoutParams.MATCH_PARENT, 4.0f));
                                chartLayout.addView(recyclerView,new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.MATCH_PARENT, 2.0f));

                                mLinearLayoutChapter.addView(chartLayout, chartParams);
                                progressBar.setVisibility(View.GONE);
                                mLinearLayoutChapter.setVisibility(View.VISIBLE);
                            }else{
                                showFailMessage();
                            }


                        }
                    }
                });

    }

    private void initializeGraph(PieChart mChart){
        mChart.setDrawHoleEnabled(false);
        mChart.setDescription("");
        mChart.setUsePercentValues(true);
        mChart.setDrawSliceText(false);
        mChart.setMarkerView(new CustomMarkerView(getActivity(),R.layout.marker_view_pie_chart));
        mChart.setPadding(5, 10, 5, 10);
        mChart.getLegend().setEnabled(false);

    }

    private void buildChapterData(List<CourseAnalysis> courseAnalysisList){
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

    private synchronized void showFailMessage(){
        failCount++;
        if(failCount == 2){
            mFailText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    ArrayList<String> xData ;
    ArrayList<Float> yData ;

    private synchronized boolean buildGraphData(List<CourseAnalysis> courseAnalysisList,String graphType,PieChart mChart){

        xData = new ArrayList<>();
        yData = new ArrayList<>();
        for(CourseAnalysis courseAnalysis : courseAnalysisList){
            if(courseAnalysis.timeTaken == null ){
                continue;
            }else if(Float.parseFloat(courseAnalysis.timeTaken) == 0){
                continue;
            }
            yData.add(Float.parseFloat(courseAnalysis.timeTaken));

            if(graphType.equalsIgnoreCase(CHAPTER)) {
                xData.add(courseAnalysis.chapterName);
            }else if(graphType.equalsIgnoreCase(SUBJECT)){
                xData.add(courseAnalysis.subjectName);
            }

        }

        ArrayList<Entry> yVals1 = new ArrayList<>();

        for (int i=0;i < yData.size();i++) {
            yVals1.add(new Entry(yData.get(i),i));
        }


        if(!yVals1.isEmpty()) {
            PieDataSet dataSet = new PieDataSet(yVals1, "");
            dataSet.setSliceSpace(1f);
            dataSet.setSelectionShift(5);

            dataSet.setColors(AnalyticsHelper.getColors());

            PieData data = new PieData(xData, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);

            data.setDrawValues(false);
            mChart.setData(data);
            mChart.highlightValues(null);
            mChart.invalidate();
            mChart.setVisibility(View.VISIBLE);
            return true;
        }else{
            mChart.setVisibility(View.GONE);
            return false;
        }

    }

    int converDPToPX(float dp){
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);

    }

    class CustomMarkerView extends MarkerView {

        private TextView markerViewLine;

        public CustomMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            markerViewLine = (TextView) findViewById(R.id.tv_marker_view_text);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            markerViewLine.setText(xData.get(e.getXIndex())+", "+ yData.get(e.getXIndex()));
        }

        @Override
        public int getXOffset(float xPos) {
            // this will center the marker-view horizontally
            return -(getWidth() / 2);
        }

        @Override
        public int getYOffset(float yPos) {
            // this will cause the marker-view to be above the selected value
            return -getHeight();
        }
    }
}
