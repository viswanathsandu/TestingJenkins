package com.education.corsalite.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.ProgressBar;

import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.models.socket.response.ResponseEvent;
import com.education.corsalite.utils.AppPref;
import com.localytics.android.Localytics;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 9/12/15.
 */
public abstract class BaseFragment extends Fragment {
    private Dialog dialog;
    protected AppPref appPref;
    protected SugarDbManager dbManager;

    @Override
    public void onStart() {
        super.onStart();
        Localytics.tagScreen(this.getClass().getSimpleName());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = SugarDbManager.get(getActivity().getApplicationContext());
        appPref = AppPref.get(getActivity());
    }

    public void showToast(String message) {
        if (getActivity() != null && getActivity() instanceof AbstractBaseActivity) {
            ((AbstractBaseActivity) getActivity()).showToast(message);
        }
    }

    public void showLongToast(String message) {
        if (getActivity() != null && getActivity() instanceof AbstractBaseActivity) {
            ((AbstractBaseActivity) getActivity()).showLongToast(message);
        }
    }

    public void showProgress() {
        if (getActivity() != null) {
            ProgressBar pbar = new ProgressBar(getActivity());
            pbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(pbar);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    public void closeProgress() {
        if (getActivity() != null && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // Dummy event to avoid no subscriber issues
    public void onEvent(ResponseEvent event) {
    }
}
