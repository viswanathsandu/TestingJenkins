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
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ChallengeActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.requestmodels.ChallengeFriend;
import com.education.corsalite.models.requestmodels.CreateChallengeRequest;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.CreateChallengeResponseModel;
import com.education.corsalite.models.responsemodels.Exam;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.client.Response;


public class TestSetupFragment extends BaseFragment {

    private static final String ARG_CALLBACK = "ARG_CALLBACK";
    private ChallengeActivity.TestSetupCallback mTestSetupCallback;
    @Bind(R.id.selectSubjectSpinner)
    public Spinner selectSubjSpinner;
    @Bind(R.id.selectChapterSpinner)
    public Spinner selectChapSpinner;
    @Bind(R.id.examsSpinner)
    public Spinner examsSpinner;
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
    private List<SubjectModel> subjectList;
    private List<ChapterModel> chapterList;
    private List<Exam> examsList;

    public TestSetupFragment() {
        // Required empty public constructor
    }

    public static TestSetupFragment newInstance(ChallengeActivity.TestSetupCallback mTestSetupCallback) {
        TestSetupFragment fragment = new TestSetupFragment();
        Bundle args = new Bundle();
        fragment.mTestSetupCallback = mTestSetupCallback;
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
        loadExams();
        titleTv.setText(AbstractBaseActivity.getSelectedCourseName());
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
        CreateChallengeRequest request = new CreateChallengeRequest();
        request.subjectId = subjectList.get(selectSubjSpinner.getSelectedItemPosition() - 1).idSubject;
        request.chapterId = chapterList.get(selectChapSpinner.getSelectedItemPosition() - 1).idChapter;
        request.courseId = AbstractBaseActivity.getSelectedCourseId();
        int durationInMins = 0;
        try {
            durationInMins = Integer.parseInt(timeInMinsEdit.getText().toString());
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        request.durationInSeconds = String.valueOf(durationInMins * 60);
        request.questionCount = noOfQuesEdit.getText().toString();
        request.virtualCurrencyChallenged = virtCurrencyEdit.getText().toString();
        if(examsSpinner.getSelectedItemPosition() == 0) {
            showToast("Please select the exam");
            return;
        }
        request.examId = examsList.get(examsSpinner.getSelectedItemPosition() - 1).examId;
        request.studentId = LoginUserCache.getInstance().getStudentId();
        ChallengeFriend friend = new ChallengeFriend();
        List<FriendsData.Friend> selectedFriends = ((ChallengeActivity)getActivity()).selectedFriends;
        if(selectedFriends != null) {
            if(selectedFriends.size() > 0) {
                friend.friend1 = selectedFriends.get(0).idStudent;
            }
            if(selectedFriends.size() > 1) {
                friend.friend2 = selectedFriends.get(1).idStudent;
            }
            if(selectedFriends.size() > 2) {
                friend.friend3 = selectedFriends.get(2).idStudent;
            }
            if(selectedFriends.size() > 3) {
                friend.friend4 = selectedFriends.get(3).idStudent;
            }
        }
        request.challengedFriendsId = friend;
        showProgress();
        ApiManager.getInstance(getActivity()).createChallenge(request, new ApiCallback<CreateChallengeResponseModel>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                closeProgress();
                Toast.makeText(getActivity(), "Failed to create test", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(CreateChallengeResponseModel createChallengeResponseModel, Response response) {
                super.success(createChallengeResponseModel, response);
                closeProgress();
                if(createChallengeResponseModel != null && createChallengeResponseModel.testQuestionPaperId != null) {
                    EventBus.getDefault().post(createChallengeResponseModel);
                }
            }
        });
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
        if(mTestSetupCallback != null) {
            mTestSetupCallback.popUpFriendsListFragment();
        }
    }

    private void loadContent() {
        showProgress();
        ApiManager.getInstance(getActivity()).getContentIndex(
                AbstractBaseActivity.getSelectedCourseId(), LoginUserCache.getInstance().getStudentId(),
                new ApiCallback<List<ContentIndex>>(getActivity()) {

                    @Override
                    public void success(List<ContentIndex> contentIndexList, Response response) {
                        super.success(contentIndexList, response);
                        closeProgress();
                        if (contentIndexList != null) {
                            showSubjects(contentIndexList);
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        ((AbstractBaseActivity) getActivity()).showToast("No data available");
                    }
                });
    }

    private void loadExams() {
        ApiManager.getInstance(getActivity()).getStandardExamsByCourse(AbstractBaseActivity.getSelectedCourseId(), LoginUserCache.getInstance().getEntityId(),
                new ApiCallback<List<Exam>>(getActivity()) {
                    @Override
                    public void success(List<Exam> exams, Response response) {
                        super.success(exams, response);
                        if(exams != null) {
                            examsList = exams;
                            showExams();
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                    }
                });
    }

    private void showExams() {
        examsSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.challenge_spinner_item, R.id.mock_test_txt, getExams(examsList)));
        selectSubjSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    if (position > 0) {
                        L.info("Selected exam : " + examsList.get(position - 1).examId + " - " + examsList.get(position - 1).examName);
                    }
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }


    private void showSubjects(final List<ContentIndex> contentIndexList) {
        for (int i = 0; i < contentIndexList.size(); i++) {
            if (contentIndexList.get(i).idCourse.equalsIgnoreCase(AbstractBaseActivity.getSelectedCourseId())) {
                subjectList = contentIndexList.get(i).subjectModelList;
            }
        }
        selectSubjSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.challenge_spinner_item, R.id.mock_test_txt, getSubjects(subjectList)));
        final List<SubjectModel> finalSubjectsList = subjectList;
        selectSubjSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (adapterView.getItemAtPosition(position).toString().equalsIgnoreCase("Select Subject")) {
                    selectChapSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.challenge_spinner_item, R.id.mock_test_txt, getChapters(null)));
                } else {
                    selectSubjError.setVisibility(View.GONE);
                    for (int i = 0; i < finalSubjectsList.size(); i++) {
                        if (finalSubjectsList.get(i).subjectName.equalsIgnoreCase(adapterView.getItemAtPosition(position).toString())) {
                            chapterList = finalSubjectsList.get(i).chapters;
                            selectChapSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.challenge_spinner_item, R.id.mock_test_txt, getChapters(chapterList)));
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        selectChapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (!adapterView.getItemAtPosition(position).toString().equalsIgnoreCase("Chapter")) {
                    selectChapError.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private List<String> getExams(List<Exam> exams) {
        List<String> strings = new ArrayList<>();
        strings.add("Select Exam");
        if (exams != null && !exams.isEmpty()) {
            for (int i = 0; i < exams.size(); i++) {
                strings.add(exams.get(i).examName);
            }
        }
        return strings;
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
