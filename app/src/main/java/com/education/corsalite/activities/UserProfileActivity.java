package com.education.corsalite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.education.corsalite.R;
import com.education.corsalite.adapters.UserTabBaseAdapter;
import com.education.corsalite.fragments.UserProfileDetailsFragment;
import com.education.corsalite.models.responsemodels.ExamDetail;
import com.education.corsalite.models.responsemodels.Message;
import com.education.corsalite.models.responsemodels.VirtualCurrencyTransaction;

import java.util.List;

public class UserProfileActivity extends AbstractBaseActivity implements UserProfileDetailsFragment.UpdateExamData{

    TabLayout userProfileLayout ;
    ViewPager viewPager;
    List<ExamDetail> examDetails;
    List<Message> messages;
    List<VirtualCurrencyTransaction> virtualCurrencyTransactions;
    public static String BALANCE_CURRENCY;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initUI();
        initToolbar();
    }

    private void initUI() {
        userProfileLayout = (TabLayout)findViewById(R.id.tl_userprofile);
        viewPager = (ViewPager)findViewById(R.id.pager);
    }

    protected void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ico_actionbar_slidemenu));
    }


    private void setTabView(List<ExamDetail> examDetailList) {
        viewPager.setAdapter(new UserTabBaseAdapter(getSupportFragmentManager(), examDetailList));
        userProfileLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.plus_menu, menu);
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