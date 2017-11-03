package com.education.corsalite.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.education.corsalite.models.socket.response.ResponseEvent;
import com.education.corsalite.utils.AppPref;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 4/2/16.
 */
public class BaseDialogFragment extends DialogFragment {

    protected Dialog dialog;
    protected AppPref appPref;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        appPref = AppPref.get(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    // Dummy event to avoid no subscriber issues
    public void onEvent(ResponseEvent event) {
    }

    public void showToast(String message) {
        if (getActivity() != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public void showLongToast(String message) {
        if (getActivity() != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    public void showProgress() {
        ProgressBar pbar = new ProgressBar(getActivity());
        pbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(pbar);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void closeProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
