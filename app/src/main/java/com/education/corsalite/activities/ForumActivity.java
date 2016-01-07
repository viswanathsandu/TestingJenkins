package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.education.corsalite.R;
import com.education.corsalite.adapters.PostPagerAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseData;
import com.education.corsalite.models.responsemodels.StudyCenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by ayush on 27/10/15.
 */
public class ForumActivity extends AbstractBaseActivity {

    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;

    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    private LayoutInflater inflater;
    private CourseData mCourseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_forum, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this, myView);

        //set adapter to your ViewPager
        mViewPager.setAdapter(new PostPagerAdapter(getSupportFragmentManager(), this));
        mTabLayout.setupWithViewPager(mViewPager);
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
//                            setupSubjects(mCourseData);
                        }
                    }
                });
    }

//    private void setupSubjects(CourseData courseData) {
//        subjects = new ArrayList<String>();
//        List<String> courses = new ArrayList<>();
//        for (StudyCenter studyCenter : courseData.StudyCenter) {
//            courses.add(studyCenter.SubjectName.toString());
//        }
//        spinnerLayout.setVisibility(View.VISIBLE);
//        addSubjectsAndCreateViews(courses);
//    }
//
//    private void addSubjectsAndCreateViews(List<String> courses) {
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_title_textview, courses);
//        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
//        courseSpinner.setAdapter(dataAdapter);
//        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                key = courseSpinner.getSelectedItem().toString();
//                for (StudyCenter studyCenter : mCourseData.StudyCenter) {
//                    if (key.equalsIgnoreCase(studyCenter.SubjectName)) {
//                        ((TextView) courseSpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.black));
//                        callNotesData(studyCenter.idCourseSubject);
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }
//
//    private View getView() {
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.study_center_text_view, null);
//        view.findViewById(R.id.arrow_img).setVisibility(View.GONE);
//        return view;
//    }
//
//    private void setListener(final TextView textView, final String text) {
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (selectedSubjectTxt != null) {
//                    selectedSubjectTxt.setSelected(false);
//                }
//                selectedSubjectTxt = textView;
//                selectedSubjectTxt.setSelected(true);
//                if (mCourseData != null && mCourseData.StudyCenter != null) {
//                    key = text;
//                    for (StudyCenter studyCenter : mCourseData.StudyCenter) {
//                        if (key.equalsIgnoreCase(studyCenter.SubjectName)) {
////                            callNotesData(studyCenter.textView);
//                            break;
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    private void callNotesData(Integer idCourseSubject) {
//        hideList();
//        mSubjectId = idCourseSubject.toString();
//        mChapterId = null;
//        getNotesData();
//        mAdapter.notifyDataSetChanged();
//    }

//    private void getNotesData() {
//        ApiManager.getInstance(this).getNotes(LoginUserCache.getInstance().loginResponse.studentId, mSubjectId, mChapterId, mTopicId, new ApiCallback<List<Note>>(this) {
//            @Override
//            public void failure(CorsaliteError error) {
//                super.failure(error);
//                if (error != null && !TextUtils.isEmpty(error.message)) {
//                    showToast(error.message);
//                }
//            }
//
//            @Override
//            public void success(List<Note> notesList, Response response) {
//                super.success(notesList, response);
//                mListData = new ArrayList<SubjectNameSection>();
//                if (notesList != null) {
//                    showData();
//                    String chapter = "";
//                    String topic = "";
//                    for (Note note : notesList) {
//                        if (note != null && note.chapter != null) {
//                            if (!chapter.equals(note.chapter)) {
//                                chapter = note.chapter;
//                                SubjectNameSection item = new SubjectNameSection(SubjectNameSection.SECTION, note);
//                                mListData.add(item);
//                                if (!topic.equals(note.topic)) {
//                                    topic = setItemAndSection(note);
//                                }
//                            } else {
//                                if (topic.equals(note.topic)) {
//                                    setItemData(note);
//                                } else {
//                                    topic = setItemAndSection(note);
//                                }
//                            }
//                        }
//                    }
//                    ((ForumAdapter) mAdapter).updateNotesList(mListData);
//                } else {
//                    recyclerView.setVisibility(View.GONE);
//                    notesLayout.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//    }
//
//    private String setItemAndSection(Note note) {
//        String topic;
//        topic = note.topic;
//        SubjectNameSection subitem = new SubjectNameSection(SubjectNameSection.SUBSECTION, note);
//        mListData.add(subitem);
//        setItemData(note);
//        return topic;
//    }
//
//    private void setItemData(Note note) {
//        SubjectNameSection listItem = new SubjectNameSection(SubjectNameSection.ITEM, note);
//        mListData.add(listItem);
//    }
//
//    public class SubjectNameSection {
//
//        public static final int SECTION = 0;
//        public static final int SUBSECTION = 1;
//        public static final int ITEM = 2;
//        public final int type;
//        public final Object tag;
//
//        public SubjectNameSection(int type, Object tag) {
//            this.type = type;
//            this.tag = tag;
//        }
//    }
}

