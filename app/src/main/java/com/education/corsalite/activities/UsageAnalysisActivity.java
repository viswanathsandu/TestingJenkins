package com.education.corsalite.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.UsageAnalysis;
import com.education.corsalite.utils.L;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by Aastha on 19/11/15.
 */
public class UsageAnalysisActivity extends AbstractBaseActivity {

    CombinedChart mChart1;
    CombinedChart mChart2;
    ProgressBar mProgressBar;
    TextView mFailText;
    LinearLayout mLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_usage_analysis, null);
        frameLayout.addView(myView);
        mChart1 = (CombinedChart)findViewById(R.id.usage_analysis_chart1);
        mChart2 = (CombinedChart)findViewById(R.id.usage_analysis_chart2);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar_tab);
        mFailText = (TextView)findViewById(R.id.tv_failure_text);
        mLinearLayout = (LinearLayout)findViewById(R.id.ll_usage_analysis);
        setToolbarForUsageAnalysis();
        init();
    }

    private void init(){
        ApiManager.getInstance(this).getUsageAnalysis(appPref.getUserId(),
                new ApiCallback<UsageAnalysis>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error(error.message);
                        mProgressBar.setVisibility(View.GONE);
                        mFailText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void success(UsageAnalysis usageAnalysis, Response response) {
                        super.success(usageAnalysis, response);
                        mProgressBar.setVisibility(View.GONE);
                        mLinearLayout.setVisibility(View.VISIBLE);

                        initializeGraph(mChart1);
                        initializeGraph(mChart2);
                        CombinedData data1 = new CombinedData(usageAnalysis.pagesName);
                        data1.setData(generateLineData(usageAnalysis.pageUsagePercentageList));
                        data1.setData(generateBarData(usageAnalysis.userAuditList, "Student Audit",getResources().getColor(R.color.green)));
                        mChart1.setData(data1);
                        mChart1.invalidate();

                        CombinedData data2 = new CombinedData(usageAnalysis.pagesName);
                        data2.setData(generateLineData(usageAnalysis.pageUsagePercentageList));
                        data2.setData(generateBarData(usageAnalysis.allUserAuditList, " All Students Audit",getResources().getColor(R.color.cyan)));
                        mChart2.setData(data2);
                        mChart2.invalidate();

                    }
                });
    }

    private void initializeGraph(CombinedChart mChart){
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        mChart.getLegend().setTextSize(15f);
        mChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

    }

    private LineData generateLineData(List<Integer> data){
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i=0;i<data.size();i++) {
            entries.add(new Entry(data.get(i), i));
        }

        LineDataSet set = new LineDataSet(entries, "Recommended Percentage");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleSize(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setDrawCubic(true);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData(List<Float> data,String setName,int color){
        BarData d = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i=0;i<data.size();i++){
            entries.add(new BarEntry(data.get(i),i));
        }

        BarDataSet set = new BarDataSet(entries, setName);
        set.setColor(color);
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);
        set.setBarSpacePercent(20f);

        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }


}
