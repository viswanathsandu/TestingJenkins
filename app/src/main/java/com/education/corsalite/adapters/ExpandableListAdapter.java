package com.education.corsalite.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.TestInstructionsActivity;
import com.education.corsalite.activities.TestStartActivity;
import com.education.corsalite.enums.Tests;
import com.education.corsalite.event.ScheduledTestStartEvent;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.MockTest;
import com.education.corsalite.models.db.OfflineTestObjectModel;
import com.education.corsalite.models.responsemodels.Chapter;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by madhuri on 2/28/16.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> headers; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<OfflineTestObjectModel>> childs;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<OfflineTestObjectModel>> listChildData) {
        this.context = context;
        this.headers = listDataHeader;
        this.childs = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        try {
            if (headers.get(groupPosition).equalsIgnoreCase("Mock Test")) {
                MockTest test = this.childs.get(this.headers.get(groupPosition)).get(childPosititon).mockTest;
                if (TextUtils.isEmpty(test.subjectId)) {
                    return test.displayName;
                } else {
                    return test.examName + " ( " + test.subjectName + " ) ";
                }
            } else if (headers.get(groupPosition).equalsIgnoreCase("Scheduled Test")) {
                return this.childs.get(this.headers.get(groupPosition)).get(childPosititon).scheduledTest.examName;
            } else if (headers.get(groupPosition).equalsIgnoreCase("Take Test")) {
                return this.childs.get(this.headers.get(groupPosition)).get(childPosititon).baseTest.chapter.chapterName;
            } else {
                return this.childs.get(this.headers.get(groupPosition)).get(childPosititon).baseTest.subjectName;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.offline_test_child_item, null);
        }
        OfflineTestObjectModel model = childs.get(this.headers.get(groupPosition)).get(childPosition);
        TextView txtListChild = (TextView) convertView.findViewById(R.id.mock_test_txt);
        TextView textViewTime = (TextView) convertView.findViewById(R.id.mock_test_time_txt);
        Button textViewStatus = (Button) convertView.findViewById(R.id.test_status);
        ImageView ivStatus = (ImageView) convertView.findViewById(R.id.iv_status);
        if (model.status == Constants.STATUS_COMPLETED) {
            textViewStatus.setText("Completed");
            ivStatus.setImageResource(R.drawable.ico_correct);
        } else if (model.status == Constants.STATUS_START) {
            textViewStatus.setText("Start");
            ivStatus.setImageResource(R.drawable.ico_yettoattempt);
        } else if (model.status == Constants.STATUS_SUSPENDED) {
            textViewStatus.setText("Restart");
            ivStatus.setImageResource(R.drawable.ico_notattended);
        }
        if (headers.get(groupPosition).equals("Scheduled Test")) {
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = df.parse(childs.get(this.headers.get(groupPosition)).get(childPosition).scheduledTest.startTime);
                textViewTime.setText(TimeUtils.getDateTime(date.getTime()));
            } catch(Exception e) {
                L.error(e.getMessage(), e);
                textViewTime.setText(TimeUtils.getDateTime(childs.get(this.headers.get(groupPosition)).get(childPosition).dateTime));
            }
        } else {
            textViewTime.setText(TimeUtils.getDateTime(childs.get(this.headers.get(groupPosition)).get(childPosition).dateTime));
        }
        txtListChild.setText(childText);
        textViewStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (headers.get(groupPosition).equals("Mock Test")) {
                    startMockTest(childs.get(headers.get(groupPosition)).get(childPosition));
                } else if (headers.get(groupPosition).equals("Scheduled Test")) {
                    startScheduleTest(childs.get(headers.get(groupPosition)).get(childPosition));
                } else if (headers.get(groupPosition).equals("Take Test")) {
                    startTakeTest(childs.get(headers.get(groupPosition)).get(childPosition));
                } else if (headers.get(groupPosition).equals("Part Test")) {
                    startPartTest(childs.get(headers.get(groupPosition)).get(childPosition));
                }
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (childs != null && !childs.isEmpty()) {
            return this.childs.get(this.headers.get(groupPosition)).size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (headers != null && !headers.isEmpty()) {
            return this.headers.get(groupPosition);
        } else {
            return 0;
        }
    }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.offline_test_header_item, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.title_txt);
        ImageView ivNotStarted = (ImageView) convertView.findViewById(R.id.iv_yetttoattempt);
        ImageView ivCompleted = (ImageView) convertView.findViewById(R.id.iv_correct);
        ImageView ivRejected = (ImageView) convertView.findViewById(R.id.iv_notattended);
        TextView tvNotStarted = (TextView) convertView.findViewById(R.id.count_yettoattempt);
        TextView tvCompleted = (TextView) convertView.findViewById(R.id.count_correct);
        TextView tvRejected = (TextView) convertView.findViewById(R.id.count_notattended);
        Status status = getRejectedStatus(childs.get(headerTitle));
        if (status.statusCorrect == 0) {
            ivCompleted.setVisibility(View.GONE);
            tvCompleted.setVisibility(View.GONE);
        } else {
            ivCompleted.setVisibility(View.VISIBLE);
            tvCompleted.setVisibility(View.VISIBLE);
            tvCompleted.setText("" + status.statusCorrect);
        }
        if (status.statusNotattempted == 0) {
            ivNotStarted.setVisibility(View.GONE);
            tvNotStarted.setVisibility(View.GONE);
        } else {
            ivNotStarted.setVisibility(View.VISIBLE);
            tvNotStarted.setVisibility(View.VISIBLE);
            tvNotStarted.setText("" + status.statusNotattempted);
        }
        if (status.statusRejected == 0) {
            ivRejected.setVisibility(View.GONE);
            tvRejected.setVisibility(View.GONE);
        } else {
            ivRejected.setVisibility(View.VISIBLE);
            tvRejected.setVisibility(View.VISIBLE);
            tvRejected.setText("" + status.statusRejected);
        }
        lblListHeader.setText(headerTitle);
        ImageView ivDownload = (ImageView) convertView.findViewById(R.id.download_test);
        ivDownload.setImageResource(isExpanded ? R.drawable.ico_offline_arrow_down_black : R.drawable.ico_offline_arrow_black);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void startMockTest(OfflineTestObjectModel model) {
        if (model.status != Constants.STATUS_COMPLETED) {
            Intent intent = new Intent(context, TestInstructionsActivity.class);
            intent.putExtra("test_question_paper_id", model.testQuestionPaperId);
            intent.putExtra(Constants.TEST_TITLE, "Mock Test");
            intent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.getSelectedCourseId());
            intent.putExtra(Constants.IS_OFFLINE, true);
            intent.putExtra(Constants.DB_ROW_ID, model.getId());
            intent.putExtra("mock_test_data_json", Gson.get().toJson(model.mockTest));
            intent.putExtra("OfflineTestObjectModel", model.dateTime);
            if(model.status == Constants.STATUS_SUSPENDED) {
                intent.putExtra("test_status", "Suspended");
            }
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "You have already taken the test.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startScheduleTest(OfflineTestObjectModel model) {
        if (model.status != Constants.STATUS_COMPLETED) {
            try {

                ScheduledTestStartEvent event = new ScheduledTestStartEvent();
                event.testQuestionPaperId = model.testQuestionPaperId;
                EventBus.getDefault().post(event);
                return;
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
            Toast.makeText(context, "Please access the test during scheduled time", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "You have already taken the test.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTakeTest(OfflineTestObjectModel model) {
        if (model.status != Constants.STATUS_COMPLETED) {
            Chapter chapter = model.baseTest.chapter;
            String subjectId = model.baseTest.subjectId;
            Intent intent = new Intent(context, TestInstructionsActivity.class);
            intent.putExtra(TestStartActivity.KEY_TEST_TYPE, Tests.CHAPTER.getType());
            intent.putExtra(Constants.TEST_TITLE, "Take Test");
            intent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.getSelectedCourseId());
            intent.putExtra(Constants.SELECTED_CHAPTERID, chapter.idCourseSubjectChapter);
            intent.putExtra(Constants.SELECTED_CHAPTER_NAME, chapter.chapterName);
            intent.putExtra(Constants.LEVEL_CROSSED, chapter.passedComplexity);
            intent.putExtra(Constants.SELECTED_SUBJECTID, subjectId);
            intent.putExtra("test_question_paper_id", model.testQuestionPaperId);
            intent.putExtra("test_answer_paper_id", model.testAnswerPaperId);
            if(model.status == Constants.STATUS_SUSPENDED) {
                intent.putExtra("test_status", "Suspended");
            }
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "You have already taken the test.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPartTest(OfflineTestObjectModel model) {
        if (model.status != Constants.STATUS_COMPLETED) {
            String subjectName = model.baseTest.subjectName;
            String subjectId = model.baseTest.subjectId;
            Intent intent = new Intent(context, TestInstructionsActivity.class);
            intent.putExtra(TestStartActivity.KEY_TEST_TYPE, Tests.PART.getType());
            intent.putExtra(Constants.TEST_TITLE, "Part Test - " + subjectName);
            intent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.getSelectedCourseId());
            intent.putExtra(Constants.SELECTED_SUBJECTID, subjectId);
            intent.putExtra(Constants.SELECTED_TOPIC_NAME, subjectName);
            intent.putExtra("test_question_paper_id", model.testQuestionPaperId);
            intent.putExtra("test_answer_paper_id", model.testAnswerPaperId);
            if(model.status == Constants.STATUS_SUSPENDED) {
                intent.putExtra("test_status", "Suspended");
            }
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "You have already taken the test.", Toast.LENGTH_SHORT).show();
        }
    }

    private Status getRejectedStatus(List<OfflineTestObjectModel> offlineTestObjectModels) {
        Status status = new Status();
        for (OfflineTestObjectModel model : offlineTestObjectModels) {
            if (model.status == Constants.STATUS_COMPLETED) {
                ++status.statusCorrect;
            } else if (model.status == Constants.STATUS_START) {
                ++status.statusNotattempted;
            } else if (model.status == Constants.STATUS_SUSPENDED) {
                ++status.statusRejected;
            }
        }
        return status;
    }

    class Status {
        int statusNotattempted;
        int statusCorrect;
        int statusRejected;
    }
}

