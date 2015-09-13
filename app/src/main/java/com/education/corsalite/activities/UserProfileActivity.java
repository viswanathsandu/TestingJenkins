package com.education.corsalite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.education.corsalite.R;
import com.education.corsalite.adapters.UserTabBaseAdapter;
import com.education.corsalite.fragments.UserProfileDetailsFragment;
import com.education.corsalite.responsemodels.ExamDetail;

import java.util.List;

public class UserProfileActivity extends AbstractBaseActivity
        implements UserProfileDetailsFragment.UpdateExamData{

    TabLayout userProfileLayout ;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.ic_drawer);
        initUI();
    }

    private void initUI() {
        userProfileLayout = (TabLayout)findViewById(R.id.tl_userprofile);
        viewPager = (ViewPager)findViewById(R.id.pager);
    }

    private void setTabView(List<ExamDetail> examDetailList) {
        viewPager.setAdapter(new UserTabBaseAdapter(getSupportFragmentManager(), examDetailList));
        userProfileLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getExamData(List<ExamDetail> examDetailList) {
        setTabView(examDetailList);
    }

    public void onClickIntent(View view) {
        Intent intent = new Intent(this, VirtualCurrencyActivity.class);
        startActivity(intent);
    }
}
