package com.education.corsalite.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.utils.L;

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
public class TestCoverageTabFragment extends Fragment {

    HashMap<String,List<TestCoverage>> courseTestDataMap;


    @Bind(R.id.ll_test_coverage)LinearLayout mLinearLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_coverage,container,false);
        ButterKnife.bind(this,view);

        ApiManager.getInstance(getActivity()).getTestCoverage("1154", "13", new ApiCallback<List<TestCoverage>>() {
            @Override
            public void failure(CorsaliteError error) {
                L.info(error.message);
            }

            @Override
            public void success(List<TestCoverage> testCoverages, Response response) {
                buildTestData(testCoverages);
                for (Map.Entry<String,List<TestCoverage>> entry : courseTestDataMap.entrySet()) {
                    TableLayout mTableLayout = new TableLayout(getActivity());
                    mLinearLayout.addView(mTableLayout,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                    buildTable(entry.getValue(), mTableLayout);
                    mTableLayout.setPadding(15, 15, 10, 20);
                    mTableLayout.setGravity(Gravity.CENTER);
                }
            }
        });

        return view;
    }
    private void buildTable(List<TestCoverage> testCoverageListBySubject,TableLayout tableLayout) {
        //level on x-axis and chapter along y-axis and testpercentage  as value
        HashMap<String, String[]> tableDataMap = new HashMap<>();
        int maxLevel = -1;

        for (TestCoverage testCoverage : testCoverageListBySubject) {
            String[] rowData;
            int level = Integer.parseInt(testCoverage.level);
            if (tableDataMap.get(testCoverage.chapter) == null) {
                rowData = new String[6];
            }else {
                rowData = tableDataMap.get(testCoverage.chapter);
            }
            rowData[level]= testCoverage.testCoverage;
            tableDataMap.put(testCoverage.chapter, rowData);
            if (level > maxLevel) {
                maxLevel = level;
            }
        }

        //Create table row of levels
        TableRow levelRow = new TableRow(getActivity());
        TextView[] levelRowData = new TextView[maxLevel + 1];
        levelRowData[0] = new TextView(getActivity());
        levelRowData[0].setText("");
        levelRow.addView(levelRowData[0]);

        for(int i=1;i<=maxLevel;i++){
            levelRowData[i]=new TextView(getActivity());
            levelRowData[i].setText("Level " + i);
            levelRow.addView(levelRowData[i]);
        }
        tableLayout.addView(levelRow);

        for (Map.Entry<String, String[]> entry : tableDataMap.entrySet()) {
            int index = 0;
            TextView[] tableRowData = new TextView[maxLevel + 1];
            TableRow tableRow = new TableRow(getActivity());
            tableRowData[index] = new TextView(getActivity());
            tableRowData[index].setText(entry.getKey());
            tableRow.addView(tableRowData[index]);
            String[] row = entry.getValue();
            for (int i=1;i<row.length;i++) {
                index +=1;
                tableRowData[index] = new TextView(getActivity());
                tableRowData[index].setText(row[i]);
                tableRow.addView(tableRowData[index]);
            }
            tableLayout.addView(tableRow);
        }
    }


    private void buildTestData(List<TestCoverage> testCoverages){

        courseTestDataMap = new HashMap<>();

        for(TestCoverage testCoverage:testCoverages){
            if(courseTestDataMap.get(testCoverage.subject) == null){
                ArrayList<TestCoverage> courseDataList = new ArrayList<>();
                courseDataList.add(testCoverage);
                courseTestDataMap.put(testCoverage.subject,courseDataList);
            }else{
                List<TestCoverage> courseDataList = courseTestDataMap.get(testCoverage.subject);
                courseDataList.add(testCoverage);
                courseTestDataMap.put(testCoverage.subject,courseDataList);
            }
        }
    }
}
