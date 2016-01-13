package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created on 13/01/16.
 *
 * @author Meeth D Jain
 */
public class ChapterTestSetupFragment extends BaseFragment {

    public static ChapterTestSetupFragment newInstance(Bundle bundle) {
        ChapterTestSetupFragment fragment = new ChapterTestSetupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static String getMyTag() {
        return "tag_fragment_chapter_test";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

