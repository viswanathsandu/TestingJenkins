package com.corsalite.tabletapp.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.activities.VideoActivity;
import com.corsalite.tabletapp.adapters.VideoListAdapter;
import com.corsalite.tabletapp.models.ContentModel;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Girish on 17/10/15.
 */
public class VideoListDialog extends DialogFragment implements VideoListAdapter.IVideoSelectedListener {

    @Bind(R.id.tv_title)TextView tvTitle;
    @Bind(R.id.rv_videolist)RecyclerView rvVideoList;

    List<ContentModel> mVideoList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video_list, container, false);
        ButterKnife.bind(this, v);

        if(getArguments().getString("title") != null && getArguments().getString("title").length() >0 ) {
            String title = tvTitle.getText().toString() + "<b>" + getArguments().getString("title") + "</b>";
            tvTitle.setText(Html.fromHtml(title));
        }

        if(getArguments().getSerializable("videolist") != null) {
            mVideoList = (List<ContentModel>) getArguments().getSerializable("videolist");
        }

        final LinearLayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvVideoList.setLayoutManager(layoutManager);
        VideoListAdapter videoListAdapter = new VideoListAdapter(mVideoList, getActivity().getLayoutInflater());
        videoListAdapter.setVideoSelectedListener(this);
        rvVideoList.setAdapter(videoListAdapter);
        return v;
    }

    @Override
    public void onVideoSelected(int position) {
        dismiss();
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putExtra("selectedPosition", position);
        intent.putExtra("videoList", (Serializable) mVideoList);
        startActivity(intent);
    }
}
