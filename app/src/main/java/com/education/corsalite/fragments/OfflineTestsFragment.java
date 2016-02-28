package com.education.corsalite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.OfflineContentActivity;
import com.education.corsalite.adapters.ExpandableListAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.models.OfflineMockTestModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 28/11/15.
 */
public class OfflineTestsFragment  extends BaseFragment implements OfflineContentActivity.IOfflineTestEventListener {
    @Bind(R.id.expandableList)ExpandableListView rvOfflineTests;
    private List<OfflineMockTestModel> mockTestModels;
    private List<OfflineMockTestModel> scheduledTestModels;
    private ArrayList<String> allTests;
    private HashMap<String, List<OfflineMockTestModel>> offlineTests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_offline_test, container, false);
        ButterKnife.bind(this, v);
        rvOfflineTests.setGroupIndicator(null);
        offlineTests = new HashMap<>();
        allTests = loadAllTests();
        loadOfflineTests();
        return v;
    }

    private void loadOfflineTests() {
        DbManager.getInstance(getActivity()).getAllOfflineMockTests(new ApiCallback<List<OfflineMockTestModel>>(getActivity()) {
            @Override
            public void success(List<OfflineMockTestModel> offlineMockTestModels, Response response) {
                super.success(offlineMockTestModels, response);
                if (offlineMockTestModels != null && !offlineMockTestModels.isEmpty()) {
                    separateTestModel(offlineMockTestModels);
                    offlineTests.put("Mock Test", mockTestModels);
                    offlineTests.put("Scheduled Test", scheduledTestModels);
                    initNodes();
                } else {
                    Toast.makeText(getActivity(), "No offline test available ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void separateTestModel(List<OfflineMockTestModel> offlineMockTestModels){
        List<OfflineMockTestModel> list = new ArrayList<>();
        mockTestModels = new ArrayList<>();
        scheduledTestModels = new ArrayList<>();
        for (OfflineMockTestModel model: offlineMockTestModels) {
            if(model.mockTest != null){
                mockTestModels.add(model);
            }else if(model.scheduledTest != null){
                scheduledTestModels.add(model);
            }
        }
    }

    private ArrayList<String>  loadAllTests()
    {
        ArrayList<String> alltests = new ArrayList<>();
        alltests.add("Exercise Test");
        alltests.add("Part Test");
        alltests.add("Scheduled Test");
        alltests.add("Mock Test");
        return alltests;
    }

    private void initNodes(){

        ExpandableListAdapter adapter = new ExpandableListAdapter(getActivity(),allTests,offlineTests);
        rvOfflineTests.setAdapter(adapter);
    }

    @Override
    public void onDeleteOfflineTest(int position, String tag) {
            if(tag.equalsIgnoreCase("Mock Test")){
               DbManager.getInstance(getActivity()).deleteOfflineMockTest(mockTestModels.get(position));
            }else  if(tag.equalsIgnoreCase("Scheduled Test")) {

            } else  if(tag.equalsIgnoreCase("Part Test")){

            }else  if(tag.equalsIgnoreCase("Exercise Test")){

            }

    }
}
