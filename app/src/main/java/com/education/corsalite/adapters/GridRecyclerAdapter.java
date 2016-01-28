package com.education.corsalite.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.activities.NotesActivity;
import com.education.corsalite.activities.OfflineSubjectActivity;
import com.education.corsalite.activities.StudyCentreActivity;
import com.education.corsalite.activities.TestStartActivity;
import com.education.corsalite.activities.ContentReadingActivity;
import com.education.corsalite.enums.Tests;
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.Data;
import com.localytics.android.Localytics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.StudyCenterSubjectViewHolder> {
    public static final String COURSE_ID = "courseId";
    public static final String SUBJECT_ID = "subjectId";
    public static final String CHAPTER_ID = "chapterId";
    public static final String SUBJECT = "subject";
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

        Resources r = studyCentreActivity.getResources();
        final Chapters chapter = chapters.get(position);
        String label = chapter.chapterName;


        if (chapter.isChapterOffline) {
            holder.rootGridLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Localytics.tagEvent(chapter.chapterName);
                    getAlertDialog(v, holder, chapter);
                }
            });
            holder.gridLayout.setVisibility(View.VISIBLE);
            holder.gridLayoutGray.setVisibility(View.GONE);
            holder.textView.setText(label);
            holder.timeSpent.setText(getDateFromMillis(chapter.timeSpent));
            holder.level.setText(studyCentreActivity.getResources().getString(R.string.level_text) + Data.getInt(chapter.completedTopics));
            holder.rootGridLayout.setBackground(getColorDrawable(holder, chapter));

            int max = Data.getInt(chapter.totalTopics);
            holder.progressBar.setMax(max == 0 ? 1 : max);
            holder.progressBar.setProgress(Data.getInt(chapter.completedTopics));

            getLevelDrawable(holder, chapter.completedTopics);

            holder.star.setText((int) Data.getDoubleInInt(chapter.earnedMarks) + "/" + (int) Data.getDoubleInInt(chapter.totalTestedMarks));

            holder.timeSpent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_tile_time, 0, 0, 0);
        } else {
            holder.gridLayoutGray.setVisibility(View.VISIBLE);
            holder.gridLayout.setVisibility(View.GONE);
            holder.rootGridLayout.setBackground(studyCentreActivity.getResources().getDrawable(R.drawable.grayshape));

            holder.textViewGray.setText(label);
            holder.timeSpentGray.setText(getDateFromMillis(chapter.timeSpent));
            holder.levelGray.setText(studyCentreActivity.getResources().getString(R.string.level_text) + Data.getInt(chapter.completedTopics));

            int max = Data.getInt(chapter.totalTopics);
            holder.progressBarGray.setMax(max == 0 ? 1 : max);
            holder.progressBarGray.setProgress(Data.getInt(chapter.completedTopics));

            getLevelDrawable(holder, chapter.completedTopics);

            holder.starGray.setText((int) Data.getDoubleInInt(chapter.earnedMarks) + "/" + (int) Data.getDoubleInInt(chapter.totalTestedMarks));
            holder.gridLayoutGray.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(studyCentreActivity, studyCentreActivity.getResources().getString(R.string.study_center_offline_click_text), Toast.LENGTH_SHORT).show();
                }
            });

            Drawable drawable = holder.timeSpentGray.getCompoundDrawables()[0];
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

            drawable = holder.levelGray.getCompoundDrawables()[0];
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

            drawable = holder.starGray.getCompoundDrawables()[0];
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        }
    }

    public void updateData(List<Chapters> chapters, String key) {
        this.chapters = chapters;
        this.key = key;
    }

    private void getAlertDialog(View v, StudyCenterSubjectViewHolder holder, Chapters chapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.gridLayout.getContext());
        LayoutInflater li = (LayoutInflater) studyCentreActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.layout_list_item_view_popup, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        setDataForAlert(dialog, dialogView, holder, chapter);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        // position the dialog
        wmlp.x = (int) v.getX() + 15;
        wmlp.y = (int) v.getY() + 140;
        dialog.show();
        dialog.getWindow().setAttributes(wmlp);
        dialog.getWindow().setLayout(300, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setDataForAlert(final AlertDialog dialog, View dialogView, StudyCenterSubjectViewHolder holder, final Chapters chapter) {
        TextView score = (TextView) dialogView.findViewById(R.id.score);
        score.setText(holder.star.getText().toString());
        score.setBackground(holder.gridLayout.getBackground());
        TextView notes = (TextView) dialogView.findViewById(R.id.notes);
        notes.setText(TextUtils.isEmpty(chapter.notesCount) ? "0" : chapter.notesCount);
        TextView completedTopics = (TextView) dialogView.findViewById(R.id.completed_topics);
        completedTopics.setText(String.format("%.2f", getCompletedTopicsPercentage(chapter)) + "%");
        dialogView.findViewById(R.id.take_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                // startExerciseActivity(chapter);
                startPartTest(chapter);
            }
        });
        dialogView.findViewById(R.id.start_reading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                startContentActivity(chapter);
            }
        });
        dialogView.findViewById(R.id.notes_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                startNotesActivity(chapter);
            }
        });
        dialogView.findViewById(R.id.offline_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                startOfflineActivity(chapter);
            }
        });
        dialogView.findViewById(R.id.flagged_questions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                startFlaggedQuestionView(chapter);
            }
        });
    }

    private void startFlaggedQuestionView(Chapters chapter) {
        Intent exerciseIntent = new Intent(studyCentreActivity, ExamEngineActivity.class);

        exerciseIntent.putExtra(Constants.TEST_TITLE, "Flagged Questions");
        exerciseIntent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.selectedCourse.courseId.toString());
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECTID, studyCentreActivity.getSelectedSubjectId());
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECT, key);
        exerciseIntent.putExtra(Constants.SELECTED_CHAPTERID, chapter.idCourseSubjectchapter);
        studyCentreActivity.startActivity(exerciseIntent);
    }

    private void startPartTest(Chapters chapter) {
        Intent exerciseIntent = new Intent(studyCentreActivity, TestStartActivity.class);
        exerciseIntent.putExtra(TestStartActivity.KEY_TEST_TYPE, Tests.CHAPTER.getType());
        exerciseIntent.putExtra(Constants.TEST_TITLE, key);
        exerciseIntent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.selectedCourse.courseId.toString());
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECTID, studyCentreActivity.getSelectedSubjectId());
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECT, key);
        exerciseIntent.putExtra(Constants.SELECTED_CHAPTERID, chapter.idCourseSubjectchapter);
        exerciseIntent.putExtra(Constants.SELECTED_CHAPTER_NAME, chapter.chapterName);
        studyCentreActivity.startActivity(exerciseIntent);

    }

    private void startNotesActivity(Chapters chapter) {
        Intent intent = new Intent(studyCentreActivity, NotesActivity.class);
        putIntentExtras(chapter, intent, COURSE_ID, SUBJECT_ID, CHAPTER_ID);
        studyCentreActivity.startActivity(intent);
    }

    private void startOfflineActivity(Chapters chapter) {
        Intent intent = new Intent(studyCentreActivity, OfflineSubjectActivity.class);
        putIntentExtras(chapter, intent, COURSE_ID, SUBJECT_ID, CHAPTER_ID);
        intent.putExtra("chapterName", chapter.chapterName);
        intent.putExtra("courseName", AbstractBaseActivity.selectedCourse.name.toString());
        intent.putExtra(SUBJECT, key);
        studyCentreActivity.startActivity(intent);
    }

   /* private void startExerciseActivity(Chapters chapters) {
        Intent intent = new Intent(studyCentreActivity, ExamEngineActivity.class);
        intent.putExtra(Constants.SELECTED_COURSE, "13" *//*AbstractBaseActivity.selectedCourse.courseId.toString()*//*);
        intent.putExtra(Constants.SELECTED_TOPICID, "1310");
        intent.putExtra(Constants.TEST_TITLE, "Take Test");
        intent.putExtra(Constants.SELECTED_TOPIC, "Take Test");
        studyCentreActivity.startActivity(intent);
    }*/

    private void startContentActivity(Chapters chapter) {
        Intent intent = new Intent(studyCentreActivity, ContentReadingActivity.class);
        putIntentExtras(chapter, intent, COURSE_ID, SUBJECT_ID, CHAPTER_ID);
        studyCentreActivity.startActivity(intent);
    }

    private void putIntentExtras(Chapters chapter, Intent intent, String courseId, String subjectId, String chapterId) {
        intent.putExtra(courseId, AbstractBaseActivity.selectedCourse.courseId.toString());
        intent.putExtra(subjectId, studyCentreActivity.getSelectedSubjectId());
        intent.putExtra(chapterId, chapter.idCourseSubjectchapter);
    }

    private double getCompletedTopicsPercentage(Chapters chapter) {
        int totalTopics = Integer.parseInt(TextUtils.isEmpty(chapter.totalTopics) ? "0" : chapter.totalTopics);
        int completedTopics = Integer.parseInt(TextUtils.isEmpty(chapter.completedTopics) ? "0" : chapter.completedTopics);
        double completedPercentage = (double) completedTopics / (double) totalTopics * 100;
        return Math.round(completedPercentage * 100.0) / 100.0;
    }

    private Drawable getColorDrawable(StudyCenterSubjectViewHolder holder, Chapters chapter) {
        double totalMarks = Data.getDoubleWithTwoDecimals(chapter.totalTestedMarks);
        double earnedMarks = Data.getDoubleWithTwoDecimals(chapter.earnedMarks);
        double scoreRedPercentage = Data.getInt(chapter.scoreRed) * totalMarks / 100;
        double scoreAmberPercentage = Data.getInt(chapter.scoreAmber) * totalMarks / 100;
        if (earnedMarks == 0 && totalMarks == 0) {
            return studyCentreActivity.getResources().getDrawable(R.drawable.blueshape);
        } else if (earnedMarks < scoreRedPercentage) {
            return studyCentreActivity.getResources().getDrawable(R.drawable.redshape);
        } else if (earnedMarks < scoreAmberPercentage) {
            return studyCentreActivity.getResources().getDrawable(R.drawable.ambershape);
        } else {
            return studyCentreActivity.getResources().getDrawable(R.drawable.greenshape);
        }
    }

    private void getLevelDrawable(StudyCenterSubjectViewHolder holder, String levelstr) {
        int level = 0;
        if (!TextUtils.isEmpty(levelstr)) {
            level = Data.getInt(levelstr);
        }
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

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public String getDateFromMillis(String millisStr) {
        if (!TextUtils.isEmpty(millisStr)) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            return formatter.format(new Date(Data.getLong(millisStr)));
        } else {
            return "00:00:00";
        }
    }

    public class StudyCenterSubjectViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView timeSpent;
        public TextView level;
        public TextView star;
        public TextView gridChildText;
        public LinearLayout gridLayout;
        public LinearLayout rootGridLayout;
        public TextView textViewGray;
        public TextView timeSpentGray;
        public TextView levelGray;
        public TextView starGray;
        public TextView gridChildTextGray;
        public LinearLayout gridLayoutGray;
        public ProgressBar progressBar;
        public ProgressBar progressBarGray;

        public StudyCenterSubjectViewHolder(View itemView) {
            super(itemView);
            gridLayout = (LinearLayout) itemView.findViewById(R.id.grid_layout_color);
            rootGridLayout = (LinearLayout) itemView.findViewById(R.id.grid_layout);
            textView = (TextView) itemView.findViewById(R.id.subject_name);
            timeSpent = (TextView) itemView.findViewById(R.id.clock);
            level = (TextView) itemView.findViewById(R.id.level);
            star = (TextView) itemView.findViewById(R.id.star);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_id);
            gridChildText = (TextView) itemView.findViewById(R.id.grid_child_text_view);
            gridLayoutGray = (LinearLayout) itemView.findViewById(R.id.grid_layout_gray);
            textViewGray = (TextView) itemView.findViewById(R.id.subject_name_gray);
            timeSpentGray = (TextView) itemView.findViewById(R.id.clock_gray);
            levelGray = (TextView) itemView.findViewById(R.id.level_gray);
            starGray = (TextView) itemView.findViewById(R.id.star_gray);
            progressBarGray = (ProgressBar) itemView.findViewById(R.id.progress_id_gray);
            gridChildTextGray = (TextView) itemView.findViewById(R.id.grid_child_text_view_gray);

        }
    }
}
