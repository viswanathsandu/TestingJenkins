package com.corsalite.tabletapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.models.responsemodels.RecommendedModel;
import com.corsalite.tabletapp.utils.AnalyticsHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 01/10/15.
 */
public class RecommendationsAdapter extends AbstractRecycleViewAdapter {
    private SetOnRecommendationClickListener setOnRecommendationClickListener;
    LayoutInflater inflater;

    public interface SetOnRecommendationClickListener {
        void onItemClick(int position);
    }

    public RecommendationsAdapter(List<RecommendedModel> recommendedModels, LayoutInflater inflater, SetOnRecommendationClickListener setOnRecommendationClickListener) {
        this(recommendedModels);
        this.inflater = inflater;
        this.setOnRecommendationClickListener = setOnRecommendationClickListener;
    }

    public RecommendationsAdapter(List<RecommendedModel> recommendedModels) {
        addAll(recommendedModels);
    }

    @Override
    public RecommendationsDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecommendationsDataHolder(inflater.inflate(R.layout.row_recommended_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecommendationsDataHolder) holder).bindData(position, (RecommendedModel) getItem(position));
    }

    public class RecommendationsDataHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_subject_name)
        TextView subject;
        @Bind(R.id.tv_chapter)
        TextView chapter;
        @Bind(R.id.tv_topic)
        TextView topic;
        @Bind(R.id.tv_date)
        TextView dateTime;
        @Bind(R.id.tv_marks)
        TextView marks;
        @Bind(R.id.tv_accuracy)
        TextView accuracy;
        @Bind(R.id.tv_speed)
        TextView speed;
        View parent;

        public RecommendationsDataHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final RecommendedModel recommendedModel) {
            if ((position + 1) % 2 == 0) {
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.tab_recycler_alternate_row));
            } else {
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.white));
            }
            subject.setText(recommendedModel.subjectName);
            chapter.setText(recommendedModel.chapterName);
            topic.setText(recommendedModel.topicName);
            dateTime.setText(recommendedModel.recentTestDate);
            marks.setText(AnalyticsHelper.truncateString(recommendedModel.totalTestedMarks));
            accuracy.setText(AnalyticsHelper.truncateString(recommendedModel.accuracy));
            speed.setText(AnalyticsHelper.truncateString(recommendedModel.speed));
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setOnRecommendationClickListener.onItemClick(position);
                }
            });

        }
    }
}
