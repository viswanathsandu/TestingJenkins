package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.ExerciseActivity;
import com.education.corsalite.activities.TestStartActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 13/01/16.
 *
 * @author Meeth D Jain
 */
public class ChapterTestSetupFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    @Bind(R.id.checkbox_adaptive_learning)
    CheckBox mAdaptiveLearningCheckbox;
    @Bind(R.id.edit_txt_chapter_test_setup_questions)
    EditText mNoOfQuestionsEditTxt;
    @Bind(R.id.spinner_chapter_level)
    Spinner mChapterLevelSpinner;

    private Bundle mExtras;
    private int mChapterLevel = 0;
    private boolean mIsAdaptiveLearningEnabled;
    private ArrayList<String> mChapterLevels;

    public static ChapterTestSetupFragment newInstance(Bundle bundle) {
        ChapterTestSetupFragment fragment = new ChapterTestSetupFragment();
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
        mChapterLevels = mExtras.getStringArrayList(TestStartActivity.EXTRAS_CHAPTER_LEVELS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chapter_test_setup, container, false);
        ButterKnife.bind(this, rootView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mChapterLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mChapterLevelSpinner.setAdapter(adapter);
        mChapterLevelSpinner.setOnItemSelectedListener(this);

        getDialog().setTitle("Test Option");
        getDialog().getWindow().setBackgroundDrawableResource(R.color.white);

        mAdaptiveLearningCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsAdaptiveLearningEnabled = isChecked;
                Toast.makeText(getActivity(), "Adaptive : " + mIsAdaptiveLearningEnabled, Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    @OnClick({R.id.btn_cancel, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel : {
                getDialog().dismiss();
                break;
            }
            case R.id.btn_next : {
                startActivity(ExerciseActivity.getMyIntent(getActivity(), mExtras));
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mChapterLevel = position;
        Toast.makeText(getActivity(), "Selected : " + mChapterLevel, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

