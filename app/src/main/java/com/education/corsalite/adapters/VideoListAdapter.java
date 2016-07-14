package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.fragments.VideoListDialog;
import com.education.corsalite.models.ContentModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Girish on 18/10/15.
 */
public class VideoListAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;
    IVideoSelectedListener mListener;

    public VideoListAdapter(List<ContentModel> videoList, LayoutInflater inflater) {
        this(videoList);
        this.inflater = inflater;
    }

    private VideoListAdapter(List<ContentModel> videoList) {
        addAll(videoList);
    }

    public void setVideoSelectedListener(IVideoSelectedListener mListener){
        this.mListener = mListener;
    }

    @Override
    public VideoDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoDataHolder(inflater.inflate(R.layout.row_spinner_text, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((VideoDataHolder) holder).bindData(position, (ContentModel)getItem(position));
    }

    public class VideoDataHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_spn)
        TextView tvName;

        View parent;

        public VideoDataHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final ContentModel video) {
            tvName.setText(video.contentName);
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.onVideoSelected(position);
                    }
                }
            });
        }
    }

    public interface IVideoSelectedListener{
        void onVideoSelected(int position);
    }
}
