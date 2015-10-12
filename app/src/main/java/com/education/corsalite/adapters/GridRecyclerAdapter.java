package com.education.corsalite.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.models.responsemodels.CompletionStatus;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.utils.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.StudyCenterSubjectViewHolder> {
    private List<Chapters> chapters;
    private String key;
    private StudyCentreActivity studyCentreActivity;

    public GridRecyclerAdapter(List<Chapters> chapters, StudyCentreActivity studyCentreActivity, String key) {
        this.chapters = chapters;
        this.studyCentreActivity = studyCentreActivity;
        this.key = key;
    }

    @Override
    public StudyCenterSubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_centre_grid_view, parent, false);
        return new StudyCenterSubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StudyCenterSubjectViewHolder holder, final int position) {

        Chapters chapter = chapters.get(position);
        String label = chapter.chapterName;
        holder.textView.setText(label);
        holder.timeSpent.setText(getDateFromMillis(chapter.timeSpent));
        holder.level.setText(studyCentreActivity.getResources().getString(R.string.level_text) + Data.getInt(chapter.completedTopics));
        setColor(holder, chapter);
        int max = Data.getInt(chapter.totalTopics);
        holder.progressBar.setMax(max==0 ? 1 : max);
        holder.progressBar.setProgress(Data.getInt(chapter.completedTopics));
        getLevelDrawable(holder, chapter.completedTopics);
        holder.star.setText((int)Data.getDoubleInInt(chapter.earnedMarks)+"/"+(int)Data.getDoubleInInt(chapter.totalTestedMarks));
        holder.gridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlertDialog(v, holder);
            }
        });
    }

    public void updateData(List<Chapters> chapters, String key) {
        this.chapters = chapters;
        this.key = key;
    }

    private void getAlertDialog(View v, StudyCenterSubjectViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.gridLayout.getContext());
        LayoutInflater li = (LayoutInflater) studyCentreActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.layout_list_item_view_popup, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = (int) v.getX() + 50;
        wmlp.y = (int) v.getY() + 160;
        dialog.show();
        dialog.getWindow().setLayout(300, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setColor(StudyCenterSubjectViewHolder holder, Chapters chapter) {
        if (Data.getDoubleInInt(chapter.earnedMarks) == 0 && Data.getDoubleInInt(chapter.totalTestedMarks) == 0) {
            holder.gridLayout.setBackground(studyCentreActivity.getResources().getDrawable(R.drawable.blueshape));
        } else if(Data.getDoubleInInt(chapter.earnedMarks) < Data.getDoubleInInt(chapter.scoreRed)) {
            holder.gridLayout.setBackground(studyCentreActivity.getResources().getDrawable(R.drawable.redshape));
        } else if (Data.getDoubleInInt(chapter.earnedMarks) < Data.getDoubleInInt(chapter.scoreAmber)) {
            holder.gridLayout.setBackground(studyCentreActivity.getResources().getDrawable(R.drawable.yellowshape));
        } else {
            holder.gridLayout.setBackground(studyCentreActivity.getResources().getDrawable(R.drawable.greenshape));
        }
    }

    private void getLevelDrawable(StudyCenterSubjectViewHolder holder, String levelstr) {
        if(!TextUtils.isEmpty(levelstr)) {
            int level = Data.getInt(levelstr);
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
                default:
                    holder.level.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_tile_level_five, 0, 0, 0);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public class StudyCenterSubjectViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView timeSpent;
        public TextView level;
        public TextView star;
        public LinearLayout gridLayout;
        public ProgressBar progressBar;

        public StudyCenterSubjectViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.subject_name);
            timeSpent = (TextView) itemView.findViewById(R.id.clock);
            level = (TextView) itemView.findViewById(R.id.level);
            star = (TextView) itemView.findViewById(R.id.star);
            gridLayout = (LinearLayout) itemView.findViewById(R.id.grid_layout);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_id);
        }
    }

    private String getDateFromMillis(String millisStr) {
        if(!TextUtils.isEmpty(millisStr)) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            return formatter.format(new Date(Data.getLong(millisStr)));
        } else {
            return "00:00:00";
        }
    }
}