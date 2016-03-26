package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.PartTestGridElement;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by madhuri on 3/26/16.
 */
public class PartTestGridAdapter extends AbstractRecycleViewAdapter{

    LayoutInflater inflater;
    public PartTestGridAdapter(List<PartTestGridElement> elementList, LayoutInflater inflater) {
        this(elementList);
        this.inflater = inflater;
    }
    private PartTestGridAdapter(List<PartTestGridElement> elementList) {
        addAll(elementList);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GridViewHolder(inflater.inflate(R.layout.parttest_grid_row, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((GridViewHolder) holder).bindData(position, (PartTestGridElement)getItem(position));
    }

    public class GridViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.tv_chapter)TextView tvChapter;
        @Bind(R.id.tv_recommended)EditText tvRecommended;
        @Bind(R.id.tv_questions)TextView tvQuestions;
        View parent;
        public GridViewHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final PartTestGridElement element) {
            tvChapter.setText(element.chapterName);
            tvRecommended.setText(element.recommendedQuestionCount+"");
            tvQuestions.setText(element.questionCount+"");
        }
    }
}
