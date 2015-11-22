package com.education.corsalite.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.UsageAnalysis;
import com.education.corsalite.utils.L;
import com.github.mikephil.charting.charts.CombinedChart;
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

    CombinedChart mChart ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_usage_analysis, null);
        frameLayout.addView(myView);
        mChart = (CombinedChart)findViewById(R.id.usage_analysis_chart);

        setToolbarForUsageAnalysis();
        init();
    }

    private void init(){
        ApiManager.getInstance(this).getUsageAnalysis(LoginUserCache.getInstance().loginResponse.userId,
                new ApiCallback<UsageAnalysis>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error(error.message);

                    }

                    @Override
                    public void success(UsageAnalysis usageAnalysis, Response response) {
                        super.success(usageAnalysis, response);
                        initializeGraph();
                        CombinedData data = new CombinedData(usageAnalysis.pagesName);
                        data.setData(generateLineData(usageAnalysis.pageUsagePercentageList));
                        data.setData(generateBarData(usageAnalysis));
                        mChart.setData(data);
                        mChart.invalidate();
                    }
                });
    }


    private void initializeGraph(){
        mChart.setDescription("Usage Analysis");
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

    private BarData generateBarData(UsageAnalysis usageAnalysisData){
        BarData d = new BarData(usageAnalysisData.pagesName);

        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();

        for (int i=0;i<usageAnalysisData.pagesName.size();i++){
            entries1.add(new BarEntry(usageAnalysisData.userAuditList.get(i),i));
            entries2.add(new BarEntry(usageAnalysisData.allUserAuditList.get(i),i));
        }

        BarDataSet set1 = new BarDataSet(entries1, "Student Audit");
        set1.setColor(Color.rgb(0, 130, 58));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setBarSpacePercent(0f);

        BarDataSet set2 = new BarDataSet(entries2, "All Students Audit");
        set2.setColor(getResources().getColor(R.color.material_blue_grey_800));
        set2.setValueTextColor(Color.rgb(60, 220, 78));
        set2.setValueTextSize(10f);
        set2.setBarSpacePercent(0f);

        d.addDataSet(set1);
        d.addDataSet(set2);

        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }
}
