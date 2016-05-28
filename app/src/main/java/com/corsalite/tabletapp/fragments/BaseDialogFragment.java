package com.corsalite.tabletapp.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.corsalite.tabletapp.models.socket.response.ResponseEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 4/2/16.
 */
public class BaseDialogFragment extends DialogFragment {

    Dialog dialog;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    public void showProgress(){
        ProgressBar pbar = new ProgressBar(getActivity());
        pbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(pbar);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void closeProgress(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
