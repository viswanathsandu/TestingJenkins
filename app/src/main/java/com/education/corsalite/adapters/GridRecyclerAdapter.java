package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.StudyCentreActivity;
import com.education.corsalite.models.responsemodels.CompletionStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.TextViewHolder> {
    private HashMap<String, List<CompletionStatus>> completionStatuses;
    private String key;
    private StudyCentreActivity studyCentreActivity;

    public GridRecyclerAdapter(HashMap<String, List<CompletionStatus>> completionStatuses, String key, StudyCentreActivity studyCentreActivity) {
        this.completionStatuses = completionStatuses;
        this.key = key;
        this.studyCentreActivity = studyCentreActivity;
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
        holder.timespent.setText(getDateFromMillis(Long.parseLong(list.get(position).getTimeSpent())));
        String level = list.get(position).getCompletedTopics();
        holder.level.setText(studyCentreActivity.getResources().getString(R.string.level_text) + level);
        getLevelDrawable(holder, Integer.parseInt(level));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        holder.textView.getContext(), label, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLevelDrawable(TextViewHolder holder, int level) {
        switch (level) {
            case 0:
                holder.level.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_tile_level_zero, 0, 0, 0);
                break;
            case 1:
                holder.level.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_tile_level_one, 0, 0, 0);
                break;
            case 2:
                holder.level.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_tile_level_two, 0, 0, 0);
                break;
            case 3:
                holder.level.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_tile_level_three, 0, 0, 0);
                break;
            case 4:
                holder.level.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_tile_level_four, 0, 0, 0);
                break;
            case 5:
                holder.level.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_tile_level_five, 0, 0, 0);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return completionStatuses.size();
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView timespent;
        public TextView level;

        public TextViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.subject_name);
            timespent = (TextView) itemView.findViewById(R.id.clock);
            level = (TextView) itemView.findViewById(R.id.level);
        }
    }

    private String getDateFromMillis(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return formatter.format(new Date(millis));
    }
}