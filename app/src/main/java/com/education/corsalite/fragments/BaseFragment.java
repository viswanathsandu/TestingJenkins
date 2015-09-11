package com.education.corsalite.fragments;

import android.app.Fragment;
import android.widget.Toast;

/**
 * Created by vissu on 9/12/15.
 */
public abstract class BaseFragment extends Fragment {

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
