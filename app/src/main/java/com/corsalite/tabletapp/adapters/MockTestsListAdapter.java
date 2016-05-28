package com.corsalite.tabletapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.models.db.MockTest;

import java.util.List;

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

    public void setMockTestSelectedListener(IMockTestSelectedListener mListener) {
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

        private TextView tvName;
        private ImageView ivDownload;

        public MockTestDataHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.mock_test_txt);
            ivDownload = (ImageView) view.findViewById(R.id.download_test);
        }

        public void bindData(final int position, final MockTest mockTest) {
            tvName.setText(mockTest.displayName+"");
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onMockTestSelected(position);
                    }
                }
            });
            ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onMockTestDownload(position);
                    }
                }
            });
        }
    }

    public interface IMockTestSelectedListener {
        void onMockTestSelected(int position);
        void onMockTestDownload(int position);
    }
}
