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
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ContentReadingActivity;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.activities.NotesActivity;
import com.education.corsalite.activities.SaveForOfflineActivity;
import com.education.corsalite.activities.StudyCentreActivity;
import com.education.corsalite.activities.TestStartActivity;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.enums.Tests;
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.services.TestDownloadService;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.Data;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.Gson;
import com.localytics.android.Localytics;

import java.util.List;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.StudyCenterSubjectViewHolder> {
    public static final String COURSE_ID = "courseId";
    public static final String SUBJECT_ID = "subjectId";
    public static final String CHAPTER_ID = "chapterId";
    public static final String TOPIC_ID = "topicId";
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_centre_grid_view_new, parent, false);
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
            holder.level.setText(studyCentreActivity.getResources().getString(R.string.level_text) + " " + (chapter.passedComplexity + 1));
            holder.gridLayout.setBackgroundDrawable(getColorDrawable(holder, chapter));
            holder.rootGridLayout.setBackgroundDrawable(getColorDrawable(holder, chapter));
            int totalTopics = Data.getInt(chapter.totalTopics);
            int completedTopics = Data.getInt(chapter.completedTopics);
            int percentage = (completedTopics != 0)
                                ? completedTopics * 100 /totalTopics
                                : 0;
            holder.progressBar.setMax(100);
            holder.progressBar.setProgress(percentage);
            getLevelDrawable(holder, chapter.passedComplexity+1, holder.level);
            holder.star.setText((int) Data.getDoubleInInt(chapter.earnedMarks) + "/" + (int) Data.getDoubleInInt(chapter.totalTestedMarks));
            holder.timeSpent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_tile_time, 0, 0, 0);
            holder.timeSpent.getCompoundDrawables()[0] = holder.timeSpent.getCompoundDrawables()[0].mutate();
            holder.timeSpent.getCompoundDrawables()[0].setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            holder.level.getCompoundDrawables()[1] = holder.level.getCompoundDrawables()[1].mutate();
            holder.level.getCompoundDrawables()[1].setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            holder.star.getCompoundDrawables()[1] = holder.star.getCompoundDrawables()[1].mutate();
            holder.star.getCompoundDrawables()[1].setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        } else {
            holder.gridLayoutGray.setVisibility(View.VISIBLE);
            holder.gridLayout.setVisibility(View.GONE);
            holder.gridLayoutGray.setBackgroundDrawable(studyCentreActivity.getResources().getDrawable(R.drawable.grayshape));
            holder.rootGridLayout.setBackgroundDrawable(studyCentreActivity.getResources().getDrawable(R.drawable.grayshape));
            holder.textViewGray.setText(label);
            holder.timeSpentGray.setText(getDateFromMillis(chapter.timeSpent));
            holder.levelGray.setText(studyCentreActivity.getResources().getString(R.string.level_text) + " " + (chapter.passedComplexity + 1));
            int totalTopics = Data.getInt(chapter.totalTopics);
            int completedTopics = Data.getInt(chapter.completedTopics);
            int percentage = (completedTopics != 0)
                    ? completedTopics * 100 /totalTopics
                    : 0;
            holder.progressBarGray.setMax(100);
            holder.progressBarGray.setProgress(percentage);
            getLevelDrawable(holder, chapter.passedComplexity+1, holder.levelGray);
            holder.starGray.setText((int) Data.getDoubleInInt(chapter.earnedMarks) + "/" + (int) Data.getDoubleInInt(chapter.totalTestedMarks));
            holder.gridLayoutGray.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(studyCentreActivity, studyCentreActivity.getResources().getString(R.string.study_center_offline_click_text), Toast.LENGTH_SHORT).show();
                }
            });
            holder.timeSpentGray.getCompoundDrawables()[0] = holder.timeSpentGray.getCompoundDrawables()[0].mutate();
            holder.timeSpentGray.getCompoundDrawables()[0].setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            holder.levelGray.getCompoundDrawables()[1] = holder.levelGray.getCompoundDrawables()[1].mutate();
            holder.levelGray.getCompoundDrawables()[1].setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            holder.starGray.getCompoundDrawables()[1] = holder.starGray.getCompoundDrawables()[1].mutate();
            holder.starGray.getCompoundDrawables()[1].setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
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
                startTakeTest(chapter);
            }
        });
        dialogView.findViewById(R.id.download_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadTakeTest(chapter);
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

    private void startTakeTest(Chapters chapter) {
        Intent exerciseIntent = new Intent(studyCentreActivity, TestStartActivity.class);
        exerciseIntent.putExtra(TestStartActivity.KEY_TEST_TYPE, Tests.CHAPTER.getType());
        exerciseIntent.putExtra(Constants.TEST_TITLE, key);
        exerciseIntent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.selectedCourse.courseId.toString());
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECTID, studyCentreActivity.getSelectedSubjectId());
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECT, key);
        exerciseIntent.putExtra(Constants.SELECTED_CHAPTERID, chapter.idCourseSubjectchapter);
        exerciseIntent.putExtra(Constants.SELECTED_CHAPTER_NAME, chapter.chapterName);
        exerciseIntent.putExtra(Constants.LEVEL_CROSSED, chapter.passedComplexity);
        studyCentreActivity.startActivity(exerciseIntent);

    }

    private void downloadTakeTest(Chapters chapter) {
        Intent exerciseIntent = new Intent(studyCentreActivity, TestDownloadService.class);
        exerciseIntent.putExtra("subjectId", studyCentreActivity.getSelectedSubjectId());
        exerciseIntent.putExtra("chapterId", chapter.idCourseSubjectchapter);
        exerciseIntent.putExtra("selectedTakeTest",new Gson().toJson(chapter));
        exerciseIntent.putExtra("courseId",AbstractBaseActivity.selectedCourse.courseId.toString());
        exerciseIntent.putExtra("entityId", LoginUserCache.getInstance().loginResponse.entitiyId);
        studyCentreActivity.startService(exerciseIntent);
        Toast.makeText(studyCentreActivity, "Downloading test paper in background", Toast.LENGTH_SHORT).show();

    }


    private void startNotesActivity(Chapters chapter) {
        Intent intent = new Intent(studyCentreActivity, NotesActivity.class);
        putIntentExtras(chapter, intent, COURSE_ID, SUBJECT_ID, CHAPTER_ID);
        studyCentreActivity.startActivity(intent);
    }

    private void startOfflineActivity(Chapters chapter) {
        Intent intent = new Intent(studyCentreActivity, SaveForOfflineActivity.class);
        putIntentExtras(chapter, intent, COURSE_ID, SUBJECT_ID, CHAPTER_ID);
        intent.putExtra("chapterName", chapter.chapterName);
        intent.putExtra("courseName", AbstractBaseActivity.selectedCourse.name.toString());
        intent.putExtra(SUBJECT, key);
        studyCentreActivity.startActivity(intent);
    }

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

    private void getLevelDrawable(StudyCenterSubjectViewHolder holder, int levelstr, TextView level) {
        switch (levelstr) {
            case 2:
                level.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.level_two, 0, 0);
                break;
            case 3:
                level.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.level_three, 0, 0);
                break;
            case 4:
                level.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.level_four, 0, 0);
                break;
            case 5:
                level.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.level_five, 0, 0);
                break;
            case 0:
            case 1:
            default:
                level.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.level_one, 0, 0);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public String getDateFromMillis(String secondsStr) {
        if (!TextUtils.isEmpty(secondsStr)) {
            long seconds = Data.getLong(secondsStr);
            return String.format("%d:%02d:%02d", seconds/3600, (seconds%3600)/60, (seconds%60));
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
        public DonutProgress progressBar;
        public DonutProgress progressBarGray;

        public StudyCenterSubjectViewHolder(View itemView) {
            super(itemView);
            gridLayout = (LinearLayout) itemView.findViewById(R.id.grid_layout_color);
            rootGridLayout = (LinearLayout) itemView.findViewById(R.id.grid_layout);
            textView = (TextView) itemView.findViewById(R.id.subject_name);
            timeSpent = (TextView) itemView.findViewById(R.id.clock);
            level = (TextView) itemView.findViewById(R.id.level);
            star = (TextView) itemView.findViewById(R.id.star);
            progressBar = (DonutProgress) itemView.findViewById(R.id.progress_id);
            gridChildText = (TextView) itemView.findViewById(R.id.grid_child_text_view);
            gridLayoutGray = (LinearLayout) itemView.findViewById(R.id.grid_layout_gray);
            textViewGray = (TextView) itemView.findViewById(R.id.subject_name_gray);
            timeSpentGray = (TextView) itemView.findViewById(R.id.clock_gray);
            levelGray = (TextView) itemView.findViewById(R.id.level_gray);
            starGray = (TextView) itemView.findViewById(R.id.star_gray);
            progressBarGray = (DonutProgress) itemView.findViewById(R.id.progress_id_gray);
            gridChildTextGray = (TextView) itemView.findViewById(R.id.grid_child_text_view_gray);

        }
    }
}
