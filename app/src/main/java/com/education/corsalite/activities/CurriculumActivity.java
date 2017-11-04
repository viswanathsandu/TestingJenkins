package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.adapters.CurriculumPagerAdapter;
import com.education.corsalite.models.responsemodels.Course;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CurriculumActivity extends AbstractBaseActivity {
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    private CurriculumPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_curriculum, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        setToolbarForCurriculumScreen();
        //set adapter to your ViewPager
        pagerAdapter = new CurriculumPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
        refreshData();
    }

    public void refreshData() {
        pagerAdapter.notifyDataSetChanged();
        mViewPager.invalidate();
    }
}
