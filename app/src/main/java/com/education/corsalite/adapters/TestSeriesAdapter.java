package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.listener.iTestSeriesClickListener;
import com.education.corsalite.models.responsemodels.TestChapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vissu on 3/28/17.
 */

public class TestSeriesAdapter extends RecyclerView.Adapter<TestSeriesAdapter.TestViewHolder> {

    private List<TestChapter> mChapters;
    private iTestSeriesClickListener mListener;

    public TestSeriesAdapter(iTestSeriesClickListener listener) {
        super();
        mChapters = new ArrayList<>();
        this.mListener = listener;
    }

    public void update(List<TestChapter> chapters) {
        if(mChapters != null) {
            mChapters.clear();;
            mChapters.addAll(chapters);
        } else {
            mChapters = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_series_item, parent, false);
        TestViewHolder viewHolder = new TestViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        holder.bindData(mListener, mChapters.get(position));
    }

    @Override
    public int getItemCount() {
        return mChapters == null ? 0 : mChapters.size();
    }

    public static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView titleTxt;
        TextView remainingTxt;
        TextView accuracyTxt;
        TextView maxQuestionsTxt;
        TextView marksTxt;
        TextView speedTxt;
        Button takeTestBTn;

        public TestViewHolder(View view) {
            super(view);
            titleTxt = (TextView) view.findViewById(R.id.title_txt);
            remainingTxt = (TextView) view.findViewById(R.id.remaining_txt);
            maxQuestionsTxt = (TextView) view.findViewById(R.id.max_questions_txt);
            marksTxt = (TextView) view.findViewById(R.id.marks_txt);
            speedTxt = (TextView) view.findViewById(R.id.speed_txt);
            accuracyTxt = (TextView) view.findViewById(R.id.accuracy_txt);
            takeTestBTn = (Button) view.findViewById(R.id.take_test_btn);
        }

        public void bindData(final iTestSeriesClickListener listener, final TestChapter chapter) {
            if(chapter != null) {
                if(!TextUtils.isEmpty(chapter.TestType)) {
                    if (chapter.TestType.equalsIgnoreCase("Chapter")) {
                        takeTestBTn.setText("Take Test");
                    } else if (chapter.TestType.toLowerCase().contains("mock")) {
                        takeTestBTn.setText("Mock Test");
                    }
                }
                takeTestBTn.setEnabled(!TextUtils.isEmpty(chapter.AvailableTests) && !chapter.AvailableTests.equalsIgnoreCase("0"));
                if(takeTestBTn.isEnabled()) {
                    takeTestBTn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(takeTestBTn.getText().toString().equalsIgnoreCase("Take Test")) {
                                listener.onTakeTest(chapter);
                            } else if(takeTestBTn.getText().toString().equalsIgnoreCase("Mock Test")) {
                                listener.onMockTest(chapter);
                            }
                        }
                    });
                }
                titleTxt.setText(chapter.ChapterName);
                remainingTxt.setText(chapter.AvailableTests + " remaining");
                maxQuestionsTxt.setText(chapter.NumberOfQuestions);
                marksTxt.setText(chapter.EarnedMarks + "/" + chapter.TotalTestedMarks);
                double timeTakenInMins = Double.parseDouble(chapter.TimeTaken);
                double earnedMarks = Double.parseDouble(chapter.EarnedMarks);
                double speed = earnedMarks / timeTakenInMins;
                DecimalFormat df = new DecimalFormat("####0.00");
                speedTxt.setText(df.format(speed));
            }
        }
    }
}
