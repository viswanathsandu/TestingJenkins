package com.education.corsalite.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.utils.L;

import java.util.ArrayList;
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
public class TestCoverageTabFragment extends Fragment {

    @Bind(R.id.tv_failure_text)TextView failureText;
    @Bind(R.id.progressBar)ProgressBar progressBar;
    @Bind(R.id.ll_test_coverage)LinearLayout mLinearLayout;

    private HashMap<String,List<TestCoverage>> courseTestDataMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_coverage,container,false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
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
        courseTestDataMap.clear();
        mLinearLayout.removeAllViews();
        progressBar.setVisibility(View.VISIBLE);
        failureText.setVisibility(View.GONE);
        fetchDataFromServer(course.courseId + "");
    }

    private void fetchDataFromServer(String courseId) {
        ApiManager.getInstance(getActivity()).getTestCoverage(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, new ApiCallback<List<TestCoverage>>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                L.info(error.message);
                progressBar.setVisibility(View.GONE);
                failureText.setVisibility(View.VISIBLE);
            }

            @Override
            public void success(List<TestCoverage> testCoverages, Response response) {
                super.success(testCoverages, response);
                if(getActivity() == null) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                buildTestData(testCoverages);
                for (Map.Entry<String, List<TestCoverage>> entry : courseTestDataMap.entrySet()) {

                    TextView mTableDesc = new TextView(getActivity());
                    mTableDesc.setText("Test Coverage (%) by Subject - " + entry.getKey());
                    mTableDesc.setGravity(Gravity.CENTER);
                    mTableDesc.setTextAppearance(getActivity(), R.style.analytics_title_style);
                    mTableDesc.setPadding(8, 8, 8, 8);
                    mLinearLayout.addView(mTableDesc);

                    TableLayout mTableLayout = new TableLayout(getActivity());
                    buildTable(entry.getValue(), mTableLayout);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(5, 20, 5, 100);
                    mTableLayout.setGravity(Gravity.CENTER);
                    mLinearLayout.addView(mTableLayout, lp);
                }
            }
        });

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
        levelRowData[0].setTextAppearance(getActivity(),R.style.analytics_text_style);
        levelRow.addView(levelRowData[0]);

        for(int i=1;i<=maxLevel;i++){
            levelRowData[i]=new TextView(getActivity());
            levelRowData[i].setText("Level " + i);
            levelRowData[i].setTextAppearance(getActivity(),R.style.analytics_text_style);
            levelRowData[i].setPadding(10, 2, 10, 2);
            levelRowData[i].setGravity(Gravity.CENTER);
            levelRow.addView(levelRowData[i]);
        }

        tableLayout.addView(levelRow);

        for (Map.Entry<String, String[]> entry : tableDataMap.entrySet()) {
            int index = 0;
            TextView[] tableRowData = new TextView[maxLevel + 1];
            TableRow tableRow = new TableRow(getActivity());
            tableRowData[index] = new TextView(getActivity());
            tableRowData[index].setText(entry.getKey());
            tableRowData[index].setTextAppearance(getActivity(), R.style.analytics_text_style);
            tableRowData[index].setPadding(5, 2, 5, 2);
            tableRowData[index].setGravity(Gravity.RIGHT);
            tableRow.addView(tableRowData[index]);

            String[] row = entry.getValue();
            for (int i=1;i<row.length;i++) {
                index +=1;
                // TODO : app is crashing for some courses. Because of arrayindex outofbound issue. Fix it
                if(index < tableRowData.length) {
                    tableRowData[index] = new TextView(getActivity());
                    tableRowData[index].setText(row[i]);
                    setTextViewLayouParams(tableRowData[index]);
                    tableRow.addView(tableRowData[index]);
                }
            }

            tableLayout.addView(tableRow);
        }
    }



    private void setTextViewLayouParams(TextView textViewLayoutParams){
        textViewLayoutParams.setPadding(10, 2, 10, 2);
        textViewLayoutParams.setGravity(Gravity.CENTER);
        textViewLayoutParams.setTextAppearance(getActivity(), R.style.analytics_text_style);
        if(textViewLayoutParams.getText() == null || textViewLayoutParams.getText().toString().isEmpty()){
            return;
        }
        float value = Float.parseFloat(textViewLayoutParams.getText().toString());
            if(value < 20.0 && value>=0.0 ){
                textViewLayoutParams.setBackgroundColor(getResources().getColor(R.color.table_level_1));
            }else if(value>=20.0 && value< 40.0){
                textViewLayoutParams.setBackgroundColor(getResources().getColor(R.color.table_level_2));

            }else if(value>=40.0 && value< 60.0){
                textViewLayoutParams.setBackgroundColor(getResources().getColor(R.color.table_level_3));

            }else if(value>=60.0 && value< 80.0){
                textViewLayoutParams.setBackgroundColor(getResources().getColor(R.color.table_level_4));

            }else if(value>=80.0 && value <= 100.0){
                textViewLayoutParams.setBackgroundColor(getResources().getColor(R.color.table_level_5));

            }
        //TODO add focusable to textview
    }

    private void buildTestData(List<TestCoverage> testCoverages){
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
