package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;

/**
 * Created by vissu on 2/21/16.
 */
public class MockSubjectsAdapter extends AbstractRecycleViewAdapter<String, MockSubjectsAdapter.MockSubjectViewHolder> {

    private int selectedItem = 0;
    private OnMockSectionClickListener onMockSectionClickListener;

    public void setOnMockSubjectClickListener(OnMockSectionClickListener listener) {
        this.onMockSectionClickListener = listener;
    }

    public void setSelectedItem(String section) {
        for (int i = 0; i < getItemCount(); i++) {
            if (section.equalsIgnoreCase(getItem(i))) {
                selectedItem = i;
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public MockSubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mock_subject_tab, parent, false);
        return new MockSubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MockSubjectViewHolder holder, final int position) {
        holder.subjectTxt.setSelected(position == selectedItem);
        holder.subjectTxt.setText(getItem(position));
        holder.subjectTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selectedItem);
                selectedItem = position;
                notifyItemChanged(selectedItem);
                if (onMockSectionClickListener != null) {
                    onMockSectionClickListener.onSectionClick(getItem(position));
                }
            }
        });
    }

    public class MockSubjectViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectTxt;

        public MockSubjectViewHolder(View itemView) {
            super(itemView);
            subjectTxt = (TextView) itemView.findViewById(R.id.subject_txt);
        }
    }

    public interface OnMockSectionClickListener {
        void onSectionClick(String section);
    }
}
