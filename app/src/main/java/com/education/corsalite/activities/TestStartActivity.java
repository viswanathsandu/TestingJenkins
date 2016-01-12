package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class TestStartActivity extends AbstractBaseActivity {

    @Bind(R.id.ll_container)
    LinearLayout mContainerLayout;
    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;
    @Bind(R.id.txt_view_test_start_chapter_name)
    TextView mChapterNameTxtView;
    @Bind(R.id.txt_view_test_start_note)
    TextView mNoteTxtView;

    private String chapterID, chapterName, subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_test_start, null);
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
        setData();
        fetchDataFromServer();
    }

    private void fetchDataFromServer() {
        ApiManager.getInstance(this).getTestCoverage(LoginUserCache.getInstance().loginResponse.studentId, AbstractBaseActivity.selectedCourse.courseId.toString(), subjectId, chapterID,
                new ApiCallback<List<TestCoverage>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error(error.message);
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void success(List<TestCoverage> testCoverages, Response response) {
                        super.success(testCoverages, response);
                        mProgressBar.setVisibility(View.GONE);
                        mContainerLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void loadDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        chapterID = bundle.getString(Constants.SELECTED_CHAPTERID, "");
        chapterName = bundle.getString(Constants.SELECTED_CHAPTER_NAME, "");
        subjectId = bundle.getString(Constants.SELECTED_SUBJECTID, "");
        if (TextUtils.isEmpty(chapterID) || TextUtils.isEmpty(chapterName) || TextUtils.isEmpty(subjectId)) {
            //In case data is missing finish this activity,
            finish();
        }
    }

    private void setData() {
        mChapterNameTxtView.setText(getString(R.string.value_test_chapter_name, chapterName));
        mNoteTxtView.setText(getString(R.string.value_test_note));
    }

    private void startTest() {
        startActivity(ExerciseActivity.getMyIntent(this, getIntent().getExtras()));
        finish();
    }
}
