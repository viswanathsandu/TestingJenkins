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
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.CustomLegendAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.utils.L;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    LinearLayoutManager mLayoutManagerDates;
    LinearLayoutManager mLayoutManagerChapter;
    RecyclerView.Adapter customLegendAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accuracy_tab, container, false);
        ButterKnife.bind(this, view);

        mLayoutManagerDates = new LinearLayoutManager(getActivity());
        mLayoutManagerChapter = new LinearLayoutManager(getActivity());
        rvChapterLegend.setLayoutManager(mLayoutManagerChapter);
        rvDatesLegend.setLayoutManager(mLayoutManagerDates);
        initializeGraph();
        //TODO: Build from api response later
        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId, "13", "51", "Chapter", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        buildChapterGraphData(courseAnalysisList, CHAPTER);
                        //create custom legend
                        Legend chapterLegend = accuracyChapterChart.getLegend();
                        customLegendAdapter = new CustomLegendAdapter(chapterLegend.getColors(),chapterLegend.getLabels(),getActivity().getLayoutInflater());
                        rvChapterLegend.setAdapter(customLegendAdapter);
                    }
                });

        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId, "13", "51", "Subject", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        buildChapterGraphData(courseAnalysisList, DATES);

                        Legend datesLegend = accuracyDateChart.getLegend();
                        customLegendAdapter = new CustomLegendAdapter(datesLegend.getColors(),datesLegend.getLabels(),getActivity().getLayoutInflater());
                        rvDatesLegend.setAdapter(customLegendAdapter);
                    }
                });

        return view;
    }

    private void initializeGraph(){
        accuracyChapterChart.setDescription("Accuracy Vs Speed based on recent 365 days performance");
        accuracyDateChart.setDescription("Accuracy Vs Speed based on recent 365 days performance");

        accuracyDateChart.getLegend().setEnabled(false);
        accuracyChapterChart.getLegend().setEnabled(false);

        setMarkerView();

    }

    private void setMarkerView(){
        accuracyChapterChart.setMarkerView(new CustomMarkerView(getActivity(),R.layout.marker_view_chart,CHAPTER));
        accuracyDateChart.setMarkerView(new CustomMarkerView(getActivity(),R.layout.marker_view_chart,DATES));
    }

    ArrayList<String> xVals = new ArrayList<>();
    ArrayList<String> chapterList = new ArrayList<>();
    ArrayList<String> dateList = new ArrayList<>();
    ArrayList<String> subjectName = new ArrayList<>();
    private void buildChapterGraphData(List<CourseAnalysis> courseAnalysisList,String graphType){


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
                    list.add(new Entry(Float.valueOf(analysisDetail.accuracy), xVals.indexOf(analysisDetail.speed)));
                    numYValEntries.put(key, list);
            } else {
                    ArrayList<Entry> list = numYValEntries.get(key);
                    list.add(new Entry(Float.valueOf(analysisDetail.accuracy), xVals.indexOf(analysisDetail.speed)));
                    numYValEntries.put(key, list);
            }
        }

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        ArrayList<ScatterDataSet> scatterDataSetList = new ArrayList<>();

        int i=0;
        for (Map.Entry<String,ArrayList<Entry>> entry : numYValEntries.entrySet()) {
            ScatterDataSet dataSet = new ScatterDataSet(entry.getValue(),entry.getKey());
            scatterDataSetList.add(dataSet);
            dataSet.setColor(colors.get(i%colors.size()));
            dataSet.setScatterShape(ScatterChart.getAllPossibleShapes()[i % 4]);
            dataSet.setScatterShapeSize(8f);
            i++;
        }

        ScatterData data = new ScatterData(xVals, scatterDataSetList);
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
            markerViewLine1 = (TextView) findViewById(R.id.tv_chapterDate);
            markerViewLine2 = (TextView) findViewById(R.id.tv_accuracy_speed);
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
        public int getXOffset() {
            // this will center the marker-view horizontally
            return -(getWidth() / 2);
        }

        @Override
        public int getYOffset() {
            // this will cause the marker-view to be above the selected value
            return -getHeight();
        }
    }
}
