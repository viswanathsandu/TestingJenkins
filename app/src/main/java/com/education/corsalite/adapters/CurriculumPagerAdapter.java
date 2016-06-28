package com.education.corsalite.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.education.corsalite.enums.CurriculumTabType;
import com.education.corsalite.fragments.CurriculumFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sridharnalam on 1/7/16.
 */
public class CurriculumPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private List<CurriculumTabType> tabTypes;

    public CurriculumPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        tabTypes = new ArrayList<>();
        tabTypes.add(CurriculumTabType.TODO);
        tabTypes.add(CurriculumTabType.UPCOMING);
        tabTypes.add(CurriculumTabType.COMPLETED);
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return tabTypes.size();
    }

    @Override
    public Fragment getItem(int position) {
        return CurriculumFragment.newInstance(tabTypes.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTypes.get(position).toString();
    }

}
