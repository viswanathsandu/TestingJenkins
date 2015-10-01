package com.education.corsalite.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.utils.L;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accuracy_tab, container, false);
        ButterKnife.bind(this, view);

        initializeGraph();
        //TODO: Build from api response later
        ApiManager.getInstance(getActivity()).getCourseAnalysisData("1154", "13", "51", "Chapter", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        buildChapterGraphData(courseAnalysisList,CHAPTER);
                    }
                });

        ApiManager.getInstance(getActivity()).getCourseAnalysisData("1154", "13", "51", "Dates", "Month", "365", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        buildChapterGraphData(courseAnalysisList,DATES);
                    }
                });

        return view;
    }

    private void initializeGraph(){
        Legend l = accuracyChapterChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        Legend l1 = accuracyDateChart.getLegend();
        l1.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        setMarkerView();
    }

    private void setMarkerView(){
        //TODO
    }
    private void buildChapterGraphData(List<CourseAnalysis> courseAnalysisList,String graphType){
        ArrayList<String> xVals = new ArrayList<>();

        HashMap<String,ArrayList<Entry>> numYValEntries = new HashMap<>();
        for (CourseAnalysis analysisDetail: courseAnalysisList) {
            if(analysisDetail.speed == null || analysisDetail.accuracy == null) {
                analysisDetail.speed = "0";
                analysisDetail.accuracy = "0";
            }

            xVals.add(analysisDetail.speed);

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
                    ArrayList<Entry> list = numYValEntries.get(analysisDetail.chapterName);
                    list.add(new Entry(Float.valueOf(analysisDetail.accuracy), xVals.indexOf(analysisDetail.speed)));
                    numYValEntries.put(key, list);
            }
        }

        ArrayList<ScatterDataSet> scatterDataSetList = new ArrayList<>();

        int i=0;
        for (Map.Entry<String,ArrayList<Entry>> entry : numYValEntries.entrySet()) {
            ScatterDataSet dataSet = new ScatterDataSet(entry.getValue(),entry.getKey());
            scatterDataSetList.add(dataSet);
            dataSet.setColor(ColorTemplate.COLORFUL_COLORS[i%5]);
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

}
