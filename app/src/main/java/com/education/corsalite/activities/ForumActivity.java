package com.education.corsalite.activities;


import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.adapters.PostPagerAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.fragments.EditorDialogFragment;
import com.education.corsalite.models.responsemodels.Chapter;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseData;
import com.education.corsalite.models.responsemodels.StudyCenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by ayush on 27/10/15.
 */
public class ForumActivity extends AbstractBaseActivity{
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    private List<Chapter> allChapters = new ArrayList<>();
    private LayoutInflater inflater;
    private CourseData mCourseData;
    private String key;
    PostPagerAdapter adapter;

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

        adapter = new PostPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public interface  RefreshForum{
        void refresh();
    }


   @Override
    public void onEvent(Course course) {
        super.onEvent(course);
       adapter.notifyDataSetChanged();
       mViewPager.invalidate();
    }

    public void onNewPostClicked() {
        EditorDialogFragment fragment = new EditorDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", "Forum");
        bundle.putString("operation", "Add");
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "ForumEditorDialog");
    }




}

