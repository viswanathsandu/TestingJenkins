package com.education.corsalite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.education.corsalite.R;
import com.education.corsalite.activities.OfflineActivity;
import com.education.corsalite.adapters.ExpandableListAdapter;
import com.education.corsalite.models.db.OfflineTestObjectModel;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.utils.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 28/11/15.
 */
public class OfflineTestsFragment extends BaseFragment implements OfflineActivity.IOfflineTestEventListener {
    @Bind(R.id.expandableList)
    ExpandableListView rvOfflineTests;
    @Bind(R.id.no_test_txt)
    View emptyTestsView;
    private List<OfflineTestObjectModel> mockTestModels;
    private List<OfflineTestObjectModel> scheduledTestModels;
    private List<OfflineTestObjectModel> chaptersList;
    private List<OfflineTestObjectModel> partTestList;
    private ArrayList<String> allTests;
    private HashMap<String, List<OfflineTestObjectModel>> offlineTests;
    private ExpandableListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_offline_test, container, false);
        ButterKnife.bind(this, v);
        if (getActivity() instanceof OfflineActivity) {
            ((OfflineActivity) getActivity()).setOfflineTestListener(this);
        }
        rvOfflineTests.setGroupIndicator(null);
        offlineTests = new HashMap<>();
        allTests = loadAllTests();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOfflineTests();
    }

    @Override
    public void onCourseIdSelected(Course selectedCourse) {
        loadOfflineTests();
    }

    private void loadOfflineTests() {
        try {
            List<OfflineTestObjectModel> offlineTestObjectModels = dbManager.fetchRecords(OfflineTestObjectModel.class);
            if (offlineTestObjectModels != null && !offlineTestObjectModels.isEmpty()) {
                emptyTestsView.setVisibility(View.GONE);
                separateTestModel(offlineTestObjectModels);
                offlineTests.put("Take Test", chaptersList);
                offlineTests.put("Part Test", partTestList);
                offlineTests.put("Scheduled Test", scheduledTestModels);
                offlineTests.put("Mock Test", mockTestModels);
                initNodes();
            } else {
                emptyTestsView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void separateTestModel(List<OfflineTestObjectModel> offlineTestObjectModels) {
        chaptersList = new ArrayList<>();
        partTestList = new ArrayList<>();
        scheduledTestModels = new ArrayList<>();
        mockTestModels = new ArrayList<>();
        for (OfflineTestObjectModel model : offlineTestObjectModels) {
            if (model != null && model.testType != null) {
                switch (model.testType) {
                    case CHAPTER:
                        chaptersList.add(model);
                        break;
                    case PART:
                        partTestList.add(model);
                        break;
                    case SCHEDULED:
                        scheduledTestModels.add(model);
                        break;
                    case MOCK:
                        mockTestModels.add(model);
                        break;
                }
            }
        }
    }

    private ArrayList<String> loadAllTests() {
        ArrayList<String> alltests = new ArrayList<>();
        alltests.add("Take Test");
        alltests.add("Part Test");
        alltests.add("Scheduled Test");
        alltests.add("Mock Test");
        return alltests;
    }

    private void initNodes() {
        adapter = new ExpandableListAdapter(getActivity(), allTests, offlineTests);
        rvOfflineTests.setAdapter(adapter);
    }

    @Override
    public void onDeleteOfflineTest(int position, String tag) {
        if (tag.equalsIgnoreCase("Mock Test")) {
            dbManager.deleteOfflineMockTest(mockTestModels.get(position));
        } else if (tag.equalsIgnoreCase("Scheduled Test")) {

        } else if (tag.equalsIgnoreCase("Part Test")) {

        } else if (tag.equalsIgnoreCase("Exercise Test")) {

        }
    }
}
