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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.education.corsalite.R;
import com.education.corsalite.adapters.UserTabBaseAdapter;
import com.education.corsalite.fragments.UserProfileDetailsFragment;
import com.education.corsalite.models.db.CourseList;
import com.education.corsalite.models.responsemodels.ExamDetail;
import com.education.corsalite.models.responsemodels.Message;
import com.education.corsalite.models.responsemodels.VirtualCurrencyTransaction;
import com.education.corsalite.utils.Constants;

import java.util.List;

public class UserProfileActivity extends AbstractBaseActivity implements UserProfileDetailsFragment.UpdateExamData{

    TabLayout userProfileLayout ;
    ViewPager viewPager;
    Spinner coursesSpinner;
    List<ExamDetail> examDetails;
    List<Message> messages;
    List<VirtualCurrencyTransaction> virtualCurrencyTransactions;
    UserTabBaseAdapter userTabAdapter;
    public static String BALANCE_CURRENCY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_user_profile, null);
        frameLayout.addView(myView);
        initUI();
    }

    private void initUI() {
        userProfileLayout = (TabLayout)findViewById(R.id.tl_userprofile);
        viewPager = (ViewPager)findViewById(R.id.pager);
        coursesSpinner = (Spinner)findViewById(R.id.spinner_nav);
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

    public void showCoursesInToolbar(CourseList courseList) {
        if(courseList != null) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_title_textview, courseList.courses);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            coursesSpinner.setAdapter(dataAdapter);
            coursesSpinner.setSelection(courseList.defaultCourseIndex);
        }
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
            case R.id.analytics:
                Intent i = new Intent(UserProfileActivity.this,AnalyticsActivity.class);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void addGuardian() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.URL, Constants.ADD_GUARDIAN_URL);
        startActivity(intent);
    }

    private void addCourses() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.URL, Constants.ADD_COURSES_URL);
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