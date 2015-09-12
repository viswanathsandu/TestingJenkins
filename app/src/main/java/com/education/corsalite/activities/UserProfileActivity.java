package com.education.corsalite.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.education.corsalite.R;
import com.education.corsalite.adapters.UserTabBaseAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.fragments.UserProfileDetailsFragment;
import com.education.corsalite.responsemodels.CorsaliteError;
import com.education.corsalite.responsemodels.ExamDetail;
import com.education.corsalite.responsemodels.Message;
import com.education.corsalite.responsemodels.MessageResponse;
import com.education.corsalite.responsemodels.UserProfileResponse;
import com.education.corsalite.responsemodels.VirtualCurrencySummaryResponse;
import com.education.corsalite.responsemodels.VirtualCurrencyTransaction;
import com.education.corsalite.services.ApiClientService;

import java.util.List;

import retrofit.client.Response;

public class UserProfileActivity extends AbstractBaseActivity implements UserProfileDetailsFragment.UpdateExamData{

    TabLayout userProfileLayout ;
    ViewPager viewPager;
    List<ExamDetail> examDetails;
    List<Message> messages;
    List<VirtualCurrencyTransaction> virtualCurrencyTransactions;
    public static String BALANCE_CURRENCY;

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
}