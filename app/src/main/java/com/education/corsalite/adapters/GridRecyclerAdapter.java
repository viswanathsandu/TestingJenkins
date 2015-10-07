package com.education.corsalite.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.StudyCentreActivity;
import com.education.corsalite.models.responsemodels.CompletionStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.StudyCenterSubjectViewHolder> {
    private HashMap<String, List<CompletionStatus>> mCompletionStatuses;
    private String key;
    private StudyCentreActivity studyCentreActivity;

    public GridRecyclerAdapter(HashMap<String, List<CompletionStatus>> mCompletionStatuses, String key, StudyCentreActivity studyCentreActivity) {
        this.mCompletionStatuses = mCompletionStatuses;
        this.key = key;
        this.studyCentreActivity = studyCentreActivity;
    }

    public void updateData(HashMap<String, List<CompletionStatus>> completionStatuses, String key) {
        this.mCompletionStatuses = completionStatuses;
        this.key = key;
    }

    @Override
    public StudyCenterSubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_centre_grid_view, parent, false);
        return new StudyCenterSubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StudyCenterSubjectViewHolder holder, final int position) {
        ArrayList<CompletionStatus> completionStatuses = (ArrayList<CompletionStatus>) this.mCompletionStatuses.get(key);
        if (completionStatuses.isEmpty())
            return;
        final String label = completionStatuses.get(position).getChapterName();
        holder.textView.setText(label);
        holder.timeSpent.setText(getDateFromMillis(Long.parseLong(completionStatuses.get(position).getTimeSpent())));
        String level = completionStatuses.get(position).getCompletedTopics();
        holder.level.setText(studyCentreActivity.getResources().getString(R.string.level_text) + level);
        holder.progressBar.setMax(20);
        holder.progressBar.setProgress(4);
        getLevelDrawable(holder, Integer.parseInt(level));
        holder.gridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlertDialog(v, holder);
            }
        });
        removeLogic(holder, position, (ArrayList<CompletionStatus>) this.mCompletionStatuses.get(key));
    }

    private void getAlertDialog(View v, StudyCenterSubjectViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.gridLayout.getContext());
        LayoutInflater li = (LayoutInflater) studyCentreActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.layout_list_item_view_popup, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = (int) v.getX();
        wmlp.y = (int) v.getY() + 160;
        dialog.show();
        dialog.getWindow().setLayout(300, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void removeLogic(StudyCenterSubjectViewHolder holder, int position, ArrayList<CompletionStatus> completionStatuses) {
        if (completionStatuses.get(position).statusColor == 2) {
            holder.gridLayout.setBackground(studyCentreActivity.getResources().getDrawable(R.drawable.blueshape));
        } else if (completionStatuses.get(position).statusColor == 1) {
            holder.gridLayout.setBackground(studyCentreActivity.getResources().getDrawable(R.drawable.redshape));
        } else if (completionStatuses.get(position).statusColor == 3) {
            holder.gridLayout.setBackground(studyCentreActivity.getResources().getDrawable(R.drawable.greenshape));
        }else{
            holder.gridLayout.setBackground(studyCentreActivity.getResources().getDrawable(R.drawable.yellowshape));
        }
    }

    private void getLevelDrawable(StudyCenterSubjectViewHolder holder, int level) {
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
            default:
                holder.level.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_tile_level_five, 0, 0, 0);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCompletionStatuses.size();
    }

    public class StudyCenterSubjectViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView timeSpent;
        public TextView level;
        public LinearLayout gridLayout;
        public ProgressBar progressBar;

        public StudyCenterSubjectViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.subject_name);
            timeSpent = (TextView) itemView.findViewById(R.id.clock);
            level = (TextView) itemView.findViewById(R.id.level);
            gridLayout = (LinearLayout) itemView.findViewById(R.id.grid_layout);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_id);
        }
    }

    private String getDateFromMillis(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return formatter.format(new Date(millis));
    }
}