package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.education.corsalite.R;

import butterknife.ButterKnife;

public class TestStartActivity extends AbstractBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_test_start, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        setToolbarForTestStartScreen();
        toolbar.findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest();
            }
        });
    }

    private void startTest() {
        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
        finish();
    }
}
