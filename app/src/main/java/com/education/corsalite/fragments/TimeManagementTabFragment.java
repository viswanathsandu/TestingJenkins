package com.education.corsalite.fragments;

import android.app.Fragment;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 27/09/15.
 */
public class TimeManagementTabFragment extends Fragment {

    @Bind(R.id.pc_chemistry)PieChart graphChemistry;
    @Bind(R.id.pc_maths)PieChart graphMaths;
    @Bind(R.id.pc_english)PieChart graphEnglish;
    @Bind(R.id.pc_lr)PieChart graphLogicalReasoning;
    @Bind(R.id.pc_physics)PieChart graphPhysics;
    @Bind(R.id.pc_subject)PieChart graphBySubject;


    final String SUBJECT = "subject";
    final String CHAPTER = "chapter";

    final String SUBJECT_PHYSICS = "Physics";
    final String SUBJECT_CHEMISTRY = "Chemistry";
    final String SUBJECT_MATHS = "Maths";
    final String SUBJECT_ENGLISH = "English";
    final String SUBJECT_LOGICAL_REASONING = "LogicalReasoning";

    List<CourseAnalysis> physicsDataList = new ArrayList<>();
    List<CourseAnalysis> chemistryDataList = new ArrayList<>();
    List<CourseAnalysis> mathsDataList = new ArrayList<>();
    List<CourseAnalysis> englishDataList = new ArrayList<>();
    List<CourseAnalysis> lrDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time_management_tab,container,false);
        ButterKnife.bind(this,v);

        initializeGraph();
        ApiManager.getInstance(getActivity()).getCourseAnalysisData("1154", "13", null, "Subject", "None", "30", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        buildGraphData(courseAnalysisList, SUBJECT, graphBySubject);
                    }
                });


             ApiManager.getInstance(getActivity()).getCourseAnalysisData("1154", "13", null, "Chapter", "None", "30", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        buildChapterData(courseAnalysisList);
                        if(physicsDataList.size() == 0){
                            graphPhysics.setVisibility(View.GONE);
                        }else{
                            buildGraphData(physicsDataList, CHAPTER, graphPhysics);
                        }
                        if(chemistryDataList.size() == 0){
                            graphChemistry.setVisibility(View.GONE);
                        }else{
                            buildGraphData(chemistryDataList, CHAPTER, graphChemistry);
                        }
                        if(mathsDataList.size() == 0){
                            graphMaths.setVisibility(View.GONE);
                        }else{
                            buildGraphData(mathsDataList,CHAPTER,graphMaths);
                        }
                        if(lrDataList.size() == 0){
                            graphLogicalReasoning.setVisibility(View.GONE);
                        }else{
                            buildGraphData(lrDataList,CHAPTER,graphLogicalReasoning);
                        }
                        if(englishDataList.size() == 0){
                            graphEnglish.setVisibility(View.GONE);
                        }else{
                            buildGraphData(englishDataList,CHAPTER,graphEnglish);
                        }

                    }
                });


        return v;
    }

    private void initializeGraph(){
        graphBySubject.setDrawHoleEnabled(false);
        graphPhysics.setDrawHoleEnabled(false);
        graphEnglish.setDrawHoleEnabled(false);
        graphChemistry.setDrawHoleEnabled(false);
        graphMaths.setDrawHoleEnabled(false);
        graphLogicalReasoning.setDrawHoleEnabled(false);

        graphBySubject.setUsePercentValues(true);
        graphLogicalReasoning.setUsePercentValues(true);
        graphMaths.setUsePercentValues(true);
        graphPhysics.setUsePercentValues(true);
        graphChemistry.setUsePercentValues(true);
        graphEnglish.setUsePercentValues(true);


        graphBySubject.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        graphLogicalReasoning.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        graphPhysics.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        graphMaths.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        graphChemistry.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        graphEnglish.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART);


        }

    private void buildChapterData(List<CourseAnalysis> courseAnalysisList){
        for(CourseAnalysis course:courseAnalysisList){
            switch (course.subjectName){
                case SUBJECT_PHYSICS:
                    physicsDataList.add(course);
                    break;
                case SUBJECT_CHEMISTRY:
                    chemistryDataList.add(course);
                    break;
                case SUBJECT_ENGLISH:
                    englishDataList.add(course);
                    break;
                case SUBJECT_MATHS:
                    mathsDataList.add(course);
                    break;
                case SUBJECT_LOGICAL_REASONING:
                    lrDataList.add(course);
                    break;
            }
        }
    }
    private void buildGraphData(List<CourseAnalysis> courseAnalysisList,String graphType,PieChart mChart){

        ArrayList<String> xData = new ArrayList<>();
        ArrayList<Float> yData = new ArrayList<>();

        for(CourseAnalysis courseAnalysis : courseAnalysisList){
            if(graphType.equalsIgnoreCase(CHAPTER)) {
                xData.add(courseAnalysis.chapterName);
            }else if(graphType.equalsIgnoreCase(SUBJECT)){
                xData.add(courseAnalysis.subjectName);
            }
            if(courseAnalysis.accuracy == null){
                yData.add(0F);
                continue;
            }
            yData.add(Float.parseFloat(courseAnalysis.accuracy));
        }

        ArrayList<Entry> yVals1 = new ArrayList<>();

        for (int i=0;i<yData.size();i++) {
            yVals1.add(new Entry(yData.get(i),i));
        }


        PieDataSet dataSet = new PieDataSet(yVals1, "Accuracy By"+graphType);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xData, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.highlightValues(null);
        mChart.invalidate();

    }
}
