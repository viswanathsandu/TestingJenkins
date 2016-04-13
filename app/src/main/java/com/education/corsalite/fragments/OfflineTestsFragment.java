package com.education.corsalite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.education.corsalite.R;
import com.education.corsalite.activities.OfflineContentActivity;
import com.education.corsalite.adapters.ExpandableListAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.models.OfflineTestModel;

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
    private List<OfflineTestModel> mockTestModels;
    private List<OfflineTestModel> scheduledTestModels;
    private List<OfflineTestModel> chaptersList;
    private List<OfflineTestModel> partTestList;
    private ArrayList<String> allTests;
    private HashMap<String, List<OfflineTestModel>> offlineTests;
    private  ExpandableListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_offline_test, container, false);
        ButterKnife.bind(this, v);
        rvOfflineTests.setGroupIndicator(null);
        offlineTests = new HashMap<>();
        allTests = loadAllTests();
        //loadOfflineTests();
        return v;
    }

    private void loadOfflineTests() {
        DbManager.getInstance(getActivity().getApplicationContext()).getAllOfflineMockTests(new ApiCallback<List<OfflineTestModel>>(getActivity()) {
            @Override
            public void success(List<OfflineTestModel> offlineTestModels, Response response) {
                super.success(offlineTestModels, response);
                if (offlineTestModels != null && !offlineTestModels.isEmpty()) {
                    separateTestModel(offlineTestModels);
                    offlineTests.put("Mock Test", mockTestModels);
                    offlineTests.put("Scheduled Test", scheduledTestModels);
                    offlineTests.put("Take Test", chaptersList);
                    offlineTests.put("Part Test", partTestList);
                    initNodes();
                } else {
                    // Show no tests layout
//                    Toast.makeText(getActivity(), "No offline test available ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void separateTestModel(List<OfflineTestModel> offlineTestModels){
        mockTestModels = new ArrayList<>();
        scheduledTestModels = new ArrayList<>();
        chaptersList = new ArrayList<>();
        partTestList = new ArrayList<>();
        for (OfflineTestModel model: offlineTestModels) {
            if(model.mockTest != null){
                mockTestModels.add(model);
            }else if(model.scheduledTest != null){
                scheduledTestModels.add(model);
            }else if(model.baseTest.chapter != null){
                chaptersList.add(model);
            }else if(model.baseTest.subjectName != null){
                partTestList.add(model);
            }
        }
    }

    private ArrayList<String>  loadAllTests() {
        ArrayList<String> alltests = new ArrayList<>();
        // alltests.add("Exercise Test");
        alltests.add("Take Test");
        alltests.add("Part Test");
        alltests.add("Scheduled Test");
        alltests.add("Mock Test");
        return alltests;
    }

    private void initNodes(){
        adapter = new ExpandableListAdapter(getActivity(),allTests,offlineTests);
        rvOfflineTests.setAdapter(adapter);
    }

    @Override
    public void onDeleteOfflineTest(int position, String tag) {
            if(tag.equalsIgnoreCase("Mock Test")){
               DbManager.getInstance(getActivity().getApplicationContext()).deleteOfflineMockTest(mockTestModels.get(position));
            }else  if(tag.equalsIgnoreCase("Scheduled Test")) {

            } else  if(tag.equalsIgnoreCase("Part Test")){

            }else  if(tag.equalsIgnoreCase("Exercise Test")){

            }

    }

    @Override
    public void onResume() {
        super.onResume();
       loadOfflineTests();
    }
}
