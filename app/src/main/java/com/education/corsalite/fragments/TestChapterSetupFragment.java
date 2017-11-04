package com.education.corsalite.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.responsemodels.Chapter;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.services.TestDownloadService;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
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
public class TestChapterSetupFragment extends DialogFragment
        implements AdapterView.OnItemSelectedListener {

    public static final String EXTRAS_CHAPTER_LEVELS = "key_chapter_levels";

    @Bind(R.id.checkbox_adaptive_learning)
    CheckBox mAdaptiveLearningCheckbox;
    @Bind(R.id.levels_layout)
    LinearLayout levelsLayout;
    @Bind(R.id.btn_download)
    Button downloadButton;
    @Bind(R.id.question_selection_spinner)
    AppCompatSpinner questionSelectionSpinner;

    private Bundle mExtras;
    private boolean mIsAdaptiveLearningEnabled;
    private ArrayList<String> mChapterLevels;
    private List<TestCoverage> testCoverages;
    private Chapter chapter;
    private String subjectId;
    private int levelCrossed;
    private int questionCount;
    private Integer maxQuestionLimit;
    ArrayAdapter<Integer> questionAdapter;


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
        maxQuestionLimit = mExtras.getInt(Constants.QUESTIONS_COUNT, -1);
        if (chapterStr != null) {
            chapter = Gson.get().fromJson(chapterStr, Chapter.class);
        }
        if (chapter != null && TextUtils.isEmpty(chapter.idCourseSubjectChapter)) {
            chapter.idCourseSubjectChapter = mExtras.getString(Constants.SELECTED_CHAPTERID);
        }
        String testCoveragesGson = mExtras.getString(Constants.TEST_COVERAGE_LIST_GSON);
        if (!TextUtils.isEmpty(testCoveragesGson)) {
            testCoverages = Gson.get().fromJson(testCoveragesGson, new TypeToken<List<TestCoverage>>() {
            }.getType());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_chapter_test_setup, container, false);
        ButterKnife.bind(this, rootView);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        questionAdapter = new ArrayAdapter<Integer>(getActivity(), R.layout.number_dropdown_item, new ArrayList<Integer>());
        questionSelectionSpinner.setAdapter(questionAdapter);
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

    @Override
    public void onPause() {
        if (getActivity() != null && getActivity() instanceof AbstractBaseActivity) {
            ((AbstractBaseActivity) getActivity()).hideKeyboard();
        }
        super.onPause();
    }

    private void loadLevels() {
        try {
            for (final String level : mChapterLevels) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.level_checkbox, null);
                TextView txt = (TextView) view.findViewById(R.id.level_txt);
                txt.setText("Level " + level);
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.setTag("Level " + level);
                if (Integer.parseInt(level) <= levelCrossed) {
                    checkBox.setChecked(true);
                    if (testCoverages != null) {
                        for (TestCoverage coverage : testCoverages) {
                            if (coverage.level.equalsIgnoreCase(level + "")) {
                                questionCount += Integer.valueOf(coverage.questionCount);
                                questionAdapter.clear();
                                questionAdapter.addAll(getListTill(1, getMaxQuestionCount(questionCount)));
                                questionAdapter.notifyDataSetChanged();
                                questionSelectionSpinner.setSelection(questionAdapter.getCount() - 1);
                                break;
                            }
                        }
                    }
                } else if (Integer.valueOf(level) > levelCrossed) {
                    checkBox.setEnabled(false);
                }
                getDialog().setTitle("Test Option");

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
                        if (testCoverage != null && checkBox.isEnabled()) {
                            if (isChecked) {
                                questionAdapter.clear();
                                questionCount += Integer.valueOf(testCoverage.questionCount);
                            } else {
                                questionCount -= Integer.valueOf(testCoverage.questionCount);
                            }
                            questionAdapter.clear();
                            questionAdapter.addAll(getListTill(1, getMaxQuestionCount(questionCount)));
                            questionAdapter.notifyDataSetChanged();
                            questionSelectionSpinner.setSelection(questionAdapter.getCount() - 1);
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
        if (getActivity() != null && getActivity() instanceof AbstractBaseActivity) {
            ((AbstractBaseActivity) getActivity()).hideKeyboard();
        }
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
                    Integer count = Integer.parseInt(coverage.questionCount);
                    int maxCount = maxQuestionLimit < count ? maxQuestionLimit : count;
                    questionAdapter.clear();
                    questionAdapter.addAll(getListTill(1, maxCount));
                    questionAdapter.notifyDataSetChanged();
                    questionSelectionSpinner.setSelection(questionAdapter.getPosition(maxCount));
                    break;
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void requestQuestionPaperDetails() {

        if (questionAdapter.isEmpty()) {
            Toast.makeText(getActivity(), "Please select the number of questionsor the level", Toast.LENGTH_SHORT).show();
            return;
        }
        mExtras.putBoolean(Constants.ADAPIVE_LEAERNING, mIsAdaptiveLearningEnabled);
        mExtras.putString(Constants.QUESTIONS_COUNT, questionSelectionSpinner.getSelectedItem().toString());
        startActivity(ExamEngineActivity.getMyIntent(getActivity(), mExtras));
        getActivity().finish();
    }

    private void downloadTakeTest(Chapter chapter) {

        if (questionAdapter.isEmpty()) {
            Toast.makeText(getActivity(), "Please select the number of questionsor the level", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent exerciseIntent = new Intent(getActivity(), TestDownloadService.class);
        exerciseIntent.putExtra("subjectId", subjectId);
        exerciseIntent.putExtra("chapterId", chapter.idCourseSubjectChapter);
        exerciseIntent.putExtra("selectedTakeTest", Gson.get().toJson(chapter));
        exerciseIntent.putExtra("courseId", AbstractBaseActivity.getSelectedCourseId());
        exerciseIntent.putExtra("questions_count", questionSelectionSpinner.getSelectedItem().toString());
        exerciseIntent.putExtra("entityId", LoginUserCache.getInstance().getEntityId());
        getActivity().startService(exerciseIntent);
        Toast.makeText(getActivity(), "Downloading test paper in background", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    private int getMaxQuestionCount(int questionCount) {
        return (maxQuestionLimit > 0 && maxQuestionLimit < questionCount) ? maxQuestionLimit : questionCount;
    }

    private List<Integer> getListTill(int min, int max) {
        List<Integer> items = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            items.add(i);
        }
        return items;
    }
}

