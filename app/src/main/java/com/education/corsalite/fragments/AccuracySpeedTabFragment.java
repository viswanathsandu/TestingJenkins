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
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
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
public class AccuracySpeedTabFragment extends Fragment   {

    private final String CHAPTER = "chapter";
    private final String DATES = "dates";
    @Bind(R.id.sch_accuray_chapter)ScatterChart accuracyChapterChart;
    @Bind(R.id.sch_accuray_date)ScatterChart accuracyDateChart;
    @Bind(R.id.rv_chapter_legend)RecyclerView rvChapterLegend;
    @Bind(R.id.rv_dates_legend)RecyclerView rvDatesLegend;
    @Bind(R.id.progress_bar_tab)ProgressBar mProgressBar;
    @Bind(R.id.tv_failure_text)TextView mTextViewFail ;
    @Bind(R.id.ll_chapter)LinearLayout mParentLayoutChapter;
    @Bind(R.id.ll_dates)LinearLayout mParentLayoutDates;
    LinearLayoutManager mLayoutManagerDates;
    LinearLayoutManager mLayoutManagerChapter;
    RecyclerView.Adapter customLegendAdapter;
    int failCount =0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_accuracy_tab, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mLayoutManagerDates = new LinearLayoutManager(getActivity());
        mLayoutManagerChapter = new LinearLayoutManager(getActivity());
        rvChapterLegend.setLayoutManager(mLayoutManagerChapter);
        rvDatesLegend.setLayoutManager(mLayoutManagerDates);
        initializeGraph(accuracyChapterChart);
        initializeGraph(accuracyDateChart);
        setMarkerView();
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
        mProgressBar.setVisibility(View.VISIBLE);
        mParentLayoutChapter.setVisibility(View.GONE);
        mParentLayoutDates.setVisibility(View.GONE);
        mTextViewFail.setVisibility(View.GONE);
        drawGraphs(course.courseId+"");
    }

    private void drawGraphs(String courseId) {
        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "Chapter", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysis>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error)
                    {
                        L.error(error.message);
                        showFailMessage();
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if(getActivity() == null) {
                            return;
                        }
                        buildGraphData(courseAnalysisList, CHAPTER);
                        //create custom legend
                        Legend chapterLegend = accuracyChapterChart.getLegend();
                        customLegendAdapter = new CustomLegendAdapter(chapterLegend.getColors(),chapterLegend.getLabels(),getActivity().getLayoutInflater());
                        rvChapterLegend.setAdapter(customLegendAdapter);

                        mProgressBar.setVisibility(View.GONE);
                        mParentLayoutChapter.setVisibility(View.VISIBLE);
                    }
                });

        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "Subject", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysis>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error(error.message);
                        showFailMessage();
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if(getActivity() == null) {
                            return;
                        }
                        buildGraphData(courseAnalysisList, DATES);
                        Legend datesLegend = accuracyDateChart.getLegend();
                        ArrayList<String> dates = new ArrayList<String>(Arrays.asList(datesLegend.getLabels()));
                        Object[] objectArray =  AnalyticsHelper.parseDate(dates,false).toArray();
                        customLegendAdapter = new CustomLegendAdapter(datesLegend.getColors()
                                ,Arrays.asList(objectArray).toArray(new String[objectArray.length])
                                ,getActivity().getLayoutInflater());
                        rvDatesLegend.setAdapter(customLegendAdapter);

                        mProgressBar.setVisibility(View.GONE);
                        mParentLayoutDates.setVisibility(View.VISIBLE);

                    }
                });

    }

    private synchronized void showFailMessage(){
        failCount++;
        if(failCount == 2){
            mTextViewFail.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

        }
    }
    private void initializeGraph(ScatterChart mChart){

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

    }

    private void setMarkerView(){
        accuracyChapterChart.setMarkerView(new CustomMarkerView(getActivity(),R.layout.marker_view_chart,CHAPTER));
        accuracyDateChart.setMarkerView(new CustomMarkerView(getActivity(),R.layout.marker_view_chart,DATES));
    }

    ArrayList<String> xVals ;
    ArrayList<String> chapterList ;
    ArrayList<String> dateList ;
    ArrayList<String> subjectName ;

    private synchronized void buildGraphData(List<CourseAnalysis> courseAnalysisList,String graphType){
        xVals = new ArrayList<>();
        chapterList = new ArrayList<>();
        dateList = new ArrayList<>();
        subjectName = new ArrayList<>();

        HashMap<String,ArrayList<Entry>> numYValEntries = new HashMap<>();
        for (CourseAnalysis analysisDetail: courseAnalysisList) {
            if(analysisDetail.speed == null || analysisDetail.accuracy == null) {
                analysisDetail.speed = "0";
                analysisDetail.accuracy = "0";
            }

            xVals.add(analysisDetail.speed);
            chapterList.add(analysisDetail.chapterName);
            dateList.add(analysisDetail.date);
            subjectName.add(analysisDetail.subjectName);

            String key = null;
            if(graphType.equalsIgnoreCase(CHAPTER)){
                key = analysisDetail.chapterName;
            }else if(graphType.equalsIgnoreCase(DATES)){
                key = analysisDetail.date;
            }
            if (numYValEntries.get(key) == null) {
                    ArrayList<Entry> list = new ArrayList<>();
                    list.add(new Entry(Math.round(Float.valueOf(analysisDetail.accuracy) * 100.0F) / 100.0F, xVals.indexOf(analysisDetail.speed)));
                    numYValEntries.put(key, list);
            } else {
                    ArrayList<Entry> list = numYValEntries.get(key);
                    list.add(new Entry(Math.round(Float.valueOf(analysisDetail.accuracy) * 100.0F) / 100.0F, xVals.indexOf(analysisDetail.speed)));
                    numYValEntries.put(key, list);
            }
        }

        ArrayList<ScatterDataSet> scatterDataSetList = new ArrayList<>();

        int i=0;
        for (Map.Entry<String,ArrayList<Entry>> entry : numYValEntries.entrySet()) {
            ScatterDataSet dataSet = new ScatterDataSet(entry.getValue(),entry.getKey());
            scatterDataSetList.add(dataSet);
            dataSet.setColor(AnalyticsHelper.getColors().get(i % AnalyticsHelper.getColors().size()));
            dataSet.setScatterShape(ScatterChart.getAllPossibleShapes()[i % 4]);
            dataSet.setScatterShapeSize(8f);
            i++;
        }

        //truncate xvals
        ArrayList<String> truncXVals = new ArrayList<>();
        for(String val:xVals){
            truncXVals.add(AnalyticsHelper.truncateString(val));
        }
        xVals = truncXVals;

        ScatterData data = new ScatterData(xVals, scatterDataSetList);
        data.setDrawValues(false);
        if(graphType.equalsIgnoreCase(CHAPTER)) {
            accuracyChapterChart.setData(data);
            accuracyChapterChart.invalidate();
        }else{
            accuracyDateChart.setData(data);
            accuracyDateChart.invalidate();
        }


    }

    class CustomMarkerView extends MarkerView {

        private TextView markerViewLine1;
        private TextView markerViewLine2;
        String chartType;

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
            if(chartType.equalsIgnoreCase(CHAPTER)) {
                markerViewLine1.setText(chapterList.get(e.getXIndex()) + "," + dateList.get(e.getXIndex()));
            }else if(chartType.equalsIgnoreCase(DATES)) {
                markerViewLine1.setText(chapterList.get(e.getXIndex()) + "," + subjectName.get(e.getXIndex()));
            }
            markerViewLine2.setText("Accuracy:" + e.getVal() + "," + "Speed:" + xVals.get(e.getXIndex()));
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
