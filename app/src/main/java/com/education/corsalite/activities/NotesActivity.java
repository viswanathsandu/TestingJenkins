package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
import com.education.corsalite.models.responsemodels.Notes;
import com.education.corsalite.models.responsemodels.StudyCenter;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by ayush on 27/10/15.
 */
public class NotesActivity extends AbstractBaseActivity {

    private LinearLayout subjectLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LayoutInflater inflater;
    private String mSubjectId = null;
    private String mChapterId = null;
    private String mTopicId = null;
    private List<SubjectNameSection> mListData;
    private CourseData mCourseData;
    private ArrayList<String> subjects;
    private LinearLayout linearLayout;
    private TextView selectedSubjectTxt;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_notes, null);
        linearLayout = (LinearLayout) myView.findViewById(R.id.notes_layout);
        subjectLayout = (LinearLayout) myView.findViewById(R.id.subjects_name_id);
        frameLayout.addView(myView);
        setToolbarForNotes();
        getBundleData();
        initUI();
        setAdapter();
        getNotesData();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(GridRecyclerAdapter.SUBJECT_ID) && bundle.getString(GridRecyclerAdapter.SUBJECT_ID) != null) {
                mSubjectId = bundle.getString("subjectId");
            }
            if (bundle.containsKey(GridRecyclerAdapter.CHAPTER_ID) && bundle.getString(GridRecyclerAdapter.CHAPTER_ID) != null) {
                mChapterId = bundle.getString("chapterId");
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
        getStudyCentreData(course.courseId.toString());
    }

    private void getStudyCentreData(String courseId) {
        ApiManager.getInstance(this).getStudyCentreData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, new ApiCallback<List<StudyCenter>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                    }

                    @Override
                    public void success(List<StudyCenter> studyCenters, Response response) {
                        super.success(studyCenters, response);
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
        subjectLayout.removeAllViews();
        subjects = new ArrayList<String>();
        for (StudyCenter studyCenter : courseData.StudyCenter) {
            addSubjectsAndCreateViews(studyCenter);
        }
    }

    private void addSubjectsAndCreateViews(StudyCenter studyCenter) {
        String subject = studyCenter.SubjectName;
        subjects.add(subject);
        subjectLayout.addView(getSubjectView(subject, studyCenter.idCourseSubject + "", subjects.size() == 1));
    }

    private View getSubjectView(String subjectName, String subjectId, boolean isSelected) {
        View v = getView();
        TextView tv = (TextView) v.findViewById(R.id.subject);
        tv.setText(subjectName);
        tv.setTag(subjectId);
        if(isSelected) {
            tv.setSelected(true);
            callNotesData(tv);
            selectedSubjectTxt = tv;
        }
        setListener(tv, subjectName);
        return v;
    }
    private View getView() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.study_center_text_view, null);
    }

    private void setListener(final TextView textView, final String text) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showList();
                if (selectedSubjectTxt != null) {
                    selectedSubjectTxt.setSelected(false);
                }
                selectedSubjectTxt = textView;
                selectedSubjectTxt.setSelected(true);
                if (mCourseData != null && mCourseData.StudyCenter != null) {
                    key = text;
                    for (StudyCenter studyCenter : mCourseData.StudyCenter) {
                        if (key.equalsIgnoreCase(studyCenter.SubjectName)) {
                            callNotesData(textView);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void callNotesData(TextView textView) {
        mSubjectId = textView.getTag().toString();
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
                    String chapter = "";
                    for (Note note : notesList) {
                        if (note != null && note.chapter != null) {
                            if (!chapter.equals(note.chapter)) {
                                chapter = note.chapter;
                                SubjectNameSection item = new SubjectNameSection(SubjectNameSection.SECTION, note);
                                mListData.add(item);
                                setItemData(note);
                            } else {
                                setItemData(note);
                            }
                        }
                    }
                    ((NotesAdapter) mAdapter).updateNotesList(mListData);
                }
            }
        });
    }

    private void setItemData(Note note) {
        SubjectNameSection listItem = new SubjectNameSection(SubjectNameSection.ITEM, note);
        mListData.add(listItem);
    }

    public class SubjectNameSection {

        public static final int SECTION = 0;
        public static final int ITEM = 1;
        public final int type;
        public final Object tag;

        public SubjectNameSection(int type, Object tag) {
            this.type = type;
            this.tag = tag;
        }
    }
}

