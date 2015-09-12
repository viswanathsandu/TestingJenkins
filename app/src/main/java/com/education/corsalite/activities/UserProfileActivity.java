package com.education.corsalite.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.education.corsalite.R;
import com.education.corsalite.adapters.UserTabFragmentAdapter;

public class UserProfileActivity extends AbstractBaseActivity {

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
        setTabView();
    }

    private void initUI() {
        userProfileLayout = (TabLayout)findViewById(R.id.tl_userprofile);
        viewPager = (ViewPager)findViewById(R.id.pager);
    }

    private void setTabView() {

        viewPager.setAdapter(new UserTabFragmentAdapter(getSupportFragmentManager()));
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
}
