package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.CustomLegend;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 10/10/15.
 */
public class CustomLegendAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;

    public CustomLegendAdapter(int[] colors, String[] labels, LayoutInflater inflater) {
        this(colors, labels);
        this.inflater = inflater;

    }

    public CustomLegendAdapter(int[] colors, String[] labels) {
        List<CustomLegend> customLegendList = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            CustomLegend legend = new CustomLegend(colors[i], labels[i]);
            customLegendList.add(legend);
        }
        addAll(customLegendList);
    }

    @Override
    public CustomLegendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomLegendViewHolder(inflater.inflate(R.layout.row_custom_legend, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CustomLegendViewHolder) holder).bindData((CustomLegend) getItem(position));
    }

    public class CustomLegendViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_color)
        ImageView color;
        @Bind(R.id.tv_label)
        TextView label;

        View parent;

        public CustomLegendViewHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(CustomLegend legend) {
            color.setBackgroundColor(legend.color);
            label.setText(legend.label);


        }
    }
}
