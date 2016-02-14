package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.GridRecyclerAdapter;
import com.education.corsalite.adapters.NotesAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseData;
import com.education.corsalite.models.responsemodels.Note;
import com.education.corsalite.models.responsemodels.StudyCenter;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by ayush on 27/10/15.
 */
public class NotesActivity extends AbstractBaseActivity {

    private LinearLayout spinnerLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LayoutInflater inflater;
    private String mSubjectId = null;
    private String mChapterId = null;
    private String mTopicId = null;
    private List<SubjectNameSection> mListData;
    private CourseData mCourseData;
    private ArrayList<String> subjects;
    private RelativeLayout relativeLayout;
    Spinner courseSpinner;
    private TextView selectedSubjectTxt;
    private String key;
    private LinearLayout notesLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_notes, null);
        relativeLayout = (RelativeLayout) myView.findViewById(R.id.notes_layout);
        courseSpinner = (Spinner) myView.findViewById(R.id.spinner);
        notesLayout = (LinearLayout) myView.findViewById(R.id.no_notes);
        spinnerLayout = (LinearLayout) myView.findViewById(R.id.spinner_layout);
        progressBar = (ProgressBar)myView.findViewById(R.id.headerProgress);
        frameLayout.addView(myView);
        setToolbarForNotes();
        getBundleData();
        initUI();
        setAdapter();
        getNotesData();
        sendAnalytics(getString(R.string.screen_notes));
    }

    private void hideList() {
        recyclerView.setVisibility(View.GONE);
    }

    private void showData() {
        recyclerView.setVisibility(View.VISIBLE);
        notesLayout.setVisibility(View.GONE);
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String subjectId = GridRecyclerAdapter.SUBJECT_ID;
            String chapterId = GridRecyclerAdapter.CHAPTER_ID;
            String topicId = GridRecyclerAdapter.TOPIC_ID;
            if (bundle.containsKey(subjectId) && bundle.getString(subjectId) != null) {
                mSubjectId = bundle.getString(subjectId);
            }
            if (bundle.containsKey(chapterId) && bundle.getString(chapterId) != null) {
                mChapterId = bundle.getString(chapterId);
            }
            if (bundle.containsKey(topicId) && bundle.getString(topicId) != null) {
                mTopicId = bundle.getString(topicId);
            }
        }
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.notes_list);
    }

    private void setAdapter() {
        mAdapter = new NotesAdapter(this, new ArrayList<SubjectNameSection>(), inflater);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
        progressBar.setVisibility(View.VISIBLE);
        getStudyCentreData(course.courseId.toString());
    }

    private void getStudyCentreData(String courseId) {
        ApiManager.getInstance(this).getStudyCentreData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, new ApiCallback<List<StudyCenter>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        progressBar.setVisibility(View.GONE);
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                    }

                    @Override
                    public void success(List<StudyCenter> studyCenters, Response response) {
                        super.success(studyCenters, response);
                        progressBar.setVisibility(View.GONE);
                        if (studyCenters != null) {
                            mCourseData = new CourseData();
                            mCourseData.StudyCenter = studyCenters;
                        }
                        if (mCourseData != null && mCourseData.StudyCenter != null && !mCourseData.StudyCenter.isEmpty()) {
                            setupSubjects(mCourseData);
                        }
                    }
                });
    }

    private void setupSubjects(CourseData courseData) {
        subjects = new ArrayList<String>();
        List<String> courses = new ArrayList<>();
        for (StudyCenter studyCenter : courseData.StudyCenter) {
            courses.add(studyCenter.SubjectName.toString());
        }
        spinnerLayout.setVisibility(View.VISIBLE);
        addSubjectsAndCreateViews(courses);
    }

    private void addSubjectsAndCreateViews(List<String> courses) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_title_textview, courses);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        courseSpinner.setAdapter(dataAdapter);
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                key = courseSpinner.getSelectedItem().toString();
                for (StudyCenter studyCenter : mCourseData.StudyCenter) {
                    if (key.equalsIgnoreCase(studyCenter.SubjectName)) {
                        ((TextView) courseSpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
                        callNotesData(studyCenter.idCourseSubject);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private View getView() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.study_center_text_view, null);
        view.findViewById(R.id.arrow_img).setVisibility(View.GONE);
        return view;
    }

    private void setListener(final TextView textView, final String text) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSubjectTxt != null) {
                    selectedSubjectTxt.setSelected(false);
                }
                selectedSubjectTxt = textView;
                selectedSubjectTxt.setSelected(true);
                if (mCourseData != null && mCourseData.StudyCenter != null) {
                    key = text;
                    for (StudyCenter studyCenter : mCourseData.StudyCenter) {
                        if (key.equalsIgnoreCase(studyCenter.SubjectName)) {
//                            callNotesData(studyCenter.textView);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void callNotesData(Integer idCourseSubject) {
        hideList();
        mSubjectId = idCourseSubject.toString();
        mChapterId = null;
        getNotesData();
        mAdapter.notifyDataSetChanged();
    }

    private void getNotesData() {
        ApiManager.getInstance(this).getNotes(LoginUserCache.getInstance().loginResponse.studentId, mSubjectId, mChapterId, mTopicId, new ApiCallback<List<Note>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                if (error != null && !TextUtils.isEmpty(error.message)) {
                    showToast(error.message);
                }
            }

            @Override
            public void success(List<Note> notesList, Response response) {
                super.success(notesList, response);
                mListData = new ArrayList<SubjectNameSection>();
                if (notesList != null) {
                    showData();
                    String chapter = "";
                    String topic = "";
                    for (Note note : notesList) {
                        if (note != null && note.chapter != null) {
                            if (!chapter.equals(note.chapter)) {
                                chapter = note.chapter;
                                SubjectNameSection item = new SubjectNameSection(SubjectNameSection.SECTION, note);
                                mListData.add(item);
                                if (!topic.equals(note.topic)) {
                                    topic = setItemAndSection(note);
                                }
                            } else {
                                if (topic.equals(note.topic)) {
                                    setItemData(note);
                                } else {
                                    topic = setItemAndSection(note);
                                }
                            }
                        }
                    }
                    ((NotesAdapter) mAdapter).updateNotesList(mListData);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    notesLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String setItemAndSection(Note note) {
        String topic;
        topic = note.topic;
        SubjectNameSection subitem = new SubjectNameSection(SubjectNameSection.SUBSECTION, note);
        mListData.add(subitem);
        setItemData(note);
        return topic;
    }

    private void setItemData(Note note) {
        SubjectNameSection listItem = new SubjectNameSection(SubjectNameSection.ITEM, note);
        mListData.add(listItem);
    }

    public class SubjectNameSection {

        public static final int SECTION = 0;
        public static final int SUBSECTION = 1;
        public static final int ITEM = 2;
        public final int type;
        public final Object tag;

        public SubjectNameSection(int type, Object tag) {
            this.type = type;
            this.tag = tag;
        }
    }
}

