package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.adapters.UserTabBaseAdapter;
import com.education.corsalite.fragments.UserProfileDetailsFragment;
import com.education.corsalite.models.responsemodels.ExamDetail;
import com.education.corsalite.models.responsemodels.VirtualCurrencyTransaction;
import com.education.corsalite.utils.WebUrls;

import java.util.List;

public class UserProfileActivity extends AbstractBaseActivity implements UserProfileDetailsFragment.UpdateExamData{

    private TabLayout userProfileLayout ;
    private ViewPager viewPager;
    private List<VirtualCurrencyTransaction> virtualCurrencyTransactions;
    private UserTabBaseAdapter userTabAdapter;
    public static String BALANCE_CURRENCY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_user_profile, null);
        frameLayout.addView(myView);
        initUI();
        setToolbarForProfile();
        sendAnalytics(getString(R.string.screen_profile));
    }

    private void initUI() {
        userProfileLayout = (TabLayout)findViewById(R.id.tl_userprofile);
        viewPager = (ViewPager)findViewById(R.id.pager);
    }

    private void setTabView(List<ExamDetail> examDetailList) {
        viewPager.setOffscreenPageLimit(2);
        if(userTabAdapter == null) {
            userTabAdapter = new UserTabBaseAdapter(getSupportFragmentManager(), examDetailList);
            viewPager.setAdapter(userTabAdapter);
        } else {
            userTabAdapter.updateExamDetailData(examDetailList);
        }
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
        switch (item.getItemId()) {
            case R.id.action_add_guardian :
                addGuardian();
                return true;
            case R.id.action_add_courses :
                addCourses();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addGuardian() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, getString(R.string.add_guardian));
        intent.putExtra(LoginActivity.URL, WebUrls.getAddGuardianUrl());
        startActivity(intent);
    }

    private void addCourses() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.TITLE, getString(R.string.add_course));
        intent.putExtra(LoginActivity.URL, WebUrls.getAddCoursesUrl());
        startActivity(intent);
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