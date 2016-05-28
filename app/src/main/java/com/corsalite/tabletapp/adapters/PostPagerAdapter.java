package com.corsalite.tabletapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.fragments.PostsFragment;

/**
 * Created by sridharnalam on 1/7/16.
 */
public class PostPagerAdapter extends FragmentPagerAdapter
{
    private final String[] titles;
    private Context mContext;

    public PostPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        titles = mContext.getResources().getStringArray(R.array.tab_names);
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return PostsFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
