package com.corsalite.tabletapp.activities;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.adapters.PostPagerAdapter;
import com.corsalite.tabletapp.models.responsemodels.Chapter;
import com.corsalite.tabletapp.models.responsemodels.Course;
import com.corsalite.tabletapp.models.responsemodels.CourseData;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ayush on 27/10/15.
 */
public class ForumActivity extends AbstractBaseActivity {
    @Bind(R.id.tabLayout) TabLayout mTabLayout;
    @Bind(R.id.viewPager) ViewPager mViewPager;
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

    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
        adapter.notifyDataSetChanged();
        mViewPager.invalidate();
    }

    public void refreshData() {
        adapter.notifyDataSetChanged();
        mViewPager.invalidate();
    }

    public void onNewPostClicked() {
        Bundle bundle = new Bundle();
        bundle.putString("type", "Forum");
        bundle.putString("operation", "Add");
        Intent post = new Intent(this, EditorActivity.class);
        post.putExtras(bundle);
        startActivity(post);
    }
}
