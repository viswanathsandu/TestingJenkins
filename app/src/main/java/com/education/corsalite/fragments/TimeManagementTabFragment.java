package com.education.corsalite.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 27/09/15.
 */
public class TimeManagementTabFragment extends Fragment {

    @Bind(R.id.pc_subject)PieChart graphBySubject;
    @Bind(R.id.ll_time_mgmnt)LinearLayout mLinearLayout;

    HashMap<String,List<CourseAnalysis>> courseDataMap;
    final String SUBJECT = "Subject";
    final String CHAPTER = "Chapter";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time_management_tab,container,false);
        ButterKnife.bind(this, v);

        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId, "13", null, "Subject", "None", "30", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        initializeGraph(graphBySubject);
                        buildGraphData(courseAnalysisList, SUBJECT, graphBySubject);
                    }
                });


             ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId, "13", null, "Chapter", "None", "30", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        buildChapterData(courseAnalysisList);
                        for (Map.Entry<String,List<CourseAnalysis>> entry : courseDataMap.entrySet()) {
                            TextView mDescText = new TextView(getActivity());
                            mDescText.setText(entry.getKey());
                            mDescText.setGravity(Gravity.CENTER);
                            mDescText.setTextSize(16);
                            mDescText.setPadding(0, 10, 0, 10);
                            mLinearLayout.addView(mDescText);
                            PieChart chart = new PieChart(getActivity());
                            mLinearLayout.addView(chart, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, converDPToPX(350)));
                            initializeGraph(chart);
                            buildGraphData(entry.getValue(), CHAPTER, chart);

                        }
                    }
                });


        return v;
    }

    private void initializeGraph(PieChart mChart){
        mChart.setDrawHoleEnabled(false);
        mChart.setDescription("");
        mChart.setUsePercentValues(true);
        mChart.setPadding(5, 10, 5, 10);
        mChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);

    }

    private void buildChapterData(List<CourseAnalysis> courseAnalysisList){

        courseDataMap = new HashMap<>();

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
    private void buildGraphData(List<CourseAnalysis> courseAnalysisList,String graphType,PieChart mChart){

        ArrayList<String> xData = new ArrayList<>();
        ArrayList<Float> yData = new ArrayList<>();

        for(CourseAnalysis courseAnalysis : courseAnalysisList){
            if(graphType.equalsIgnoreCase(CHAPTER)) {
                xData.add(courseAnalysis.chapterName);
            }else if(graphType.equalsIgnoreCase(SUBJECT)){
                xData.add(courseAnalysis.subjectName);
            }
            if(courseAnalysis.timeTaken == null ){
                continue;
            }else if(Float.parseFloat(courseAnalysis.timeTaken) == 0){
                continue;
            }
            yData.add(Float.parseFloat(courseAnalysis.timeTaken));
        }

        ArrayList<Entry> yVals1 = new ArrayList<>();

        for (int i=0;i<yData.size();i++) {
            yVals1.add(new Entry(yData.get(i),i));
        }


        PieDataSet dataSet = new PieDataSet(yVals1, "Accuracy By "+graphType);
        dataSet.setSliceSpace(1f);
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

    int converDPToPX(float dp){
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);

    }
}
