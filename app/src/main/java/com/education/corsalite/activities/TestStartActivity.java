package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.utils.Constants;

import butterknife.ButterKnife;

public class TestStartActivity extends AbstractBaseActivity {

    private String chapterName = "";

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
        loadDataFromIntent();
        setdata();
    }

    private void setdata() {
        TextView chapterNameTxt = ((TextView)findViewById(R.id.chapter_name_txt));
        chapterNameTxt.setText(chapterNameTxt.getText().toString()+chapterName);
    }

    private void loadDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        chapterName = bundle.getString(Constants.SELECTED_CHAPTER_NAME, "");
    }

    private void startTest() {
        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
        finish();
    }
}
