package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.CompletionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.TextViewHolder> {
    private HashMap<String, List<CompletionStatus>> completionStatuses;
    private String key;

    public GridRecyclerAdapter(HashMap<String, List<CompletionStatus>> completionStatuses, String key) {
        this.completionStatuses = completionStatuses;
        this.key = key;
    }

    public void updateData(HashMap<String, List<CompletionStatus>> completionStatuses, String key) {
        this.completionStatuses = completionStatuses;
        this.key = key;
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_centre_grid_view, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TextViewHolder holder, final int position) {
        ArrayList<CompletionStatus> list = (ArrayList<CompletionStatus>) completionStatuses.get(key);
        final String label = list.get(position).getChapterName();
        holder.textView.setText(label);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        holder.textView.getContext(), label, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return completionStatuses.size();
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public TextViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.subject_name);
        }
    }
}