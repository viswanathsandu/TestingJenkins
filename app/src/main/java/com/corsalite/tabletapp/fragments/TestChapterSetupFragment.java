package com.corsalite.tabletapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.activities.AbstractBaseActivity;
import com.corsalite.tabletapp.activities.ExamEngineActivity;
import com.corsalite.tabletapp.cache.LoginUserCache;
import com.corsalite.tabletapp.models.responsemodels.Chapter;
import com.corsalite.tabletapp.models.responsemodels.TestCoverage;
import com.corsalite.tabletapp.services.TestDownloadService;
import com.corsalite.tabletapp.utils.Constants;
import com.corsalite.tabletapp.utils.L;
import com.corsalite.tabletapp.views.InputFilterMinMax;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 13/01/16.
 *
 * @author Meeth D Jain
 */
public class TestChapterSetupFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public static final String EXTRAS_CHAPTER_LEVELS = "key_chapter_levels";

    @Bind(R.id.checkbox_adaptive_learning)
    CheckBox mAdaptiveLearningCheckbox;
    @Bind(R.id.edit_txt_chapter_test_setup_questions)
    EditText mNoOfQuestionsEditTxt;
    @Bind(R.id.levels_layout)
    LinearLayout levelsLayout;
    @Bind(R.id.btn_download)
    Button downloadButton;

    private Bundle mExtras;
    private boolean mIsAdaptiveLearningEnabled;
    private ArrayList<String> mChapterLevels;
    private List<TestCoverage> testCoverages;
    private Chapter chapter;
    private String subjectId;
    private int levelCrossed;
    private int questionCount;

    public static TestChapterSetupFragment newInstance(Bundle bundle) {
        TestChapterSetupFragment fragment = new TestChapterSetupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static String getMyTag() {
        return "tag_fragment_chapter_test_setup";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExtras = getArguments();
        mChapterLevels = mExtras.getStringArrayList(EXTRAS_CHAPTER_LEVELS);
        subjectId = mExtras.getString(Constants.SELECTED_SUBJECTID, "");
        String chapterStr = mExtras.getString("chapter");
        levelCrossed = mExtras.getInt(Constants.LEVEL_CROSSED, 0);
        if (chapterStr != null) {
            chapter = new Gson().fromJson(chapterStr, Chapter.class);
        }
        String testCoveragesGson = mExtras.getString(Constants.TEST_COVERAGE_LIST_GSON);
        if (!TextUtils.isEmpty(testCoveragesGson)) {
            testCoverages = new Gson().fromJson(testCoveragesGson, new TypeToken<List<TestCoverage>>() {
            }.getType());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_chapter_test_setup, container, false);
        ButterKnife.bind(this, rootView);
        loadLevels();
        getDialog().setTitle("Test Option");
        getDialog().getWindow().setBackgroundDrawableResource(R.color.white);
        mAdaptiveLearningCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsAdaptiveLearningEnabled = isChecked;
                downloadButton.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });
        return rootView;
    }

    private void loadLevels() {
        try {
            for (final String level : mChapterLevels) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.level_checkbox, null);
                TextView txt = (TextView) view.findViewById(R.id.level_txt);
                txt.setText("Level " + level);
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.setTag("Level " + level);
                if (level.equals(levelCrossed + "")) {
                    checkBox.setChecked(true);
                    if (testCoverages != null) {
                        for (TestCoverage coverage : testCoverages) {
                            if (coverage.level.equalsIgnoreCase(level + "")) {
                                questionCount += Integer.valueOf(coverage.questionCount);
                                mNoOfQuestionsEditTxt.setFilters(new InputFilter[]{new InputFilterMinMax("1", questionCount+"")});
                                mNoOfQuestionsEditTxt.setText(questionCount+"");
                                break;
                            }
                        }
                    }
                } else if (Integer.valueOf(level) > levelCrossed) {
                    checkBox.setEnabled(false);
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TestCoverage testCoverage = null;
                        if (testCoverages != null) {
                            for (TestCoverage coverage : testCoverages) {
                                if (coverage.level.equalsIgnoreCase(level + "")) {
                                    testCoverage = coverage;
                                }
                            }
                        }
                        if(testCoverage != null && checkBox.isEnabled()) {
                            if (isChecked) {
                                questionCount += Integer.valueOf(testCoverage.questionCount);
                            } else {
                                questionCount -= Integer.valueOf(testCoverage.questionCount);
                            }
                            mNoOfQuestionsEditTxt.setFilters(new InputFilter[]{new InputFilterMinMax("1", questionCount+"")});
                            mNoOfQuestionsEditTxt.setText(questionCount + "");
                        }
                    }
                });
                levelsLayout.addView(view);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    @OnClick({R.id.btn_cancel, R.id.btn_download, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                getDialog().dismiss();
                break;
            case R.id.btn_download:
                downloadTakeTest(chapter);
                break;
            case R.id.btn_next:
                requestQuestionPaperDetails();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String chapterLevel = mChapterLevels.get(position);
        if (testCoverages != null) {
            for (TestCoverage coverage : testCoverages) {
                if (coverage.level.equalsIgnoreCase(chapterLevel + "")) {
                    mNoOfQuestionsEditTxt.setText(coverage.questionCount);
                    break;
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void requestQuestionPaperDetails() {
        String noOfQuestions = mNoOfQuestionsEditTxt.getText().toString();
        if (TextUtils.isEmpty(noOfQuestions) || !TextUtils.isDigitsOnly(noOfQuestions)) {
            Toast.makeText(getActivity(), "Please select the number of questions", Toast.LENGTH_SHORT).show();
            return;
        } else if(questionCount == 0) {
            Toast.makeText(getActivity(), "Please select the level", Toast.LENGTH_SHORT).show();
            return;
        }
        mExtras.putBoolean(Constants.ADAPIVE_LEAERNING, mIsAdaptiveLearningEnabled);
        mExtras.putString(Constants.QUESTIONS_COUNT, noOfQuestions);
        startActivity(ExamEngineActivity.getMyIntent(getActivity(), mExtras));
        getActivity().finish();
    }

    private void downloadTakeTest(Chapter chapter) {
        String noOfQuestions = mNoOfQuestionsEditTxt.getText().toString();
        if (TextUtils.isEmpty(noOfQuestions) || !TextUtils.isDigitsOnly(noOfQuestions)) {
            Toast.makeText(getActivity(), "Please select the number of questions", Toast.LENGTH_SHORT).show();
            return;
        } else if(questionCount == 0) {
            Toast.makeText(getActivity(), "Please select the level", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent exerciseIntent = new Intent(getActivity(), TestDownloadService.class);
        exerciseIntent.putExtra("subjectId", subjectId);
        exerciseIntent.putExtra("chapterId", chapter.idCourseSubjectchapter);
        exerciseIntent.putExtra("selectedTakeTest", new Gson().toJson(chapter));
        exerciseIntent.putExtra("courseId", AbstractBaseActivity.selectedCourse.courseId.toString());
        if (!TextUtils.isEmpty(noOfQuestions) && TextUtils.isDigitsOnly(noOfQuestions)) {
            exerciseIntent.putExtra("questions_count", noOfQuestions);
        }
        exerciseIntent.putExtra("entityId", LoginUserCache.getInstance().loginResponse.entitiyId);
        getActivity().startService(exerciseIntent);
        Toast.makeText(getActivity(), "Downloading test paper in background", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }
}

