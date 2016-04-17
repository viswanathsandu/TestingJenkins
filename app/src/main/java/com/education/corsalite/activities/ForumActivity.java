package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

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
        setToolbarForForum();
        toolbar.findViewById(R.id.new_post_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNewPostClicked();
            }
        });
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
                    }
                });
    }


    public void onNewPostClicked() {
        Bundle bundle = new Bundle();
        bundle.putString("type", "Forum");
        bundle.putString("operation", "Add");
        Intent post = new Intent(this, NewPostActivity.class);
        post.putExtras(bundle);
        startActivity(post);
    }
}

