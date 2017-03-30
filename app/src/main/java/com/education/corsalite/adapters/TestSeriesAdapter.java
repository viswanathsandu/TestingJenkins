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
import com.education.corsalite.models.responsemodels.TestSubject;

import java.text.DecimalFormat;

/**
 * Created by vissu on 3/28/17.
 */

public class TestSeriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private TestSubject mSubject;
    private iTestSeriesClickListener mListener;

    private static final int HEADER_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    public TestSeriesAdapter(iTestSeriesClickListener listener) {
        super();
        this.mListener = listener;
    }

    public void update(TestSubject subject) {
        mSubject = subject;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_series_header_item, parent, false);
                TestHeaderViewHolder headerViewHolder = new TestHeaderViewHolder(headerView);
                return headerViewHolder;
            case ITEM_TYPE:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_series_item, parent, false);
                TestViewHolder viewHolder = new TestViewHolder(v);
                return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HEADER_TYPE:
                ((TestHeaderViewHolder)holder).bindData(mListener, mSubject);
                break;
            case ITEM_TYPE:
                ((TestViewHolder)holder).bindData(mListener, mSubject.SubjectChapters.get(position));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER_TYPE : ITEM_TYPE;
    }

    @Override
    public int getItemCount() {
        if(mSubject == null || mSubject.SubjectChapters == null) {
            return 0;
        } else {
            return mSubject.SubjectChapters.size() + 1;
        }
    }

    public static class TestHeaderViewHolder extends RecyclerView.ViewHolder {

        TextView percentageTxt;
        TextView accuracyTxt;
        TextView marksTxt;
        TextView speedTxt;

        public TestHeaderViewHolder(View view) {
            super(view);
            percentageTxt = (TextView) view.findViewById(R.id.percentage_txt);
            marksTxt = (TextView) view.findViewById(R.id.marks_txt);
            speedTxt = (TextView) view.findViewById(R.id.speed_txt);
            accuracyTxt = (TextView) view.findViewById(R.id.accuracy_txt);
        }

        public void bindData(final iTestSeriesClickListener listener, final TestSubject subject) {
            if (subject != null) {

                marksTxt.setText(subject.EarnedMarks + "/" + subject.TotalTestedMarks);
                double timeTakenInMins = subject.TimeTaken;
                double speed = subject.EarnedMarks / timeTakenInMins;
                DecimalFormat df = new DecimalFormat("####0.00");
                speedTxt.setText(df.format(speed));
                double accuracy = subject.EarnedMarks / subject.TotalTestedMarks * 100;
                accuracyTxt.setText(Double.toString(accuracy));
            }
        }
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
            if (chapter != null) {
                if (!TextUtils.isEmpty(chapter.TestType)) {
                    if (chapter.TestType.equalsIgnoreCase("Chapter")) {
                        takeTestBTn.setText("Take Test");
                    } else if (chapter.TestType.toLowerCase().contains("mock")) {
                        takeTestBTn.setText("Mock Test");
                    }
                }
                takeTestBTn.setEnabled(chapter.AvailableTests != 0);
                if (takeTestBTn.isEnabled()) {
                    takeTestBTn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (takeTestBTn.getText().toString().equalsIgnoreCase("Take Test")) {
                                listener.onTakeTest(chapter);
                            } else if (takeTestBTn.getText().toString().equalsIgnoreCase("Mock Test")) {
                                listener.onMockTest(chapter);
                            }
                        }
                    });
                }
                titleTxt.setText(chapter.ChapterName);
                int allowedTests = Integer.parseInt(chapter.TestCountAllowed);
                int testsTaken = Integer.parseInt(chapter.TestCountTaken);
                int remainingTests = allowedTests - testsTaken;
                remainingTxt.setText(remainingTests + " of " + allowedTests + " remaining");
                maxQuestionsTxt.setText(chapter.NumberOfQuestions);
                marksTxt.setText(chapter.EarnedMarks + "/" + chapter.TotalTestedMarks);
                double timeTakenInMins = chapter.TimeTaken;
                double speed = chapter.EarnedMarks / timeTakenInMins;
                DecimalFormat df = new DecimalFormat("####0.00");
                speedTxt.setText(df.format(speed));
                double accuracy = chapter.EarnedMarks / chapter.TotalTestedMarks * 100;
                accuracyTxt.setText(Double.toString(accuracy));
            }
        }
    }
}
