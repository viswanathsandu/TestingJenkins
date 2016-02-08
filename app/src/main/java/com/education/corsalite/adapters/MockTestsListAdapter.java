package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.MockTest;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Girish on 18/10/15.
 */
public class MockTestsListAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;
    IMockTestSelectedListener mListener;

    public MockTestsListAdapter(List<MockTest> videoList, LayoutInflater inflater) {
        this(videoList);
        this.inflater = inflater;
    }

    private MockTestsListAdapter(List<MockTest> videoList) {
        addAll(videoList);
    }

    public void setMockTestSelectedListener(IMockTestSelectedListener mListener){
        this.mListener = mListener;
    }

    @Override
    public MockTestDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MockTestDataHolder(inflater.inflate(R.layout.mocktest_spinner_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MockTestDataHolder) holder).bindData(position, (MockTest) getItem(position));
    }

    public class MockTestDataHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.mock_test_txt) TextView tvName;
        View parent;

        public MockTestDataHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final MockTest mockTest) {
            tvName.setText(mockTest.examName);
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(mListener != null) {
                    mListener.onMockTestSelected(position);
                }
                }
            });
        }
    }

    public interface IMockTestSelectedListener{
        void onMockTestSelected(int position);
    }
}
