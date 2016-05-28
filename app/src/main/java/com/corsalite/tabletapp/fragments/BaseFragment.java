package com.corsalite.tabletapp.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.corsalite.tabletapp.db.SugarDbManager;
import com.corsalite.tabletapp.models.socket.response.ResponseEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 9/12/15.
 */
public abstract class BaseFragment extends Fragment {
    private Dialog dialog;

    protected SugarDbManager dbManager;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = SugarDbManager.get(getActivity().getApplicationContext());
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
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    // Dummy event to avoid no subscriber issues
    public void onEvent(ResponseEvent event) {
    }
}
