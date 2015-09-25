package com.education.corsalite.activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.education.corsalite.R;
import com.education.corsalite.utils.Constants;

/**
 * Created by GIRISH on 13/09/15.
 */
public class VirtualCurrencyActivity extends AbstractBaseActivity {

    private Toolbar mToolbar;
    private ImageView redeemBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        initToolbar();
    }

    protected void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ico_actionbar_slidemenu));
        redeemBtn = (ImageView)findViewById(R.id.redeem_btn);
        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeem();
            }
        });
    }

    private void redeem() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra(LoginActivity.URL, Constants.REDEEM_URL);
        startActivity(intent);
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
