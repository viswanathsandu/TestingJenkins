package com.education.corsalite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.activities.ChallengeActivity;


public class TestSetupFragment extends BaseFragment {

    private static final String ARG_CALLBACK = "ARG_CALLBACK";
    private ChallengeActivity.TestSetupCallback mTestSetupCallback;

    public TestSetupFragment() {
        // Required empty public constructor
    }

    public static TestSetupFragment newInstance(ChallengeActivity.TestSetupCallback mTestSetupCallback) {
        TestSetupFragment fragment = new TestSetupFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CALLBACK, mTestSetupCallback);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTestSetupCallback = (ChallengeActivity.TestSetupCallback) getArguments().getSerializable(ARG_CALLBACK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_setup, container, false);
    }


}
