package com.education.corsalite.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.education.corsalite.R;
import com.education.corsalite.fragments.VirtualCurrencyFragment;

/**
 * Created by GIRISH on 13/09/15.
 */
public class VirtualCurrencyActivity extends AbstractBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.ic_drawer);

        if(savedInstanceState == null) {
            VirtualCurrencyFragment fragment = new VirtualCurrencyFragment();
            fragment.
        }
    }

    private void initUI() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_currency, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
