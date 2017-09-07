package com.education.corsalite.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.responsemodels.ExamResultSummayBysubject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.orm.util.Collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExamResultSummaryGraphFragment extends BaseFragment {

    @Bind(R.id.chart) LineChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_result_summary_graph, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadScatterChart();
    }

    private void loadScatterChart() {
        ExamResultSummayBysubject data = getData();
        List<LineDataSet> sets = new ArrayList<>();

        List<Entry> maxMarksEntries = new ArrayList<>();
        List<Entry> totalScoreEntries = new ArrayList<>();
        List<Entry> recommendedTimeEntries = new ArrayList<>();
        List<Entry> timeTakenEntries = new ArrayList<>();
        List<Entry> levelEntries = new ArrayList<>();
        for(int i=0; i<data.questions.size(); i++) {
            maxMarksEntries.add(new Entry(Float.parseFloat(data.questions.get(i).toString()), Float.parseFloat(data.maxMarks.get(i).toString())));
            totalScoreEntries.add(new Entry(Float.parseFloat(data.questions.get(i).toString()), Float.parseFloat(data.totalMarks.get(i).toString())));
            recommendedTimeEntries.add(new Entry(Float.parseFloat(data.questions.get(i).toString()), Float.parseFloat(data.timeRecommended.get(i).toString())));
            timeTakenEntries.add(new Entry(Float.parseFloat(data.questions.get(i).toString()), Float.parseFloat(data.timeTaken.get(i).toString())));
            levelEntries.add(new Entry(Float.parseFloat(data.questions.get(i).toString()), Float.parseFloat(data.levels.get(i).toString())));
        }

        Collections.sort(totalScoreEntries, new EntryXComparator());
        LineDataSet totalScoreDataset = new LineDataSet(totalScoreEntries, "Total Score");
        totalScoreDataset.setDrawIcons(true);
        totalScoreDataset.setColor(ColorTemplate.COLORFUL_COLORS[2]);
        sets.add(totalScoreDataset);

        Collections.sort(maxMarksEntries, new EntryXComparator());
        LineDataSet maxMarksDataset = new LineDataSet(maxMarksEntries, "Max Marks");
        maxMarksDataset.setDrawIcons(true);
        maxMarksDataset.setColor(ColorTemplate.COLORFUL_COLORS[1]);
        sets.add(maxMarksDataset);

        Collections.sort(timeTakenEntries, new EntryXComparator());
        LineDataSet timeTakenDataset = new LineDataSet(timeTakenEntries, "Time Taken");
        timeTakenDataset.setDrawIcons(true);
        timeTakenDataset.setColor(ColorTemplate.COLORFUL_COLORS[4]);
        sets.add(timeTakenDataset);

        Collections.sort(levelEntries, new EntryXComparator());
        LineDataSet levelsDataset = new LineDataSet(levelEntries, "Levels");
        levelsDataset.setDrawIcons(true);
        levelsDataset.setColor(ColorTemplate.LIBERTY_COLORS[1]);
        sets.add(levelsDataset);

        Collections.sort(recommendedTimeEntries, new EntryXComparator());
        LineDataSet recommendedTimeDataset = new LineDataSet(recommendedTimeEntries, "Recommended Time");
        recommendedTimeDataset.setDrawIcons(true);
        recommendedTimeDataset.setColor(ColorTemplate.COLORFUL_COLORS[3]);
        sets.add(recommendedTimeDataset);

        LineData lineData = new LineData();
        for(LineDataSet dataSet : sets) {
            lineData.addDataSet(dataSet);
        }
        chart.setData(lineData);
        chart.setDrawGridBackground(false);




        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line


        LimitLine ll1 = new LimitLine(150f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(-50f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);



        chart.invalidate();
    }

    private ExamResultSummayBysubject getData() {
        return Gson.get().fromJson(" {\n" +
                "                \"QuestionsJson\": [\n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    3, \n" +
                "                    4, \n" +
                "                    5, \n" +
                "                    6, \n" +
                "                    7, \n" +
                "                    8, \n" +
                "                    9, \n" +
                "                    10, \n" +
                "                    11, \n" +
                "                    12, \n" +
                "                    13, \n" +
                "                    14, \n" +
                "                    15, \n" +
                "                    16, \n" +
                "                    17, \n" +
                "                    18, \n" +
                "                    19, \n" +
                "                    20\n" +
                "                ], \n" +
                "                \"MarkTotalJson\": [\n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    1\n" +
                "                ], \n" +
                "                \"MarkMaxJson\": [\n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1\n" +
                "                ], \n" +
                "                \"PeerAvgScoreJson\": [\n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    1\n" +
                "                ], \n" +
                "                \"TimeTakenJson\": [\n" +
                "                    3, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    3, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    3\n" +
                "                ], \n" +
                "                \"LevelJson\": [\n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1\n" +
                "                ], \n" +
                "                \"TimeRecommendedJson\": [\n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30\n" +
                "                ], \n" +
                "                \"PeerAvgTimeTakenJson\": [\n" +
                "                    3, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    3, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    3\n" +
                "                ]\n" +
                "            }", ExamResultSummayBysubject.class);
    }

}
