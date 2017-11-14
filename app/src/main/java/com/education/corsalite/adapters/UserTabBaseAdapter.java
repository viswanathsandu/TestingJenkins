package com.education.corsalite.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.education.corsalite.fragments.ExamTabFragment;
import com.education.corsalite.fragments.MessageTabFragment;
import com.education.corsalite.fragments.VirtualCurrencyFragment;
import com.education.corsalite.models.responsemodels.ExamDetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish on 12/09/15.
 */
public class UserTabBaseAdapter extends FragmentPagerAdapter {

    private final String ADAPTER_TYPE = "adapter_type";
    List<ExamDetail> examDetailList;
    String examTitle = "";

    public UserTabBaseAdapter(android.support.v4.app.FragmentManager fm, List<ExamDetail> examDetailList) {
        super(fm);
        this.examDetailList = examDetailList;
        setExamTitle();
    }

    private void setExamTitle() {
        if (examDetailList != null && examDetailList.size() > 0) {
            examTitle = " (" + String.format("%02d", examDetailList.size()) + ")";
        }
    }


    public void updateExamDetailData(List<ExamDetail> examDetails) {
        this.examDetailList = examDetails;
        notifyDataSetChanged();

    }


    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (i) {
            case 0:
                fragment = new ExamTabFragment();
                args.putSerializable(ADAPTER_TYPE, (Serializable) examDetailList);
                fragment.setArguments(args);
                break;
            case 1:
                fragment = new MessageTabFragment();
                args.putSerializable(ADAPTER_TYPE, new ArrayList<>());
                fragment.setArguments(args);
                break;
            case 2:
                fragment = new VirtualCurrencyFragment();
                args.putSerializable(ADAPTER_TYPE, new ArrayList<>());
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
                title = "Exam Schedule" + examTitle;
                break;
            case 1:
                title = "Messages";
                break;
            case 2:
                title = "Virtual Currency Summary";
                break;
        }
        return title;
    }
}