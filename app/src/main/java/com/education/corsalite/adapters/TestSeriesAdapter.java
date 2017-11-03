package com.education.corsalite.adapters;

import android.content.Context;
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
import com.education.corsalite.models.responsemodels.TestSeriesMockData;
import com.education.corsalite.models.responsemodels.TestSubject;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by vissu on 3/28/17.
 */

public class TestSeriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private TestSubject mSubject;
    private List<TestSeriesMockData> mMockTexts;
    private iTestSeriesClickListener mListener;

    private static final int HEADER_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    public TestSeriesAdapter(Context context, iTestSeriesClickListener listener) {
        super();
        this.mContext = context;
        this.mListener = listener;
    }

    public void update(TestSubject subject, List<TestSeriesMockData> mockTests) {
        this.mSubject = subject;
        this.mMockTexts = mockTests;
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
                ((TestHeaderViewHolder) holder).bindData(mListener, mSubject);
                break;
            case ITEM_TYPE:
                if (position < (getHeaderCount() + getChapterCount())) {
                    ((TestViewHolder) holder).bindData(mListener, mSubject.SubjectChapters.get(position - getHeaderCount()));
                } else {
                    ((TestViewHolder) holder).bindData(mListener, mMockTexts.get(position - getChapterCount() - getHeaderCount()));
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER_TYPE : ITEM_TYPE;
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getChapterCount() + getMockCount();
    }

    private int getHeaderCount() {
        return getChapterCount() == 0 && getMockCount() == 0 ? 0 : 1;
    }

    private int getChapterCount() {
        return (mSubject == null || mSubject.SubjectChapters == null) ? 0 : mSubject.SubjectChapters.size();
    }

    private int getMockCount() {
        return mMockTexts == null ? 0 : mMockTexts.size();
    }

    public static class TestHeaderViewHolder extends RecyclerView.ViewHolder {

        TextView percentageTxt;
        TextView accuracyTxt;
        TextView speedTxt;
        TextView marksTxt;

        public TestHeaderViewHolder(View view) {
            super(view);
            percentageTxt = (TextView) view.findViewById(R.id.percentage_txt);
            marksTxt = (TextView) view.findViewById(R.id.marks_txt);
            speedTxt = (TextView) view.findViewById(R.id.speed_txt);
            accuracyTxt = (TextView) view.findViewById(R.id.accuracy_txt);
        }

        public void bindData(final iTestSeriesClickListener listener, final TestSubject subject) {
            if (subject != null) {
                percentageTxt.setText(subject.TestCountTaken + "");
                marksTxt.setText(subject.EarnedMarks + "/" + subject.TotalTestedMarks);
                double timeTakenInSecs = subject.TimeTaken;
                if (timeTakenInSecs > 0) {
                    double speed = subject.EarnedMarks / (timeTakenInSecs / 60);
                    DecimalFormat df = new DecimalFormat("####0.00");
                    speedTxt.setText(df.format(speed) + " marks/min");
                }
                if (subject.TotalTestedMarks > 0) {
                    double accuracy = subject.EarnedMarks / subject.TotalTestedMarks;
                    double accuracyPercentage = accuracy * 100;
                    DecimalFormat df = new DecimalFormat("####0.00");
                    accuracyTxt.setText(df.format(accuracyPercentage) + "%");
                }
            }
        }
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {

        ViewGroup containerLayout;
        TextView titleTxt;
        TextView remainingTxt;
        TextView accuracyTxt;
        TextView maxQuestionsTxt;
        TextView marksTxt;
        TextView speedTxt;
        Button takeTestBTn;

        public TestViewHolder(View view) {
            super(view);
            containerLayout = (ViewGroup) view.findViewById(R.id.container_layout);
            titleTxt = (TextView) view.findViewById(R.id.title_txt);
            remainingTxt = (TextView) view.findViewById(R.id.remaining_txt);
            maxQuestionsTxt = (TextView) view.findViewById(R.id.max_questions_txt);
            marksTxt = (TextView) view.findViewById(R.id.marks_txt);
            speedTxt = (TextView) view.findViewById(R.id.speed_txt);
            accuracyTxt = (TextView) view.findViewById(R.id.accuracy_txt);
            takeTestBTn = (Button) view.findViewById(R.id.take_test_btn);
        }

        private void bindData(final iTestSeriesClickListener listener, final TestSeriesMockData mockTest) {
            if (mockTest != null) {
                if (!TextUtils.isEmpty(mockTest.testType)) {
                    if (mockTest.testType.toLowerCase().contains("mock")) {
                        takeTestBTn.setText("Mock Test");
                        try {
                            int testsCount = Integer.parseInt(mockTest.testCountAllowed);
                            int testsTaken = Integer.parseInt(mockTest.testCountTaken);
                            int remainingTests = testsCount - testsTaken;
                            takeTestBTn.setEnabled(remainingTests > 0);
                        } catch (NumberFormatException e) {
                            L.error(e.getMessage(), e);
                            takeTestBTn.setEnabled(false);
                        }
                    }
                }
                try {
                    if (!SystemUtils.isNetworkConnected(mContext)) {
                        takeTestBTn.setEnabled(false);
                        containerLayout.setBackgroundResource(R.color.gray);
                    } else {
                        containerLayout.setBackgroundResource(R.color.white);
                    }
                    if (takeTestBTn.isEnabled()) {
                        takeTestBTn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onMockTest(mockTest);
                            }
                        });
                    }
                    speedTxt.setText("0");
                    accuracyTxt.setText("0");
                    titleTxt.setText(mockTest.testName);
                    int allowedTests = Integer.parseInt(mockTest.testCountAllowed);
                    int testsTaken = Integer.parseInt(mockTest.testCountTaken);
                    int remainingTests = allowedTests - testsTaken;
                    remainingTxt.setText(remainingTests + " of " + allowedTests + " remaining");
                    maxQuestionsTxt.setText(mockTest.numberOfQuestionsPerTestLimit);
                    DecimalFormat df = new DecimalFormat("####0");
                    DecimalFormat intFormat = new DecimalFormat("####0");
                    marksTxt.setText(intFormat.format(Double.parseDouble(mockTest.earnedScore)) + "/"
                            + intFormat.format(Double.parseDouble(mockTest.totalTestedMarks)));
                    double timeTakenInSecs = Double.parseDouble(mockTest.timeTaken);
                    if (timeTakenInSecs > 0) {
                        double speed = Double.parseDouble(mockTest.earnedScore) / (timeTakenInSecs / 60);
                        speedTxt.setText(df.format(speed));
                    }
                    double accuracy = 0;
                    if (Double.parseDouble(mockTest.totalTestedMarks) > 0) {
                        accuracy = Double.parseDouble(mockTest.earnedScore) / Double.parseDouble(mockTest.totalTestedMarks) * 100;
                    }
                    accuracyTxt.setText((accuracy == 0 ? intFormat : df).format(accuracy));
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                }
            }
        }

        public void bindData(final iTestSeriesClickListener listener, final TestChapter chapter) {

            if (chapter != null) {
                if (!TextUtils.isEmpty(chapter.TestType)) {
                    if (chapter.TestType.equalsIgnoreCase("Chapter")) {
                        takeTestBTn.setText("Take Test");
                        takeTestBTn.setEnabled(chapter.AvailableTests != null && chapter.AvailableTests != 0);
                    } else if (chapter.TestType.toLowerCase().contains("mock")) {
                        takeTestBTn.setText("Mock Test");
                        try {
                            int testsCount = Integer.parseInt(chapter.TestCountAllowed);
                            int testsTaken = Integer.parseInt(chapter.TestCountTaken);
                            int remainingTests = testsCount - testsTaken;
                            takeTestBTn.setEnabled(remainingTests > 0);
                        } catch (NumberFormatException e) {
                            L.error(e.getMessage(), e);
                            takeTestBTn.setEnabled(false);
                        }
                    }
                }
                if (!SystemUtils.isNetworkConnected(mContext)) {
                    takeTestBTn.setEnabled(false);
                    containerLayout.setBackgroundResource(R.color.gray);
                } else {
                    containerLayout.setBackgroundResource(R.color.white);
                }
                try {
                    if (takeTestBTn.isEnabled()) {
                        takeTestBTn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (takeTestBTn.getText().toString().equalsIgnoreCase("Take Test")) {
                                    listener.onTakeTest(chapter);
                                } else if (takeTestBTn.getText().toString().equalsIgnoreCase("Mock Test")) {
                                    listener.onMockTest(chapter);
                                } else if (takeTestBTn.getText().toString().equalsIgnoreCase("Recommended Reading")) {
                                    listener.onRecommendedReading(chapter);
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
                    DecimalFormat intFormat = new DecimalFormat("####0");
                    DecimalFormat df = new DecimalFormat("####0.00");
                    if (chapter.EarnedMarks != null) {
                        marksTxt.setText(intFormat.format(chapter.EarnedMarks) + "/" + intFormat.format(chapter.TotalTestedMarks));
                        double timeTakenInSecs = chapter.TimeTaken;
                        if (timeTakenInSecs > 0) {
                            double speed = chapter.EarnedMarks / (timeTakenInSecs / 60);
                            speedTxt.setText(df.format(speed));
                        }
                    }
                    double accuracy = 0;
                    if (chapter.TotalTestedMarks != null && chapter.TotalTestedMarks > 0) {
                        accuracy = chapter.EarnedMarks / chapter.TotalTestedMarks * 100;
                    }
                    accuracyTxt.setText((accuracy == 0 ? intFormat : df).format(accuracy));
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                }
            }
        }
    }
}
