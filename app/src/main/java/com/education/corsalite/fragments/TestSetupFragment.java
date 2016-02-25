package com.education.corsalite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ChallengeActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.FriendsData;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.client.Response;


public class TestSetupFragment extends BaseFragment {

    private static final String ARG_CALLBACK = "ARG_CALLBACK";
    private ChallengeActivity.TestSetupCallback mTestSetupCallback;
    @Bind(R.id.selectSubjectSpinner)
    public Spinner selectSubjSpinner;
    @Bind(R.id.selectChapterSpinner)
    public Spinner selectChapSpinner;
    @Bind(R.id.et_noOfQues)
    public EditText noOfQuesEdit;
    @Bind(R.id.et_timeInMins)
    public EditText timeInMinsEdit;
    @Bind(R.id.et_virCurrency)
    public EditText virtCurrencyEdit;
    @Bind(R.id.tv_title)
    public TextView titleTv;
    @Bind(R.id.tv_noOfQuesError)
    public TextView noOfQuesError;
    @Bind(R.id.tv_selectChapError)
    public TextView selectChapError;
    @Bind(R.id.tv_selectSubjError)
    public TextView selectSubjError;
    @Bind(R.id.tv_timeInMinsError)
    public TextView timeInMinsError;
    @Bind(R.id.tv_virCurrencyError)
    public TextView virCurrencyError;

    public TestSetupFragment() {
        // Required empty public constructor
    }

    public static TestSetupFragment newInstance(ChallengeActivity.TestSetupCallback mTestSetupCallback, ArrayList<FriendsData.Friends> selectedFriends) {
        TestSetupFragment fragment = new TestSetupFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CALLBACK, mTestSetupCallback);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTestSetupCallback = (ChallengeActivity.TestSetupCallback) getArguments().getSerializable(ARG_CALLBACK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_setup, container, false);
        ButterKnife.bind(this, view);

        loadContent();
        titleTv.setText(AbstractBaseActivity.selectedCourse.name.toString());

        return view;
    }

    @OnClick(R.id.tv_testsetup_next)
    public void onNextClick() {
        String selectedSubject = null, selectedChapter = null;
        if (selectSubjSpinner.getAdapter() != null) {
            selectedSubject = selectSubjSpinner.getSelectedItem().toString();
        }
        if (selectSubjSpinner.getAdapter() != null) {
            selectedChapter = selectChapSpinner.getSelectedItem().toString();
        }
        String noOfQuestions = noOfQuesEdit.getText().toString();
        String timeInMins = timeInMinsEdit.getText().toString();
        String virCurrency = virtCurrencyEdit.getText().toString();

        if (selectedSubject != null && !selectedSubject.isEmpty() && !selectedSubject.equalsIgnoreCase("Select Subject")) {
            selectSubjError.setVisibility(View.GONE);
            if (selectedChapter != null && !selectedChapter.isEmpty() && !selectedChapter.equalsIgnoreCase("Chapter")) {
                selectChapError.setVisibility(View.GONE);
                if (noOfQuestions != null && !noOfQuestions.isEmpty() && isValidNoQues(noOfQuestions)) {
                    noOfQuesError.setVisibility(View.GONE);
                    if (timeInMins != null && !timeInMins.isEmpty() && isValidTime(timeInMins)) {
                        timeInMinsError.setVisibility(View.GONE);
                        if (virCurrency != null && !virCurrency.isEmpty() && isValidCurrency(virCurrency)) {
                            virCurrencyError.setVisibility(View.GONE);
                            challengeTest();
                        } else {
                            virCurrencyError.setText("Enter Valid Currency");
                            virCurrencyError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        timeInMinsError.setText("Enter Valid time");
                        timeInMinsError.setVisibility(View.VISIBLE);
                    }
                } else {
                    noOfQuesError.setText("Enter valid no. of questions");
                    noOfQuesError.setVisibility(View.VISIBLE);
                }
            } else {
                selectChapError.setText("Please select chapter");
                selectChapError.setVisibility(View.VISIBLE);
            }
        } else {
            selectSubjError.setText("Please select subject");
            selectSubjError.setVisibility(View.VISIBLE);
        }
    }

    private void challengeTest() {

    }

    private boolean isValidNoQues(String noOfQuestions) {
        try {
            int quesCount =  Integer.parseInt(noOfQuestions);
            if (quesCount > 0) {
                return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidTime(String timeInMins) {
        try {
            int timeCount =  Integer.parseInt(timeInMins);
            if (timeCount > 0 && timeCount <= 60) {
                return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidCurrency(String virCurrency) {
        try {
            int currencyCount =  Integer.parseInt(virCurrency);
            if (currencyCount > 0) {
                return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnClick(R.id.tv_testsetup_cancel)
    public void onCancelClick() {
        mTestSetupCallback.popUpFriendsListFragment();
    }

    private void loadContent() {
        ApiManager.getInstance(getActivity()).getContentIndex(
                AbstractBaseActivity.selectedCourse.courseId.toString(), LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<List<ContentIndex>>(getActivity()) {

                    @Override
                    public void success(List<ContentIndex> contentIndexList, Response response) {
                        super.success(contentIndexList, response);
                        if (contentIndexList != null) {
                            showSubjects(contentIndexList);
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        ((AbstractBaseActivity) getActivity()).showToast("No data available");
                    }
                });
    }

    private void showSubjects(final List<ContentIndex> contentIndexList) {
        List<SubjectModel> subjectsList = new ArrayList<>();
        for (int i = 0; i < contentIndexList.size(); i++) {
            if (contentIndexList.get(i).idCourse.equalsIgnoreCase(AbstractBaseActivity.selectedCourse.courseId.toString())) {
                subjectsList = contentIndexList.get(i).subjectModelList;
            }
        }
        selectSubjSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.challenge_spinner_item, R.id.mock_test_txt, getSubjects(subjectsList)));
        final List<SubjectModel> finalSubjectsList = subjectsList;
        selectSubjSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (adapterView.getItemAtPosition(position).toString().equalsIgnoreCase("Select Subject")) {
                    selectChapSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.challenge_spinner_item, R.id.mock_test_txt, getChapters(null)));
                } else {
                    selectSubjError.setVisibility(View.GONE);
                    for (int i = 0; i < finalSubjectsList.size(); i++) {
                        if (finalSubjectsList.get(i).subjectName.equalsIgnoreCase(adapterView.getItemAtPosition(position).toString())) {
                            List<ChapterModel> chaptersList = finalSubjectsList.get(i).chapters;
                            selectChapSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.challenge_spinner_item, R.id.mock_test_txt, getChapters(chaptersList)));
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        selectChapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (adapterView.getItemAtPosition(position).toString().equalsIgnoreCase("Chapter")) {
                } else {
                    selectChapError.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private List<String> getSubjects(List<SubjectModel> subjectModelList) {
        List<String> strings = new ArrayList<>();
        strings.add("Select Subject");
        if (subjectModelList != null && subjectModelList.size() > 0) {
            for (int i = 0; i < subjectModelList.size(); i++) {
                strings.add(subjectModelList.get(i).subjectName);
            }
        }
        return strings;
    }

    private List<String> getChapters(List<ChapterModel> chapterModelList) {
        List<String> strings = new ArrayList<>();
        strings.add("Chapter");
        if (chapterModelList != null && chapterModelList.size() > 0) {
            for (int i = 0; i < chapterModelList.size(); i++) {
                strings.add(chapterModelList.get(i).chapterName);
            }
        }
        return strings;
    }

}
