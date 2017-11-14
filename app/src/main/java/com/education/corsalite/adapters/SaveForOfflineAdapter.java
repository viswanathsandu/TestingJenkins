package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.education.corsalite.databinding.SaveForOfflineItemBinding;
import com.education.corsalite.models.SubjectModel;

import java.util.List;

public class SaveForOfflineAdapter extends AbstractRecycleViewAdapter {

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    LayoutInflater inflater;
    private List<SubjectModel> data;

    public SaveForOfflineAdapter(LayoutInflater inflater,
            OnItemClickListener onItemClickListener) {
        this.inflater = inflater;
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public SaveForOfflineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SaveForOfflineItemBinding binding = SaveForOfflineItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new SaveForOfflineViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SaveForOfflineViewHolder) holder).bind((SubjectModel) getItem(position));
    }

    public class SaveForOfflineViewHolder extends RecyclerView.ViewHolder {

        SaveForOfflineItemBinding mBinding;

        public SaveForOfflineViewHolder(SaveForOfflineItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public void bind(SubjectModel subject) {
            mBinding.setSubject(subject);
        }
    }

}
