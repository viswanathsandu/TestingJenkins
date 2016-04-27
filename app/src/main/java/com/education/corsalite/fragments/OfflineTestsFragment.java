package com.education.corsalite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.OfflineActivity;
import com.education.corsalite.adapters.ExpandableListAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.models.OfflineTestModel;
import com.education.corsalite.models.responsemodels.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 28/11/15.
 */
public class OfflineTestsFragment extends BaseFragment implements OfflineActivity.IOfflineTestEventListener {
    @Bind(R.id.expandableList)
    ExpandableListView rvOfflineTests;
    @Bind(R.id.no_test_txt)
    View emptyTestsView;
    private List<OfflineTestModel> mockTestModels;
    private List<OfflineTestModel> scheduledTestModels;
    private List<OfflineTestModel> chaptersList;
    private List<OfflineTestModel> partTestList;
    private ArrayList<String> allTests;
    private HashMap<String, List<OfflineTestModel>> offlineTests;
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
        DbManager.getInstance(getActivity().getApplicationContext()).getAllOfflineMockTests(AbstractBaseActivity.selectedCourse.courseId + "",
                new ApiCallback<List<OfflineTestModel>>(getActivity()) {
                    @Override
                    public void success(List<OfflineTestModel> offlineTestModels, Response response) {
                        super.success(offlineTestModels, response);
                        if (offlineTestModels != null && !offlineTestModels.isEmpty()) {
                            emptyTestsView.setVisibility(View.GONE);
                            separateTestModel(offlineTestModels);
                            offlineTests.put("Mock Test", mockTestModels);
                            offlineTests.put("Scheduled Test", scheduledTestModels);
                            offlineTests.put("Take Test", chaptersList);
                            offlineTests.put("Part Test", partTestList);
                            initNodes();
                        } else {
                            emptyTestsView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void separateTestModel(List<OfflineTestModel> offlineTestModels) {
        mockTestModels = new ArrayList<>();
        scheduledTestModels = new ArrayList<>();
        chaptersList = new ArrayList<>();
        partTestList = new ArrayList<>();
        for (OfflineTestModel model : offlineTestModels) {
            if (model != null && model.testType != null) {
                switch (model.testType) {
                    case MOCK:
                        mockTestModels.add(model);
                        break;
                    case SCHEDULED:
                        scheduledTestModels.add(model);
                        break;
                    case CHAPTER:
                        chaptersList.add(model);
                        break;
                    case PART:
                        partTestList.add(model);
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
            DbManager.getInstance(getActivity().getApplicationContext()).deleteOfflineMockTest(mockTestModels.get(position));
        } else if (tag.equalsIgnoreCase("Scheduled Test")) {

        } else if (tag.equalsIgnoreCase("Part Test")) {

        } else if (tag.equalsIgnoreCase("Exercise Test")) {

        }
    }
}
