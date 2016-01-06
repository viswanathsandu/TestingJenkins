package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.education.corsalite.R;
import com.education.corsalite.event.OfflineEventClass;
import com.education.corsalite.fragments.OfflineContentFragment;
import com.education.corsalite.fragments.OfflineTestsFragment;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.utils.L;

/**
 * Created by Aastha on 05/10/15.
 */
public class OfflineContentActivity extends AbstractBaseActivity {

    TabLayout tabLayout;
    ViewPager mViewPager;
    IOfflineEventListener offlineEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_offline_content, null);
        frameLayout.addView(myView);
        tabLayout = (TabLayout)findViewById(R.id.tl_offline);
        mViewPager = (ViewPager)findViewById(R.id.pager);
        setToolbarForOfflineContent();
        setTabView();
        sendAnalytics(getString(R.string.screen_offlineContent));
    }

    private void setTabView() {
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new OfflineBaseTabAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void setOfflineListener(IOfflineEventListener offlineEventListener){
        this.offlineEventListener = offlineEventListener;
    }

    @Override
    public void onEvent(Course course) {
        if(offlineEventListener!=null) {
            offlineEventListener.onCourseIdSelected(course);
        }
        super.onEvent(course);
    }

    @Override
    public void onEvent(OfflineEventClass offlineEventClass) {
        L.info("Saved data with this id: "+offlineEventClass.id);
        super.onEvent(offlineEventClass);
    }

    private class OfflineBaseTabAdapter extends FragmentPagerAdapter{

        public OfflineBaseTabAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            Bundle args = new Bundle();
            switch (i) {
                case 0:
                    fragment = new OfflineContentFragment();
                    fragment.setArguments(args);
                    break;
                case 1:
                    fragment = new OfflineTestsFragment();
                    fragment.setArguments(args);
                    break;

            }
            return fragment;
        }


        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case 0:
                    title = "Content";
                    break;
                case 1:
                    title = "Tests";
                    break;
            }
            return title;
        }
    }

    @Override
    public void onBackPressed() {
        if(offlineEventListener!=null) {
            offlineEventListener.onUpdateOfflineData(selectedCourse.courseId.toString());
        }
        super.onBackPressed();
    }

    public void onDelete(String selectedId,String tag){
        if(offlineEventListener !=null){
            offlineEventListener.onDeleteOfflineData(selectedId,tag);
        }
    }

    public interface IOfflineEventListener{
        void onUpdateOfflineData(String selectedCourse);
        void onDeleteOfflineData(String selectedId,String tag);
        void onCourseIdSelected(Course selectedCourse);

    }


}
