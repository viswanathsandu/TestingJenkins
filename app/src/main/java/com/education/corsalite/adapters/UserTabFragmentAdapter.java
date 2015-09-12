package com.education.corsalite.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.education.corsalite.fragments.UserProfileTab;

/**
 * Created by Girish on 12/09/15.
 */
public class UserTabFragmentAdapter extends FragmentPagerAdapter {
    public UserTabFragmentAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (i) {
            case 0:
                fragment = new UserProfileTab();
                args.putString("adapter_type", "exam");
                fragment.setArguments(args);
                break;
            case 1:
                fragment = new UserProfileTab();
                args.putString("adapter_type", "messages");
                fragment.setArguments(args);
                break;
            case 2:
                fragment = new UserProfileTab();
                args.putString("adapter_type", "virtualCurrency");
                fragment.setArguments(args);
                break;

        }
        return fragment;

    }

    @Override
    public int getCount() {
       return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = "Exam Schedule";
                break;
            case 1:
                title = "Messages";
                break;
            case 2:
                title = "Virtual Currency Summary";
                break;
        };
       return title;
    }
}