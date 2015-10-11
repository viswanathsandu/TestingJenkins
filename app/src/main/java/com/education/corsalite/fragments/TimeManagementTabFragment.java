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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.CustomLegendAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.utils.L;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
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
public class TimeManagementTabFragment extends Fragment {

    @Bind(R.id.pc_subject)PieChart graphBySubject;
    @Bind(R.id.ll_time_mgmnt)LinearLayout mLinearLayout;
    @Bind(R.id.rv_legend)RecyclerView mRecyclerView;

    HashMap<String,List<CourseAnalysis>> courseDataMap;
    final String SUBJECT = "Subject";
    final String CHAPTER = "Chapter";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time_management_tab,container,false);
        ButterKnife.bind(this, v);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId, "13", null, "Subject", "None", "30", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        if(getActivity() == null) {
                            return;
                        }
                        initializeGraph(graphBySubject);
                        buildGraphData(courseAnalysisList, SUBJECT, graphBySubject);
                        //Custom Legend
                        Legend mLegend = graphBySubject.getLegend();
                        CustomLegendAdapter customLegendAdapter = new CustomLegendAdapter(mLegend.getColors(),mLegend.getLabels(),getActivity().getLayoutInflater());
                        mRecyclerView.setAdapter(customLegendAdapter);
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
                        if(getActivity() == null) {
                            return;
                        }
                        buildChapterData(courseAnalysisList);
                        for (Map.Entry<String,List<CourseAnalysis>> entry : courseDataMap.entrySet()) {
                            TextView mDescText = new TextView(getActivity());
                            mDescText.setText(entry.getKey());
                            mDescText.setGravity(Gravity.CENTER);
                            mDescText.setTextSize(16);
                            mDescText.setPadding(0, 10, 0, 10);
                            mLinearLayout.addView(mDescText);
                            PieChart chart = new PieChart(getActivity());
                            RelativeLayout chartLayout = new RelativeLayout(getActivity());

                            RelativeLayout.LayoutParams chartPrams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, converDPToPX(400));
                            initializeGraph(chart);
                            buildGraphData(entry.getValue(), CHAPTER, chart);

                            RecyclerView recyclerView = new RecyclerView(getActivity());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            //Custom Legend
                            Legend mLegend = chart.getLegend();
                            CustomLegendAdapter customLegendAdapter = new CustomLegendAdapter(mLegend.getColors(),mLegend.getLabels(),getActivity().getLayoutInflater());
                            recyclerView.setAdapter(customLegendAdapter);
                            recyclerView.setPadding(0,15,0,0);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(converDPToPX(150), converDPToPX(400));
                            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                            chartLayout.addView(chart, chartPrams);
                            chartLayout.addView(recyclerView, params);
                            mLinearLayout.addView(chartLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        }
                    }
                });


        return v;
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

    ArrayList<String> xData = new ArrayList<>();
    ArrayList<Float> yData = new ArrayList<>();

    private void buildGraphData(List<CourseAnalysis> courseAnalysisList,String graphType,PieChart mChart){


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


        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        //colors.add(ColorTemplate.getHoloBlue());

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

    class CustomMarkerView extends MarkerView {

        private TextView markerViewLine;

        public CustomMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            markerViewLine = (TextView) findViewById(R.id.tv_marker_view_text);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            markerViewLine.setText(xData.get(e.getXIndex())+", "+ yData.get(e.getXIndex()));


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
