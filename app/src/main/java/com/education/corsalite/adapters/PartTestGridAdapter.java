package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.PartTestGridElement;
import com.education.corsalite.views.InputFilterMinMax;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by madhuri on 3/26/16.
 */
public class PartTestGridAdapter extends AbstractRecycleViewAdapter {

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
        ((GridViewHolder) holder).bindData(position, (PartTestGridElement) getItem(position));
    }

    public List<PartTestGridElement> getListData() {
        List<PartTestGridElement> elements = new ArrayList<>();
        for(Object item : data) {
            if(item instanceof PartTestGridElement) {
                String count = ((PartTestGridElement)item).recommendedQuestionCount;
                if(!TextUtils.isEmpty(count) && !count.equalsIgnoreCase("0")) {
                    elements.add((PartTestGridElement) item);
                }
            }
        }
        return elements;
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_chapter)
        TextView tvChapter;
        @Bind(R.id.tv_recommended)
        EditText etRecommended;
        @Bind(R.id.tv_questions)
        TextView tvQuestions;
        View parent;

        public GridViewHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, PartTestGridElement element) {
            if (TextUtils.isEmpty(element.questionCount)) {
                element.questionCount = "0";
                element.recommendedQuestionCount = "0";
                ((PartTestGridElement) data.get(position)).questionCount = "0";
                ((PartTestGridElement) data.get(position)).recommendedQuestionCount = "0";
            }
            tvChapter.setText(element.chapterName);
            etRecommended.setText(element.recommendedQuestionCount + "");
            etRecommended.setFilters(new InputFilter[]{new InputFilterMinMax("0", element.questionCount+"")});
            etRecommended.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    if (etRecommended.getText().toString().length() > 0) {
                        ((PartTestGridElement) data.get(position)).recommendedQuestionCount = etRecommended.getText().toString();
                    } else {
                        ((PartTestGridElement) data.get(position)).recommendedQuestionCount = "0";
                    }
                }
            });
            tvQuestions.setText(element.questionCount + "");
        }
    }
}
