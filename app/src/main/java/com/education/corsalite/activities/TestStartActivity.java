package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.education.corsalite.R;
import com.education.corsalite.enums.Tests;
import com.education.corsalite.fragments.TestChapterFragment;
import com.education.corsalite.fragments.TestScheduledFragment;

import butterknife.ButterKnife;

public class TestStartActivity extends AbstractBaseActivity {

    public static final String KEY_TEST_TYPE = "key_test_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout myView = (FrameLayout) inflater.inflate(R.layout.activity_test_start, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        setToolbarForTestStartScreen();
        //Hide the toolbar button.
        toolbar.findViewById(R.id.start_btn).setVisibility(View.GONE);

        if (savedInstanceState == null) {
            int testType = getIntent().getIntExtra(KEY_TEST_TYPE, Tests.INVALID.getType());
            loadTest(testType);
        }
    }

    private void loadTest(int testType) {
        Tests test = Tests.getTest(testType);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (test) {
            case INVALID:
                finish();
                break;
            case CHAPTER:
                fragmentTransaction.add(R.id.fragment_container, TestChapterFragment.newInstance(getIntent().getExtras()), TestChapterFragment.getMyTag())
                        .commit();
                break;
            case SCHEDULED:
                fragmentTransaction.add(R.id.fragment_container, TestScheduledFragment.newInstance(getIntent().getExtras()), TestScheduledFragment.getMyTag())
                        .commit();
                break;
        }
    }
}
