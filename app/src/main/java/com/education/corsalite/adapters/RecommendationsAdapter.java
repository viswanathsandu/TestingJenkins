package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.CourseAnalysis;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 01/10/15.
 */
public class RecommendationsAdapter extends  AbstractRecycleViewAdapter {
    LayoutInflater inflater;

    public RecommendationsAdapter(List<CourseAnalysis> courseAnalysisList, LayoutInflater inflater) {
        this(courseAnalysisList);
        this.inflater = inflater;
    }
    public RecommendationsAdapter(List<CourseAnalysis> courseAnalysisList) {
        addAll(courseAnalysisList);
    }

    @Override
    public RecommendationsDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecommendationsDataHolder(inflater.inflate(R.layout.row_recommended_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecommendationsDataHolder) holder).bindData(position, (CourseAnalysis) getItem(position));
    }

    public class RecommendationsDataHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_subject_name)TextView subject;
        @Bind(R.id.tv_chapter)TextView chapter;
        @Bind(R.id.tv_topic)TextView topic;
        @Bind(R.id.tv_date)TextView dateTime;
        @Bind(R.id.tv_marks)TextView marks;
        @Bind(R.id.tv_accuracy)TextView accuracy;
        @Bind(R.id.tv_speed)TextView speed;
        View parent;

        public RecommendationsDataHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final CourseAnalysis course) {
            if((position+1)% 2 == 0) {
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.tab_recycler_alternate_row));
            }
            subject.setText(course.subjectName);
            chapter.setText(course.chapterName);
            topic.setText(course.topic);
            dateTime.setText(course.date);
            marks.setText(course.earnedMarks);
            accuracy.setText(course.accuracy);
            //TODO :accuracy color red,amber or green
            speed.setText(course.speed);

        }
    }
}
